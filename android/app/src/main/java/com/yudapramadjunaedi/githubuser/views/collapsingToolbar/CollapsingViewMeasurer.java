package com.yudapramadjunaedi.githubuser.views.collapsingToolbar;

import com.yudapramadjunaedi.githubuser.params.StyleParams;
import com.yudapramadjunaedi.githubuser.screens.Screen;
import com.yudapramadjunaedi.githubuser.utils.ViewUtils;
import com.yudapramadjunaedi.githubuser.views.utils.ViewMeasurer;

public class CollapsingViewMeasurer extends ViewMeasurer {
    int screenHeight;
    int bottomTabsHeight = 0;
    CollapsingTopBar topBar;
    protected StyleParams styleParams;

    public CollapsingViewMeasurer(final CollapsingTopBar topBar, final Screen collapsingSingleScreen, StyleParams styleParams) {
        this.topBar = topBar;
        this.styleParams = styleParams;
        bottomTabsHeight = (int) ViewUtils.convertDpToPixel(56);
        ViewUtils.runOnPreDraw(collapsingSingleScreen, new Runnable() {
            @Override
            public void run() {
                screenHeight = collapsingSingleScreen.getHeight();
            }
        });
    }

    public float getFinalCollapseValue() {
        return topBar.getFinalCollapseValue();
    }

    @Override
    public int getMeasuredHeight(int heightMeasureSpec) {
        int height = screenHeight - topBar.getCollapsedHeight();
        if (styleParams.bottomTabsHidden) {
            height += bottomTabsHeight;
        }
        return height;
    }
}
