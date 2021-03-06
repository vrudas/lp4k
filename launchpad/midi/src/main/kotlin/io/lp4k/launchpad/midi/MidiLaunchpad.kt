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

import io.lp4k.launchpad.api.Launchpad
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.Receiver
import javax.sound.midi.Transmitter

/**
 * Represents a physical MIDI Launchpad device.
 *
 * @param configuration The MIDI configuration to use. Must not be null.
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
abstract class MidiLaunchpad(
    /**
     * The MIDI configuration holder.
     * @throws MidiUnavailableException If the input or output channels cannot be opened.
     */
    private val configuration: MidiDeviceConfiguration
) : Launchpad {

    /**
     * The Launchpad's input channel (Device -> LP4J).
     */
    protected val receiver: Receiver

    /**
     * The Launchpad's output channel (LP4J -> Device).
     */
    protected val transmitter: Transmitter

    /**
     * Indicates that the output channel has been successfully opened.
     */
    private var openedOutputDevice = false

    /**
     * Indicates that the input channel has been successfully opened.
     */
    private var openedInputDevice = false

    init {
        this.receiver = openReceiver()
        this.transmitter = openTransmitter()
    }

    private fun openReceiver(): Receiver {
        val outputDevice = configuration.outputDevice
        if (!outputDevice.isOpen) {
            outputDevice.open()
        }

        openedOutputDevice = true

        return outputDevice.receiver
    }

    private fun openTransmitter(): Transmitter {
        val inputDevice = configuration.inputDevice
        if (!inputDevice.isOpen) {
            inputDevice.open()
        }

        openedInputDevice = true

        return inputDevice.transmitter
    }

    override fun close() {
        if (openedOutputDevice) {
            val outputDevice = configuration.outputDevice
            if (outputDevice.isOpen) {
                outputDevice.close()
            }
        }
        if (openedInputDevice) {
            val inputDevice = configuration.inputDevice
            if (inputDevice.isOpen) {
                inputDevice.close()
            }
        }
    }

}
