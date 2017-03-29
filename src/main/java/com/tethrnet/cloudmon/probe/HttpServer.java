package com.tethrnet.cloudmon.probe;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class HttpServer extends AbstractVerticle {

    protected Log log = LogFactory.getLog(HttpServer.class);

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        HttpServer server = new HttpServer(Vertx.vertx());
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpServer(Vertx v) {
        vertx = v;
    }

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
            log.info("receive request from :" + req.remoteAddress().host() + ":"+ req.remoteAddress().port());
            req.response().putHeader("content-type", "text/html").end
                    ("<html><body><h1>Hello from Probe ProbeServer" +
                            ".x!</h1></body></html>");
        }).listen(Constant.HTTP_SERVER_PORT);
        log.info("Http Probe Serer started");
    }
}
