package com.t3hh4xx0r.uberverification.library;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.parse.Parse;

import org.joda.time.DateTimeConstants;

/**
 * Created by kenkyger on 9/3/14.
 */
public class Verifier {

    private final static String PREF_VERIFIED = "verified";
    private final static String PREF_NUKED = "nuked";
    private final static String PREF_NUKED_MESSAGE = "nuked_message";
    private final static long DEFAULT_DEMO_PERIOD = DateTimeConstants.MILLIS_PER_DAY * 7;
    private final static String DEMO_PERIOD = "demo_period";

    private static void setDemoPeriodLength(Context c, long demoInMillis) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putLong(DEMO_PERIOD, demoInMillis).commit();
    }

    /**
     *
     * @param c
     * @return length in milliseconds of demo period
     */
    public static long getDemoPeriodLength(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getLong(DEMO_PERIOD, DEFAULT_DEMO_PERIOD);
    }

    /**
     *
     * Set if user has valid code.
     *
     * @param c
     * @param b
     */
    public static void setHasVerifiedCode(Context c, boolean b) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(PREF_VERIFIED, b).commit();
    }

    public static boolean getHasVerifiedCode(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(PREF_VERIFIED, false);
    }

    /**
     *
     * Get the message to displayed when the demo is expired. This message should contain a short
     * description of how long ago the demo expired
     *
     * @param c
     * @return the message
     *
     */
    public static String getAppNukedMessage(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(PREF_NUKED_MESSAGE, "");
    }

    /**
     * Set whether the demo has expired.
     *
     * @param c
     * @param b whether the demo is expired
     * @param appNukedMessage
     *
     */
    public static void setAppNuked(Context c, boolean b, String appNukedMessage) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(PREF_NUKED, b).putString(PREF_NUKED_MESSAGE, appNukedMessage).commit();
    }

    /**
     *
     * @param c
     * @return whether the demo ha expired
     *
     */
    public static boolean getIsAppNuked(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(PREF_NUKED, false);
    }

    public static void setParseInstallObject(ParseInstallObject obj, Context c) {
        Gson g = new Gson();
        String toStringValue = g.toJson(obj.getInstallObject());
        PreferenceManager.getDefaultSharedPreferences(c).edit().putString("parse_install", toStringValue).commit();
    }

    public static ParseInstallObject.InstallObject getInstallObject(Context c) {
        Gson g = new Gson();
        return g.fromJson(PreferenceManager.getDefaultSharedPreferences(c)
                        .getString("parse_install", ""),
                ParseInstallObject.InstallObject.class);
    }


    /**
     * Call from your {@link android.app.Application#onCreate()}
     *
     * @param c
     * @param parseApplicationId
     * @param parseClientKey
     * @param demoPeriodInMillis
     */
    public static void setup(Context c, String parseApplicationId, String parseClientKey, long demoPeriodInMillis) {
        Verifier.setDemoPeriodLength(c, demoPeriodInMillis);
        setup(c, parseApplicationId, parseClientKey);
    }

    /**
     * Call from your {@link android.app.Application#onCreate()}
     *
     * @param c
     * @param parseApplicationId
     * @param parseClientKey
     */
    public static void setup(Context c, String parseApplicationId, String parseClientKey) {
        Parse.initialize(c, parseApplicationId,
                parseClientKey);
    }

    /**
     *
     * For demo purposes mostly.
     *
     * @param c
     */
    public static void reset(Context c) {
        setHasVerifiedCode(c, false);
    }

}
