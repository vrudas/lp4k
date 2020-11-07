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
package io.lp4k.launchpad.midi

import io.lp4k.launchpad.midi.MidiDeviceConfiguration.Companion.autodetect
import io.lp4k.launchpad.midi.MidiDeviceConfiguration.Companion.autodetectInputDevice
import io.lp4k.launchpad.midi.MidiDeviceConfiguration.Companion.autodetectOutputDevice
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import javax.sound.midi.MidiDevice

@ExtendWith(MockitoExtension::class)
class MidiDeviceConfigurationTest {

    @Mock
    private lateinit var inputDevice: MidiDevice

    @Mock
    private lateinit var outputDevice: MidiDevice

    private lateinit var configuration: MidiDeviceConfiguration

    @BeforeEach
    fun init() {
        configuration = MidiDeviceConfiguration(inputDevice, outputDevice)
    }

    private fun assertMidiInputDevice(inputDevice: MidiDevice) {
        val expectedMaxTransmittersValue = -1

        assertEquals(
            expectedMaxTransmittersValue,
            inputDevice.maxTransmitters
        )
    }

    private fun assertMidiOutputDevice(outputDevice: MidiDevice) {
        val expectedMaxReceiversValue = -1

        assertEquals(
            expectedMaxReceiversValue,
            outputDevice.maxReceivers
        )
    }

    @Test
    fun testGetInputDevice() {
        val device = configuration.inputDevice
        assertEquals(inputDevice, device)
    }

    @Test
    fun testGetOutputDevice() {
        val device = configuration.outputDevice
        assertEquals(outputDevice, device)
    }

    @Test
    fun auto_detection_not_found_launchpad_midi_input_device() {
        assertThrows<DeviceNotFoundException> { autodetectInputDevice() }
    }

    @Test
    fun auto_detection_not_found_launchpad_midi_output_device() {
        assertThrows<DeviceNotFoundException> { autodetectOutputDevice() }
    }

    @Test
    fun auto_detection_failed_because_not_found_device() {
        assertThrows<DeviceNotFoundException> { autodetect() }
    }

    @Test
    fun auto_detection_found_devices() {
        assertDoesNotThrow<MidiDeviceConfiguration> {
            autodetect(JDK_MIDI_DEVICE_DESCRIPTION)
        }
    }

    @Test
    fun auto_detection_failed_because_of_empty_device_signature() {
        assertThrows<IllegalArgumentException> { autodetect("") }
    }

    @Test
    fun auto_detection_failed_because_of_blank_device_signature() {
        assertThrows<IllegalArgumentException> { autodetect(" ") }
    }

    @Test
    fun detect_by_name_software_midi_input_device_provided_from_jdk() {
        val inputDevice = autodetectInputDevice(JDK_MIDI_DEVICE_NAME)
        assertMidiInputDevice(inputDevice)
    }

    @Test
    fun detect_by_description_software_midi_input_device_provided_from_jdk() {
        val inputDevice = autodetectInputDevice(JDK_MIDI_DEVICE_DESCRIPTION)
        assertMidiInputDevice(inputDevice)
    }

    @Test
    fun detect_by_name_software_midi_output_device_provided_from_jdk() {
        val outputDevice = autodetectOutputDevice(JDK_MIDI_DEVICE_NAME)
        assertMidiOutputDevice(outputDevice)
    }

    @Test
    fun detect_by_description_software_midi_output_device_provided_from_jdk() {
        val outputDevice = autodetectOutputDevice(JDK_MIDI_DEVICE_DESCRIPTION)
        assertMidiOutputDevice(outputDevice)
    }

    companion object {
        const val JDK_MIDI_DEVICE_NAME = "Real Time Sequencer"
        const val JDK_MIDI_DEVICE_DESCRIPTION = "Software sequencer"
    }
}