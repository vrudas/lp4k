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

/**
 * Represents the color of a pad or button on the Launchpad. The Launchpad has a red light and a green light under each
 * pad or button. The actual color is obtained by adjusting their respective intensities.
 *
 *
 * `Color` instances are immutable and cached.
 *
 * @param redIntensity The red component intensity
 * @param greenIntensity The green component intensity
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
data class Color(val redIntensity: Int, val greenIntensity: Int) {

    companion object {

        /** Minimal red or green component intensity  */
        const val MIN_INTENSITY = 0

        /** Maximal red or green component intensity  */
        const val MAX_INTENSITY = 3

        // Color cache
        private val CACHE = initColors()

        private fun initColors(): Array<Array<Color>> {
            val dimensionSize = MAX_INTENSITY + 1

            return Array(dimensionSize) { i ->
                Array(dimensionSize) { j -> Color(i, j) }
            }
        }

        // Most used colors
        /** Black (red 0, green 0)  */
        val BLACK = CACHE[0][0]

        /** Red (red 3, green 0)  */
        val RED = CACHE[3][0]

        /** Green (red 0, green 3)  */
        val GREEN = CACHE[0][3]

        /** Orange (red 3, green 2)  */
        val ORANGE = CACHE[3][2]

        /** Amber (red 3, green 3)  */
        val AMBER = CACHE[3][3]

        /** Yellow (red 2, green 3)  */
        val YELLOW = CACHE[2][3]

        /**
         * Factory method
         *
         * @param red The red component. Acceptable values are in [[net.thecodersbreakfast.lp4j.api.Color.MIN_INTENSITY],[net.thecodersbreakfast.lp4j.api.Color.MAX_INTENSITY]].
         * @param green The green component. Acceptable values are in [[net.thecodersbreakfast.lp4j.api.Color.MIN_INTENSITY],[net.thecodersbreakfast.lp4j.api.Color.MAX_INTENSITY]].
         *
         * @return The Color obtained by mixing the given red and green values.
         *
         * @throws java.lang.IllegalArgumentException If the red or green parameters are out of acceptable range.
         */
        fun of(red: Int, green: Int): Color {
            require(red in (MIN_INTENSITY..MAX_INTENSITY)) {
                "Invalid red value : $red. Acceptable values are in range [0..3]."
            }
            require(green in (MIN_INTENSITY..MAX_INTENSITY)) {
                "Invalid green value : $green. Acceptable values are in range [0..3]."
            }

            return CACHE[red][green]
        }

    }

}