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
package com.hivemq.extensions.heartbeat.service;

import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import com.hivemq.extensions.heartbeat.servlet.HiveMQHeartbeatServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Build and start an HTTP service and provides the HiveMQHeartbeatServlet.
 *
 * @author Anja Helmbrecht-Schaar
 */
public class HTTPService {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HTTPService.class);

    private static @Nullable Server server;

    private final @NotNull Heartbeat heartbeat;
    private final @NotNull HiveMQHeartbeatServlet hiveMQHeartbeatServlet;

    public HTTPService(
            final @NotNull Heartbeat heartbeat,
            final @NotNull HiveMQHeartbeatServlet hiveMQHeartbeatServlet) {
        this.heartbeat = heartbeat;
        this.hiveMQHeartbeatServlet = hiveMQHeartbeatServlet;
    }

    public void startHttpServer() {
        LOG.info("Initializing Heartbeat HTTP service");
        try {
            final var servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.setContextPath("/");

            final var holder = new ServletHolder(hiveMQHeartbeatServlet);
            servletContextHandler.addServlet(holder, heartbeat.getPath());

            final var address = new InetSocketAddress(heartbeat.getBindAddress(), heartbeat.getPort());
            server = new Server(address);
            server.setHandler(servletContextHandler);

            server.start();
            LOG.info(
                    "Heartbeat HTTP service started with status running '{}' on address '{}' and port '{}' for path '{}' ",
                    server.isRunning(),
                    heartbeat.getBindAddress(),
                    heartbeat.getPort(),
                    heartbeat.getPath());
        } catch (final Exception e) {
            LOG.error("Could not start Heartbeat HTTP server.", e);
            throw new RuntimeException("Could not start Heartbeat HTTP server.", e);
        }
    }

    public final void stopHTTPServer() {
        try {
            if (server != null && server.isRunning()) {
                server.stop();
                LOG.info("Stopped Heartbeat HTTP server.");
            } else {
                LOG.info("Heartbeat HTTP server already stopped.");
            }
        } catch (final Exception e) {
            LOG.error("Could not stop Heartbeat HTTP server.", e);
        }
    }
}
