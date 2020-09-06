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
package io.lp4k.emulator.input

import net.thecodersbreakfast.lp4j.api.Button
import net.thecodersbreakfast.lp4j.api.LaunchpadListener
import net.thecodersbreakfast.lp4j.api.Pad
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@Suppress("kotlin:S100")
internal class EventBusHandlerTest {

    private lateinit var launchpadListener: LaunchpadListener
    private lateinit var handler: EventBusHandler

    @BeforeEach
    fun setUp() {
        launchpadListener = mock(LaunchpadListener::class.java)

        handler = EventBusHandler()
        handler.listener = launchpadListener
    }

    @Test
    fun `exception is thrown for null input event type`() {
        assertThrows<IllegalStateException> {
            handler.handle(message(null))
        }
    }

    @Test
    fun `exception is thrown for empty input event type`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(message(""))
        }
    }

    @Test
    fun `exception is thrown for invalid input event type`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(message("InputEventType"))
        }
    }

    @Test
    fun `launchpad listener scroll text was called for required input event type`() {
        handler.handle(message(InputEventType.TS.name))
        verify(launchpadListener).onTextScrolled(anyLong())
    }

    @Test
    fun `launchpad listener pad press event was not called because of missing x coordinate`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(padPressMessage(null, null))
        }
    }

    @Test
    fun `launchpad listener pad press event was not called because of missing y coordinate`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(padPressMessage(Pad.X_MAX, null))
        }
    }

    @Test
    fun `launchpad listener pad press was called for required input event type`() {
        val pad = Pad.at(Pad.X_MIN, Pad.Y_MAX)

        handler.handle(padPressMessage(pad.x, pad.y))

        verify(launchpadListener).onPadPressed(eq(pad), anyLong())
    }

    @Test
    fun `launchpad listener pad release event was not called because of missing x coordinate`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(padPressMessage(null, null))
        }
    }

    @Test
    fun `launchpad listener pad release event was not called because of missing y coordinate`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(padPressMessage(Pad.X_MIN, null))
        }
    }

    @Test
    fun `launchpad listener pad release was called for required input event type`() {
        val pad = Pad.at(Pad.X_MIN, Pad.Y_MAX)

        handler.handle(padReleaseMessage(pad.x, pad.y))

        verify(launchpadListener).onPadReleased(eq(pad), anyLong())
    }

    @Test
    fun `launchpad listener button press event was not called because of missing x coordinate`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(buttonPressMessage(null, null))
        }
    }

    @Test
    fun `launchpad listener button press event was not called because of missing y coordinate`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(buttonPressMessage(Button.MAX_COORD, null))
        }
    }

    @Test
    fun `launchpad listener top button press was called for required input event type`() {
        val topButton = Button.UP

        handler.handle(buttonPressMessage(topButton.coordinate, RIGHT_BUTTON_COORDINATE))

        verify(launchpadListener).onButtonPressed(eq(topButton), anyLong())
    }

    @Test
    fun `launchpad listener right button press was called for required input event type`() {
        val rightButton = Button.VOL

        handler.handle(buttonPressMessage(RIGHT_BUTTON_COORDINATE, rightButton.coordinate))

        verify(launchpadListener).onButtonPressed(eq(rightButton), anyLong())
    }

    @Test
    fun `launchpad listener button release event was not called because of missing x coordinate`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(buttonReleaseMessage(null, null))
        }
    }

    @Test
    fun `launchpad listener button release event was not called because of missing y coordinate`() {
        assertThrows<IllegalArgumentException> {
            handler.handle(buttonReleaseMessage(Button.MAX_COORD, null))
        }
    }

    @Test
    fun `launchpad listener top button release was called for required input event type`() {
        val topButton = Button.UP

        handler.handle(buttonReleaseMessage(topButton.coordinate, RIGHT_BUTTON_COORDINATE))

        verify(launchpadListener).onButtonReleased(eq(topButton), anyLong())
    }

    @Test
    fun `launchpad listener right button release was called for required input event type`() {
        val rightButton = Button.VOL

        handler.handle(buttonReleaseMessage(RIGHT_BUTTON_COORDINATE, rightButton.coordinate))

        verify(launchpadListener).onButtonReleased(eq(rightButton), anyLong())
    }

    companion object {
        private const val EVENT_TYPE_KEY = "evt"

        private const val PAD_X_KEY = "x"
        private const val PAD_Y_KEY = "y"

        private const val BUTTON_X_KEY = "x"
        private const val BUTTON_Y_KEY = "y"

        private const val RIGHT_BUTTON_COORDINATE = -1

        private fun padPressMessage(x: Int?, y: Int?) = message(
            InputEventType.PP,
            Coordinate(PAD_X_KEY, x),
            Coordinate(PAD_Y_KEY, y)
        )

        private fun padReleaseMessage(x: Int, y: Int) = message(
            InputEventType.PR,
            Coordinate(PAD_X_KEY, x),
            Coordinate(PAD_Y_KEY, y)
        )

        private fun buttonPressMessage(x: Int?, y: Int?) = message(
            InputEventType.BP,
            Coordinate(BUTTON_X_KEY, x),
            Coordinate(BUTTON_Y_KEY, y)
        )

        private fun buttonReleaseMessage(x: Int?, y: Int?) = message(
            InputEventType.BR,
            Coordinate(BUTTON_X_KEY, x),
            Coordinate(BUTTON_Y_KEY, y)
        )

        private fun message(
            eventType: InputEventType,
            xCoordinate: Coordinate,
            yCoordinate: Coordinate
        ): MockMessage {
            val bodyMap = mapOf(
                EVENT_TYPE_KEY to eventType.name,
                xCoordinate.keyName to xCoordinate.coordinateValue,
                yCoordinate.keyName to yCoordinate.coordinateValue
            )

            return MockMessage(bodyMap)
        }

        private fun message(inputEventName: String?): MockMessage {
            return MockMessage(mapOf(EVENT_TYPE_KEY to inputEventName))
        }
    }
}