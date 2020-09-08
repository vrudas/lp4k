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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PadTest {

    @Test
    fun at() {
        val pad = Pad.at(Pad.X_MIN, Pad.Y_MIN)
        assertEquals(Pad.X_MIN, pad.x)
        assertEquals(Pad.Y_MIN, pad.y)
    }

    @Test
    fun check_pad_coordinates_for_different_input_values() {
        val pad = Pad.at(1, 2)
        assertEquals(1, pad.x)
        assertEquals(2, pad.y)
    }

    @Test
    fun at_xTooLow() {
        assertThrows<IllegalArgumentException> { Pad.at(Pad.X_MIN - 1, Pad.Y_MIN) }
    }

    @Test
    fun at_xTooHigh() {
        assertThrows<IllegalArgumentException> { Pad.at(Pad.X_MAX + 1, Pad.Y_MIN) }
    }

    @Test
    fun at_yTooLow() {
        assertThrows<IllegalArgumentException> { Pad.at(Pad.X_MIN, Pad.Y_MIN - 1) }
    }

    @Test
    fun at_yTooHigh() {
        assertThrows<IllegalArgumentException> { Pad.at(Pad.X_MIN, Pad.Y_MAX + 1) }
    }

    @Test
    fun check_equals() {
        assertEquals(
            Pad.at(Pad.X_MIN, Pad.Y_MIN),
            Pad.at(Pad.X_MIN, Pad.Y_MIN)
        )
    }

    @Test
    fun not_equal() {
        assertNotEquals(
            Pad.at(Pad.X_MIN, Pad.Y_MIN),
            Pad.at(Pad.X_MAX, Pad.Y_MAX)
        )
    }

    @Test
    fun not_equal_for_different_type() {
        assertNotEquals(Pad.at(Pad.X_MAX, Pad.Y_MAX), Pad.X_MIN)
    }

    @Test
    fun not_equal_for_null() {
        assertNotEquals(Pad.at(Pad.X_MIN, Pad.Y_MIN), null)
    }

    @Test
    fun hashcode_is_equal_for_pads_with_same_coordinates() {
        val pads = setOf(
            Pad.at(Pad.X_MAX, Pad.Y_MAX),
            Pad.at(7, 7)
        )

        assertEquals(1, pads.size)
    }
}