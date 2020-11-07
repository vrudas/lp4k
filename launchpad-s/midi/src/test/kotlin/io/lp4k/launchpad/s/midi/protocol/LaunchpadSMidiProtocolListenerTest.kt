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

import io.lp4k.launchpad.api.LaunchpadListener
import io.lp4k.launchpad.s.api.ButtonLaunchS
import io.lp4k.launchpad.s.api.PadLaunchS
import io.lp4k.launchpad.midi.protocol.MidiProtocolListener
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class LaunchpadSMidiProtocolListenerTest {
    
    private lateinit var midiProtocolListener: MidiProtocolListener
    private lateinit var listener: LaunchpadListener

    @BeforeEach
    fun init() {
        listener = mock(LaunchpadListener::class.java)
        midiProtocolListener = LaunchpadSMidiProtocolListener(listener)
    }

    @Test
    fun onButtonOn_up() {
        midiProtocolListener.onButtonOn(BUTTON_UP, TIMESTAMP)
        verify(listener).onButtonPressed(ButtonLaunchS.UP, TIMESTAMP)
    }

    @Test
    fun onButtonOn_unknown() {
        assertThrows<IllegalArgumentException> {
            midiProtocolListener.onButtonOn(BUTTON_UNKNOWN, TIMESTAMP)
            verify(listener).onButtonPressed(ButtonLaunchS.UP, TIMESTAMP)
        }
    }

    @Test
    fun onButtonOff_up() {
        midiProtocolListener.onButtonOff(BUTTON_UP, TIMESTAMP)
        verify(listener).onButtonReleased(ButtonLaunchS.UP, TIMESTAMP)
    }

    @Test
    fun onButtonOff_unknown() {
        assertThrows<IllegalArgumentException> {
            midiProtocolListener.onButtonOff(BUTTON_UNKNOWN, TIMESTAMP)
            verify(listener).onButtonReleased(ButtonLaunchS.UP, TIMESTAMP)
        }
    }

    @Test
    fun onNoteOn_vol() {
        midiProtocolListener.onNoteOn(NOTE_VOL, TIMESTAMP)
        verify(listener).onButtonPressed(ButtonLaunchS.VOL, TIMESTAMP)
    }

    @Test
    fun onNoteOff_vol() {
        midiProtocolListener.onNoteOff(NOTE_VOL, TIMESTAMP)
        verify(listener).onButtonReleased(ButtonLaunchS.VOL, TIMESTAMP)
    }

    @Test
    fun onNoteOn_pad00() {
        midiProtocolListener.onNoteOn(NOTE_PAD00, TIMESTAMP)
        verify(listener).onPadPressed(PadLaunchS.at(0, 0), TIMESTAMP)
    }

    @Test
    fun onNoteOff_pad00() {
        midiProtocolListener.onNoteOff(NOTE_PAD00, TIMESTAMP)
        verify(listener).onPadReleased(PadLaunchS.at(0, 0), TIMESTAMP)
    }

    @Test
    fun onNoteOn_unknown() {
        assertThrows<IllegalArgumentException> {
            midiProtocolListener.onNoteOn(NOTE_UNKNOWN, TIMESTAMP)
            verify(listener).onPadPressed(PadLaunchS.at(0, 0), TIMESTAMP)
        }
    }

    @Test
    fun onNoteOff_unknown() {
        assertThrows<IllegalArgumentException> {
            midiProtocolListener.onNoteOff(NOTE_UNKNOWN, TIMESTAMP)
            verify(listener).onPadReleased(PadLaunchS.at(0, 0), TIMESTAMP)
        }
    }

    @Test
    fun onTextScrolled() {
        midiProtocolListener.onTextScrolled(TIMESTAMP)
        verify(listener).onTextScrolled(TIMESTAMP)
    }

    companion object {
        private const val TIMESTAMP: Long = -1
        private const val BUTTON_UP = 104
        private const val BUTTON_UNKNOWN = 100
        private const val NOTE_VOL = 8
        private const val NOTE_PAD00 = 0
        private const val NOTE_UNKNOWN = -1
    }
}