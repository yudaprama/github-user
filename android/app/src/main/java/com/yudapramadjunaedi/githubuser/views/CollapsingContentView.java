package com.yudapramadjunaedi.githubuser.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.yudapramadjunaedi.githubuser.params.NavigationParams;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapseAmount;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapsingView;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapsingViewMeasurer;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.OnScrollViewAddedListener;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.ScrollListener;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.ScrollViewDelegate;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.ViewCollapser;
import com.yudapramadjunaedi.githubuser.views.utils.ScrollViewDetector;

@SuppressLint("ViewConstructor")
public class CollapsingContentView extends ContentView implements CollapsingView {

    private @Nullable ScrollViewDelegate scrollViewDelegate;
    private @Nullable ScrollViewDetector scrollViewDetector;
    private final ViewCollapser viewCollapser;

    public CollapsingContentView(Context context, String screenId, NavigationParams navigationParams) {
        super(context, screenId, navigationParams);
        viewCollapser = new ViewCollapser(this);
    }

    public void setupCollapseDetection(ScrollListener scrollListener, OnScrollViewAddedListener onScrollViewAddedListener) {
        scrollViewDelegate = new ScrollViewDelegate(scrollListener);
        scrollViewDetector = new ScrollViewDetector(this, onScrollViewAddedListener, scrollViewDelegate);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (scrollViewDelegate != null && scrollViewDelegate.hasScrollView()) {
            boolean consumed = scrollViewDelegate.didInterceptTouchEvent(ev);
            if (consumed) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onViewAdded(final View child) {
        super.onViewAdded(child);
        if (scrollViewDetector != null) {
            scrollViewDetector.findScrollView(child);
        }
    }

    @Override
    public void collapse(CollapseAmount amount) {
        viewCollapser.collapse(amount);
    }

    @Override
    public void fling(CollapseAmount amount) {
        viewCollapser.fling(amount);
    }

    public void destroy() {
        if (scrollViewDetector != null) {
            scrollViewDetector.destroy();
        }
        if (scrollViewDelegate != null) {
            scrollViewDelegate.destroy();
        }
    }

    @Override
    public float getFinalCollapseValue() {
        return ((CollapsingViewMeasurer) viewMeasurer).getFinalCollapseValue();
    }

    @Override
    public float getCurrentCollapseValue() {
        return getTranslationY();
    }

    @Override
    public View asView() {
        return this;
    }
}
