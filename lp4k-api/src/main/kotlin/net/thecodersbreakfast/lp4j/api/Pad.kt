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
 * Represents a square pad on the Launchpad.
 *
 *
 * `Pad` instances are immutable and cached.
 *
 * @param x The X coordinate of the pad.
 * @param y The Y coordinate of the pad.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
data class Pad(val x: Int, val y: Int) {

    companion object {

        /** Minimal X coordinate for a pad  */
        const val X_MIN = 0

        /** Maximal X coordinate for a pad  */
        const val X_MAX = 7

        /** Minimal Y coordinate for a pad  */
        const val Y_MIN = 0

        /** Maximal Y coordinate for a pad  */
        const val Y_MAX = 7

        /** Cache of all pads  */
        private val PADS: Array<Array<Pad>> = initPads()

        private fun initPads(): Array<Array<Pad>> {
            val padRowSize = X_MAX + 1
            val padColumnSize = Y_MAX + 1

            return Array(padRowSize) { i ->
                Array(padColumnSize) { j -> Pad(i, j) }
            }
        }

        /**
         * Factory method.
         *
         * @param x The X coordinate of the pad. Must be in range [[Pad.X_MIN],[Pad.X_MAX]].
         * @param y The Y coordinate of the pad. Must be in range [[Pad.Y_MIN],[Pad.Y_MAX]].
         * @return The pad.
         * @throws java.lang.IllegalArgumentException If the coordinates are invalid.
         */
        fun at(x: Int, y: Int): Pad {
            require(x in (X_MIN..X_MAX) && y in (Y_MIN..Y_MAX)) {
                "Illegal pad coordinates : ($x,$y). Acceptable values are in [0..7] on both axis."
            }
            return PADS[x][y]
        }

    }

}