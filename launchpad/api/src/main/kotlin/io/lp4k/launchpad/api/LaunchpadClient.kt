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
package io.lp4k.launchpad.api

/**
 * Represents a client through which the API user can send commands to a particular Launchpad implementation. Usually
 * obtained by calling [Launchpad.client].
 *
 * @author Olivier Croisier (olivier.croisier@gmail.com)
 */
interface LaunchpadClient {
    /**
     * Reset the state of the Launchpad.
     */
    fun reset()

    /**
     * Lights up all the pads and buttons with the given light intensity.
     *
     * @param intensity The desired light intensity.
     */
    fun testLights(intensity: LightIntensity)

    /**
     * Bulk-set the colors of all pads and buttons.
     *
     * The length of colors array passed as a parameter may vary. Lights will be lit line by line, starting from the
     * upper-left pad down to the bottom-right pad, then under the upper-row buttons, and finally under the right-side
     * buttons.
     *
     * @param colors The colors to be set
     * @param operation What to do on the backbuffer
     */
    fun setLights(
        colors: Array<Color>,
        operation: BackBufferOperation
    )

    /**
     * Lights up the given pad with the given color.
     *
     * @param pad The pad to light up.
     * @param color The color to use. Use [net.thecodersbreakfast.lp4j.api.Color.BLACK] to switch the light off.
     * @param operation What to do on the backbuffer
     */
    fun setPadLight(
        pad: Pad,
        color: Color,
        operation: BackBufferOperation
    )

    /**
     * Lights up the given button with the given color.
     *
     * @param button The button to light up.
     * @param color The color to use. Use [net.thecodersbreakfast.lp4j.api.Color.BLACK] to switch the light off.
     * @param operation What to do on the backbuffer
     */
    fun setButtonLight(
        button: Button,
        color: Color,
        operation: BackBufferOperation
    )

    /**
     * Set the overall brightness of the pad and button lights.
     *
     * @param brightness The desired brightness.
     */
    fun setBrightness(brightness: Brightness)

    /**
     * Sets the current write and visible buffers.
     *
     * @param visibleBuffer The buffer to display
     * @param writeBuffer The buffer to which the commands are applied.
     * @param copyVisibleBufferToWriteBuffer Tells if the visible buffer state should be copied to the write buffer.
     * This operation occurs after the new write and visible buffers have been set.
     * @param autoSwap Set to `true` to make the visible buffer quickly swap between the two buffers. This can be
     * used to create a blinking effect. Set back to `false` to stop the blinking.
     */
    fun setBuffers(
        visibleBuffer: Buffer,
        writeBuffer: Buffer,
        copyVisibleBufferToWriteBuffer: Boolean,
        autoSwap: Boolean
    )

    /**
     * Starts scrolling a text across the Launchpad, using the 8x8 pad grid as a font grid. Beware, this operation may
     * be blocking !
     *
     * After the text has been displayed (or after each loop if `loop` is set to `true`), the listener's
     * [net.thecodersbreakfast.lp4j.api.LaunchpadListener.onTextScrolled] is notified.
     *
     * @param text The text to display.
     * @param color The color to use
     * @param speed The speed to use
     * @param loop Tells if the text should loop endlessly. In that case, set to `false` to stop the scrolling.
     * @param operation What to do on the backbuffer
     */
    fun scrollText(
        text: String,
        color: Color,
        speed: ScrollSpeed,
        loop: Boolean,
        operation: BackBufferOperation
    )
}