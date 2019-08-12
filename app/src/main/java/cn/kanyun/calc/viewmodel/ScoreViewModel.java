package cn.kanyun.calc.activity.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.TypedArrayUtils;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.common.primitives.Ints;

import java.util.Random;

import es.dmoral.toasty.Toasty;

/**
 * AndroidViewModel的好处是可以访问SharedPrefences
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
     * sharedprefemces文件名
     */
    private static String SHARED_PREFENCES_NAME = "calc";

    /**
     * 解锁条件
     */
    private static int[] unLockScore = {5, 10, 20, 50};

    /**
     * 解锁标志
     */
    public boolean unLock = false;

    public ScoreViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
//        初始化Context,构造函数里的Application,是因为该ViewModel是继承的AndroidViewModel而不是继承ViewModel
        this.context = application;
//        如果不包含最高分数,则是第一次进入
        if (!savedStateHandle.contains(KEY_HIGH_SCORE)) {
            SPUtils sp = SPUtils.getInstance(SHARED_PREFENCES_NAME);
            savedStateHandle.set(KEY_HIGH_SCORE, sp.getInt(KEY_HIGH_SCORE, 0));
            savedStateHandle.set(KEY_LEFT_NUMBER, 0);
            savedStateHandle.set(KEY_RIGHT_NUMBER, 0);
            savedStateHandle.set(KEY_OPERATOR_SYMBOL, "+");
            savedStateHandle.set(KEY_CURRENT_SCORE, 0);
            savedStateHandle.set(KEY_ANSWER, 0);

        }
        this.handle = savedStateHandle;
    }

    public MutableLiveData<Integer> getLeftNumber() {
        return handle.getLiveData(KEY_LEFT_NUMBER);
    }

    public MutableLiveData<Integer> getRightNumber() {
        return handle.getLiveData(KEY_RIGHT_NUMBER);
    }

    public MutableLiveData<String> getOperatorSymbol() {
        return handle.getLiveData(KEY_OPERATOR_SYMBOL);
    }

    public MutableLiveData<Integer> getHighScore() {
        return handle.getLiveData(KEY_HIGH_SCORE);
    }

    public MutableLiveData<Integer> getCurrentScore() {
        return handle.getLiveData(KEY_CURRENT_SCORE);
    }

    public MutableLiveData<Integer> getAnswer() {
        return handle.getLiveData(KEY_ANSWER);
    }

    /**
     * 生成算式
     */
    public void generator() {
        int LEVEL = 20;
        Random random = new Random();
        int x, y;
        x = random.nextInt(LEVEL) + 1;
        y = random.nextInt(LEVEL) + 1;
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
        SPUtils.getInstance(SHARED_PREFENCES_NAME).put(KEY_HIGH_SCORE, getHighScore().getValue());
        Toasty.success(context, "历史最佳记录", Toast.LENGTH_LONG, true).show();
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
            if (Ints.contains(unLockScore, getCurrentScore().getValue())) {
//                设置解锁标志为true
                unLock = true;
            }
        }

//        生成下一个计算式
        generator();
    }
}
