package com.androidbase.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFilterUtils {

    public static final int VALIDETE_CODE_WATING_TIME_MAX = 60;

    public static InputFilter spacefilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //禁止输入框输入空格和换行符号
            if (source.equals(" ") || source.toString().contentEquals("\n")) {
                // placed instead
                return "";
            } else {
                // accept original
                return null;
            }
        }
    };

    public static InputFilter sPwFilter = new InputFilter() {
        //只能输入字母与数字
        final Pattern pattern = Pattern.compile("[a-zA-Z0-9]");

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Matcher matcher = pattern.matcher(charSequence);
            if (matcher.find()) {
                return null;
            } else {
                return "";
            }
        }
    };

    public static InputFilter sEmojiFilter = new InputFilter() {
        //过滤Emoji表情和颜文字
        final Pattern pattern = Pattern.compile("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]|[\\ud83e\\udd00-\\ud83e\\uddff]|[\\u2300-\\u23ff]|[\\u2500-\\u25ff]|[\\u2100-\\u21ff]|[\\u0000-\\u00ff]|[\\u2b00-\\u2bff]|[\\u2d06]|[\\u3030]");

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Matcher matcher = pattern.matcher(charSequence);
            if (matcher.find()) {
                return null;
            } else {
                return "";
            }
        }
    };

    public static void addEditSpace(CharSequence s, int start, int before, EditText _text) {
        if (s == null || s.length() == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            int index = start + 1;
            if (start < sb.length()) {
                if (sb.charAt(start) == ' ') {
                    if (before == 0) {
                        index++;
                    } else {
                        index--;
                    }
                } else {
                    if (before == 1) {
                        index--;
                    }
                }
                _text.setText(sb.toString());
                _text.setSelection(index);
            }
        }
    }

}
// binding.etPhone.setFilters(new InputFilter[]{InputFilterUtils.spacefilter, new InputFilter.LengthFilter(13)});
