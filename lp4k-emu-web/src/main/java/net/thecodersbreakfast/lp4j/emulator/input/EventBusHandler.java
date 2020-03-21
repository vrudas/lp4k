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

package net.thecodersbreakfast.lp4j.emulator.input;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.LaunchpadListener;
import net.thecodersbreakfast.lp4j.api.Pad;

/**
 * Handler for Vertx eventbus messages.
 */
public class EventBusHandler implements Handler<Message<JsonObject>> {

    static final String EVENT_TYPE_KEY = "evt";

    static final String PAD_X_KEY = "x";
    static final String PAD_Y_KEY = "y";

    static final String BUTTON_X_KEY = "x";
    static final String BUTTON_Y_KEY = "y";

    static final int RIGHT_BUTTON_COORDINATE = -1;

    private LaunchpadListener listener;

    public void setListener(LaunchpadListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(Message<JsonObject> message) {
        if (listener == null) {
            return;
        }

        long timestamp = System.currentTimeMillis();
        JsonObject body = message.body();

        InputEventType inputEventType = convertToInputEventType(body.getString(EVENT_TYPE_KEY));

        switch (inputEventType) {
            case PP: {
                Pad pad = extractPad(body);
                listener.onPadPressed(pad, timestamp);
                break;
            }
            case PR: {
                Pad pad = extractPad(body);
                listener.onPadReleased(pad, timestamp);
                break;
            }
            case BP: {
                Button button = extractButton(body);
                listener.onButtonPressed(button, timestamp);
                break;
            }
            case BR: {
                Button button = extractButton(body);
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

    private InputEventType convertToInputEventType(String eventTypeName) {
        try {
            return InputEventType.valueOf(eventTypeName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cant convert '" + eventTypeName + "' to enum value");
        }
    }

    private Pad extractPad(JsonObject body) {
        Integer x = extractCoordinate(body, PAD_X_KEY);
        Integer y = extractCoordinate(body, PAD_Y_KEY);
        return Pad.at(x, y);
    }

    private Button extractButton(JsonObject body) {
        int x = extractCoordinate(body, "x");
        int y = extractCoordinate(body, "y");

        int buttonCoordinate = calculateButtonCoordinate(x, y);

        if (x == RIGHT_BUTTON_COORDINATE) {
            return Button.atRight(buttonCoordinate);
        } else {
            return Button.atTop(buttonCoordinate);
        }
    }

    private Integer extractCoordinate(JsonObject body, String coordinateKey) {
        Integer coordinate = body.getInteger(coordinateKey);

        if (coordinate == null) {
            throw new IllegalArgumentException("Coordinate '" + coordinateKey + "' cant be null");
        }

        return coordinate;
    }

    private int calculateButtonCoordinate(int x, int y) {
        if (x == RIGHT_BUTTON_COORDINATE) {
            return y;
        } else {
            return x;
        }
    }

}
