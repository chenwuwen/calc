package cn.kanyun.calc;

import android.app.Application;

import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;

import es.dmoral.toasty.Toasty;

public class MyApplication extends Application {

    private static MyApplication instance;


    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//       初始化QMUISwipeBackActivityManager，否则点击屏幕时就程序就会崩溃
        QMUISwipeBackActivityManager.init(this);
        Toasty.Config.getInstance()
                .tintIcon(true) //将文字颜色应用到图标
                .setTextSize(15)
                .allowQueue(true) //允许Toast排队
                .apply();
    }
}
