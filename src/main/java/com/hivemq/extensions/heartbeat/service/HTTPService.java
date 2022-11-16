package com.hivemq.extensions.heartbeat.service;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extensions.heartbeat.configuration.entities.Heartbeat;
import com.hivemq.extensions.heartbeat.servlet.HiveMQHeartbeatServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Build and start an HTTP service and provides the HiveMQHeartbeatServlet.
 *
 * @author Anja Helmbrecht-Schaar
 */
public class HTTPService {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HTTPService.class);
    private static @Nullable Server server;
    private final @NotNull Heartbeat heartbeat;
    private final @NotNull HiveMQHeartbeatServlet hiveMQHeartbeatServlet;

    public HTTPService(
            final @NotNull Heartbeat heartbeat, final @NotNull HiveMQHeartbeatServlet hiveMQHeartbeatServlet) {
        this.heartbeat = heartbeat;
        this.hiveMQHeartbeatServlet = hiveMQHeartbeatServlet;
    }


    public void startHttpServer() {

        LOG.info("Initializing Heartbeat HTTP service");
        try {
            final @NotNull ServletContextHandler servletContextHandler =
                    new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.setContextPath("/");

            final InetSocketAddress address = new InetSocketAddress(heartbeat.getBindAddress(), heartbeat.getPort());

            server = new Server(address);
            server.setHandler(servletContextHandler);

            final @NotNull ServletHolder holder = new ServletHolder(hiveMQHeartbeatServlet);
            servletContextHandler.addServlet(holder, heartbeat.getPath());

            server.start();

            LOG.info(
                    "Heartbeat HTTP service started with status running '{}' on address '{}' and port '{}' for path '{}' ",
                    server.isRunning(),
                    heartbeat.getBindAddress(),
                    heartbeat.getPort(),
                    heartbeat.getPath());

        } catch (final Exception e) {
            LOG.error("Could not start Heartbeat HTTP server.", e);
            throw new RuntimeException("Could not start Heartbeat HTTP server.", e);
        }
    }

    public final void stopHTTPServer() {
        try {
            if (server != null && server.isRunning()) {
                server.stop();
                LOG.info("Stopped Heartbeat HTTP server.");
            } else {
                LOG.info("Heartbeat HTTP server already stopped.");
            }
        } catch (final Exception e) {
            LOG.error("Could not stop Heartbeat HTTP server.", e);
        }
    }

}
