/*
 * Copyright 2019-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.extensions.heartbeat.configuration.entities;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * JAXB entity representing the heartbeat extension configuration.
 * <p>
 * This class defines the configuration parameters for the heartbeat HTTP endpoint:
 * <ul>
 *     <li><b>port</b>: The port number where the HTTP server listens (default: {@value DEFAULT_PORT})</li>
 *     <li><b>bind-address</b>: The network address to bind to (default: {@value DEFAULT_BIND_ADDRESS})</li>
 *     <li><b>path</b>: The URL path for the heartbeat endpoint (default: {@value DEFAULT_SERVLET_PATH})</li>
 * </ul>
 * <p>
 * This class is used by JAXB to deserialize XML configuration files. The {@code @XmlElement}
 * annotations map XML elements to Java fields, with default values provided for optional elements.
 *
 * @author David Sondermann
 * @since 1.0.0
 */
@SuppressWarnings("FieldMayBeFinal")
@XmlRootElement(name = "heartbeat-extension-configuration")
@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.NONE)
public class Heartbeat {

    private static final int DEFAULT_PORT = 9090;

    private static final @NotNull String DEFAULT_BIND_ADDRESS = "0.0.0.0";
    private static final @NotNull String DEFAULT_SERVLET_PATH = "/heartbeat";

    @XmlElement(name = "port", defaultValue = "" + DEFAULT_PORT)
    private int port = DEFAULT_PORT;

    @XmlElement(name = "bind-address", defaultValue = DEFAULT_BIND_ADDRESS)
    private @NotNull String bindAddress = DEFAULT_BIND_ADDRESS;

    @XmlElement(name = "path", defaultValue = DEFAULT_SERVLET_PATH)
    private @NotNull String path = DEFAULT_SERVLET_PATH;

    /**
     * Default constructor for JAXB deserialization.
     * <p>
     * Initializes all fields with their default values.
     */
    public Heartbeat() {
    }

    /**
     * Returns the port number where the HTTP server listens.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port number where the HTTP server should listen.
     *
     * @param port the port number (must be greater than 0)
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Returns the network address to which the HTTP server is bound.
     *
     * @return the bind address
     */
    public @NotNull String getBindAddress() {
        return bindAddress;
    }

    /**
     * Returns the URL path for the heartbeat endpoint.
     *
     * @return the path (e.g., "/heartbeat")
     */
    public @NotNull String getPath() {
        return path;
    }

    @Override
    public @NotNull String toString() {
        return "Heartbeat{" + "port=" + port + ", bindAddress='" + bindAddress + '\'' + ", path='" + path + '\'' + '}';
    }
}
