package cn.kanyun.calc.widget;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import cn.kanyun.calc.R;
import razerdp.basepopup.BasePopupWindow;

public class UnlockPopup extends BasePopupWindow {

    Button returnButton, conditionButton;

    public UnlockPopup(Context context) {
        super(context);

        returnButton = findViewById(R.id.unlock_return_button);
        conditionButton = findViewById(R.id.unlock_condition_button);
//        返回
        returnButton.setOnClickListener(v->{
//            由于此处的view是popup的view,所以使用它不能找到NavController

//            View view = (View) v.getParent().getParent().getParent().getParent().getParent();
            NavController navController = Navigation.findNavController(getContentView());
            navController.navigate(R.id.action_unlockFragment_to_mainFragment);
        });
//        继续挑战
        conditionButton.setOnClickListener(v -> {
//            由于此处的view是popup的view,所以使用它不能找到NavController
            NavController navController = Navigation.findNavController(v.findFocus());
            navController.navigate(R.id.action_unlockFragment_to_questionFragment);
        });
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_unlock);
    }
}
