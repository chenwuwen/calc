package cn.kanyun.calc.listener;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import cn.kanyun.calc.activity.ImageAppreciateActivity;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * 已解锁的奖励的点击事件监听
 */
public class RewardClickListener implements View.OnClickListener {

    /**
     * 被点击的奖励的名字
     */
    String rewardName;

    /**
     * 被点击的奖励的View
     */
    View itemView;

    /**
     * itemId
     */
    long itemId;

    /**
     * position与itemId不一样,当position为0时,itemId为-1
     */
    int position;

    public RewardClickListener(View itemView, String rewardName, long itemId,int position) {
        this.rewardName = rewardName;
        this.itemView = itemView;
        this.itemId = itemId;
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), rewardName, Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            share(v, position);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void share(View view, int position) {
        Intent intent = new Intent(itemView.getContext(), ImageAppreciateActivity.class);
        intent.putExtra(ImageAppreciateActivity.IMG_POSITION, position);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) view.getContext(), view, "share").toBundle();
        startActivity(intent, bundle);

    }
}
