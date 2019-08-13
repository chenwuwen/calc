package cn.kanyun.calc.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.kanyun.calc.R;
import cn.kanyun.calc.databinding.UnlockFragmentBinding;
import cn.kanyun.calc.widget.UnlockPopup;

public class UnlockFragment extends Fragment {

    UnlockFragmentBinding unlockFragmentBinding;

    cn.kanyun.calc.viewmodel.ScoreViewModel scoreViewModel;

    public static UnlockFragment newInstance() {
        return new UnlockFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        unlockFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.unlock_fragment, container, false);
        scoreViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(cn.kanyun.calc.viewmodel.ScoreViewModel.class);
        unlockFragmentBinding.setLifecycleOwner(requireActivity());
        unlockFragmentBinding.setData(scoreViewModel);
        unlockFragmentBinding.reward.setImageResource(scoreViewModel.getUnlockReward().getValue());
        unlockFragmentBinding.constraintLayout2.setOnClickListener(v -> {
            UnlockPopup unlockPopup = new UnlockPopup(getContext());
//            设置是否点击popup外部时dismiss
            unlockPopup.setOutSideDismiss(true);
//                设置是否允许back键dismiss
            unlockPopup.setBackPressEnable(true);
            unlockPopup.setPopupGravity(Gravity.BOTTOM);
//            显示Popup
            unlockPopup.showPopupWindow();
        });
        return unlockFragmentBinding.getRoot();
    }


}
