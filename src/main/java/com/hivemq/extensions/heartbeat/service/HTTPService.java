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
import com.hivemq.extension.sdk.api.services.CompletableScheduledFuture;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.LifecycleStage;
import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import com.hivemq.extensions.heartbeat.servlet.HiveMQHeartbeatServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Build and start an HTTP service and provides the HiveMQHeartbeatServlet.
 *
 * @author Anja Helmbrecht-Schaar
 */
public class HTTPService {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HTTPService.class);
    private static @Nullable Server server;
    private static @NotNull Heartbeat heartbeat;
    private static @NotNull HiveMQHeartbeatServlet hiveMQHeartbeatServlet;
    private static @NotNull CompletableScheduledFuture<?> completableScheduledFuture;

    public HTTPService(final @NotNull Heartbeat heartbeat, final @NotNull HiveMQHeartbeatServlet hiveMQHeartbeatServlet) {
        this.heartbeat = heartbeat;
        this.hiveMQHeartbeatServlet= hiveMQHeartbeatServlet;
    }


    @NotNull
    public void startHttpServer() {

        LOG.info("Initializing Heartbeat HTTP service");
        try {
            @NotNull final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.setContextPath("/");

            final InetSocketAddress address = new InetSocketAddress(heartbeat.getBindAddress(), heartbeat.getPort());

            server = new Server(address);
            server.setHandler(servletContextHandler);

            @NotNull final ServletHolder holder = new ServletHolder(hiveMQHeartbeatServlet);
            servletContextHandler.addServlet(holder, heartbeat.getPath());

            server.start();

            LOG.info("Heartbeat HTTP service started with status running '{}' on address '{}' and port '{}' for path '{}' ",
                    server.isRunning(), heartbeat.getBindAddress(), heartbeat.getPort(), heartbeat.getPath());

        } catch (final Exception e) {
            LOG.error("Could not start Heartbeat HTTP service. ", e);
            throw new RuntimeException("Could not start HTTP service. ", e);
        }
    }

    @NotNull
    public final void stopHTTPServer() {
        try {
            if (server != null && server.isRunning()) {
                server.stop();
                LOG.info("Stopped HeartbeatHTTP server");
            } else {
                LOG.info("No Heartbeat HTTP server to stop");
            }
        } catch (final Exception e) {
            LOG.error("Could not stop Heartbeat HTTP server. ", e);
        }
    }

}
