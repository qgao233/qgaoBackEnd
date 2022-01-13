package com.qgao.springcloud.utils.util;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.SecureRandom;

/**
 * 分布式 ID 生成器：雪花算法
 * ID 生成规则: ID长达 64 bits
 *
 * 1 bit(符号位不用) | 41 bits: Timestamp (毫秒) | 5 bits: 区域（机房） | 5 bits: 机器编号 | 12 bits: 序列号 |
 */
public class SnowFlakeIdUtil {
    // 基准时间
    private final long twepoch = 1622275454791L; //Sat May 29 17:04:14 JST 2021
    // 区域标志位数
    private final static long regionIdBits = 5L;
    // 机器标识位数
    private final static long workerIdBits = 5L;
    // 序列号识位数
    private final static long sequenceBits = 12L;

    // 区域标志ID最大值:2^6-1=31
    private final static long maxRegionId = -1L ^ (-1L << regionIdBits);
    // 机器ID最大值
    private final static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 序列号ID最大值:2^13-1=31
    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 机器ID偏左移10位
    private final static long workerIdShift = sequenceBits;
    // 业务ID偏左移20位
    private final static long regionIdShift = sequenceBits + workerIdBits;
    // 时间毫秒左移23位
    private final static long timestampLeftShift = sequenceBits + workerIdBits + regionIdBits;

    private static long lastTimestamp = -1L;

    private long sequence = 0L;
    private final long workerId;
    private final long regionId;

    private static volatile SnowFlakeIdUtil idWorker;


    public SnowFlakeIdUtil(long regionId, long workerId) {

        // 如果超出范围就抛出异常
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("worker Id can't be greater than %d or less than 0");
        }
        if (regionId > maxRegionId || regionId < 0) {
            throw new IllegalArgumentException("datacenter Id can't be greater than %d or less than 0");
        }

        this.workerId = workerId;
        this.regionId = regionId;
    }

    public SnowFlakeIdUtil(long workerId) {
        // 如果超出范围就抛出异常
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("worker Id can't be greater than %d or less than 0");
        }
        this.workerId = workerId;
        this.regionId = 0;
    }

    /**
     * 实际产生代码的
     * @return
     */
    private synchronized long nextId(boolean isChange, Long myWorkerId) {

        long timestamp = timeGen();

        long realWorkId = workerId;
        if(isChange){
            realWorkId = myWorkerId;
        }

        if (timestamp < lastTimestamp) {
            try {
                throw new Exception("Clock moved backwards.  Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //如果上次生成时间和当前时间相同,在同一毫秒内
        if (lastTimestamp == timestamp) {
            //sequence自增，因为sequence规定只有10bit，所以和sequenceMask相与一下，去掉高位
            sequence = (sequence + 1) & sequenceMask;
            //判断是否溢出,也就是每毫秒内超过1024，当为1024时，与sequenceMask相与，sequence就等于0
            if (sequence == 0) {
                //自旋等待到下一毫秒
                timestamp = tailNextMillis(lastTimestamp);
            }
        } else {
            // 如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加,
            // 为了保证尾数随机性更大一些,最后一位设置一个随机数
            sequence = new SecureRandom().nextInt(10);
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift)
                | (regionId << regionIdShift)
                | (realWorkId << workerIdShift)
                | sequence;
    }

    // 防止产生的时间比之前的时间还要小（由于NTP回拨等问题）,保持增量的趋势.
    private long tailNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    // 获取当前的时间戳
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    private static Long getRegionId(){
        int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
        int sums = 0;
        for (int i: ints) {
            sums += i;
        }
        return (long)(sums % 32);
    }

    private static Long getWorkerId(){

        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for(int b : ints){
                sums += b;
            }
            return (long)(sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0,31);
        }
    }


    private static SnowFlakeIdUtil getInstance(){
        if(idWorker == null){
            synchronized (SnowFlakeIdUtil.class){
                if(idWorker == null){
                    idWorker = new SnowFlakeIdUtil(getRegionId(),getWorkerId());
                }
            }
        }
        return idWorker;
    }

    public static long generateID(boolean isChange, Long myWorkerId){
        return getInstance().nextId(isChange,myWorkerId);
    }

    public static long generateID(){
        return generateID(false, null);
    }

    public static void main(String[] args) {
        System.out.println(generateID());
    }
}
