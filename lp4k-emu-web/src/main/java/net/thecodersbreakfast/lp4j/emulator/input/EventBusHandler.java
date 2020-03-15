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

    private LaunchpadListener listener;

    @Override
    public void handle(Message<JsonObject> message) {
        if (listener == null) {
            return;
        }

        long timestamp = System.currentTimeMillis();
        JsonObject body = message.body();
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
