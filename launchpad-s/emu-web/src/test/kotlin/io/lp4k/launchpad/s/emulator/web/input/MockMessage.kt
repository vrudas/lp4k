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
package io.lp4k.launchpad.s.emulator.web.input

import io.vertx.core.Future
import io.vertx.core.MultiMap
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

class MockMessage (
    bodyMap: Map<String, Any?> = emptyMap()
) : Message<JsonObject> {

    private val body: JsonObject = JsonObject(bodyMap)

    override fun address(): String? = null

    override fun headers(): MultiMap? = null

    override fun body(): JsonObject = body

    override fun replyAddress(): String? = null

    override fun isSend(): Boolean = false

    override fun reply(message: Any) = Unit

    override fun reply(message: Any, options: DeliveryOptions) = Unit

    override fun fail(failureCode: Int, message: String) = Unit

    override fun <R : Any?> replyAndRequest(message: Any?, options: DeliveryOptions?): Future<Message<R>> {
        return Future.succeededFuture<Message<R>>()
    }

}
