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

package net.thecodersbreakfast.lp4j.midi;

import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.midi.protocol.MidiProtocolClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sound.midi.InvalidMidiDataException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MidiLaunchpadClientTest {

    private static final int LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE = 104;
    public static final int LAUNCHPAD_S_RAW_RIGHT_BUTTON_MIDI_CODE = 8;

    private LaunchpadClient launchpadClient;

    @Mock
    private MidiProtocolClient midiProtocolClient;

    @BeforeEach
    public void init() {
        midiProtocolClient = mock(MidiProtocolClient.class);
        launchpadClient = new MidiLaunchpadClient(midiProtocolClient);
    }

    @Test
    void instance_creation_failed_for_null_midi_protocol_client() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MidiLaunchpadClient(null)
        );
    }

    /*
    ================================================================================
    setBrightness
    ================================================================================
    */

    @Test
    void brightness_was_not_set_for_null_argument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setBrightness(null)
        );
    }

    @Test
    public void brightness_set_successful() throws InvalidMidiDataException {
        int expectedNumerator = 1;
        int expectedDenominator = 18 - Brightness.BRIGHTNESS_MAX.getBrightnessLevel();

        launchpadClient.setBrightness(Brightness.BRIGHTNESS_MAX);

        verify(midiProtocolClient).brightness(expectedNumerator, expectedDenominator);
    }

    @Test
    public void setBrightness_tooLow() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setBrightness(Brightness.of(Brightness.MIN_VALUE - 1))
        );
    }

    @Test
    public void setBrightness_tooHigh() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setBrightness(Brightness.of(Brightness.MAX_VALUE + 1))
        );
    }

    @Test
    public void setBrightness_exception() throws InvalidMidiDataException {
        doThrow(new InvalidMidiDataException()).when(midiProtocolClient).brightness(anyInt(), anyInt());
        assertThrows(LaunchpadException.class, () -> launchpadClient.setBrightness(Brightness.BRIGHTNESS_MIN));
    }

    /*
    ================================================================================
    setBuffers
    ================================================================================
    */

    @Test
    public void setBuffers() throws InvalidMidiDataException {
        launchpadClient.setBuffers(Buffer.BUFFER_0, Buffer.BUFFER_1, false, false);
        verify(midiProtocolClient).doubleBufferMode(0, 1, false, false);
    }

    @Test
    public void setBuffers_exception() throws InvalidMidiDataException {
        doThrow(new InvalidMidiDataException()).when(midiProtocolClient).doubleBufferMode(anyInt(), anyInt(), anyBoolean(), anyBoolean());
        assertThrows(
            LaunchpadException.class,
            () -> launchpadClient.setBuffers(Buffer.BUFFER_0, Buffer.BUFFER_1, false, false)
        );
    }

    @Test
    void set_buffers_failed_for_null_visible_buffer() {
        assertThrows(
            IllegalArgumentException.class,
            ()->launchpadClient.setBuffers(
                null,
                Buffer.BUFFER_1,
                true,
                true
            )
        );
    }

    @Test
    void set_buffers_failed_for_null_write_buffer() {
        assertThrows(
            IllegalArgumentException.class,
            ()->launchpadClient.setBuffers(
                Buffer.BUFFER_0,
                null,
                false,
                false
            )
        );
    }

    /*
    ================================================================================
    setButtonLight
    ================================================================================
    */

    @Test
    public void setButtonLight_exception() throws InvalidMidiDataException {
        doThrow(new InvalidMidiDataException()).when(midiProtocolClient).buttonOn(anyInt(), anyInt());
        assertThrows(
            LaunchpadException.class,
            () -> launchpadClient.setButtonLight(Button.UP, Color.BLACK, BackBufferOperation.COPY)
        );
    }

    @Test
    void set_button_light_failed_for_null_button() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setButtonLight(
                null,
                Color.RED,
                BackBufferOperation.NONE
            )
        );
    }

    @Test
    void set_button_light_failed_for_null_color() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setButtonLight(
                Button.MIXER,
                null,
                BackBufferOperation.NONE
            )
        );
    }

    @Test
    void set_button_light_failed_for_null_operation() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setButtonLight(
                Button.MIXER,
                Color.RED,
                null
            )
        );
    }

    @Test
    public void setButtonLight_topButton_NONE() throws InvalidMidiDataException {
        launchpadClient.setButtonLight(Button.DOWN, Color.BLACK, BackBufferOperation.NONE);
        verify(midiProtocolClient)
            .buttonOn(
                LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE + Button.DOWN.getCoordinate(),
                0
            );
    }

    @Test
    public void setButtonLight_topButton_CLEAR() throws InvalidMidiDataException {
        launchpadClient.setButtonLight(Button.UP, Color.BLACK, BackBufferOperation.CLEAR);
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 8);
    }

    @Test
    public void setButtonLight_topButton_COPY() throws InvalidMidiDataException {
        launchpadClient.setButtonLight(Button.UP, Color.BLACK, BackBufferOperation.COPY);
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 12);
    }

    @Test
    public void setButtonLight_OORANGE_for_topButton_COPY() throws InvalidMidiDataException {
        launchpadClient.setButtonLight(Button.UP, Color.ORANGE, BackBufferOperation.COPY);
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 47);
    }

    @Test
    public void setButtonLight_rightButton() throws InvalidMidiDataException {
        launchpadClient.setButtonLight(Button.PAN, Color.BLACK, BackBufferOperation.COPY);
        verify(midiProtocolClient).noteOn(24, 12);
    }

    @Test
    public void setButtonLight_rightButton_COPY() throws InvalidMidiDataException {
        launchpadClient.setButtonLight(Button.VOL, Color.BLACK, BackBufferOperation.COPY);
        verify(midiProtocolClient).noteOn(LAUNCHPAD_S_RAW_RIGHT_BUTTON_MIDI_CODE, 12);
    }

    @Test
    public void setButtonLight_XY_illegal_COPY() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setButtonLight(Button.atTop(-1), Color.BLACK, BackBufferOperation.COPY)
        );
    }

    @Test
    public void setButtonLight_topButton_XY_COPY() throws InvalidMidiDataException {
        launchpadClient.setButtonLight(Button.atTop(0), Color.BLACK, BackBufferOperation.COPY); // UP
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 12);
    }

    @Test
    public void setButtonLight_rightButton_XY_COPY() throws InvalidMidiDataException {
        launchpadClient.setButtonLight(Button.atRight(0), Color.BLACK, BackBufferOperation.COPY); // VOL
        verify(midiProtocolClient).noteOn(8, 12);
    }

    /*
    ================================================================================
    setPadLight
    ================================================================================
    */

    @Test
    public void setPadLight_COPY() throws InvalidMidiDataException {
        launchpadClient.setPadLight(Pad.at(0, 0), Color.BLACK, BackBufferOperation.COPY);
        verify(midiProtocolClient).noteOn(0, 12);
    }

    @Test
    public void setPadLight_illegal() {
        assertThrows(IllegalArgumentException.class,
            () -> launchpadClient.setPadLight(
                Pad.at(-1, -1),
                Color.BLACK,
                BackBufferOperation.COPY
            )
        );
    }

    @Test
    public void setPadLight_exception() throws InvalidMidiDataException {
        doThrow(new InvalidMidiDataException()).when(midiProtocolClient).noteOn(anyInt(), anyInt());
        assertThrows(
            LaunchpadException.class,
            () -> launchpadClient.setPadLight(
                Pad.at(1, 1),
                Color.BLACK,
                BackBufferOperation.COPY
            )
        );
    }

    @Test
    void set_pad_light_failed_for_null_pad() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setPadLight(
                null,
                Color.AMBER,
                BackBufferOperation.NONE
            )
        );
    }

    @Test
    void set_pad_light_failed_for_null_color() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setPadLight(
                Pad.at(Pad.X_MAX, Pad.Y_MAX),
                null,
                BackBufferOperation.NONE
            )
        );
    }

    @Test
    void set_pad_light_failed_for_null_operation() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setPadLight(
                Pad.at(Pad.X_MAX, Pad.Y_MAX),
                Color.AMBER,
                null
            )
        );
    }

    /*
    ================================================================================
    reset
    ================================================================================
    */

    @Test
    public void reset() throws InvalidMidiDataException {
        launchpadClient.reset();
        verify(midiProtocolClient).reset();
    }

    @Test
    public void reset_exception() throws InvalidMidiDataException {
        doThrow(new InvalidMidiDataException()).when(midiProtocolClient).reset();
        assertThrows(LaunchpadException.class, () -> launchpadClient.reset());
        verify(midiProtocolClient).reset();
    }

    /*
    ================================================================================
    testLights
    ================================================================================
    */

    @Test
    public void testLights() throws InvalidMidiDataException {
        launchpadClient.testLights(LightIntensity.LOW);
        verify(midiProtocolClient).lightsOn(125);
        launchpadClient.testLights(LightIntensity.MEDIUM);
        verify(midiProtocolClient).lightsOn(126);
        launchpadClient.testLights(LightIntensity.HIGH);
        verify(midiProtocolClient).lightsOn(127);
    }

    @Test
    public void testLights_exception() throws InvalidMidiDataException {
        doThrow(new InvalidMidiDataException()).when(midiProtocolClient).lightsOn(anyInt());
        assertThrows(
            LaunchpadException.class,
            () -> launchpadClient.testLights(LightIntensity.LOW)
        );
    }

    @Test
    void test_lights_failed_because_of_null_input() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.testLights(null)
        );
    }

    /*
    ================================================================================
    setLights
    ================================================================================
    */

    @Test
    public void setLights_null() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setLights(null, BackBufferOperation.NONE)
        );
    }

    @Test
    public void setLights_odd() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setLights(new Color[1], BackBufferOperation.NONE)
        );
    }

    @Test
    public void setLights_exception() throws InvalidMidiDataException {
        doThrow(new InvalidMidiDataException()).when(midiProtocolClient).notesOn(any());

        assertThrows(
            LaunchpadException.class,
            () -> launchpadClient.setLights(new Color[]{Color.BLACK, Color.BLACK}, BackBufferOperation.COPY)
        );
    }

    @Test
    public void setLights_COPY() throws InvalidMidiDataException {
        launchpadClient.setLights(new Color[]{Color.BLACK, Color.BLACK}, BackBufferOperation.COPY);
        verify(midiProtocolClient).notesOn(12, 12);
    }

    @Test
    void set_lights_failed_for_null_operation() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.setLights(new Color[]{}, null)
        );
    }

    /*
    ================================================================================
    scrollText
    ================================================================================
    */

    @Test
    public void scrollText_speedTooLow() {
        assertThrows(IllegalArgumentException.class,
            () -> launchpadClient.scrollText("Hello", Color.BLACK,
                ScrollSpeed.of(ScrollSpeed.MIN_VALUE - 1),
                false,
                BackBufferOperation.COPY)
        );
    }

    @Test
    public void scrollText_speedTooHIgh() {
        assertThrows(IllegalArgumentException.class,
            () -> launchpadClient.scrollText(
                "Hello",
                Color.BLACK,
                ScrollSpeed.of(ScrollSpeed.MAX_VALUE + 1),
                false,
                BackBufferOperation.COPY
            )
        );
    }

    @Test
    public void scrollText_nullColor() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.scrollText(
                "Hello",
                null,
                ScrollSpeed.SPEED_MIN,
                false,
                BackBufferOperation.COPY
            )
        );
    }

    @Test
    public void scrollText_COPY() throws InvalidMidiDataException {
        launchpadClient.scrollText("Hello", Color.BLACK, ScrollSpeed.SPEED_MIN, false, BackBufferOperation.COPY);
        verify(midiProtocolClient).text("Hello", 12, 1, false);
    }

    @Test
    public void scrollText_exception() throws InvalidMidiDataException {
        doThrow(new InvalidMidiDataException())
            .when(midiProtocolClient).text(anyString(), anyInt(), anyInt(), anyBoolean());

        assertThrows(
            LaunchpadException.class,
            () -> launchpadClient.scrollText(
                "Hello",
                Color.BLACK,
                ScrollSpeed.SPEED_MIN,
                false,
                BackBufferOperation.COPY
            )
        );
    }

    @Test
    void set_scroll_text_failed_for_null_speed() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.scrollText(
                "text",
                Color.ORANGE,
                null,
                true,
                BackBufferOperation.NONE
            )
        );
    }

    @Test
    void set_scroll_text_failed_for_null_operation() {
        assertThrows(
            IllegalArgumentException.class,
            () -> launchpadClient.scrollText(
                "text",
                Color.ORANGE,
                ScrollSpeed.SPEED_MAX,
                true,
                null
            )
        );
    }
}