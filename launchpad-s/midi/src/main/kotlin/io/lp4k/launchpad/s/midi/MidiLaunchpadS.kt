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

package io.lp4k.launchpad.s.midi

import io.lp4k.launchpad.api.LaunchpadClient
import io.lp4k.launchpad.api.LaunchpadListener
import io.lp4k.launchpad.s.midi.protocol.LaunchpadSMidiProtocolClient
import io.lp4k.launchpad.s.midi.protocol.LaunchpadSMidiProtocolListener
import io.lp4k.launchpad.s.midi.protocol.LaunchpadSMidiProtocolReceiver
import io.lp4k.midi.MidiDeviceConfiguration
import io.lp4k.midi.MidiLaunchpad

class MidiLaunchpadS(
    configuration: MidiDeviceConfiguration
) : MidiLaunchpad(configuration) {

    override val client: LaunchpadClient = MidiLaunchpadSClient(
        LaunchpadSMidiProtocolClient(this.receiver)
    )

    override fun setListener(listener: LaunchpadListener) {
        val midiProtocolListener = LaunchpadSMidiProtocolListener(listener)
        val midiReceiver = LaunchpadSMidiProtocolReceiver(midiProtocolListener)
        transmitter.receiver = midiReceiver
    }

}