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

public class LaunchpadExceptionTest {

    @Test
    public void testLaunchpadException_message() {
        String expectedMessage = "message";

        LaunchpadException launchpadException = new LaunchpadException(expectedMessage);

        assertEquals(expectedMessage, launchpadException.getMessage());
    }

    @Test
    public void testLaunchpadException_cause() {
        RuntimeException expectedCause = new RuntimeException();

        LaunchpadException launchpadException = new LaunchpadException(expectedCause);

        assertEquals(expectedCause, launchpadException.getCause());
    }

    @Test
    public void testLaunchpadException_both_message_and_cause() {
        String expectedMessage = "message";
        RuntimeException expectedCause = new RuntimeException();

        LaunchpadException launchpadException = new LaunchpadException(expectedMessage, expectedCause);

        assertEquals(expectedMessage, launchpadException.getMessage());
        assertEquals(expectedCause, launchpadException.getCause());
    }

}
