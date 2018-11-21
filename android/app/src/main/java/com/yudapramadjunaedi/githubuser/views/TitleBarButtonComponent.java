package com.yudapramadjunaedi.githubuser.views;

import android.content.Context;
import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.NavigationParams;
import com.yudapramadjunaedi.githubuser.views.utils.Constants;
import com.yudapramadjunaedi.githubuser.views.utils.ViewMeasurer;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TitleBarButtonComponent extends ContentView {

    public TitleBarButtonComponent(Context context, String componentName, Bundle passProps) {
        super(context, componentName, NavigationParams.EMPTY, passProps);
        setLayoutParams(new LayoutParams(WRAP_CONTENT, Constants.TOOLBAR_BUTTON_SIZE));
        setViewMeasurer(new ViewMeasurer() {
            @Override
            public int getMeasuredWidth(int widthMeasureSpec) {
                return getChildCount() > 0 ? getChildAt(0).getWidth() : 0;
            }
        });
    }
}
