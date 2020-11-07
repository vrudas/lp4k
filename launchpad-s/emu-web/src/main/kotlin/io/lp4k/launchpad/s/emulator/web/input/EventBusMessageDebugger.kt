/*
 *    Copyright 2020 Vasyl Rudas
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
package io.lp4k.launchpad.s.emulator.web.input

import io.vertx.core.Handler
import io.vertx.ext.bridge.BridgeEventType
import io.vertx.ext.web.handler.sockjs.BridgeEvent
import org.slf4j.LoggerFactory

internal class EventBusMessageDebugger : Handler<BridgeEvent> {

    override fun handle(event: BridgeEvent) {
        // You can also optionally provide a handler like this which will be passed any events that occur on the bridge
        // You can use this for monitoring or logging, or to change the raw messages in-flight.
        // It can also be used for fine grained acgcess control.

        if (event.type() == BridgeEventType.SOCKET_CREATED) {
            logger.debug("A socket was created")
        }

        if (event.type() == BridgeEventType.PUBLISH || event.type() == BridgeEventType.SEND) {
            logger.debug("Event raw message: ${event.rawMessage}")
        }

        event.complete(true)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EventBusMessageDebugger::class.java)
    }
}
