package com.lzp.rpc.registry;

import cn.hutool.json.JSONUtil;
import com.lzp.rpc.config.RegistryConfig;
import com.lzp.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {

    private volatile Client client;
    private volatile KV kvClient;

    public static final String ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        if (client == null) {
            synchronized (EtcdRegistry.class) {
                if (client == null) {
                    client = Client.builder()
                            .endpoints(registryConfig.getAddress())
                            .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                            .build();
                    kvClient = client.getKVClient();
                }
            }
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String serviceKey = ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(serviceKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        Lease leaseClient = client.getLeaseClient();
        long leaseId = leaseClient.grant(30).get().getID();
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();

        kvClient.put(key, value, putOption).get();
    }

    @Override
    public List<ServiceMetaInfo> discovery(String serviceKey) {
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        try {
            GetResponse response = kvClient.get(ByteSequence.from(serviceKey, StandardCharsets.UTF_8), getOption).get();
            return response.getKvs().stream()
                    .map(kv -> JSONUtil.toBean(kv.getValue().toString(StandardCharsets.UTF_8), ServiceMetaInfo.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        kvClient.delete(ByteSequence.from(ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
    }


    @Override
    public void destroy() {
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
