package com.t3hh4xx0r.uberverification.library;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

public class VerificationCode {
    String code;
    public String attachedUser;

    public interface CodeValidationListener {
        void onValidation(boolean success, boolean isValid, VerificationCode code);
    }


    //http://stackoverflow.com/a/5494474/1117029    
    public static String md5(String input, Context ctx) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(input.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
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
                        String email = getEncryptedPrimaryEmail(c);
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

    public static String getRawPrimaryEmail(Context c) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(c).getAccounts();
        if (accounts != null && accounts.length > 0) {
            for (Account acct : accounts) {
                if (acct.type.equals("com.google")) {
                    if (emailPattern.matcher(acct.name).matches()) {
                        return acct.name;
                    }
                }
            }
        }
        return "";
    }

    public static String getEncryptedPrimaryEmail(Context c) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(c).getAccounts();
        if (accounts != null && accounts.length > 0) {
            for (Account acct : accounts) {
                if (acct.type.equals("com.google")) {
                    if (emailPattern.matcher(acct.name).matches()) {
                        return md5(acct.name, c);
                    }
                }
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
