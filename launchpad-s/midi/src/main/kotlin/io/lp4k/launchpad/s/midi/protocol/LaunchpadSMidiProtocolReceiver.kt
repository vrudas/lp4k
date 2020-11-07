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
package io.lp4k.launchpad.s.midi.protocol

import io.lp4k.launchpad.api.LaunchpadException
import io.lp4k.launchpad.midi.protocol.MidiProtocolListener
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage

/**
 * A MIDI Receiver, to which the Launchpad sends low-level commands.
 * Those commands are parsed and transmitted (still in a close-to-the-metal format) to a
 * [MidiProtocolListener] for further processing.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
class LaunchpadSMidiProtocolReceiver(
    /**
     * The MidiProtocolListener to to notify when commands are received.
     */
    private val midiProtocolListener: MidiProtocolListener
) : Receiver {

    /**
     * {@inheritDoc}
     *
     * THIS METHOD SHOULD ONLY BE CALLED BY THE LAUNCHPAD DEVICE.
     */
    override fun send(message: MidiMessage, timestamp: Long) {
        if (message is ShortMessage) {
            handleShortMessage(message, timestamp)
        } else {
            throw LaunchpadException("Unknown event : $message")
        }
    }

    /**
     * Parses and routes MIDI short messages to adequate sub-handlers.
     *
     * @param message   The incoming message.
     * @param timestamp When the message arrived.
     */
    private fun handleShortMessage(message: ShortMessage, timestamp: Long) {
        val status = message.status
        val note = message.data1
        val velocity = message.data2

        when (status) {
            ShortMessage.NOTE_ON -> {
                handleNoteOnMessage(note, velocity, timestamp)
            }
            ShortMessage.CONTROL_CHANGE -> {
                handleControlChangeMessage(note, velocity, timestamp)
            }
            else -> {
                throw LaunchpadException("Unknown event : $message")
            }
        }
    }

    /**
     * Parses "note on" messages and notifies the to the higher-level `midiProtocolListener`
     *
     * @param note      The activated note.
     * @param velocity  The note velocity.
     * @param timestamp When the note was activated.
     */
    private fun handleNoteOnMessage(note: Int, velocity: Int, timestamp: Long) {
        if (velocity == 0) {
            midiProtocolListener.onNoteOff(note, timestamp)
        } else {
            midiProtocolListener.onNoteOn(note, timestamp)
        }
    }

    /**
     * Parses "control" messages and notifies the to the higher-level `midiProtocolListener`
     *
     * @param note      The activated note.
     * @param velocity  The note velocity.
     * @param timestamp When the note was activated.
     */
    private fun handleControlChangeMessage(note: Int, velocity: Int, timestamp: Long) {
        if (note == 0 && velocity == 3) {
            midiProtocolListener.onTextScrolled(timestamp)
        } else if (velocity == 0) {
            midiProtocolListener.onButtonOff(note, timestamp)
        } else {
            midiProtocolListener.onButtonOn(note, timestamp)
        }
    }

    override fun close() = Unit

}