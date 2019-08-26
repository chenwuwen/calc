package cn.kanyun.calc.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.orhanobut.logger.Logger;

import cn.kanyun.calc.R;

/**
 * SoundPool 是 Android 提供的一个API类，用来播放简短的音频，使用简单但功能相对强大。只需花很少的气力，
 * 就可以完成音频的播放、暂停、恢复及停止等操作
 * 可以加载多个音频资源到内存（256限制），进行管理与播放
 * 可以设置播放速率，设置范围为0.5 ~ 2.0 ， 正常为 1.0
 * 可以设置播放的重复次数，当设置为-1 则表示会无限循环下去，此时若要停止，需要显示的调用stop() 方法
 * 虽然SoundPool比MediaPlayer的效果好，但也不是绝对不存在延迟问题，尤其在那些性能不太好的手机中，SoundPool的延迟问题会更严重。
 *
 */
public class SoundPoolUtil implements SoundPool.OnLoadCompleteListener {
    private static SoundPoolUtil soundPoolUtil;
    private SoundPool soundPool;
    /**
     * 是否加载完成标志
     */
    private static boolean loaded = false;

    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    private SoundPoolUtil(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // 5.0 及 之后
            AudioAttributes audioAttributes = null;
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(16)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
//            在 5.0 以前 ，直接使用它的构造方法即可，而在这之后，则需要使用Builder模式来创建
            soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);  // 创建SoundPool
        }

        //加载音频文件(可加载多个)

//        回答正确的音频文件
        soundPool.load(context, R.raw.good, 1);
        soundPool.load(context, R.raw.great, 2);
        soundPool.load(context, R.raw.electricity, 3);

//        解锁音效
        soundPool.load(context, R.raw.unbelievable, 4);

//        普通按钮音效文件
        soundPool.load(context, R.raw.click, 1);


//        回答错误音效文件
        soundPool.load(context, R.raw.lose, 6);
        soundPool.setOnLoadCompleteListener(this);


    }

    /**
     * soundPool 的play方法（参数顺序）
     * soundID : 音频的序号(load到SoundPool的顺序，从1开始)
     * leftVolume/rightVolume : 左\右声道的音量控制, 0.0 到 1.0
     * priority : 优先级，0是最低优先级
     * loop : 是否循环播放，0为不循环，-1为循环
     * rate : 播放比率，从0.5到2，一般为1，表示正常播放
     *
     * @param number
     */
    public void play(int number) {
        Logger.d("tag", "number " + number);
        //播放音频(这个方法返回值streamID ， 返回0则播放失败,否则成功,streamID是对流的特定实例的引用，通过 SoundPool 的 pause\resume\stop 方法, 就可以对播放进行控制)
        soundPool.play(number, 1, 1, 0, 0, 1);
    }

    /**
     * 释放资源
     */
    public void releaseSoundPool(int soundId) {
        if (soundPool != null) {
            soundPool.autoPause();
            soundPool.unload(soundId);
            soundPool.release();
            soundPool = null;
        }
    }


    /**
     * 判断是否加载完成。如果在load后就立即播放的话会失败的
     * onLoadComplete方法是用来播放的合理位置,它的调用预示着资源已经就绪,可以进行播放了,
     * 调用 SoundPool 的 play 方法即可进行播放.
     * @param soundPool
     * @param sampleId
     * @param status
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Logger.d("背景音乐资源加载完成");
        loaded = true;
    }
}
