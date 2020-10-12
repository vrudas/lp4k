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

import io.lp4k.launchpad.api.ScrollSpeed

/**
 * Represents the speed at which the text scrolls through the Launchpad grid (see [net.thecodersbreakfast.lp4j.api.LaunchpadClient.scrollText]).
 *
 *
 * `ScrollSpeed` instances are immutable and cached.
 * @param speedValue The scrolling speed
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
data class ScrollSpeedLaunchS(
    override val speedValue: Int
): ScrollSpeed {
    companion object {
        /** Minimum value for a scrolling speed  */
        const val MIN_VALUE = 1

        /** Maximum value for a scrolling speed  */
        const val MAX_VALUE = 7

        /** Cache of all possible scrolling speeds  */
        private val CACHE: Array<ScrollSpeedLaunchS> = Array(MAX_VALUE) { ScrollSpeedLaunchS(it + 1) }

        /** Slowest scrolling speed  */
        val SPEED_MIN = of(MIN_VALUE)

        /** Fastest scrolling speed  */
        val SPEED_MAX = of(MAX_VALUE)

        /**
         * Factory method.
         *
         * @param speed The desired scrolling speed. Must be in range [[net.thecodersbreakfast.lp4j.api.ScrollSpeed.MIN_VALUE],[ ][net.thecodersbreakfast.lp4j.api.ScrollSpeed.MAX_VALUE]].
         * @return The ScrollSpeed instance
         * @throws java.lang.IllegalArgumentException If the requested speed is out of acceptable range.
         */
        fun of(speed: Int): ScrollSpeedLaunchS {
            require(speed in (MIN_VALUE..MAX_VALUE)) {
                "Invalid speed value : $speed. Acceptable values are in range [$MIN_VALUE..$MAX_VALUE]."
            }
            return CACHE[speed - MIN_VALUE]
        }

    }

}