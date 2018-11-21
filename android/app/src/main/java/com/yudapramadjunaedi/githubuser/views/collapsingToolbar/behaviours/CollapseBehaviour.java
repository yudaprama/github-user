package com.yudapramadjunaedi.githubuser.views.collapsingToolbar.behaviours;

public interface CollapseBehaviour {
    boolean shouldCollapseOnFling();

    boolean shouldCollapseOnTouchUp();

    boolean canExpend(int scrollY);
}
