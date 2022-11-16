/*
 * Copyright 2018-present HiveMQ GmbH
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
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extensions.heartbeat.configuration.ExtensionConfiguration;
import com.hivemq.extensions.heartbeat.service.HTTPService;
import com.hivemq.extensions.heartbeat.servlet.HiveMQHeartbeatServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Main class for HiveMQ Heartbeat extension
 * <p>
 * If HiveMQ is starting and starts this extension:
 * The settings were read from configuration file
 * the HTTPS Server will be started
 * and the HeartbeatServlet instantiated.
 * If HiveMQ stops - stops the HTTP Server
 *
 * @author Anja Helmbrecht-Schaar
 */
public class HeartbeatMain implements ExtensionMain {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HeartbeatMain.class);
    private static @Nullable HTTPService httpService;

    @Override
    public final void extensionStart(final @NotNull ExtensionStartInput extensionStartInput, final @NotNull ExtensionStartOutput extensionStartOutput) {
        try {
            final @NotNull File extensionHomeFolder = extensionStartInput.getExtensionInformation().getExtensionHomeFolder();
            final @NotNull ExtensionConfiguration extensionConfiguration = new ExtensionConfiguration(extensionHomeFolder);

            startRestService(extensionConfiguration);

        } catch (Exception e) {
            extensionStartOutput.preventExtensionStartup("Heartbeat Extension cannot be started.");
            LOG.error("{} extension could not be started. An exception was thrown while starting!", extensionStartInput.getExtensionInformation().getName(), e);
        }
    }

    @Override
    public final void extensionStop(final @NotNull ExtensionStopInput extensionStopInput, final @NotNull ExtensionStopOutput extensionStopOutput) {
        if (httpService != null) {
            httpService.stopHTTPServer();
        }
    }

    private void startRestService(final @NotNull ExtensionConfiguration extensionConfiguration) {
        httpService = new HTTPService(extensionConfiguration.getHeartbeatConfig(), new HiveMQHeartbeatServlet());
        httpService.startHttpServer();
    }

}
