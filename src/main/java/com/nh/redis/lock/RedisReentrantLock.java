package com.nh.redis.lock;

import com.nh.redis.config.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author nihh
 * @Date 2021/9/30 14:49
 * @Version 1.0
 **/
@Component
public class RedisReentrantLock {

    @Autowired
    private RedisUtil redisUtil;

    ThreadLocal<Map<String,Integer>> localers = new ThreadLocal<>();
    private static final long EXPIRE_TIME = 1000L;

    private boolean _lock(String key){

        return redisUtil.setNx(key,"",EXPIRE_TIME);
    }

    private  void _unlock(String key){
        redisUtil.del(key);
    }

    private Map<String,Integer> getCurrentLocks(){
        Map<String,Integer> currentLocks =  localers.get();
        if(currentLocks==null){
            localers.set( new HashMap<>());
        }
        return localers.get();
    }

    public boolean lock(String key){
        Map<String,Integer> currentLocks = getCurrentLocks();
        Integer locCount = currentLocks.get(key);
        if(locCount!=null){
            locCount+=1;
            return true;
        }
        while(!_lock(key)){
            try {
                Thread.sleep(5);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        currentLocks.put(key,1);
        return true;
    }

    public boolean unlock(String key){
        Map<String,Integer> currentLocks = getCurrentLocks();
        Integer locCount = currentLocks.get(key);
        if(locCount==null){
            return false;
        }
        locCount-=1;
        if(locCount>0){
           currentLocks.put(key,locCount);
        }else{
            currentLocks.remove(key);
            _unlock(key);
        }
        return true;
    }
}
