package com.yudapramadjunaedi.githubuser.params;

import android.support.annotation.Nullable;

import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.behaviours.CollapseBehaviour;

public class CollapsingTopBarParams {
    public @Nullable String imageUri;
    public @Nullable String reactViewId;
    public StyleParams.Color scrimColor;
    public CollapseBehaviour collapseBehaviour;
    public boolean expendOnTopTabChange;
    public boolean showTitleWhenExpended;
    public boolean showTitleWhenCollapsed;
    public StyleParams.Color expendedTitleBarColor;

    public boolean hasBackgroundImage() {
        return imageUri != null;
    }

    public boolean hasReactView() {
        return reactViewId != null;
    }
}
