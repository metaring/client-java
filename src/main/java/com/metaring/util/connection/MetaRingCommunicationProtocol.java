/**
 *    Copyright 2019 MetaRing s.r.l.
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
 */

package com.metaring.util.connection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.metaring.framework.CoreType;
import com.metaring.framework.Tools;
import com.metaring.framework.functionality.FunctionalityExecutionResult;
import com.metaring.framework.functionality.FunctionalityInfo;
import com.metaring.framework.rpc.RpcRequest;
import com.metaring.framework.rpc.RpcResponse;
import com.metaring.framework.rpc.auth.AuthFunctionalitiesManager;
import com.metaring.framework.type.DataRepresentation;
import com.metaring.framework.type.factory.DataRepresentationFactory;
import com.metaring.framework.util.StringUtil;

public final class MetaRingCommunicationProtocol {

    private static final String CALL_RESERVED = AuthFunctionalitiesManager.CALL_RESERVED.getFunctionalityFullyQualifiedName();
    private static final String CALL_RESTRICTED = AuthFunctionalitiesManager.CALL_RESTRICTED.getFunctionalityFullyQualifiedName();

    private final SockJSClient sockJSClient;
    private final Supplier<DataRepresentation> getIdentificationData;
    private final Supplier<DataRepresentation> getEnableData;

    private final Map<Long, CompletableFuture<FunctionalityExecutionResult>> sockJSCalls = new HashMap<>();
    private final Map<String, Consumer<FunctionalityExecutionResult>> permanentSockJSCallbacks = new HashMap<>();

    private Runnable onConnect;
    private Consumer<Throwable> onDisconnect;

    public final String IP;

    private MetaRingCommunicationProtocol(String url, String sockResource, String ipResource, Supplier<DataRepresentation> getIdentificationData, Supplier<DataRepresentation> getEnableData) {
        if(!url.endsWith("/")) {
            url += "/";
        }
        String ip = null;
        if(!StringUtil.isNullOrEmpty(ipResource)) {
            ip = retrieveClientIp(url, ipResource);
        }
        this.IP = ip;
        this.sockJSClient = SockJSClient.create(url + sockResource).onOpen(this::connected).onMessage(this::consumeResponse).onError(this::onError);
        this.getIdentificationData = getIdentificationData;
        this.getEnableData = getEnableData;
    }

    private final String retrieveClientIp(String urlString, String ipResource) {
        String ip = ipResource;
        try {
            String connection = urlString + ipResource;
            URL url = new URL(connection);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            try(InputStream inputStream = urlConnection.getInputStream()) {
                try(InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                    try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        ip = bufferedReader.readLine().trim();
                    }
                }
            }
        } catch(Exception e) {
        }
        return ip;
    }

    public final MetaRingCommunicationProtocol connect() {
        this.sockJSClient.connect();
        return this;
    }

    public final void disconnect() {
        this.sockJSClient.disconnect();
    }

    public final MetaRingCommunicationProtocol onConnect(Runnable onConnect) {
        this.onConnect = onConnect;
        return this;
    }

    public final MetaRingCommunicationProtocol onDisconnect(Consumer<Throwable> onDisconnect) {
        this.onDisconnect = onDisconnect;
        return this;
    }

    private final void connected() {
        if(onConnect != null) {
            onConnect.run();
        }
    }

    private final void onError(Throwable e) {
        if(onDisconnect != null) {
            onDisconnect.accept(e);
        }
    }

    public static final MetaRingCommunicationProtocol create(String url, String sockResource, String ipResource, Supplier<DataRepresentation> getIdentificationData, Supplier<DataRepresentation> getEnableData) {
        return new MetaRingCommunicationProtocol(url, sockResource, ipResource, getIdentificationData, getEnableData);
    }

    public static final MetaRingCommunicationProtocol create(String url, String sockResource, String ipResource, Supplier<DataRepresentation> getIdentificationData) {
        return create(url, sockResource, ipResource, getIdentificationData, null);
    }

    public static final MetaRingCommunicationProtocol create(String url, String sockResource, String ipResource) {
        return create(url, sockResource, ipResource, null, null);
    }

    public static final MetaRingCommunicationProtocol create(String url, String sockResource) {
        return create(url, sockResource, "127.0.0.1", null, null);
    }

    private final CompletableFuture<FunctionalityExecutionResult> call(FunctionalityInfo functionalityInfo, DataRepresentation functionalityParam) {
        CompletableFuture<FunctionalityExecutionResult> future = new CompletableFuture<>();
        RpcRequest rpcRequest = encapsulate(functionalityInfo, functionalityParam);
        sockJSCalls.put(rpcRequest.getId(), future);
        sockJSClient.send(rpcRequest.toJson());
        return future;
    }

    public final CompletableFuture<FunctionalityExecutionResult> call(FunctionalityInfo functionalityInfo, String param) {
        return call(functionalityInfo, Tools.FACTORY_DATA_REPRESENTATION.fromObject(param));
    }

    public final CompletableFuture<FunctionalityExecutionResult> call(FunctionalityInfo functionalityInfo, Long param) {
        return call(functionalityInfo, Tools.FACTORY_DATA_REPRESENTATION.fromObject(param));
    }

    public final CompletableFuture<FunctionalityExecutionResult> call(FunctionalityInfo functionalityInfo, Double param) {
        return call(functionalityInfo, Tools.FACTORY_DATA_REPRESENTATION.fromObject(param));
    }

    public final CompletableFuture<FunctionalityExecutionResult> call(FunctionalityInfo functionalityInfo, Boolean param) {
        return call(functionalityInfo, Tools.FACTORY_DATA_REPRESENTATION.fromObject(param));
    }

    public final CompletableFuture<FunctionalityExecutionResult> call(FunctionalityInfo functionalityInfo, CoreType param) {
        return call(functionalityInfo, Tools.FACTORY_DATA_REPRESENTATION.fromObject(param));
    }

    public final CompletableFuture<FunctionalityExecutionResult> call(FunctionalityInfo functionalityInfo) {
        return call(functionalityInfo, (DataRepresentation) null);
    }

    public final MetaRingCommunicationProtocol callback(String name, Consumer<FunctionalityExecutionResult> callback) {
        permanentSockJSCallbacks.put(name, callback);
        return this;
    }

    public final RpcRequest encapsulate(FunctionalityInfo functionalityInfo) {
        return encapsulate(functionalityInfo, null);
    }

    public final RpcRequest encapsulate(FunctionalityInfo functionalityInfo, DataRepresentation functionalityParam) {
        DataRepresentationFactory dataRepresentationFactory = Tools.FACTORY_DATA_REPRESENTATION;
        DataRepresentation request = dataRepresentationFactory.create().add("name", functionalityInfo.getFunctionalityFullyQualifiedName());
        if(functionalityParam != null) {
            request.add("param", functionalityParam);
        }
        if(functionalityInfo.isRestricted()) {
            request = dataRepresentationFactory
                    .create()
                    .add("name", CALL_RESTRICTED)
                    .add("param", request.add("data", getEnableData.get()));
        }
        if(functionalityInfo.isReserved() || functionalityInfo.isRestricted()) {
            request = dataRepresentationFactory
                    .create()
                    .add("name", CALL_RESERVED)
                    .add("param", request.add("data", getIdentificationData.get()));
        }
        long id = System.currentTimeMillis();
        while(sockJSCalls.containsKey(id)) {
            id = System.currentTimeMillis();
        }
        request.add("id", id);
        return request.as(RpcRequest.class);
    }

    private final void consumeResponse(String json) {
        DataRepresentation response = Tools.FACTORY_DATA_REPRESENTATION.fromJson(json);
        CompletableFuture<FunctionalityExecutionResult> callback = null;
        RpcResponse rpcResponse = response.as(RpcResponse.class);
        if(rpcResponse.getId() != null && rpcResponse.getId() > 0) {
            callback = sockJSCalls.remove(rpcResponse.getId());
            if(callback != null) {
                callback.complete(rpcResponse.getResult());
            }
            if(response.hasProperty("data") && !response.isNull("data") && response.get("data").hasProperty("oneTimeToken") && permanentSockJSCallbacks.containsKey("oneTimeToken")) {
                response
                .add("response", "SUCCESS")
                .add("result", response.get("data").getText("oneTimeToken"));
                permanentSockJSCallbacks.get("oneTimeToken").accept(response.as(FunctionalityExecutionResult.class));
            }
            return;
        }
        Consumer<FunctionalityExecutionResult> permanentCallback = null;
        response
            .add("response", "SUCCESS")
            .add("result", response.get("payload"));
        permanentCallback = permanentSockJSCallbacks.get(response.getText("topic"));
        if(permanentCallback != null) {
            permanentCallback.accept(response.as(FunctionalityExecutionResult.class));
        }
    }
}