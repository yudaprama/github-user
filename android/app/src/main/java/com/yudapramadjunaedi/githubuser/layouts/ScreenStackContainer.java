package com.yudapramadjunaedi.githubuser.layouts;

import com.facebook.react.bridge.Promise;
import com.yudapramadjunaedi.githubuser.params.ScreenParams;
import com.yudapramadjunaedi.githubuser.views.LeftButtonOnClickListener;

public interface ScreenStackContainer extends LeftButtonOnClickListener {
    void push(ScreenParams screenParams, Promise onPushComplete);

    void pop(ScreenParams screenParams);

    void popToRoot(ScreenParams params);

    void newStack(ScreenParams params);

    void destroy();
}
