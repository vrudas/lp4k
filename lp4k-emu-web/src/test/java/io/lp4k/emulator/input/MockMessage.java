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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class MockMessage implements Message<JsonObject> {

    private final JsonObject body;

    public MockMessage() {
        this(emptyMap());
    }

    public MockMessage(Map<String, Object> bodyMap) {
        this.body = new JsonObject(bodyMap);
    }

    @Override
    public String address() {
        return null;
    }

    @Override
    public MultiMap headers() {
        return null;
    }

    @Override
    public JsonObject body() {
        return body;
    }

    @Override
    public String replyAddress() {
        return null;
    }

    @Override
    public boolean isSend() {
        return false;
    }

    @Override
    public void reply(Object message) {

    }

    @Override
    public <R> void reply(Object message, Handler<AsyncResult<Message<R>>> replyHandler) {

    }

    @Override
    public void reply(Object message, DeliveryOptions options) {

    }

    @Override
    public <R> void reply(Object message, DeliveryOptions options, Handler<AsyncResult<Message<R>>> replyHandler) {

    }

    @Override
    public void fail(int failureCode, String message) {

    }
}
