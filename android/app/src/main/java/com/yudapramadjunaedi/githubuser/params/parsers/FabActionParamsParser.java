package com.yudapramadjunaedi.githubuser.params.parsers;

import android.graphics.Color;
import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.FabActionParams;
import com.yudapramadjunaedi.githubuser.params.StyleParams;
import com.yudapramadjunaedi.githubuser.react.ImageLoader;
import com.yudapramadjunaedi.githubuser.utils.ViewUtils;

public class FabActionParamsParser extends Parser {
    public FabActionParams parse(Bundle params, String navigatorEventId) {
        FabActionParams fabActionParams = new FabActionParams();
        fabActionParams.id = params.getString("id");
        fabActionParams.navigatorEventId = navigatorEventId;
        fabActionParams.icon = ImageLoader.loadImage(params.getString("icon"));
        fabActionParams.backgroundColor = getColor(params, "backgroundColor", new StyleParams.Color());
        fabActionParams.iconColor = StyleParams.Color.parse(params, "iconColor");
        if (fabActionParams.iconColor.hasColor()) {
            ViewUtils.tintDrawable(fabActionParams.icon, fabActionParams.iconColor.getColor(), true);
        }
        fabActionParams.title = params.getString("title");
        fabActionParams.titleBackgroundColor = getColor(params, "titleBackgroundColor",
            new StyleParams.Color(Color.parseColor("#CC000000")));
        fabActionParams.titleColor = getColor(params, "titleColor", new StyleParams.Color(Color.WHITE));

        return fabActionParams;
    }
}