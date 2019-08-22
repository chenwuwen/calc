package cn.kanyun.calc.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.common.collect.Multimap;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.R;
import cn.kanyun.calc.Type;
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

    /**
     * 数值上限
     */
    private static int NUMBER_UPPER;

    /**
     * 数值上限类型
     */
    public int numberUpperType;

    /**
     * 答题时长上限,默认是10s 当在设置项更改时,会改变此值
     */
    public int timeoutAnswer = 10;


    /**
     * 解锁条件
     */
    private static Map<Integer, Bitmap> unLockScore = new HashMap(){{
        put(100, null);
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

//        消费者需要注册EventBus,发送者不需要注册
        EventBus.getDefault().register(this);
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
//                如果包含除法符号,将其替换成 / (之所以想着替换是因为想把符号作为运算符来操作,不过想了想没必要,可以直接使用工具类来进行运算)
//                if (OPERATOR_SYMBOLS.contains(application.getString(R.string.set_operator_symbol_division))) {
//                    Collections.replaceAll(OPERATOR_SYMBOLS, application.getString(R.string.set_operator_symbol_division), "/");
//                }
            }
//            数值上限
            NUMBER_UPPER = sp.getInt(Constant.KEY_NUMBER_UPPER_LIMIT, 10);
//            数值上限类型
            numberUpperType = sp.getInt(Constant.KEY_NUMBER_UPPER_TYPE, Type.MEMBER_GUIDE.number);

        }
        initReward();
        this.handle = savedStateHandle;
    }

    private void initReward() {
        AssetManager assetManager = context.getAssets();
//        assetManager.

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
    public MutableLiveData<Bitmap> getUnlockReward() {
        return handle.getLiveData(KEY_UNLOCK_REWARD);
    }

    /**
     * 生成运算符
     *
     * @return
     */
    public String generatorSymbol() {
        Random symbolRandom = new Random();
        int k = symbolRandom.nextInt(OPERATOR_SYMBOLS.size());
        String symbol = OPERATOR_SYMBOLS.get(k);
        return symbol;
    }

    /**
     * 生成算式
     * 这个方法生成的是参与运算的数字上限
     */
    public void generator() {
//        生成运算符
        String symbol = generatorSymbol();
//        生成左右两边的数字
        Random numberRandom = new Random();
        int x, y;
        x = numberRandom.nextInt(NUMBER_UPPER) + 1;
        y = numberRandom.nextInt(NUMBER_UPPER) + 1;

        if (symbol.equals(context.getString(R.string.set_operator_symbol_add))) {
//            加法
            getOperatorSymbol().setValue(context.getString(R.string.set_operator_symbol_add));
//          该方法返回的是整数a和整数b的和，如果和不超过整数范围，就返回这个值[guava工具类实现]
            getAnswer().setValue(IntMath.checkedAdd(x, y));
        } else if (symbol.equals(context.getString(R.string.set_operator_symbol_reduce))) {
//            减法
            getOperatorSymbol().setValue(context.getString(R.string.set_operator_symbol_reduce));
//                如果x<y则交换数值
            if (Ints.compare(x, y) == -1) {
                x = x ^ y;
                y = x ^ y;
                x = x ^ y;
            }
            getAnswer().setValue(IntMath.checkedSubtract(x, y));
        } else if (symbol.equals(context.getString(R.string.set_operator_symbol_multiplication))) {
//            乘法
            getOperatorSymbol().setValue(context.getString(R.string.set_operator_symbol_multiplication));
            getAnswer().setValue(IntMath.checkedMultiply(x, y));

        } else {
//            除法
            getOperatorSymbol().setValue(context.getString(R.string.set_operator_symbol_division));
//          如果生成的是 除法 运算符,那么把x 作为结果， xy的乘积当做左边的数赋值给x(被除数) ,y 作为除数
            getAnswer().setValue(x);
            x = IntMath.checkedMultiply(x, y);
        }

//        设置左右两边的数值
        getLeftNumber().setValue(x);
        getRightNumber().setValue(y);
    }

    /**
     * 生成运算表达式
     * 这个方法生成的是运算结果的上限
     */
    public void generator1() {
        Random numberRandom = new Random();
        int x, y;
        x = numberRandom.nextInt(NUMBER_UPPER) + 1;
        y = numberRandom.nextInt(NUMBER_UPPER) + 1;
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
        if (numberUpperType == Type.MEMBER_GUIDE.number) {
            generator();
        } else {
            generator1();
        }
    }

    /**
     * EventBus取消注册
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收shareprefences文件变化事件
     * 这里使用的形参是Guava的Multimap
     * 其发送端的真实类型是LinkedListMultimap
     * @param map
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sharePrefencesChanged(Multimap<Integer, Object> map) {

//        原来是直接使用的Map
//        Set<Map.Entry<Integer, String>> set = map.entrySet();
//        Iterator iterator = set.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>) iterator.next();
//            Logger.d("EventBus消息订阅者收到消息，数值上限调整为：" + entry.getKey());
//            Logger.d("EventBus消息订阅者收到消息，运算符调整为：" + entry.getValue());
//            NUMBER_UPPER = entry.getKey();
//            String string = entry.getValue();
//            OPERATOR_SYMBOLS = Arrays.asList(string.split(","));
//        }

        Set<Integer> set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {


            Integer numUpper = (Integer) iterator.next();

            Collection<Object> collection = map.get(numUpper);
            Object[] o = collection.toArray();
            String symbols = (String) o[0];
            Integer numUpperType = (Integer) o[1];
            Integer timeout = (Integer) o[2];

            Logger.d("EventBus消息订阅者收到消息，数值上限调整为：" + numUpper);
            Logger.d("EventBus消息订阅者收到消息，运算符调整为：" + symbols);
            Logger.d("EventBus消息订阅者收到消息，数值上限类型为：" + numUpperType);
            Logger.d("EventBus消息订阅者收到消息，答题时长上限为：" + timeout);

            NUMBER_UPPER = numUpper;
            OPERATOR_SYMBOLS = Arrays.asList(symbols.split(","));
            numberUpperType = numUpperType;
            timeoutAnswer = timeout;
        }

    }
}
