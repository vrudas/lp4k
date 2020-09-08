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

/**
 * Represents the Launchpad's 16 round buttons (8 at the top, 8 on the right side)
 *
 * @param coordinate The coordinate of the button.
 * @param isTopButton `true` for a top-row button, `false` otherwise.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
enum class Button(

    /** The button coordinates  */
    val coordinate: Int,

    /** Tells if it is a top-row button (`true`), as opposed to a righ-side button (`false`)  */
    val isTopButton: Boolean
) {

    /** The UP button (1st position from the left, on the top row)  */
    UP(0, true),

    /** The DOWN button (2nd position from the left, on the top row)  */
    DOWN(1, true),

    /** The LEFT button (3rd position from the left, on the top row)  */
    LEFT(2, true),

    /** The RIGHT button (4th position from the left, on the top row)  */
    RIGHT(3, true),

    /** The SESSION button (5th position from the left, on the top row)  */
    SESSION(4, true),

    /** The USER_1 button (6th position from the left, on the top row)  */
    USER_1(5, true),

    /** The USER_2 button (7th position from the left, on the top row)  */
    USER_2(6, true),

    /** The MIXER button (8th position from the left, on the top row)  */
    MIXER(7, true),

    /** The VOL button (1st position from the top, on the right side)  */
    VOL(0, false),

    /** The PAN button (2nd position from the top, on the right side)  */
    PAN(1, false),

    /** The SND_A button (3rd position from the top, on the right side)  */
    SND_A(2, false),

    /** The SND_B button (4th position from the top, on the right side)  */
    SND_B(3, false),

    /** The STOP button (5th position from the top, on the right side)  */
    STOP(4, false),

    /** The TRACK_ON button (6th position from the top, on the right side)  */
    TRACK_ON(5, false),

    /** The SOLO button (7th position from the top, on the right side)  */
    SOLO(6, false),

    /** The ARM button (8th position from the top, on the right side)  */
    ARM(7, false);

    /**
     * Tells if this button is located on the right side.
     *
     * @return `true` if this button belongs to the right side, `false` otherwise.
     */
    val isRightButton: Boolean
        get() = !isTopButton

    override fun toString(): String {
        val isTopButtonLabel = if (isTopButton) "top" else "right"
        return "Button[$name($isTopButtonLabel,$coordinate)]"
    }

    companion object {
        /** Minimal coordinate for a button  */
        const val MIN_COORD = 0

        /** Maximal coordinate for a button  */
        const val MAX_COORD = 7

        /** Top-row buttons, in left-to-right order  */
        val BUTTONS_TOP = arrayOf(UP, DOWN, LEFT, RIGHT, SESSION, USER_1, USER_2, MIXER)

        /** Right-side buttons, in top-to-bottom order  */
        val BUTTONS_RIGHT = arrayOf(VOL, PAN, SND_A, SND_B, STOP, TRACK_ON, SOLO, ARM)

        /**
         * Factory method for a top-row button.
         *
         * @param isTopButton `true` if the button is on the top row, `false` if it is on the right side
         * @param coordinate The coordinate of the button. Must be in range [[Button.MIN_COORD],[Button.MAX_COORD]].
         * @return The button
         * @throws IllegalArgumentException if the coordinates are invalid.
         */
        private fun at(isTopButton: Boolean, coordinate: Int): Button {
            require(!(coordinate < MIN_COORD || coordinate > MAX_COORD)) {
                "Invalid button coordinates : $coordinate. Value must be between $MIN_COORD and $MAX_COORD inclusive."
            }

            return if (isTopButton) {
                BUTTONS_TOP[coordinate]
            } else {
                BUTTONS_RIGHT[coordinate]
            }
        }

        /**
         * Factory method for a top-row button.
         *
         * @param c The coordinate of the button. Must be in range [[Button.MIN_COORD],[Button.MAX_COORD]].
         * @return The button
         * @throws IllegalArgumentException if the coordinates are invalid.
         */
        fun atTop(c: Int): Button = at(true, c)

        /**
         * Factory method for a top-row button.
         *
         * @param c The coordinate of the button. Must be in range [[Button.MIN_COORD],[Button.MAX_COORD]].
         * @return The button
         * @throws IllegalArgumentException if the coordinates are invalid.
         */
        fun atRight(c: Int): Button = at(false, c)
    }
}