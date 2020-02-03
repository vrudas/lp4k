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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
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
    public void testGetInputDevice() throws Exception {
        MidiDevice device = configuration.getInputDevice();
        assertEquals(inputDevice, device);
    }

    @Test
    public void testGetOutputDevice() throws Exception {
        MidiDevice device = configuration.getOutputDevice();
        assertEquals(outputDevice, device);
    }
}
