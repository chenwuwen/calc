package cn.kanyun.calc;

/**
 * 难度
 */
public enum Difficulty {

    SIMPLE(2),NORMAL(5), DIFFICULT(10);
    public int price;
    Difficulty(int price){
        this.price = price;
    }

    public static Difficulty getDifficulty(int num){
        for (Difficulty bt : values()) {
            if (bt.price == num) {
                return bt;
            }
        }
        return null;
    }
}
