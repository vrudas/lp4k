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

import io.lp4k.emulator.output.OutputEventType
import io.lp4k.launchpad.api.BackBufferOperation
import io.lp4k.launchpad.api.Buffer
import io.lp4k.launchpad.api.s.*
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*

@Suppress("kotlin:S100")
internal class EmulatorLaunchpadClientTest {

    private fun verifyEventWasPublished(
        outputEventType: OutputEventType,
        expectedParametersForPublish: Map<String, Any?>
    ) {
        val parametersToCheck = mutableMapOf<String, Any?>(
            "evt" to outputEventType.name
        ) + expectedParametersForPublish

        val message = JsonObject(parametersToCheck)

        verify(eventBus).publish("lp4j:client", message)
    }

    private lateinit var eventBus: MockEventBus
    private lateinit var emulatorLaunchpadClient: EmulatorLaunchpadClient

    @BeforeEach
    fun setUp() {
        val vertx = mock(MockVertx::class.java)
        eventBus = mock(MockEventBus::class.java)

        `when`(vertx.eventBus()).thenReturn(eventBus)

        emulatorLaunchpadClient = EmulatorLaunchpadClient(vertx)
    }

    @Test
    fun `reset event was published`() {
        emulatorLaunchpadClient.reset()

        verifyEventWasPublished(OutputEventType.RST, emptyMap())
    }

    @Test
    fun `set lights event not published because not implemented`() {
        assertThrows<UnsupportedOperationException> {
            emulatorLaunchpadClient.setLights(
                emptyArray(),
                BackBufferOperation.NONE
            )
        }
    }

    @Test
    fun `test lights event published with low light intensity`() {
        emulatorLaunchpadClient.testLights(LightIntensityLaunchS.LOW)

        verifyEventWasPublished(
            OutputEventType.TST,
            java.util.Map.of<String, Any>(LIGHT_INTENSITY_KEY, 5)
        )
    }

    @Test
    fun `test lights event published with medium light intensity`() {
        emulatorLaunchpadClient.testLights(LightIntensityLaunchS.MEDIUM)

        verifyEventWasPublished(
            OutputEventType.TST,
            mapOf(LIGHT_INTENSITY_KEY to 10)
        )
    }

    @Test
    fun `test lights event published with high light intensity`() {
        emulatorLaunchpadClient.testLights(LightIntensityLaunchS.HIGH)

        verifyEventWasPublished(
            OutputEventType.TST,
            mapOf(LIGHT_INTENSITY_KEY to 15)
        )
    }

    @Test
    fun `set pad light event published`() {
        emulatorLaunchpadClient.setPadLight(
            PadLaunchS.at(0, 0),
            ColorLaunchS.RED,
            BackBufferOperation.NONE
        )

        verifyEventWasPublished(
            OutputEventType.PADLGT,
            mapOf(
                PAD_X_KEY to 0,
                PAD_Y_KEY to 0,
                COLOR_KEY to JsonObject()
                    .put(RED_COLOR_KEY, 3)
                    .put(GREEN_COLOR_KEY, 0),
                OPERATION_KEY to "NONE"
            )
        )
    }

    @Test
    fun `set button light event published for right button`() {
        emulatorLaunchpadClient.setButtonLight(
            ButtonLaunchS.VOL,
            ColorLaunchS.GREEN,
            BackBufferOperation.CLEAR
        )

        verifyEventWasPublished(
            OutputEventType.BTNLGT,
            mapOf(
                BUTTON_COORDINATE_KEY to 0,
                BUTTON_IS_TOP_KEY to false,
                COLOR_KEY to
                    JsonObject()
                        .put(RED_COLOR_KEY, 0)
                        .put(GREEN_COLOR_KEY, 3),
                OPERATION_KEY to "CLEAR"
            )
        )
    }

    @Test
    fun `set button light event published for top button`() {
        emulatorLaunchpadClient.setButtonLight(
            ButtonLaunchS.UP,
            ColorLaunchS.GREEN,
            BackBufferOperation.COPY
        )

        verifyEventWasPublished(
            OutputEventType.BTNLGT,
            mapOf(
                BUTTON_COORDINATE_KEY to 0,
                BUTTON_IS_TOP_KEY to true,
                COLOR_KEY to JsonObject()
                    .put(RED_COLOR_KEY, 0)
                    .put(GREEN_COLOR_KEY, 3)
                ,
                OPERATION_KEY to "COPY"
            )
        )
    }

    @Test
    fun `set brightness event published`() {
        emulatorLaunchpadClient.setBrightness(BrightnessLaunchS.BRIGHTNESS_MAX)

        verifyEventWasPublished(
            OutputEventType.BRGHT,
            mapOf(BRIGHTNESS_KEY to BrightnessLaunchS.BRIGHTNESS_MAX.brightnessLevel)
        )
    }

    @Test
    fun `set buffers event published`() {
        emulatorLaunchpadClient.setBuffers(
            Buffer.BUFFER_1,
            Buffer.BUFFER_0,
            copyVisibleBufferToWriteBuffer = true,
            autoSwap = true
        )

        verifyEventWasPublished(
            OutputEventType.BUF,
            mapOf(
                VISIBLE_BUFFER_KEY to Buffer.BUFFER_1.name,
                WRITE_BUFFER_KEY to Buffer.BUFFER_0.name,
                COPY_VISIBLE_BUFFER_KEY to true,
                AUTO_SWAP_BUFFERS_KEY to true
            )
        )
    }

    @Test
    fun `scroll text event not published because not implemented`() {
        assertThrows<UnsupportedOperationException> {
            emulatorLaunchpadClient.scrollText(
                "",
                ColorLaunchS.AMBER,
                ScrollSpeedLaunchS.SPEED_MIN,
                false,
                BackBufferOperation.NONE
            )
        }
    }

    companion object {
        private const val BRIGHTNESS_KEY = "b"

        private const val LIGHT_INTENSITY_KEY = "i"

        private const val OPERATION_KEY = "o"

        private const val COLOR_KEY = "c"
        private const val RED_COLOR_KEY = "r"
        private const val GREEN_COLOR_KEY = "g"

        private const val PAD_X_KEY = "x"
        private const val PAD_Y_KEY = "y"

        private const val BUTTON_COORDINATE_KEY = "i"
        private const val BUTTON_IS_TOP_KEY = "t"

        private const val VISIBLE_BUFFER_KEY = "v"
        private const val WRITE_BUFFER_KEY = "w"
        private const val COPY_VISIBLE_BUFFER_KEY = "c"
        private const val AUTO_SWAP_BUFFERS_KEY = "a"
    }
}