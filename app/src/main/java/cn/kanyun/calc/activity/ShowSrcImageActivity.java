package cn.kanyun.calc.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.MyApplication;
import cn.kanyun.calc.R;
import cn.kanyun.calc.databinding.ActivityShowSrcImageBinding;

/**
 * 查看原图
 */
public class ShowSrcImageActivity extends AppCompatActivity {

    ActivityShowSrcImageBinding activityShowSrcImageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        传递Bitmap的方法由Intent传递,变为全局变量的方式传递
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        Bitmap bitmap = (Bitmap) bundle.get(Constant.KEY_ACTIVITY_SRC_IMAGE_BITMAP);

        Bitmap bitmap = MyApplication.showBitmap;
        activityShowSrcImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_src_image);
        activityShowSrcImageBinding.showSrcImg.setImageDrawable(new BitmapDrawable(getResources(), bitmap));

        activityShowSrcImageBinding.showSrcImg.setOnClickListener(v -> {
            finish();
        });
//        全屏,隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }
}
