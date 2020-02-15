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

package net.thecodersbreakfast.lp4j.midi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({MockitoExtension.class, })
public class MidiDeviceConfigurationTest {

    @Mock
    private MidiDevice inputDevice;

    @Mock
    private MidiDevice outputDevice;

    private MidiDeviceConfiguration configuration;

    @BeforeEach
    public void init() {
        configuration = new MidiDeviceConfiguration(inputDevice, outputDevice);
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
    void auto_detection_not_found_launchpad_midi_input_device() throws MidiUnavailableException {
        assertNull(MidiDeviceConfiguration.autodetectInputDevice());
    }

    @Test
    void auto_detection_not_found_launchpad_midi_output_device() throws MidiUnavailableException {
        assertNull(MidiDeviceConfiguration.autodetectOutputDevice());
    }

    @Test
    void auto_detection_not_found_launchpad_midi_input_and_output_device() throws MidiUnavailableException {
        MidiDeviceConfiguration deviceConfiguration = MidiDeviceConfiguration.autodetect();

        MidiDevice inputDevice = deviceConfiguration.getInputDevice();
        MidiDevice outputDevice = deviceConfiguration.getOutputDevice();

        assertNull(inputDevice);
        assertNull(outputDevice);
    }

}
