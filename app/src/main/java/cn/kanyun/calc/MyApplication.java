package cn.kanyun.calc;

import android.app.Application;

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
        Toasty.Config.getInstance()
                .tintIcon(true) //将文字颜色应用到图标
                .setTextSize(20)
                .allowQueue(true) //允许Toast排队
                .apply();
    }
}
