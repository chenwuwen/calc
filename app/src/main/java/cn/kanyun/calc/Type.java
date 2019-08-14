package cn.kanyun.calc;

public enum Type {
//    成员为导向
    MEMBER_GUIDE(0),
//    结果为导向
    RESULT_GUIDE(1);

    public int number;

    Type(int number) {
        this.number = number;
    }
}
