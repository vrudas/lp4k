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

import java.nio.charset.StandardCharsets
import javax.sound.midi.*

/**
 * Default implementation of a  [MidiProtocolClient].
 *
 * @param receiver The Launchpad's Receiver, to which commands are sent.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
class DefaultMidiProtocolClient(

    private val receiver: Receiver
) : MidiProtocolClient {

    // ================================================================================
    // Low-level MIDI output API
    // ================================================================================

    @Throws(InvalidMidiDataException::class)
    override fun reset() {
        sendShortMessage(ShortMessage.CONTROL_CHANGE, 0, 0)
    }

    @Throws(InvalidMidiDataException::class)
    override fun lightsOn(intensity: Int) {
        sendShortMessage(ShortMessage.CONTROL_CHANGE, 0, intensity)
    }

    @Throws(InvalidMidiDataException::class)
    override fun noteOn(note: Int, color: Int) {
        sendShortMessage(ShortMessage.NOTE_ON, note, color)
    }

    @Throws(InvalidMidiDataException::class)
    override fun noteOff(note: Int) {
        sendShortMessage(ShortMessage.NOTE_OFF, note, 0)
    }

    @Throws(InvalidMidiDataException::class)
    override fun notesOn(vararg colors: Int) {
        val nbMessages = colors.size / 2
        for (i in 0 until nbMessages) {
            sendShortMessage(ShortMessage.NOTE_ON, 3, colors[i * 2], colors[i * 2 + 1])
        }
    }

    @Throws(InvalidMidiDataException::class)
    override fun layout(mode: Int) {
        sendShortMessage(ShortMessage.CONTROL_CHANGE, 0, mode)
    }

    @Throws(InvalidMidiDataException::class)
    override fun buttonOn(button: Int, color: Int) {
        sendShortMessage(ShortMessage.CONTROL_CHANGE, button, color)
    }

    @Throws(InvalidMidiDataException::class)
    override fun brightness(numerator: Int, denominator: Int) {
        if (numerator < 9) {
            sendShortMessage(ShortMessage.CONTROL_CHANGE, 30, 16 * (numerator - 1) + (denominator - 3))
        } else {
            sendShortMessage(ShortMessage.CONTROL_CHANGE, 31, 16 * (numerator - 9) + (denominator - 3))
        }
    }

    @Throws(InvalidMidiDataException::class)
    override fun text(text: String?, color: Int, speed: Int, loop: Boolean) {
        var midiColor = color

        if (loop) {
            midiColor += 64
        }

        val header = byteArrayOf(240.toByte(), 0, 32, 41, 9, midiColor.toByte())
        val chars = text?.toByteArray(StandardCharsets.US_ASCII) ?: byteArrayOf()
        val message = ByteArray(chars.size + 8)

        System.arraycopy(header, 0, message, 0, header.size)
        message[header.size] = speed.toByte()
        System.arraycopy(chars, 0, message, header.size + 1, chars.size)
        message[message.size - 1] = 247.toByte()

        sendSysExMessage(message)
    }

    @Throws(InvalidMidiDataException::class)
    override fun doubleBufferMode(
        visibleBuffer: Int,
        writeBuffer: Int,
        copyVisibleBufferToWriteBuffer: Boolean,
        autoSwap: Boolean
    ) {
        var mode = 32 + 4 * writeBuffer + visibleBuffer

        if (copyVisibleBufferToWriteBuffer) {
            mode = mode or 16
        }

        if (autoSwap) {
            mode = mode or 8
        }

        sendShortMessage(ShortMessage.CONTROL_CHANGE, 0, mode)
    }

    // ================================================================================
    // Utils
    // ================================================================================

    @Throws(InvalidMidiDataException::class)
    private fun sendShortMessage(command: Int, controller: Int, data: Int) {
        val message = ShortMessage()
        message.setMessage(command, controller, data)
        send(message)
    }

    @Throws(InvalidMidiDataException::class)
    private fun sendShortMessage(command: Int, channel: Int, controller: Int, data: Int) {
        val message = ShortMessage()
        message.setMessage(command, channel, controller, data)
        send(message)
    }

    @Throws(InvalidMidiDataException::class)
    private fun sendSysExMessage(data: ByteArray) {
        val message = SysexMessage()
        message.setMessage(data, data.size)
        send(message)
    }

    private fun send(message: MidiMessage) {
        receiver.send(message, -1)
    }

}