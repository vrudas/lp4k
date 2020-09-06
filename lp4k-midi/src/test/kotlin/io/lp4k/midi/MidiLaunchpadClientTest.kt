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
package io.lp4k.midi

import io.lp4k.midi.protocol.MidiProtocolClient
import net.thecodersbreakfast.lp4j.api.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import javax.sound.midi.InvalidMidiDataException

@ExtendWith(MockitoExtension::class)
class MidiLaunchpadClientTest {

    private lateinit var launchpadClient: LaunchpadClient

    @Mock
    private lateinit var midiProtocolClient: MidiProtocolClient

    @BeforeEach
    fun init() {
        midiProtocolClient = Mockito.mock(MidiProtocolClient::class.java)
        launchpadClient = MidiLaunchpadClient(midiProtocolClient)
    }

    /*
    ================================================================================
    setBrightness
    ================================================================================
    */
    @Test
    fun brightness_was_not_set_for_null_argument() {
        assertThrows<IllegalArgumentException> { launchpadClient.setBrightness(null) }
    }

    @Test
    fun brightness_set_successful() {
        val expectedNumerator = 1
        val expectedDenominator = 18 - Brightness.BRIGHTNESS_MAX.brightnessLevel

        launchpadClient.setBrightness(Brightness.BRIGHTNESS_MAX)

        verify(midiProtocolClient).brightness(expectedNumerator, expectedDenominator)
    }

    @Test
    fun setBrightness_tooLow() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setBrightness(Brightness.of(Brightness.MIN_VALUE - 1))
        }
    }

    @Test
    fun setBrightness_tooHigh() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setBrightness(Brightness.of(Brightness.MAX_VALUE + 1))
        }
    }

    @Test
    fun setBrightness_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).brightness(anyInt(), anyInt())

        assertThrows<LaunchpadException> {
            launchpadClient.setBrightness(Brightness.BRIGHTNESS_MIN)
        }
    }

    /*
    ================================================================================
    setBuffers
    ================================================================================
    */
    @Test
    fun setBuffers() {
        launchpadClient.setBuffers(Buffer.BUFFER_0, Buffer.BUFFER_1, false, false)

        verify(midiProtocolClient).doubleBufferMode(
            visibleBuffer = 0,
            writeBuffer = 1,
            copyVisibleBufferToWriteBuffer = false,
            autoSwap = false
        )
    }

    @Test
    fun setBuffers_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).doubleBufferMode(anyInt(), anyInt(), anyBoolean(), anyBoolean())

        assertThrows<LaunchpadException> {
            launchpadClient.setBuffers(
                Buffer.BUFFER_0,
                Buffer.BUFFER_1,
                false,
                false
            )
        }
    }

    @Test
    fun set_buffers_failed_for_null_visible_buffer() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setBuffers(
                null,
                Buffer.BUFFER_1,
                true,
                true
            )
        }
    }

    @Test
    fun set_buffers_failed_for_null_write_buffer() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setBuffers(
                Buffer.BUFFER_0,
                null,
                false,
                false
            )
        }
    }

    /*
    ================================================================================
    setButtonLight
    ================================================================================
    */
    @Test
    fun setButtonLight_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).buttonOn(anyInt(), anyInt())

        assertThrows<LaunchpadException> {
            launchpadClient.setButtonLight(
                Button.UP,
                Color.BLACK,
                BackBufferOperation.COPY
            )
        }
    }

    @Test
    fun set_button_light_failed_for_null_button() {
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            launchpadClient.setButtonLight(
                null,
                Color.RED,
                BackBufferOperation.NONE
            )
        }
    }

    @Test
    fun set_button_light_failed_for_null_color() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setButtonLight(
                Button.MIXER,
                null,
                BackBufferOperation.NONE
            )
        }
    }

    @Test
    fun set_button_light_failed_for_null_operation() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setButtonLight(
                Button.MIXER,
                Color.RED,
                null
            )
        }
    }

    @Test
    fun setButtonLight_topButton_NONE() {
        launchpadClient.setButtonLight(Button.DOWN, Color.BLACK, BackBufferOperation.NONE)

        verify(midiProtocolClient)
            .buttonOn(
                LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE + Button.DOWN.coordinate,
                0
            )
    }

    @Test
    fun setButtonLight_topButton_CLEAR() {
        launchpadClient.setButtonLight(Button.UP, Color.BLACK, BackBufferOperation.CLEAR)
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 8)
    }

    @Test
    fun setButtonLight_topButton_COPY() {
        launchpadClient.setButtonLight(Button.UP, Color.BLACK, BackBufferOperation.COPY)
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 12)
    }

    @Test
    fun setButtonLight_OORANGE_for_topButton_COPY() {
        launchpadClient.setButtonLight(Button.UP, Color.ORANGE, BackBufferOperation.COPY)
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 47)
    }

    @Test
    fun setButtonLight_rightButton() {
        launchpadClient.setButtonLight(Button.PAN, Color.BLACK, BackBufferOperation.COPY)
        verify(midiProtocolClient).noteOn(24, 12)
    }

    @Test
    fun setButtonLight_rightButton_COPY() {
        launchpadClient.setButtonLight(Button.VOL, Color.BLACK, BackBufferOperation.COPY)
        verify(midiProtocolClient).noteOn(LAUNCHPAD_S_RAW_RIGHT_BUTTON_MIDI_CODE, 12)
    }

    @Test
    fun setButtonLight_XY_illegal_COPY() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setButtonLight(Button.atTop(-1), Color.BLACK, BackBufferOperation.COPY)
        }
    }

    @Test
    fun setButtonLight_topButton_XY_COPY() {
        launchpadClient.setButtonLight(Button.atTop(0), Color.BLACK, BackBufferOperation.COPY) // UP
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 12)
    }

    @Test
    fun setButtonLight_rightButton_XY_COPY() {
        launchpadClient.setButtonLight(Button.atRight(0), Color.BLACK, BackBufferOperation.COPY) // VOL
        verify(midiProtocolClient).noteOn(8, 12)
    }

    /*
    ================================================================================
    setPadLight
    ================================================================================
    */
    @Test
    fun setPadLight_COPY() {
        launchpadClient.setPadLight(Pad.at(0, 0), Color.BLACK, BackBufferOperation.COPY)
        verify(midiProtocolClient).noteOn(0, 12)
    }

    @Test
    fun setPadLight_illegal() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setPadLight(
                Pad.at(-1, -1),
                Color.BLACK,
                BackBufferOperation.COPY
            )
        }
    }

    @Test
    fun setPadLight_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).noteOn(anyInt(), anyInt())
        assertThrows<LaunchpadException> {
            launchpadClient.setPadLight(
                Pad.at(1, 1),
                Color.BLACK,
                BackBufferOperation.COPY
            )
        }
    }

    @Test
    fun set_pad_light_failed_for_null_pad() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setPadLight(
                null,
                Color.AMBER,
                BackBufferOperation.NONE
            )
        }
    }

    @Test
    fun set_pad_light_failed_for_null_color() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setPadLight(
                Pad.at(Pad.X_MAX, Pad.Y_MAX),
                null,
                BackBufferOperation.NONE
            )
        }
    }

    @Test
    fun set_pad_light_failed_for_null_operation() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setPadLight(
                Pad.at(Pad.X_MAX, Pad.Y_MAX),
                Color.AMBER,
                null
            )
        }
    }

    /*
    ================================================================================
    reset
    ================================================================================
    */
    @Test
    fun reset() {
        launchpadClient.reset()
        verify(midiProtocolClient).reset()
    }

    @Test
    fun reset_exception() {
        doThrow(InvalidMidiDataException()).`when`(midiProtocolClient).reset()
        assertThrows<LaunchpadException> { launchpadClient.reset() }
        verify(midiProtocolClient).reset()
    }

    /*
    ================================================================================
    testLights
    ================================================================================
    */
    @Test
    fun testLights() {
        launchpadClient.testLights(LightIntensity.LOW)
        verify(midiProtocolClient).lightsOn(125)

        launchpadClient.testLights(LightIntensity.MEDIUM)
        verify(midiProtocolClient).lightsOn(126)

        launchpadClient.testLights(LightIntensity.HIGH)
        verify(midiProtocolClient).lightsOn(127)
    }

    @Test
    fun testLights_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).lightsOn(anyInt())

        assertThrows<LaunchpadException> { launchpadClient.testLights(LightIntensity.LOW) }
    }

    @Test
    fun test_lights_failed_because_of_null_input() {
        assertThrows<IllegalArgumentException> { launchpadClient.testLights(null) }
    }

    /*
    ================================================================================
    setLights
    ================================================================================
    */
    @Test
    fun setLights_null() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setLights(null, BackBufferOperation.NONE)
        }
    }

    @Test
    fun setLights_odd() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setLights(arrayOfNulls(1), BackBufferOperation.NONE)
        }
    }

    @Test
    fun setLights_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).notesOn(12, 12)

        assertThrows<LaunchpadException> {
            launchpadClient.setLights(arrayOf(Color.BLACK, Color.BLACK), BackBufferOperation.COPY)
        }
    }

    @Test
    fun setLights_COPY() {
        launchpadClient.setLights(arrayOf(Color.BLACK, Color.BLACK), BackBufferOperation.COPY)
        verify(midiProtocolClient).notesOn(12, 12)
    }

    @Test
    fun set_lights_failed_for_null_operation() {
        assertThrows<IllegalArgumentException> { launchpadClient.setLights(arrayOf(), null) }
    }

    /*
    ================================================================================
    scrollText
    ================================================================================
    */
    @Test
    fun scrollText_speedTooLow() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.scrollText("Hello", Color.BLACK,
                ScrollSpeed.of(ScrollSpeed.MIN_VALUE - 1),
                false,
                BackBufferOperation.COPY)
        }
    }

    @Test
    fun scrollText_speedTooHIgh() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.scrollText(
                "Hello",
                Color.BLACK,
                ScrollSpeed.of(ScrollSpeed.MAX_VALUE + 1),
                false,
                BackBufferOperation.COPY
            )
        }
    }

    @Test
    fun scrollText_nullColor() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.scrollText(
                "Hello",
                null,
                ScrollSpeed.SPEED_MIN,
                false,
                BackBufferOperation.COPY
            )
        }
    }

    @Test
    fun scrollText_COPY() {
        launchpadClient.scrollText(
            "Hello",
            Color.BLACK,
            ScrollSpeed.SPEED_MIN,
            false,
            BackBufferOperation.COPY
        )

        verify(midiProtocolClient).text("Hello", 12, 1, false)
    }

    @Test
    fun scrollText_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).text(anyString(), anyInt(), anyInt(), anyBoolean())

        assertThrows<LaunchpadException> {
            launchpadClient.scrollText(
                "Hello",
                Color.BLACK,
                ScrollSpeed.SPEED_MIN,
                false,
                BackBufferOperation.COPY
            )
        }
    }

    @Test
    fun set_scroll_text_failed_for_null_speed() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.scrollText(
                "text",
                Color.ORANGE,
                null,
                true,
                BackBufferOperation.NONE
            )
        }
    }

    @Test
    fun set_scroll_text_failed_for_null_operation() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.scrollText(
                "text",
                Color.ORANGE,
                ScrollSpeed.SPEED_MAX,
                true,
                null
            )
        }
    }

    companion object {
        private const val LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE = 104
        const val LAUNCHPAD_S_RAW_RIGHT_BUTTON_MIDI_CODE = 8
    }
}