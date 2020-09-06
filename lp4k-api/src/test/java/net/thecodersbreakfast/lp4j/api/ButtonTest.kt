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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ButtonTest {

    @Test
    fun at_topButton() {
        val button = Button.atTop(0)

        assertEquals(Button.UP, button)
        assertTrue(button.isTopButton)
    }

    @Test
    fun at_rightButton() {
        val button = Button.atRight(7)

        assertEquals(Button.ARM, button)
        assertTrue(button.isRightButton)
    }

    @Test
    fun at_coordinateTooLow() {
        assertThrows<IllegalArgumentException> { Button.atTop(Button.MIN_COORD - 1) }
    }

    @Test
    fun at_coordinateTooHigh() {
        assertThrows<IllegalArgumentException> { Button.atTop(Button.MAX_COORD + 1) }
    }

    @Test
    fun `is Top or Right`() {
        assertTrue(Button.UP.isTopButton)
        assertFalse(Button.UP.isRightButton)
        assertTrue(Button.VOL.isRightButton)
        assertFalse(Button.VOL.isTopButton)
    }

    @Test
    fun tostring() {
        assertEquals("Button[UP(top,0)]", Button.UP.toString())
    }

    @Test
    fun top_buttons_coordinate_are_correct() {
        for (expectedCoordinate in Button.BUTTONS_TOP.indices) {
            val buttonCoordinate = Button.BUTTONS_TOP[expectedCoordinate].coordinate
            assertEquals(expectedCoordinate, buttonCoordinate)
        }
    }

    @Test
    fun right_buttons_coordinate_are_correct() {
        for (expectedCoordinate in Button.BUTTONS_RIGHT.indices) {
            val buttonCoordinate = Button.BUTTONS_RIGHT[expectedCoordinate].coordinate
            assertEquals(expectedCoordinate, buttonCoordinate)
        }
    }
}