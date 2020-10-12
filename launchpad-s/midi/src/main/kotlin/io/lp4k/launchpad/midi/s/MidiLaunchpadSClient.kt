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

import io.lp4k.launchpad.api.*
import io.lp4k.launchpad.api.s.ColorLaunchS
import io.lp4k.launchpad.api.s.LightIntensityLaunchS
import io.lp4k.midi.MidiLaunchpadClient
import io.lp4k.midi.protocol.MidiProtocolClient
import javax.sound.midi.InvalidMidiDataException

class MidiLaunchpadSClient(
    midiProtocolClient: MidiProtocolClient
) : MidiLaunchpadClient(midiProtocolClient) {

    override fun testLights(intensity: LightIntensity) {
        val value: Int = when (intensity) {
            LightIntensityLaunchS.LOW -> 125
            LightIntensityLaunchS.MEDIUM -> 126
            LightIntensityLaunchS.HIGH -> 127
            else -> throw IllegalArgumentException("Unknown intensity value : $intensity")
        }
        try {
            midiProtocolClient.lightsOn(value)
        } catch (e: InvalidMidiDataException) {
            throw LaunchpadException(e)
        }
    }

    override fun setLights(colors: Array<Color>, operation: BackBufferOperation) {
        val nbColors = colors.size

        require(nbColors.isEven()) {
            "The number of colors for a batch update must be even."
        }

        val rawColors = IntArray(nbColors)
        for (i in 0 until nbColors) {
            rawColors[i] = toRawColor(colors[i], operation).toInt()
        }
        try {
            midiProtocolClient.notesOn(*rawColors)
        } catch (e: InvalidMidiDataException) {
            throw LaunchpadException(e)
        }
    }

    private fun Int.isEven(): Boolean = this % 2 == 0

    override fun setPadLight(pad: Pad, color: Color, operation: BackBufferOperation) {
        val rawCoords = toRawCoords(pad.x, pad.y)
        val rawColor = toRawColor(color, operation).toInt()
        try {
            midiProtocolClient.noteOn(rawCoords, rawColor)
        } catch (e: InvalidMidiDataException) {
            throw LaunchpadException(e)
        }
    }

    override fun setButtonLight(button: Button, color: Color, operation: BackBufferOperation) {
        try {
            val rawColor = toRawColor(color, operation).toInt()
            if (button.isTopButton) {
                val rawCoords = 104 + button.coordinate
                midiProtocolClient.buttonOn(rawCoords, rawColor)
            } else {
                val rawCoords = toRawCoords(8, button.coordinate)
                midiProtocolClient.noteOn(rawCoords, rawColor)
            }
        } catch (e: InvalidMidiDataException) {
            throw LaunchpadException(e)
        }
    }

    override fun setBrightness(brightness: Brightness) {
        val level = brightness.brightnessLevel
        try {
            midiProtocolClient.brightness(1, 18 - level)
        } catch (e: InvalidMidiDataException) {
            throw LaunchpadException(e)
        }
    }

    override fun scrollText(
        text: String,
        color: Color,
        speed: ScrollSpeed,
        loop: Boolean,
        operation: BackBufferOperation
    ) {
        val rawColor = toRawColor(color, operation).toInt()
        try {
            midiProtocolClient.text(text, rawColor, speed.speedValue, loop)
        } catch (e: InvalidMidiDataException) {
            throw LaunchpadException(e)
        }
    }

    /*
    ================================================================================
    Utils
    ================================================================================
    */

    /**
     * Converts a Color into its Launchpad-specific low-level representation
     *
     * @param color     The Color to convert. Must not be null.
     * @param operation What to do on the backbuffer. Must not be null.
     * @return A binary representation of the color and how it should be applied to the Launchpad's buffers.
     */
    private fun toRawColor(color: Color, operation: BackBufferOperation): Byte {
        color as ColorLaunchS

        val flags: Int = when (operation) {
            BackBufferOperation.CLEAR -> 8
            BackBufferOperation.COPY -> 12
            BackBufferOperation.NONE -> 0
            else -> 0
        }
        return (flags + color.redIntensity + 16 * color.greenIntensity).toByte()
    }

    /**
     * Converts an X-Y coordinates into its Launchpad-specific low-level representation.
     *
     * @param x The X coordinate.
     * @param y The Y corrdinate.
     * @return The low-level representation of those coordinates.
     */
    private fun toRawCoords(x: Int, y: Int): Int {
        return x + 16 * y
    }

}