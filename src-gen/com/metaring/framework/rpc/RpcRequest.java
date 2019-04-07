package com.metaring.framework.rpc;

import com.metaring.framework.Tools;
import com.metaring.framework.type.DataRepresentation;
import com.metaring.framework.GeneratedCoreType;

public class RpcRequest implements GeneratedCoreType {

    public static final String FULLY_QUALIFIED_NAME = "com.metaring.framework.rpc.rpcRequest";

    private Long id;
    private DataRepresentation data;
    private String name;
    private DataRepresentation param;

    private RpcRequest(Long id, DataRepresentation data, String name, DataRepresentation param) {
        this.id = id;
        this.data = data;
        this.name = name;
        this.param = param;
    }

    public Long getId() {
        return this.id;
    }

    public DataRepresentation getData() {
        return this.data;
    }

    public String getName() {
        return this.name;
    }

    public DataRepresentation getParam() {
        return this.param;
    }

    public static RpcRequest create(Long id, DataRepresentation data, String name, DataRepresentation param) {
        return new RpcRequest(id, data, name, param);
    }

    public static RpcRequest fromJson(String jsonString) {

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

        DataRepresentation data = null;
        if(dataRepresentation.hasProperty("data")) {
            try {
                data = dataRepresentation.get("data");
            } catch (Exception e) {
            }
        }

        String name = null;
        if(dataRepresentation.hasProperty("name")) {
            try {
                name = dataRepresentation.getText("name");
            } catch (Exception e) {
            }
        }

        DataRepresentation param = null;
        if(dataRepresentation.hasProperty("param")) {
            try {
                param = dataRepresentation.get("param");
            } catch (Exception e) {
            }
        }

        RpcRequest rpcRequest = create(id, data, name, param);
        return rpcRequest;
    }

    public DataRepresentation toDataRepresentation() {
        DataRepresentation dataRepresentation = Tools.FACTORY_DATA_REPRESENTATION.create();
        if (id != null) {
            dataRepresentation.add("id", id);
        }

        if (data != null) {
            dataRepresentation.add("data", data);
        }

        if (name != null) {
            dataRepresentation.add("name", name);
        }

        if (param != null) {
            dataRepresentation.add("param", param);
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