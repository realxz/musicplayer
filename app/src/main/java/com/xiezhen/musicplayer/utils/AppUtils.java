package com.xiezhen.musicplayer.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;

import com.xiezhen.musicplayer.application.CrashAppliacation;

/**
 * Created by Administrator on 2015/12/26 0026.
 */
public class AppUtils {
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) CrashAppliacation.context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
