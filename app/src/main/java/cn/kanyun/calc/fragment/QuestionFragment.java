package cn.kanyun.calc.fragment;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.ImagePiece;
import cn.kanyun.calc.R;
import cn.kanyun.calc.Reward;
import cn.kanyun.calc.Type;
import cn.kanyun.calc.databinding.QuestionFragmentBinding;
import cn.kanyun.calc.service.SoundIntentService;
import cn.kanyun.calc.util.ImageSplitter;
import cn.kanyun.calc.viewmodel.RewardViewModel;
import cn.kanyun.calc.viewmodel.ScoreViewModel;
import es.dmoral.toasty.Toasty;

public class QuestionFragment extends Fragment implements View.OnClickListener {

    private ScoreViewModel mViewModel;
    private RewardViewModel rewardViewModel;

    QuestionFragmentBinding questionFragmentBinding;

    /**
     * 输入的答案
     */
    StringBuilder sb;

    /**
     * 定时器
     */
    Timer timer;

    /**
     * 定时任务
     */
    TimerTask timerTask;

    /**
     * 计数器 [主要记录单次答题用了多少个 100毫秒]
     */
    AtomicInteger atomicInteger = new AtomicInteger(1);

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
//        这是AAC Navigation中传递的参数
        Bundle args = getArguments();
//        这里使用requireActivity()getActivity()都可以
        mViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(ScoreViewModel.class);

        rewardViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(RewardViewModel.class);

        if (args != null && args.getBoolean(Constant.KEY_FLAG_LAST_ERROR)) {
//            如果跳转到该Fragment的上一个Fragment是LoseFragment,那么重置当前得分
            mViewModel.getCurrentScore().setValue(0);
        }
//        第一次进入页面时生成计算式子,否则会显示成 0+0
        if (mViewModel.numberUpperType == Type.MEMBER_GUIDE.number) {
            mViewModel.generator();
        } else {
            mViewModel.generator1();
        }

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

//        答题时长上限
        int timeout = mViewModel.timeoutAnswer;

//        异步处理
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                double consume = Double.parseDouble(msg.obj.toString());
//                double 转int会有精度损失
                int surplus = (int) (100 - consume);
                if (surplus < 0) {
//                如果进度条走完了,则设置答案为 -1,且自动提交
                    sb.setLength(0);
                    sb.append(-1);
//                    View类的performClick和callOnclick函数都可以实现，不用用户手动点击，直接触发View的点击事件(callOnClick是performClick的简化版，不包含点击播放声音，不具有辅助功能)
//                    这里直接使用Navigation跳转到错误页面会报错,
                    questionFragmentBinding.buttonSubmit.callOnClick();
                }

//                设置processbar的进度(如果这个方法不能改变进度条的进度,尝试使用下面的方法)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    questionFragmentBinding.progressBar.setProgress(surplus, true);
                } else {
                    questionFragmentBinding.progressBar.setProgress(surplus);
                }
//                questionFragmentBinding.progressBar.post(() ->
//                        questionFragmentBinding.progressBar.setProgress(surplus, true)
//                );

            }
        };


//        每100毫秒进度条消耗情况
        double unit = (100.00F / (timeout * 1000)) * 100;

//        定时任务处理
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
//                得到Message对象(这种方式性能好)
                Message message = handler.obtainMessage();
//                得到消耗的量
                double consume = unit * atomicInteger.get();
//                message.obj 使用来放对象的，这个对象可以使任何类型,message.what 只能放数字（作用可以使用来做if判断）
                message.obj = consume;
//                计数器加1
                atomicInteger.incrementAndGet();
                Logger.d("TimerTask定时发送消息,已消耗：" + consume);
                handler.sendMessage(message);

            }
        };


//        每100ms刷新一次ui[第一个参数：所要安排执行的任务 第二个参数：time-首次执行任务的时间 第三个参数：period-执行一次task的时间间隔，单位毫秒]
        timer.schedule(timerTask, 100, 100);


        return questionFragmentBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
//        如果奖励碎片list为null,则初始化奖励碎片列表
        if (mViewModel.getImagePieces() == null) {
            List<Reward> imagePieceList = rewardViewModel.getLockReward();
            Reward reward = imagePieceList.get(0);
            AssetManager assetManager = getActivity().getAssets();
            try {
                InputStream inputStream = assetManager.open(reward.getPath());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                List<ImagePiece> imagePieces = ImageSplitter.split(bitmap, ImageSplitter.NINE_GRID[0][0], ImageSplitter.NINE_GRID[0][0]);
                mViewModel.setImagePieces(imagePieces);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
//        按键音效
        SoundIntentService.startActionNormalKey(v.getContext());
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

                int userAnswer;
                try {
                    userAnswer = Integer.parseInt(sb.toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toasty.error(v.getContext(), "请输入合法的值！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userAnswer == mViewModel.getAnswer().getValue()) {
//                    回答正确
                    mViewModel.answerCorrect();
//                    音效
                    SoundIntentService.startActionAnswerResult(v.getContext(), true);

//                    重置进度条和计数器
                    questionFragmentBinding.progressBar.setProgress(100);
                    atomicInteger.set(1);

//                    是否到达解锁分数
                    if (mViewModel.unLock) {
//                        取消定时任务
                        timer.cancel();
                        mViewModel.unLock = false;
//                        解锁碎片音效
                        SoundIntentService.startActionAnswerReward(v.getContext(), 1);
//                        跳转到解锁碎片页面
                        NavController controller = Navigation.findNavController(v);
                        controller.navigate(R.id.action_questionFragment_to_unlockFragment);
                    }

//                    是否到达拼图游戏分数
                    if (mViewModel.game) {
//                        取消定时任务
                        timer.cancel();
                        mViewModel.game = false;
//                        解锁碎片音效
                        SoundIntentService.startActionAnswerReward(v.getContext(), 1);
//                        跳转到拼图游戏页面,同时携带奖励碎片(由于直接携带Bitmap造成数据量过大而报错,所以使用Uri来代替,切图则在目的Fragment进行)
                        Bundle chipImgBundle = new Bundle();
//                        ArrayList<ImagePiece> arrayList = (ArrayList<ImagePiece>) mViewModel.getImagePieces();
//                        chipImgBundle.putSerializable(Constant.KEY_CHIP_IMG_BUNDLE_NAME, arrayList);
                        String path = rewardViewModel.getLockReward().get(0).getPath();
                        Uri uri = null;
                        AssetManager assetManager = getActivity().getAssets();
                        try {
                            InputStream inputStream = assetManager.open(path);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            将Bitmap转换为Uri
                            uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        chipImgBundle.putParcelable(Constant.KEY_SRC_IMG_BUNDLE_NAME, uri);
                        chipImgBundle.putString(Constant.KEY_SRC_IMG_BUNDLE_PATH, path);
                        NavController controller = Navigation.findNavController(v);
                        controller.navigate(R.id.action_questionFragment_to_jigsawPuzzleFragment, chipImgBundle);

//                        获取到了全部的奖励碎片,将会跳转到拼图页面,此时设置碎片list为null
                        mViewModel.setImagePieces(null);
                    }

                } else {
//                  取消定时任务
                    timer.cancel();
                    SoundIntentService.startActionAnswerResult(v.getContext(), false);
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

    /**
     * QuestionFragment销毁时取消TimerTask
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }


}
