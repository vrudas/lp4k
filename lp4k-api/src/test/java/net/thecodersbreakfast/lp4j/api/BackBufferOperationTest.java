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

package net.thecodersbreakfast.lp4j.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BackBufferOperationTest {

    @Test
    void check_back_buffer_operation_values_exists() {
        List<String> backBufferOperationNames = List.of("NONE", "COPY", "CLEAR");

        assertEquals(backBufferOperationNames.size(), BackBufferOperation.values().length);

        assertDoesNotThrow(() -> backBufferOperationNames.forEach(BackBufferOperation::valueOf));
    }
}