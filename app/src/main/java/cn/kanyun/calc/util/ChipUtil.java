package cn.kanyun.calc.util;

import java.util.concurrent.atomic.AtomicInteger;

import cn.kanyun.calc.Difficulty;

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
     * 该返回值将决定选择使用哪个碎片,也可能不返回碎片
     * 该方法执行的最基础条件就是当前分数大于历史最高分，且高出的分数对解锁难度取余为0
     *
     * @param current
     * @return  解锁图片的索引
     */
    public static int compute(int current, Difficulty difficulty) {
        int temp = current - HIGH_SCORE;


        if (temp < 0 || temp == 0 ) return temp;

//       当atomicInteger为8时表示,解锁了最后一个碎片,此时返回Integer.MAX_VALUE
        if (atomicInteger.get() > 8) {
            atomicInteger.set(0);
            return Integer.MAX_VALUE;
        }

        if (temp % difficulty.price == 0){
//            返回次数+1
            atomicInteger.incrementAndGet();
            return atomicInteger.intValue();
        }else {
            return Integer.MIN_VALUE;
        }

    }
}
