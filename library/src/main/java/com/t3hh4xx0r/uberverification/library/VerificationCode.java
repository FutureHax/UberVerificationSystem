package com.t3hh4xx0r.uberverification.library;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.regex.Pattern;

public class VerificationCode {
    String code;
    public String attachedUser;

    public interface CodeValidationListener {
        void onValidation(boolean success, boolean isValid, VerificationCode code);
    }

    /**
     * Check the codes validity against Parse
     *
     * @param c
     * @param code
     * @param listener
     */
    public static void validateCode(final Context c, final String code,
                                    final CodeValidationListener listener) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "VerificationCode");
        query.whereEqualTo("code", code.trim().toLowerCase());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> res, ParseException e) {
                VerificationCode vCode = new VerificationCode();
                vCode.code = code;
                boolean success, isValid;

                if (e == null) {
                    success = true;
                    if (res.isEmpty()) {
                        isValid = false;
                    } else {
                        String email = getPrimaryEmail(c);
                        if (!res.get(0).has("attachedUser")) {
                            res.get(0).put("attachedUser", email);
                            res.get(0).saveInBackground();
                            vCode.attachedUser = email;
                            isValid = true;
                        } else {
                            vCode.attachedUser = res.get(0).getString("attachedUser");
                            ParseInstallObject.InstallObject obj = Verifier.getInstallObject(c);
                            if (vCode.attachedUser.equalsIgnoreCase(obj.getPrimaryEmail())) {
                                isValid = true;
                            } else {
                                isValid = false;
                            }
                        }
                    }
                } else {
                    success = false;
                    isValid = false;
                }

                listener.onValidation(success, isValid, vCode);
                Verifier.setHasVerifiedCode(c, isValid);
            }
        });
    }

    public static String getPrimaryEmail(Context c) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(c).getAccounts();
        if (accounts[0] != null) {
            if (emailPattern.matcher(accounts[0].name).matches()) {
                return accounts[0].name;
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return "VerificationCode [code=" + code + ", attachedUser="
                + attachedUser + "]";
    }
}
