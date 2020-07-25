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
package io.lp4k.midi.protocol

import javax.sound.midi.InvalidMidiDataException

/**
 * A low-level client used to send raw MIDI commands to a physical Launchpad device.
 *
 * Please note that some that the Launchpad considers some of the (round) buttons as (square) pads, whereas some
 * other buttons are treated as, well, buttons. This explains some oddities in the method signatures.
 *
 * For the full specification and details about the meaning of each command and their acceptable parameter values,
 * please refer to Novation's online documentation.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
interface MidiProtocolClient {
    /**
     * Sends a "reset" command.
     *
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun reset()

    /**
     * Sends a "lights on" command, lightning all the lights at once. Useful to test the LEDs.
     *
     * @param intensity The desired light intensity.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun lightsOn(intensity: Int)

    /**
     * Sends a "note on" command, to light up the LED under a pad or button.
     *
     * @param note  The "note" identifying the target pad or button.
     * @param color The color to display.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun noteOn(note: Int, color: Int)

    /**
     * Sends a "note off" command, to turn off the LED under a pad or button.
     *
     * @param note The "note" identifying the target pad or button.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun noteOff(note: Int)

    /**
     * A batch version of the "light on" command. Multiple lights can be set at once, starting from the upper-left one.
     *
     * @param colors The colors to display.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun notesOn(vararg colors: Int)

    /**
     * Toggles between a X-Y layout and "note-oriented" one.
     *
     * @param mode The layout to switch to.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun layout(mode: Int)

    /**
     * Sends a "button on" command, to light up the LED under a button.
     *
     * @param button The button to light up.
     * @param color  The desired color.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun buttonOn(button: Int, color: Int)

    /**
     * Sets the overall brightness of the LEDs, expressed as a ratio between a numerator and a denominator.
     *
     * @param numerator   The numerator.
     * @param denominator The denominator.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun brightness(numerator: Int, denominator: Int)

    /**
     * Scrolls the given text across the board, using an embedded 8x8 dot matrix font.
     *
     * @param text  The text to display.
     * @param color The text color.
     * @param speed The scrolling speed.
     * @param loop  Whether to display the text once, or to loop until a new "text" command is emitted.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun text(
        text: String?,
        color: Int,
        speed: Int,
        loop: Boolean
    )

    /**
     * Sets which buffer is written to, and which one is currently displayed (can be the same).
     * The "autoswap" parameter allows a "blinking" effect, where the Launchpad keeps swapping the visible and
     * non-visible buffers until autoswapping is turned off again.
     *
     * @param visibleBuffer                  The buffer to set as the visible buffer.
     * @param writeBuffer                    The buffer to set as the write buffer.
     * @param copyVisibleBufferToWriteBuffer Whether the visible buffer should be copied to the back buffer during the
     * process.
     * @param autoSwap                       Whether to trigger the "blinking" effect. Set to `false` to disable blinking.
     * @throws InvalidMidiDataException If a MIDI communication error occurs.
     */
    @Throws(InvalidMidiDataException::class)
    fun doubleBufferMode(
        visibleBuffer: Int,
        writeBuffer: Int,
        copyVisibleBufferToWriteBuffer: Boolean,
        autoSwap: Boolean
    )
}