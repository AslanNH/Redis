package com.nh.redis.queue;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nh.redis.config.RedisUtil;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

/**
 * @Description
 * @Author nihh
 * @Date 2021/9/30 16:09
 * @Version 1.0
 **/
public class RedisSortQueue<T> {

    static class Task<T> {
        public String id;
        public T msg;
    }

    // fastJson 序列化对象 存在 generic 类型 ，需要使用 TypeReference
    // 使用TypeReference可以明确的指定反序列化的类型
    private Type TaskType = new TypeReference<Task<T>>() {
    }.getType();

    private String queueKey;

    private RedisUtil redisUtil;

    public RedisSortQueue(String queueKey,RedisUtil redisUtil) {
        this.queueKey = queueKey;
        this.redisUtil = redisUtil;
    }

    public void delay(T msg) {
        Task<T> task = new Task<>();
        task.id = UUID.randomUUID().toString();
        task.msg = msg;
        String s = JSON.toJSONString(task);
        redisUtil.zSetAdd(queueKey, s, System.currentTimeMillis());
    }

    public void loop() {
        while (!Thread.interrupted()) {
            Set<Object> values = redisUtil.zSetRangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            if (values == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            if(values.iterator().hasNext()) {
                String s = (String) values.iterator().next();
                if (redisUtil.zSetRem(queueKey, s) > 0) {
                    Task<T> task = JSON.parseObject(s, Task.class);
                    System.out.println(task.msg);
                }
            }
        }
    }

    public long size(){
        long i = 0;
        try {
            i = redisUtil.zSetSize(queueKey);
        }catch (Exception e){
            i = 0;
        }
        return i;
    }
}
