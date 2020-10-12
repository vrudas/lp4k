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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ButtonLaunchSTest {

    @Test
    fun at_topButton() {
        val button = ButtonLaunchS.atTop(0)

        assertEquals(ButtonLaunchS.UP, button)
        assertTrue(button.isTopButton)
    }

    @Test
    fun at_rightButton() {
        val button = ButtonLaunchS.atRight(7)

        assertEquals(ButtonLaunchS.ARM, button)
        assertTrue(button.isRightButton)
    }

    @Test
    fun at_coordinateTooLow() {
        assertThrows<IllegalArgumentException> { ButtonLaunchS.atTop(ButtonLaunchS.MIN_COORD - 1) }
    }

    @Test
    fun at_coordinateTooHigh() {
        assertThrows<IllegalArgumentException> { ButtonLaunchS.atTop(ButtonLaunchS.MAX_COORD + 1) }
    }

    @Test
    fun `is Top or Right`() {
        assertTrue(ButtonLaunchS.UP.isTopButton)
        assertFalse(ButtonLaunchS.UP.isRightButton)
        assertTrue(ButtonLaunchS.VOL.isRightButton)
        assertFalse(ButtonLaunchS.VOL.isTopButton)
    }

    @Test
    fun tostring() {
        assertEquals("Button[UP(top,0)]", ButtonLaunchS.UP.toString())
    }

    @Test
    fun top_buttons_coordinate_are_correct() {
        for (expectedCoordinate in ButtonLaunchS.BUTTONS_TOP.indices) {
            val buttonCoordinate = ButtonLaunchS.BUTTONS_TOP[expectedCoordinate].coordinate
            assertEquals(expectedCoordinate, buttonCoordinate)
        }
    }

    @Test
    fun right_buttons_coordinate_are_correct() {
        for (expectedCoordinate in ButtonLaunchS.BUTTONS_RIGHT.indices) {
            val buttonCoordinate = ButtonLaunchS.BUTTONS_RIGHT[expectedCoordinate].coordinate
            assertEquals(expectedCoordinate, buttonCoordinate)
        }
    }
}