package cn.kanyun.calc.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.blankj.utilcode.util.FragmentUtils;

import cn.kanyun.calc.R;
import cn.kanyun.calc.fragment.UnlockFragment;
import razerdp.basepopup.BasePopupWindow;

public class UnlockPopup extends BasePopupWindow {

    Button returnButton, conditionButton;

    public UnlockPopup(Context context) {
        super(context);

        returnButton = findViewById(R.id.unlock_return_button);
        conditionButton = findViewById(R.id.unlock_condition_button);
//        返回
        returnButton.setOnClickListener(v -> {
//            由于此处的view是popup的view,所以使用它不能找到NavController
//            Activity activity = (Activity) context;
//            View view = activity.findViewById(R.id.reward);
//            NavController navController = Navigation.findNavController(view);
//            navController.popBackStack();
//            navController.navigate(R.id.action_unlockFragment_to_mainFragment);

            FragmentUtils.remove(UnlockFragment.newInstance());
//            关闭弹窗,否则页面跳转后,popup还存在
            dismiss();
        });
//        继续挑战
        conditionButton.setOnClickListener(v -> {

//            由于此处的view是popup的view,所以使用它不能找到NavController
//            Activity activity = (Activity) context;
//            View view = activity.findViewById(R.id.reward);
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_unlockFragment_to_questionFragment);

            FragmentUtils.remove(UnlockFragment.newInstance());

//            关闭弹窗,否则页面跳转后,popup还存在
            dismiss();
        });
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_unlock);
    }

    /**
     * 设置Popup显示位置
     */
    @Override
    public void showPopupWindow() {
        setOffsetX(-getWidth() - getWidth() / 2);
//        底部显示
        setOffsetY(getScreenHeight());
        super.showPopupWindow();
    }

    /**
     * 返回一个Animator，在BasePopup显示的时候执行，返回为空则不执行动画
     *
     * @return
     */
    @Override
    protected Animator onCreateShowAnimator() {
        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(getDisplayAnimateView(), View.TRANSLATION_Y, getHeight() * 0.75f, 0);
        showAnimator.setDuration(1000);
        showAnimator.setInterpolator(new OvershootInterpolator(6));
        return showAnimator;
    }
}
