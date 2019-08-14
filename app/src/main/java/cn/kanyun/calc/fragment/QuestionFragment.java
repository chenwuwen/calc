package cn.kanyun.calc.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.kanyun.calc.R;
import cn.kanyun.calc.databinding.QuestionFragmentBinding;
import cn.kanyun.calc.viewmodel.ScoreViewModel;
import es.dmoral.toasty.Toasty;

public class QuestionFragment extends Fragment implements View.OnClickListener {

    private ScoreViewModel mViewModel;

    QuestionFragmentBinding questionFragmentBinding;

    /**
     * 输入的答案
     */
    StringBuilder sb;


    public static QuestionFragment newInstance() {
        return new QuestionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
//        这里使用requireActivity()getActivity()都可以
        mViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(ScoreViewModel.class);
        if (args != null && args.getBoolean("loseFlg")) {
//            如果跳转到该Fragment的上一个Fragment是LoseFragment,那么重置当前得分
            mViewModel.getCurrentScore().setValue(0);
        }
//        第一次进入页面时生成计算式子,否则会显示成 0+0
        mViewModel.generator();
        questionFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.question_fragment, container, false);
        questionFragmentBinding.setData(mViewModel);
        questionFragmentBinding.setLifecycleOwner(requireActivity());

        sb = new StringBuilder();

        questionFragmentBinding.button0.setOnClickListener(this);
        questionFragmentBinding.button1.setOnClickListener(this);
        questionFragmentBinding.button2.setOnClickListener(this);
        questionFragmentBinding.button3.setOnClickListener(this);
        questionFragmentBinding.button4.setOnClickListener(this);
        questionFragmentBinding.button5.setOnClickListener(this);
        questionFragmentBinding.button6.setOnClickListener(this);
        questionFragmentBinding.button7.setOnClickListener(this);
        questionFragmentBinding.button8.setOnClickListener(this);
        questionFragmentBinding.button9.setOnClickListener(this);
        questionFragmentBinding.buttonClear.setOnClickListener(this);
        questionFragmentBinding.buttonSubmit.setOnClickListener(this);

//        处理占位符
        questionFragmentBinding.answer.setText(getString(R.string.input_result, ""));

        return questionFragmentBinding.getRoot();
    }


    @Override
    public void onClick(View v) {
        boolean cancelFlg = false;
        switch (v.getId()) {
            case R.id.button0:
                sb.append("0");
                break;
            case R.id.button1:
                sb.append("1");
                break;
            case R.id.button2:
                sb.append("2");
                break;
            case R.id.button3:
                sb.append("3");
                break;
            case R.id.button4:
                sb.append("4");
                break;
            case R.id.button5:
                sb.append("5");
                break;
            case R.id.button6:
                sb.append("6");
                break;
            case R.id.button7:
                sb.append("7");
                break;
            case R.id.button8:
                sb.append("8");
                break;
            case R.id.button9:
                sb.append("9");
                break;
            case R.id.button_clear:
                sb.setLength(0);
                cancelFlg = true;
                break;
            default:
                if (sb.length() < 1) {
                    Toasty.warning(getContext(), "请输入答案后提交", Toast.LENGTH_SHORT, true).show();
                    return;
                }

//                配置Navigation跳转携带的参数
                Bundle bundle = new Bundle();
                bundle.putInt("currentScore", mViewModel.getCurrentScore().getValue());
                bundle.putInt("highScore", mViewModel.getHighScore().getValue());

                if (Integer.parseInt(sb.toString()) == mViewModel.getAnswer().getValue()) {
//                    回答正确
                    mViewModel.answerCorrect();

//                    是否到达解锁分数
                    if (mViewModel.unLock) {
                        mViewModel.unLock = false;

                        NavController controller = Navigation.findNavController(v);
                        controller.navigate(R.id.action_questionFragment_to_unlockFragment);
                    }
                } else {

//                    回答失败,跳转到失败页面
                    NavController controller = Navigation.findNavController(v);
                    controller.navigate(R.id.action_questionFragment_to_loseFragment, bundle);
                }
                sb.setLength(0);

        }
//        将点击的按键字符显示在界面上
        if (!cancelFlg) {
            questionFragmentBinding.answer.setText(getString(R.string.input_result, sb.toString()));
        } else {
            questionFragmentBinding.answer.setText(getString(R.string.input_result, ""));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
