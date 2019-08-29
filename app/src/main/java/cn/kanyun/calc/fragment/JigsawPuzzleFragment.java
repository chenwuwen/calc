package cn.kanyun.calc.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateVMFactory;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.blankj.utilcode.util.ActivityUtils;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.ImagePiece;
import cn.kanyun.calc.MyApplication;
import cn.kanyun.calc.R;
import cn.kanyun.calc.activity.ShowSrcImageActivity;
import cn.kanyun.calc.databinding.FragmentJigsawPuzzleBinding;
import cn.kanyun.calc.listener.GameJigsawPuzzleListener;
import cn.kanyun.calc.util.ImageSplitter;
import cn.kanyun.calc.viewmodel.RewardViewModel;
import es.dmoral.toasty.Toasty;

/**
 * 拼图Fragment
 */
public class JigsawPuzzleFragment extends Fragment {

    /**
     * 奖励图片 碎片列表
     */
    private static List<ImagePiece> imagePieceList;

    private static Bitmap srcBitmap;
    private static String srcImagePath;

    FragmentJigsawPuzzleBinding fragmentJigsawPuzzleBinding;

    RewardViewModel rewardViewModel;

    public JigsawPuzzleFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            获取奖励原图
            Uri uri = getArguments().getParcelable(Constant.KEY_SRC_IMG_BUNDLE_NAME);
            srcImagePath = getArguments().getString(Constant.KEY_SRC_IMG_BUNDLE_PATH);
            try {
//                uri 转换 bitmap
                srcBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
//                切图
                imagePieceList = ImageSplitter.split(srcBitmap, ImageSplitter.NINE_GRID[0][0], ImageSplitter.NINE_GRID[0][0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentJigsawPuzzleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_jigsaw_puzzle, container, false);
        rewardViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(RewardViewModel.class);

//        Chronometer是Android自带的一个简单的计时器
        fragmentJigsawPuzzleBinding.chronometer.setFormat("计时开始 %s");
        fragmentJigsawPuzzleBinding.chronometer.start();

//        查看原图按钮点击事件
        fragmentJigsawPuzzleBinding.viewSrcImgButton.setOnClickListener(v -> {
//            将原图的Bitmap放到MyApplication的静态变量中
            MyApplication.showBitmap = srcBitmap;
            Intent intent = new Intent(getActivity(), ShowSrcImageActivity.class);
//            由于Activity间传递数据有大小限制(40k),所以这种方式会报错 FAILED BINDER TRANSACTION
//            那么为什么在Fragment间传递不报错呢?那是因为Fragment寄生在Activity下,每个Activity都是一个进程,所以Fragment间传递没有问题
//            解决方式是采用全局变量的方式来做,根据不同的场景也可以尝试传递Uri
//            intent.putExtra(Constant.KEY_ACTIVITY_SRC_IMAGE_BITMAP, srcBitmap);
            startActivityForResult(intent, Constant.KEY_PLAY_ACTIVITY_TO_SHOW_IMAGE_ACTIVITY_REQUEST_CODE);
        });

//        待拼图的碎片
        fragmentJigsawPuzzleBinding.gameView.mItemBitmaps = imagePieceList;
//        显示时间
        fragmentJigsawPuzzleBinding.gameView.setTimeEnabled(true);
        fragmentJigsawPuzzleBinding.gameView.setOnGameListener(new GameJigsawPuzzleListener() {
            @Override
            public void nextLevel(int nextLevel) {
                Logger.d("完成拼图游戏");
                Toasty.success(getContext(), "恭喜你获得该奖励,快去仓库看看吧！", Toast.LENGTH_SHORT).show();
//                将奖励报错到SharedPrefences
                rewardViewModel.addWinReward(srcImagePath);
                fragmentJigsawPuzzleBinding.chronometer.stop();
                NavController controller = Navigation.findNavController(fragmentJigsawPuzzleBinding.gameView);
                controller.navigateUp();
            }

            @Override
            public void timeChanged(int time) {
                Logger.d("拼图游戏时间改变");
            }

            @Override
            public void gameOver() {
                Logger.d("拼图游戏结束,失败！");

            }
        });
        return fragmentJigsawPuzzleBinding.getRoot();
    }

    @Override
    public void onStart() {
        Logger.d("JigsawPuzzleFragment onStart()方法执行");
        super.onStart();
    }

    @Override
    public void onStop() {
        Logger.d("JigsawPuzzleFragment onStop()方法执行");
        super.onStop();
    }

    @Override
    public void onResume() {
        Logger.d("JigsawPuzzleFragment onResume()方法执行");
        super.onResume();
    }

    @Override
    public void onPause() {
        Logger.d("JigsawPuzzleFragment onPause()方法执行");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Logger.d("JigsawPuzzleFragment onDestroy()方法执行");
        fragmentJigsawPuzzleBinding.chronometer.stop();
        super.onDestroy();
    }
}
