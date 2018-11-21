package com.yudapramadjunaedi.githubuser.views;

import android.content.Context;
import android.view.Menu;
import android.view.ViewManager;

import com.facebook.react.bridge.Callback;
import com.yudapramadjunaedi.githubuser.NavigationApplication;
import com.yudapramadjunaedi.githubuser.events.ContextualMenuHiddenEvent;
import com.yudapramadjunaedi.githubuser.events.EventBus;
import com.yudapramadjunaedi.githubuser.params.ContextualMenuButtonParams;
import com.yudapramadjunaedi.githubuser.params.ContextualMenuParams;
import com.yudapramadjunaedi.githubuser.params.StyleParams;
import com.yudapramadjunaedi.githubuser.params.TitleBarLeftButtonParams;

import java.util.List;

public class ContextualMenu extends TitleBar implements LeftButtonOnClickListener, ContextualMenuButton.ContextualButtonClickListener {
    private ContextualMenuParams params;
    private Callback onButtonClicked;
    private final String navigatorEventId;

    public ContextualMenu(Context context, ContextualMenuParams params, StyleParams styleParams, Callback onButtonClicked) {
        super(context);
        this.params = params;
        this.onButtonClicked = onButtonClicked;
        navigatorEventId = params.navigationParams.navigatorEventId;
        setStyle(styleParams);
        setButtons();
    }

    public void setStyle(StyleParams styleParams) {
        params.setButtonsColor(styleParams.contextualMenuButtonsColor);
        if (styleParams.contextualMenuBackgroundColor.hasColor()) {
            setBackgroundColor(styleParams.contextualMenuBackgroundColor.getColor());
        }
    }

    public void setButtons() {
        addButtonsToContextualMenu(params.buttons, getMenu());
        setBackButton(params.leftButton);
    }

    private void setBackButton(TitleBarLeftButtonParams leftButton) {
        setLeftButton(leftButton, this, null, false);
    }

    private void addButtonsToContextualMenu(List<ContextualMenuButtonParams> buttons, Menu menu) {
        for (int i = 0; i < buttons.size(); i++) {
            final TitleBarButton button = new ContextualMenuButton(menu, getActionMenuView(), buttons.get(i), this);
            addButtonInReverseOrder(buttons, i, button);
        }
    }

    @Override
    public boolean onTitleBarBackButtonClick() {
        dismiss();
        EventBus.instance.post(new ContextualMenuHiddenEvent());
        return true;
    }

    @Override
    public void onSideMenuButtonClick() {
        // nothing
    }

    @Override
    public void onClick(int index) {
        dismiss();
        EventBus.instance.post(new ContextualMenuHiddenEvent());
        onButtonClicked.invoke(index);
    }

    public void dismiss() {
        hide(new Runnable() {
            @Override
            public void run() {
                ((ViewManager) getParent()).removeView(ContextualMenu.this);
            }
        });
        NavigationApplication.instance.getEventEmitter().sendNavigatorEvent("contextualMenuDismissed", navigatorEventId);
    }
}
