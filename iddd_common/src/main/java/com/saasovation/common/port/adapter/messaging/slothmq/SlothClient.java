//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.saasovation.common.port.adapter.messaging.slothmq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlothClient extends SlothWorker {

	private static SlothClient instance;

	private Map<String,ExchangeListener> exchangeListeners;
	private Object lock;

	public static synchronized SlothClient instance() {
		if (instance == null) {
			instance = new SlothClient();
		}

		return instance;
	}

	public void close() {
		System.out.println("SLOTH CLIENT: Closing...");

		super.close();

		List<ExchangeListener> listeners =
				new ArrayList<ExchangeListener>(this.exchangeListeners.values());

		for (ExchangeListener listener : listeners) {
			this.unregister(listener);
		}

		System.out.println("SLOTH CLIENT: Closed.");
	}

	public void closeAll() {
		instance = null;

		this.close();

		this.sendToServer("CLOSE:");
	}

	public void publish(String anExchangeName, String aType, String aMessage) {
		String encodedMessage = "PUBLISH:" + anExchangeName + "TYPE:" + aType + "MSG:" + aMessage;

        this.sendToServer(encodedMessage);
	}

	public void register(ExchangeListener anExchangeListener) {
		synchronized (lock) {
			this.exchangeListeners.put(anExchangeListener.name(), anExchangeListener);
		}

		this.sendToServer("SUBSCRIBE:" + this.port() + ":" + anExchangeListener.exchangeName());
	}

	public void unregister(ExchangeListener anExchangeListener) {
		synchronized (lock) {
			this.exchangeListeners.remove(anExchangeListener.name());
		}

		this.sendToServer("UNSUBSCRIBE:" + this.port() + ":" + anExchangeListener.exchangeName());
	}

	private SlothClient() {
		super();

		this.exchangeListeners = new HashMap<String,ExchangeListener>();
		this.lock = new Object();

		this.attach();
		this.receiveAll();
	}

	private void attach() {
        this.sendToServer("ATTACH:" + this.port());
	}

	private void dispatchMessage(String anEncodedMessage) {
		int exchangeDivider = anEncodedMessage.indexOf("PUBLISH:");
		int typeDivider = anEncodedMessage.indexOf("TYPE:", exchangeDivider + 8);
		int msgDivider = anEncodedMessage.indexOf("MSG:", typeDivider + 5);

		String exchangeName = anEncodedMessage.substring(exchangeDivider + 8, typeDivider);
		String type = anEncodedMessage.substring(typeDivider + 5, msgDivider);
		String message = anEncodedMessage.substring(msgDivider + 4);

		List<ExchangeListener> listeners = null;

		synchronized (lock) {
			listeners = new ArrayList<ExchangeListener>(this.exchangeListeners.values());
		}

		for (ExchangeListener listener : listeners) {
			if (listener.exchangeName().equals(exchangeName) && listener.listensTo(type)) {
				try {
					System.out.println("SLOTH CLIENT: Dispatching: Exchange: " + exchangeName + " Type: " + type + " Msg: " + message);

					listener.filteredDispatch(type, message);
				} catch (Exception e) {
					System.out.println("SLOTH CLIENT: Exception while dispatching message: "
							+ e.getMessage() + ": " + anEncodedMessage);
					e.printStackTrace();
				}
			}
		}
	}

	private void receiveAll() {
		Thread receiverThread = new Thread() {
			@Override
			public void run() {
				while (!isClosed()) {
					String receivedData = null;

                    synchronized (lock) {
                        receivedData = receive();
                    }

					if (receivedData != null) {
						dispatchMessage(receivedData.trim());
					} else {
                        sleepFor(10L);
					}
				}
			}
		};

		receiverThread.start();
	}
}
