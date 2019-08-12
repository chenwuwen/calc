package cn.kanyun.calc.fragment;

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
import cn.kanyun.calc.activity.viewmodel.ScoreViewModel;
import cn.kanyun.calc.databinding.FragmentMainBinding;


public class MainFragment extends Fragment {

    FragmentMainBinding mainFragmentBinding;

    ScoreViewModel scoreViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        scoreViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(ScoreViewModel.class);
        mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        mainFragmentBinding.setMainData(scoreViewModel);
        mainFragmentBinding.setLifecycleOwner(requireActivity());

//        跳转到游戏页面
        mainFragmentBinding.buttonBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_mainFragment_to_questionFragment);
            }
        });
        return mainFragmentBinding.getRoot();
    }
}
