package com.yudapramadjunaedi.githubuser.layouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.yudapramadjunaedi.githubuser.NavigationApplication;
import com.yudapramadjunaedi.githubuser.events.EventBus;
import com.yudapramadjunaedi.githubuser.events.ScreenChangedEvent;
import com.yudapramadjunaedi.githubuser.params.ActivityParams;
import com.yudapramadjunaedi.githubuser.params.AppStyle;
import com.yudapramadjunaedi.githubuser.params.ContextualMenuParams;
import com.yudapramadjunaedi.githubuser.params.FabParams;
import com.yudapramadjunaedi.githubuser.params.LightBoxParams;
import com.yudapramadjunaedi.githubuser.params.ScreenParams;
import com.yudapramadjunaedi.githubuser.params.SideMenuParams;
import com.yudapramadjunaedi.githubuser.params.SlidingOverlayParams;
import com.yudapramadjunaedi.githubuser.params.SnackbarParams;
import com.yudapramadjunaedi.githubuser.params.StyleParams;
import com.yudapramadjunaedi.githubuser.params.TitleBarButtonParams;
import com.yudapramadjunaedi.githubuser.params.TitleBarLeftButtonParams;
import com.yudapramadjunaedi.githubuser.screens.NavigationType;
import com.yudapramadjunaedi.githubuser.screens.Screen;
import com.yudapramadjunaedi.githubuser.screens.ScreenStack;
import com.yudapramadjunaedi.githubuser.utils.Task;
import com.yudapramadjunaedi.githubuser.utils.ViewUtils;
import com.yudapramadjunaedi.githubuser.views.BottomTabs;
import com.yudapramadjunaedi.githubuser.views.LightBox;
import com.yudapramadjunaedi.githubuser.views.SideMenu;
import com.yudapramadjunaedi.githubuser.views.SideMenu.Side;
import com.yudapramadjunaedi.githubuser.views.SnackbarAndFabContainer;
import com.yudapramadjunaedi.githubuser.views.slidingOverlay.SlidingOverlay;
import com.yudapramadjunaedi.githubuser.views.slidingOverlay.SlidingOverlaysQueue;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@SuppressLint("ViewConstructor")
public class BottomTabsLayout extends BaseLayout implements AHBottomNavigation.OnTabSelectedListener {

    private ActivityParams params;
    private SnackbarAndFabContainer snackbarAndFabContainer;
    private BottomTabs bottomTabs;
    private ScreenStack[] screenStacks;
    private final SideMenuParams leftSideMenuParams;
    private final SideMenuParams rightSideMenuParams;
    private final SlidingOverlaysQueue slidingOverlaysQueue = new SlidingOverlaysQueue();
    private
    @Nullable
    SideMenu sideMenu;
    private int currentStackIndex = 0;
    private LightBox lightBox;

    public BottomTabsLayout(AppCompatActivity activity, ActivityParams params) {
        super(activity);
        this.params = params;
        leftSideMenuParams = params.leftSideMenuParams;
        rightSideMenuParams = params.rightSideMenuParams;
        screenStacks = new ScreenStack[params.tabParams.size()];
        createLayout();
    }

    private void createLayout() {
        createSideMenu();
        createBottomTabs();
        addBottomTabs();
        addScreenStacks();
        createSnackbarContainer();
        showInitialScreenStack();
        setInitialTabIndex();
    }

    private void setInitialTabIndex() {
        bottomTabs.setCurrentItem(AppStyle.appStyle.bottomTabsInitialIndex);
    }

    private void createSideMenu() {
        if (leftSideMenuParams == null && rightSideMenuParams == null) {
            return;
        }
        sideMenu = new SideMenu(getContext(), leftSideMenuParams, rightSideMenuParams);
        LayoutParams lp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        addView(sideMenu, lp);
    }

    private void addScreenStacks() {
        for (int i = screenStacks.length - 1; i >= 0; i--) {
            createAndAddScreens(i);
        }
    }

    private void createAndAddScreens(int position) {
        ScreenParams screenParams = params.tabParams.get(position);
        ScreenStack newStack = new ScreenStack(getActivity(), getScreenStackParent(), screenParams.getNavigatorId(), this);
        newStack.pushInitialScreen(screenParams, createScreenLayoutParams(screenParams));
        for (ScreenParams screen : screenParams.screens) {
            newStack.pushInitialScreen(screen, createScreenLayoutParams(screen));
        }
        screenStacks[position] = newStack;
    }

    private RelativeLayout getScreenStackParent() {
        return sideMenu == null ? this : sideMenu.getContentContainer();
    }

    @NonNull
    private LayoutParams createScreenLayoutParams(ScreenParams params) {
        LayoutParams lp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        if (params.styleParams.drawScreenAboveBottomTabs) {
            lp.addRule(RelativeLayout.ABOVE, bottomTabs.getId());
        }
        return lp;
    }

    private void createBottomTabs() {
        bottomTabs = new BottomTabs(getContext());
        bottomTabs.addTabs(params.tabParams, this);
    }

    private void addBottomTabs() {
        LayoutParams lp = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        getScreenStackParent().addView(bottomTabs, lp);
    }

    private void createSnackbarContainer() {
        snackbarAndFabContainer = new SnackbarAndFabContainer(getContext(), this);
        LayoutParams lp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        alignSnackbarContainerWithBottomTabs(lp, getCurrentScreen().getStyleParams());
        snackbarAndFabContainer.setClickable(false);
        getScreenStackParent().addView(snackbarAndFabContainer, lp);
    }

    private void showInitialScreenStack() {
        bottomTabs.setVisibilityByInitialScreen(getInitialScreenStack().peek().getStyleParams());
        showStackAndUpdateStyle(getInitialScreenStack(), NavigationType.InitialScreen);
        EventBus.instance.post(new ScreenChangedEvent(screenStacks[0].peek().getScreenParams()));
    }

    private ScreenStack getInitialScreenStack() {
        return screenStacks[AppStyle.appStyle.bottomTabsInitialIndex];
    }

    @Override
    public View asView() {
        return this;
    }

    @Override
    public boolean onBackPressed() {
        if (handleBackInJs()) {
            return true;
        }

        if (getCurrentScreenStack().canPop()) {
            getCurrentScreenStack().pop(true, System.currentTimeMillis());
            setBottomTabsStyleFromCurrentScreen();
            EventBus.instance.post(new ScreenChangedEvent(getCurrentScreenStack().peek().getScreenParams()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleBackInJs() {
        return getCurrentScreenStack().handleBackPressInJs();
    }

    @Override
    public void setTopBarVisible(String screenInstanceId, boolean hidden, boolean animated) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setScreenTopBarVisible(screenInstanceId, hidden, animated);
        }
    }

    public void setBottomTabsVisible(boolean hidden, boolean animated) {
        getCurrentScreenStack().peek().updateBottomTabsVisibility(hidden);
        bottomTabs.setVisibility(hidden, animated);
    }

    @Override
    public void setTitleBarTitle(String screenInstanceId, String title) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setScreenTitleBarTitle(screenInstanceId, title);
        }
    }

    @Override
    public void setTitleBarSubtitle(String screenInstanceId, String subtitle) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setScreenTitleBarSubtitle(screenInstanceId, subtitle);
        }
    }

    @Override
    public void setTitleBarRightButtons(String screenInstanceId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setScreenTitleBarRightButtons(screenInstanceId, navigatorEventId, titleBarButtons);
        }
    }

    @Override
    public void setTitleBarLeftButton(String screenInstanceId, String navigatorEventId, TitleBarLeftButtonParams titleBarLeftButtonParams) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setScreenTitleBarLeftButton(screenInstanceId, navigatorEventId, titleBarLeftButtonParams);
        }
    }

    @Override
    public void setFab(String screenInstanceId, String navigatorEventId, FabParams fabParams) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setFab(screenInstanceId, fabParams);
        }
    }

    @Override
    public void updateScreenStyle(String screenInstanceId, Bundle styleParams) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].updateScreenStyle(screenInstanceId, styleParams);
        }
    }

    @Override
    public String getCurrentlyVisibleScreenId() {
        return getCurrentScreen().getScreenInstanceId();
    }

    @Override
    public void selectTopTabByTabIndex(String screenInstanceId, int index) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].selectTopTabByTabIndex(screenInstanceId, index);
        }
    }

    @Override
    public void selectTopTabByScreen(String screenInstanceId) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].selectTopTabByScreen(screenInstanceId);
        }
    }

    @Override
    public void toggleSideMenuVisible(boolean animated, Side side) {
        if (sideMenu != null) {
            sideMenu.toggleVisible(animated, side);
        }
    }

    @Override
    public void setSideMenuVisible(boolean animated, boolean visible, Side side) {
        if (sideMenu != null) {
            sideMenu.setVisible(visible, animated, side);
        }
    }

    @Override
    public void setSideMenuEnabled(boolean enabled, Side side) {
        if (sideMenu != null) {
            sideMenu.setDrawerLockMode(enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void showSnackbar(SnackbarParams params) {
        final String eventId = getCurrentScreenStack().peek().getNavigatorEventId();
        snackbarAndFabContainer.showSnackbar(eventId, params);
    }

    @Override
    public void dismissSnackbar() {
        snackbarAndFabContainer.dismissSnackbar();
    }

    @Override
    public void showLightBox(LightBoxParams params) {
        if (lightBox == null) {
            lightBox = new LightBox(getActivity(), new Runnable() {
                @Override
                public void run() {
                    lightBox = null;
                }
            }, params);
            lightBox.show();
        }
    }

    @Override
    public void dismissLightBox() {
        if (lightBox != null) {
            lightBox.hide();
            lightBox = null;
        }
    }

    @Override
    public void showSlidingOverlay(final SlidingOverlayParams params) {
        slidingOverlaysQueue.add(new SlidingOverlay(this, params));
    }

    @Override
    public void hideSlidingOverlay() {
        slidingOverlaysQueue.remove();
    }

    @Override
    public void onModalDismissed() {
        getCurrentScreenStack().peek().setStyle();
        getCurrentScreenStack().peek().getScreenParams().timestamp = System.currentTimeMillis();
        NavigationApplication.instance.getEventEmitter().sendWillAppearEvent(getCurrentScreenStack().peek().getScreenParams(), NavigationType.DismissModal);
        NavigationApplication.instance.getEventEmitter().sendDidAppearEvent(getCurrentScreenStack().peek().getScreenParams(), NavigationType.DismissModal);
        EventBus.instance.post(new ScreenChangedEvent(getCurrentScreenStack().peek().getScreenParams()));
    }

    @Override
    public boolean containsNavigator(String navigatorId) {
        // Unused
        return false;
    }

    @Override
    public void showContextualMenu(String screenInstanceId, ContextualMenuParams params, Callback onButtonClicked) {
        getCurrentScreenStack().peek().showContextualMenu(params, onButtonClicked);
    }

    @Override
    public void dismissContextualMenu(String screenInstanceId) {
        getCurrentScreenStack().peek().dismissContextualMenu();
    }

    @Override
    public Screen getCurrentScreen() {
        return getCurrentScreenStack().peek();
    }

    public void selectBottomTabByTabIndex(Integer index) {
        if (bottomTabs.getCurrentItem() != index) {
            bottomTabs.setCurrentItemWithoutInvokingTabSelectedListener(index);
            switchTab(index, NavigationType.SwitchToTab);
        }
    }

    public void selectBottomTabByNavigatorId(final String navigatorId) {
        performOnStack(navigatorId, new Task<ScreenStack>() {
            @Override
            public void run(ScreenStack param) {
                selectBottomTabByTabIndex(getScreenStackIndex(navigatorId));
            }
        });
    }

    private boolean hasBackgroundColor(StyleParams params) {
        return params.screenBackgroundColor != null &&
            params.screenBackgroundColor.hasColor();
    }

    private void setStyleFromScreen(StyleParams params) {
        bottomTabs.setStyleFromScreen(params);
        if (snackbarAndFabContainer != null && snackbarAndFabContainer.getLayoutParams() instanceof LayoutParams)
            alignSnackbarContainerWithBottomTabs((LayoutParams) snackbarAndFabContainer.getLayoutParams(), params);
        if (hasBackgroundColor(params)) {
            asView().setBackgroundColor(params.screenBackgroundColor.getColor());
        }
    }

    @Override
    public void push(final ScreenParams params, final Promise onPushComplete) {
        performOnStack(params.getNavigatorId(), new Task<ScreenStack>() {
            @Override
            public void run(ScreenStack screenStack) {
                screenStack.push(params, createScreenLayoutParams(params), onPushComplete);
                if (isCurrentStack(screenStack)) {
                    setStyleFromScreen(params.styleParams);
                    EventBus.instance.post(new ScreenChangedEvent(params));
                }
            }
        }, onPushComplete);
    }

    @Override
    public void pop(final ScreenParams params) {
        performOnStack(params.getNavigatorId(), new Task<ScreenStack>() {
            @Override
            public void run(ScreenStack stack) {
            stack.pop(params.animateScreenTransitions, params.timestamp, new ScreenStack.OnScreenPop() {
                    @Override
                    public void onScreenPopAnimationEnd() {
                        setBottomTabsStyleFromCurrentScreen();
                        EventBus.instance.post(new ScreenChangedEvent(getCurrentScreenStack().peek().getScreenParams()));
                    }
                });
            }
        });
    }

    @Override
    public void popToRoot(final ScreenParams params) {
        performOnStack(params.getNavigatorId(), new Task<ScreenStack>() {
            @Override
            public void run(final ScreenStack stack) {
                stack.popToRoot(params.animateScreenTransitions, params.timestamp, new ScreenStack.OnScreenPop() {
                    @Override
                    public void onScreenPopAnimationEnd() {
                        if (isCurrentStack(stack)) {
                            setBottomTabsStyleFromCurrentScreen();
                            alignSnackbarContainerWithBottomTabs((LayoutParams) snackbarAndFabContainer.getLayoutParams(), params.styleParams);
                            EventBus.instance.post(new ScreenChangedEvent(stack.peek().getScreenParams()));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void newStack(final ScreenParams params) {
        performOnStack(params.getNavigatorId(), new Task<ScreenStack>() {
            @Override
            public void run(ScreenStack screenStack) {
                screenStack.newStack(params, createScreenLayoutParams(params));
                if (isCurrentStack(screenStack)) {
                    setStyleFromScreen(params.styleParams);
                    alignSnackbarContainerWithBottomTabs((LayoutParams) snackbarAndFabContainer.getLayoutParams(), params.styleParams);
                    EventBus.instance.post(new ScreenChangedEvent(params));
                }
            }
        });
    }

    private void alignSnackbarContainerWithBottomTabs(LayoutParams lp, StyleParams styleParams) {
        if (styleParams.drawScreenAboveBottomTabs || !styleParams.bottomTabsHidden) {
            lp.addRule(ABOVE, bottomTabs.getId());
        } else {
            ViewUtils.removeRuleCompat(lp, ABOVE);
        }
    }

    private void performOnStack(String navigatorId, Task<ScreenStack> task) {
        performOnStack(navigatorId, task, null);
    }

    private void performOnStack(String navigatorId, Task<ScreenStack> task, @Nullable Promise onPushComplete) {
        try {
            ScreenStack screenStack = getScreenStack(navigatorId);
            task.run(screenStack);
        } catch (ScreenStackNotFoundException e) {
            if (onPushComplete != null) {
                onPushComplete.reject("Navigation", "Could not perform action on stack [" + navigatorId + "]." +
                                                    "This should not have happened, it probably means a navigator action" +
                                                    "was called from an unmounted tab.");
            }
            Log.e("Navigation", "Could not perform action on stack [" + navigatorId + "]." +
                                "This should not have happened, it probably means a navigator action" +
                                "was called from an unmounted tab.");
        }
    }

    @Override
    public void destroy() {
        snackbarAndFabContainer.destroy();
        for (ScreenStack screenStack : screenStacks) {
            screenStack.destroy();
        }
        if (sideMenu != null) {
            sideMenu.destroy();
        }
        if (lightBox != null) {
            lightBox.destroy();
            lightBox = null;
        }
        slidingOverlaysQueue.destroy();
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        if (wasSelected) {
            sendTabReselectedEventToJs();
            return false;
        }

        final int unselectedTabIndex = currentStackIndex;
        sendTabSelectedEventToJs(position, unselectedTabIndex);
        switchTab(position, NavigationType.BottomTabSelected);
        return true;
    }

    private void switchTab(int position, NavigationType navigationType) {
        hideCurrentStack();
        showNewStack(position, navigationType);
        EventBus.instance.post(new ScreenChangedEvent(getCurrentScreenStack().peek().getScreenParams()));
    }

    private void sendTabSelectedEventToJs(int selectedTabIndex, int unselectedTabIndex) {
        String navigatorEventId = screenStacks[selectedTabIndex].peek().getNavigatorEventId();
        WritableMap data = createTabSelectedEventData(selectedTabIndex, unselectedTabIndex);
        NavigationApplication.instance.getEventEmitter().sendNavigatorEvent("bottomTabSelected", navigatorEventId, data);

        data = createTabSelectedEventData(selectedTabIndex, unselectedTabIndex);
        NavigationApplication.instance.getEventEmitter().sendNavigatorEvent("bottomTabSelected", data);
    }

    private WritableMap createTabSelectedEventData(int selectedTabIndex, int unselectedTabIndex) {
        WritableMap data = Arguments.createMap();
        data.putInt("selectedTabIndex", selectedTabIndex);
        data.putInt("unselectedTabIndex", unselectedTabIndex);
        return data;
    }

    private void sendTabReselectedEventToJs() {
        WritableMap data = Arguments.createMap();
        String navigatorEventId = getCurrentScreenStack().peek().getNavigatorEventId();
        NavigationApplication.instance.getEventEmitter().sendNavigatorEvent("bottomTabReselected", navigatorEventId, data);
    }

    private void showNewStack(int position, NavigationType type) {
        showStackAndUpdateStyle(screenStacks[position], type);
        currentStackIndex = position;
    }

    private void showStackAndUpdateStyle(ScreenStack newStack, NavigationType type) {
        newStack.show(type);
        setStyleFromScreen(newStack.getCurrentScreenStyleParams());
    }

    private void hideCurrentStack() {
        ScreenStack currentScreenStack = getCurrentScreenStack();
        currentScreenStack.hide(NavigationType.BottomTabSelected);
    }

    private ScreenStack getCurrentScreenStack() {
        return screenStacks[currentStackIndex];
    }

    private
    @NonNull
    ScreenStack getScreenStack(String navigatorId) throws ScreenStackNotFoundException {
        int index = getScreenStackIndex(navigatorId);
        return screenStacks[index];
    }

    public void setBottomTabBadgeByIndex(Integer index, String badge) {
        bottomTabs.setNotification(badge, index);
    }

    public void setBottomTabBadgeByNavigatorId(String navigatorId, String badge) {
        bottomTabs.setNotification(badge, getScreenStackIndex(navigatorId));
    }

    public void setBottomTabButtonByIndex(Integer index, ScreenParams params) {
        bottomTabs.setTabButton(params, index);
    }

    public void setBottomTabButtonByNavigatorId(String navigatorId, ScreenParams params) {
        bottomTabs.setTabButton(params, getScreenStackIndex(navigatorId));
    }

    private int getScreenStackIndex(String navigatorId) throws ScreenStackNotFoundException {
        for (int i = 0; i < screenStacks.length; i++) {
            if (screenStacks[i].getNavigatorId().equals(navigatorId)) {
                return i;
            }
        }
        throw new ScreenStackNotFoundException("Stack " + navigatorId + " not found");
    }

    private class ScreenStackNotFoundException extends RuntimeException {
        ScreenStackNotFoundException(String navigatorId) {
            super(navigatorId);
        }
    }

    private boolean isCurrentStack(ScreenStack screenStack) {
        return getCurrentScreenStack() == screenStack;
    }

    private void setBottomTabsStyleFromCurrentScreen() {
        setStyleFromScreen(getCurrentScreenStack().getCurrentScreenStyleParams());
    }

    @Override
    public boolean onTitleBarBackButtonClick() {
        if (getCurrentScreenStack().canPop()) {
            getCurrentScreenStack().pop(true, System.currentTimeMillis(), new ScreenStack.OnScreenPop() {
                @Override
                public void onScreenPopAnimationEnd() {
                    setBottomTabsStyleFromCurrentScreen();
                    EventBus.instance.post(new ScreenChangedEvent(getCurrentScreenStack().peek().getScreenParams()));
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onSideMenuButtonClick() {
        final String navigatorEventId = getCurrentScreenStack().peek().getNavigatorEventId();
        NavigationApplication.instance.getEventEmitter().sendNavigatorEvent("sideMenu", navigatorEventId);
        if (sideMenu != null) {
            sideMenu.openDrawer(Side.Left);
        }
    }
}
