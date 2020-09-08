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
package io.lp4k.api

/**
 * The Launchpad handles two buffers internally. It is up to the user to select which one is written to, and which one
 * is currently displayed (can be the same).
 *
 *
 * In addition of the current write buffer, most operations can (depending on a [ ] parameter) impact a "backbuffer", defined as "the buffer that is
 * not the current write buffer".
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
enum class Buffer {
    /** The first buffer  */
    BUFFER_0 {
        override fun other(): Buffer = BUFFER_1
    },

    /** The second buffer  */
    BUFFER_1 {
        override fun other(): Buffer = BUFFER_0
    };

    /**
     * Returns the other buffer ([Buffer.BUFFER_1] if the current buffer is [Buffer.BUFFER_0], and
     * vice-versa).
     *
     * @return The other buffer
     */
    abstract fun other(): Buffer
}