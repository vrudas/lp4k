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
package io.lp4k.api

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BrightnessTest {

    @Test
    fun valueOf_for_brightness_min_level() {
        val brightness = Brightness.of(0)
        assertEquals(0, brightness.brightnessLevel)
    }

    @Test
    fun valueOf_for_brightness_max_level() {
        val brightness = Brightness.of(15)
        assertEquals(15, brightness.brightnessLevel)
    }

    @Test
    fun valueOf_tooLow() {
        assertThrows<IllegalArgumentException> { Brightness.of(Brightness.MIN_VALUE - 1) }
    }

    @Test
    fun valueOf_tooHigh() {
        assertThrows<IllegalArgumentException> { Brightness.of(Brightness.MAX_VALUE + 1) }
    }

    @Test
    fun call_of_more_method_returns_max_brightness() {
        val almostMaxBrightness = Brightness.of(Brightness.MAX_VALUE - 1)
        assertEquals(Brightness.BRIGHTNESS_MAX, almostMaxBrightness.more())
    }

    @Test
    fun call_of_less_method_returns_min_brightness() {
        val almostMaxBrightness = Brightness.of(Brightness.MIN_VALUE + 1)
        assertEquals(Brightness.BRIGHTNESS_MIN, almostMaxBrightness.less())
    }

    @Test
    fun not_equal() {
        assertNotEquals(Brightness.BRIGHTNESS_MAX, Brightness.BRIGHTNESS_MIN)
    }

    @Test
    fun not_equal_for_different_type() {
        assertNotEquals(Brightness.BRIGHTNESS_MAX, Brightness.MAX_VALUE)
    }

    @Test
    fun not_equal_for_null() {
        assertNotEquals(Brightness.BRIGHTNESS_MAX, null)
    }

    @Test
    fun hashcode_are_equal() {
        assertEquals(
            Brightness.MAX_VALUE,
            Brightness.of(15).hashCode()
        )
    }

    @Test
    fun more_return_same_max_brightness_value_for_already_max_value() {
        assertEquals(
            Brightness.BRIGHTNESS_MAX,
            Brightness.BRIGHTNESS_MAX.more()
        )
    }

    @Test
    fun less_return_same_min_brightness_value_for_already_min_value() {
        assertEquals(
            Brightness.BRIGHTNESS_MIN,
            Brightness.BRIGHTNESS_MIN.less()
        )
    }
}