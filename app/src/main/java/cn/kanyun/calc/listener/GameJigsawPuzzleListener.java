package cn.kanyun.calc.listener;

public interface GameJigsawPuzzleListener {
    /**
     * 关卡
     * @param nextLevel
     */
    void nextLevel(int nextLevel);

    /**
     * 时间改变
     * @param time
     */
    void timeChanged(int time);

    /**
     * 游戏结束
     */
    void gameOver();
}
