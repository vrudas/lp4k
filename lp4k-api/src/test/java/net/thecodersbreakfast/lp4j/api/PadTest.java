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

package net.thecodersbreakfast.lp4j.api;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class PadTest {

    @Test
    public void at() {
        Pad pad = Pad.at(Pad.X_MIN, Pad.Y_MIN);
        assertNotNull(pad);
        assertEquals(Pad.X_MIN, pad.getX());
        assertEquals(Pad.Y_MIN, pad.getY());
    }

    @Test
    public void check_pad_coordinates_for_different_input_values() {
        Pad pad = Pad.at(1, 2);
        assertNotNull(pad);
        assertEquals(1, pad.getX());
        assertEquals(2, pad.getY());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void at_xTooLow() {
        assertThrows(IllegalArgumentException.class, () -> Pad.at(Pad.X_MIN - 1, Pad.Y_MIN));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void at_xTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> Pad.at(Pad.X_MAX + 1, Pad.Y_MIN));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void at_yTooLow() {
        assertThrows(IllegalArgumentException.class, () -> Pad.at(Pad.X_MIN, Pad.Y_MIN - 1));
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void at_yTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> Pad.at(Pad.X_MIN, Pad.Y_MAX + 1));
    }

    @Test
    public void tostring() {
        Pad pad = Pad.at(Pad.X_MIN, Pad.Y_MIN);
        assertEquals("Pad[0,0]", pad.toString());
    }

    @Test
    @SuppressWarnings("java:S3415")
    void check_equals() {
        assertEquals(
            Pad.at(Pad.X_MIN, Pad.Y_MIN),
            Pad.at(Pad.X_MIN, Pad.Y_MIN)
        );
    }

    @Test
    @SuppressWarnings("java:S3415")
    void not_equal() {
        assertNotEquals(
            Pad.at(Pad.X_MIN, Pad.Y_MIN),
            Pad.at(Pad.X_MAX, Pad.Y_MAX)
        );
    }

    @Test
    @SuppressWarnings("java:S3415")
    void not_equal_for_different_type() {
        assertNotEquals(Pad.at(Pad.X_MAX, Pad.Y_MAX), Pad.X_MIN);
    }

    @Test
    @SuppressWarnings("java:S3415")
    void not_equal_for_null() {
        assertNotEquals(Pad.at(Pad.X_MIN, Pad.Y_MIN), null);
    }

    @Test
    void hashcode_is_equal_for_pads_with_same_coordinates() {
        Pad pad = Pad.at(Pad.X_MIN, Pad.Y_MIN);

        Set<Pad> pads = new HashSet<>();
        pads.add(pad);
        pads.add(pad);

        assertEquals(1, pads.size());
    }
}
