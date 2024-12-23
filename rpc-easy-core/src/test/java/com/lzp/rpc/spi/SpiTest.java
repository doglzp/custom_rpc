package com.lzp.rpc.spi;


import cn.hutool.core.io.resource.ResourceUtil;
import com.lzp.rpc.serializer.Serializer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class SpiTest {

    @Test
    public void test() throws Exception {
        List<URL> resources = ResourceUtil.getResources("META-INF/rpc/system/" + Serializer.class.getName());
        for (URL resource : resources) {
            InputStreamReader reader = new InputStreamReader(resource.openStream());
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null){
                String[] split = line.split("=");
                String key = split[0];
                String value = split[1];
                System.out.println(key + ":" + value);
            }
        }
    }

}
