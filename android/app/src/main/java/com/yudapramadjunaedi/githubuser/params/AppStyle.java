package com.yudapramadjunaedi.githubuser.params;

import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.parsers.StyleParamsParser;

public class AppStyle {
    public static StyleParams appStyle;

    public static void setAppStyle(Bundle params) {
        appStyle = new StyleParamsParser(params.getBundle("appStyle")).parse();
    }
}
