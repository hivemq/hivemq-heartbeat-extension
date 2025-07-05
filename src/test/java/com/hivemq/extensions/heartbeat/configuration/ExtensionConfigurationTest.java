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
        final var extensionContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<heartbeat-extension-configuration>\n" +
                "        <port>4711</port>\n" +
                "        <bind-address>1.2.3.4</bind-address>\n" +
                "        <path>/examplePath</path>\n" +
                "</heartbeat-extension-configuration>\n";
        Files.writeString(tempDir.resolve("extension-config.xml"), extensionContent);

        final var config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        assertThat(config.getBindAddress()).isEqualTo("1.2.3.4");
        assertThat(config.getPath()).isEqualTo("/examplePath");
        assertThat(config.getPort()).isEqualTo(4711);
    }

    @Test
    void portConfiguration_Nok() throws IOException {
        final var portConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<heartbeat-extension-configuration>\n" +
                "        <port>-4711</port>\n" +
                "</heartbeat-extension-configuration>\n";
        Files.writeString(tempDir.resolve("extension-config.xml"), portConfig);

        final var config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        assertThat(config.getPort()).isEqualTo(9090);
    }
}
