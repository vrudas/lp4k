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

import io.lp4k.launchpad.api.Buffer
import io.lp4k.launchpad.api.LaunchpadClient
import io.lp4k.launchpad.api.LaunchpadException
import io.lp4k.launchpad.midi.protocol.MidiProtocolClient
import javax.sound.midi.InvalidMidiDataException

/**
 * A client to communicate with a MIDI Launchpad device.
 *
 * This class serves as an adapter between the high-level LP4J API and the low-level MIDI communication layer
 *
 * @param midiProtocolClient Low-level MIDI client to communicate with the Launchpad.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
abstract class MidiLaunchpadClient(
    protected val midiProtocolClient: MidiProtocolClient
) : LaunchpadClient {

    /*
    ================================================================================
    Launchpad API
    ================================================================================
    */

    override fun reset() {
        try {
            midiProtocolClient.reset()
        } catch (e: InvalidMidiDataException) {
            throw LaunchpadException(e)
        }
    }

    override fun setBuffers(
        visibleBuffer: Buffer,
        writeBuffer: Buffer,
        copyVisibleBufferToWriteBuffer: Boolean,
        autoSwap: Boolean
    ) {
        try {
            midiProtocolClient.doubleBufferMode(
                getBufferValue(visibleBuffer),
                getBufferValue(writeBuffer),
                copyVisibleBufferToWriteBuffer,
                autoSwap
            )
        } catch (e: InvalidMidiDataException) {
            throw LaunchpadException(e)
        }
    }

    /**
     * Converts an abstract Buffer into its Launchpad-specific low-level representation.
     *
     * @param buffer The buffer to convert. Must not be null.
     * @return The buffer's low-level representation.
     */
    private fun getBufferValue(buffer: Buffer): Int {
        return if (buffer === Buffer.BUFFER_0) 0 else 1
    }

}