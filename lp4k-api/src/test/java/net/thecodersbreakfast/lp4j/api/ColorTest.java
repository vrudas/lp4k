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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ColorTest {

    @Test
    public void valueOf() {
        Color color = Color.of(Color.MIN_INTENSITY, Color.MIN_INTENSITY);
        assertEquals(Color.BLACK, color);
        assertEquals(Color.MIN_INTENSITY, color.getRed());
        assertEquals(Color.MIN_INTENSITY, color.getGreen());
        Color colorMax = Color.of(Color.MAX_INTENSITY, Color.MAX_INTENSITY);
        assertEquals(Color.AMBER, colorMax);
        assertEquals(Color.MAX_INTENSITY, colorMax.getRed());
        assertEquals(Color.MAX_INTENSITY, colorMax.getGreen());
    }

    @Test
    public void valueOf_redTooLow() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Color.of(Color.MIN_INTENSITY - 1, Color.MIN_INTENSITY)
        );
    }

    @Test
    public void valueOf_redTooHigh() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Color.of(Color.MAX_INTENSITY + 1, Color.MIN_INTENSITY)
        );
    }

    @Test
    public void valueOf_greenTooLow() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Color.of(Color.MIN_INTENSITY, Color.MIN_INTENSITY - 1)
        );
    }

    @Test
    public void valueOf_greenTooHigh() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Color.of(Color.MIN_INTENSITY, Color.MAX_INTENSITY + 1)
        );
    }

}
