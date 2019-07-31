/*
 * Copyright 2019 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hivemq.extensions.heartbeat.servlet;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.LifecycleStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {

        final int status = (Services.adminService().getCurrentStage() == LifecycleStage.STARTED_SUCCESSFULLY )
                ? HttpServletResponse.SC_OK
                : HttpServletResponse.SC_SERVICE_UNAVAILABLE;

        resp.setStatus(status);

        //create and mark metric for heartbeat
        Services.metricRegistry().meter(HTTP_HEARTBEAT_METER).mark();

        LOG.debug("Heartbeat Request from IP {} (port {}) received on listener {}:{} and URI {}, with status {} ",
                req.getRemoteAddr(), req.getRemotePort(), req.getLocalAddr(), req.getLocalPort(), req.getRequestURI(), status);

    }

}
