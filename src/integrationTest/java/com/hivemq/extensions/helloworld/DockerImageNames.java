package com.hivemq.extensions.helloworld;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.testcontainers.utility.DockerImageName;

public class DockerImageNames {

    public static final @NotNull DockerImageName HIVEMQ =
            DockerImageName.parse("acidsepp/hivemq-ce").withTag("latest").asCompatibleSubstituteFor("hivemq/hivemq-ce");
}
