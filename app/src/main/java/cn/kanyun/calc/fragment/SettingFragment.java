package cn.kanyun.calc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(SettingViewModel.class);
        settingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        settingBinding.setSetData(settingViewModel);
        settingBinding.setLifecycleOwner(this);
        return settingBinding.getRoot();
    }

}
