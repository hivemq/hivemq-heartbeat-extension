package com.hivemq.extensions.helloworld;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.concurrent.TimeUnit;

import static com.hivemq.extensions.helloworld.DockerImageNames.HIVEMQ;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Yannick Weber
 * @since 1.0.4
 */
@Testcontainers
class FallbackToDefaultsIT {

    @Container
    final @NotNull HiveMQContainer hivemq = new HiveMQContainer(HIVEMQ) //
            .withExtension(MountableFile.forClasspathResource("hivemq-heartbeat-extension"))
            .waitForExtension("HiveMQ Heartbeat Extension")
            .withExposedPorts(9090)
            .withFileInExtensionHomeFolder(MountableFile.forClasspathResource("broken-config.xml"),
                    "hivemq-heartbeat-extension",
                    "/extension-config.xml")
            .withLogConsumer(outputFrame -> System.out.print("HiveMQ: " + outputFrame.getUtf8String()));

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void brokenConfigFilePresent_defaultsUsed() throws Exception {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder().url("http://" +
                hivemq.getHost() +
                ":" +
                hivemq.getMappedPort(9090) +
                "/heartbeat").build();

        try (final Response response = client.newCall(request).execute()) {
            final ResponseBody body = response.body();
            assertEquals(200, response.code());
            assertNotNull(body);
        }
    }
}
