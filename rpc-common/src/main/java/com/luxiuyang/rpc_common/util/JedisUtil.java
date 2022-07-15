package com.luxiuyang.rpc_common.util;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Set;

public class JedisUtil {

    private JedisPool pool;

    public JedisUtil(String host, int port){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(2);
        config.setMaxTotal(5);
        pool = new JedisPool(config, host, port);
    }

    private Jedis getJedis(){
        return pool.getResource();
    }

    private void close(Jedis jedis){
        jedis.close();
    }

    public void closePool(){
        pool.close();
    }

    public String get(String key){
        Jedis jedis = getJedis();
        String res = null;
        try {
            res = jedis.get(key);
        } finally {
            close(jedis);
        }
        return res;
    }

    public boolean set(String key, String value){
        Jedis jedis = getJedis();
        String s = null;
        try {
            s = jedis.set(key, value);
        } finally {
            close(jedis);
        }
        return "OK".equals(s);
    }

    public void del(String key){
        Jedis jedis = getJedis();
        try {
            jedis.del(key);
        } finally {
            close(jedis);
        }
    }

    public Set<String> sGet(String key){
        Jedis jedis = getJedis();
        Set<String> set = null;
        try {
             set = jedis.smembers(key);
        }finally {
            close(jedis);
        }
        return set;
    }

    public boolean sAdd(String key, String value){
        Jedis jedis = getJedis();
        long l = 0;
        try {
            l = jedis.sadd(key, value);
        }finally {
            close(jedis);
        }
        return l > 0;
    }

    public boolean sDel(String key, String value){
        Jedis jedis = getJedis();
        long l = 0;
        try {
            l = jedis.srem(key, value);
        }finally {
            close(jedis);
        }
        return l > 0;
    }

    public boolean sIsMember(String key, String member){
        Jedis jedis = getJedis();
        boolean res = false;
        try {
            res = jedis.sismember(key, member);
        }finally {
            close(jedis);
        }
        return res;
    }
}
