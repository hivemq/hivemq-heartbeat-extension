package com.hivemq.extensions.heartbeat.configuration;

import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.security.sasl.SaslException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    private static String extensionContent =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<heartbeat-extension-configuration>\n" +
                    "        <port>4711</port>\n" +
                    "        <bind-address>1.2.3.4</bind-address>\n" +
                    "        <path>/examplePath</path>\n" +
                    "</heartbeat-extension-configuration>\n";
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File root;
    private File file;

    @Before
    public void setUp() throws Exception {
        root = folder.getRoot();
        String fileName = "extension-config.xml";
        file = folder.newFile(fileName);
    }

    @Test
    public void defaultConfiguration_ok() {

        final Heartbeat config = new ExtensionConfiguration(root).getHeartbeatConfig();
        final Heartbeat defaultConfig = new Heartbeat();

        assertEquals(config.getBindAddress(), defaultConfig.getBindAddress());
        assertEquals(config.getPath(), defaultConfig.getPath());
        assertEquals(config.getPort(), defaultConfig.getPort());

    }

    @Test
    public void loadConfiguration_ok() throws IOException {

        Files.writeString(file.toPath(), extensionContent);

        final Heartbeat config = new ExtensionConfiguration(root).getHeartbeatConfig();

        assertEquals(config.getBindAddress(), "1.2.3.4");
        assertEquals(config.getPath(), "/examplePath");
        assertEquals(config.getPort(), 4711);
    }


    @Test
    public void portConfiguration_Nok() throws IOException {
        String portConfig =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<heartbeat-extension-configuration>\n" +
                        "        <port>-4711</port>\n" +
                        "</heartbeat-extension-configuration>\n";

        Files.writeString(file.toPath(), portConfig);
        final Heartbeat config = new ExtensionConfiguration(root).getHeartbeatConfig();
        assertEquals(config.getPort(), 9090);
    }

}