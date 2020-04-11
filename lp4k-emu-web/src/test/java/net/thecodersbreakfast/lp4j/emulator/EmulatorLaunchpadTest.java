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

package net.thecodersbreakfast.lp4j.emulator;

import io.lp4k.api.MockLaunchpadListener;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.thecodersbreakfast.lp4j.emulator.input.EventBusHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
class EmulatorLaunchpadTest {

    @Captor
    private ArgumentCaptor<Router> routerCaptor;

    private EmulatorLaunchpad emulatorLaunchpad;

    @Test
    void emulator_created_for_port() {
        assertDoesNotThrow(() -> {
            emulatorLaunchpad = new EmulatorLaunchpad(0);
            emulatorLaunchpad.close();
        });
    }

    @Test
    void server_event_bus_was_created_with_required_configuration(
        Vertx vertx,
        VertxTestContext testContext
    ) {
        Vertx vertxSpy = spy(vertx);
        EventBus eventBusSpy = spy(vertxSpy.eventBus());

        doReturn(eventBusSpy).when(vertxSpy).eventBus();

        emulatorLaunchpad = new EmulatorLaunchpad(
            0,
            vertxSpy
        );

        verify(eventBusSpy).consumer(
            eq("lp4j:server"),
            any(EventBusHandler.class)
        );

        testContext.completeNow();
    }

    @Test
    void http_server_configured_on_required_port(
        Vertx vertx,
        VertxTestContext testContext
    ) {
        Vertx vertxSpy = spy(vertx);
        HttpServer httpServerSpy = spy(vertxSpy.createHttpServer());

        doReturn(httpServerSpy).when(vertxSpy).createHttpServer();

        emulatorLaunchpad = new EmulatorLaunchpad(
            9999,
            vertxSpy
        );

        verify(httpServerSpy).listen(9999);

        testContext.completeNow();
    }

    @Test
    void web_resources_route_registered(
        Vertx vertx,
        VertxTestContext testContext
    ) {
        Vertx vertxSpy = spy(vertx);
        HttpServer httpServerSpy = spy(vertxSpy.createHttpServer());

        doReturn(httpServerSpy).when(vertxSpy).createHttpServer();

        emulatorLaunchpad = new EmulatorLaunchpad(
            9999,
            vertxSpy
        );

        verify(httpServerSpy).requestHandler(routerCaptor.capture());

        Router router = routerCaptor.getValue();

        assertNotNull(
            router.get("/"),
            "Web static resources route is not registered"
        );

        testContext.completeNow();
    }

    @Test
    void launchpad_events_route_registered(
        Vertx vertx,
        VertxTestContext testContext
    ) {
        Vertx vertxSpy = spy(vertx);
        HttpServer httpServerSpy = spy(vertxSpy.createHttpServer());

        doReturn(httpServerSpy).when(vertxSpy).createHttpServer();

        emulatorLaunchpad = new EmulatorLaunchpad(
            9999,
            vertxSpy
        );

        verify(httpServerSpy).requestHandler(routerCaptor.capture());

        Router router = routerCaptor.getValue();

        assertNotNull(
            router.get("/eventbus"),
            "Launchpad events route is not registered"
        );

        testContext.completeNow();
    }


    @Test
    void emulator_successfully_closed() {
        Vertx vertxSpy = spy(Vertx.vertx());

        emulatorLaunchpad = new EmulatorLaunchpad(
            0,
            vertxSpy
        );

        emulatorLaunchpad.close();

        verify(vertxSpy).close();
    }

    @Test
    void emulator_client_is_created_on_getting() {
        emulatorLaunchpad = new EmulatorLaunchpad(
            0,
            Vertx.vertx()
        );

        assertNotNull(emulatorLaunchpad.getClient());
    }

    @Test
    void launchpad_listener_was_set() {
        EventBusHandler eventBusHandler = mock(EventBusHandler.class);

        emulatorLaunchpad = new EmulatorLaunchpad(
            0,
            Vertx.vertx(),
            eventBusHandler
        );

        MockLaunchpadListener launchpadListener = new MockLaunchpadListener() {
        };

        emulatorLaunchpad.setListener(launchpadListener);

        verify(eventBusHandler).setListener(launchpadListener);
    }

}