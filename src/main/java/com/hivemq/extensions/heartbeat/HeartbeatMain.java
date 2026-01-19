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
package com.hivemq.extensions.heartbeat;

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extensions.heartbeat.configuration.ExtensionConfiguration;
import com.hivemq.extensions.heartbeat.http.HTTPService;
import com.hivemq.extensions.heartbeat.http.HiveMQHeartbeatHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the HiveMQ Heartbeat Extension.
 * <p>
 * This extension provides a lightweight HTTP endpoint for load balancer health checks.
 * During extension startup, it:
 * <ol>
 *     <li>Loads configuration from the extension home folder</li>
 *     <li>Starts an HTTP server on the configured port and bind address</li>
 *     <li>Registers a heartbeat handler at the configured path</li>
 * </ol>
 * <p>
 * During extension shutdown, the HTTP server is gracefully stopped.
 * <p>
 * The heartbeat endpoint returns HTTP 200 when HiveMQ is fully started and ready to accept connections,
 * or HTTP 503 when HiveMQ is still starting up or shutting down.
 *
 * @author David Sondermann
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class HeartbeatMain implements ExtensionMain {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HeartbeatMain.class);
    private static @Nullable HTTPService httpService;

    /**
     * Called when the extension is started by HiveMQ.
     * <p>
     * This method loads the extension configuration and starts the HTTP service.
     * If any error occurs during startup, the extension start is prevented and an error is logged.
     *
     * @param extensionStartInput  provides information about the extension and its environment
     * @param extensionStartOutput allows preventing extension startup in case of errors
     */
    @Override
    public final void extensionStart(
            final @NotNull ExtensionStartInput extensionStartInput,
            final @NotNull ExtensionStartOutput extensionStartOutput) {
        try {
            final var extensionHome = extensionStartInput.getExtensionInformation().getExtensionHomeFolder();
            final var extensionConfiguration = new ExtensionConfiguration(extensionHome);
            startRestService(extensionConfiguration);
        } catch (final Exception e) {
            extensionStartOutput.preventExtensionStartup("Heartbeat Extension cannot be started");
            LOG.error("{} extension could not be started due to an exception",
                    extensionStartInput.getExtensionInformation().getName(),
                    e);
        }
    }

    /**
     * Called when the extension is stopped by HiveMQ.
     * <p>
     * This method gracefully stops the HTTP server to ensure all resources are properly released.
     *
     * @param extensionStopInput  provides information about the extension stop event
     * @param extensionStopOutput allows customizing the extension stop behavior
     */
    @Override
    public final void extensionStop(
            final @NotNull ExtensionStopInput extensionStopInput,
            final @NotNull ExtensionStopOutput extensionStopOutput) {
        if (httpService != null) {
            httpService.stopHTTPServer();
        }
    }

    /**
     * Initializes and starts the HTTP service with the loaded configuration.
     *
     * @param extensionConfiguration the configuration containing heartbeat settings
     */
    private void startRestService(final @NotNull ExtensionConfiguration extensionConfiguration) {
        httpService = new HTTPService(extensionConfiguration.getHeartbeatConfig(), new HiveMQHeartbeatHandler());
        httpService.startHttpServer();
    }
}
