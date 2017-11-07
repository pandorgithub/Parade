package com.asmodeusstudio.parade;

import android.app.Application;

import com.twitter.sdk.android.core.Twitter;

/**
 * Parade for FretX
 * Created by pandor on 28/08/17 15:41.
 */

public class Parade extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Twitter.initialize(this);
    }
}
