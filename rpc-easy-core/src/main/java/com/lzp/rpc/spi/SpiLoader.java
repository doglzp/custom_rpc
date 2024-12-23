package com.lzp.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.lzp.rpc.serializer.JdkSerializer;
import com.lzp.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {

    // 接口名 => (key => 实现类名)
    private static Map<String, Map<String, Class<?>>> loadMap = new ConcurrentHashMap<>();

    // 实现类名 => 实现类实例对象
    private static Map<String, Object> cacheMap = new ConcurrentHashMap<>();

    public static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    public static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    // 系统路径写在前面，先加载系统，这样就能做到优先加载用户自定配置
    public static final String[] SPI_DIRS = {RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    public static <T> T getInstance(Class<T> tClass, String key) {
        Map<String, Class<?>> classMap = loadMap.get(tClass.getName());
        if (classMap == null || classMap.isEmpty()) {
            throw new RuntimeException("No implementation class found for " + tClass.getName());
        }
        if (!classMap.containsKey(key)){
            throw new RuntimeException("No implementation class found for " + tClass.getName());
        }
        Class<?> aClass = classMap.get(key);
        if (!cacheMap.containsKey(aClass.getName())){
            try {
                cacheMap.put(aClass.getName(), aClass.newInstance());
            } catch (Exception e) {
                log.error("Failed to create instance for {}", tClass.getName(), e);
                throw new RuntimeException(e);
            }
        }
        return (T) cacheMap.get(aClass.getName());
    }

    public static Map<String, Class<?>> load(Class<?> iClass) {
        Map<String, Class<?>> map = new ConcurrentHashMap<>();
        for (String spiDir : SPI_DIRS) {
            List<URL> resources = ResourceUtil.getResources(spiDir + iClass.getName());
            for (URL resource : resources) {
                try {
                    InputStreamReader reader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        String[] split = line.split("=");
                        String key = split[0];
                        String value = split[1];
                        map.put(key, Class.forName(value));
                    }
                } catch (Exception e) {
                    log.error("Failed to load spi file", e);
                    throw new RuntimeException(e);
                }
            }
        }
        loadMap.put(iClass.getName(), map);
        return map;
    }

}
