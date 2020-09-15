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
package io.lp4k.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LaunchpadExceptionTest {

    @Test
    fun testLaunchpadException_message() {
        val expectedMessage = "message"
        val launchpadException = LaunchpadException(expectedMessage)

        assertEquals(expectedMessage, launchpadException.message)
    }

    @Test
    fun testLaunchpadException_cause() {
        val expectedCause = RuntimeException()
        val launchpadException = LaunchpadException(expectedCause)

        assertEquals(expectedCause, launchpadException.cause)
    }

    @Test
    fun testLaunchpadException_both_message_and_cause() {
        val expectedMessage = "message"
        val expectedCause = RuntimeException()
        val launchpadException = LaunchpadException(expectedMessage, expectedCause)

        assertEquals(expectedMessage, launchpadException.message)
        assertEquals(expectedCause, launchpadException.cause)
    }
}