package com.leiyaqiang.lenovo.lznbarrage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by lenovo on 2017/5/3.
 */

public class LznView  extends RelativeLayout {
    private Context mContext;
    private BarrageHandler mHandler = new BarrageHandler();
    private Random random = new Random(System.currentTimeMillis());
    //两个弹幕的最小间隔时间
    private static final long BARRAGE_GAP_MIN_DURATION = 1000;
    //两个弹幕的最大间隔时间
    private static final long BARRAGE_GAP_MAX_DURATION = 2000;
    //速度，ms
    private int maxSpeed = 10000;
    //速度，ms
    private int minSpeed = 5000;
    //文字大小，dp
    private int maxSize = 30;
    //文字大小，dp
    private int minSize = 15;

    private int totalHeight = 0;
    //每一行弹幕的高度
    private int lineHeight = 0;
    //弹幕的行数
    private int totalLine = 0;
    private String[] itemText = {"挺喜欢的一首歌", "真有这样的人嘛", "估计也就是说说", "抢占沙发。。。。。。", "************", "这样的男朋友来一打",
            "下一个如果是女生，告诉他我喜欢她", "楼上单身狗", "这是我见过的最长长长长长长长长长长长的评论"};
    private int textCount;

    public LznView(Context context) {
        this(context, null);
    }

    public LznView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LznView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        textCount = itemText.length;

        int duration = (int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
        mHandler.sendEmptyMessageDelayed(0, duration);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        totalHeight = getMeasuredHeight();
        lineHeight = getLineHeight();
        totalLine = totalHeight / lineHeight;
    }

    private void generateItem() {
        LznItem item = new LznItem();
        String tx = itemText[(int) (Math.random() * textCount)];
        int sz = (int) (minSize + (maxSize - minSize) * Math.random());
        item.textView = new TextView(mContext);
        item.textView.setText(tx);
        item.textView.setTextSize(sz);
        item.textView.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        item.textMeasuredWidth = (int) getTextWidth(item, tx, sz);
        item.moveSpeed = (int) (minSpeed + (maxSpeed - minSpeed) * Math.random());
        if (totalLine == 0) {
            totalHeight = getMeasuredHeight();
            lineHeight = getLineHeight();
            totalLine = totalHeight / lineHeight;
        }
        item.verticalPos = random.nextInt(totalLine) * lineHeight;
        showBarrageItem(item);
    }

    private void showBarrageItem(final LznItem item) {

        int leftMargin = this.getRight() - this.getLeft() - this.getPaddingLeft();

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = item.verticalPos;
        this.addView(item.textView, params);
        Animation anim = generateTranslateAnim(item, leftMargin);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                item.textView.clearAnimation();
                LznView.this.removeView(item.textView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        item.textView.startAnimation(anim);
    }

    private TranslateAnimation generateTranslateAnim(LznItem item, int leftMargin) {
        TranslateAnimation anim = new TranslateAnimation(leftMargin, -item.textMeasuredWidth, 0, 0);
        anim.setDuration(item.moveSpeed);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setFillAfter(true);
        return anim;
    }

    /**
     * 计算TextView中字符串的长度
     *
     *
     * @param item
     * @param text 要计算的字符串
     * @param Size 字体大小
     * @return TextView中字符串的长度
     */
    public float getTextWidth(LznItem item, String text, float Size) {
        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * 获得每一行弹幕的最大高度
     *
     * @return
     */
    private int getLineHeight() {
        LznItem item = new LznItem();
        String tx = itemText[0];
        item.textView = new TextView(mContext);
        item.textView.setText(tx);
        item.textView.setTextSize(maxSize);

        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(tx, 0, tx.length(), bounds);
        return bounds.height();
    }

    class BarrageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            generateItem();
            //每个弹幕产生的间隔时间随机
            int duration = (int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
            this.sendEmptyMessageDelayed(0, duration);
        }
    }

}

