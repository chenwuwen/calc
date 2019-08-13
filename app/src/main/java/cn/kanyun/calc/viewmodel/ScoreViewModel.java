package cn.kanyun.calc.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.R;
import es.dmoral.toasty.Toasty;

/**
 * 通过继承AndroidViewModel的好处是可以访问SharedPrefences
 * SavedStateHandle
 */
public class ScoreViewModel extends AndroidViewModel {

    SavedStateHandle handle;

    Context context;

    /**
     * 历史最高分
     */
    private static String KEY_HIGH_SCORE = "high_score";
    /**
     * 本次分数
     */
    private static String KEY_CURRENT_SCORE = "current_score";
    /**
     * 左边的数字
     */
    private static String KEY_LEFT_NUMBER = "left_number";
    /**
     * 右边的数字
     */
    private static String KEY_RIGHT_NUMBER = "right_number";
    /**
     * 运算符号
     */
    private static String KEY_OPERATOR_SYMBOL = "operator_symbol";
    /**
     * 正确的答案
     */
    private static String KEY_ANSWER = "answer";

    /**
     * 解锁奖励
     */
    private static String KEY_UNLOCK_REWARD = "key_unlock_reward";


    private static List<String> OPERATOR_SYMBOLS = new ArrayList<>();

    private static int NUMBER_UPPER;


    /**
     * 解锁条件
     */
    private static Map<Integer, Integer> unLockScore = new HashMap() {{
        put(5, R.drawable.unlock_1);
        put(10, R.drawable.unlock_2);
        put(15, R.drawable.unlock_3);
        put(20, R.drawable.unlock_4);
        put(25, R.drawable.unlock_5);
    }};

    /**
     * 解锁标志
     */
    public boolean unLock = false;

    /**
     * 构造函数的参数,添加了savedStateHandle,所以不再需要在ViewModel类中定义 MutableLiveData类型的字段了
     *
     * @param application
     * @param savedStateHandle
     */
    public ScoreViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
//        初始化Context,构造函数里的Application,是因为该ViewModel是继承的AndroidViewModel而不是继承ViewModel
        this.context = application;
//        如果不包含最高分数,则是第一次进入
        if (!savedStateHandle.contains(KEY_HIGH_SCORE)) {
            SPUtils sp = SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME);
            savedStateHandle.set(KEY_HIGH_SCORE, sp.getInt(KEY_HIGH_SCORE, 0));
            savedStateHandle.set(KEY_LEFT_NUMBER, 0);
            savedStateHandle.set(KEY_RIGHT_NUMBER, 0);
            savedStateHandle.set(KEY_CURRENT_SCORE, 0);
            savedStateHandle.set(KEY_ANSWER, 0);
            savedStateHandle.set(KEY_UNLOCK_REWARD, 0);

//            运算符号
            String symbols = sp.getString(Constant.KEY_OPERATOR_SYMBOLS, null);
            if (StringUtils.isEmpty(symbols)) {
                OPERATOR_SYMBOLS.add(application.getString(R.string.set_operator_symbol_add));
                OPERATOR_SYMBOLS.add(application.getString(R.string.set_operator_symbol_reduce));
            } else {
                OPERATOR_SYMBOLS = new ArrayList<>(Arrays.asList(symbols.split(",")));
//                如果包含除法符号,将其替换成 /
                if (OPERATOR_SYMBOLS.contains(application.getString(R.string.set_operator_symbol_division))) {
                    Collections.replaceAll(OPERATOR_SYMBOLS, application.getString(R.string.set_operator_symbol_division), "/");
                }
            }
//            数值上限
            NUMBER_UPPER = sp.getInt(Constant.KEY_NUMBER_UPPER_LIMIT, 20);

        }
        this.handle = savedStateHandle;
    }

    /**
     * 得到左边的数字
     *
     * @return
     */
    public MutableLiveData<Integer> getLeftNumber() {
        return handle.getLiveData(KEY_LEFT_NUMBER);
    }

    /**
     * 得到右边的数字
     *
     * @return
     */
    public MutableLiveData<Integer> getRightNumber() {
        return handle.getLiveData(KEY_RIGHT_NUMBER);
    }

    /**
     * 得到运算符
     *
     * @return
     */
    public MutableLiveData<String> getOperatorSymbol() {
        return handle.getLiveData(KEY_OPERATOR_SYMBOL);
    }

    /**
     * 历史最高纪录
     *
     * @return
     */
    public MutableLiveData<Integer> getHighScore() {
        return handle.getLiveData(KEY_HIGH_SCORE);
    }

    /**
     * 得到本次答题分数
     *
     * @return
     */
    public MutableLiveData<Integer> getCurrentScore() {
        return handle.getLiveData(KEY_CURRENT_SCORE);
    }

    /**
     * 正确答案
     *
     * @return
     */
    public MutableLiveData<Integer> getAnswer() {
        return handle.getLiveData(KEY_ANSWER);
    }

    /**
     * 解锁奖励
     *
     * @return
     */
    public MutableLiveData<Integer> getUnlockReward() {
        return handle.getLiveData(KEY_UNLOCK_REWARD);
    }

    /**
     * 生成算式
     */
    public void generator() {
        Random random = new Random();
        int x, y;
        x = random.nextInt(NUMBER_UPPER) + 1;
        y = random.nextInt(NUMBER_UPPER) + 1;
        if (x % 2 == 0) {
            getOperatorSymbol().setValue("+");
            if (x > y) {
                getAnswer().setValue(x);
                getLeftNumber().setValue(y);
                getRightNumber().setValue(x - y);
            } else {
                getAnswer().setValue(y);
                getLeftNumber().setValue(x);
                getRightNumber().setValue(y - x);
            }
        } else {
            getOperatorSymbol().setValue("-");
            if (x > y) {
                getAnswer().setValue(y);
                getLeftNumber().setValue(x);
                getRightNumber().setValue(x - y);
            } else {
                getAnswer().setValue(x);
                getLeftNumber().setValue(y);
                getRightNumber().setValue(y - x);
            }
        }

    }

    /**
     * 将新纪录保存到SharedPrefences文件中
     */
    void saveHighScore() {
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(KEY_HIGH_SCORE, getHighScore().getValue());

    }

    /**
     * 回答正确处理
     */
    public void answerCorrect() {
//        当前得分加1
        getCurrentScore().setValue(getCurrentScore().getValue() + 1);
//        判断是否打破之前的记录
        if (getCurrentScore().getValue() > getHighScore().getValue()) {
            getHighScore().setValue(getCurrentScore().getValue());
            saveHighScore();

//            判断是否达到解锁分数,并且当前分数超过了历史最高纪录
            if (unLockScore.get(getCurrentScore().getValue()) != null) {
//                设置解锁标志为true
                unLock = true;
//                将解锁奖励放到ViewModel中
                getUnlockReward().setValue(unLockScore.get(getCurrentScore().getValue()));
                Toasty.success(context, "解锁新车辆", Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.success(context, "刷新历史最佳记录", Toast.LENGTH_SHORT, true).show();
            }
        }

//        生成下一个计算式
        generator();
    }
}
