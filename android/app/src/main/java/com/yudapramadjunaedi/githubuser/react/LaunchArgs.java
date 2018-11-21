package com.yudapramadjunaedi.githubuser.react;

import android.content.Intent;
import android.os.Bundle;

public enum LaunchArgs {
    instance;

    private static final Bundle EMPTY = new Bundle();
    private Bundle launchArgs;

    public void set(Intent intent) {
        if (intent != null && intent.getExtras() != null && launchArgs == null) {
            this.launchArgs = intent.getExtras();
        }
    }

    public Bundle get() {
        return this.launchArgs == null ? EMPTY : this.launchArgs;
    }
}
