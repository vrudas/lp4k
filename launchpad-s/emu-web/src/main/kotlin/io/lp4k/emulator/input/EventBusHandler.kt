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

package io.lp4k.emulator.input

import io.lp4k.launchpad.s.api.ButtonLaunchS
import io.lp4k.launchpad.s.api.PadLaunchS
import io.lp4k.emulator.input.InputEventType.*
import io.lp4k.launchpad.api.LaunchpadListener
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

/**
 * Handler for Vertx eventbus messages.
 */
class EventBusHandler : Handler<Message<JsonObject>> {

    lateinit var listener: LaunchpadListener

    override fun handle(message: Message<JsonObject>) {
        val timestamp = System.currentTimeMillis()
        val body = message.body()

        when (body.extractInputEventType()) {
            PP -> processPadEvent(body, timestamp, listener::onPadPressed)
            PR -> processPadEvent(body, timestamp, listener::onPadReleased)
            BP -> processButtonEvent(body, timestamp, listener::onButtonPressed)
            BR -> processButtonEvent(body, timestamp, listener::onButtonReleased)
            TS -> listener.onTextScrolled(timestamp)
        }
    }

    private fun JsonObject.extractInputEventType(): InputEventType = valueOf(getString(EVENT_TYPE_KEY))

    private fun processPadEvent(
        body: JsonObject,
        timestamp: Long,
        padEventConsumer: (PadLaunchS, Long) -> (Unit)
    ) {
        val pad = body.extractPad()
        padEventConsumer(pad, timestamp)
    }

    private fun JsonObject.extractPad(): PadLaunchS {
        val x = extractCoordinate(PAD_X_KEY)
        val y = extractCoordinate(PAD_Y_KEY)
        return PadLaunchS.at(x, y)
    }

    private fun processButtonEvent(
        body: JsonObject,
        timestamp: Long,
        buttonEventConsumer: (ButtonLaunchS, Long) -> (Unit)
    ) {
        val button = body.extractButton()
        buttonEventConsumer(button, timestamp)
    }

    private fun JsonObject.extractButton(): ButtonLaunchS {
        val x = extractCoordinate(BUTTON_X_KEY)
        val y = extractCoordinate(BUTTON_Y_KEY)

        val buttonCoordinate = calculateButtonCoordinate(x, y)

        return if (x == RIGHT_BUTTON_COORDINATE) {
            ButtonLaunchS.atRight(buttonCoordinate)
        } else {
            ButtonLaunchS.atTop(buttonCoordinate)
        }
    }

    private fun JsonObject.extractCoordinate(coordinateKey: String): Int {
        return getInteger(coordinateKey)
            ?: throw IllegalArgumentException("Coordinate '$coordinateKey' cant be null")
    }

    private fun calculateButtonCoordinate(x: Int, y: Int): Int {
        return if (x == RIGHT_BUTTON_COORDINATE) y else x
    }

    companion object {
        private const val EVENT_TYPE_KEY = "evt"

        private const val PAD_X_KEY = "x"
        private const val PAD_Y_KEY = "y"

        private const val BUTTON_X_KEY = "x"
        private const val BUTTON_Y_KEY = "y"

        private const val RIGHT_BUTTON_COORDINATE = -1
    }
}
