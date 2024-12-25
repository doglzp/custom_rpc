package com.lzp.rpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import com.lzp.rpc.anno.Prefix;
import com.lzp.rpc.constants.FileFormatConstant;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;

public class ConfigUtils {

    public static <T> T getConfig(String prefix, Class<T> clazz) {
        return getConfig(prefix, clazz, null, FileFormatConstant.YAML);
    }

    public static <T> T getProConfig(String prefix, Class<T> clazz, String environment) {
        StringBuilder configFileName = new StringBuilder("application");
        if (StrUtil.isNotEmpty(environment)) {
            configFileName.append("-").append(environment);
        }
        Props props = new Props(configFileName.append(".").append(FileFormatConstant.PROPERTIES).toString());
        props.autoLoad(true);
        return props.toBean(clazz, prefix);
    }

    public static <T> T getYmlConfig(String prefix, Class<T> clazz, String environment, String fileFormat) {
        StringBuilder configFileName = new StringBuilder("application");
        if (StrUtil.isNotEmpty(environment)) {
            configFileName.append("-").append(environment);
        }
        try {
            Resource resource = ResourceUtil.getResourceObj(configFileName.append(".").append(fileFormat).toString());
            Dict dict = YamlUtil.load(resource.getReader(Charset.defaultCharset()));
            T config = BeanUtil.toBean(dict.getBean(prefix), clazz);

            Map<String, Object> configMap = (Map<String, Object>) dict.get(prefix);
            setFields(config,configMap);
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getConfig(String prefix, Class<T> clazz, String environment, String fileFormat) {
        if (StrUtil.isEmpty(fileFormat)) {
            fileFormat = FileFormatConstant.YAML;
        }
        switch (fileFormat) {
            case FileFormatConstant.YAML:
            case FileFormatConstant.YML:
                return getYmlConfig(prefix, clazz, environment, fileFormat);
            case FileFormatConstant.PROPERTIES:
                return getProConfig(prefix, clazz, environment);
            default:
                throw new NoResourceException("file format not support");
        }
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
                            if (value instanceof Map) {
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
