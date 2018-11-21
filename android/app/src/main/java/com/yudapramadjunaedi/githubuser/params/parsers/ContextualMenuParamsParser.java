package com.yudapramadjunaedi.githubuser.params.parsers;

import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.ContextualMenuParams;
import com.yudapramadjunaedi.githubuser.params.NavigationParams;

public class ContextualMenuParamsParser extends Parser {
    public ContextualMenuParams parse(Bundle bundle) {
        ContextualMenuParams result = new ContextualMenuParams();
        result.buttons = new ContextualMenuButtonParamsParser().parseContextualMenuButtons(bundle.getBundle("buttons"));
        result.leftButton = new TitleBarLeftButtonParamsParser().parseSingleButton(bundle.getBundle("backButton"));
        result.navigationParams = new NavigationParams(bundle.getBundle("navigationParams"));
        return result;
    }
}
