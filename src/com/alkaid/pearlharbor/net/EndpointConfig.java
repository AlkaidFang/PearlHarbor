/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * this class is used for config endpoint interfaced and anonotated class.
 * it is used specially for endpoint subclass.
 * also can view: http://m.blog.csdn.net/blog/deepbluecn/25798679
 */

package com.alkaid.pearlharbor.net;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

public class EndpointConfig implements ServerApplicationConfig {

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(
            Set<Class<? extends Endpoint>> scanned) {

        Set<ServerEndpointConfig> result = new HashSet<>();

        // Endpoint subclass config

        if (scanned.contains(MyEndpoint.class)) {
            result.add(ServerEndpointConfig.Builder.create(
            		MyEndpoint.class,
                    "/MyEndpoint").build());
        }
        
        if (scanned.contains(GameEndpoint.class)) {
            result.add(ServerEndpointConfig.Builder.create(
            		GameEndpoint.class,
                    "/Game").build());
        }

        return result;
    }


    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // Deploy all WebSocket endpoints defined by annotations in the examples
        // web application. Filter out all others to avoid issues when running
        // tests on Gump
    	

        // Annotated config
    	
        Set<Class<?>> results = new HashSet<>();
        for (Class<?> clazz : scanned) {
            if (clazz.getPackage().getName().startsWith("com。alkaid.pearlharbor.")) {
                results.add(clazz);
            }
        }
        return results;
    }
}
