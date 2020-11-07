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

import io.lp4k.launchpad.midi.DeviceNotFoundException.Companion.inputDeviceNotFound
import io.lp4k.launchpad.midi.DeviceNotFoundException.Companion.outputDeviceNotFound
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException

/**
 * Configuration for MIDI I/O.
 *
 * <p>Requires both an inbound and outbound MidiDevices, because data is sent from (pad events) and to (lights, etc.)
 * the Launchpad.
 *
 * <p>In most cases, it should suffice to call the {@link MidiDeviceConfiguration#autodetect()} factory method to get
 * a working configuration.
 *
 * @param inputDevice  The inbound Midi channel.
 * @param outputDevice The outbound midi channel.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
class MidiDeviceConfiguration(
    /**
     * Inbound communication channel.
     */
    val inputDevice: MidiDevice,
    /**
     * Outbound communication channel.
     */
    val outputDevice: MidiDevice
) {
    companion object {
        /**
         * Device signature of a Launchpad S, used for auto detection.
         */
        const val DEVICE_SIGNATURE: String = "Launchpad S"

        /**
         * Tries to auto-detect a MIDI Launchpad based on its device signature.
         *
         * @return The auto-detected configuration.
         * @throws MidiUnavailableException If an error occurs during device probing.
         */
        @Throws(MidiUnavailableException::class)
        fun autodetect(): MidiDeviceConfiguration {
            return autodetect(DEVICE_SIGNATURE)
        }

        /**
         * Tries to detect a valid inbound communication channel, based on a known device signature
         * (see [MidiDeviceConfiguration.DEVICE_SIGNATURE]).
         *
         * @return A valid outbound communication channel if was found.
         * @throws MidiUnavailableException if the requested device is not available due to resource restrictions
         */
        @Throws(MidiUnavailableException::class)
        fun autodetectInputDevice(): MidiDevice {
            return autodetectInputDevice(DEVICE_SIGNATURE)
        }

        /**
         * Tries to detect a valid outbound communication channel, based on a known device signature
         * (see [MidiDeviceConfiguration.DEVICE_SIGNATURE]).
         *
         * @return A valid outbound communication channel if was found.
         * @throws MidiUnavailableException if the requested device is not available due to resource restrictions
         * @throws DeviceNotFoundException If device was not found.
         */
        @Throws(MidiUnavailableException::class)
        fun autodetectOutputDevice(): MidiDevice {
            return autodetectOutputDevice(DEVICE_SIGNATURE)
        }

        /**
         * Tries to auto-detect a MIDI Launchpad based on its device signature.
         *
         * @param deviceSignature The MIDI device signature used for device detection. Must be not null or not blank.
         * @return The auto-detected configuration.
         * @throws MidiUnavailableException If an error occurs during device probing.
         * @throws IllegalArgumentException If deviceSignature is null or empty.
         * @throws DeviceNotFoundException If device was not found.
         */
        @Throws(MidiUnavailableException::class)
        fun autodetect(deviceSignature: String): MidiDeviceConfiguration {
            if (deviceSignature.isBlank()) {
                throw IllegalArgumentException("Not valid deviceSignature = '$deviceSignature'")
            }

            val inputDevice = autodetectInputDevice(deviceSignature)
            val outputDevice = autodetectOutputDevice(deviceSignature)
            return MidiDeviceConfiguration(inputDevice, outputDevice)
        }

        /**
         * Tries to detect a valid inbound communication channel, based on a device signature
         *
         * @param deviceSignature The MIDI device signature used for device detection.
         * @return A valid outbound communication channel if was found.
         * @throws MidiUnavailableException if the requested device is not available due to resource restrictions
         * @throws DeviceNotFoundException If device was not found.
         */
        @Throws(MidiUnavailableException::class)
        fun autodetectInputDevice(deviceSignature: String): MidiDevice {
            return MidiSystem.getMidiDeviceInfo()
                .asSequence()
                .filter { info -> info.description.contains(deviceSignature) || info.name.contains(deviceSignature) }
                .map { info -> MidiSystem.getMidiDevice(info) }
                .find { device -> device.maxTransmitters == -1 }
                ?: inputDeviceNotFound(deviceSignature)
        }

        /**
         * Tries to detect a valid outbound communication channel, based on a device signature
         *
         * @param deviceSignature The MIDI device signature used for device detection.
         * @return A valid outbound communication channel if was found.
         * @throws MidiUnavailableException if the requested device is not available due to resource restrictions
         * @throws DeviceNotFoundException If device was not found.
         */
        @Throws(MidiUnavailableException::class)
        fun autodetectOutputDevice(deviceSignature: String): MidiDevice {
            return MidiSystem.getMidiDeviceInfo()
                .asSequence()
                .filter { info -> info.description.contains(deviceSignature) || info.name.contains(deviceSignature) }
                .map { info -> MidiSystem.getMidiDevice(info) }
                .find { device -> device.maxReceivers == -1 }
                ?: outputDeviceNotFound(deviceSignature)
        }
    }
}
