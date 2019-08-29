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

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.R;
import cn.kanyun.calc.service.MusicService;
import cn.kanyun.calc.service.SoundIntentService;
import cn.kanyun.calc.viewmodel.SettingViewModel;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PlayActivity extends AppCompatActivity implements ServiceConnection {

    /**
     * 申请权限码
     */
    private static final int PERMISSION_STORAGE_CODE = 0X22;

    NavController controller;

    SettingViewModel settingViewModel;

    MusicService musicService;

    /**
     * 权限
     */
    private static final String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};


    /**
     * onCreate方法的参数是一个Bundle类型的参数。Bundle类型的数据与Map类型的数据相似，都
     * 是以key-value的形式存储数据的。
     * 从字面上看savedInstanceState，是保存实例状态的。实际上，savedInstanceState也就是保存Activity的状态的。
     * 那么，savedInstanceState中的状态数据是从何处而来的呢?
     * 其实就是从Activity的另一个方法saveInstanceState来的
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        controller = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, controller);
//        注册EventBus
        EventBus.getDefault().register(this);

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
//        申请权限
        EasyPermissions.requestPermissions(this, "", PERMISSION_STORAGE_CODE, perms);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d("PlayActivity onActivityResult()执行");
        if (requestCode == Constant.KEY_PLAY_ACTIVITY_TO_SHOW_IMAGE_ACTIVITY_REQUEST_CODE) {
            Logger.d("由ShowSrcImageActivity回到PlayActivity");

        }
    }

    /**
     * onSaveInstanceState方法是用来保存Activity的状态的。当一个Activity在生命周期结束前，
     * 会调用该方法保存状态。这个方法有一个参数名称与onCreate方法参数名称相同
     * 在实际应用中，当一个Activity结束前，如果需要保存状态，就在onSaveInstanceState中，
     * 将状态数据以key-value的形式放入到savedInstanceState中。
     * 这样，当一个Activity被创建时，就能从onCreate的参数savedInstanceState中获得状态数据
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    /**
     * EventBus订阅者 用于控制背景音乐开关
     *
     * @param state
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void controlMusic(Boolean state) {
        Logger.d("控制背景音乐的订阅者收到消息");
        if (state) {
            musicService.controlMusic(state.booleanValue());
        } else {
            musicService.controlMusic(state.booleanValue());
        }
    }

    /**
     * EventBus订阅者 用于控制背景音效开关
     * 这里之所以写在Activity中(本来写在SoundIntentService中是最好的),是因为SoundIntentService中定义了
     * 几个静态方法(IDE自动生成的,方便外部调用),而该静态方法则startService() 启动了服务,因此在EventBus
     * 进行注册时,并不能放在onCreate()方法中(因为执行startService()后才会执行onCrate()方法),所以放在Activity方法中
     *
     * @param state
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void contolSound(Integer state) {
        Logger.d("控制音效的订阅者收到消息");
        SoundIntentService.openSate = state.intValue();
    }

    /**
     * 申请权限结果
     */
    @AfterPermissionGranted(PERMISSION_STORAGE_CODE)
    public void perResult() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 已经赋予了权限
            Logger.d("被@AfterPermissionGranted注解的方法被执行,且已经获取到权限");
        } else {
            Logger.d("被@AfterPermissionGranted注解的方法被执行,但是没有获取到权限,将继续进行申请");
            // 没有赋予权限，此时请求权限
//            第一个参数：Context对象  第二个参数：权限弹窗上的文字提示语。告诉用户，这个权限用途。 第三个参数：这次请求权限的唯一标示，code。 第四个参数 : 一些系列的权限
            EasyPermissions.requestPermissions(this, "请确认允许权限,这将打开您的相机为您制作个性皮肤",
                    PERMISSION_STORAGE_CODE, perms);
        }
    }
}
