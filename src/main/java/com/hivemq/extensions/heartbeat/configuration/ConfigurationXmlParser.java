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

import com.hivemq.extension.sdk.api.annotations.ThreadSafe;
import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * XML parser for heartbeat extension configuration files.
 * <p>
 * This parser uses JAXB (Java Architecture for XML Binding) to deserialize XML configuration
 * files into {@link Heartbeat} objects. The parser is thread-safe as the underlying JAXB context
 * is thread-safe.
 * <p>
 * The parser handles XML unmarshalling errors gracefully by wrapping JAXB exceptions in
 * {@link IOException} for easier error handling by callers.
 *
 * @author David Sondermann
 * @since 1.0.0
 */
@ThreadSafe
public class ConfigurationXmlParser {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(ConfigurationXmlParser.class);

    // JAXB context is thread safe
    private final @NotNull JAXBContext jaxb;

    /**
     * Initializes the XML parser by creating a JAXB context for {@link Heartbeat} objects.
     *
     * @throws RuntimeException if the JAXB context cannot be initialized
     */
    ConfigurationXmlParser() {
        try {
            jaxb = JAXBContext.newInstance(Heartbeat.class);
        } catch (final JAXBException e) {
            LOG.error("Error in the Heartbeat Extension: Could not initialize XML parser", e);
            throw new RuntimeException("Could not initialize XML parser.", e);
        }
    }

    /**
     * Unmarshals an XML configuration file into a {@link Heartbeat} object.
     *
     * @param file the XML configuration file to parse
     * @return the parsed heartbeat configuration
     * @throws IOException if the file cannot be read or the XML is invalid
     */
    final @NotNull Heartbeat unmarshalExtensionConfig(final @NotNull File file) throws IOException {
        try {
            final var unmarshaller = jaxb.createUnmarshaller();
            return (Heartbeat) unmarshaller.unmarshal(file);
        } catch (final JAXBException e) {
            LOG.error("Error in the Heartbeat Extension: Could not unmarshal XML configuration", e);
            throw new IOException("Could not unmarshal XML configuration.", e);
        }
    }
}
