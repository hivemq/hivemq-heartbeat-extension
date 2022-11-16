package com.hivemq.extensions.heartbeat.configuration;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.ThreadSafe;
import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

/**
 * @author Anja Helmbrecht-Schaar
 */
@ThreadSafe
public class ConfigurationXmlParser {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(ConfigurationXmlParser.class);

    //jaxb context is thread safe
    private final @NotNull JAXBContext jaxb;

    ConfigurationXmlParser() {
        try {
            jaxb = JAXBContext.newInstance(Heartbeat.class);
        } catch (JAXBException e) {
            LOG.error("Error in the Heartbeat Extension. Could not initialize XML parser", e);
            throw new RuntimeException("Could not initialize XML parser.", e);
        }
    }

    final @NotNull Heartbeat unmarshalExtensionConfig(final @NotNull File file) throws IOException {
        try {
            final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            return (Heartbeat) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            LOG.error("Error in the Heartbeat Extension. Could not unmarshal XML configuration", e);
            throw new IOException("Could not unmarshal XML configuration.", e);
        }
    }

}
