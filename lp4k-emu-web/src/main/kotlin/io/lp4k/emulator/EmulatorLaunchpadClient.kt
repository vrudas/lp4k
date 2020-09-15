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

import io.lp4k.launchpad.api.s.ColorLaunchS
import io.lp4k.launchpad.api.s.LightIntensityLaunchS
import io.lp4k.emulator.output.OutputEventType
import io.lp4k.launchpad.api.*
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

/**
 * A client to communicate with the Launchpad emulator
 *
 */
internal class EmulatorLaunchpadClient(
    /**
     * The Vertx engine that powers the emulator on the server side
     */
    private val vertx: Vertx
) : LaunchpadClient {

    override fun reset() {
        publishEvent(OutputEventType.RST)
    }

    /**
     * {@inheritDoc}
     *
     * @param intensity The desired light intensity. Must not be null.
     */
    override fun testLights(intensity: LightIntensity) {
        val brightness = prepareBrightnessValue(intensity)

        val params = JsonObject().put("i", brightness)

        publishEvent(OutputEventType.TST, params)
    }

    private fun prepareBrightnessValue(intensity: LightIntensity): Int {
        return when (intensity) {
            LightIntensityLaunchS.LOW -> 5
            LightIntensityLaunchS.MEDIUM -> 10
            LightIntensityLaunchS.HIGH -> 15
            else -> 0
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException because not implemented in emulator
     */
    override fun setLights(
        colors: Array<Color>,
        operation: BackBufferOperation
    ) {
        throw UnsupportedOperationException("Not implemented in emulator")
    }

    /**
     * {@inheritDoc}
     *
     * @param pad       The pad to light up. Must not be null.
     * @param color     The color to use. Use {@link net.thecodersbreakfast.lp4j.api.Color#BLACK}
     *                  to switch the light off. Must not be null.
     * @param operation What to do on the backbuffer. Must not be null.
     */
    override fun setPadLight(
        pad: Pad,
        color: Color,
        operation: BackBufferOperation
    ) {
        color as ColorLaunchS

        val params = JsonObject()
            .put("x", pad.x)
            .put("y", pad.y)
            .put(
                "c",
                JsonObject()
                    .put("r", color.redIntensity)
                    .put("g", color.greenIntensity)
            )
            .put("o", operation.name)

        publishEvent(OutputEventType.PADLGT, params)
    }

    /**
     * {@inheritDoc}
     *
     * @param button    The button to light up. Must not be null.
     * @param color     The color to use. Use {@link net.thecodersbreakfast.lp4j.api.Color#BLACK}
     *                  to switch the light off. Must not be null.
     * @param operation What to do on the backbuffer. Must not be null.
     */
    override fun setButtonLight(
        button: Button,
        color: Color,
        operation: BackBufferOperation
    ) {
        color as ColorLaunchS

        val params = JsonObject()
            .put("t", button.isTopButton)
            .put("i", button.coordinate)
            .put(
                "c",
                JsonObject()
                    .put("r", color.redIntensity)
                    .put("g", color.greenIntensity)
            )
            .put("o", operation.name)

        publishEvent(OutputEventType.BTNLGT, params)
    }

    /**
     * {@inheritDoc}
     *
     * @param brightness The desired brightness. Must not be null.
     */
    override fun setBrightness(brightness: Brightness) {
        val params = JsonObject()
            .put("b", brightness.brightnessLevel)

        publishEvent(OutputEventType.BRGHT, params)
    }

    /**
     * {@inheritDoc}
     *
     * @param visibleBuffer The buffer to display. Must not be null.
     * @param writeBuffer   The buffer to which the commands are applied. Must not be null.
     */
    override fun setBuffers(
        visibleBuffer: Buffer,
        writeBuffer: Buffer,
        copyVisibleBufferToWriteBuffer: Boolean,
        autoSwap: Boolean
    ) {
        val params = JsonObject()
            .put("v", visibleBuffer.name)
            .put("w", writeBuffer.name)
            .put("c", copyVisibleBufferToWriteBuffer)
            .put("a", autoSwap)

        publishEvent(OutputEventType.BUF, params)
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException because not implemented in emulator
     */
    override fun scrollText(
        text: String,
        color: Color,
        speed: ScrollSpeed,
        loop: Boolean,
        operation: BackBufferOperation
    ) {
        throw UnsupportedOperationException("Not implemented in emulator")
    }

    /**
     * Sends the given event to the emulator, with no additional parameters
     *
     * @param outputEventType The event to send
     */
    private fun publishEvent(outputEventType: OutputEventType) {
        publishEvent(outputEventType, null)
    }

    /**
     * Sends the given event to the emulator, with the given additional parameters
     *
     * @param outputEventType The event to send
     * @param params          The additional parameters
     */
    private fun publishEvent(
        outputEventType: OutputEventType,
        params: JsonObject?
    ) {
        val payload = JsonObject()
        payload.put("evt", outputEventType.name)

        if (params != null) {
            payload.mergeIn(params)
        }

        vertx.eventBus().publish(EVENTBUS_CLIENT_HANDLER_ID, payload)
    }

    companion object {
        /**
         * Eventbus ID of the emulator, on the browser side
         */
        private const val EVENTBUS_CLIENT_HANDLER_ID = "lp4j:client"
    }
}
