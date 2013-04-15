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

import java.util.HashMap;
import java.util.Map;

/**
 * I am a simple messaging server.
 *
 * @author Vaughn Vernon
 */
public class SlothServer extends SlothWorker {

	private Map<Integer,ClientRegistration> clientRegistrations;

	public static void executeInProcessDetachedServer() {
		Thread serverThread = new Thread() {
			@Override
			public void run() {
				SlothServer.executeNewServer();
			}
		};

		serverThread.start();
	}

	public static void executeNewServer() {
		SlothServer slothServer = new SlothServer();

		slothServer.execute();
	}

	public static void main(String anArguments[]) throws Exception {
		SlothServer.executeNewServer();
	}

	public SlothServer() {
		super();

		this.clientRegistrations = new HashMap<Integer,ClientRegistration>();
	}

	public void execute() {

		while (!this.isClosed()) {
		    String receivedData = this.receive();

		    if (receivedData != null) {
		        this.handleMessage(receivedData);
		    }
		}
	}

	@Override
	protected boolean slothHub() {
	    return true;
	}

	private ClientRegistration attach(String aReceivedData) {
		int port = Integer.parseInt(aReceivedData.substring(7));

		return this.attach(port);
	}

    private ClientRegistration attach(int aPort) {
        ClientRegistration clientRegistration = this.clientRegistrations.get(aPort);

        if (clientRegistration == null) {
            clientRegistration = new ClientRegistration(aPort);
            this.clientRegistrations.put(aPort, clientRegistration);
        }

        return clientRegistration;
    }

	private void handleMessage(String aReceivedData) {
		System.out.println("SLOTH SERVER: Handling: " + aReceivedData);

		if (aReceivedData.startsWith("ATTACH:")) {
			this.attach(aReceivedData);
		} else if (aReceivedData.startsWith("CLOSE:")) {
			this.close();
		} else if (aReceivedData.startsWith("PUBLISH:")) {
			this.publishToClients(aReceivedData);
		} else if (aReceivedData.startsWith("SUBSCRIBE:")) {
			this.subscribeClientTo(aReceivedData.substring(10));
		} else if (aReceivedData.startsWith("UNSUBSCRIBE:")) {
			this.unsubscribeClientFrom(aReceivedData.substring(12));
		} else {
			System.out.println("SLOTH SERVER: Does not understand: " + aReceivedData);
		}
	}

	private void publishToClients(String anExchangeMessage) {

		int exchangeDivider = anExchangeMessage.indexOf("PUBLISH:");
		int typeDivider = anExchangeMessage.indexOf("TYPE:", exchangeDivider + 8);

		if (exchangeDivider == -1) {
			System.out.println("SLOTH SERVER: PUBLISH: No exchange name; ignoring: " + anExchangeMessage);
		} else if (typeDivider == -1) {
			System.out.println("SLOTH SERVER: PUBLISH: No TYPE; ignoring: " + anExchangeMessage);
		} else {
			String exchangeName = anExchangeMessage.substring(exchangeDivider + 8, typeDivider);

			for (ClientRegistration clientSubscriptions : this.clientRegistrations.values()) {
				if (clientSubscriptions.isSubscribedTo(exchangeName)) {
					this.sendTo(clientSubscriptions.port(), anExchangeMessage);
				}
			}
		}
	}

	private void subscribeClientTo(String aPortWithExchangeName) {
	    String[] parts = aPortWithExchangeName.split(":");
		int port = Integer.parseInt(parts[0]);
		String exchangeName = parts[1];

		ClientRegistration clientRegistration = this.clientRegistrations.get(port);

		if (clientRegistration == null) {
			clientRegistration = this.attach(port);
		}

		clientRegistration.addSubscription(exchangeName);

		System.out.println("SLOTH SERVER: Subscribed: " + clientRegistration + " TO: " + exchangeName);
	}

	private void unsubscribeClientFrom(String aPortWithExchangeName) {
        String[] parts = aPortWithExchangeName.split(":");
        int port = Integer.parseInt(parts[0]);
        String exchangeName = parts[1];

		ClientRegistration clientRegistration = this.clientRegistrations.get(port);

		if (clientRegistration != null) {
			clientRegistration.removeSubscription(exchangeName);

			System.out.println("SLOTH SERVER: Unsubscribed: " + clientRegistration + " FROM: " + exchangeName);
		}
	}
}
