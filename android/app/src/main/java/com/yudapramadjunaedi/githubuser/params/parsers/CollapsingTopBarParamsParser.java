package com.yudapramadjunaedi.githubuser.params.parsers;

import android.os.Bundle;

import com.yudapramadjunaedi.githubuser.params.CollapsingTopBarParams;
import com.yudapramadjunaedi.githubuser.params.StyleParams;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.behaviours.CollapseBehaviour;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.behaviours.CollapseTitleBarBehaviour;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.behaviours.CollapseTopBarBehaviour;
import com.yudapramadjunaedi.githubuser.views.collapsingToolbar.behaviours.TitleBarHideOnScrollBehaviour;

class CollapsingTopBarParamsParser extends Parser {
    private Bundle params;
    private boolean titleBarHideOnScroll;
    private boolean drawBelowTopBar;
    private final boolean hasReactView;
    private final boolean hasBackgroundImage;

    CollapsingTopBarParamsParser(Bundle params, boolean titleBarHideOnScroll, boolean drawBelowTopBar) {
        this.params = params;
        this.titleBarHideOnScroll = titleBarHideOnScroll;
        this.drawBelowTopBar = drawBelowTopBar;
        hasReactView = params.containsKey("collapsingToolBarComponent");
        hasBackgroundImage = params.containsKey("collapsingToolBarImage");
    }

    public CollapsingTopBarParams parse() {
        if (!validateParams()) {
            return null;
        }
        CollapsingTopBarParams result = new CollapsingTopBarParams();
        result.imageUri = params.getString("collapsingToolBarImage", null);
        result.reactViewId = params.getString("collapsingToolBarComponent", null);
        result.expendOnTopTabChange = params.getBoolean("expendCollapsingToolBarOnTopTabChange");
        result.scrimColor = getColor(params, "collapsingToolBarCollapsedColor", new StyleParams.Color());
        result.expendedTitleBarColor = getColor(params, "collapsingToolBarExpendedColor", new StyleParams.Color());
        result.showTitleWhenCollapsed = hasReactView;
        result.showTitleWhenExpended = params.getBoolean("showTitleWhenExpended", result.expendedTitleBarColor.hasColor());
        result.collapseBehaviour = getCollapseBehaviour();
        return result;
    }

    private boolean validateParams() {
        return titleBarHideOnScroll || hasImageOrReactView();
    }

    private CollapseBehaviour getCollapseBehaviour() {
        if (hasImageOrReactView()) {
            return new CollapseTopBarBehaviour();
        }
        if (titleBarHideOnScroll && drawBelowTopBar) {
            return new CollapseTitleBarBehaviour();
        }
        return new TitleBarHideOnScrollBehaviour();
    }

    private boolean hasImageOrReactView() {
        return hasBackgroundImage || hasReactView;
    }
}
