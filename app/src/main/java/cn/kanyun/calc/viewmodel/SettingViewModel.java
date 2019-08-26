package cn.kanyun.calc.viewmodel;

import android.app.Application;
import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.InverseMethod;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.orhanobut.logger.Logger;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.R;
import cn.kanyun.calc.Type;
import es.dmoral.toasty.Toasty;

/**
 * AndroidViewModel的好处是可以访问SharedPrefences
 * SavedStateHandle
 */
public class SettingViewModel extends AndroidViewModel {

    SavedStateHandle handle;

    Context context;


    /**
     * 构造函数的参数,添加了savedStateHandle,所以不再需要在ViewModel类中定义 MutableLiveData类型的字段了
     *
     * @param application
     * @param savedStateHandle
     */
    public SettingViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
//        初始化Context,构造函数里的Application,是因为该ViewModel是继承的AndroidViewModel而不是继承ViewModel
        this.context = application;
//        如果不包含数值上限,则是第一次进入
        if (!savedStateHandle.contains(Constant.KEY_NUMBER_UPPER_LIMIT)) {
            SPUtils sp = SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME);
            savedStateHandle.set(Constant.KEY_NUMBER_UPPER_LIMIT, sp.getInt(Constant.KEY_NUMBER_UPPER_LIMIT, 10));
//            初始化时,将加减运算符默认勾选
            savedStateHandle.set(Constant.KEY_OPERATOR_SYMBOLS, application.getString(R.string.set_operator_symbol_add) + "," + application.getString(R.string.set_operator_symbol_reduce));
//            数值上限类型(默认是1)
            savedStateHandle.set(Constant.KEY_NUMBER_UPPER_TYPE, sp.getInt(Constant.KEY_NUMBER_UPPER_TYPE, Type.MEMBER_GUIDE.number));
//            背景音乐默认开启
            savedStateHandle.set(Constant.KEY_BACKGROUND_MUSIC, sp.getBoolean(Constant.KEY_BACKGROUND_MUSIC, true));
//            背景音效默认开启
            savedStateHandle.set(Constant.KEY_BACKGROUND_SOUND, sp.getBoolean(Constant.KEY_BACKGROUND_SOUND, true));
//            做题时长SeekBar,默认10秒
            savedStateHandle.set(Constant.KEY_TIMEOUT_ANSWER, sp.getInt(Constant.KEY_TIMEOUT_ANSWER, 10));
        }
        this.handle = savedStateHandle;
    }

    /**
     * 获取数值上限
     *
     * @return
     */
    public MutableLiveData<Integer> getNumberUpperLimit() {
        return handle.getLiveData(Constant.KEY_NUMBER_UPPER_LIMIT);
    }

    /**
     * 获取数值上限类型[以结果为导向还是以参与运算的数为导向]
     *
     * @return
     */
    public MutableLiveData<Integer> getNumberUpperType() {
        return handle.getLiveData(Constant.KEY_NUMBER_UPPER_TYPE);
    }

    /**
     * 获取保存的启用符号
     *
     * @return
     */
    public MutableLiveData<String> getCheckedSymbols() {
        return handle.getLiveData(Constant.KEY_OPERATOR_SYMBOLS);
    }

    /**
     * 获取背景音乐状态
     *
     * @return
     */
    public MutableLiveData<Boolean> getBackGroundMusic() {
        return handle.getLiveData(Constant.KEY_BACKGROUND_MUSIC);
    }

    /**
     * 设置背景音乐状态
     *
     * @return
     */
    public void setBackGroundMusic(CompoundButton compoundButton, boolean isChecked) {
//        要发送的对象(需要注意的是：发送的是对象,而非基本类型,如果要发送基本类型则需要将其转换为对应的包装类型)
        Boolean state;
        if (isChecked) {
            Logger.d("背景音乐打开,EvenBus发送消息");
            state = new Boolean(true);
            EventBus.getDefault().post(state);
        } else {
            Logger.d("背景音乐关闭,EvenBus发送消息");
            state = new Boolean(false);
            EventBus.getDefault().post(state);
        }
        handle.getLiveData(Constant.KEY_BACKGROUND_MUSIC).setValue(false);
    }

    /**
     * 获取背景音效状态
     *
     * @return
     */
    public MutableLiveData<Boolean> getBackGroundSound() {
        return handle.getLiveData(Constant.KEY_BACKGROUND_SOUND);
    }

    /**
     * 设置背景音效状态
     *
     * @return
     */
    public void setBackGroundSound(CompoundButton compoundButton, boolean isChecked) {
//        要EventBus发送的对象,这里本来可以用Boolean对象的,但是因为背景音乐使用了Boolean对象,因为这里使用Integer对象
        Integer state;
        if (isChecked) {
            Logger.d("背景音效打开,EvenBus发送消息");
            state = new Integer(1);
            EventBus.getDefault().post(state);
        } else {
            Logger.d("背景音效关闭,EvenBus发送消息");
            state = new Integer(0);
            EventBus.getDefault().post(state);
        }
        handle.getLiveData(Constant.KEY_BACKGROUND_SOUND).setValue(isChecked);
    }

    /**
     * 获取单次答题超时时间
     *
     * @return
     */
    public MutableLiveData<Integer> getTimeout() {
        return handle.getLiveData(Constant.KEY_TIMEOUT_ANSWER);
    }

    /**
     * 设置单次答题超时时间
     *
     * @return
     */
    public void setTimeout(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
        getTimeout().setValue(progress);
    }


    /**
     * 判断是否是勾选状态
     *
     * @param symbol
     * @return
     */
    public boolean isChecked(String symbol) {
        String string = getCheckedSymbols().getValue();
        List<String> symbols = new ArrayList(Arrays.asList(string.split(",")));
        if (symbols.contains(symbol)) {
            return true;
        }
        return false;
    }


    /**
     * checkbox的点击事件
     *
     * @param symbol
     */
    public void checkedSymbol(String symbol) {
        String string = getCheckedSymbols().getValue();
        List<String> symbols = new ArrayList(Arrays.asList(string.split(",")));
        if (symbols.contains(symbol)) {
            symbols.remove(symbol);
        } else {
            symbols.add(symbol);
        }
        String joinStr = Joiner.on(",")  //分隔符
                .skipNulls()  //过滤null元素
                .join(symbols);//要分割的集合
        System.out.println(joinStr);
        getCheckedSymbols().setValue(joinStr);
    }

    /**
     * 数值上限改变
     * 按键之前字符串的start位置的before个字符已经被count个字符替换形成新字符串charSequence
     *
     * @param val    新字符串
     * @param start
     * @param before
     * @param count  改变的字符个数
     */
    public void numberChange(CharSequence val, int start, int before, int count) {
        int number = 10;
        try {
            number = Integer.parseInt(val.toString());
        } catch (Exception e) {

        }
        getNumberUpperLimit().setValue(number);
    }

    /**
     * 数值上限类型改变
     *
     * @param radioGroup
     * @param checkedId
     */
    public void typeChange(RadioGroup radioGroup, int checkedId) {
        if (checkedId == R.id.join_operation_type_radio) {
            getNumberUpperType().setValue(Type.MEMBER_GUIDE.number);
        } else {
            getNumberUpperType().setValue(Type.RESULT_GUIDE.number);
        }
    }


    /**
     * 保存设置，持久化到SharedPrefences
     */
    public void saveSetting() {
        int numberUpper = getNumberUpperLimit().getValue();
        String symbols = getCheckedSymbols().getValue();
        int numberUpperType = getNumberUpperType().getValue();
        if (numberUpper < 1 || StringUtils.isEmpty(symbols)) {
            Toasty.error(context, "请正确设置选项", Toast.LENGTH_LONG).show();
            return;
        }

        boolean soundState = getBackGroundSound().getValue();
        boolean musicState = getBackGroundMusic().getValue();
        int timeout = getTimeout().getValue();

//        数值上限
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_NUMBER_UPPER_LIMIT, numberUpper);
//        运算符号
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_OPERATOR_SYMBOLS, symbols);
//        数值上限类型
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_NUMBER_UPPER_TYPE, numberUpperType);
//        背景音乐
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_BACKGROUND_MUSIC, musicState);
//        背景音效
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_BACKGROUND_SOUND, soundState);
//        单次答题超时时长
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_TIMEOUT_ANSWER, timeout);

//        这里使用Guava的Multimap,一个key对应多个value
        Multimap<Integer, Object> param = LinkedListMultimap.create();
        param.put(numberUpper, symbols);
        param.put(numberUpper, numberUpperType);
        param.put(numberUpper, timeout);
//        发送
        EventBus.getDefault().post(param);
        Toasty.success(context, "保存配置成功！", Toast.LENGTH_SHORT).show();
    }

}
