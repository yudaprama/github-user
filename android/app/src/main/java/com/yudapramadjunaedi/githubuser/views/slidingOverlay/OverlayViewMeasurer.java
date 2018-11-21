package com.yudapramadjunaedi.githubuser.views.slidingOverlay;

import android.view.View;

import com.yudapramadjunaedi.githubuser.views.ContentView;
import com.yudapramadjunaedi.githubuser.views.utils.ViewMeasurer;

public class OverlayViewMeasurer extends ViewMeasurer {
    private final ContentView view;

    public OverlayViewMeasurer(ContentView view) {
        this.view = view;
    }

    @Override
    public int getMeasuredHeight(int heightMeasureSpec) {
        View view = this.view.getChildAt(0);
        if (view != null) {
            return view.getMeasuredHeight();
        }

        return super.getMeasuredHeight(heightMeasureSpec);
    }
}