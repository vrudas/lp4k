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
package io.lp4k.launchpad.s.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ColorLaunchSTest {

    @Test
    fun valueOf() {
        val color = ColorLaunchS.of(ColorLaunchS.MIN_INTENSITY, ColorLaunchS.MIN_INTENSITY)

        assertEquals(ColorLaunchS.BLACK, color)
        assertEquals(ColorLaunchS.MIN_INTENSITY, color.redIntensity)
        assertEquals(ColorLaunchS.MIN_INTENSITY, color.greenIntensity)

        val colorMax = ColorLaunchS.of(ColorLaunchS.MAX_INTENSITY, ColorLaunchS.MAX_INTENSITY)

        assertEquals(ColorLaunchS.AMBER, colorMax)
        assertEquals(ColorLaunchS.MAX_INTENSITY, colorMax.redIntensity)
        assertEquals(ColorLaunchS.MAX_INTENSITY, colorMax.greenIntensity)
    }

    @Test
    fun valueOf_redTooLow() {
        assertThrows<IllegalArgumentException> {
            ColorLaunchS.of(ColorLaunchS.MIN_INTENSITY - 1, ColorLaunchS.MIN_INTENSITY)
        }
    }

    @Test
    fun valueOf_redTooHigh() {
        assertThrows<IllegalArgumentException> {
            ColorLaunchS.of(ColorLaunchS.MAX_INTENSITY + 1, ColorLaunchS.MIN_INTENSITY)
        }
    }

    @Test
    fun valueOf_greenTooLow() {
        assertThrows<IllegalArgumentException> {
            ColorLaunchS.of(ColorLaunchS.MIN_INTENSITY, ColorLaunchS.MIN_INTENSITY - 1)
        }
    }

    @Test
    fun valueOf_greenTooHigh() {
        assertThrows<IllegalArgumentException> {
            ColorLaunchS.of(ColorLaunchS.MIN_INTENSITY, ColorLaunchS.MAX_INTENSITY + 1)
        }
    }

    @Test
    fun check_equals() {
        assertEquals(ColorLaunchS.RED, ColorLaunchS.RED)
    }

    @Test
    fun not_equal() {
        assertNotEquals(ColorLaunchS.GREEN, ColorLaunchS.ORANGE)
    }

    @Test
    fun not_equal_for_different_type() {
        assertNotEquals(ColorLaunchS.YELLOW, ColorLaunchS.MIN_INTENSITY)
    }

    @Test
    fun hashcode_is_equal_for_same_colors() {
        val colors = setOf(
            ColorLaunchS.AMBER,
            ColorLaunchS.of(3, 3)
        )

        assertEquals(1, colors.size)
    }
}