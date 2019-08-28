package cn.kanyun.calc;

import android.app.Application;
import android.graphics.Bitmap;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;

import es.dmoral.toasty.Toasty;

public class MyApplication extends Application {

    private static MyApplication instance;

    /**
     * 用于Activity间传递Bitmap
     * 因为Activity间使用(intent)传递数据有大小限制(40k)
     * 所以使用全局变量来传递
     */
    public static Bitmap showBitmap;

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

        //        初始化Logger
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
