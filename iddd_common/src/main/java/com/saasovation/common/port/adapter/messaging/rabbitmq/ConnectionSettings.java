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

package com.saasovation.common.port.adapter.messaging.rabbitmq;

import com.saasovation.common.AssertionConcern;

/**
 * I am a configuration for making a connection to
 * RabbitMQ. I include information for the host, port,
 * virtual host, and user.
 *
 * @author Vaughn Vernon
 */
public class ConnectionSettings extends AssertionConcern {

    /** My hostName, which is the name of the host server. */
    private String hostName;

    /** My password, which is the password of the connecting user. */
    private String password;

    /** My port, which is the host server port. */
    private int port;

    /** My username, which is the name of the connecting user. */
    private String username;

    /** My virtualHost, which is the name of the RabbitMQ virtual host. */
    private String virtualHost;

    /**
     * Answers a new ConnectionSettings with defaults.
     * @return ConnectionSettings
     */
    public static ConnectionSettings instance() {
        return new ConnectionSettings("localhost", -1, "/", null, null);
    }

    /**
     * Answers a new ConnectionSettings with a specific
     * host name and virtual host and remaining defaults.
     * @param aHostName the String name of the host server
     * @param aVirtualHost the String name of the virtual host
     * @return ConnectionSettings
     */
    public static ConnectionSettings instance(
            String aHostName,
            String aVirtualHost) {
        return new ConnectionSettings(aHostName, -1, aVirtualHost, null, null);
    }

    /**
     * Constructs my default state.
     * @param aHostName the String name of the host server
     * @param aPort the int port number on the host server, or -1
     * @param aVirtualHost the String name of the virtual host
     * @param aUsername the String name of the user, or null
     * @param aPassword the String password of the user, or null
     */
    public static ConnectionSettings instance(
            String aHostName,
            int aPort,
            String aVirtualHost,
            String aUsername,
            String aPassword) {
        return new ConnectionSettings(
                aHostName, aPort, aVirtualHost, aUsername, aPassword);
    }

    /**
     * Constructs my default state.
     * @param aHostName the String name of the host server
     * @param aPort the int port number on the host server, or -1
     * @param aVirtualHost the String name of the virtual host
     * @param aUsername the String name of the user, or null
     * @param aPassword the String password of the user, or null
     */
    protected ConnectionSettings(
            String aHostName,
            int aPort,
            String aVirtualHost,
            String aUsername,
            String aPassword) {

        super();

        this.setHostName(aHostName);
        this.setPassword(aPassword);
        this.setPort(aPort);
        this.setUsername(aUsername);
        this.setVirtualHost(aVirtualHost);
    }

    /**
     * Answers my hostName.
     * @return String
     */
    protected String hostName() {
        return this.hostName;
    }

    /**
     * Sets my hostName.
     * @param aHostName the String to set as my hostName
     */
    private void setHostName(String aHostName) {
        this.assertArgumentNotEmpty(aHostName, "Host name must be provided.");

        this.hostName = aHostName;
    }

    /**
     * Answers my password.
     * @return String
     */
    protected String password() {
        return this.password;
    }

    /**
     * Sets my password.
     * @param aPassword the String to set as my password
     */
    private void setPassword(String aPassword) {
        this.password = aPassword;
    }

    /**
     * Answers my port.
     * @return int
     */
    protected int port() {
        return this.port;
    }

    /**
     * Answers whether or not a port is included.
     * @return boolean
     */
    protected boolean hasPort() {
        return this.port() > 0;
    }

    /**
     * Sets my port.
     * @param aPort the int to set as my port
     */
    private void setPort(int aPort) {
        this.port = aPort;
    }

    /**
     * Answers whether or not the user credentials are included.
     * @return boolean
     */
    protected boolean hasUserCredentials() {
        return this.username() != null && this.password() != null;
    }

    /**
     * Answers my username.
     * @return String
     */
    protected String username() {
        return this.username;
    }

    /**
     * Sets my username.
     * @param aUsername the String to set as my username
     */
    private void setUsername(String aUsername) {
        this.username = aUsername;
    }

    /**
     * Answers my virtualHost.
     * @return String
     */
    protected String virtualHost() {
        return this.virtualHost;
    }

    /**
     * Sets my virtualHost.
     * @param aVirtualHost the String to set as my virtualHost
     */
    private void setVirtualHost(String aVirtualHost) {
        this.assertArgumentNotEmpty(aVirtualHost, "Virtual host must be provided.");

        this.virtualHost = aVirtualHost;
    }
}
