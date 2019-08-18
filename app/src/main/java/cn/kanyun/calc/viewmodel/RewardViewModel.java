package cn.kanyun.calc.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.SavedStateHandle;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 奖励 ViewModel
 */
public class RewardViewModel extends AndroidViewModel {

    SavedStateHandle handle;
    Context context;

    /**
     * 所有奖励的图片
     */
    public static List<Bitmap> bitmapList = new ArrayList<>();

    public RewardViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.handle = savedStateHandle;
        this.context = application;
    }


    /**
     * 初始化奖励
     * 图片资源在assert目录下,assert目录下可以创建子目录,assert目录下的文件不参与编译,保持原格式
     */
    private void initReward() {
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list("reward");
            Logger.d("AssertManger取到assert目录下的文件：" + files.toString());
            for (int i = 0; i < files.length; i++) {
                InputStream inputStream = assetManager.open(files[i], AssetManager.ACCESS_BUFFER);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmapList.add(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
