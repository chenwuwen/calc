package cn.kanyun.calc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.kanyun.calc.R;
import cn.kanyun.calc.Reward;
import cn.kanyun.calc.databinding.ItemRewardBinding;
import cn.kanyun.calc.listener.RewardClickListener;

/**
 * 奖励 RecycleViewAdapter
 */
public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    List<Reward> rewards;

    public RewardAdapter(List<Reward> rewards) {
        this.rewards = rewards;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        需要注意的是,这里使用的DataBinding是自定义RecycleView的binding,也就是说自定义的RecycleVie的布局文件也要转换成databinding的形式
        ItemRewardBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_reward, parent, false);
        return new ViewHolder(binding);
    }

    /**
     * 填充视图
     * 将数据与界面进行绑定的操作
     * 这个方法将会遍历,遍历此时为dataList.size()
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reward reward = rewards.get(position);
        holder.getBinding().setReward(reward);
//        rewards.get(position).getName()
//        利用共享元素预览大图：https://www.jianshu.com/p/05e3b99ce5ce  https://www.jianshu.com/p/3d432402448e
        ViewCompat.setTransitionName(holder.getBinding().imageView, "share");
//        配置点击事件
        holder.getBinding().linearLayout2.setOnClickListener(new RewardClickListener(holder.itemView, holder.getBinding().getReward().getName(), holder.getItemId(), position));
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    /**
     * 当使用MVVM 结合 RecycleView使用时 就不用findViewById了
     * 需要注意的是 ，这里使用的DataBinding 绑定的是 自定义layout的binding
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        ItemRewardBinding itemRewardBinding;

        public ViewHolder(@NonNull ItemRewardBinding binding) {
            super(binding.getRoot());
            this.itemRewardBinding = binding;
        }

        public ItemRewardBinding getBinding() {
            return itemRewardBinding;
        }
    }
}
