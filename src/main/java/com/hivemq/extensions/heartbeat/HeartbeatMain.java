
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

package com.hivemq.extensions.heartbeat;

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
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
 *
 * After HiveMQ is ready - Extension reads settings from configuration file and starts the HTTPS Server and the HeartbeatServlet
 * if HiveMQ stops - stops the HTTP Server
 *
 * @Author Anja Helmbrecht-Schaar
 *
 */
public class HeartbeatMain implements ExtensionMain {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HeartbeatMain.class);
    private static @NotNull HTTPService httpService;

    @Override
    public final void extensionStart(final @NotNull ExtensionStartInput extensionStartInput,
                                     final @NotNull ExtensionStartOutput extensionStartOutput) {
        try {
            final @NotNull File extensionHomeFolder = extensionStartInput.getExtensionInformation().getExtensionHomeFolder();
            final @NotNull ExtensionConfiguration extensionConfiguration = new ExtensionConfiguration(extensionHomeFolder);

            startRestService(extensionConfiguration);

            LOG.info("Start {}", extensionStartInput.getExtensionInformation().getName());
        } catch (Exception e) {
            extensionStartOutput.preventExtensionStartup("Heartbeat Extension cannot be started due to errors. ");
            LOG.error("Exception for {} thrown at start: ", extensionStartInput.getExtensionInformation().getName(), e);
        }
    }

    @Override
    public final void extensionStop(final @NotNull ExtensionStopInput extensionStopInput, final @NotNull ExtensionStopOutput extensionStopOutput) {
        LOG.info("Stop {} ", extensionStopInput.getExtensionInformation().getName());
        if( httpService != null ) {
            httpService.stopHTTPServer();
        }
    }

    @NotNull
    private void startRestService(final @NotNull ExtensionConfiguration extensionConfiguration) {
        httpService = new HTTPService(extensionConfiguration.getHeartbeatConfig(), new HiveMQHeartbeatServlet());
        httpService.tryStartHttpServer();
    }

}
