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
 * Manages the heartbeat extension configuration, including loading and validating settings.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Loading configuration from the extension-config.xml file in the extension home folder</li>
 *     <li>Validating configuration values and falling back to defaults when necessary</li>
 *     <li>Providing access to the heartbeat configuration throughout the extension lifecycle</li>
 * </ul>
 * <p>
 * If the configuration file is missing, unreadable, or contains invalid values, the extension
 * will use sensible defaults to ensure the heartbeat endpoint remains operational.
 *
 * @author David Sondermann
 * @since 1.0.0
 */
public class ExtensionConfiguration {

    private static final @NotNull String EXTENSION_NAME = "HiveMQ Heartbeat Extension";
    private static final @NotNull String EXTENSION_CONFIG_LOCATION = "conf/config.xml";
    private static final @NotNull String EXTENSION_CONFIG_LEGACY_LOCATION = "extension-config.xml";
    private static final @NotNull Logger LOG = LoggerFactory.getLogger(ExtensionConfiguration.class);

    private final @NotNull ConfigurationXmlParser configurationXmlParser = new ConfigurationXmlParser();
    private final @NotNull ConfigResolver configResolver;

    private @Nullable Heartbeat heartbeat;

    /**
     * Creates a new extension configuration manager.
     *
     * @param extensionHomeFolder the extension home folder where the configuration file is located
     */
    public ExtensionConfiguration(final @NotNull File extensionHomeFolder) {
        this.configResolver = new ConfigResolver(extensionHomeFolder.toPath(),
                EXTENSION_NAME,
                EXTENSION_CONFIG_LOCATION,
                EXTENSION_CONFIG_LEGACY_LOCATION);
    }

    /**
     * Returns the heartbeat configuration, loading it from the configuration file if needed.
     * <p>
     * This method uses lazy loading - the configuration is only read when first requested.
     * Subsequent calls return the cached configuration object.
     *
     * @return the heartbeat configuration with validated settings
     */
    public @NotNull Heartbeat getHeartbeatConfig() {
        if (heartbeat == null) {
            heartbeat = read(configResolver.get().toFile());
        }
        return heartbeat;
    }

    /**
     * Reads and validates the heartbeat configuration from the specified file.
     * <p>
     * If the file does not exist, cannot be read, or contains invalid XML, this method
     * returns a default configuration and logs a warning.
     *
     * @param configFile the configuration file to read
     * @return the validated heartbeat configuration or defaults if the configuration is invalid
     */
    private @NotNull Heartbeat read(final @NotNull File configFile) {
        final var defaultHeartbeat = new Heartbeat();
        if (configFile.exists() && configFile.canRead() && configFile.length() > 0) {
            try {
                final var newHeartbeat = configurationXmlParser.unmarshalExtensionConfig(configFile);
                return validate(newHeartbeat, defaultHeartbeat);
            } catch (final IOException e) {
                LOG.warn("Could not read Heartbeat extension configuration file, using defaults {}, reason: {}",
                        defaultHeartbeat,
                        e.getMessage());
                return defaultHeartbeat;
            }
        } else {
            LOG.warn("Unable to read Heartbeat extension configuration file {}, using defaults {}",
                    configFile.getAbsolutePath(),
                    defaultHeartbeat);
            return defaultHeartbeat;
        }
    }

    /**
     * Validates the heartbeat configuration and replaces invalid values with defaults.
     * <p>
     * Currently validates:
     * <ul>
     *     <li>Port number must be greater than 0</li>
     * </ul>
     *
     * @param newHeartbeat     the heartbeat configuration to validate
     * @param defaultHeartbeat the default configuration to use for invalid values
     * @return the validated heartbeat configuration with corrected values
     */
    private @NotNull Heartbeat validate(
            final @NotNull Heartbeat newHeartbeat,
            final @NotNull Heartbeat defaultHeartbeat) {
        if (newHeartbeat.getPort() < 1) {
            LOG.warn("Port must be greater than 0, using default port {}", defaultHeartbeat.getPort());
            newHeartbeat.setPort(defaultHeartbeat.getPort());
        }
        return newHeartbeat;
    }
}
