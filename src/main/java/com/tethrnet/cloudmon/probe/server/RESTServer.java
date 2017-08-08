/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package com.tethrnet.cloudmon.probe.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class RESTServer extends AbstractVerticle {

    @Override
    public void start() {

        vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/api/v1/probes/:probeId").handler(this::handleGetProbe);
        router.put("/api/v1/probes/:probeId").handler(this::handleAddProbe);
        router.get("/api/v1/probes").handler(this::handleListProbes);

        vertx.createHttpServer().requestHandler(router::accept).listen(8199);
    }

    private void handleGetProbe(RoutingContext routingContext) {
        String agentHost = routingContext.request().getParam("probeId");
        HttpServerResponse response = routingContext.response();
        if (agentHost == null) {
            sendError(400, response);
        } else {
            JsonObject product = ProbeAgentMgr.getInstance().getAgentStatus(agentHost);
            if (product == null) {
                sendError(404, response);
            } else {
                response.putHeader("content-type", "application/json").end(product.encodePrettily());
            }
        }
    }

    private void handleAddProbe(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("probeId");
        HttpServerResponse response = routingContext.response();
        if (productID == null) {
            sendError(400, response);
        } else {
            JsonObject product = routingContext.getBodyAsJson();
            if (product == null) {
                sendError(400, response);
            } else {
//                products.put(productID, product);
                response.end();
            }
        }
    }

    private void handleListProbes(RoutingContext routingContext) {
        JsonArray arr = new JsonArray();

        List<JsonObject> probes = ProbeAgentMgr.getInstance().listAgentStatus();
        probes.forEach(v -> arr.add(v));
        routingContext.response().putHeader("content-type", "application/json").end(new JsonObject().put("probeList",
                arr).encode());
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    public static void main(String[] args) {
        RESTServer restServer = new RESTServer();
        restServer.start();
    }
}
