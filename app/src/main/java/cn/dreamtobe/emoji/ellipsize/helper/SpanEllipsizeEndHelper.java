package cn.dreamtobe.emoji.ellipsize.helper;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

/**
 * Copyright 2015 Jacks Blog(blog.dreamtobe.cn).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Created by Jacksgong on 9/16/15.
 * <p/>
 */
public class SpanEllipsizeEndHelper {

    private static final String TAG = "SpanEllipsizeEnd";
    private static final int SPANNABLE_MAXWIDTH_CACHE_SIZE = 100;
    private static final LruCache<String, SpannableString> SPAN_MAXWIDTH_CACHE = new LruCache<>(SPANNABLE_MAXWIDTH_CACHE_SIZE);

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static String getMaxWidthKey(CharSequence targetText, TextView textView) {
        String key = String.format("%s@%d@%d", targetText, textView.getMaxWidth(), (int) textView.getTextSize());
        if (targetText instanceof SpannableString) {
            Object[] spans = ((SpannableString) targetText).getSpans(0, targetText.length(), Object.class);
            for (Object span : spans) {
                key += ((SpannableString) targetText).getSpanStart(span);
                key += ((SpannableString) targetText).getSpanEnd(span);
            }
        }

        return key;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static CharSequence matchMaxWidth(SpannableString targetText, TextView textView) {
        if (targetText.length() <= 0) {
            return targetText;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return targetText;
        }

        if (textView == null) {
            return targetText;
        }

        final int maxWidth = textView.getMaxWidth();

        if (maxWidth <= 0 || maxWidth >= Integer.MAX_VALUE) {
            return targetText;
        }


        if (textView.getEllipsize() != TextUtils.TruncateAt.END) {
            return targetText;
        }

        if (textView.getMaxLines() != 1) {
            return targetText;
        }

        final String maxWidthKey = getMaxWidthKey(targetText, textView);
        SpannableString tmpText = SPAN_MAXWIDTH_CACHE.get(maxWidthKey);
        if (tmpText != null) {
            removeClickableSpan(tmpText);
            return tmpText;
        }

        TextPaint textPaint = textView.getPaint();
        if (textPaint == null) {
            return targetText;
        }

        final int totalWidth = (int) textPaint.measureText(targetText, 0, targetText.length());
        if (totalWidth <= maxWidth) {
            return targetText;
        }

        final long startTime = System.currentTimeMillis();
        // deal maxwitdh

        final int dotWidth = (int) textPaint.measureText("...");

        tmpText = targetText;


        int start = 0;
        int end = targetText.length();

        // targetX is maxWidth - "...".length
        int targetX = maxWidth - dotWidth;

        //dichotomy: get x most touch targetX
        int middle = targetText.length();
        int x = 0;
        while (start <= end) {
            // tx = targetX, tl = targetLength

            // width:  0           x
            // length: 0         middle           end
            //         -------------|-------------
            middle = (start + end) / 2;


            int emojiDraW = 0;
            int emojiStrW = 0;


            int emojiExcursion = 1;

            final Object[] tmpSpans = tmpText.getSpans(0, middle, Object.class);
            if (tmpSpans != null) {
                for (Object tmpSpan : tmpSpans) {
                    final int tmpStart = tmpText.getSpanStart(tmpSpan);
                    final int tmpEnd = tmpText.getSpanEnd(tmpSpan);

                    //middle in (tmpStart, tmpEnd)
                    if (tmpStart < middle && tmpEnd > middle) {
                        middle = tmpEnd;
                        emojiExcursion = tmpEnd - tmpStart;
                    }
                }

                // TextPaint#measure do not attention span, so adjust by ourselves
                for (Object tmpSpan : tmpSpans) {
                    final int tmpStart = tmpText.getSpanStart(tmpSpan);
                    final int tmpEnd = tmpText.getSpanEnd(tmpSpan);

                    if (tmpStart < middle && tmpSpan instanceof ImageSpan) {
                        emojiDraW += ((ImageSpan) tmpSpan).getDrawable().getBounds().width();
                        emojiStrW += textPaint.measureText(tmpText, tmpStart, tmpEnd);
                    }
                }

            }

            x = (int) textPaint.measureText(tmpText, 0, middle);
            x = x - emojiStrW + emojiDraW;

//            x = (int) (textPaint.measureText(pureStr, 0, pureStr.length()) + emojiWidth);

//            Log.d(TAG, String.format("targetX: %d, currentX: %d, currentLength: %d, totalLength: %d, emojiStrW[%d], emojiDraW[%d]", targetX, x, middle, targetText.length(), emojiStrW, emojiDraW));

            if (x > targetX) {
                // width:  0       tx        x
                // length: start   tl      middle         end
                //             ----|---------|-------------
                // TO:     start   |       *end
                //             ----|--------|--------------
                end = middle - emojiExcursion;
            } else if (x < targetX) {
                // width:  0               x       tx
                // length: start         middle    tl     end
                //           --------------|-------|------
                // TO:                      *start  |       end
                //           ---------------|------|------
                start = middle + 1;
            } else {
                break;
            }
        }

        // adjust x larger targetX
        while (x > targetX && middle > 0) {
            x = (int) textPaint.measureText(tmpText, 0, --middle);
        }

        // adjust x middle emoji span
        final Object[] ajustSpans = tmpText.getSpans(0, tmpText.length(), Object.class);
        for (Object adjustSpan : ajustSpans) {
            final int adjustStart = tmpText.getSpanStart(adjustSpan);
            final int adjustEnd = tmpText.getSpanEnd(adjustSpan);

            //[adjustStart, adjustEnd)
            if (middle >= adjustStart && middle < adjustEnd) {
                middle = adjustStart - 1;
                break;
            }
        }

        // finnal middle

        // remove [middle + 1, length) spans
//        final Object[] newSpans = tmpText.getSpans(middle + 1, tmpText.length(), Object.class);
//        if (newSpans != null) {
//            for (Object newSpan : newSpans) {
//                tmpText.removeSpan(newSpan);
//            }
//        }

        // sub sequence [0, middle + 1)
        tmpText = (SpannableString) tmpText.subSequence(0, middle + 1);
//        Log.d(TAG, String.format("sub Sequence[0, %d), [%s] to [%s]", middle + 1, targetText, tmpText));

        // add ...
        final SpannableString maxWidthSS = new SpannableString(tmpText + "...");

        final Object[] maxWidthSpans = tmpText.getSpans(0, tmpText.length(), Object.class);
        if (maxWidthSpans != null) {
            for (Object maxWidthSpan : maxWidthSpans) {
                final int mwSpanStart = tmpText.getSpanStart(maxWidthSpan);
                final int mwSpanEnd = tmpText.getSpanEnd(maxWidthSpan);
                final int mwSpanFlag = tmpText.getSpanFlags(maxWidth);

                maxWidthSS.setSpan(maxWidthSpan, mwSpanStart, mwSpanEnd, mwSpanFlag);
            }
        }


        targetText = maxWidthSS;

        SPAN_MAXWIDTH_CACHE.put(maxWidthKey, targetText);
        Log.d(TAG, String.format("deal maxWidth %d", System.currentTimeMillis() - startTime));

        return targetText;
    }

    private static void removeClickableSpan(Spannable content) {
        ClickableSpan[] clickSpans = content.getSpans(0, content.length(), ClickableSpan.class);
        for (int i = 0; i < clickSpans.length; i++) {
            content.removeSpan(clickSpans[i]);
        }
    }


}
