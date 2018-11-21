package com.yudapramadjunaedi.githubuser.events;

import com.yudapramadjunaedi.githubuser.params.FabParams;

public class FabSetEvent implements Event {
    public static final String TYPE = "FabSetEvent";

    public FabParams fabParams;

    public FabSetEvent(FabParams fabParams) {
        this.fabParams = fabParams;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
