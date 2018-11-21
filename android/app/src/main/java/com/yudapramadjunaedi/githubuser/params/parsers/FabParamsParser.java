package com.yudapramadjunaedi.githubuser.params.parsers;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.FabActionParams;
import com.yudapramadjunaedi.githubuser.params.FabParams;
import com.yudapramadjunaedi.githubuser.params.StyleParams;
import com.yudapramadjunaedi.githubuser.react.ImageLoader;
import com.yudapramadjunaedi.githubuser.utils.ViewUtils;

public class FabParamsParser extends Parser {
    public FabParams parse(Bundle params, final String navigatorEventId, String screenInstanceId) {
        FabParams fabParams = new FabParams();
        fabParams.collapsedId = params.getString("collapsedId");
        fabParams.expendedId = params.getString("expendedId");
        fabParams.collapsedIconColor = getColor(params, "collapsedIconColor", new StyleParams.Color());
        fabParams.expendedIconColor = getColor(params, "expendedIconColor", new StyleParams.Color());
        fabParams.navigatorEventId = navigatorEventId;
        fabParams.screenInstanceId = screenInstanceId;
        fabParams.backgroundColor = getColor(params, "backgroundColor", new StyleParams.Color());

        if (hasKey(params, "collapsedIcon")) {
            fabParams.collapsedIcon = ImageLoader.loadImage(params.getString("collapsedIcon"));
            tintIcon(fabParams.collapsedIcon, fabParams.collapsedIconColor);
        }
        if (hasKey(params, "expendedIcon")) {
            fabParams.expendedIcon = ImageLoader.loadImage(params.getString("expendedIcon"));
            tintIcon(fabParams.expendedIcon, fabParams.expendedIconColor);
        }
        if (hasKey(params, "actions")) {
            fabParams.actions = parseBundle(params.getBundle("actions"), new ParseStrategy<FabActionParams>() {
                @Override
                public FabActionParams parse(Bundle params) {
                    return new FabActionParamsParser().parse(params, navigatorEventId);
                }
            });
        }
        return fabParams;
    }

    private void tintIcon(Drawable icon, StyleParams.Color color) {
        if (color.hasColor()) {
            ViewUtils.tintDrawable(icon, color.getColor(), true);
        }
    }
}
