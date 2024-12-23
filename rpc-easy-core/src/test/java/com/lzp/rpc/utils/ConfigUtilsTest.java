package com.lzp.rpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.lzp.rpc.anno.Prefix;
import com.lzp.rpc.config.RegistryConfig;
import com.lzp.rpc.config.RpcConfig;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;

public class ConfigUtilsTest {

    @Test
    public void test(){
        String environment = null;
        String fileFormat = "yaml";
        String prefix = "rpc";
        Class rpcConfigClass = com.lzp.rpc.config.RpcConfig.class;

        StringBuilder configFileName = new StringBuilder("application");
        if (StrUtil.isNotEmpty(environment)) {
            configFileName.append("-").append(environment);
        }

        Resource resource = ResourceUtil.getResourceObj(configFileName.append(".").append(fileFormat).toString());
        Dict dict = YamlUtil.load(resource.getReader(Charset.defaultCharset()));
        RpcConfig rpcConfig = (RpcConfig)BeanUtil.toBean(dict.getBean(prefix), rpcConfigClass);

        Map<String, Object> rpcConfigMap = (Map<String, Object>) dict.get(prefix);
        setFields(rpcConfig,rpcConfigMap);
        System.out.println(rpcConfig);

    }

    public static void setFields(Object rpcConfig, Map<String, Object> rpcConfigMap) {
        // 获取类的所有字段
        Field[] fields = rpcConfig.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 如果字段上标注了Prefix注解
            if (field.isAnnotationPresent(Prefix.class)) {
                String prefixValue = field.getAnnotation(Prefix.class).value();
                // 如果map中包含该前缀
                if (rpcConfigMap.containsKey(prefixValue)) {
                    field.setAccessible(true);
                    try {
                        Object value = rpcConfigMap.get(prefixValue);
                        Class<?> fieldType = field.getType();
                        // 如果字段类型是基础类型，直接赋值
                        if (isBasicType(fieldType)) {
                            field.set(rpcConfig, BeanUtil.toBean(value, fieldType));
                        } else {
                            // 如果字段类型是一个对象，递归处理
                            Object nestedObject = BeanUtil.toBean(value, fieldType);
                            if (value instanceof Map){
                                Map<String, Object> nestedMap = (Map<String, Object>) value;
                                // 递归处理嵌套对象中的Prefix注解
                                setFields(nestedObject, nestedMap);
                                field.set(rpcConfig, nestedObject);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 判断是否是基础类型
    private static boolean isBasicType(Class<?> fieldType) {
        return fieldType.isPrimitive() || fieldType.equals(String.class) || fieldType.equals(Integer.class)
                || fieldType.equals(Double.class) || fieldType.equals(Boolean.class) || fieldType.equals(Long.class);
    }
}
