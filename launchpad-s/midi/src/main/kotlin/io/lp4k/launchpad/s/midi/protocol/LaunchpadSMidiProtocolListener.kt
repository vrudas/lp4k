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

import io.lp4k.launchpad.api.LaunchpadListener
import io.lp4k.launchpad.s.api.ButtonLaunchS
import io.lp4k.launchpad.s.api.PadLaunchS
import io.lp4k.launchpad.midi.protocol.MidiProtocolListener


/**
 * Parses low-level messages and notifies a high-level [net.thecodersbreakfast.lp4j.api.LaunchpadListener].
 *
 * @param listener The high-level LaunchpadListener to notify.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
class LaunchpadSMidiProtocolListener(
    private val listener: LaunchpadListener
) : MidiProtocolListener {

    override fun onNoteOn(note: Int, timestamp: Long) {
        val x = note % 16
        val y = note / 16
        if (x >= 8) {
            val button = ButtonLaunchS.atRight(y)
            listener.onButtonPressed(button, timestamp)
        } else {
            require((x < 0 || y < 0 || y > 7).not()) {
                "Invalid pad coordinates : ($x,$y). Acceptable values on either axis are in range [0..7]."
            }

            listener.onPadPressed(PadLaunchS.at(x, y), timestamp)
        }
    }

    override fun onNoteOff(note: Int, timestamp: Long) {
        val x = note % 16
        val y = note / 16
        if (x >= 8) {
            val button = ButtonLaunchS.atRight(y)
            listener.onButtonReleased(button, timestamp)
        } else {
            require((x < 0 || y < 0 || y > 7).not()) {
                "Invalid pad coordinates : ($x,$y). Acceptable values on either axis are in range [0..7]."
            }

            listener.onPadReleased(PadLaunchS.at(x, y), timestamp)
        }
    }

    override fun onButtonOn(buttonNote: Int, timestamp: Long) {
        val midiButton = buttonNote - 104
        val button = ButtonLaunchS.atTop(midiButton)
        listener.onButtonPressed(button, timestamp)
    }

    override fun onButtonOff(buttonNote: Int, timestamp: Long) {
        val midiButton = buttonNote - 104
        val button = ButtonLaunchS.atTop(midiButton)
        listener.onButtonReleased(button, timestamp)
    }

    override fun onTextScrolled(timestamp: Long) {
        listener.onTextScrolled(timestamp)
    }

}