package cn.kanyun.calc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;


import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;

import cn.kanyun.calc.R;
import cn.kanyun.calc.databinding.FragmentSettingBinding;
import cn.kanyun.calc.viewmodel.SettingViewModel;

/**
 *
 */
public class SettingFragment extends Fragment {

    SettingViewModel settingViewModel;
    FragmentSettingBinding settingBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(SettingViewModel.class);
        settingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        settingBinding.setSetData(settingViewModel);
        settingBinding.setLifecycleOwner(this);


        settingBinding.seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                settingViewModel.setTimeout(bubbleSeekBar, progress, progressFloat, fromUser);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

        return settingBinding.getRoot();
    }

}
