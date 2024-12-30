package com.lzp.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRegisterInfo<T> {

    private String serviceName;

    private Class<? extends T> interfaceClass;
}
