package com.t3hh4xx0r.uberverification.library;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.joda.time.DateTimeConstants;

import java.text.DecimalFormat;
import java.util.List;

public class ParseInstallObject {
    private IParseInstallFinished parseInstallListener;
    private Context c;
    InstallObject installObject;

    private static final String PRIMARY_EMAIL = "primaryEmail";

    public ParseInstallObject(Context c) {
        this.c = c;
        installObject = new InstallObject();
    }

    public ParseInstallObject() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Call from your main {@link android.app.Activity#onCreate(android.os.Bundle)}
     *
     * @param c
     * @param listener
     */
    public static void createInstall(Context c, IParseInstallFinished listener) {
        ParseInstallObject o = new ParseInstallObject(c);
        String primaryEmail = VerificationCode.getEncryptedPrimaryEmail(c);
        if (!primaryEmail.isEmpty()) {
            o.installObject.setPrimaryEmail(primaryEmail);
        }

        o.setParseInstallListener(listener);
        o.getInstallByEmail();
    }

    private void setParseInstallListener(
            IParseInstallFinished parseInstallListener) {
        this.parseInstallListener = parseInstallListener;
    }

    private void getInstallByEmail() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Install");
        query.whereEqualTo(PRIMARY_EMAIL, installObject.primaryEmail);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    if (results.isEmpty()) {
                        createNewInstall();
                    } else {
                        updateInstallFromRemote(results.get(0));
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createNewInstall() {
        ParseObject install = toParseObject();
        install.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException arg0) {
                Verifier.setParseInstallObject(ParseInstallObject.this, c);
                parseInstallListener.finished(ParseInstallObject.this);
            }
        });
    }

    private ParseObject toParseObject() {
        ParseObject install = new ParseObject("Install");
        install.put(PRIMARY_EMAIL, installObject.primaryEmail);
        return install;
    }


    
    protected void updateInstallFromRemote(ParseObject install) {
        installObject.setFirstInstall(install.getCreatedAt().getTime());
        installObject.primaryEmail = install.getString(PRIMARY_EMAIL);
        long now = System.currentTimeMillis();
        if ((now - installObject.getFirstInstall()) > Verifier.getDemoPeriodLength(c)) {
            handleNukedInstall();
        } else {
            Verifier.setAppNuked(c, false, "");
        }

        Verifier.setParseInstallObject(ParseInstallObject.this, c);
        parseInstallListener.finished(this);

        Log.d("TIME SINCE INSTALL",
                Long.toString(now - installObject.firstInstall));
    }

    private void handleNukedInstall() {
        long now = System.currentTimeMillis();
        DecimalFormat f = new DecimalFormat("#");
        String mod = " mins ago.";
        long nukedFor = (now - installObject.firstInstall)
                / (DateTimeConstants.MILLIS_PER_MINUTE);
        if (nukedFor > 60) {
            mod = " hours ago.";
            nukedFor = nukedFor / 60;
            if (nukedFor > 24) {
                mod = " days ago.";
                nukedFor = nukedFor / 24;
            }
        }
        Verifier.setAppNuked(c, true,
                "Your demo expired " + f.format(nukedFor) + mod);
    }

    public interface IParseInstallFinished {
        void finished(ParseInstallObject object);
    }

    public InstallObject getInstallObject() {
        return installObject;
    }

    public class InstallObject {
        private String primaryEmail;
        private long firstInstall = -1;

        public String getPrimaryEmail() {
            return primaryEmail;
        }

        @Override
        public String toString() {
            return "InstallObject [getEncryptedPrimaryEmail()=" + getPrimaryEmail()
                    + ", getFirstInstall()=" + getFirstInstall() + "]";
        }

        public void setPrimaryEmail(String primaryEmail) {
            this.primaryEmail = primaryEmail;
        }

        public long getFirstInstall() {
            return firstInstall;
        }

        public void setFirstInstall(long firstInstall) {
            this.firstInstall = firstInstall;
        }
    }

}
