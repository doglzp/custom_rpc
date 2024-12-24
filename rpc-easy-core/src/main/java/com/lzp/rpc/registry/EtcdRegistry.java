package com.lzp.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.lzp.rpc.config.RegistryConfig;
import com.lzp.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {

    private volatile Client client;

    private volatile KV kvClient;

    public static final String ROOT_PATH = "/rpc/";

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();


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
                    heartBeat();
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
        localRegisterNodeKeySet.add(serviceKey);
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
        String serviceKey = ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(serviceKey, StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(serviceKey);
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

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", (Task) ()->{
            for (String key : localRegisterNodeKeySet) {
                try {
                    List<KeyValue> keyValueList = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                    if (CollUtil.isEmpty(keyValueList)){
                        continue;
                    }
                    String value = keyValueList.get(0).getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("心跳任务执行中...");
            }
        });
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

}
