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
package net.thecodersbreakfast.lp4j.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ColorTest {

    @Test
    fun valueOf() {
        val color = Color.of(Color.MIN_INTENSITY, Color.MIN_INTENSITY)

        assertEquals(Color.BLACK, color)
        assertEquals(Color.MIN_INTENSITY, color.redIntensity)
        assertEquals(Color.MIN_INTENSITY, color.greenIntensity)

        val colorMax = Color.of(Color.MAX_INTENSITY, Color.MAX_INTENSITY)

        assertEquals(Color.AMBER, colorMax)
        assertEquals(Color.MAX_INTENSITY, colorMax.redIntensity)
        assertEquals(Color.MAX_INTENSITY, colorMax.greenIntensity)
    }

    @Test
    fun valueOf_redTooLow() {
        assertThrows<IllegalArgumentException> {
            Color.of(Color.MIN_INTENSITY - 1, Color.MIN_INTENSITY)
        }
    }

    @Test
    fun valueOf_redTooHigh() {
        assertThrows<IllegalArgumentException> {
            Color.of(Color.MAX_INTENSITY + 1, Color.MIN_INTENSITY)
        }
    }

    @Test
    fun valueOf_greenTooLow() {
        assertThrows<IllegalArgumentException> {
            Color.of(Color.MIN_INTENSITY, Color.MIN_INTENSITY - 1)
        }
    }

    @Test
    fun valueOf_greenTooHigh() {
        assertThrows<IllegalArgumentException> {
            Color.of(Color.MIN_INTENSITY, Color.MAX_INTENSITY + 1)
        }
    }

    @Test
    fun check_equals() {
        assertEquals(Color.RED, Color.RED)
    }

    @Test
    fun not_equal() {
        assertNotEquals(Color.GREEN, Color.ORANGE)
    }

    @Test
    fun not_equal_for_different_type() {
        assertNotEquals(Color.YELLOW, Color.MIN_INTENSITY)
    }

    @Test
    fun hashcode_is_equal_for_same_colors() {
        val colors = setOf(
            Color.AMBER,
            Color.of(3, 3)
        )

        assertEquals(1, colors.size)
    }
}