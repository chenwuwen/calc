package cn.kanyun.calc.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.recyclerview.widget.SortedList;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.common.io.Files;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.Reward;

/**
 * 奖励 ViewModel
 */
public class RewardViewModel extends AndroidViewModel {

    SavedStateHandle handle;
    Context context;

    private static SPUtils spUtils;

    /**
     * 所有的奖励,这个所有奖励并不包含解锁/未解锁的信息
     */
    public static List<Reward> rewardList = new ArrayList<>();

    public RewardViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.handle = savedStateHandle;
        this.context = application;
        if (!savedStateHandle.contains(Constant.HAS_UNLOCK_REWARD)) {
            spUtils = SPUtils.getInstance(Constant.SHARED_PREFENCES_NAME);
            savedStateHandle.set(Constant.HAS_UNLOCK_REWARD, spUtils.getString(Constant.HAS_UNLOCK_REWARD, ""));
        }
//        当多处引用了RewardViewModel时,该方法会执行多次,所以先判断一下
        if (rewardList.size() == 0) {
            initReward();
        }
    }

    /**
     * 获取已经赢得的奖励
     *
     * @return 奖励的路径
     */
    public MutableLiveData<String> getWinReward() {
        return handle.getLiveData(Constant.HAS_UNLOCK_REWARD);
    }

    /**
     * 添加赢得的奖励【在这里指的是,完成拼图后的奖励】
     *
     * @param rewardPath 获得的奖励路径
     * @return
     */
    public void addWinReward(String rewardPath) {
        String rewards = getWinReward().getValue();
        if (StringUtils.isEmpty(rewards)) {
            rewards += rewardPath;
        } else {
            rewards = rewards + "," + rewardPath;
        }
//        保存到SharedPrefences
        spUtils.put(Constant.HAS_UNLOCK_REWARD, rewards);
//        保存到Handler中
        getWinReward().setValue(rewards);
    }

    /**
     * 初始化奖励(这里指的是全部的奖励[预置入的] 包括获得和未获得的)
     * 图片资源在assert目录下,assert目录下可以创建子目录,assert目录下的文件不参与编译,保持原格式
     */
    private void initReward() {

//        测试：用来添加获得的奖励,直接修改SharedPrefences无效
//        addWinReward("unlock_2.jpg");
//        addWinReward("unlock_3.jpg");
//        addWinReward("unlock_5.jpg");

        AssetManager assetManager = context.getAssets();
        try {
//            注意这个path(获取/assets目录下所有文件)
            String[] files = assetManager.list("");
            Logger.d("AssertManger取到assert目录下的文件：" + files.toString());
            for (int i = 0; i < files.length; i++) {
                String extension = Files.getFileExtension(files[i]);
                if (extension.equals("jpg")) {
                    Reward reward = new Reward();
                    InputStream inputStream = assetManager.open(files[i], AssetManager.ACCESS_BUFFER);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                    reward.setDrawable(bitmapDrawable);
                    reward.setName(Files.getNameWithoutExtension(files[i]));
                    reward.setPath(files[i]);
                    rewardList.add(reward);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取展现出来【呈现在奖励仓库页面】的 奖励
     * 包括获得的/未获得
     *
     * @return
     */
    public List<Reward> getShowReward() {
        List<Reward> rewards = new ArrayList<>();
//        已获得的奖励
        String winStr = getWinReward().getValue();
        String[] wins = winStr.split(",");
        List<String> winList = Arrays.asList(wins);
//        遍历全部的奖励,如果其中的一项奖励的名字包含在已获得的奖励中,则将其设置为已解锁
        for (Reward reward : rewardList) {
            if (winList.contains(reward.getPath())) {
                reward.setLock(false);
            }
            rewards.add(reward);
        }


//        在这里进行排序(保证已解锁的在未解锁的前面),目的是在奖励仓库页面,在预览图模式下,保证点击的小图与查看的大图是一致的
//        如果不排序,由于奖励仓库页面使用的是GridView,其各个item的索引 是根据全部奖励设定的,而不是已解锁的奖励设定的,
//        所以会造成,点击小图预览大图会出现不一致的情况,所以需要排序
        Collections.sort(rewards, new Comparator<Reward>() {
            @Override
            public int compare(Reward o1, Reward o2) {
                if (o1.isLock() && !o2.isLock()) {
                    return 1;
                } else if (!o1.isLock() && o2.isLock()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        return rewards;
    }

    /**
     * 获得未获得的奖励
     *
     * @return
     */
    public List<Reward> getLockReward() {
        List<Reward> lockRewardList = new ArrayList<>();
//        遍历全部的奖励,如果其中的一项奖励的名字包含在已获得的奖励中,则将其设置为已解锁
        for (Reward reward : rewardList) {
            if (reward.isLock()) {
                lockRewardList.add(reward);
            }
        }
        return lockRewardList;
    }
}
