package com.metaring.framework.rpc;

import com.metaring.framework.Tools;
import com.metaring.framework.functionality.FunctionalityExecutionResult;
import com.metaring.framework.type.DataRepresentation;
import com.metaring.framework.GeneratedCoreType;

public class RpcResponse implements GeneratedCoreType {

    public static final String FULLY_QUALIFIED_NAME = "com.metaring.framework.rpc.rpcResponse";

    private Long id;
    private FunctionalityExecutionResult result;

    private RpcResponse(Long id, FunctionalityExecutionResult result) {
        this.id = id;
        this.result = result;
    }

    public Long getId() {
        return this.id;
    }

    public FunctionalityExecutionResult getResult() {
        return this.result;
    }

    public static RpcResponse create(Long id, FunctionalityExecutionResult result) {
        return new RpcResponse(id, result);
    }

    public static RpcResponse fromJson(String jsonString) {

        if(jsonString == null) {
            return null;
        }

        jsonString = jsonString.trim();
        if(jsonString.isEmpty()) {
            return null;
        }

        if(jsonString.equalsIgnoreCase("null")) {
            return null;
        }

        DataRepresentation dataRepresentation = Tools.FACTORY_DATA_REPRESENTATION.fromJson(jsonString);

        Long id = null;
        if(dataRepresentation.hasProperty("id")) {
            try {
                id = dataRepresentation.getDigit("id");
            } catch (Exception e) {
            }
        }

        FunctionalityExecutionResult result = null;
        if(dataRepresentation.hasProperty("result")) {
            try {
                result = dataRepresentation.get("result", FunctionalityExecutionResult.class);
            } catch (Exception e) {
            }
        }

        RpcResponse rpcResponse = create(id, result);
        return rpcResponse;
    }

    public DataRepresentation toDataRepresentation() {
        DataRepresentation dataRepresentation = Tools.FACTORY_DATA_REPRESENTATION.create();
        if (id != null) {
            dataRepresentation.add("id", id);
        }

        if (result != null) {
            dataRepresentation.add("result", result);
        }

        return dataRepresentation;
    }

    @Override
    public String toJson() {
        return toDataRepresentation().toJson();
    }

    @Override
    public String toString() {
        return this.toJson();
    }
}