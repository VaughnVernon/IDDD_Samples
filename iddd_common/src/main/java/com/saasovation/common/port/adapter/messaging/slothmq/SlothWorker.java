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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract class SlothWorker {

	private static final int HUB_PORT = 55555;

	private int port;
	private ServerSocketChannel socket;

	protected SlothWorker() {
		super();

		this.open();
	}

	protected void close() {
	    try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println(this.getClass().getSimpleName() + ": problems closing socket.");
        }

	    this.socket = null;
	}

	protected boolean isClosed() {
	    return this.socket == null;
	}

	protected int port() {
	    return this.port;
	}

    protected String receive() {
        SocketChannel socketChannel = null;

        try {
            socketChannel = this.socket.accept();

            if (socketChannel == null) {
                return null; // if non-blocking
            }

            ReadableByteChannel readByteChannel =
                    Channels.newChannel(socketChannel.socket().getInputStream());

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

            ByteBuffer readBuffer = ByteBuffer.allocate(8);

            while (readByteChannel.read(readBuffer) != -1) {
                readBuffer.flip();

                while (readBuffer.hasRemaining()) {
                    byteArray.write(readBuffer.get());
                }

                readBuffer.clear();
            }

            return new String(byteArray.toByteArray());

        } catch (IOException e) {
            System.out.println("SLOTH SERVER: Failed to receive because: " + e.getMessage() + ": Continuing...");
            e.printStackTrace();

            return null;

        } finally {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

	protected void sendTo(int aPort, String anEncodedMessage) {
	    SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();

            socketChannel.connect(new InetSocketAddress(aPort));

            socketChannel.write(ByteBuffer.wrap(anEncodedMessage.getBytes()));

            System.out.println(this.getClass().getSimpleName() + ": Sent: " + anEncodedMessage);

        } catch (IOException e) {
            System.out.println(this.getClass().getSimpleName() + ": Failed to send because: " + e.getMessage() + ": Continuing...");
            e.printStackTrace();
        } finally {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    System.out.println(this.getClass().getSimpleName() + ": Failed to close client socket because: " + e.getMessage() + ": Continuing...");
                }
            }
        }
    }

    protected void sendToServer(String anEncodedMessage) {
        this.sendTo(HUB_PORT, anEncodedMessage);
    }

    protected void sleepFor(long aMillis) {
        try {
            Thread.sleep(aMillis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

	protected boolean slothHub() {
	    return false;
	}

    private int discoverClientPort() {
        boolean discovered = false;
        int discoveryPort = HUB_PORT + 1;
        final int errorPort = discoveryPort + 20;

        while (!discovered && discoveryPort < errorPort) {
            try {
                this.socket.bind(new InetSocketAddress(discoveryPort));

                discovered = true;

            } catch (Exception e) {
                ++discoveryPort;
            }
        }

        if (!discovered) {
            throw new IllegalStateException("No ports available.");
        }

        return discoveryPort;
    }

	private void open() {
	    if (this.slothHub()) {
	        this.openHub();
	    } else {
	        this.openClient();
	    }
	}

    private void openClient() {
        try {
            this.socket = ServerSocketChannel.open();
            this.port = this.discoverClientPort();
            this.socket.configureBlocking(false);
            System.out.println("SLOTH CLIENT: Opened on port: " + this.port);

        } catch (Exception e) {
            System.out.println("SLOTH CLIENT: Cannot connect because: " + e.getMessage());
        }
    }

    private void openHub() {
        try {
            this.socket = ServerSocketChannel.open();
            this.socket.bind(new InetSocketAddress(HUB_PORT));
            this.socket.configureBlocking(true);
            this.port = HUB_PORT;
            System.out.println("SLOTH SERVER: Opened on port: " + this.port);

        } catch (Exception e) {
            System.out.println("SLOTH SERVER: Cannot connect because: " + e.getMessage());
        }
    }
}
