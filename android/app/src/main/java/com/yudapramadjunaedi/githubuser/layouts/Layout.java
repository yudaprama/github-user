package com.yudapramadjunaedi.githubuser.layouts;

import android.os.Bundle;
import android.view.View;

import com.facebook.react.bridge.Callback;
import com.yudapramadjunaedi.githubuser.params.ContextualMenuParams;
import com.yudapramadjunaedi.githubuser.params.FabParams;
import com.yudapramadjunaedi.githubuser.params.LightBoxParams;
import com.yudapramadjunaedi.githubuser.params.SlidingOverlayParams;
import com.yudapramadjunaedi.githubuser.params.SnackbarParams;
import com.yudapramadjunaedi.githubuser.params.TitleBarButtonParams;
import com.yudapramadjunaedi.githubuser.params.TitleBarLeftButtonParams;
import com.yudapramadjunaedi.githubuser.screens.Screen;
import com.yudapramadjunaedi.githubuser.views.SideMenu.Side;

import java.util.List;

public interface Layout extends ScreenStackContainer {
    View asView();

    boolean onBackPressed();

    boolean handleBackInJs();

    void setTopBarVisible(String screenInstanceId, boolean hidden, boolean animated);

    void setTitleBarTitle(String screenInstanceId, String title);

    void setTitleBarSubtitle(String screenInstanceId, String subtitle);

    void setTitleBarRightButtons(String screenInstanceId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons);

    void setTitleBarLeftButton(String screenInstanceId, String navigatorEventId, TitleBarLeftButtonParams titleBarLeftButtonParams);

    void setFab(String screenInstanceId, String navigatorEventId, FabParams fabParams);

    void toggleSideMenuVisible(boolean animated, Side side);

    void setSideMenuVisible(boolean animated, boolean visible, Side side);

    void setSideMenuEnabled(boolean enabled, Side side);

    void showSnackbar(SnackbarParams params);

    void showSlidingOverlay(SlidingOverlayParams params);

    void hideSlidingOverlay();

    void onModalDismissed();

    boolean containsNavigator(String navigatorId);

    void showContextualMenu(String screenInstanceId, ContextualMenuParams params, Callback onButtonClicked);

    void dismissContextualMenu(String screenInstanceId);

    Screen getCurrentScreen();

    void dismissSnackbar();

    void showLightBox(LightBoxParams params);

    void dismissLightBox();

    void selectTopTabByTabIndex(String screenInstanceId, int index);

    void selectTopTabByScreen(String screenInstanceId);

    void updateScreenStyle(String screenInstanceId, Bundle styleParams);

    String getCurrentlyVisibleScreenId();
}
