package com.yudapramadjunaedi.githubuser.screens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.facebook.react.uimanager.RootViewUtil;
import com.yudapramadjunaedi.githubuser.events.Event;
import com.yudapramadjunaedi.githubuser.events.ViewPagerScreenChangedEvent;
import com.yudapramadjunaedi.githubuser.events.ViewPagerScreenScrollStartEvent;
import com.yudapramadjunaedi.githubuser.params.PageParams;
import com.yudapramadjunaedi.githubuser.params.ScreenParams;
import com.yudapramadjunaedi.githubuser.views.CollapsingContentView;
import com.yudapramadjunaedi.githubuser.views.ContentView;
import com.yudapramadjunaedi.githubuser.views.LeftButtonOnClickListener;
import com.yudapramadjunaedi.githubuser.views.TopBar;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapseAmount;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapseCalculator;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapsingTopBar;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapsingView;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapsingViewMeasurer;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapsingViewPager;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.CollapsingViewPagerContentViewMeasurer;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.OnScrollListener;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.OnScrollViewAddedListener;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.ScrollListener;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.behaviours.CollapseBehaviour;

@SuppressLint("ViewConstructor")
public class CollapsingViewPagerScreen extends ViewPagerScreen {
    public CollapsingViewPagerScreen(AppCompatActivity activity, ScreenParams screenParams, LeftButtonOnClickListener backButtonListener) {
        super(activity, screenParams, backButtonListener);
    }

    @Override
    protected TopBar createTopBar() {
        final CollapsingTopBar topBar = new CollapsingTopBar(getContext(), styleParams);
        topBar.setScrollListener(getScrollListener(topBar));
        return topBar;
    }

    @Override
    protected ViewPager createViewPager(Context context) {
        CollapsingViewPager viewPager = new CollapsingViewPager(context);
        if (screenParams.styleParams.drawScreenBelowTopBar) {
            viewPager.setViewMeasurer(new CollapsingViewMeasurer((CollapsingTopBar) topBar, this, styleParams));
        }
        return viewPager;
    }

    protected ContentView createContentView(PageParams tab) {
        CollapsingContentView contentView = new CollapsingContentView(getContext(), tab.screenId, tab.navigationParams);
        contentView.setViewMeasurer(new CollapsingViewPagerContentViewMeasurer((CollapsingTopBar) topBar, this, screenParams.styleParams));
        setupCollapseDetection(contentView);
        return contentView;
    }

    private void setupCollapseDetection(CollapsingContentView contentView) {
        ScrollListener scrollListener = getScrollListener((CollapsingView) topBar);
        contentView.setupCollapseDetection(scrollListener, new OnScrollViewAddedListener() {
            @Override
            public void onScrollViewAdded(ScrollView scrollView) {
                ((CollapsingTopBar) topBar).onScrollViewAdded(scrollView);
            }
        });
    }

    private ScrollListener getScrollListener(final CollapsingView topBar) {
        return new ScrollListener(new CollapseCalculator(topBar, getCollapseBehaviour()),
                new OnScrollListener() {
                    @Override
                    public void onScroll(MotionEvent event, CollapseAmount amount) {
                        RootViewUtil.getRootView(getCurrentPage()).onChildStartedNativeGesture(event);
                        topBar.collapse(amount);
                        ((CollapsingView) viewPager).collapse(amount);
                    }

                    @Override
                    public void onFling(CollapseAmount amount) {
                        topBar.fling(amount);
                        ((CollapsingView) viewPager).fling(amount);
                    }
                },
                getCollapseBehaviour()
        );
    }

    private CollapseBehaviour getCollapseBehaviour() {
        return screenParams.styleParams.collapsingTopBarParams.collapseBehaviour;
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if (ViewPagerScreenScrollStartEvent.TYPE.equals(event.getType()) || ViewPagerScreenChangedEvent.TYPE.equals(event.getType())) {
            if (screenParams.styleParams.collapsingTopBarParams.expendOnTopTabChange) {
                ((CollapsingView) topBar).collapse(new CollapseAmount(CollapseCalculator.Direction.Down));
                ((CollapsingView) viewPager).collapse(new CollapseAmount(CollapseCalculator.Direction.Down));
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        for (ContentView contentView : contentViews) {
            if (contentView instanceof CollapsingContentView) {
                ((CollapsingContentView) contentView).destroy();
            }
        }
    }

    protected ContentView getCurrentPage() {
        return (ContentView) viewPager.getChildAt(viewPager.getCurrentItem());
    }
}
