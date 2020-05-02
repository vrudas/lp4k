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

package io.lp4k.emulator;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import net.thecodersbreakfast.lp4j.api.*;
import io.lp4k.emulator.output.OutputEventType;

/**
 * A client to communicate with the Launchpad emulator
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
public class EmulatorLaunchpadClient implements LaunchpadClient {

    /**
     * Eventbus ID of the emulator, on the browser side
     */
    private static final String EVENTBUS_CLIENT_HANDLER_ID = "lp4j:client";

    /**
     * The Vertx engine that powers the emulator on the server side
     */
    private final Vertx vertx;

    /**
     * Constructor
     *
     * @param vertx The Vertx engine to use
     */
    public EmulatorLaunchpadClient(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void reset() {
        publishEvent(OutputEventType.RST);
    }

    /**
     * {@inheritDoc}
     *
     * @param intensity The desired light intensity. Must not be null.
     */
    @Override
    public void testLights(LightIntensity intensity) {
        requireNonNullIntensity(intensity);

        int brightness = prepareBrightnessValue(intensity);

        JsonObject params = new JsonObject()
            .put("i", brightness);

        publishEvent(OutputEventType.TST, params);
    }

    private int prepareBrightnessValue(LightIntensity intensity) {
        switch (intensity) {
            case LOW:
                return 5;
            case MEDIUM:
                return 10;
            case HIGH:
                return 15;
            default:
                return 0;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException because not implemented in emulator
     */
    @Override
    public void setLights(
        Color[] colors,
        BackBufferOperation operation
    ) {
        throw new UnsupportedOperationException("Not implemented in emulator");
    }

    /**
     * {@inheritDoc}
     *
     * @param pad       The pad to light up. Must not be null.
     * @param color     The color to use. Use {@link net.thecodersbreakfast.lp4j.api.Color#BLACK}
     *                  to switch the light off. Must not be null.
     * @param operation What to do on the backbuffer. Must not be null.
     */
    @Override
    public void setPadLight(
        Pad pad,
        Color color,
        BackBufferOperation operation
    ) {
        requireNonNullPad(pad);
        requireNonNullColor(color);
        requireNonNullBackBufferOperation(operation);

        JsonObject params = new JsonObject()
            .put("x", pad.getX())
            .put("y", pad.getY())
            .put(
                "c",
                new JsonObject()
                    .put("r", color.getRedIntensity())
                    .put("g", color.getGreenIntensity())
            )
            .put("o", operation.name());

        publishEvent(OutputEventType.PADLGT, params);
    }

    /**
     * {@inheritDoc}
     *
     * @param button    The button to light up. Must not be null.
     * @param color     The color to use. Use {@link net.thecodersbreakfast.lp4j.api.Color#BLACK}
     *                  to switch the light off. Must not be null.
     * @param operation What to do on the backbuffer. Must not be null.
     */
    @Override
    public void setButtonLight(
        Button button,
        Color color,
        BackBufferOperation operation
    ) {
        requireNonNullButton(button);
        requireNonNullColor(color);
        requireNonNullBackBufferOperation(operation);

        JsonObject params = new JsonObject()
            .put("t", button.isTopButton())
            .put("i", button.getCoordinate())
            .put(
                "c",
                new JsonObject()
                    .put("r", color.getRedIntensity())
                    .put("g", color.getGreenIntensity())
            )
            .put("o", operation.name());

        publishEvent(OutputEventType.BTNLGT, params);
    }

    /**
     * {@inheritDoc}
     *
     * @param brightness The desired brightness. Must not be null.
     */
    @Override
    public void setBrightness(Brightness brightness) {
        requireNonNullBrightness(brightness);

        JsonObject params = new JsonObject()
            .put("b", brightness.getBrightnessLevel());

        publishEvent(OutputEventType.BRGHT, params);
    }

    /**
     * {@inheritDoc}
     *
     * @param visibleBuffer The buffer to display. Must not be null.
     * @param writeBuffer   The buffer to which the commands are applied. Must not be null.
     */
    @Override
    public void setBuffers(
        Buffer visibleBuffer,
        Buffer writeBuffer,
        boolean copyVisibleBufferToWriteBuffer,
        boolean autoSwap
    ) {
        requireNonNullBuffer(visibleBuffer, "Visible buffer must not be null.");
        requireNonNullBuffer(writeBuffer, "Write buffer must not be null.");

        JsonObject params = new JsonObject()
            .put("v", visibleBuffer.name())
            .put("w", writeBuffer.name())
            .put("c", copyVisibleBufferToWriteBuffer)
            .put("a", autoSwap);

        publishEvent(OutputEventType.BUF, params);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException because not implemented in emulator
     */
    @Override
    public void scrollText(
        String text,
        Color color,
        ScrollSpeed speed,
        boolean loop,
        BackBufferOperation operation
    ) {
        throw new UnsupportedOperationException("Not implemented in emulator");
    }

    /**
     * Sends the given event to the emulator, with no additional parameters
     *
     * @param outputEventType The event to send
     */
    private void publishEvent(OutputEventType outputEventType) {
        publishEvent(outputEventType, null);
    }

    /**
     * Sends the given event to the emulator, with the given additional parameters
     *
     * @param outputEventType The event to send
     * @param params          The additional parameters
     */
    private void publishEvent(
        OutputEventType outputEventType,
        JsonObject params
    ) {
        JsonObject payload = new JsonObject();
        payload.put("evt", outputEventType.name());

        if (params != null) {
            payload.mergeIn(params);
        }

        vertx.eventBus().publish(EVENTBUS_CLIENT_HANDLER_ID, payload);
    }

    private void requireNonNullIntensity(LightIntensity intensity) {
        if (intensity == null) {
            throw new IllegalArgumentException("Light intensity must not be null.");
        }
    }

    private void requireNonNullColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color must not be null.");
        }
    }

    private void requireNonNullBackBufferOperation(BackBufferOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("BackBuffer operation must not be null.");
        }
    }

    private void requireNonNullPad(Pad pad) {
        if (pad == null) {
            throw new IllegalArgumentException("Pad must not be null");
        }
    }

    private void requireNonNullButton(Button button) {
        if (button == null) {
            throw new IllegalArgumentException("Button must not be null.");
        }
    }

    private void requireNonNullBrightness(Brightness brightness) {
        if (brightness == null) {
            throw new IllegalArgumentException("Brightness must not be null");
        }
    }

    private void requireNonNullBuffer(Buffer visibleBuffer, String errorMessage) {
        if (visibleBuffer == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

}
