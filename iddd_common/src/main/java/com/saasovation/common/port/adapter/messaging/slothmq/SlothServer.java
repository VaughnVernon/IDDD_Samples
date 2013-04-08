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
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * I am a simple messaging server.
 *
 * @author Vaughn Vernon
 */
public class SlothServer extends SlothWorker {

	private Map<String,ClientRegistration> clientRegistrations;
	private boolean closed;
	private DatagramSocket serverSocket;

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

		this.clientRegistrations = new HashMap<String,ClientRegistration>();

		this.openSocket();
	}

	public void execute() {

		while (!this.closed) {
			DatagramPacket receivePacket = this.receive();

			String message = new String(receivePacket.getData()).trim();

			this.handleMessage(receivePacket, message);
		}
	}

	private ClientRegistration attach(DatagramPacket aReceivePacket) {
		InetAddress ipAddress = aReceivePacket.getAddress();
		int port = aReceivePacket.getPort();

		String name = ipAddress.toString() + ":" + port;

		ClientRegistration clientRegistration = this.clientRegistrations.get(name);

		if (clientRegistration == null) {
			clientRegistration = new ClientRegistration(ipAddress, port);
			this.clientRegistrations.put(name, clientRegistration);
		}

		return clientRegistration;
	}

	private void close() {
		System.out.println("SLOTH SERVER: Closing...");

		this.closed = true;

		this.serverSocket.close();

		System.out.println("SLOTH SERVER: Closed.");
	}

	private void handleMessage(DatagramPacket aReceivePacket, String aMessage) {
		System.out.println("SLOTH SERVER: Handling: " + aMessage);

		if (aMessage.startsWith("ATTACH:")) {
			this.attach(aReceivePacket);
		} else if (aMessage.startsWith("CLOSE:")) {
			this.close();
		} else if (aMessage.startsWith("PUBLISH:")) {
			this.publishToClients(aReceivePacket, aMessage);
		} else if (aMessage.startsWith("SUBSCRIBE:")) {
			this.subscribeClientTo(aReceivePacket, aMessage.substring(10));
		} else if (aMessage.startsWith("UNSUBSCRIBE:")) {
			this.unsubscribeClientFrom(aReceivePacket, aMessage.substring(12));
		} else {
			System.out.println("SLOTH SERVER: Does not understand: " + aMessage);
		}
	}

	private void openSocket() {
		try {
			this.serverSocket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			System.out.println("SLOTH SERVER: Won't start because: " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		}
	}

	private void publishToClients(
			DatagramPacket aReceivePacket,
			String anExchangeMessage) {

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
					this.send(clientSubscriptions.ipAddress(), clientSubscriptions.port(), anExchangeMessage);
				}
			}
		}
	}

	private DatagramPacket receive() {

		byte[] receiveBuffer = new byte[BUFFER_LENGTH];

		DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, BUFFER_LENGTH);

		try {
			this.serverSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.println("SLOTH SERVER: Failed to receive because: " + e.getMessage() + ": Continuing...");
			e.printStackTrace();
		}

		return receivePacket;
	}

	private void send(
			InetAddress anIPAddress,
			int aPort,
			String aMessage) {

		byte[] sendBuffer = aMessage.getBytes();

		DatagramPacket sendPacket =
				new DatagramPacket(
						sendBuffer,
						sendBuffer.length,
						anIPAddress,
						aPort);

		try {
			this.serverSocket.send(sendPacket);

			System.out.println("SLOTH SERVER: Sent: " + aMessage);

		} catch (IOException e) {
			System.out.println("SLOTH SERVER: Failed to send because: " + e.getMessage() + ": Continuing...");
			e.printStackTrace();
		}
	}

	private void subscribeClientTo(
			DatagramPacket aReceivePacket,
			String anExchangeName) {

		InetAddress ipAddress = aReceivePacket.getAddress();
		int port = aReceivePacket.getPort();

		String name = ipAddress.toString() + ":" + port;

		ClientRegistration clientRegistration = this.clientRegistrations.get(name);

		if (clientRegistration == null) {
			clientRegistration = this.attach(aReceivePacket);
		}

		clientRegistration.addSubscription(anExchangeName);

		System.out.println("SLOTH SERVER: Subscribed: " + clientRegistration + " TO: " + anExchangeName);
	}

	private void unsubscribeClientFrom(
			DatagramPacket aReceivePacket,
			String anExchangeName) {

		InetAddress ipAddress = aReceivePacket.getAddress();
		int port = aReceivePacket.getPort();

		String name = ipAddress.toString() + ":" + port;

		ClientRegistration clientRegistration = this.clientRegistrations.get(name);

		if (clientRegistration != null) {
			clientRegistration.removeSubscription(anExchangeName);

			System.out.println("SLOTH SERVER: Unsubscribed: " + clientRegistration + " FROM: " + anExchangeName);
		}
	}
}
