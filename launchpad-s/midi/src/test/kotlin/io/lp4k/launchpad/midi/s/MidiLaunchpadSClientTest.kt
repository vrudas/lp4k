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
package io.lp4k.launchpad.midi.s

import io.lp4k.launchpad.api.BackBufferOperation
import io.lp4k.launchpad.api.Buffer
import io.lp4k.launchpad.api.LaunchpadClient
import io.lp4k.launchpad.api.LaunchpadException
import io.lp4k.launchpad.api.s.*
import io.lp4k.midi.protocol.MidiProtocolClient
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
class MidiLaunchpadSClientTest {

    private lateinit var launchpadClient: LaunchpadClient

    @Mock
    private lateinit var midiProtocolClient: MidiProtocolClient

    @BeforeEach
    fun init() {
        midiProtocolClient = Mockito.mock(MidiProtocolClient::class.java)
        launchpadClient = MidiLaunchpadSClient(midiProtocolClient)
    }

    /*
    ================================================================================
    setBrightness
    ================================================================================
    */

    @Test
    fun brightness_set_successful() {
        val expectedNumerator = 1
        val expectedDenominator = 18 - BrightnessLaunchS.BRIGHTNESS_MAX.brightnessLevel

        launchpadClient.setBrightness(BrightnessLaunchS.BRIGHTNESS_MAX)

        verify(midiProtocolClient).brightness(expectedNumerator, expectedDenominator)
    }

    @Test
    fun setBrightness_tooLow() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setBrightness(BrightnessLaunchS.of(BrightnessLaunchS.MIN_VALUE - 1))
        }
    }

    @Test
    fun setBrightness_tooHigh() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setBrightness(BrightnessLaunchS.of(BrightnessLaunchS.MAX_VALUE + 1))
        }
    }

    @Test
    fun setBrightness_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).brightness(anyInt(), anyInt())

        assertThrows<LaunchpadException> {
            launchpadClient.setBrightness(BrightnessLaunchS.BRIGHTNESS_MIN)
        }
    }

    /*
    ================================================================================
    setBuffers
    ================================================================================
    */
    @Test
    fun setBuffers() {
        launchpadClient.setBuffers(
            Buffer.BUFFER_0,
            Buffer.BUFFER_1,
            copyVisibleBufferToWriteBuffer = false,
            autoSwap = false
        )

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
                copyVisibleBufferToWriteBuffer = false,
                autoSwap = false
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
                ButtonLaunchS.UP,
                ColorLaunchS.BLACK,
                BackBufferOperation.COPY
            )
        }
    }

    @Test
    fun setButtonLight_topButton_NONE() {
        launchpadClient.setButtonLight(ButtonLaunchS.DOWN, ColorLaunchS.BLACK, BackBufferOperation.NONE)

        verify(midiProtocolClient)
            .buttonOn(
                LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE + ButtonLaunchS.DOWN.coordinate,
                0
            )
    }

    @Test
    fun setButtonLight_topButton_CLEAR() {
        launchpadClient.setButtonLight(ButtonLaunchS.UP, ColorLaunchS.BLACK, BackBufferOperation.CLEAR)
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 8)
    }

    @Test
    fun setButtonLight_topButton_COPY() {
        launchpadClient.setButtonLight(ButtonLaunchS.UP, ColorLaunchS.BLACK, BackBufferOperation.COPY)
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 12)
    }

    @Test
    fun setButtonLight_OORANGE_for_topButton_COPY() {
        launchpadClient.setButtonLight(ButtonLaunchS.UP, ColorLaunchS.ORANGE, BackBufferOperation.COPY)
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 47)
    }

    @Test
    fun setButtonLight_rightButton() {
        launchpadClient.setButtonLight(ButtonLaunchS.PAN, ColorLaunchS.BLACK, BackBufferOperation.COPY)
        verify(midiProtocolClient).noteOn(24, 12)
    }

    @Test
    fun setButtonLight_rightButton_COPY() {
        launchpadClient.setButtonLight(ButtonLaunchS.VOL, ColorLaunchS.BLACK, BackBufferOperation.COPY)
        verify(midiProtocolClient).noteOn(LAUNCHPAD_S_RAW_RIGHT_BUTTON_MIDI_CODE, 12)
    }

    @Test
    fun setButtonLight_XY_illegal_COPY() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setButtonLight(ButtonLaunchS.atTop(-1), ColorLaunchS.BLACK, BackBufferOperation.COPY)
        }
    }

    @Test
    fun setButtonLight_topButton_XY_COPY() {
        launchpadClient.setButtonLight(ButtonLaunchS.atTop(0), ColorLaunchS.BLACK, BackBufferOperation.COPY) // UP
        verify(midiProtocolClient).buttonOn(LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE, 12)
    }

    @Test
    fun setButtonLight_rightButton_XY_COPY() {
        launchpadClient.setButtonLight(ButtonLaunchS.atRight(0), ColorLaunchS.BLACK, BackBufferOperation.COPY) // VOL
        verify(midiProtocolClient).noteOn(8, 12)
    }

    /*
    ================================================================================
    setPadLight
    ================================================================================
    */
    @Test
    fun setPadLight_COPY() {
        launchpadClient.setPadLight(PadLaunchS.at(0, 0), ColorLaunchS.BLACK, BackBufferOperation.COPY)
        verify(midiProtocolClient).noteOn(0, 12)
    }

    @Test
    fun setPadLight_illegal() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.setPadLight(
                PadLaunchS.at(-1, -1),
                ColorLaunchS.BLACK,
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
                PadLaunchS.at(1, 1),
                ColorLaunchS.BLACK,
                BackBufferOperation.COPY
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
        launchpadClient.testLights(LightIntensityLaunchS.LOW)
        verify(midiProtocolClient).lightsOn(125)

        launchpadClient.testLights(LightIntensityLaunchS.MEDIUM)
        verify(midiProtocolClient).lightsOn(126)

        launchpadClient.testLights(LightIntensityLaunchS.HIGH)
        verify(midiProtocolClient).lightsOn(127)
    }

    @Test
    fun testLights_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).lightsOn(anyInt())

        assertThrows<LaunchpadException> { launchpadClient.testLights(LightIntensityLaunchS.LOW) }
    }

    /*
    ================================================================================
    setLights
    ================================================================================
    */

    @Test
    fun setLights_exception() {
        doThrow(InvalidMidiDataException())
            .`when`(midiProtocolClient).notesOn(12, 12)

        assertThrows<LaunchpadException> {
            launchpadClient.setLights(arrayOf(ColorLaunchS.BLACK, ColorLaunchS.BLACK), BackBufferOperation.COPY)
        }
    }

    @Test
    fun setLights_COPY() {
        launchpadClient.setLights(arrayOf(ColorLaunchS.BLACK, ColorLaunchS.BLACK), BackBufferOperation.COPY)
        verify(midiProtocolClient).notesOn(12, 12)
    }

    /*
    ================================================================================
    scrollText
    ================================================================================
    */
    @Test
    fun scrollText_speedTooLow() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.scrollText("Hello", ColorLaunchS.BLACK,
                ScrollSpeedLaunchS.of(ScrollSpeedLaunchS.MIN_VALUE - 1),
                false,
                BackBufferOperation.COPY)
        }
    }

    @Test
    fun scrollText_speedTooHIgh() {
        assertThrows<IllegalArgumentException> {
            launchpadClient.scrollText(
                "Hello",
                ColorLaunchS.BLACK,
                ScrollSpeedLaunchS.of(ScrollSpeedLaunchS.MAX_VALUE + 1),
                false,
                BackBufferOperation.COPY
            )
        }
    }

    @Test
    fun scrollText_COPY() {
        launchpadClient.scrollText(
            "Hello",
            ColorLaunchS.BLACK,
            ScrollSpeedLaunchS.SPEED_MIN,
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
                ColorLaunchS.BLACK,
                ScrollSpeedLaunchS.SPEED_MIN,
                false,
                BackBufferOperation.COPY
            )
        }
    }

    companion object {
        private const val LAUNCHPAD_S_RAW_TOP_BUTTON_MIDI_CODE = 104
        const val LAUNCHPAD_S_RAW_RIGHT_BUTTON_MIDI_CODE = 8
    }
}