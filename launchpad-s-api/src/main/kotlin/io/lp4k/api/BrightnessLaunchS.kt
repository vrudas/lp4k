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

import io.lp4k.launchpad.api.Brightness

/**
 * Describes the level of brightness of the pads and buttons lights.
 *
 * `Brightness` instances are immutable and cached.
 *
 * @param brightnessLevel The desired level of brightness.
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
data class BrightnessLaunchS(
    override val brightnessLevel: Int
): Brightness {
    companion object {
        /** Minimum level of brightness  */
        const val MIN_VALUE = 0

        /** Maximum level of brightness  */
        const val MAX_VALUE = 15

        /** Cache for all levels of brightness  */
        private val CACHE = Array(16) { BrightnessLaunchS(it) }

        /** Minimum brightness  */
        val BRIGHTNESS_MIN = of(MIN_VALUE)

        /** Maximum brightness  */
        val BRIGHTNESS_MAX = of(MAX_VALUE)

        /**
         * Factory method.
         *
         * @param brightness The desired level of brightness. Must be in range [[net.thecodersbreakfast.lp4j.api.Brightness.MIN_VALUE],[net.thecodersbreakfast.lp4j.api.Brightness.MAX_VALUE]]
         * @return The Brightness instance.
         * @throws java.lang.IllegalArgumentException If the requested brightness level is out of acceptable range.
         */
        fun of(brightness: Int): BrightnessLaunchS {
            require(brightness in (MIN_VALUE..MAX_VALUE)) {
                "Invalid brightness level : $brightness. Acceptable values are in range [$MIN_VALUE..$MAX_VALUE]."
            }
            return CACHE[brightness]
        }

    }
    /**
     * Returns the level of brightness
     *
     * @return the level of brightness
     */

    /**
     * Returns a brighter Brightness instance, if the maximum level of brightness is not already reached.
     *
     * @return a brighter Brightness instance, or the same instance if it was already the brightest.
     */
    fun more(): BrightnessLaunchS {
        return if (brightnessLevel < MAX_VALUE) {
            of(brightnessLevel + 1)
        } else {
            this
        }
    }

    /**
     * Returns a less bright Brightness instance, if the minimum level of brightness is not already reached.
     *
     * @return a less bright Brightness instance, or the same instance if it was already the least bright.
     */
    fun less(): BrightnessLaunchS {
        return if (brightnessLevel > MIN_VALUE) {
            of(brightnessLevel - 1)
        } else {
            this
        }
    }

}