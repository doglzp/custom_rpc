package com.lzp.rpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import com.lzp.rpc.constans.FileFormatConstant;

import java.io.FileReader;
import java.nio.charset.Charset;

public class ConfigUtils {

    public static <T> T getConfig(String prefix, Class<T> clazz) {
        return getConfig(prefix, clazz, null, FileFormatConstant.YML);
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
            return BeanUtil.toBean(dict.getBean(prefix), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getConfig(String prefix, Class<T> clazz, String environment, String fileFormat) {
        if (StrUtil.isEmpty(fileFormat)){
            fileFormat = FileFormatConstant.YML;
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
}
