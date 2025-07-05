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
package com.hivemq.extensions.heartbeat.configuration;

import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Anja Helmbrecht-Schaar
 */
public class ExtensionConfiguration {

    private static final @NotNull String EXTENSION_CONFIG_FILE_NAME = "extension-config.xml";
    private static final @NotNull Logger LOG = LoggerFactory.getLogger(ExtensionConfiguration.class);

    private final @NotNull ConfigurationXmlParser configurationXmlParser = new ConfigurationXmlParser();
    private final @NotNull File extensionHomeFolder;

    private @Nullable Heartbeat heartbeat;

    public ExtensionConfiguration(final @NotNull File extensionHomeFolder) {
        this.extensionHomeFolder = extensionHomeFolder;
    }

    public @NotNull Heartbeat getHeartbeatConfig() {
        if (heartbeat == null) {
            heartbeat = read(new File(extensionHomeFolder, EXTENSION_CONFIG_FILE_NAME));
        }
        return heartbeat;
    }

    /**
     * @param configFile the new heartbeat file to read.
     * @return the new heartbeat based on the file contents or default if the heartbeat configuration is invalid
     */
    private @NotNull Heartbeat read(final @NotNull File configFile) {
        final var defaultHeartbeat = new Heartbeat();
        if (configFile.exists() && configFile.canRead() && configFile.length() > 0) {
            try {
                final var newHeartbeat = configurationXmlParser.unmarshalExtensionConfig(configFile);
                return validate(newHeartbeat, defaultHeartbeat);
            } catch (final IOException e) {
                LOG.warn("Could not read Heartbeat extension configuration file, reason: {}, using defaults {}.",
                        e.getMessage(),
                        defaultHeartbeat);
                return defaultHeartbeat;
            }
        } else {
            LOG.warn("Unable to read Heartbeat extension configuration file {}, using defaults {}.",
                    configFile.getAbsolutePath(),
                    defaultHeartbeat);
            return defaultHeartbeat;
        }
    }

    private @NotNull Heartbeat validate(
            final @NotNull Heartbeat newHeartbeat,
            final @NotNull Heartbeat defaultHeartbeat) {
        if (newHeartbeat.getPort() < 1) {
            LOG.warn("Port must be greater than 0, using default port " + defaultHeartbeat.getPort());
            newHeartbeat.setPort(defaultHeartbeat.getPort());
        }
        return newHeartbeat;
    }
}
