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

package io.lp4k.midi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class,})
public class MidiDeviceConfigurationTest {

    public static final String JDK_MIDI_DEVICE_NAME = "Real Time Sequencer";
    public static final String JDK_MIDI_DEVICE_DESCRIPTION = "Software sequencer";

    @Mock
    private MidiDevice inputDevice;

    @Mock
    private MidiDevice outputDevice;

    private MidiDeviceConfiguration configuration;

    @BeforeEach
    public void init() {
        configuration = new MidiDeviceConfiguration(inputDevice, outputDevice);
    }

    private void assertMidiInputDevice(MidiDevice inputDevice) {
        int expectedMaxTransmittersValue = -1;

        assertNotNull(inputDevice);
        assertEquals(
            expectedMaxTransmittersValue,
            inputDevice.getMaxTransmitters()
        );
    }

    private void assertMidiOutputDevice(MidiDevice outputDevice) {
        int expectedMaxReceiversValue = -1;

        assertNotNull(outputDevice);
        assertEquals(
            expectedMaxReceiversValue,
            outputDevice.getMaxReceivers()
        );
    }

    @Test
    public void testGetInputDevice() {
        MidiDevice device = configuration.getInputDevice();
        assertEquals(inputDevice, device);
    }

    @Test
    public void testGetOutputDevice() {
        MidiDevice device = configuration.getOutputDevice();
        assertEquals(outputDevice, device);
    }

    @Test
    void auto_detection_not_found_launchpad_midi_input_device() {
        assertThrows(
            DeviceNotFoundException.class,
            MidiDeviceConfiguration.Companion::autodetectInputDevice
        );
    }

    @Test
    void auto_detection_not_found_launchpad_midi_output_device() {
        assertThrows(
            DeviceNotFoundException.class,
            MidiDeviceConfiguration.Companion::autodetectOutputDevice
        );
    }

    @Test
    void auto_detection_failed_because_not_found_device() {
        assertThrows(
            DeviceNotFoundException.class,
            MidiDeviceConfiguration.Companion::autodetect
        );
    }

    @Test
    void auto_detection_found_devices() {
        MidiDeviceConfiguration midiDeviceConfiguration = Assertions.assertDoesNotThrow(
            () -> MidiDeviceConfiguration.Companion.autodetect(JDK_MIDI_DEVICE_DESCRIPTION)
        );

        assertNotNull(midiDeviceConfiguration);
    }

    @Test
    void auto_detection_failed_because_of_null_device_signature() {
        assertThrows(
            IllegalArgumentException.class,
            () -> MidiDeviceConfiguration.Companion.autodetect(null)
        );
    }

    @Test
    void auto_detection_failed_because_of_empty_device_signature() {
        assertThrows(
            IllegalArgumentException.class,
            () -> MidiDeviceConfiguration.Companion.autodetect("")
        );
    }

    @Test
    void auto_detection_failed_because_of_blank_device_signature() {
        assertThrows(
            IllegalArgumentException.class,
            () -> MidiDeviceConfiguration.Companion.autodetect(" ")
        );
    }

    @Test
    void detect_by_name_software_midi_input_device_provided_from_jdk() throws MidiUnavailableException {
        MidiDevice inputDevice = MidiDeviceConfiguration.Companion.autodetectInputDevice(JDK_MIDI_DEVICE_NAME);

        assertMidiInputDevice(inputDevice);
    }

    @Test
    void detect_by_description_software_midi_input_device_provided_from_jdk() throws MidiUnavailableException {
        MidiDevice inputDevice = MidiDeviceConfiguration.Companion.autodetectInputDevice(JDK_MIDI_DEVICE_DESCRIPTION);

        assertMidiInputDevice(inputDevice);
    }

    @Test
    void detect_by_name_software_midi_output_device_provided_from_jdk() throws MidiUnavailableException {
        MidiDevice outputDevice = MidiDeviceConfiguration.Companion.autodetectOutputDevice(JDK_MIDI_DEVICE_NAME);

        assertMidiOutputDevice(outputDevice);
    }

    @Test
    void detect_by_description_software_midi_output_device_provided_from_jdk() throws MidiUnavailableException {
        MidiDevice outputDevice = MidiDeviceConfiguration.Companion.autodetectOutputDevice(JDK_MIDI_DEVICE_DESCRIPTION);

        assertMidiOutputDevice(outputDevice);
    }

}
