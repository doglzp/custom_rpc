package com.lzp.rpc.enums;

public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEARTBEAT(2),
    OTHERS(3),

    ;

    private final int key;

    public int getKey() {
        return key;
    }

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
            if (anEnum.getKey() == key) {
                return anEnum;
            }
        }
        return null;
    }
}
