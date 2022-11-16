package com.hivemq.extensions.heartbeat.configuration.entities;

import com.hivemq.extension.sdk.api.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Anja Helmbrecht-Schaar
 */
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

    public Heartbeat() {
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public @NotNull String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(final @NotNull String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public @NotNull String getPath() {
        return path;
    }

    public void setPath(final @NotNull String path) {
        this.path = path;
    }

    @Override
    public @NotNull String toString() {
        return "Heartbeat{" + "port=" + port + ", bindAddress='" + bindAddress + '\'' + ", path='" + path + '\'' + '}';
    }
}
