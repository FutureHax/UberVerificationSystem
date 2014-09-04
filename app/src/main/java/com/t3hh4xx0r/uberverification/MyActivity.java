package com.t3hh4xx0r.uberverification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.t3hh4xx0r.uberverification.library.ParseInstallObject;
import com.t3hh4xx0r.uberverification.library.VerificationCode;
import com.t3hh4xx0r.uberverification.library.Verifier;


public class MyActivity extends Activity {
    TextView message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        message = (TextView) findViewById(R.id.message);
        setupInstall();
    }

    public void setMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ");
        String pEmail = Verifier.getInstallObject(this).getPrimaryEmail();
        if (pEmail != null) {
            sb.append(pEmail);
            sb.append(".\n");
        }

        if (Verifier.getHasVerifiedCode(this)) {
            sb.append("You have authenticated your code!");
        } else {
            if (Verifier.getIsAppNuked(this)) {
                sb.append(Verifier.getAppNukedMessage(this));
            } else {
                sb.append("Your demo is still valid.");
            }
        }

        message.setText(sb.toString());
    }

    private void setupInstall() {
        ParseInstallObject.createInstall(this,
                new ParseInstallObject.IParseInstallFinished() {
                    @Override
                    public void finished(ParseInstallObject obj) {
                        setMessage();
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        MenuItem upgrade = menu.findItem(R.id.action_upgrade);
        MenuItem reset = menu.findItem(R.id.action_reset);
        if (Verifier.getHasVerifiedCode(this)) {
            menu.removeItem(upgrade.getItemId());
        } else {
            menu.removeItem(reset.getItemId());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_upgrade) {
            launchRedeemFlow();
            return true;
        } else if (id == R.id.action_reset) {
            Verifier.reset(this);
            invalidateOptionsMenu();
            setMessage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void launchRedeemFlow() {
        final ViewFlipper flippy = new ViewFlipper(this);
        final EditText input = new EditText(this);
        final ProgressBar pBar = new ProgressBar(this);
        flippy.addView(input);
        flippy.addView(pBar);

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Redeem Code");
        if (Verifier.getIsAppNuked(this)) {
            b.setMessage(Verifier.getAppNukedMessage(this));
        } else {
            b.setMessage("Your demo is still valid");
        }
        b.setView(flippy);
        b.setPositiveButton("Redeem", null);
        final AlertDialog d = b.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchRedeemCode(input.getText().toString(), d);
                        flippy.setDisplayedChild(1);
                    }
                });
            }
        });
        d.show();
    }

    protected void launchRedeemCode(String code, final AlertDialog d) {
        VerificationCode.validateCode(this, code,
                new VerificationCode.CodeValidationListener() {
                    @Override
                    public void onValidation(boolean success, boolean isValid, VerificationCode code) {
                        d.dismiss();
                        if (success) {
                            Log.d("THE CODE", code.toString());
                            if (isValid) {
                                AlertDialog.Builder b = new AlertDialog.Builder(
                                        MyActivity.this);
                                b.setTitle("Valid Code");
                                b.setMessage("Thank you! You have now removed the demo restrictions. Please restart the app.");
                                b.setPositiveButton("Restart",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                setupInstall();
                                                invalidateOptionsMenu();
                                            }
                                        }
                                );
                                b.create().show();
                            } else {
                                AlertDialog.Builder b = new AlertDialog.Builder(
                                        MyActivity.this);
                                b.setTitle("Invalid Code");
                                ParseInstallObject.InstallObject obj = Verifier.getInstallObject(MyActivity.this);

                                if (code.attachedUser == null) {
                                    b.setMessage("Please try again.");
                                } else {
                                    if (!code.attachedUser.equalsIgnoreCase(obj
                                            .getPrimaryEmail())) {
                                        b.setMessage("This code has already been redeemed by another account.");
                                    } else {
                                        b.setMessage("Please try again.");
                                    }
                                }
                                b.setPositiveButton("Dismiss",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        }
                                );
                                b.create().show();
                            }
                        }
                    }
                }
        );
    }

}
