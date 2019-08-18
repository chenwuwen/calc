package cn.kanyun.calc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import cn.kanyun.calc.R;
import cn.kanyun.calc.databinding.FragmentMainBinding;
import cn.kanyun.calc.service.MusicService;
import cn.kanyun.calc.viewmodel.ScoreViewModel;
import cn.kanyun.calc.viewmodel.SettingViewModel;


public class MainFragment extends Fragment {

    FragmentMainBinding mainFragmentBinding;

    ScoreViewModel scoreViewModel;

    SettingViewModel settingViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        scoreViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(ScoreViewModel.class);
        mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        mainFragmentBinding.setMainData(scoreViewModel);
        mainFragmentBinding.setLifecycleOwner(requireActivity());

//        实例化SettingViewModel（由于这个ViewModel并不与该Fragment进行绑定(仅用来判断是否播放背景音乐),所以不需要 sefData）
        settingViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(SettingViewModel.class);

//        判断背景音乐是否开启
        if (settingViewModel.getBackGroundMusic().getValue()) {
            Intent musicService = new Intent(requireActivity(), MusicService.class);
//            startService(service)只能单纯的开启一个服务，要想调用服务服务中的方法，必须用bindService和unbindService
            requireActivity().startService(musicService);
        }

//        跳转到游戏页面
        mainFragmentBinding.buttonBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_mainFragment_to_questionFragment);
            }
        });
//        跳到设置页面
        mainFragmentBinding.buttonSetting.setOnClickListener(v -> {
            NavController controller = Navigation.findNavController(v);
            controller.navigate(R.id.action_mainFragment_to_settingFragment);
        });

        mainFragmentBinding.buttonMeReward.setOnClickListener(v->{
            NavController controller = Navigation.findNavController(v);
            controller.navigate(R.id.action_mainFragment_to_meFragment);
        });

        return mainFragmentBinding.getRoot();
    }
}
