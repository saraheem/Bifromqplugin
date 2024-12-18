/*
 * Copyright (c) 2024. The BifroMQ Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.hyperdata.bifromq.plugin;

import com.baidu.bifromq.plugin.authprovider.IAuthProvider;
import com.baidu.bifromq.plugin.authprovider.type.CheckResult;
import com.baidu.bifromq.plugin.authprovider.type.Failed;
import com.baidu.bifromq.plugin.authprovider.type.Granted;
import com.baidu.bifromq.plugin.authprovider.type.MQTT3AuthData;
import com.baidu.bifromq.plugin.authprovider.type.MQTT3AuthResult;
import com.baidu.bifromq.plugin.authprovider.type.MQTT5AuthData;
import com.baidu.bifromq.plugin.authprovider.type.MQTT5AuthResult;
import com.baidu.bifromq.plugin.authprovider.type.MQTT5ExtendedAuthData;
import com.baidu.bifromq.plugin.authprovider.type.MQTT5ExtendedAuthResult;
import com.baidu.bifromq.plugin.authprovider.type.MQTTAction;
import com.baidu.bifromq.plugin.authprovider.type.Ok;
import com.baidu.bifromq.plugin.authprovider.type.Reject;
import com.baidu.bifromq.plugin.authprovider.type.Success;
import com.baidu.bifromq.plugin.authprovider.type.Failed.Code;
import com.baidu.bifromq.type.ClientInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Extension
public class HdcBifromqPluginAuthProvider implements IAuthProvider {
    private static final Logger log = LoggerFactory.getLogger(HdcBifromqPluginAuthProvider.class);
    private static final String PLUGIN_AUTHPROVIDER_URL = "plugin.hdcauthprovider.url";
    private URI authServiceUri = null;
    private HttpClient httpClient = null;
    ObjectMapper mapper = null;

    public HdcBifromqPluginAuthProvider(HdcBifromqPluginContext context) {
        log.info("TODO: Initialize your AuthProvider using context: {}", context);

         Properties properties = System.getProperties();
        // Java 8
        properties.forEach((k, v) -> System.out.println(k + ":" + v));

        String webhookUrl = System.getProperty(PLUGIN_AUTHPROVIDER_URL);
        if (webhookUrl == null) {
            log.error("No webhook url specified, the fallback behavior will reject all auth/check requests.");
            
        } else {
            try {
                authServiceUri = URI.create(webhookUrl);
                this.httpClient = HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1)
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();
                
                log.info("HdcBifrommqPluginAuthProviders's webhook URL: {}", webhookUrl);
            } catch (Throwable e) {
                
            }
        }
    }

    @Override
    public CompletableFuture<MQTT3AuthResult> auth(MQTT3AuthData authData) {
        log.info("Mqtt3Auth");
        

        MQTT3AuthResult result = null;
        switch (authData.getUsername()){
                case "saraheem":
                        result = MQTT3AuthResult.newBuilder()
                        .setOk(Ok.newBuilder()
                                .setTenantId("t1")
                                .setUserId("saraheem")
                                .build())
                        .build();

                        break;

                case "mqttrouter":
                        result = MQTT3AuthResult.newBuilder()
                        .setOk(Ok.newBuilder()
                                .setUserId("mqttrouter")
                                .setTenantId("global")
                                .build())
                        .build();

                        break;

                case "araheem":

                        result = MQTT3AuthResult.newBuilder()
                        .setOk(Ok.newBuilder()
                                .setTenantId("t2")
                                .setUserId("araheem")
                                .build())
                        .build();

                        break;

                default:
                        if (httpClient == null){
                                log.error(String.format("httpClient is null, %s not set with Auth Service Url", PLUGIN_AUTHPROVIDER_URL));
                                break;
                        }

                        MqttCredentials credentials = new MqttCredentials(authData.getUsername(), authData.getPassword().toStringUtf8(), authData.getClientId());
                        
                         HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(authServiceUri + "/Auth/Device/MqttAuth"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(credentials.toJson()))
                        .timeout(Duration.ofSeconds(5))
                        .build();

                        CompletableFuture<MQTT3AuthResult> f = 
                                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(response -> {
                                if (response.statusCode() == 200) {
                                        MQTT3AuthResult.Builder resultBuilder = MQTT3AuthResult.newBuilder()
                                        .setOk(Ok.newBuilder()
                                                .setTenantId("global")
                                                .setUserId(authData.getUsername())
                                                );
                                        
                                        return resultBuilder.build();
                                } else {
                                        return MQTT3AuthResult.newBuilder()
                                        .setReject(Reject.newBuilder()
                                                .setCode(Reject.Code.Error)
                                                .setReason("Authenticate failed")
                                                .build())
                                        .build();
                                }
                                })
                                .exceptionally(e -> {
                                log.error("Failed to call webhook: " + e.getMessage());
                                return null;
                                });

                        try{
                                result = f.get();

                        }
                        catch (Exception e){
                                log.error(e.getMessage());

                        }
        
        }
        

        return CompletableFuture.completedFuture(result);
    }


    // Default version is implemented in IAuthrozation Interface, not necessary to implement this
    @Override
    public CompletableFuture<MQTT5AuthResult> auth(MQTT5AuthData authData) {
        log.info("Mqtt5Auth");
        MQTT5AuthResult result = null;
        switch (authData.getUsername()){
                case "saraheem":
                        result = MQTT5AuthResult.newBuilder()
                                .setSuccess(Success.newBuilder()
                                .setTenantId("t1")
                                .setUserId("saraheem")
                                .build())
                        .build();

                        break;

                case "mqttrouter":
                        result = MQTT5AuthResult.newBuilder()
                                .setSuccess(Success.newBuilder()
                                .setUserId("mqttrouter")
                                .build())
                        .build();
        
                        break;

                case "araheem":
                        result = MQTT5AuthResult.newBuilder()
                                .setSuccess(Success.newBuilder()
                                .setUserId("mqttrouter")
                                .build())
                        .build();

                        break;


                default:
                        if (httpClient == null){
                                log.error(String.format("httpClient is null, %s not set with Auth Service Url", PLUGIN_AUTHPROVIDER_URL));
                                break;
                        }
                        MqttCredentials credentials = new MqttCredentials(authData.getUsername(), authData.getPassword().toStringUtf8(), authData.getClientId());

                        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(authServiceUri + "/auth"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(credentials.toJson()))
                        .timeout(Duration.ofSeconds(5))
                        .build();

                        CompletableFuture<MQTT5AuthResult> f =
                                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(response -> {
                                if (response.statusCode() == 200) {
                                        MQTT5AuthResult.Builder resultBuilder = MQTT5AuthResult.newBuilder()
                                        .setSuccess(Success.newBuilder()
                                                .setTenantId("global")
                                                .setUserId(authData.getUsername())
                                                );
                                        
                                        return resultBuilder.build();
                                } else {
                                        MQTT5AuthResult.Builder resultBuilder = MQTT5AuthResult.newBuilder()
                                        .setFailed(Failed.newBuilder()
                                                .setCode(Code.NotAuthorized)
                                                .setReason("Auth failed").build()
                                                );
                                        return resultBuilder.build();
                                        
                                        
                                }
                                })
                                .exceptionally(e -> {
                                System.out.println("Failed to call webhook: " + e.getMessage());
                                return null;
                                });

                        try{
                                result = f.get();

                        }
                        catch (Exception e){

                        }

        
        }
        

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<MQTT5ExtendedAuthResult> extendedAuth(MQTT5ExtendedAuthData authData) {
        log.info("Abdul Raheem");
        return CompletableFuture.completedFuture(MQTT5ExtendedAuthResult.newBuilder()
                .setFailed(Failed.newBuilder()
                        .setCode(Failed.Code.Banned)
                        .setReason("Unimplemented")
                        .build())
                .build());
    }

    @Override
    public CompletableFuture<Boolean> check(ClientInfo client, MQTTAction action) {
        log.info("check");

        return CompletableFuture.completedFuture(true);
        //return CompletableFuture.failedFuture(new UnsupportedOperationException("Unimplemented"));
    }

    @Override
    public CompletableFuture<CheckResult> checkPermission(ClientInfo client, MQTTAction action) {
        log.info("checkPermission");

        //return CompletableFuture.completedFuture(CheckResult.newBuilder()
                
        return CompletableFuture.completedFuture(CheckResult.newBuilder()
                .setGranted(Granted.getDefaultInstance())
                .build());
    }

    
    
}