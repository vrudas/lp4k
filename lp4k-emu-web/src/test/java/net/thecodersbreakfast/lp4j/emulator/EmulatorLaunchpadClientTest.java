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

package net.thecodersbreakfast.lp4j.emulator;

import io.lp4k.emulator.MockEventBus;
import io.lp4k.emulator.MockVertx;
import io.vertx.core.json.JsonObject;
import net.thecodersbreakfast.lp4j.api.*;
import io.lp4k.emulator.output.OutputEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmulatorLaunchpadClientTest {

    private static final String BRIGHTNESS_KEY = "b";
    private static final String LIGHT_INTENSITY_KEY = "i";
    private static final String OPERATION_KEY = "o";

    private static final String COLOR_KEY = "c";
    private static final String RED_COLOR_KEY = "r";
    private static final String GREEN_COLOR_KEY = "g";

    private static final String PAD_X_KEY = "x";
    private static final String PAD_Y_KEY = "y";

    private static final String BUTTON_COORDINATE_KEY = "i";
    private static final String BUTTON_IS_TOP_KEY = "t";

    private static final String VISIBLE_BUFFER_KEY = "v";
    private static final String WRITE_BUFFER_KEY = "w";
    private static final String COPY_VISIBLE_BUFFER_KEY = "c";
    private static final String AUTO_SWAP_BUFFERS_KEY = "a";

    void verifyEventWasPublished(
        OutputEventType outputEventType,
        Map<String, Object> expectedParametersForPublish
    ) {
        Map<String, Object> parametersToCheck = new HashMap<>();

        parametersToCheck.put("evt", outputEventType.name());
        parametersToCheck.putAll(expectedParametersForPublish);

        var message = new JsonObject(parametersToCheck);
        verify(eventBus).publish("lp4j:client", message);
    }

    private MockEventBus eventBus;

    private EmulatorLaunchpadClient emulatorLaunchpadClient;

    @BeforeEach
    void setUp() {
        MockVertx vertx = mock(MockVertx.class);

        eventBus = mock(MockEventBus.class);
        when(vertx.eventBus()).thenReturn(eventBus);

        emulatorLaunchpadClient = new EmulatorLaunchpadClient(vertx);
    }

    @Test
    void reset_event_was_published() {
        emulatorLaunchpadClient.reset();

        verifyEventWasPublished(OutputEventType.RST, emptyMap());
    }

    @Test
    void set_lights_event_not_published_because_not_implemented() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> emulatorLaunchpadClient.setLights(new Color[0], BackBufferOperation.NONE)
        );
    }

    @Test
    void test_lights_event_not_published_because_of_null_light_intensity() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.testLights(null)
        );
    }

    @Test
    void test_lights_event_published_with_low_light_intensity() {
        emulatorLaunchpadClient.testLights(LightIntensity.LOW);

        verifyEventWasPublished(
            OutputEventType.TST,
            Map.of(LIGHT_INTENSITY_KEY, 5)
        );
    }

    @Test
    void test_lights_event_published_with_medium_light_intensity() {
        emulatorLaunchpadClient.testLights(LightIntensity.MEDIUM);

        verifyEventWasPublished(
            OutputEventType.TST,
            Map.of(LIGHT_INTENSITY_KEY, 10)
        );
    }

    @Test
    void test_lights_event_published_with_high_light_intensity() {
        emulatorLaunchpadClient.testLights(LightIntensity.HIGH);

        verifyEventWasPublished(
            OutputEventType.TST,
            Map.of(LIGHT_INTENSITY_KEY, 15)
        );
    }

    @Test
    void set_pad_light_event_not_published_because_of_null_pad_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setPadLight(null, null, null)
        );
    }

    @Test
    void set_pad_light_event_not_published_because_of_null_color_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setPadLight(Pad.at(0, 0), null, null)
        );
    }

    @Test
    void set_pad_light_event_not_published_because_of_null_operation_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setPadLight(
                Pad.at(0, 0),
                Color.RED,
                null
            )
        );
    }

    @Test
    void set_pad_light_event_published() {
        emulatorLaunchpadClient.setPadLight(Pad.at(0, 0), Color.RED, BackBufferOperation.NONE);

        verifyEventWasPublished(
            OutputEventType.PADLGT,
            Map.ofEntries(
                entry(PAD_X_KEY, 0),
                entry(PAD_Y_KEY, 0),
                entry(
                    COLOR_KEY,
                    new JsonObject()
                        .put(RED_COLOR_KEY, 3)
                        .put(GREEN_COLOR_KEY, 0)
                ),
                entry(OPERATION_KEY, "NONE")
            )
        );
    }

    @Test
    void set_button_light_event_not_published_because_of_null_pad_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setButtonLight(null, null, null)
        );
    }

    @Test
    void set_button_light_event_not_published_because_of_null_color_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setButtonLight(Button.UP, null, null)
        );
    }

    @Test
    void set_button_light_event_not_published_because_of_null_operation_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setButtonLight(
                Button.VOL,
                Color.GREEN,
                null
            )
        );
    }

    @Test
    void set_button_light_event_published_for_right_button() {
        emulatorLaunchpadClient.setButtonLight(Button.VOL, Color.GREEN, BackBufferOperation.CLEAR);

        verifyEventWasPublished(
            OutputEventType.BTNLGT,
            Map.ofEntries(
                entry(BUTTON_COORDINATE_KEY, 0),
                entry(BUTTON_IS_TOP_KEY, false),
                entry(
                    COLOR_KEY,
                    new JsonObject()
                        .put(RED_COLOR_KEY, 0)
                        .put(GREEN_COLOR_KEY, 3)
                ),
                entry(OPERATION_KEY, "CLEAR")
            )
        );
    }

    @Test
    void set_button_light_event_published_for_top_button() {
        emulatorLaunchpadClient.setButtonLight(Button.UP, Color.GREEN, BackBufferOperation.COPY);

        verifyEventWasPublished(
            OutputEventType.BTNLGT,
            Map.ofEntries(
                entry(BUTTON_COORDINATE_KEY, 0),
                entry(BUTTON_IS_TOP_KEY, true),
                entry(
                    COLOR_KEY,
                    new JsonObject()
                        .put(RED_COLOR_KEY, 0)
                        .put(GREEN_COLOR_KEY, 3)
                ),
                entry(OPERATION_KEY, "COPY")
            )
        );
    }

    @Test
    void set_brightness_event_not_published_because_of_null_brightness_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setBrightness(null)
        );
    }

    @Test
    void set_brightness_event_published() {
        emulatorLaunchpadClient.setBrightness(Brightness.BRIGHTNESS_MAX);

        verifyEventWasPublished(
            OutputEventType.BRGHT,
            Map.of(
                BRIGHTNESS_KEY,
                Brightness.BRIGHTNESS_MAX.getBrightnessLevel()
            )
        );
    }

    @Test
    void set_buffers_event_not_published_because_of_null_visible_buffer_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setBuffers(
                null,
                null,
                false,
                false
            )
        );
    }

    @Test
    void set_buffers_event_not_published_because_of_null_write_buffer_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> emulatorLaunchpadClient.setBuffers(
                Buffer.BUFFER_0,
                null,
                false,
                false
            )
        );
    }

    @Test
    void set_buffers_event_published() {
        emulatorLaunchpadClient.setBuffers(
            Buffer.BUFFER_1,
            Buffer.BUFFER_0,
            true,
            true
        );

        verifyEventWasPublished(
            OutputEventType.BUF,
            Map.ofEntries(
                entry(VISIBLE_BUFFER_KEY, Buffer.BUFFER_1.name()),
                entry(WRITE_BUFFER_KEY, Buffer.BUFFER_0.name()),
                entry(COPY_VISIBLE_BUFFER_KEY, true),
                entry(AUTO_SWAP_BUFFERS_KEY, true)
            )
        );
    }

    @Test
    void scroll_text_event_not_published_because_not_implemented() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> emulatorLaunchpadClient.scrollText(
                "",
                Color.AMBER,
                ScrollSpeed.SPEED_MIN,
                false,
                BackBufferOperation.NONE
            )
        );
    }
}