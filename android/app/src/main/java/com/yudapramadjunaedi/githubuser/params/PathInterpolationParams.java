package com.yudapramadjunaedi.githubuser.params;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.animation.Interpolator;

import com.yudapramadjunaedi.githubuser.views.sharedElementTransition.ControlPoint;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PathInterpolationParams extends InterpolationParams {

    public ControlPoint p1;
    public ControlPoint p2;

    @Override
    public Interpolator get() {
        // Not called
        return null;
    }
}
