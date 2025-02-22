/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.skywalking.apm.plugin.spring.cloud.gateway.v412x.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesNoArguments;
import static org.apache.skywalking.apm.agent.core.plugin.bytebuddy.ArgumentTypeNameMatch.takesArgumentWithType;
import static org.apache.skywalking.apm.agent.core.plugin.match.NameMatch.byName;

public class HttpClientConnectV412Instrumentation extends AbstractGatewayV412EnhancePluginDefine {

    private static final String ENHANCE_CLASS = "reactor.netty.http.client.HttpClientConnect";
    private static final String HTTP_CLIENT_CONNECT_DUPLICATE_INTERCEPTOR = "org.apache.skywalking.apm.plugin.spring.cloud.gateway.v412x.HttpClientConnectDuplicateV412Interceptor";
    private static final String HTTP_CLIENT_CONNECT_REQUEST_INTERCEPTOR = "org.apache.skywalking.apm.plugin.spring.cloud.gateway.v412x.HttpClientConnectRequestV412Interceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return byName(ENHANCE_CLASS);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("duplicate").and(takesNoArguments())
                                .and(returns(named("reactor.netty.http.client.HttpClient")));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return HTTP_CLIENT_CONNECT_DUPLICATE_INTERCEPTOR;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                },
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("request").and(takesArgumentWithType(0, "io.netty.handler.codec.http.HttpMethod"))
                                        .and(returns(named("reactor.netty.http.client.HttpClient$RequestSender")));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return HTTP_CLIENT_CONNECT_REQUEST_INTERCEPTOR;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }
}
