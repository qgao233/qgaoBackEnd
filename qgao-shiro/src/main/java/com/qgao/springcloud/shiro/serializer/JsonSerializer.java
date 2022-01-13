package com.qgao.springcloud.shiro.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import org.crazycake.shiro.exception.SerializationException;
import org.crazycake.shiro.serializer.RedisSerializer;

public class JsonSerializer implements RedisSerializer<Object> {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private Class clazz;

    public JsonSerializer(Class clazz){
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        } else {
            try {
                return JSON.toJSONString(object, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
            } catch (Exception var3) {
                throw new SerializationException("Could not serialize: " + var3.getMessage(), var3);
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes != null && bytes.length != 0) {
            try {
                String str = new String(bytes, DEFAULT_CHARSET);
                return JSON.parseObject(str, clazz);
            } catch (Exception var3) {
                throw new SerializationException("Could not deserialize: " + var3.getMessage(), var3);
            }
        } else {
            return null;
        }
    }
}
