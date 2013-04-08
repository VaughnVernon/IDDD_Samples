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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlothClient extends SlothWorker {

	private static SlothClient instance;

	private boolean closed;
	private DatagramSocket clientSocket;
	private Map<String,ExchangeListener> exchangeListeners;
	private InetAddress serverIPAddress;
	private Object lock;

	public static synchronized SlothClient instance() {
		if (instance == null) {
			instance = new SlothClient();
		}

		return instance;
	}

	public void close() {
		System.out.println("SLOTH CLIENT: Closing...");

		this.closed = true;

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

		this.send("CLOSE:");

		this.clientSocket.close();
	}

	public void publish(String anExchangeName, String aType, String aMessage) {

		String encodedMessage = "PUBLISH:" + anExchangeName + "TYPE:" + aType + "MSG:" + aMessage;

		byte[] publishMessage = encodedMessage.getBytes();

		DatagramPacket sendPacket =
				new DatagramPacket(
						publishMessage,
						publishMessage.length,
						this.serverIPAddress,
						PORT);

		try {
			synchronized (lock) {
				this.clientSocket.send(sendPacket);
			}
		} catch (IOException e) {
			System.out.println("SLOTH CLIENT: Cannot publish because: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void register(ExchangeListener anExchangeListener) {
		synchronized (lock) {
			this.exchangeListeners.put(anExchangeListener.name(), anExchangeListener);
		}

		this.send("SUBSCRIBE:" + anExchangeListener.exchangeName());
	}

	public void unregister(ExchangeListener anExchangeListener) {
		synchronized (lock) {
			this.exchangeListeners.remove(anExchangeListener.name());
		}

		this.send("UNSUBSCRIBE:" + anExchangeListener.exchangeName());
	}

	private SlothClient() {
		super();

		this.exchangeListeners = new HashMap<String,ExchangeListener>();
		this.lock = new Object();

		this.attach();
		this.receiveAll();
	}

	private void attach() {
		try {
			this.clientSocket = new DatagramSocket();
			this.clientSocket.setSoTimeout(100);
			this.serverIPAddress = InetAddress.getByName("localhost");

			this.send("ATTACH:");

		} catch (Exception e) {
			System.out.println("SLOTH CLIENT: Cannot attach because: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void send(String anEncodedMessage) {
		byte[] message = anEncodedMessage.getBytes();

		DatagramPacket sendPacket =
				new DatagramPacket(
						message,
						message.length,
						this.serverIPAddress,
						PORT);

		try {
			this.clientSocket.send(sendPacket);
		} catch (Exception e) {
			System.out.println("SLOTH CLIENT: Exception while sending to server: "
					+ e.getMessage() + ": " + anEncodedMessage);
		}
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
				byte[] receiveBuffer = new byte[BUFFER_LENGTH];

				DatagramPacket receivePacket =
						new DatagramPacket(receiveBuffer, BUFFER_LENGTH);

				while (!closed) {

					boolean reallocate = false;
					boolean received = false;

					try {
						synchronized (lock) {
							clientSocket.receive(receivePacket);
						}

						received = true;

					} catch (SocketTimeoutException e) {
						// ignore
					} catch (IOException e) {
						reallocate = true;

						System.out.println("SLOTH CLIENT: problem receiving because: " + e.getMessage() + ": continuing...");
						e.printStackTrace();
					}

					if (received) {
						dispatchMessage(new String(receivePacket.getData()).trim());

						reallocate = true;
					}

					if (reallocate) {
						receiveBuffer = new byte[BUFFER_LENGTH];

						receivePacket = new DatagramPacket(receiveBuffer, BUFFER_LENGTH);
					} else {
						sleepFor(10L);
					}
				}
			}
		};

		receiverThread.start();
	}

	private void sleepFor(long aMillis) {
		try {
			Thread.sleep(aMillis);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
