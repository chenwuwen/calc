package cn.kanyun.calc.util;

import java.util.concurrent.atomic.AtomicInteger;

public class ChipUtil {

    /**
     * 历史最高分
     */
    public static int HIGH_SCORE = 0;

    /**
     * 初始化计数器,用于存放返回的次数
     */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);


    /**
     * 计算返回值
     * 该返回值将决定选择使用哪个碎片
     * 该方法执行的最基础条件就是当前分数大于历史最高分
     *
     * @param current
     * @return
     */
    public static int compute(int current) {
        int temp = current - HIGH_SCORE;
//            当atomicInteger为8时表示,解锁了最后一个碎片,此时返回666
        if (atomicInteger.get() > 8) {
            atomicInteger.set(0);
            return 666;
        }
//            返回次数+1
        atomicInteger.incrementAndGet();

        int mod = temp % 9;
        return mod;
    }
}
