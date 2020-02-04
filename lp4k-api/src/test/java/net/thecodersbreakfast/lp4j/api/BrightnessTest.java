/*
 * Copyright 2020 Vasya Rudas
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

package net.thecodersbreakfast.lp4j.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BrightnessTest {

    @Test
    public void valueOf() {
        Brightness brightness = Brightness.of(Brightness.MIN_VALUE);
        assertNotNull(brightness);
        assertEquals(Brightness.MIN_VALUE, brightness.getBrightness());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void valueOf_tooLow() {
        assertThrows(IllegalArgumentException.class, () -> Brightness.of(Brightness.MIN_VALUE - 1));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void valueOf_tooHigh() {
        assertThrows(IllegalArgumentException.class, () -> Brightness.of(Brightness.MAX_VALUE + 1));
    }

    @Test
    void call_of_more_method_returns_max_brightness() {
        var almostMaxBrightness = Brightness.of(Brightness.MAX_VALUE - 1);

        assertEquals(Brightness.BRIGHTNESS_MAX, almostMaxBrightness.more());
    }

    @Test
    void call_of_less_method_returns_min_brightness() {
        var almostMaxBrightness = Brightness.of(Brightness.MIN_VALUE + 1);

        assertEquals(Brightness.BRIGHTNESS_MIN, almostMaxBrightness.less());
    }

    @Test
    @SuppressWarnings("java:S3415")
    void not_equal() {
        assertNotEquals(Brightness.BRIGHTNESS_MAX, Brightness.BRIGHTNESS_MIN);
    }

    @Test
    @SuppressWarnings("java:S3415")
    void not_equal_for_different_type() {
        assertNotEquals(Brightness.BRIGHTNESS_MAX, Brightness.MAX_VALUE);
    }

    @Test
    @SuppressWarnings("java:S3415")
    void not_equal_for_null() {
        assertNotEquals(Brightness.BRIGHTNESS_MAX, null);
    }

    @Test
    void hashcode_are_equal() {
        assertEquals(
            Brightness.BRIGHTNESS_MIN.hashCode(),
            Brightness.of(Brightness.MIN_VALUE).hashCode()
        );
    }
}
