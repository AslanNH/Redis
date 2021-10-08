package com.nh.redis.config;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author nihh
 * @Date 2021/9/30 9:59
 * @Version 1.0
 **/

public class Expire {

    private long time;

    private TimeUnit timeUnit;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
