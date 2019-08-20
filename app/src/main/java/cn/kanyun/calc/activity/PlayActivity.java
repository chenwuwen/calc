package cn.kanyun.calc.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.View;

import cn.kanyun.calc.R;
import cn.kanyun.calc.service.MusicService;
import cn.kanyun.calc.viewmodel.SettingViewModel;

public class PlayActivity extends AppCompatActivity implements ServiceConnection {

    NavController controller;

    SettingViewModel settingViewModel;

    MusicService musicService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        controller = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, controller);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

//        实例化SettingViewModel（由于这个ViewModel并不与该Fragment/Activity进行绑定(仅用来判断是否播放背景音乐),所以不需要 sefData）
        settingViewModel = ViewModelProviders.of(this, new SavedStateVMFactory(this)).get(SettingViewModel.class);

//        判断背景音乐是否开启
        if (settingViewModel.getBackGroundMusic().getValue()) {
            Intent musicService = new Intent(this, MusicService.class);
//            startService(service)只能单纯的开启一个服务，要想调用服务服务中的方法，必须用bindService和unbindService
            startService(musicService);

//            https://blog.csdn.net/u014520745/article/details/49669641
//            https://blog.csdn.net/ican87/article/details/82945867
//            (混合使用)使用bindService方式(在这里如果既需要进入Activity时背景音乐响起,并且重复播放,还能可以实现控制背景音乐,就需要startService()/bindService混合使用)
            bindService(musicService, this, Context.BIND_AUTO_CREATE);
        }
        return super.onCreateView(name, context, attrs);
    }

    /**
     * Activity销毁时的回调方法
     */
    @Override
    protected void onDestroy() {
//        取消绑定Service
        unbindService(this);
        super.onDestroy();
    }

    /**
     * 失去焦点时,暂停背景音乐
     */
    @Override
    protected void onStop() {
        if (musicService != null) {
            musicService.controlMusic(false);
        }
        super.onStop();
    }

    /**
     * 获取焦点时,继续背景音乐
     */
    @Override
    protected void onResume() {
        if (musicService != null) {
            musicService.controlMusic(true);
        }
        super.onResume();
    }

    /**
     * android默认的Toolbar没有返回按钮
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        if (controller.getCurrentDestination().getId() == R.id.action_global_mainFragment) {
//        如果当前位置是mainFragment,则应用退出
            finish();
        } else if (controller.getCurrentDestination().getId() == R.id.questionFragment) {
//            如果当前位置是questionFragment,则弹出弹窗提示是否返回上一级
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.quit_dialog_title));
            builder.setMessage(R.string.quit_dialog_message);
            builder.setPositiveButton(R.string.dialog_positive_message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    controller.navigateUp();
                }
            });
            builder.setNegativeButton(R.string.dialog_negative_message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            controller.navigate(R.id.mainFragment);
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }




    /**
     * 建立Service连接的回调方法
     *
     * @param name
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
//        获得Service对象(此时就可以使用这个对象去调用service中自定义的方法了)
        musicService = ((MusicService.MusicBind) service).getMusicService();
    }

    /**
     * 失去Service连接的回调方法
     *
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }


}
