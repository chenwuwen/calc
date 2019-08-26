package cn.kanyun.calc.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;

import cn.kanyun.calc.fragment.QuestionFragment;
import cn.kanyun.calc.util.SoundPoolUtil;
import cn.kanyun.calc.viewmodel.SettingViewModel;

/**
 * IntentService，可以看做是Service和HandlerThread的结合体， 作用 :处理异步请求，实现多线程
 * 在完成了使命之后会自动停止，适合需要在工作线程处理UI无关任务的场景。
 * IntentService 是继承自 Service 并处理异步请求的一个类，在 IntentService 内有一个工作线程来处理耗时操作。
 * 当任务执行完后，IntentService 会自动停止，不需要我们去手动结束。
 * 如果启动 IntentService 多次，那么每一个耗时操作会以工作队列的方式在 IntentService 的 onHandleIntent 回调方法中执行，
 * 依次去执行，使用串行的方式，执行完自动结束。
 * <p>
 * 我们知道Service可以通过startService和bindService这两种方式启动。
 * 当然喽，IntentService是继承自Service的，自然也是可以通过上面两种方式启动。但是呢，是不建议使用 bindService 去启动的
 * <p>
 * 实现步骤:
 * 步骤1：定义IntentService的子类：传入线程名称、复写onHandleIntent()方法
 * 步骤2：在Manifest.xml中注册服务
 * 步骤3：在Activity中开启Service服务
 * 注意：若启动IntentService 多次，那么每个耗时操作则以队列的方式在 IntentService的onHandleIntent回调方法中依次执行，执行完自动结束。
 */
public class SoundIntentService extends IntentService {


    /**
     * 定义SoundPoolUtil
     */
    private static SoundPoolUtil soundPoolUtil;

    // 定义Action

    /**
     * 普通按钮点击Action ,如1,2，3
     */
    private static final String ACTION_NORMAL_KEY_CLICK = "cn.kanyun.calc.service.action.ACTION_NORMAL_KEY_CLICK";

    /**
     * 处理回答
     */
    private static final String ACTION_ANSWER = "cn.kanyun.calc.service.action.ANSWER";

    /**
     * 奖励
     */
    private static final String ACTION_REWARD = "cn.kanyun.calc.service.action.REWARD";

    /**
     * 定义一些参数
     * 回答是否正确
     */
    private static final String EXTRA_PARAM_RESULT = "cn.kanyun.calc.service.extra.PARAM_RESULT";


    /**
     * 打开状态
     */
    public static int openSate = 1;

    public SoundIntentService() {
        super("SoundIntentService");
    }



    /**
     * 复写onStartCommand()方法
     * 默认实现将请求的Intent添加到工作队列里
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 启动Action,启动Service
     * 外部直接调用该方法
     *
     * @see IntentService
     */
    public static void startActionNormalKey(Context context) {
        if (openSate == 1) {
            if (soundPoolUtil == null) {
                soundPoolUtil = SoundPoolUtil.getInstance(context);
            }
            Intent intent = new Intent(context, SoundIntentService.class);
            intent.setAction(ACTION_NORMAL_KEY_CLICK);
            context.startService(intent);
        }
    }

    /**
     * 回答问题的调用
     *
     * @see IntentService
     */
    public static void startActionAnswerResult(Context context, boolean param1) {
        if (openSate == 1) {
            if (soundPoolUtil == null) {
                soundPoolUtil = SoundPoolUtil.getInstance(context);
            }
            Intent intent = new Intent(context, SoundIntentService.class);
            intent.setAction(ACTION_ANSWER);
            intent.putExtra(EXTRA_PARAM_RESULT, param1);
            context.startService(intent);
        }
    }

    /**
     * 奖励Action
     *
     * @param context
     * @param param1
     */
    public static void startActionAnswerReward(Context context, int param1) {
        if (openSate == 1) {
            if (soundPoolUtil == null) {
                soundPoolUtil = SoundPoolUtil.getInstance(context);
            }
            Intent intent = new Intent(context, SoundIntentService.class);
            intent.setAction(ACTION_REWARD);
            intent.putExtra(EXTRA_PARAM_RESULT, param1);
            context.startService(intent);
        }
    }

    /**
     * 复写onHandleIntent()方法
     * 实现耗时任务的操作
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
//            根据Intent的不同进行不同的事务处理
            if (ACTION_NORMAL_KEY_CLICK.equals(action)) {
                handleActionNormalKeyClick();
            } else if (ACTION_ANSWER.equals(action)) {
                final boolean param1 = intent.getBooleanExtra(EXTRA_PARAM_RESULT, false);
                handleActionAnswerResult(param1);
            } else {
                final int param1 = intent.getIntExtra(EXTRA_PARAM_RESULT, 1);
                handleActionReward(param1);
            }
        }
    }

    /**
     * 处理奖励音效
     *
     * @param param1
     */
    private void handleActionReward(int param1) {
        if (param1 == 1) {
//            获取一个碎片
            soundPoolUtil.play(4);
        }
    }

    /**
     * 真正处理Action的方法
     * 处理点击普通按钮时的音效
     * 这个方法,在IDE中会默认生成throw new UnsupportedOperationException("Not yet implemented");的实现
     * 在自己进行实现后，要IDE自动生成的抛出异常的代码删除掉
     */
    private void handleActionNormalKeyClick() {
        soundPoolUtil.play(5);
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 处理回答结果音效的方法
     */
    private void handleActionAnswerResult(boolean param1) {
        if (param1) {
//            正确回答的音效【随机？】
            Random random = new Random();
            int num = random.nextInt(2);
            soundPoolUtil.play(num);
        } else {
//            错误回答的音效
            soundPoolUtil.play(6);
        }
    }


}
