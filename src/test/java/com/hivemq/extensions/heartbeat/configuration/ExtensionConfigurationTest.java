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

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtensionConfigurationTest {

    @TempDir
    private @NotNull Path tempDir;

    @Test
    void defaultConfiguration_ok() {
        final Heartbeat config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        final Heartbeat defaultConfig = new Heartbeat();
        assertEquals(config.getBindAddress(), defaultConfig.getBindAddress());
        assertEquals(config.getPath(), defaultConfig.getPath());
        assertEquals(config.getPort(), defaultConfig.getPort());
    }

    @Test
    void loadConfiguration_ok() throws IOException {
        final String extensionContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<heartbeat-extension-configuration>\n" +
                "        <port>4711</port>\n" +
                "        <bind-address>1.2.3.4</bind-address>\n" +
                "        <path>/examplePath</path>\n" +
                "</heartbeat-extension-configuration>\n";
        Files.writeString(tempDir.resolve("extension-config.xml"), extensionContent);

        final Heartbeat config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();

        assertEquals(config.getBindAddress(), "1.2.3.4");
        assertEquals(config.getPath(), "/examplePath");
        assertEquals(config.getPort(), 4711);
    }


    @Test
    void portConfiguration_Nok() throws IOException {
        final String portConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<heartbeat-extension-configuration>\n" +
                "        <port>-4711</port>\n" +
                "</heartbeat-extension-configuration>\n";
        Files.writeString(tempDir.resolve("extension-config.xml"), portConfig);

        final Heartbeat config = new ExtensionConfiguration(tempDir.toFile()).getHeartbeatConfig();
        assertEquals(config.getPort(), 9090);
    }

}
