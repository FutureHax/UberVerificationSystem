package com.t3hh4xx0r.uberverification;

import android.app.Application;

import com.t3hh4xx0r.uberverification.library.Verifier;

import org.joda.time.DateTimeConstants;


public class MyApplication extends Application {
    private final static String APPLICATION_KEY = ""; //
    private final static String CLIENT_KEY = ""; //

    @Override
    public void onCreate() {
        super.onCreate();
        Verifier.setup(this, APPLICATION_KEY, CLIENT_KEY, DateTimeConstants.MILLIS_PER_MINUTE);
    }
}
