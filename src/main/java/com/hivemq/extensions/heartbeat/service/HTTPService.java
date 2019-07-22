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

package com.hivemq.extensions.heartbeat.service;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import com.hivemq.extensions.heartbeat.servlet.HiveMQHeartbeatServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Build and start an HTTP service and provides the HiveMQHeartbeatServlet.
 *
 * @author Anja Helmbrecht-Schaar
 */
public class HTTPService {

    private static @NotNull final Logger LOG = LoggerFactory.getLogger(HTTPService.class);
    private static @Nullable Server server;

    public HTTPService() { }

    public void start(@NotNull final Heartbeat heartbeat, @NotNull final HiveMQHeartbeatServlet hiveMQHeartbeatServlet) {

        try {
            LOG.info("Initializing HTTP service");
            @NotNull final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.setContextPath("/");

            final InetSocketAddress address = new InetSocketAddress(heartbeat.getBindAddress(), heartbeat.getPort());

            server = new Server(address);
            server.setHandler(servletContextHandler);

            @NotNull final ServletHolder holder = new ServletHolder(hiveMQHeartbeatServlet);
            servletContextHandler.addServlet(holder, heartbeat.getPath());

            server.start();

        } catch (final Exception e) {
            LOG.error("Could not start HTTP service. ", e);
            throw new RuntimeException(e);
        }

        LOG.info("HTTP service started successfully on address '{}' and port '{}' for path '{}' ", heartbeat.getBindAddress(), heartbeat.getPort(), heartbeat.getPath());

    }

    public void stop() {
        try {
            if( server != null && server.isRunning() ) {
                server.stop();
                LOG.info("Stopped HTTP server");
            } else {
                LOG.info("No HTTP server to stop");
            }
        } catch (final Exception e) {
            LOG.error("Could not stop HTTP server. ", e);
        }
    }

}
