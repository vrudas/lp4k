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
package io.lp4k.midi

import io.lp4k.midi.protocol.DefaultMidiProtocolReceiver
import net.thecodersbreakfast.lp4j.api.Launchpad
import net.thecodersbreakfast.lp4j.api.LaunchpadListenerAdapter
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import javax.sound.midi.MidiDevice

@ExtendWith(MockitoExtension::class)
internal class MidiLaunchpadTest {

    private lateinit var launchpad: Launchpad

    private fun mockDeviceConfiguration(
        inputDevice: MidiDevice,
        outputDevice: MidiDevice
    ): MidiDeviceConfiguration {
        val deviceConfiguration = mock(MidiDeviceConfiguration::class.java)

        `when`(deviceConfiguration.inputDevice).thenReturn(inputDevice)
        `when`(deviceConfiguration.outputDevice).thenReturn(outputDevice)

        return deviceConfiguration
    }

    private fun mockOutputDevice(isOpen: Boolean): FakeMidiDevice {
        val receiver = mock(FakeReceiver::class.java)
        val outputDevice = mock(FakeMidiDevice::class.java)

        `when`(outputDevice.isOpen).thenReturn(isOpen)
        `when`(outputDevice.receiver).thenReturn(receiver)

        return outputDevice
    }

    private fun mockInputDevice(isOpen: Boolean): FakeMidiDevice {
        val transmitter = mock(FakeTransmitter::class.java)
        val inputDevice = mock(FakeMidiDevice::class.java)

        `when`(inputDevice.isOpen).thenReturn(isOpen)
        `when`(inputDevice.transmitter).thenReturn(transmitter)

        return inputDevice
    }

    @Test
    fun midi_launchpad_created_with_already_opened_receiver() {
        val outputDevice = mockOutputDevice(true)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                mockInputDevice(true),
                outputDevice
            )
        )

        verify(outputDevice, never()).open()
    }

    @Test
    fun midi_launchpad_created_with_not_opened_receiver() {
        val outputDevice = mockOutputDevice(false)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                mockInputDevice(false),
                outputDevice
            )
        )

        verify(outputDevice).open()
    }

    @Test
    fun midi_launchpad_created_with_already_opened_transmitter() {
        val inputDevice = mockInputDevice(true)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                mockOutputDevice(false)
            )
        )

        verify(inputDevice, never()).open()
    }

    @Test
    fun midi_launchpad_created_with_not_opened_transmitter() {
        val inputDevice = mockInputDevice(false)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                mockOutputDevice(false)
            )
        )

        verify(inputDevice).open()
    }

    @Test
    fun launchpad_client_was_returned() {
        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                mockInputDevice(false),
                mockOutputDevice(false)
            )
        )

        assertNotNull(launchpad.client)
    }

    @Test
    fun listener_set_was_successful() {
        val transmitter = mock(FakeTransmitter::class.java)
        val inputDevice = mockInputDevice(true)

        `when`(inputDevice.transmitter).thenReturn(transmitter)

        val deviceConfiguration = mockDeviceConfiguration(
            inputDevice,
            mockOutputDevice(true)
        )

        launchpad = MidiLaunchpad(deviceConfiguration)
        launchpad.setListener(object : LaunchpadListenerAdapter() {})

        verify(transmitter).receiver = any(DefaultMidiProtocolReceiver::class.java)
    }

    @Test
    fun output_device_was_not_closed_because_device_is_not_open() {
        val outputDevice = mockOutputDevice(false)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                mockInputDevice(false),
                outputDevice
            )
        )

        launchpad.close()

        verify(outputDevice, never()).close()
    }

    @Test
    fun output_device_was_closed() {
        val outputDevice = mockOutputDevice(true)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                mockInputDevice(true),
                outputDevice
            )
        )

        launchpad.close()

        verify(outputDevice).close()
    }

    @Test
    fun input_device_was_not_closed_because_device_is_not_open() {
        val inputDevice = mockInputDevice(false)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                mockOutputDevice(false)
            )
        )

        launchpad.close()

        verify(inputDevice, never()).close()
    }

    @Test
    fun input_device_was_closed() {
        val inputDevice = mockInputDevice(true)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                mockOutputDevice(false)
            )
        )

        launchpad.close()

        verify(inputDevice).close()
    }

    @Test
    fun io_devices_was_not_closed_because_devices_are_not_open() {
        val inputDevice = mockInputDevice(false)
        val outputDevice = mockOutputDevice(false)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                outputDevice
            )
        )

        launchpad.close()

        verify(inputDevice, never()).close()
        verify(outputDevice, never()).close()
    }

    @Test
    fun io_devices_was_closed() {
        val inputDevice = mockInputDevice(true)
        val outputDevice = mockOutputDevice(true)

        launchpad = MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                outputDevice
            )
        )

        launchpad.close()

        verify(inputDevice).close()
        verify(outputDevice).close()
    }
}