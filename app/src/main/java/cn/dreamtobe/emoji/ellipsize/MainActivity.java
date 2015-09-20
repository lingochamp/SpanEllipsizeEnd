package cn.dreamtobe.emoji.ellipsize;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.dreamtobe.emoji.ellipsize.helper.SpanEllipsizeEndHelper;

public class MainActivity extends AppCompatActivity {

    private final static String demoText = "一二34567892abcde3⑤4⑥七89123四五五六七3829八九十六7891七八九⑥⑤eddsll";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Drawable drawable = getDrawable(R.drawable.ic_l)
//        SpannableString demoS = new SpannableString(String.format("", ))

        assignViews();

    }

    private SeekBar mSpansizeSeekbar;
    private TextView mOriginTv;
    private TextView mNormalTv;
    private TextView mDemoTv;

    private void assignViews() {
        mSpansizeSeekbar = (SeekBar) findViewById(R.id.spansize_seekbar);
        mOriginTv = (TextView) findViewById(R.id.origin_tv);
        mNormalTv = (TextView) findViewById(R.id.normal_tv);
        mDemoTv = (TextView) findViewById(R.id.demo_tv);
    }


    private final static int MAX_SPAN_SIZE = 4;
    private final static int MAX_SPAN_NUMS = demoText.length() / MAX_SPAN_SIZE;
    private final static int MIN_SPAN_NUMS = 3;

    public void onClickRandom(final View view) {
        final float percent = (float) mSpansizeSeekbar.getProgress() / (float) mSpansizeSeekbar.getMax();

        SpannableString demoSS = new SpannableString(demoText);


        int spanNums = (int) (percent * MAX_SPAN_NUMS);
        spanNums = Math.max(spanNums, MIN_SPAN_NUMS);
        Random random = new Random(System.currentTimeMillis());

        final List<Span> spans = new ArrayList<>();

        while (spanNums > 0) {
            int spanSize = random.nextInt(MAX_SPAN_SIZE - 1) + 1;

            int start = createStart(spanSize);
            int end = start + spanSize;


            boolean isAvaliable = true;
            for (Span span : spans) {
                if (span.start <= start && span.end >= end) {
                    isAvaliable = false;
                    break;
                }

                if (span.start <= start && span.end > start && span.end <= end) {
                    isAvaliable = false;
                    break;
                }

                if (span.start >= start && span.start < end && span.end >= end) {
                    isAvaliable = false;
                    break;
                }
            }


            if (isAvaliable) {
                int drawableResIndex = random.nextInt(19);
                int drawableResId = getResources().getIdentifier("emoji_" + drawableResIndex, "drawable", "cn.dreamtobe.emoji.ellipsize");
                Drawable emojiDra = getResources().getDrawable(drawableResId);
                emojiDra.setBounds(0, 0, (int) (mDemoTv.getTextSize() * 1.3f), (int) (mDemoTv.getTextSize() * 1.3f));
                ImageSpan imageSpan = new ImageSpan(emojiDra, DynamicDrawableSpan.ALIGN_BOTTOM);

                Span span = new Span();
                span.start = start;
                span.end = end;
                spans.add(span);

                demoSS.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanNums--;
            }

        }


        // complete random Span
        mOriginTv.setText(demoSS);
        mNormalTv.setText(demoSS);
        mDemoTv.setText(SpanEllipsizeEndHelper.matchMaxWidth(demoSS, mDemoTv));
//        long start = System.currentTimeMillis();
        CharSequence s = TextUtils.ellipsize(demoSS, mDemoTv.getPaint(), mDemoTv.getMaxWidth(), TextUtils.TruncateAt.END);
//        Log.d("SpanEllipsizeEnd",  String.valueOf(System.currentTimeMillis() - start));
//        mDemoTv.setText(s);
    }

    private int createStart(final int spanSize) {
        return new Random(System.currentTimeMillis()).nextInt(demoText.length() - spanSize);
    }

    private class Span {
        private int start;
        private int end;
    }
}

