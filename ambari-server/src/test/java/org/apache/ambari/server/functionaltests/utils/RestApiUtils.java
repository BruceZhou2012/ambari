/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ambari.server.functionaltests.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.ambari.server.functionaltests.api.WebRequest;
import org.apache.ambari.server.functionaltests.api.WebResponse;
import org.apache.commons.httpclient.HttpStatus;

import java.io.StringReader;

/**
 * Helper that executes a web request object and evaluates the response code.
 */
public class RestApiUtils {
    /**
     * Executes a web request and throws an exception if the status code is incorrect.
     *
     * @param request
     * @return
     * @throws Exception
     */
    public  static JsonElement executeRequest(WebRequest request) throws Exception {
        WebResponse response = request.getResponse();
        int responseCode = response.getStatusCode();
        String responseBody = response.getContent();

        if (responseCode != HttpStatus.SC_OK && responseCode != HttpStatus.SC_CREATED && responseCode != HttpStatus.SC_ACCEPTED) {
            throw new RuntimeException(String.format("%d:%s", responseCode, responseBody));
        }

        return new JsonParser().parse(new JsonReader(new StringReader(responseBody)));
    }
}