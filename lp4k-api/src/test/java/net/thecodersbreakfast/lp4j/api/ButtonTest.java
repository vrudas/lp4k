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

import static net.thecodersbreakfast.lp4j.api.Button.BUTTONS_RIGHT;
import static net.thecodersbreakfast.lp4j.api.Button.BUTTONS_TOP;
import static org.junit.jupiter.api.Assertions.*;

public class ButtonTest {

    @Test
    public void at_topButton() {
        Button button = Button.atTop(0);
        assertEquals(Button.UP, button);
        assertTrue(button.isTopButton());
    }

    @Test
    public void at_rightButton() {
        Button button = Button.atRight(7);
        assertEquals(Button.ARM, button);
        assertTrue(button.isRightButton());
    }

    @Test
    public void at_coordinateTooLow() {
        assertThrows(IllegalArgumentException.class, () -> Button.atTop(Button.MIN_COORD - 1));
    }

    @Test
    public void at_coordinateTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> Button.atTop(Button.MAX_COORD + 1));
    }

    @Test
    public void isTopOrRight() {
        assertTrue(Button.UP.isTopButton());
        assertFalse(Button.UP.isRightButton());
        assertTrue(Button.VOL.isRightButton());
        assertFalse(Button.VOL.isTopButton());
    }

    @Test
    public void tostring() {
        assertEquals("Button[UP(top,0)]", Button.UP.toString());
    }

    @Test
    void top_buttons_coordinate_are_correct() {
        for (int expectedCoordinate = 0; expectedCoordinate < BUTTONS_TOP.length; expectedCoordinate++) {
            int buttonCoordinate = BUTTONS_TOP[expectedCoordinate].getCoordinate();
            assertEquals(expectedCoordinate, buttonCoordinate);
        }
    }

    @Test
    void right_buttons_coordinate_are_correct() {
        for (int expectedCoordinate = 0; expectedCoordinate < BUTTONS_RIGHT.length; expectedCoordinate++) {
            int buttonCoordinate = BUTTONS_RIGHT[expectedCoordinate].getCoordinate();
            assertEquals(expectedCoordinate, buttonCoordinate);
        }
    }
}
