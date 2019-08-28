package cn.kanyun.calc.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.kanyun.calc.Constant;
import cn.kanyun.calc.ImagePiece;
import cn.kanyun.calc.listener.GameJigsawPuzzleListener;


public class GameView extends RelativeLayout implements View.OnClickListener {


    //默认3*3
    private int mColumn = 3;
    //容器的内边距
    private int mPadding;
    /**
     * 小图的距离 dp
     */
    private int mMargin = 3;
    /**
     * ImageView数组,将拼图资源碎片中的bitmap放置到该数组中
     * 这个ImageView 也是最终显示在界面上的
     * 存储图片的，宽高 都是固定的，所以使用数组
     */
    private ImageView[] imageViewItems;

    /**
     * 每个ImageView的宽度
     * 也就是存放每一个奖励碎片的ImageView的宽度
     */
    private int mItemWidth;
    /**
     * 这个Bitmap是完整图片的Bitmap 也就是完整图片的位图
     * 当需要该类进行切图(或者查看原图)时,会被使用,由于切图的操作在该类外部
     * 进行,因此暂时无需使用
     */
    private Bitmap mBitmap;
    /**
     * 切图后存储,存放的是实体类
     * 实体类中包含Bitmap,也就是存储拼图的碎片资源
     */
    public List<ImagePiece> mItemBitmaps;
    //标记
    private boolean once;

    //记录时间
    private int mTime;

    /**
     * 存放全部奖励碎片的容器的宽度
     */
    private int mWidth;

    //判断游戏是否成功
    private boolean isGameSuccess;

    //是否显示时间
    private boolean isTimeEnabled = false;

    /**
     * 动画层，覆盖在viewGroup中
     */
    private RelativeLayout mAnimLayout;

    private boolean isGameOver;
    /**
     * 动画限制
     */
    private boolean isAnimation;

    private static final int TIME_CHANGED = 10;
    private static final int NEXT_LEVEL = 11;

    /**
     * 设置接口回调
     */
    public GameJigsawPuzzleListener gameJigsawPuzzleListener;


    public void setOnGameListener(GameJigsawPuzzleListener gameJigsawPuzzleListener) {

        this.gameJigsawPuzzleListener = gameJigsawPuzzleListener;
    }


    /**
     * 关数
     */
    private int level = 1;

    /**
     * 设置开启时间
     *
     * @param timeEnabled
     */
    public void setTimeEnabled(boolean timeEnabled) {
        isTimeEnabled = timeEnabled;
    }

    //子线程操作
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_CHANGED:
                    if (isGameSuccess || isGameOver || isPause) {
                        return;
                    }

                    if (gameJigsawPuzzleListener != null) {
                        gameJigsawPuzzleListener.timeChanged(mTime);
                        if (mTime == 0) {
                            isGameOver = true;
                            gameJigsawPuzzleListener.gameOver();
                            return;
                        }
                    }
                    mTime--;
                    handler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);

                    break;
                case NEXT_LEVEL:

                    level = level + 1;

                    if (gameJigsawPuzzleListener != null) {

                        gameJigsawPuzzleListener.nextLevel(level);
                    } else {
                        nextLevel();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public GameView(Context context) {

        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    /**
     * 初始化
     * 该方法先于onMeasure()方法执行
     */
    private void init() {
        //单位转换——dp-px
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());

        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());
    }

    /**
     * 确定当前布局的大小，我们要设置成正方形
     * onMeasure （ 比onDraw先执行 ）作用
     * （1）一般情况重写onMeasure()方法作用是为了自定义View尺寸的规则，
     * 如果你的自定义View的尺寸是根据父控件行为一致，就不需要重写onMeasure()方法
     * （2）如果不重写onMeasure方法，那么自定义view的尺寸默认就和父控件一样大小，
     * 当然也可以在布局文件里面写死宽高，而重写该方法可以根据自己的需求设置自定义view大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //拿到容器的高宽最小值
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

        if (!once) {
            //进行切图和排序
            initBitmap();

            //设置ImageView(item)的宽高等属性,同时放置碎片加入到ViewGroup
            initItem();

            //根据关卡设置时间
            checkTimeEnable();

            once = true;

        }
        setMeasuredDimension(mWidth, mWidth);
    }

    /**
     * 是否显示时间
     */
    private void checkTimeEnable() {
        //如果我们开启了
        if (isTimeEnabled) {
            countTimeBaseLevel();
            handler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    /**
     * 根据当前等级设置时间
     */
    private void countTimeBaseLevel() {
        mTime = (int) Math.pow(2, level) * 60;
    }

    /**
     * 进行切图和排序
     */
    private void initBitmap() {

//        //判断是否存在这张图片
//        if (mBitmap == null) {
//            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mm);
//        }
//        //进行裁剪
//        mItemBitmaps = ImageSplitter.split(mBitmap, mColumn);


//        裁剪完后需要进行顺序打乱sort
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece lhs, ImagePiece rhs) {

                //生成随机数，如果》0.5返回1否则返回-1
                return Math.random() > 0.5 ? 1 : -1;
            }
        });

    }

    /**
     * 放置奖励碎片
     */
    private void initItem() {
        //（ 容器的宽度 - 内边距 * 2  - 间距  ） /  裁剪的数量
        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;
        //初始化数组,当前要显示多少数量的ImageView 几 * 几
        imageViewItems = new ImageView[mColumn * mColumn];

        //开始排放
        for (int i = 0; i < imageViewItems.length; i++) {
            ImageView item = new ImageView(getContext());
//            设置单个ImageView的点击监听
            item.setOnClickListener(this);
            //设置图片(此时mItemBitmaps已经是乱序的了)
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
            //保存
            imageViewItems[i] = item;
            //设置ID
            item.setId(i + 1);
            //设置Tag(tag中包含了碎片资源的索引,之后判断拼图是否成功的时候,可以根据Tag来判断)
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());
            int mheight = mItemBitmaps.get(i).getBitmap().getHeight();
//            获得布局(即ImageView布局),并配置布局参数
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mheight);

            //判断不是最后一列
            if (i + 1 % mColumn != 0) {
                lp.rightMargin = mMargin;
            }

            //判断不是第一列
            if (i % mColumn != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF, imageViewItems[i - 1].getId());
            }

            //判断如果不是第一行
            if ((i + 1) > mColumn) {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, imageViewItems[i - mColumn].getId());
            }
            addView(item, lp);
        }
    }

    /**
     * 获取多个参数的最小值
     */
    private int min(int... params) {
        int min = params[0];
        //遍历
        for (int param : params) {
            if (param < min) {
                min = param;
            }
        }
        return min;
    }

    /**
     * 点击的第一张图和第二张图，他们进行交换
     */
    private ImageView mFirst;
    private ImageView mSecond;

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        //如果点击了一次，你还点击，则无效
        if (isAnimation) {
            return;
        }

        //重复点击
        if (mFirst == v) {
            //去掉颜色
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }

        if (mFirst == null) {
            mFirst = (ImageView) v;
            //设置选中效果
            mFirst.setColorFilter(Color.parseColor(Constant.KEY_CLICK_IMG_COVER_COLOR));
            //第二次点击
        } else {
            mSecond = (ImageView) v;
            //交换
            exchangeView();
        }
    }

    /**
     * 图片交换
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);
        // 构造我们的动画层
        setUpAnimLayout();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        // 设置动画
        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft()
                - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0,
                -mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop()
                + mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);

        // 监听动画
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);

                isAnimation = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();

                //每次移动完成判断是否过关
                checkSuccess();

                isAnimation = false;
            }
        });
    }

    /**
     * 判断是否过关
     */
    private void checkSuccess() {
        boolean isSuccess = true;
//        遍历全部图片
        for (int i = 0; i < imageViewItems.length; i++) {
            ImageView imageView = imageViewItems[i];
//            查看ImageView的Tag(这个地方需要注意字符串分割),然后跟数组下标对比,如果全部一致,则表示拼图成功
            if (getImageIndex((String) imageView.getTag()) != i) {
                isSuccess = false;
                break;
            }
        }
        if (isSuccess) {

            isGameSuccess = true;

            handler.removeMessages(TIME_CHANGED);

            Log.i("tag", "成功");
            Toast.makeText(getContext(), "成功,进入下一关！", Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(NEXT_LEVEL);

        }
    }


    /**
     * 获取tag
     *
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    /**
     * 获取图片的tag
     * 分割字符串
     * @param tag
     * @return
     */
    public int getImageIndex(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 交互动画
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            //添加到整体
            addView(mAnimLayout);
        }
    }

    /**
     * 下一关
     */
    public void nextLevel() {
        this.removeAllViews();
        mAnimLayout = null;
        mColumn++;
        isGameSuccess = false;
        checkTimeEnable();
        initBitmap();
        initItem();
    }

    /**
     * 重新开始
     */
    public void restartGame() {
        isGameOver = false;
        mColumn--;
        nextLevel();

    }

    //暂停状态
    private boolean isPause;

    /**
     * 暂停
     */
    public void pauseGame() {
        isPause = true;
        handler.removeMessages(TIME_CHANGED);
    }


    /**
     * 恢复
     */
    public void resumeGame() {
        if (isPause) {
            isPause = false;
            handler.sendEmptyMessage(TIME_CHANGED);
        }
    }
}
