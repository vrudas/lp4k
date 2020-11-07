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

import io.lp4k.launchpad.midi.protocol.MidiProtocolClient
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage

@ExtendWith(MockitoExtension::class)
class LaunchpadSMidiProtocolClientTest {

    @Mock
    private lateinit var receiver: Receiver

    private lateinit var midiProtocolClient: MidiProtocolClient

    @Captor
    private lateinit var shortMessage: ArgumentCaptor<ShortMessage>

    @Captor
    private lateinit var sysexMessage: ArgumentCaptor<SysexMessage>

    @BeforeEach
    fun init() {
        midiProtocolClient = LaunchpadSMidiProtocolClient(receiver)
    }

    /*
    ================================================================================
    reset
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testReset() {
        midiProtocolClient.reset()

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.CONTROL_CHANGE, 0, 0)
    }

    /*
    ================================================================================
    lightsOn
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testLightsOn() {
        midiProtocolClient.lightsOn(LIGHT_INTENSITY)

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.CONTROL_CHANGE, 0, LIGHT_INTENSITY)
    }

    /*
    ================================================================================
    noteOn
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testNoteOn() {
        midiProtocolClient.noteOn(NOTE_1_1, COLOR_RED)

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.NOTE_ON, NOTE_1_1, COLOR_RED)
    }

    /*
    ================================================================================
    noteOff
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testNoteOff() {
        midiProtocolClient.noteOff(NOTE_1_1)

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.NOTE_OFF, NOTE_1_1, COLOR_BLACK)
    }

    /*
    ================================================================================
    notesOn
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testNotesOn() {
        midiProtocolClient.notesOn(42, 42, 42, 42)

        verify(receiver, times(2))
            .send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.NOTE_ON, 42, 42)
        assertEquals(3, shortMessage.value.channel)
    }

    /*
    ================================================================================
    layout
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testLayout() {
        midiProtocolClient.layout(LAYOUT_XY)

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.CONTROL_CHANGE, 0, LAYOUT_XY)
    }

    /*
    ================================================================================
    buttonOn
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testButtonOn() {
        midiProtocolClient.buttonOn(BUTTON_UP, COLOR_RED)

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.CONTROL_CHANGE, BUTTON_UP, COLOR_RED)
    }

    /*
    ================================================================================
    brightness
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testBrightness_low() {
        midiProtocolClient.brightness(1, 3)

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.CONTROL_CHANGE, 30, 0)
    }

    @Test
    @Throws(Exception::class)
    fun testBrightness_high() {
        midiProtocolClient.brightness(9, 3)

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(shortMessage.value, ShortMessage.CONTROL_CHANGE, 31, 0)
    }

    /*
    ================================================================================
    text
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testText() {
        midiProtocolClient.text("Hello", COLOR_BLACK, 1, false)

        verify(receiver).send(sysexMessage.capture(), eq(-1L))
        checkSysexMessage(
            sysexMessage.value,
            byteArrayOf(0, 32, 41, 9, 0, 1, 72, 101, 108, 108, 111, 247.toByte())
        )
    }

    @Test
    @Throws(Exception::class)
    fun testText_loop() {
        midiProtocolClient.text("Hello", COLOR_BLACK, 1, true)

        verify(receiver).send(sysexMessage.capture(), eq(-1L))
        checkSysexMessage(
            sysexMessage.value,
            byteArrayOf(0, 32, 41, 9, 64, 1, 72, 101, 108, 108, 111, 247.toByte())
        )
    }

    /*
    ================================================================================
    doubleBufferMode
    ================================================================================
    */
    @Test
    @Throws(Exception::class)
    fun testDoubleBufferMode_0_1() {
        midiProtocolClient.doubleBufferMode(
            visibleBuffer = 0,
            writeBuffer = 1,
            copyVisibleBufferToWriteBuffer = false,
            autoSwap = false
        )

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(
            shortMessage.value,
            ShortMessage.CONTROL_CHANGE,
            data1 = 0,
            data2 = 36
        )
    }

    @Test
    @Throws(Exception::class)
    fun testDoubleBufferMode_1_0() {
        midiProtocolClient.doubleBufferMode(
            visibleBuffer = 1,
            writeBuffer = 0,
            copyVisibleBufferToWriteBuffer = false,
            autoSwap = false
        )

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(
            shortMessage.value,
            ShortMessage.CONTROL_CHANGE,
            data1 = 0,
            data2 = 33
        )
    }

    @Test
    @Throws(Exception::class)
    fun testDoubleBufferMode_COPY() {
        midiProtocolClient.doubleBufferMode(
            visibleBuffer = 0,
            writeBuffer = 0,
            copyVisibleBufferToWriteBuffer = true,
            autoSwap = false
        )

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(
            shortMessage.value,
            ShortMessage.CONTROL_CHANGE,
            data1 = 0,
            data2 = 32 + 16
        )
    }

    @Test
    @Throws(Exception::class)
    fun testDoubleBufferMode_LOOP() {
        midiProtocolClient.doubleBufferMode(
            visibleBuffer = 0,
            writeBuffer = 0,
            copyVisibleBufferToWriteBuffer = false,
            autoSwap = true
        )

        verify(receiver).send(shortMessage.capture(), eq(-1L))
        checkShortMessage(
            shortMessage.value,
            ShortMessage.CONTROL_CHANGE,
            data1 = 0,
            data2 = 32 + 8
        )
    }

    /*
    ================================================================================
    UTILS
    ================================================================================
    */
    private fun checkShortMessage(
        message: ShortMessage,
        command: Int,
        data1: Int,
        data2: Int
    ) {
        assertEquals(command, message.command)
        assertEquals(data1, message.data1)
        assertEquals(data2, message.data2)
    }

    private fun checkSysexMessage(message: SysexMessage, data: ByteArray) {
        assertArrayEquals(data, message.data)
    }

    companion object {
        const val LIGHT_INTENSITY = 125
        const val COLOR_RED = 3
        const val COLOR_BLACK = 0
        const val NOTE_1_1 = 17
        const val LAYOUT_XY = 1
        const val BUTTON_UP = 104
    }
}