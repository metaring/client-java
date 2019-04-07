package com.metaring.framework.rpc.auth;

import java.util.concurrent.CompletableFuture;

import com.metaring.framework.SysKB;
import com.metaring.framework.functionality.AbstractFunctionality;
import com.metaring.framework.functionality.GeneratedFunctionality;
import com.metaring.framework.rpc.RpcRequest;
import com.metaring.framework.rpc.RpcResponse;

public abstract class CallReservedFunctionality extends AbstractFunctionality implements GeneratedFunctionality {

    protected CallReservedFunctionality(SysKB sysKB) {
        super(sysKB, AuthFunctionalitiesManager.CALL_RESERVED, RpcResponse.class);
    }

    @Override
    protected final CompletableFuture<Void> beforePreConditionCheck(Object input) throws Exception {
        CompletableFuture<Void> response = beforePreConditionCheck(input == null ? null : (RpcRequest) input);
        return response == null ? end : response;
    }

    protected CompletableFuture<Void> beforePreConditionCheck(RpcRequest input) throws Exception {
        return end;
    }

    @Override
    protected final CompletableFuture<Void> preConditionCheck(Object input) throws Exception {
        CompletableFuture<Void> response = preConditionCheck(input == null ? null : (RpcRequest) input);
        return response == null ? end : response;
    }

    protected abstract CompletableFuture<Void> preConditionCheck(RpcRequest input) throws Exception;

    @Override
    protected final CompletableFuture<Void> afterPreConditionCheck(Object input) throws Exception {
        CompletableFuture<Void> response = afterPreConditionCheck(input == null ? null : (RpcRequest) input);
        return response == null ? end : response;
    }

    protected CompletableFuture<Void> afterPreConditionCheck(RpcRequest input) throws Exception {
        return end;
    }

    @Override
    protected final CompletableFuture<Void> beforeCall(Object input) throws Exception {
        CompletableFuture<Void> response = beforeCall(input == null ? null : (RpcRequest) input);
        return response == null ? end : response;
    }

    protected CompletableFuture<Void> beforeCall(RpcRequest input) throws Exception {
        return end;
    }

    @Override
    protected final CompletableFuture<Object> call(Object input) throws Exception {
        CompletableFuture<RpcResponse> call = call((RpcRequest) input);
        if(call == null) {
            return end(null);
        }
        final CompletableFuture<Object> response = new CompletableFuture<>();
        call.handleAsync((result, error) -> {
            if(error != null) {
                response.completeExceptionally(error);
            } else {
                response.complete(result);
            }
            return null;
        }, EXECUTOR);
        return response;
    }

    protected abstract CompletableFuture<RpcResponse> call(RpcRequest input) throws Exception;

    @Override
    protected final CompletableFuture<Void> afterCall(Object input, Object output) throws Exception {
        CompletableFuture<Void> response = afterCall(input == null ? null : (RpcRequest) input, output == null ? null : (RpcResponse) output);
        return response == null ? end : response;
    }

    protected CompletableFuture<Void> afterCall(RpcRequest input, RpcResponse output) throws Exception{
        return end;
    }

    @Override
    protected final CompletableFuture<Void> beforePostConditionCheck(Object input, Object output) throws Exception {
        CompletableFuture<Void> response = beforePostConditionCheck(input == null ? null : (RpcRequest) input, output == null ? null : (RpcResponse) output);
        return response == null ? end : response;
    }

    protected CompletableFuture<Void> beforePostConditionCheck(RpcRequest input, RpcResponse output) throws Exception {
        return end;
    }

    @Override
    protected final CompletableFuture<Void> postConditionCheck(Object input, Object output) throws Exception {
        CompletableFuture<Void> response = postConditionCheck(input == null ? null : (RpcRequest) input, output == null ? null : (RpcResponse) output);
        return response == null ? end : response;
    }

    protected abstract CompletableFuture<Void> postConditionCheck(RpcRequest input, RpcResponse output) throws Exception;

    @Override
    protected final CompletableFuture<Void> afterPostConditionCheck(Object input, Object output) throws Exception {
        CompletableFuture<Void> response = afterPostConditionCheck(input == null ? null : (RpcRequest) input, output == null ? null : (RpcResponse) output);
        return response == null ? end : response;
    }

    protected CompletableFuture<Void> afterPostConditionCheck(RpcRequest input, RpcResponse output) throws Exception {
        return end;
    }

    protected static CallReservedFunctionality create(SysKB sysKB) {
        return new CallReservedFunctionalityImpl(sysKB);
    }
}