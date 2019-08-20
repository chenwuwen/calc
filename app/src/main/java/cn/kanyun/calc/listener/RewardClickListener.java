package cn.kanyun.calc.listener;

import android.view.View;
import android.widget.Toast;

/**
 * 已解锁的奖励的点击事件监听
 */
public class RewardClickListener implements View.OnClickListener {

    String rewardName;

    public RewardClickListener(String rewardName) {
        this.rewardName = rewardName;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), rewardName, Toast.LENGTH_SHORT).show();
    }
}
