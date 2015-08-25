package com.t3hh4xx0r.uberverification;

import android.app.Application;

import com.t3hh4xx0r.uberverification.library.Verifier;

import org.joda.time.DateTimeConstants;


public class MyApplication extends Application {
    private final static String APPLICATION_KEY = "XsH2zp29fq8kLl3BVpWQO1D5fjn4voTujjx2t0gi"; //
    private final static String CLIENT_KEY = "M0eZ7hKyccAmlQVmKxVGWuC4yx6HatIP2MaggukQ"; //

    @Override
    public void onCreate() {
        super.onCreate();
        Verifier.setup(this, APPLICATION_KEY, CLIENT_KEY, DateTimeConstants.MILLIS_PER_MINUTE);
    }
}
