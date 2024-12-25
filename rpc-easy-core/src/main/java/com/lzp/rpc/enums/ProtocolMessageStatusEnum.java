package com.lzp.rpc.enums;

public enum ProtocolMessageStatusEnum {

    OK("ok", 20),
    BAD_REQUEST("bad request", 40),
    BAD_RESPONSE("bad response", 50),

    ;

    private final String msg;

    private final int code;

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    ProtocolMessageStatusEnum(String msg, int code){
        this.msg = msg;
        this.code = code;
    }

    public static ProtocolMessageStatusEnum getEnumByCode(int code){
        for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum;
            }
        }
        return null;
    }
}
