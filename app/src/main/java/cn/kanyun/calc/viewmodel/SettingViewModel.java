package cn.kanyun.calc.viewmodel;

import android.app.Application;
import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
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

import org.greenrobot.eventbus.EventBus;

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
     * 获取数值上限
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
     * @param radioGroup
     * @param checkedId
     */
    public void typeChange(RadioGroup radioGroup, int checkedId) {
        if (checkedId == R.id.join_operation_type_radio) {
            getNumberUpperType().setValue(Type.MEMBER_GUIDE.number);
        }else {
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
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_NUMBER_UPPER_LIMIT, numberUpper);
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_OPERATOR_SYMBOLS, symbols);
        SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME).put(Constant.KEY_NUMBER_UPPER_TYPE, numberUpperType);

//        这里使用Guava的Multimap,一个key对应多个value
        Multimap<Integer, Object> param = LinkedListMultimap.create();
        param.put(numberUpper, symbols);
        param.put(numberUpper, numberUpperType);

        EventBus.getDefault().post(param);
        Toasty.success(context, "保存配置成功！", Toast.LENGTH_SHORT).show();
    }

}
