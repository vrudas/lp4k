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

import io.lp4k.emulator.input.InputEventType;
import io.lp4k.emulator.input.MockMessage;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.LaunchpadListener;
import net.thecodersbreakfast.lp4j.api.Pad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static net.thecodersbreakfast.lp4j.emulator.input.EventBusHandler.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EventBusHandlerTest {

    private static MockMessage padPressMessage(Integer x, Integer y) {
        return message(
            InputEventType.PP,
            Coordinate.of(PAD_X_KEY, x),
            Coordinate.of(PAD_Y_KEY, y)
        );
    }

    private static MockMessage padReleaseMessage(Integer x, Integer y) {
        return message(
            InputEventType.PR,
            Coordinate.of(PAD_X_KEY, x),
            Coordinate.of(PAD_Y_KEY, y)
        );
    }

    private static MockMessage buttonPressMessage(Integer x, Integer y) {
        return message(
            InputEventType.BP,
            Coordinate.of(BUTTON_X_KEY, x),
            Coordinate.of(BUTTON_Y_KEY, y)
        );
    }

    private static MockMessage buttonReleaseMessage(Integer x, Integer y) {
        return message(
            InputEventType.BR,
            Coordinate.of(BUTTON_X_KEY, x),
            Coordinate.of(BUTTON_Y_KEY, y)
        );
    }

    private static MockMessage message(
        InputEventType eventType,
        Coordinate xCoordinate,
        Coordinate yCoordinate
    ) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(EVENT_TYPE_KEY, eventType.name());

        if (xCoordinate.getCoordinateValue() != null) {
            bodyMap.put(
                xCoordinate.getKeyName(),
                xCoordinate.getCoordinateValue()
            );
        }

        if (yCoordinate.getCoordinateValue() != null) {
            bodyMap.put(
                yCoordinate.getKeyName(),
                yCoordinate.getCoordinateValue()
            );
        }

        return new MockMessage(bodyMap);
    }

    private static MockMessage message(String inputEventName) {
        return new MockMessage(singletonMap(EVENT_TYPE_KEY, inputEventName));
    }

    private LaunchpadListener launchpadListener;

    private EventBusHandler handler;

    @BeforeEach
    void setUp() {
        launchpadListener = mock(LaunchpadListener.class);

        handler = new EventBusHandler();
        handler.setListener(launchpadListener);
    }

    @Test
    void nothing_happened_in_case_when_launchpad_listener_is_not_set() {
        handler.setListener(null);

        assertDoesNotThrow(() -> handler.handle(new MockMessage()));
    }

    @Test
    void exception_is_thrown_for_null_input_event_type() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(message(null))
        );
    }

    @Test
    void exception_is_thrown_for_empty_input_event_type() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(message(""))
        );
    }

    @Test
    void exception_is_thrown_for_invalid_input_event_type() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(message("InputEventType"))
        );
    }

    @Test
    void launchpad_listener_scroll_text_was_called_for_required_input_event_type() {
        handler.handle(message(InputEventType.TS.name()));

        verify(launchpadListener).onTextScrolled(anyLong());
    }

    @Test
    void launchpad_listener_pad_press_event_was_not_called_because_of_missing_x_coordinate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(padPressMessage(null, null))
        );
    }

    @Test
    void launchpad_listener_pad_press_event_was_not_called_because_of_missing_y_coordinate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(padPressMessage(Pad.X_MAX, null))
        );
    }

    @Test
    void launchpad_listener_pad_press_was_called_for_required_input_event_type() {
        Pad pad = Pad.at(Pad.X_MIN, Pad.Y_MAX);

        handler.handle(padPressMessage(pad.getX(), pad.getY()));

        verify(launchpadListener).onPadPressed(eq(pad), anyLong());
    }

    @Test
    void launchpad_listener_pad_release_event_was_not_called_because_of_missing_x_coordinate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(padPressMessage(null, null))
        );
    }

    @Test
    void launchpad_listener_pad_release_event_was_not_called_because_of_missing_y_coordinate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(padPressMessage(Pad.X_MIN, null))
        );
    }

    @Test
    void launchpad_listener_pad_release_was_called_for_required_input_event_type() {
        Pad pad = Pad.at(Pad.X_MIN, Pad.Y_MAX);

        handler.handle(padReleaseMessage(pad.getX(), pad.getY()));

        verify(launchpadListener).onPadReleased(eq(pad), anyLong());
    }

    @Test
    void launchpad_listener_button_press_event_was_not_called_because_of_missing_x_coordinate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(buttonPressMessage(null, null))
        );
    }

    @Test
    void launchpad_listener_button_press_event_was_not_called_because_of_missing_y_coordinate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(buttonPressMessage(Button.MAX_COORD, null))
        );
    }

    @Test
    @SuppressWarnings("SuspiciousNameCombination")
    void launchpad_listener_top_button_press_was_called_for_required_input_event_type() {
        Button topButton = Button.UP;

        handler.handle(buttonPressMessage(topButton.getCoordinate(), RIGHT_BUTTON_COORDINATE));

        verify(launchpadListener).onButtonPressed(eq(topButton), anyLong());
    }

    @Test
    void launchpad_listener_right_button_press_was_called_for_required_input_event_type() {
        Button rightButton = Button.VOL;

        handler.handle(buttonPressMessage(RIGHT_BUTTON_COORDINATE, rightButton.getCoordinate()));

        verify(launchpadListener).onButtonPressed(eq(rightButton), anyLong());
    }

    @Test
    void launchpad_listener_button_release_event_was_not_called_because_of_missing_x_coordinate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(buttonReleaseMessage(null, null))
        );
    }

    @Test
    void launchpad_listener_button_release_event_was_not_called_because_of_missing_y_coordinate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> handler.handle(buttonReleaseMessage(Button.MAX_COORD, null))
        );
    }

    @Test
    @SuppressWarnings("SuspiciousNameCombination")
    void launchpad_listener_top_button_release_was_called_for_required_input_event_type() {
        Button topButton = Button.UP;

        handler.handle(buttonReleaseMessage(topButton.getCoordinate(), RIGHT_BUTTON_COORDINATE));

        verify(launchpadListener).onButtonReleased(eq(topButton), anyLong());
    }

    @Test
    void launchpad_listener_right_button_release_was_called_for_required_input_event_type() {
        Button rightButton = Button.VOL;

        handler.handle(buttonReleaseMessage(RIGHT_BUTTON_COORDINATE, rightButton.getCoordinate()));

        verify(launchpadListener).onButtonReleased(eq(rightButton), anyLong());
    }

}