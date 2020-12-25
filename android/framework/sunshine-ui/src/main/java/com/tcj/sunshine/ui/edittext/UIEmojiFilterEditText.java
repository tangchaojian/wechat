package com.tcj.sunshine.ui.edittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * emoji 筛选输入框
 */
public class UIEmojiFilterEditText extends AppCompatEditText {

    public UIEmojiFilterEditText(Context context) {
        super(context);
        this.initUI();
    }

    public UIEmojiFilterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI();
    }

    public UIEmojiFilterEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI();
    }

    private void initUI(){
        super.setFilters(new InputFilter[]{new EmojiFilter()});
    }

    @Override
    public void setFilters(InputFilter[] filters) {

        InputFilter[] newFilters = null;
        if(filters != null && filters.length > 0){
            newFilters = new InputFilter[filters.length + 1];
            for (int i = 0; i < filters.length; i++){
                newFilters[i] = filters[i];
            }

            newFilters[filters.length] = new EmojiFilter();
        }else{
            newFilters = new InputFilter[]{new EmojiFilter()};
        }

        super.setFilters(newFilters);
    }

    private class EmojiFilter implements InputFilter {

        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);

            if (emojiMatcher.find()) {
                return "";
            }
            return null;
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        return super.dispatchTouchEvent(event);
//    }
}
