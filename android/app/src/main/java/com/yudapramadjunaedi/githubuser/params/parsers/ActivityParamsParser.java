package com.yudapramadjunaedi.githubuser.params.parsers;

import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.ActivityParams;
import com.yudapramadjunaedi.githubuser.params.AppStyle;
import com.yudapramadjunaedi.githubuser.params.SideMenuParams;
import com.yudapramadjunaedi.githubuser.views.SideMenu;

public class ActivityParamsParser extends Parser {
    public static ActivityParams parse(Bundle params) {
        ActivityParams result = new ActivityParams();

        AppStyle.setAppStyle(params);

        if (hasKey(params, "screen")) {
            result.type = ActivityParams.Type.SingleScreen;
            result.screenParams = ScreenParamsParser.parse(params.getBundle("screen"));
        }

        if (hasKey(params, "tabs")) {
            result.type = ActivityParams.Type.TabBased;
            result.tabParams = new ScreenParamsParser().parseTabs(params.getBundle("tabs"));
            if (result.tabParams.size() == 0) {
                throw new RuntimeException("Tried to start tab based app with zero tabs");
            }
        }

        if (hasKey(params, "sideMenu")) {
            SideMenuParams[] sideMenus = SideMenuParamsParser.parse(params.getBundle("sideMenu"));
            result.leftSideMenuParams = sideMenus[SideMenu.Side.Left.ordinal()];
            result.rightSideMenuParams = sideMenus[SideMenu.Side.Right.ordinal()];
        }

        result.animateShow = params.getBoolean("animateShow", true);

        return result;
    }
}
