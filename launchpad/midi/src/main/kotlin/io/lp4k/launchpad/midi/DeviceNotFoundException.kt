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

class DeviceNotFoundException private constructor(message: String) : RuntimeException(message) {
    companion object {
        fun inputDeviceNotFound(deviceSignature: String): Nothing {
            throw DeviceNotFoundException("Input device not found for signature: '$deviceSignature'")
        }

        fun outputDeviceNotFound(deviceSignature: String): Nothing {
            throw DeviceNotFoundException("OOutput device not found for signature: '$deviceSignature'")
        }
    }
}