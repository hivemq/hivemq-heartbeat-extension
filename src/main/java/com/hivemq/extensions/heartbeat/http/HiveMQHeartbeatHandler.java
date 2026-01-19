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

import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.LifecycleStage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * HTTP handler that responds to heartbeat GET requests from load balancers.
 * <p>
 * This handler checks the HiveMQ lifecycle stage and returns:
 * <ul>
 *     <li>HTTP 200 (OK) if HiveMQ has started successfully</li>
 *     <li>HTTP 503 (SERVICE_UNAVAILABLE) if HiveMQ is not yet fully started</li>
 *     <li>HTTP 405 (METHOD_NOT_ALLOWED) for non-GET requests</li>
 * </ul>
 * <p>
 * Each valid heartbeat request is tracked via a metric counter ({@value HTTP_HEARTBEAT_METER}).
 *
 * @author David Sondermann
 * @since 1.0.11
 */
public class HiveMQHeartbeatHandler implements HttpHandler {

    private static final @NotNull String HTTP_HEARTBEAT_METER = "http-heartbeat-meter";

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HiveMQHeartbeatHandler.class);

    /**
     * Handles incoming HTTP requests for the heartbeat endpoint.
     * <p>
     * This method validates that the request uses the GET method, checks the HiveMQ lifecycle stage,
     * and responds with the appropriate HTTP status code. All requests are logged at debug level
     * and tracked via metrics.
     *
     * @param exchange the HTTP exchange containing request and response information
     * @throws IOException if an I/O error occurs during request handling
     */
    @Override
    public void handle(final @NotNull HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
                return;
            }

            // create and mark metric for heartbeat
            Services.metricRegistry().meter(HTTP_HEARTBEAT_METER).mark();

            final var status = (Services.adminService().getCurrentStage() == LifecycleStage.STARTED_SUCCESSFULLY) ?
                    HttpURLConnection.HTTP_OK :
                    HttpURLConnection.HTTP_UNAVAILABLE;
            exchange.sendResponseHeaders(status, -1);

            if (LOG.isDebugEnabled()) {
                final var remoteAddress = exchange.getRemoteAddress();
                final var localAddress = exchange.getLocalAddress();
                LOG.debug("Heartbeat request from IP {} (port {}) received on listener {}:{} and URI {}, with status {}",
                        remoteAddress.getAddress().getHostAddress(),
                        remoteAddress.getPort(),
                        localAddress.getAddress().getHostAddress(),
                        localAddress.getPort(),
                        exchange.getRequestURI(),
                        status);
            }
        } finally {
            exchange.close();
        }
    }
}
