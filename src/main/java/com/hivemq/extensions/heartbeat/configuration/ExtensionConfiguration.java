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

package com.hivemq.extensions.heartbeat.configuration;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ExtensionConfiguration {

    private @NotNull static final String EXTENSION_CONFIG_FILE_NAME = "extension-config.xml";
    private @NotNull static final Logger LOG = LoggerFactory.getLogger(ExtensionConfiguration.class);
    private @NotNull final ConfigurationXmlParser configurationXmlParser = new ConfigurationXmlParser();
    private @NotNull final Heartbeat heartbeat;


    public ExtensionConfiguration(final @NotNull File extensionHomeFolder) {
        heartbeat = read(new File(extensionHomeFolder, EXTENSION_CONFIG_FILE_NAME));
    }

    @NotNull
    public Heartbeat getHeartbeatConfig() {
        return heartbeat;
    }

    /**
     * @param configFile the new heartbeat file to read.
     * @return the new heartbeat based on the file contents or null if the heartbeat is invalid
     */
    @NotNull
    private Heartbeat read(@NotNull final File configFile) {

        @NotNull final Heartbeat defaultHeartbeat = new Heartbeat();
        if (configFile.exists()  &&  configFile.canRead() && configFile.length() > 0 ) {
            return doRead(configFile, defaultHeartbeat);
        } else {
            LOG.warn("Unable to read Heartbeat extension configuration file {}, using defaults", configFile.getAbsolutePath());
            return defaultHeartbeat;
        }
    }

    @NotNull
    private Heartbeat doRead(@NotNull File configFile, @NotNull Heartbeat defaultHeartbeat) {
        try {
            @NotNull final Heartbeat newHeartbeat = configurationXmlParser.unmarshalExtensionConfig(configFile);
            if (newHeartbeat.getPort() < 1) {
                LOG.warn("Port must be greater than 0, using default port " + defaultHeartbeat.getPort());
                newHeartbeat.setPort(defaultHeartbeat.getPort());
            }

            if (newHeartbeat.getBindAddress() == null) {
                LOG.warn("Bind Address for Heartbeat extension is null, using default bind Address {}.", defaultHeartbeat.getBindAddress());
                newHeartbeat.setBindAddress(defaultHeartbeat.getBindAddress());
            }

            if (newHeartbeat.getPath() == null) {
                LOG.warn("Path for Heartbeat extension is null, using default path {}.", defaultHeartbeat.getPath());
                newHeartbeat.setPath(defaultHeartbeat.getPath());
            }
            return newHeartbeat;

        } catch (IOException e) {
            LOG.warn("Could not read Heartbeat extension configuration file, reason: {}, using defaults {}.", e.getMessage(), defaultHeartbeat.toString());
            return defaultHeartbeat;
        }
    }

}
