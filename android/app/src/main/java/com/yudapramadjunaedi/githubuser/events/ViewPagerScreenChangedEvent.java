package com.yudapramadjunaedi.githubuser.events;

public class ViewPagerScreenChangedEvent implements Event {
    public static final String TYPE = "ViewPagerScreenChangedEvent";

    @Override
    public String getType() {
        return TYPE;
    }
}
