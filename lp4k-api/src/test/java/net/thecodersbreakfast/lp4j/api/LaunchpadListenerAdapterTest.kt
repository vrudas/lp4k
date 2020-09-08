/*
 * Copyright 2020 Vasyl Rudas
 * Copyright 2015 Olivier Croisier (thecodersbreakfast.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.thecodersbreakfast.lp4j.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LaunchpadListenerAdapterTest {
    private lateinit var adapter: LaunchpadListenerAdapter
 
    @BeforeEach
    fun init() {
        adapter = object : LaunchpadListenerAdapter() {}
    }

    @Test
    fun testOnPadPressed() {
        assertDoesNotThrow { adapter.onPadPressed(Pad.at(0, 0), -1L) }
    }

    @Test
    fun testOnPadReleased() {
        assertDoesNotThrow { adapter.onPadReleased(Pad.at(0, 0), -1L) }
    }

    @Test
    fun testOnButtonPressed() {
        assertDoesNotThrow { adapter.onButtonPressed(Button.UP, -1L) }
    }

    @Test
    fun testOnButtonReleased() {
        assertDoesNotThrow { adapter.onButtonReleased(Button.UP, -1L) }
    }

    @Test
    fun testOnTextScrolled() {
        assertDoesNotThrow { adapter.onTextScrolled(-1L) }
    }
}