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

package net.thecodersbreakfast.lp4j.emulator.output;

/**
 * Types of events sent to the emulator, on the browser side
 */
public enum OutputEventType {
    /**
     * Reset
     */
    RST,
    /**
     * Padlight
     */
    PADLGT,
    /**
     * Button light
     */
    BTNLGT,
    /**
     * Test
     */
    TST,
    /**
     * Brightness
     */
    BRGHT,
    /**
     * SetBuffers
     */
    BUF
}

