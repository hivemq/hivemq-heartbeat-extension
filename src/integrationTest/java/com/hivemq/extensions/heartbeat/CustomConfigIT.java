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

import io.github.sgtsilvio.gradle.oci.junit.jupiter.OciImages;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author David Sondermann
 * @since 1.0.4
 */
@Testcontainers
class CustomConfigIT {

    @Container
    final @NotNull HiveMQContainer hivemq =
            new HiveMQContainer(OciImages.getImageName("hivemq/extensions/hivemq-heartbeat-extension")
                    .asCompatibleSubstituteFor("hivemq/hivemq-ce")) //
                    .withExposedPorts(9191)
                    .withCopyToContainer(MountableFile.forClasspathResource("config.xml"),
                            "/opt/hivemq/extensions/hivemq-heartbeat-extension/conf/config.xml")
                    .withLogConsumer(outputFrame -> System.out.print("HiveMQ: " + outputFrame.getUtf8String()))
                    .withEnv("HIVEMQ_DISABLE_STATISTICS", "true");

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void customConfigInConfFolder_customConfigUsed() throws Exception {
        try (final var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()) {
            //noinspection HttpUrlsUsage
            final var uri = "http://%s:%d/custom-endpoint".formatted(hivemq.getHost(), hivemq.getMappedPort(9191));
            final var request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();

            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
        }
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void configAtLegacyLocation_customConfigUsed() throws Exception {
        final var legacyHivemq =
                new HiveMQContainer(OciImages.getImageName("hivemq/extensions/hivemq-heartbeat-extension")
                        .asCompatibleSubstituteFor("hivemq/hivemq-ce")) //
                        .withExposedPorts(9191)
                        .withCopyToContainer(MountableFile.forClasspathResource("config.xml"),
                                "/opt/hivemq/extensions/hivemq-heartbeat-extension/extension-config.xml")
                        .withEnv("HIVEMQ_DISABLE_STATISTICS", "true");

        try (legacyHivemq) {
            legacyHivemq.start();
            try (final var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()) {
                //noinspection HttpUrlsUsage
                final var uri =
                        "http://%s:%d/custom-endpoint".formatted(legacyHivemq.getHost(), legacyHivemq.getMappedPort(9191));
                final var request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();

                final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertThat(response.statusCode()).isEqualTo(200);
                assertThat(response.body()).isNotNull();
            }
        }
    }
}
