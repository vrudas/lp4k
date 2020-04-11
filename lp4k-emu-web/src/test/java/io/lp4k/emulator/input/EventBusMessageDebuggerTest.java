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

package io.lp4k.emulator.input;

import io.vertx.ext.bridge.BridgeEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class EventBusMessageDebuggerTest {

    private EventBusMessageDebugger eventBusMessageDebugger;

    @BeforeEach
    void setUp() {
        eventBusMessageDebugger = new EventBusMessageDebugger();
    }

    @Test
    void debug_socket_created_event() {
        MockBridgeEvent bridgeEvent = mock(MockBridgeEvent.class);
        when(bridgeEvent.type()).thenReturn(BridgeEventType.SOCKET_CREATED);

        eventBusMessageDebugger.handle(bridgeEvent);

        verify(bridgeEvent).complete(eq(true));
    }

    @Test
    void debug_publish_event() {
        MockBridgeEvent bridgeEvent = mock(MockBridgeEvent.class);
        when(bridgeEvent.type()).thenReturn(BridgeEventType.PUBLISH);

        eventBusMessageDebugger.handle(bridgeEvent);

        verify(bridgeEvent).getRawMessage();
        verify(bridgeEvent).complete(eq(true));
    }

    @Test
    void debug_send_event() {
        MockBridgeEvent bridgeEvent = mock(MockBridgeEvent.class);
        when(bridgeEvent.type()).thenReturn(BridgeEventType.SEND);

        eventBusMessageDebugger.handle(bridgeEvent);

        verify(bridgeEvent).getRawMessage();
        verify(bridgeEvent).complete(eq(true));
    }
}