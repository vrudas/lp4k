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
package io.lp4k.launchpad.api.s

import io.lp4k.launchpad.api.s.ScrollSpeedLaunchS.Companion.of
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ScrollSpeedLaunchSTest {

    @Test
    fun valueOf() {
        val scrollSpeed = of(ScrollSpeedLaunchS.MIN_VALUE)
        assertEquals(ScrollSpeedLaunchS.MIN_VALUE, scrollSpeed.speedValue)
    }

    @Test
    fun valueOf_tooLow() {
        assertThrows<IllegalArgumentException> { of(ScrollSpeedLaunchS.MIN_VALUE - 1) }
    }

    @Test
    fun valueOf_tooHigh() {
        assertThrows<IllegalArgumentException> { of(ScrollSpeedLaunchS.MAX_VALUE + 1) }
    }

    @Test
    fun check_equals() {
        assertEquals(ScrollSpeedLaunchS.SPEED_MAX, of(7))
    }

    @Test
    fun not_equal() {
        assertNotEquals(ScrollSpeedLaunchS.SPEED_MAX, ScrollSpeedLaunchS.SPEED_MIN)
    }

    @Test
    fun not_equal_for_different_type() {
        assertNotEquals(ScrollSpeedLaunchS.SPEED_MAX, ScrollSpeedLaunchS.MAX_VALUE)
    }

    @Test
    fun not_equal_for_null() {
        assertNotEquals(ScrollSpeedLaunchS.SPEED_MAX, null)
    }

    @Test
    fun hashcode_are_equal() {
        assertEquals(
            ScrollSpeedLaunchS.MAX_VALUE,
            of(7).hashCode()
        )
    }
}