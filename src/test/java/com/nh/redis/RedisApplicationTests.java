package com.nh.redis;

import com.nh.redis.config.RedisUtil;
import com.nh.redis.lock.RedisReentrantLock;
import com.nh.redis.queue.RedisSortQueue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class RedisApplicationTests {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisReentrantLock redisLock;

    private static final String LOCK = "lock";
    @Test
    void contextLoads() throws Exception{
        redisUtil.set("aaaa",10);
        System.out.println(redisUtil.get("aaaa"));
        redisUtil.set("aaaa",(Integer)redisUtil.get("aaaa")+10);
        System.out.println(redisUtil.get("aaaa"));
    }

    @Test
    void lock() throws Exception{
        redisUtil.flushAll();
        redisUtil.set("aaaa",10);
        LockRunnable runnable = new LockRunnable();
        for(int i=0;i<10;i++) {
            es.execute(runnable);
        }
        cd.await();
        System.out.println(redisUtil.get("aaaa"));
    }
    class LockRunnable implements Runnable{
        @Override
        public void run() {
            redisLock.lock(LOCK);
            redisUtil.set("aaaa",(Integer)redisUtil.get("aaaa")+10);
            cd.countDown();
            redisLock.unlock(LOCK);
        }
    }

    @Test
    public void testSetNx()throws Exception{
        redisUtil.flushAll();
        redisUtil.setNx("123","456",2000);
        System.out.println(redisUtil.get("123"));
        System.out.println(redisUtil.setNx("123","789",2000));
        Thread.sleep(3000);
        System.out.println(redisUtil.setNx("123","789",2000));
        System.out.println(redisUtil.get("123"));
    }


    ExecutorService es = Executors.newFixedThreadPool(10);
    CountDownLatch cd = new CountDownLatch(10);
    public static long getRandom(){
        return (int)(Math.random()*100);
    }

    @Test
    public void delayQueueTest()throws Exception{
        redisUtil.flushAll();
        RedisSortQueue delayQueue = new RedisSortQueue("delayQueue",redisUtil);
        Runnable porducerRunnable  = new Runnable(){
            @Override
            public void run() {
                delayQueue.delay("测试消息"+System.currentTimeMillis());
            }
        };
        Runnable consumerRunnable = new Runnable(){
            @Override
            public void run() {
                delayQueue.loop();
            }
        };
        for(int i = 0;i<10;i++){

            Thread.sleep(getRandom());
            es.execute(porducerRunnable);

        }
        for(int i = 0;i<10;i++){

            Thread.sleep(getRandom());
            System.out.println(delayQueue.size());
            es.execute(consumerRunnable);

        }
        Thread.currentThread().join();
    }

}
