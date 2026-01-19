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
package com.hivemq.extensions.heartbeat.http;

import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Manages the HTTP server that provides the heartbeat endpoint for load balancer health checks.
 * <p>
 * This service creates and manages a lightweight HTTP server using Java's built-in {@link HttpServer}.
 * The server listens on a configured address and port, and delegates all requests to the provided
 * {@link HiveMQHeartbeatHandler}.
 * <p>
 * The service ensures proper lifecycle management, allowing graceful startup and shutdown of the HTTP server.
 *
 * @author David Sondermann
 * @since 1.0.11
 */
public class HTTPService {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HTTPService.class);

    private final @NotNull AtomicReference<HttpServer> serverRef = new AtomicReference<>();

    private final @NotNull Heartbeat heartbeat;
    private final @NotNull HiveMQHeartbeatHandler hiveMQHeartbeatHandler;

    /**
     * Creates a new HTTP service with the specified configuration and handler.
     *
     * @param heartbeat              the heartbeat configuration containing bind address, port, and path
     * @param hiveMQHeartbeatHandler the handler that processes heartbeat requests
     */
    public HTTPService(
            final @NotNull Heartbeat heartbeat,
            final @NotNull HiveMQHeartbeatHandler hiveMQHeartbeatHandler) {
        this.heartbeat = heartbeat;
        this.hiveMQHeartbeatHandler = hiveMQHeartbeatHandler;
    }

    /**
     * Starts the HTTP server on the configured address and port.
     * <p>
     * The server is configured to:
     * <ul>
     *     <li>Listen on the bind address and port specified in the heartbeat configuration</li>
     *     <li>Handle requests at the configured path using the heartbeat handler</li>
     *     <li>Use the default executor (JDK-managed thread pool)</li>
     * </ul>
     *
     * @throws RuntimeException if the server cannot be started due to an I/O error
     */
    public void startHttpServer() {
        LOG.info("Initializing Heartbeat HTTP service");
        try {
            final var address = new InetSocketAddress(heartbeat.getBindAddress(), heartbeat.getPort());
            final var server = HttpServer.create(address, 0);
            server.createContext(heartbeat.getPath(), hiveMQHeartbeatHandler);
            // use the default executor (JDK-managed thread pool) for handling HTTP requests
            server.setExecutor(null);
            server.start();
            serverRef.set(server);

            LOG.info("Heartbeat HTTP service started on address '{}' and port '{}' for path '{}'",
                    heartbeat.getBindAddress(),
                    heartbeat.getPort(),
                    heartbeat.getPath());
        } catch (final IOException e) {
            LOG.error("Could not start Heartbeat HTTP server", e);
            throw new RuntimeException("Could not start Heartbeat HTTP server", e);
        }
    }

    /**
     * Gracefully stops the HTTP server.
     * <p>
     * The server is stopped with a delay of 1 seconds, meaning it will stop immediately
     * but any in-progress requests will be allowed to complete.
     * If the server is already stopped or was never started, this method does nothing.
     */
    public final void stopHTTPServer() {
        final var server = serverRef.getAndSet(null);
        if (server != null) {
            server.stop(1);
            LOG.info("Stopped Heartbeat HTTP server");
        } else {
            LOG.info("Heartbeat HTTP server is not running");
        }
    }
}
