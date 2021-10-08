package com.nh.redis.entity;

/**
 * @Description
 * @Author nihh
 * @Date 2021/9/30 14:02
 * @Version 1.0
 **/
public class Student {
    private String age;
    private String name;
    private String address;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Student{" +
                "age='" + age + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
