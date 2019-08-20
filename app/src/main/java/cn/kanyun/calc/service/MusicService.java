package cn.kanyun.calc.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import cn.kanyun.calc.R;

/**
 * 在活动里调用了 startService 方法之后启动Service服务。
 * 然后service 的 oncreate 和 onstartCommand 方法就会得到执行。之后服务会一直处于运行状态，
 * 但具体的运行的什么逻辑，活动就已经控制不了[例如我在设置页面,关闭背景音]。那就是代表我们无法通过组件对service进行控制吗，
 * 不不不 ，不是的。这时候我们就要用到我们 service 中继承的另一个方法 onbind 方法
 * 绑定服务是Service的另一种变形，当Service处于绑定状态时，其代表着客户端-服务器接口中的服务器。
 * 当其他组件（如 Activity）绑定到服务时（有时我们可能需要从Activity组建中去调用Service中的方法，此时Activity以绑定的方式挂靠到Service后，我们就可以轻松地方法到Service中的指定方法），
 * 组件（如Activity）可以向Service（也就是服务端）发送请求，
 * 或者调用Service（服务端）的方法，此时被绑定的Service（服务端）会接收信息并响应，
 * 甚至可以通过绑定服务进行执行进程间通信 (即IPC，这个后面再单独分析)。
 * 与启动服务不同的是绑定服务的生命周期通常只在为其他应用组件(如Activity)服务时处于活动状态，
 * 不会无限期在后台运行，也就是说宿主(如Activity)解除绑定后，绑定服务就会被销毁。
 * 为了实现客户端与服务器的交互，我们一般都会通过下方三种方式进行处理：
 * 1.扩展 Binder 类
 * 2.使用 Messenger
 * 3.使用 AIDL
 */
public class MusicService extends Service {

    MediaPlayer mediaPlayer;

    public MusicService() {
    }

    /**
     * 定义待播放的音乐数组
     */
    private static int[] musics = {R.raw.bg0, R.raw.bg1, R.raw.bg2, R.raw.bg3};

    /**
     * 已播放资源的次数
     * 通过这个变量 对 播放列表 进行取模 用以 获得下次播放的音乐
     */
    AtomicInteger number = new AtomicInteger(1);

    /**
     * 当另一个组件想通过调用 bindService() 与服务绑定（例如执行 RPC）时，系统将调用此方法。
     * 在此方法的实现中，必须返回 一个IBinder 接口的实现类，供客户端用来与服务进行通信。
     * 无论是启动状态还是绑定状态，此方法必须重写，但在启动状态的情况下直接返回 null。
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBind();
    }

    /**
     * 扩展Binder类
     */
    public class MusicBind extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }


    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序
     * （在调用 onStartCommand() 或onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法，该方法只调用一次
     */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        // 初始化音乐资源
        try {
            // 创建MediaPlayer对象
            mediaPlayer = new MediaPlayer();
            // 将音乐保存在res/raw/ 路径中
            mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.bg0);
            // 在MediaPlayer取得播放资源与stop()之后要准备PlayBack的状态前一定要使用MediaPlayer.prepeare()
            mediaPlayer.prepare();

            number = new AtomicInteger(0);
        } catch (IllegalStateException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        super.onCreate();
    }

    /**
     * 当另一个组件（如 Activity）通过调用 startService() 请求启动服务时，系统将调用此方法。
     * 一旦执行此方法，服务即会启动并可在后台无限期运行。
     * 如果自己实现此方法，则需要在服务工作完成后，通过调用 stopSelf() 或 stopService() 来停止服务。
     * （在绑定状态下，无需实现此方法。）
     * startService()方法会调用该方法 bindService()方法不会调用该方法
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 开始播放音乐
        if (null != mediaPlayer) {
//            设置为循环播放
//            mediaPlayer.setLooping(true);
            mediaPlayer.start();

//            当前播放次数+1(由于onStartCommand()方法的最后一行代码是调用超类的代码,会导致number被添加多次,因此不在此处+1)
//            number.incrementAndGet();

            // 音乐播放完毕的事件处理(音乐播放完的监听器),在这里用来进行播放下一首音乐
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    try {
                        Logger.d("一首背景音乐播放完成,开始切换音乐");
                        if (number.get() % musics.length == 1) {
                            mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.bg1);
                        } else if (number.get() % musics.length == 2) {
                            mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.bg2);
                        } else if (number.get() % musics.length == 3) {
                            mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.bg3);
                        } else {
                            mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.bg0);
                        }

                        number.incrementAndGet();

//                        重要：实现顺序切换的代码
                        onStartCommand(intent, flags, startId);
                    } catch (IllegalStateException  e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
        // 播放音乐时发生错误的事件处理
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int extra) {
                Logger.d("MediaPlayer播放背景音乐发生错误.....");
                // 释放资源
                try {
                    if (mediaPlayer != null) {
                        mp.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 当服务不再使用且将被销毁时，系统将调用此方法。
     * 服务应该实现此方法来清理所有资源，如线程、注册的侦听器、接收器等，这是服务接收的最后一个调用。
     */
    @Override
    public void onDestroy() {

        // 服务停止时停止播放音乐并释放资源
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    /**
     * 控制音乐暂停/或者继续
     * 供外部调用(所以需要扩展Binder类,同时外部需要实现ServiceConnection接口)
     */
    public void controlMusic(boolean flag) {
        if (mediaPlayer.isPlaying()) {
            if (!flag) {
//                暂停播放器播放
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        } else {
            if (!flag) {
//                暂停播放器播放
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

}

