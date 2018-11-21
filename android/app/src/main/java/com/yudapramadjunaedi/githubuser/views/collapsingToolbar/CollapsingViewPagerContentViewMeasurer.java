package com.yudapramadjunaedi.githubuser.views.collapsingToolbar;

import com.yudapramadjunaedi.githubuser.layouts.BottomTabsLayout;
import com.yudapramadjunaedi.githubuser.params.StyleParams;
import com.yudapramadjunaedi.githubuser.screens.Screen;
import com.yudapramadjunaedi.githubuser.utils.ViewUtils;

public class CollapsingViewPagerContentViewMeasurer extends CollapsingViewMeasurer {
    private int titleBarHeight;
    private boolean layoutHasBottomTabs;

    public CollapsingViewPagerContentViewMeasurer(final CollapsingTopBar topBar, final Screen screen, StyleParams styleParams) {
        super(topBar, screen, styleParams);
        ViewUtils.runOnPreDraw(screen, new Runnable() {
            @Override
            public void run() {
                layoutHasBottomTabs = screen.getParent() instanceof BottomTabsLayout;
            }
        });
        ViewUtils.runOnPreDraw(topBar, new Runnable() {
            @Override
            public void run() {
                titleBarHeight = topBar.getTitleBarHeight();
            }
        });
    }

    @Override
    public int getMeasuredHeight(int heightMeasureSpec) {
        int height = screenHeight - topBar.getCollapsedHeight();
        if (hasBottomTabs() && drawScreenUnderBottomTabs()) {
            height -= bottomTabsHeight;
        }
        if (!styleParams.titleBarHideOnScroll) {
            height -= titleBarHeight;
        }
        return height;
    }

    private boolean drawScreenUnderBottomTabs() {
        return !styleParams.drawScreenAboveBottomTabs;
    }

    private boolean hasBottomTabs() {
        return layoutHasBottomTabs && !styleParams.bottomTabsHidden;
    }
}
