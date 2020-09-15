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

import io.lp4k.launchpad.api.LaunchpadException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.sound.midi.*

internal class DefaultMidiProtocolReceiverTest {

    private lateinit var receiver: Receiver
    private lateinit var listener: MidiProtocolListener

    @BeforeEach
    fun init() {
        listener = mock(MidiProtocolListener::class.java)
        receiver = DefaultMidiProtocolReceiver(listener)
    }

    @Test
    fun send_sysexMessage() {
        val message: MidiMessage = SysexMessage()
        assertThrows<LaunchpadException> {
            receiver.send(message, TIMESTAMP)
        }
    }

    @Test
    fun send_metaMessage() {
        val message: MidiMessage = MetaMessage()
        assertThrows<LaunchpadException> {
            receiver.send(message, TIMESTAMP)
        }
    }

    @Test
    fun send_shortMessage_unknown() {
        val message = ShortMessage()
        message.setMessage(ShortMessage.STOP, 0, 0)

        assertThrows<LaunchpadException> {
            receiver.send(message, TIMESTAMP)
        }
    }

    @Test
    fun send_noteOn_pressed() {
        val message = ShortMessage()
        message.setMessage(ShortMessage.NOTE_ON, 42, VELOCITY_PRESSED)

        receiver.send(message, TIMESTAMP)

        verify(listener).onNoteOn(42, TIMESTAMP)
    }

    @Test
    fun send_noteOn_released() {
        val message = ShortMessage()
        message.setMessage(ShortMessage.NOTE_ON, 42, VELOCITY_RELEASED)

        receiver.send(message, TIMESTAMP)

        verify(listener).onNoteOff(42, TIMESTAMP)
    }

    @Test
    fun send_controlChange_pressed() {
        val message = ShortMessage()
        message.setMessage(ShortMessage.CONTROL_CHANGE, 42, VELOCITY_PRESSED)

        receiver.send(message, TIMESTAMP)

        verify(listener).onButtonOn(42, TIMESTAMP)
    }

    @Test
    fun send_controlChange_released() {
        val message = ShortMessage()
        message.setMessage(ShortMessage.CONTROL_CHANGE, 42, VELOCITY_RELEASED)

        receiver.send(message, TIMESTAMP)

        verify(listener).onButtonOff(42, TIMESTAMP)
    }

    @Test
    fun send_controlChange_textScrolled() {
        val message = ShortMessage()
        message.setMessage(ShortMessage.CONTROL_CHANGE, 0, VELOCITY_TEXT_SCROLLED)

        receiver.send(message, TIMESTAMP)

        verify(listener).onTextScrolled(TIMESTAMP)
    }

    @Test
    fun close() {
        assertDoesNotThrow { receiver.close() }
    }

    companion object {
        private const val VELOCITY_PRESSED = 127
        private const val VELOCITY_RELEASED = 0
        private const val VELOCITY_TEXT_SCROLLED = 3
        private const val TIMESTAMP: Long = -1L
    }
}