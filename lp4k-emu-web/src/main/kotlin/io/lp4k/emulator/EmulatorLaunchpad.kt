/*
 *    Copyright 2020 Vasyl Rudas
 *    Copyright 2015 Olivier Croisier (thecodersbreakfast.net)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package io.lp4k.emulator

import io.lp4k.api.Launchpad
import io.lp4k.api.LaunchpadClient
import io.lp4k.api.LaunchpadListener
import io.lp4k.emulator.input.EventBusHandler
import io.lp4k.emulator.input.EventBusMessageDebugger
import io.vertx.core.Vertx
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import org.slf4j.LoggerFactory

/**
 * A web-based (HTML/SVG/WebSockets) Launchpad emulator.
 */
class EmulatorLaunchpad : Launchpad {

    override val client: LaunchpadClient by lazy { EmulatorLaunchpadClient(vertx) }

    private val vertx: Vertx
    private val eventBusHandler: EventBusHandler

    /**
     * @param httpPort The HTTP port on which the emulator should run.
     */
    constructor (httpPort: Int) : this(httpPort, Vertx.vertx())

    internal constructor(
        httpPort: Int,
        vertx: Vertx
    ) : this(httpPort, vertx, EventBusHandler())

    internal constructor(
        httpPort: Int,
        vertx: Vertx,
        eventBusHandler: EventBusHandler
    ) : this(vertx, eventBusHandler) {
        configureVertx(httpPort)
    }

    internal constructor(
        vertx: Vertx,
        eventBusHandler: EventBusHandler
    ) {
        this.vertx = vertx
        this.eventBusHandler = eventBusHandler
    }

    private fun configureVertx(httpPort: Int) {
        val router = Router.router(vertx)
        val httpServer = vertx.createHttpServer()

        router.route(WEB_RESOURCES_PREFIX)
            .handler(StaticHandler.create(WEB_RESOURCES_DIR))


        val sockJSHandler = SockJSHandler.create(vertx)

        val options = BridgeOptions()
            .addOutboundPermitted(PermittedOptions())
            .addInboundPermitted(PermittedOptions())

        sockJSHandler.bridge(options, EventBusMessageDebugger())

        // mount the bridge on the router
        router.route(EVENTBUS_ADDRESS).handler(sockJSHandler)

        vertx.eventBus().consumer(EVENTBUS_SERVER_HANDLER_ID, eventBusHandler)

        logger.info("Launchpad emulator is ready on http://localhost:$httpPort/")

        httpServer.requestHandler(router).listen(httpPort)
    }

    override fun setListener(listener: LaunchpadListener) {
        this.eventBusHandler.listener = listener
    }

    override fun close() {
        vertx.close()
    }

    companion object {

        private val logger = LoggerFactory.getLogger(EmulatorLaunchpad::class.java)

        /**
         * Web path for accessing static files.
         */
        private const val WEB_RESOURCES_PREFIX = "/*"

        /**
         * Resources directory to serve static files from.
         */
        private const val WEB_RESOURCES_DIR = "web"

        /**
         * URL of the Vertx eventbus bridge
         */
        private const val EVENTBUS_ADDRESS = "/eventbus/*"

        /**
         * Eventbus ID of the emulator, on the server side
         */
        private const val EVENTBUS_SERVER_HANDLER_ID = "lp4j:server"
    }
}
