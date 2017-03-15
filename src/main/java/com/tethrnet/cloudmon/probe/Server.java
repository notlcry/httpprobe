package com.tethrnet.cloudmon.probe;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends AbstractVerticle {

    protected Log log = LogFactory.getLog(Server.class);

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Server server = new Server(Vertx.vertx());
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Server(Vertx v) {
        vertx = v;
    }

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> req.response().putHeader("content-type", "text/html").end
                ("<html><body><h1>Hello from Probe Server" +
                ".x!</h1></body></html>")).listen(Constant.HTTP_SERVER_PORT);
        log.info("Http Probe Serer started");
    }
}
