package com.yudapramadjunaedi.githubuser.params.parsers;

import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.AppStyle;
import com.yudapramadjunaedi.githubuser.params.BaseTitleBarButtonParams;
import com.yudapramadjunaedi.githubuser.params.TitleBarButtonParams;
import com.yudapramadjunaedi.githubuser.react.ImageLoader;

import java.util.List;

public class TitleBarButtonParamsParser extends Parser {
    public List<TitleBarButtonParams> parseButtons(Bundle params) {
        return parseBundle(params, new ParseStrategy<TitleBarButtonParams>() {
            @Override
            public TitleBarButtonParams parse(Bundle button) {
                return parseSingleButton(button);
            }
        });
    }

    public TitleBarButtonParams parseSingleButton(Bundle bundle) {
        TitleBarButtonParams result = new TitleBarButtonParams();
        result.label = bundle.getString("title");
        if (hasKey(bundle, "icon")) {
            result.icon = ImageLoader.loadImage(bundle.getString("icon"));
        }
        result.color = getColor(bundle, "color", AppStyle.appStyle.titleBarButtonColor);
        result.disabledColor = getColor(bundle, "titleBarDisabledButtonColor", AppStyle.appStyle.titleBarDisabledButtonColor);
        result.showAsAction = parseShowAsAction(bundle.getString("showAsAction"));
        result.enabled = bundle.getBoolean("enabled", true);
        result.hint = bundle.getString("hint", "");
        result.eventId = bundle.getString("id");
        result.disableIconTint = bundle.getBoolean("disableIconTint", false);
        result.componentName = bundle.getString("component");
        result.componentProps = bundle.getBundle("passProps");
        return result;
    }

    BaseTitleBarButtonParams.ShowAsAction parseShowAsAction(String showAsAction) {
        if (showAsAction == null) {
            return BaseTitleBarButtonParams.ShowAsAction.IfRoom;
        }

        switch (showAsAction) {
            case "always":
                return BaseTitleBarButtonParams.ShowAsAction.Always;
            case "never":
                return BaseTitleBarButtonParams.ShowAsAction.Never;
            case "withText":
                return BaseTitleBarButtonParams.ShowAsAction.WithText;
            case "ifRoom":
            default:
                return BaseTitleBarButtonParams.ShowAsAction.IfRoom;
        }
    }
}
