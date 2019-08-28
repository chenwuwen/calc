//package cn.kanyun.calc.listener;
//
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//public class ChipClickListener implements View.OnClickListener {
//    /**
//     * 点击的第一张图和第二张图，他们进行交换
//     */
//    private ImageView mFirst;
//    private ImageView mSecond;
//
//    public ChipClickListener(ImageView mFirst, ImageView mSecond) {
//        this.mFirst = mFirst;
//        this.mSecond = mSecond;
//    }
//
//    /**
//     * 点击事件
//     *
//     * @param v
//     */
//    @Override
//    public void onClick(View v) {
//        //如果点击了一次，你还点击，则无效
//        if (isAnimation) {
//            return;
//        }
//
//        //重复点击
//        if (mFirst == v) {
//            //去掉颜色
//            mFirst.setColorFilter(null);
//            mFirst = null;
//            return;
//        }
//
//        if (mFirst == null) {
//            mFirst = (ImageView) v;
//            //设置选中效果
//            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
//            //第二次点击
//        } else {
//            mSecond = (ImageView) v;
//            //交换
//            exchangeView();
//        }
//    }
//
//    /**
//     * 图片交换
//     */
//    private void exchangeView() {
//        mFirst.setColorFilter(null);
//        // 构造我们的动画层
//        setUpAnimLayout();
//
//        ImageView first = new ImageView(getContext());
//        final Bitmap firstBitmap = mItemBitmaps.get(
//                getImageIdByTag((String) mFirst.getTag())).getBitmap();
//        first.setImageBitmap(firstBitmap);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
//        lp.leftMargin = mFirst.getLeft() - mPadding;
//        lp.topMargin = mFirst.getTop() - mPadding;
//        first.setLayoutParams(lp);
//        mAnimLayout.addView(first);
//
//        ImageView second = new ImageView(getContext());
//        final Bitmap secondBitmap = mItemBitmaps.get(
//                getImageIdByTag((String) mSecond.getTag())).getBitmap();
//        second.setImageBitmap(secondBitmap);
//        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
//        lp2.leftMargin = mSecond.getLeft() - mPadding;
//        lp2.topMargin = mSecond.getTop() - mPadding;
//        second.setLayoutParams(lp2);
//        mAnimLayout.addView(second);
//
//        // 设置动画
//        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft()
//                - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
//        anim.setDuration(300);
//        anim.setFillAfter(true);
//        first.startAnimation(anim);
//
//        TranslateAnimation animSecond = new TranslateAnimation(0,
//                -mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop()
//                + mFirst.getTop());
//        animSecond.setDuration(300);
//        animSecond.setFillAfter(true);
//        second.startAnimation(animSecond);
//
//        // 监听动画
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                mFirst.setVisibility(View.INVISIBLE);
//                mSecond.setVisibility(View.INVISIBLE);
//
//                isAnimation = true;
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//                String firstTag = (String) mFirst.getTag();
//                String secondTag = (String) mSecond.getTag();
//
//                mFirst.setImageBitmap(secondBitmap);
//                mSecond.setImageBitmap(firstBitmap);
//
//                mFirst.setTag(secondTag);
//                mSecond.setTag(firstTag);
//
//                mFirst.setVisibility(View.VISIBLE);
//                mSecond.setVisibility(View.VISIBLE);
//
//                mFirst = mSecond = null;
//                mAnimLayout.removeAllViews();
//
//                //每次移动完成判断是否过关
//                checkSuccess();
//
//                isAnimation = false;
//            }
//        });
//    }
//}
