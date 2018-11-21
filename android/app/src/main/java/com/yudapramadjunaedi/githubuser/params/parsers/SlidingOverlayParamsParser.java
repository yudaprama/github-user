package com.yudapramadjunaedi.githubuser.params.parsers;

import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.NavigationParams;
import com.yudapramadjunaedi.githubuser.params.SlidingOverlayParams;

public class SlidingOverlayParamsParser extends Parser {

    public SlidingOverlayParams parse(Bundle bundle) {
        final SlidingOverlayParams result = new SlidingOverlayParams();
        result.screenInstanceId = bundle.getString("screen");
        result.navigationParams = new NavigationParams(bundle.getBundle("navigationParams"));
        result.autoDismissTimerSec = bundle.containsKey("autoDismissTimerSec")
                ? bundle.getInt("autoDismissTimerSec")
                : null;
        result.position = SlidingOverlayParams.Position.fromString(bundle.getString("position", "top"));
        return result;
    }
}
