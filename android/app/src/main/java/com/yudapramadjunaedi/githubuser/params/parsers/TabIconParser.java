package com.yudapramadjunaedi.githubuser.params.parsers;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.react.ImageLoader;

class TabIconParser extends Parser {

    private Bundle params;

    TabIconParser(Bundle params) {
        this.params = params;
    }

    public Drawable parse() {
        Drawable tabIcon = null;
        if (hasKey(params, "icon")) {
            tabIcon = ImageLoader.loadImage(params.getString("icon"));
        }
        return tabIcon;
    }
}
