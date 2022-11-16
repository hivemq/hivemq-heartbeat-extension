package com.hivemq.extensions.heartbeat.servlet;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.LifecycleStage;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The servlet that responds to the heartbeat GET request from the Load Balancer
 *
 * @author Anja Helmbrecht-Schaar
 */
public class HiveMQHeartbeatServlet extends HttpServlet {

    private static final long serialVersionUID = 0L;
    private static final @NotNull Logger LOG = LoggerFactory.getLogger(HiveMQHeartbeatServlet.class);
    private static final @NotNull String HTTP_HEARTBEAT_METER = "http-heartbeat-meter";

    @Override
    protected void doGet(final @NotNull HttpServletRequest req, final @NotNull HttpServletResponse resp) {

        final int status = (Services.adminService().getCurrentStage() == LifecycleStage.STARTED_SUCCESSFULLY) ?
                HttpServletResponse.SC_OK :
                HttpServletResponse.SC_SERVICE_UNAVAILABLE;

        resp.setStatus(status);

        //create and mark metric for heartbeat
        Services.metricRegistry().meter(HTTP_HEARTBEAT_METER).mark();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Heartbeat request from IP {} (port {}) received on listener {}:{} and URI {}, with status {} ",
                    req.getRemoteAddr(),
                    req.getRemotePort(),
                    req.getLocalAddr(),
                    req.getLocalPort(),
                    req.getRequestURI(),
                    status);
        }

    }
}
