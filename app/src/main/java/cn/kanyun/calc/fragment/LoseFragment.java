package cn.kanyun.calc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import cn.kanyun.calc.R;
import cn.kanyun.calc.databinding.LoseFragmentBinding;

public class LoseFragment extends Fragment {


    LoseFragmentBinding loseFragmentBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        loseFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.lose_fragment, container, false);
        loseFragmentBinding.setLifecycleOwner(this);
        Bundle args = getArguments();
        loseFragmentBinding.textView8.setText(getString(R.string.lose_current_score_str, args.getInt("currentScore")));
        loseFragmentBinding.textView10.setText(getString(R.string.history_high_score) + args.getInt("highScore"));
//        继续挑战按钮点击事件
        loseFragmentBinding.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("loseFlg", true);
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_loseFragment_to_questionFragment, bundle);
            }
        });

//        返回按键点击事件
        loseFragmentBinding.returnButton.setOnClickListener(v -> {
            NavController controller = Navigation.findNavController(v);
            controller.navigate(R.id.action_loseFragment_to_mainFragment);
        });
        return loseFragmentBinding.getRoot();
    }


}
