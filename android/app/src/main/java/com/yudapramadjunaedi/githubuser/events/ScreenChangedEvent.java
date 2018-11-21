package com.yudapramadjunaedi.githubuser.events;

import com.yudapramadjunaedi.githubuser.params.BaseScreenParams;
import com.yudapramadjunaedi.githubuser.params.FabParams;

public class ScreenChangedEvent implements Event {
    public static final String TYPE = "ScreenChangedEvent";
    public FabParams fabParams;

    public ScreenChangedEvent(BaseScreenParams screenParams) {
        this.fabParams = screenParams.getFab();
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
