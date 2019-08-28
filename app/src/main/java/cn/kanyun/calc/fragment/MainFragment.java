package cn.kanyun.calc.fragment;

import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.ImagePiece;
import cn.kanyun.calc.MyApplication;
import cn.kanyun.calc.R;
import cn.kanyun.calc.Reward;
import cn.kanyun.calc.databinding.FragmentMainBinding;
import cn.kanyun.calc.util.ImageSplitter;
import cn.kanyun.calc.viewmodel.RewardViewModel;
import cn.kanyun.calc.viewmodel.ScoreViewModel;
import es.dmoral.toasty.Toasty;


public class MainFragment extends Fragment {

    FragmentMainBinding mainFragmentBinding;

    ScoreViewModel scoreViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        scoreViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(ScoreViewModel.class);
        mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        mainFragmentBinding.setMainData(scoreViewModel);
        mainFragmentBinding.setLifecycleOwner(requireActivity());


//        跳转到游戏页面
        mainFragmentBinding.buttonBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_mainFragment_to_questionFragment);
            }
        });
//        跳到设置页面
        mainFragmentBinding.buttonSetting.setOnClickListener(v -> {
            NavController controller = Navigation.findNavController(v);
            controller.navigate(R.id.action_mainFragment_to_settingFragment);
        });

//        跳转到我的页面(奖励查看)
        mainFragmentBinding.buttonMeReward.setOnClickListener(v -> {
            NavController controller = Navigation.findNavController(v);
            controller.navigate(R.id.action_mainFragment_to_meFragment);
        });

//        直接跳转到游戏界面(秘籍)【该按钮只在DEBUG版本中显示】
        mainFragmentBinding.buttonSecretBook.setOnClickListener(v -> {
//            从主页面点击秘籍按钮进入游戏页面,需要指定资源并携带参数
            AssetManager assetManager = getContext().getAssets();
            InputStream inputStream = null;
            RewardViewModel rewardViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(RewardViewModel.class);
            String path = "";
            try {
//                获得未获取的奖励
                List<Reward> lockRewardList = rewardViewModel.getLockReward();
                if (lockRewardList.size() == 0) {
                    Toasty.warning(getContext(), "已获取全部奖励", Toast.LENGTH_LONG).show();
                    return;
                }
                path = lockRewardList.get(0).getPath();
                inputStream = assetManager.open(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 获取 BitmapFactory.Options，这里面保存了很多有关 Bitmap 的设置
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 设置 true 轻量加载图片信息
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            Bitmap转Uri
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null));
            Bundle bundle = new Bundle();
//            之前的写法是,在该Fragment进行切图,并且把原图的Bitmap对象传递到JigsawPuzzleFragment中,但是这样的话数据量
//            会比较大,导致当从JigsawPuzzleFragment跳转到ShowSrcImageActivity时报错,所以改成将原图的Bitmap对象转换成Uri
//            同时切图也在JigsawPuzzleFragment中进行
//            ArrayList<ImagePiece> imagePieceList = (ArrayList<ImagePiece>) ImageSplitter.split(bitmap, 3, 3);
//            bundle.putSerializable(Constant.KEY_CHIP_IMG_BUNDLE_NAME, imagePieceList);
            bundle.putParcelable(Constant.KEY_SRC_IMG_BUNDLE_NAME, uri);
            bundle.putString(Constant.KEY_SRC_IMG_BUNDLE_PATH, path);

            NavController controller = Navigation.findNavController(v);
            controller.navigate(R.id.action_mainFragment_to_jigsawPuzzleFragment, bundle);

        });

//        判断当前版本是否是DEBUG版本(DEBUG版本将显示秘籍按钮,RELEASE版本隐藏秘籍按钮,默认是显示的)
        ApplicationInfo applicationInfo = getContext().getApplicationInfo();
        if ((applicationInfo.flags & applicationInfo.FLAG_DEBUGGABLE) == 0) {
//            能进入表示当前版本是RELEASE版本
            mainFragmentBinding.buttonSecretBook.setVisibility(View.GONE);
        }
        return mainFragmentBinding.getRoot();
    }
}
