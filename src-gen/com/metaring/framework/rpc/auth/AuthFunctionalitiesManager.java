package com.metaring.framework.rpc.auth;

import java.util.concurrent.CompletableFuture;

import com.metaring.framework.functionality.FunctionalitiesManager;
import com.metaring.framework.functionality.Functionality;
import com.metaring.framework.functionality.FunctionalityInfo;
import com.metaring.framework.functionality.GeneratedFunctionalitiesManager;
import com.metaring.framework.rpc.RpcRequest;
import com.metaring.framework.rpc.RpcResponse;

public class AuthFunctionalitiesManager extends FunctionalitiesManager implements GeneratedFunctionalitiesManager {

    public static final FunctionalityInfo CALL_RESTRICTED = FunctionalityInfo.create("com.metaring.framework.rpc.auth.callRestricted", true, true, false, "com.metaring.framework.rpc.RpcRequest", "com.metaring.framework.rpc.RpcResponse");

    public static final FunctionalityInfo CALL_RESERVED = FunctionalityInfo.create("com.metaring.framework.rpc.auth.callReserved", true, false, false, "com.metaring.framework.rpc.RpcRequest", "com.metaring.framework.rpc.RpcResponse");

    public static final CompletableFuture<RpcResponse> callRestricted(RpcRequest rpcRequest) {
        return call(CALL_RESTRICTED, CallRestrictedFunctionality.class, getCallingFunctionality(), rpcRequest, result -> result.as(RpcResponse.class));
    }

    public static final CompletableFuture<RpcResponse> callRestricted(Functionality functionality, RpcRequest rpcRequest) {
        return call(CALL_RESTRICTED, CallRestrictedFunctionality.class, functionality, rpcRequest, result -> result.as(RpcResponse.class));
    }

    public static final CompletableFuture<RpcResponse> callRestrictedFromJson(String rpcRequestJson) {
        return callFromJson(CALL_RESTRICTED, CallRestrictedFunctionality.class, getCallingFunctionality(), rpcRequestJson, result -> result.as(RpcResponse.class));
    }

    public static final CompletableFuture<RpcResponse> callRestrictedFromJson(Functionality callingFunctionality, String rpcRequestJson) {
        return callFromJson(CALL_RESTRICTED, CallRestrictedFunctionality.class, callingFunctionality, rpcRequestJson, result -> result.as(RpcResponse.class));
    }

    public static final CompletableFuture<RpcResponse> callReserved(RpcRequest rpcRequest) {
        return call(CALL_RESERVED, CallReservedFunctionality.class, getCallingFunctionality(), rpcRequest, result -> result.as(RpcResponse.class));
    }

    public static final CompletableFuture<RpcResponse> callReserved(Functionality functionality, RpcRequest rpcRequest) {
        return call(CALL_RESERVED, CallReservedFunctionality.class, functionality, rpcRequest, result -> result.as(RpcResponse.class));
    }

    public static final CompletableFuture<RpcResponse> callReservedFromJson(String rpcRequestJson) {
        return callFromJson(CALL_RESERVED, CallReservedFunctionality.class, getCallingFunctionality(), rpcRequestJson, result -> result.as(RpcResponse.class));
    }

    public static final CompletableFuture<RpcResponse> callReservedFromJson(Functionality callingFunctionality, String rpcRequestJson) {
        return callFromJson(CALL_RESERVED, CallReservedFunctionality.class, callingFunctionality, rpcRequestJson, result -> result.as(RpcResponse.class));
    }

}
