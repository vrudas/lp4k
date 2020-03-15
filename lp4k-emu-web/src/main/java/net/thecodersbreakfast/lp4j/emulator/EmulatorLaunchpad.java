/*
 * Copyright 2020 Vasyl Rudas
 * Copyright 2015 Olivier Croisier (thecodersbreakfast.net)
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

package net.thecodersbreakfast.lp4j.emulator;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import net.thecodersbreakfast.lp4j.api.*;

import java.io.IOException;

/**
 * A web-based (HTML/SVG/Sebsockets) Launchpad emulator.
 */
public class EmulatorLaunchpad implements Launchpad {

    /**
     * Web path for accessing static files.
     */
    public static final String WEB_RESOURCES_PREFIX = "/web/*";

    /**
     * Resources directory to serve static files from.
     */
    public static final String WEB_RESOURCES_DIR = "web";

    /**
     * URL of the Vertx eventbus bridge
     */
    public static final String EVENTBUS_ADDRESS = "/eventbus/*";

    /**
     * Eventbus ID of the emulator, on the server side
     */
    public static final String EVENTBUS_SERVER_HANDLER_ID = "lp4j:server";

    /**
     * Handler for Vertx eventbus messages.
     */
    private final EventBusHandler eventBusHandler = new EventBusHandler();

    /**
     * Vertx engine instance.
     */
    private final Vertx vertx;

    /**
     * Constructor.
     *
     * @param httpPort The HTTP port on which the emulator should run.
     */
    public EmulatorLaunchpad(int httpPort) {
        vertx = Vertx.vertx();

        Router router = Router.router(vertx);
        HttpServer httpServer = vertx.createHttpServer();

        router.route(WEB_RESOURCES_PREFIX).handler(StaticHandler.create(WEB_RESOURCES_DIR));


        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);

        BridgeOptions options = new BridgeOptions()
            .addOutboundPermitted(new PermittedOptions())
            .addInboundPermitted(new PermittedOptions());

        sockJSHandler.bridge(options, event -> {
            // You can also optionally provide a handler like this which will be passed any events that occur on the bridge
            // You can use this for monitoring or logging, or to change the raw messages in-flight.
            // It can also be used for fine grained acgcess control.

            if (event.type() == BridgeEventType.SOCKET_CREATED) {
                System.out.println("A socket was created");
            }

            if (event.type() == BridgeEventType.PUBLISH || event.type() == BridgeEventType.SEND) {
                System.out.println(event.getRawMessage());
            }

            event.complete(true);
        });

        // mount the bridge on the router
        router.route(EVENTBUS_ADDRESS).handler(sockJSHandler);

        vertx.eventBus().consumer(EVENTBUS_SERVER_HANDLER_ID, eventBusHandler::handle);

        System.out.println("Launchpad emulator is ready on http://localhost:" + httpPort + "/");
        httpServer.requestHandler(router).listen(httpPort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LaunchpadClient getClient() {
        return new EmulatorLaunchpadClient(vertx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setListener(LaunchpadListener listener) {
        this.eventBusHandler.setListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        vertx.close();
    }

    /**
     * Handler for Vertx eventbus messages.
     */
    private static class EventBusHandler implements Handler<Message> {

        private enum InputEventType {

            /**
             * Pad pressed
             */
            PP,

            /**
             * Pad released
             */
            PR,

            /**
             * Button pressed
             */
            BP,

            /**
             * Button released
             */
            BR,

            /**
             * Text scrolled
             */
            TS
        }

        private LaunchpadListener listener;

        @Override
        public void handle(Message message) {
            if (listener == null) {
                return;
            }

            long timestamp = System.currentTimeMillis();
            JsonObject body = (JsonObject) message.body();
            InputEventType inputEventType = InputEventType.valueOf(body.getString("evt"));
            switch (inputEventType) {
                case PP: {
                    Integer x = body.getInteger("x");
                    Integer y = body.getInteger("y");
                    listener.onPadPressed(Pad.at(x, y), timestamp);
                    break;
                }
                case PR: {
                    Integer x = body.getInteger("x");
                    Integer y = body.getInteger("y");
                    listener.onPadReleased(Pad.at(x, y), timestamp);
                    break;
                }
                case BP: {
                    int x = body.getInteger("x");
                    int y = body.getInteger("y");
                    int c = x == -1 ? y : x;
                    Button button = (x != -1) ? Button.atTop(c) : Button.atRight(c);
                    listener.onButtonPressed(button, timestamp);
                    break;
                }
                case BR: {
                    int x = body.getInteger("x");
                    int y = body.getInteger("y");
                    int c = x == -1 ? y : x;
                    Button button = (x != -1) ? Button.atTop(c) : Button.atRight(c);
                    listener.onButtonReleased(button, timestamp);
                    break;
                }
                case TS: {
                    listener.onTextScrolled(timestamp);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown input event type " + inputEventType.name());
                }
            }

        }

        public void setListener(LaunchpadListener listener) {
            this.listener = listener;
        }
    }
}
