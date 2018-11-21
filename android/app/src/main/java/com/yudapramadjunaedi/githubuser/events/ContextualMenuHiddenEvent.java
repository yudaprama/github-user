package com.yudapramadjunaedi.githubuser.events;

public class ContextualMenuHiddenEvent implements Event {
    public static final String TYPE = "ContextualMenuDismissed";
    @Override
    public String getType() {
        return TYPE;
    }
}
