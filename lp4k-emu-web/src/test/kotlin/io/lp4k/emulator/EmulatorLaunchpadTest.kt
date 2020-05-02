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
package io.lp4k.emulator

import io.lp4k.api.MockLaunchpadListener
import io.lp4k.emulator.input.EventBusHandler
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Captor
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@Suppress("kotlin:S100")
@ExtendWith(VertxExtension::class, MockitoExtension::class)
internal class EmulatorLaunchpadTest {

    @Captor
    private lateinit var routerCaptor: ArgumentCaptor<Router>

    private lateinit var emulatorLaunchpad: EmulatorLaunchpad

    @Test
    fun `emulator created for port`() {
        assertDoesNotThrow {
            emulatorLaunchpad = EmulatorLaunchpad(0)
            emulatorLaunchpad.close()
        }
    }

    @Test
    fun `server event bus was created with required configuration`(
        vertx: Vertx,
        testContext: VertxTestContext
    ) {
        val vertxSpy = spy(vertx)
        val eventBusSpy = spy(vertxSpy.eventBus())

        doReturn(eventBusSpy).`when`(vertxSpy).eventBus()

        emulatorLaunchpad = EmulatorLaunchpad(
            0,
            vertxSpy
        )

        verify(eventBusSpy).consumer(
            eq("lp4j:server"),
            any(EventBusHandler::class.java)
        )

        testContext.completeNow()
    }

    @Test
    fun `http server configured on required port`(
        vertx: Vertx,
        testContext: VertxTestContext
    ) {
        val vertxSpy = spy(vertx)
        val httpServerSpy = spy(vertxSpy.createHttpServer())

        doReturn(httpServerSpy).`when`(vertxSpy).createHttpServer()

        emulatorLaunchpad = EmulatorLaunchpad(
            9999,
            vertxSpy
        )

        verify(httpServerSpy).listen(9999)

        testContext.completeNow()
    }

    @Test
    fun `web resources route registered`(
        vertx: Vertx,
        testContext: VertxTestContext
    ) {
        val vertxSpy = spy(vertx)
        val httpServerSpy = spy(vertxSpy.createHttpServer())

        doReturn(httpServerSpy).`when`(vertxSpy).createHttpServer()

        emulatorLaunchpad = EmulatorLaunchpad(
            9999,
            vertxSpy
        )

        verify(httpServerSpy).requestHandler(routerCaptor.capture())

        val router = routerCaptor.value

        assertNotNull(
            router["/"],
            "Web static resources route is not registered"
        )

        testContext.completeNow()
    }

    @Test
    fun `launchpad events route registered`(
        vertx: Vertx,
        testContext: VertxTestContext
    ) {
        val vertxSpy = spy(vertx)
        val httpServerSpy = spy(vertxSpy.createHttpServer())

        doReturn(httpServerSpy).`when`(vertxSpy).createHttpServer()

        emulatorLaunchpad = EmulatorLaunchpad(
            9999,
            vertxSpy
        )

        verify(httpServerSpy).requestHandler(routerCaptor.capture())

        val router = routerCaptor.value

        assertNotNull(
            router["/eventbus"],
            "Launchpad events route is not registered"
        )

        testContext.completeNow()
    }

    @Test
    fun `emulator successfully closed`() {
        val vertxSpy = spy(Vertx.vertx())

        emulatorLaunchpad = EmulatorLaunchpad(
            0,
            vertxSpy
        )

        emulatorLaunchpad.close()

        verify(vertxSpy).close()
    }

    @Test
    fun `emulator client is created on getting`() {
        emulatorLaunchpad = EmulatorLaunchpad(
            0,
            Vertx.vertx()
        )

        assertNotNull(emulatorLaunchpad.client)
    }

    @Test
    fun `launchpad listener was set`() {
        val eventBusHandler = mock(EventBusHandler::class.java)

        emulatorLaunchpad = EmulatorLaunchpad(
            0,
            Vertx.vertx(),
            eventBusHandler
        )

        val launchpadListener: MockLaunchpadListener = object : MockLaunchpadListener {}

        emulatorLaunchpad.setListener(launchpadListener)

        verify(eventBusHandler).listener = launchpadListener
    }
}