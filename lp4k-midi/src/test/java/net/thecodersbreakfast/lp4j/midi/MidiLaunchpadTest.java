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

package net.thecodersbreakfast.lp4j.midi;

import io.lp4k.midi.FakeMidiDevice;
import io.lp4k.midi.FakeReceiver;
import io.lp4k.midi.FakeTransmitter;
import net.thecodersbreakfast.lp4j.api.Launchpad;
import net.thecodersbreakfast.lp4j.api.LaunchpadException;
import net.thecodersbreakfast.lp4j.api.LaunchpadListenerAdapter;
import net.thecodersbreakfast.lp4j.midi.protocol.DefaultMidiProtocolReceiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MidiLaunchpadTest {

    private Launchpad launchpad;

    @BeforeEach
    void setUp() throws MidiUnavailableException {
        MidiDeviceConfiguration deviceConfiguration = mockEmptyDeviceConfiguration();

        launchpad = new MidiLaunchpad(deviceConfiguration);
    }

    private MidiDeviceConfiguration mockEmptyDeviceConfiguration() {
        return mockDeviceConfiguration(
            null,
            null
        );
    }

    private MidiDeviceConfiguration mockDeviceConfiguration(
        MidiDevice inputDevice,
        MidiDevice outputDevice
    ) {
        var deviceConfiguration = mock(MidiDeviceConfiguration.class);

        when(deviceConfiguration.getInputDevice()).thenReturn(inputDevice);

        when(deviceConfiguration.getOutputDevice()).thenReturn(outputDevice);

        return deviceConfiguration;
    }

    private FakeMidiDevice mockOutputDevice(boolean isOpen) throws MidiUnavailableException {
        var receiver = mock(FakeReceiver.class);

        var outputDevice = mock(FakeMidiDevice.class);

        when(outputDevice.isOpen()).thenReturn(isOpen);

        when(outputDevice.getReceiver()).thenReturn(receiver);

        return outputDevice;
    }

    private FakeMidiDevice mockInputDevice(boolean isOpen) throws MidiUnavailableException {
        var transmitter = mock(FakeTransmitter.class);

        var inputDevice = mock(FakeMidiDevice.class);

        when(inputDevice.isOpen()).thenReturn(isOpen);
        when(inputDevice.getTransmitter()).thenReturn(transmitter);

        return inputDevice;
    }

    @Test
    void failed_to_create_midi_launchpad_because_of_null_midi_configuration() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new MidiLaunchpad(null)
        );
    }

    @Test
    void get_client_failed_because_of_null_receiver() throws MidiUnavailableException {
        var launchpad = new MidiLaunchpad(
            mockEmptyDeviceConfiguration()
        );

        assertThrows(
            LaunchpadException.class,
            launchpad::getClient
        );
    }

    @Test
    void set_listener_failed_because_of_null_transmitter() throws MidiUnavailableException {
        var launchpad = new MidiLaunchpad(
            mockEmptyDeviceConfiguration()
        );

        assertThrows(
            LaunchpadException.class,
            () -> launchpad.setListener(
                new LaunchpadListenerAdapter() {
                }
            )
        );
    }

    @Test
    void midi_launchpad_created_with_already_opened_receiver() throws MidiUnavailableException {
        var outputDevice = mockOutputDevice(true);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                null,
                outputDevice
            )
        );

        verify(outputDevice, never()).open();
    }

    @Test
    void midi_launchpad_created_with_not_opened_receiver() throws MidiUnavailableException {
        var outputDevice = mockOutputDevice(false);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                null,
                outputDevice
            )
        );

        verify(outputDevice).open();
    }

    @Test
    void midi_launchpad_created_with_already_opened_transmitter() throws MidiUnavailableException {
        var inputDevice = mockInputDevice(true);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                null
            )
        );

        verify(inputDevice, never()).open();
    }

    @Test
    void midi_launchpad_created_with_not_opened_transmitter() throws MidiUnavailableException {
        var inputDevice = mockInputDevice(false);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                null
            )
        );

        verify(inputDevice).open();
    }

    @Test
    void launchpad_client_was_returned() throws MidiUnavailableException {
        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                mockInputDevice(false),
                mockOutputDevice(false)
            )
        );

        assertNotNull(launchpad.getClient());
    }

    @Test
    void listener_set_was_successful() throws MidiUnavailableException {
        var transmitter = mock(FakeTransmitter.class);

        var inputDevice = mockInputDevice(true);
        when(inputDevice.getTransmitter()).thenReturn(transmitter);

        var deviceConfiguration = mockDeviceConfiguration(
            inputDevice,
            mockOutputDevice(true)
        );

        launchpad = new MidiLaunchpad(deviceConfiguration);

        launchpad.setListener(new LaunchpadListenerAdapter() {
        });

        verify(transmitter).setReceiver(any(DefaultMidiProtocolReceiver.class));
    }

    @Test
    void output_device_was_not_closed_because_device_is_not_open() throws MidiUnavailableException, IOException {
        var outputDevice = mockOutputDevice(false);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                null,
                outputDevice
            )
        );

        launchpad.close();

        verify(outputDevice, never()).close();
    }

    @Test
    void output_device_was_closed() throws MidiUnavailableException, IOException {
        var outputDevice = mockOutputDevice(true);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                null,
                outputDevice
            )
        );

        launchpad.close();

        verify(outputDevice).close();
    }

    @Test
    void input_device_was_not_closed_because_device_is_not_open() throws MidiUnavailableException, IOException {
        var inputDevice = mockInputDevice(false);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                null
            )
        );

        launchpad.close();

        verify(inputDevice, never()).close();
    }

    @Test
    void input_device_was_closed() throws MidiUnavailableException, IOException {
        var inputDevice = mockInputDevice(true);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                null
            )
        );

        launchpad.close();

        verify(inputDevice).close();
    }

    @Test
    void io_devices_was_not_closed_because_devices_are_not_open() throws MidiUnavailableException, IOException {
        var inputDevice = mockInputDevice(false);
        var outputDevice = mockOutputDevice(false);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                outputDevice
            )
        );

        launchpad.close();

        verify(inputDevice, never()).close();
        verify(outputDevice, never()).close();
    }

    @Test
    void io_devices_was_closed() throws MidiUnavailableException, IOException {
        var inputDevice = mockInputDevice(true);
        var outputDevice = mockOutputDevice(true);

        launchpad = new MidiLaunchpad(
            mockDeviceConfiguration(
                inputDevice,
                outputDevice
            )
        );

        launchpad.close();

        verify(inputDevice).close();
        verify(outputDevice).close();
    }

}