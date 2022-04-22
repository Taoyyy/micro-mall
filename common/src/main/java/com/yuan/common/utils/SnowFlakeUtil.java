package com.yuan.common.utils;

public class SnowFlakeUtil {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; // 序列号占用的位数
    private final static long MACHINE_BIT = 5; // 机器标识占用的位数
    private final static long DATACENTER_BIT = 5;// 数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private final long datacenterId; // 数据中心
    private final long machineId; // 机器标识
    private long lastStmp = -1L;// 上一次时间戳

    public SnowFlakeUtil(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public SnowFlakeUtil() {
        this(0L, 0L);
    }

    /**
     * 成员类，SnowFlakeUtil的实例对象的保存域
     */
    private static class IdGenHolder {
        private static final SnowFlakeUtil instance = new SnowFlakeUtil();
    }

    /**
     * 外部调用获取SnowFlakeUtil的实例对象，确保不可变
     */
    public static SnowFlakeUtil get() {
        return SnowFlakeUtil.IdGenHolder.instance;
    }

    /**
     * 产生下一个ID
     *
     */
    public synchronized long createId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        //不同毫秒内，序列号置为0 （改了这里，当前毫秒数为双数设置为0，为单数时设为1）
        // 序列号
        long sequence = currStmp & 1L;
//下方的原代码，全部注释，值保留上面一行，解决跨毫秒全为偶数问题
//        if (currStmp == lastStmp) {
//            //相同毫秒内，序列号自增
//            sequence = (sequence + 1) & MAX_SEQUENCE;
//            //同一毫秒的序列数已经达到最大
//            if (sequence == 0L) {
//                currStmp = getNextMill();
//            }
//        } else {
//            //不同毫秒内，序列号置为0
//            sequence = 0L;
//        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT // 时间戳部分
                | datacenterId << DATACENTER_LEFT // 数据中心部分
                | machineId << MACHINE_LEFT // 机器标识部分
                | sequence; // 序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

}