/*
 * MIT License
 *
 * Copyright (c) 2017 Groovylabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.github.groovylabs.lyre.engine.APIx.inflectors;

import com.github.groovylabs.lyre.domain.Endpoint;
import com.github.groovylabs.lyre.domain.appliers.Countdown;
import com.github.groovylabs.lyre.domain.factories.LogFactory;
import com.github.groovylabs.lyre.engine.APIx.websocket.Dispatcher;
import com.github.groovylabs.lyre.utils.EndpointUtils;
import org.glassfish.jersey.process.Inflector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public class APIxInflector implements Inflector<ContainerRequestContext, Object> {

    @Autowired
    private LogFactory logFactory;

    @Autowired
    private EndpointUtils endpointUtils;

    @Autowired
    private Dispatcher dispatcher;

    @Context
    private HttpServletRequest request;

    private Endpoint endpoint;

    public APIxInflector(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public Object apply(ContainerRequestContext containerRequestContext) {

        dispatcher.publish(logFactory.logger(endpoint, request).info("Endpoint called.").event());

        Countdown countdown = endpoint.getProperty().getCountdown();

        if (!StringUtils.isEmpty(endpoint.getData())) {
            String requestObject = endpointUtils.getEntityBody(containerRequestContext);

            //TODO: Make a method that will be looking for the attributes of the object, not the of object as string.
            if (!endpoint.getData().equals(requestObject))
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();

        }

        if (countdown != null && countdown.getCalls() > 0) {
            countdown.decrease();
            return Response
                .status(countdown.getStatus().value())
                .entity(countdown.getStatus().getReasonPhrase()).build();
        } else {

            if (endpoint.getProperty().getTimer().idle() > 0) {

                try {
                    Thread.sleep(endpoint.getProperty().getTimer().idle());
                } catch (InterruptedException e) {

                }

            }

            return Response
                .status(endpoint.getResponse().getStatus().value())
                .entity(endpoint.getResponse().getData()).type(endpoint.getResponse().getProduces()).build();
        }
    }
}
