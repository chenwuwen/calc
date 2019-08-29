package cn.kanyun.calc.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.kanyun.calc.R;
import cn.kanyun.calc.Reward;
import cn.kanyun.calc.databinding.ActivityImageAppreciateBinding;
import cn.kanyun.calc.viewmodel.RewardViewModel;

/**
 * 大图预览（已获得的奖励大图预览）
 */
public class ImageAppreciateActivity extends AppCompatActivity {

    public static final String IMG_POSITION = "img_position";

    public int index;

    public List<Reward> rewards;

    RewardViewModel rewardViewModel;

    /**
     * snapHelper搭配RecyclerView可以实现类似ViewPager效果
     */
    SnapHelper snapHelper;

    ImgAdapter imgAdapter;

    ActivityImageAppreciateBinding imageAppreciateBinding;

    RecyclerView appreciateRecycleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageAppreciateBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_appreciate);

        appreciateRecycleView = imageAppreciateBinding.appreciateRecycleView;

        rewardViewModel = ViewModelProviders.of(this, new SavedStateVMFactory(this)).get(RewardViewModel.class);

//        获得已经赢得的奖励
        List<Reward> totalRewardList = rewardViewModel.getShowReward();
        List<Reward> lockRewardList = rewardViewModel.getLockReward();
        totalRewardList.removeAll(lockRewardList);
        rewards = totalRewardList;

//        获得在MeFragment中点击的奖励的索引
        index = getIntent().getIntExtra(IMG_POSITION, 0);

        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(appreciateRecycleView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        appreciateRecycleView.setLayoutManager(layoutManager);
        appreciateRecycleView.scrollToPosition(index);

        imgAdapter = new ImgAdapter(rewards);
        imgAdapter.bindToRecyclerView(appreciateRecycleView);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        全屏,隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    class ImgAdapter extends BaseQuickAdapter<Reward, BaseViewHolder> {
        public ImgAdapter(List<Reward> list) {
            super(R.layout.item_img, list);
        }

        @Override
        protected void convert(BaseViewHolder helper, Reward item) {
            ImageView iv_img = helper.getView(R.id.iv_img);
            Glide.with(mContext).load(item.getDrawable()).into(iv_img);
//            点击大图预览返回
            iv_img.setOnClickListener(v -> {
                finish();
            });
        }

    }
}
