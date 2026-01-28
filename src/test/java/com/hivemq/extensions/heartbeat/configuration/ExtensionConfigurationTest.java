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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionConfigurationTest {

    @TempDir
    private @NotNull Path tempDir;

    @Test
    void defaultConfiguration_ok() {
        final var config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        final var defaultConfig = new Heartbeat();
        assertThat(defaultConfig.getBindAddress()).isEqualTo(config.getBindAddress());
        assertThat(defaultConfig.getPath()).isEqualTo(config.getPath());
        assertThat(defaultConfig.getPort()).isEqualTo(config.getPort());
    }

    @Test
    void loadConfiguration_ok() throws IOException {
        final var extensionContent = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <heartbeat-extension-configuration>
                        <port>8080</port>
                        <bind-address>5.6.7.8</bind-address>
                        <path>/newPath</path>
                </heartbeat-extension-configuration>
                """;
        final var confDir = tempDir.resolve("conf");
        Files.createDirectories(confDir);
        Files.writeString(confDir.resolve("config.xml"), extensionContent);

        final var config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        assertThat(config.getBindAddress()).isEqualTo("5.6.7.8");
        assertThat(config.getPath()).isEqualTo("/newPath");
        assertThat(config.getPort()).isEqualTo(8080);
    }

    @Test
    void loadConfiguration_fromLegacyLocation_ok() throws IOException {
        final var extensionContent = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <heartbeat-extension-configuration>
                        <port>4711</port>
                        <bind-address>1.2.3.4</bind-address>
                        <path>/examplePath</path>
                </heartbeat-extension-configuration>
                """;
        Files.writeString(tempDir.resolve("extension-config.xml"), extensionContent);

        final var config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        assertThat(config.getBindAddress()).isEqualTo("1.2.3.4");
        assertThat(config.getPath()).isEqualTo("/examplePath");
        assertThat(config.getPort()).isEqualTo(4711);
    }

    @Test
    void loadConfiguration_legacyTakesPrecedence_ok() throws IOException {
        final var legacyContent = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <heartbeat-extension-configuration>
                        <port>1111</port>
                        <bind-address>1.1.1.1</bind-address>
                        <path>/legacy</path>
                </heartbeat-extension-configuration>
                """;
        final var newContent = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <heartbeat-extension-configuration>
                        <port>2222</port>
                        <bind-address>2.2.2.2</bind-address>
                        <path>/new</path>
                </heartbeat-extension-configuration>
                """;
        // write to both locations
        Files.writeString(tempDir.resolve("extension-config.xml"), legacyContent);
        final var confDir = tempDir.resolve("conf");
        Files.createDirectories(confDir);
        Files.writeString(confDir.resolve("config.xml"), newContent);

        // legacy location should take precedence
        final var config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        assertThat(config.getBindAddress()).isEqualTo("1.1.1.1");
        assertThat(config.getPath()).isEqualTo("/legacy");
        assertThat(config.getPort()).isEqualTo(1111);
    }

    @Test
    void portConfiguration_Nok() throws IOException {
        final var portConfig = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <heartbeat-extension-configuration>
                        <port>-4711</port>
                </heartbeat-extension-configuration>
                """;
        Files.writeString(tempDir.resolve("extension-config.xml"), portConfig);

        final var config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        assertThat(config.getPort()).isEqualTo(9090);
    }
}
