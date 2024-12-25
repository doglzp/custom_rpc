package com.lzp.rpc.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProtocolMessageSerializerEnum {

    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian"),

    ;
    private final int key;

    private final String value;

    public int getKey() {
        return key;
    }


    public String getValue() {
        return value;
    }

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static List<String> getValues() {
        return Arrays.stream(ProtocolMessageSerializerEnum.values())
                .map(ProtocolMessageSerializerEnum::getValue)
                .collect(Collectors.toList());
    }

    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.getKey() == key) {
                return anEnum;
            }
        }
        return null;
    }

    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.getValue().equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
