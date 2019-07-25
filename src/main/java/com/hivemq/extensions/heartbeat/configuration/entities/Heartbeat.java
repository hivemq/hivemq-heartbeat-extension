/*
 * Copyright 2019 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hivemq.extensions.heartbeat.configuration.entities;

import com.hivemq.extension.sdk.api.annotations.NotNull;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "heartbeat-extension-configuration")
@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.NONE)
public class Heartbeat {

    private static final int DEFAULT_PORT = 9090;

    private static final String DEFAULT_BIND_ADDRESS = "0.0.0.0";
    private static final String DEFAULT_SERVLET_PATH = "/heartbeat";

    @XmlElement(name = "port", required = true, defaultValue = ""+DEFAULT_PORT)
    private int port = DEFAULT_PORT;

    @NotNull
    @XmlElement(name = "bind-address", required = true, defaultValue = DEFAULT_BIND_ADDRESS)
    private String bindAddress = DEFAULT_BIND_ADDRESS;

    @NotNull
    @XmlElement(name = "path", required = true, defaultValue = DEFAULT_SERVLET_PATH)
    private String path = DEFAULT_SERVLET_PATH;

    public Heartbeat() { }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Heartbeat{" +
                "port=" + port +
                ", bindAddress='" + bindAddress + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
