package cn.kanyun.calc.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.kanyun.calc.R;
import cn.kanyun.calc.Reward;
import cn.kanyun.calc.adapter.RewardAdapter;
import cn.kanyun.calc.databinding.MeFragmentBinding;
import cn.kanyun.calc.viewmodel.RewardViewModel;

/**
 * 我的仓库[奖励页面]
 */
public class MeFragment extends Fragment {


    private GridLayoutManager layoutManager;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    RewardViewModel rewardViewModel;

    MeFragmentBinding meFragmentBinding;

    RewardAdapter rewardAdapter;

    RecyclerView recyclerView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        meFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.me_fragment, container, false);
        rewardViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(RewardViewModel.class);
        meFragmentBinding.setRewardData(rewardViewModel);

        recyclerView = meFragmentBinding.rewardPreviewRecyclerView;

        List<Reward> rewards = rewardViewModel.getShowReward();

        rewardAdapter = new RewardAdapter(rewards);


//        GridLayoutManager构造函数接收两个参数， 第一个是Context， 第二个是列数
        layoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        layoutManager.setSmoothScrollbarEnabled(true);
//        解决NestedScrollView嵌套滑动的卡顿
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(rewardAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return meFragmentBinding.getRoot();
    }


}
