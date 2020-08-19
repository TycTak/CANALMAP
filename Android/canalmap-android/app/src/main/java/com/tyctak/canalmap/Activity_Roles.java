package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._Role;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.map.libraries.XP_Library_WS;

public class Activity_Roles extends AppCompatActivity {

    final private String TAG = "Activity_Roles";
    final private Activity_Roles activity = this;

    int args[];
    _Role.enmRoles role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roles);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_sub);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        role = _Role.enmRoles.values()[getIntent().getIntExtra("role", _Role.enmRoles.Publisher.ordinal())];
        args = getIntent().getIntArrayExtra("args");

        boolean verifyYourself = getIntent().getBooleanExtra("verify", false);
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        boolean roleAccepted = false;

        if (role == _Role.enmRoles.Administrator && mySettings.IsAdministrator) {
            roleAccepted = true;
        } else if (role == _Role.enmRoles.Publisher && mySettings.IsPublisher) {
            roleAccepted = true;
        } else if (role == _Role.enmRoles.Reviewer && mySettings.IsReviewer) {
            roleAccepted = true;
        } else if (role == _Role.enmRoles.Premium && mySettings.IsPremium) {
            roleAccepted = true;
        }

        if (roleAccepted) {
            returnToParent();
//            finish();
//            return;
        } else {
            TextView textVerifyYourself = (TextView) findViewById(R.id.textVerifyYourself);
            textVerifyYourself.setVisibility(verifyYourself ? View.VISIBLE : View.GONE);

            TextView textViewRoleTitle = (TextView) findViewById(R.id.textViewRoleTitle);
            TextView textViewRoleDescription = (TextView) findViewById(R.id.textViewRoleDescription);
            TextView textViewRoleOverview = (TextView) findViewById(R.id.textViewRoleOverview);
            Button buttonConfirm = (Button) findViewById(R.id.confirmRoleRequest);

            buttonConfirm.setTag(role.ordinal());

            if (role == _Role.enmRoles.Administrator) {
                textViewRoleTitle.setText(R.string.roleTitleAdministrator);
                textViewRoleOverview.setText(R.string.roleAdministratorOverview);
                textViewRoleDescription.setText(R.string.roleDescriptionAdministrator);
                getSupportActionBar().setTitle("Become an Administrator");
            } else if (role == _Role.enmRoles.Premium) {
                textViewRoleTitle.setText(R.string.roleTitlePremium);
                textViewRoleOverview.setText(R.string.rolePremiumOverview);
                textViewRoleDescription.setText(R.string.roleDescriptionPremium);
                getSupportActionBar().setTitle("Become a Premium Member");
            } else if (role == _Role.enmRoles.Publisher) {
                textViewRoleTitle.setText(R.string.roleTitlePublisher);
                textViewRoleOverview.setText(R.string.rolePublisherOverview);
                textViewRoleDescription.setText(R.string.roleDescriptionPublisher);
                getSupportActionBar().setTitle("Become a Publisher");
            } else {
                textViewRoleTitle.setText(R.string.roleTitleReviewer);
                textViewRoleOverview.setText(R.string.roleReviewerOverview);
                textViewRoleDescription.setText(R.string.roleDescriptionReviewer);
                getSupportActionBar().setTitle("Become a Reviewer");
            }

            EditText phoneNumber = (EditText) findViewById(R.id.phone_number_edit);
            phoneNumber.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    errorOccurred("");
                    return false;
                }

            });

            EditText verificationCodeEdit = (EditText) findViewById(R.id.verification_code_edit);
            verificationCodeEdit.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    errorCodeOccurred("");
                    return false;
                }

            });
        }
    }

    public void confirmRoleRequest(View view) {
        errorOccurred("");

        final _Role.enmRoles role = _Role.enmRoles.values()[(int) view.getTag()];
        EditText phoneNumber = (EditText) findViewById(R.id.phone_number_edit);

        final String phoneNumberText = phoneNumber.getText().toString();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_CM XPLIBCM = new XP_Library_CM();
                XP_Library_WS XPLIBWS = new XP_Library_WS();

                String internationalPhoneNumber = XPLIBWS.verifyPhoneNumber("GB", phoneNumberText);

                if (!XPLIBCM.isBlank(internationalPhoneNumber)) {
                    if (XPLIBWS.addRole(role, internationalPhoneNumber)) {
                        runOnUiThread(new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout phoneNumberLayout = (LinearLayout) findViewById(R.id.phoneNumberLayout);
                                LinearLayout confirmLayout = (LinearLayout) findViewById(R.id.confirmLayout);
                                LinearLayout verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);

                                phoneNumberLayout.setVisibility(View.GONE);
                                confirmLayout.setVisibility(View.GONE);
                                verificationLayout.setVisibility(View.VISIBLE);
                            }
                        }));
                    } else {
                        runOnUiThread(new Thread(new Runnable() {
                            @Override
                            public void run() {
                                errorOccurred("Unable to add role");
                            }
                        }));
                    }
                } else {
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            errorOccurred("Not a valid phone number");
                        }
                    }));
                }
            }
        });

        thread.start();
    }

    private void errorCodeOccurred(String message) {
        TextView textViewCodeError = (TextView) findViewById(R.id.textViewCodeError);
        XP_Library_CM XPLIBCM = new XP_Library_CM();

        if (!XPLIBCM.isBlank(message)) {
            textViewCodeError.setText(message);
            textViewCodeError.setVisibility(View.VISIBLE);
        } else {
            textViewCodeError.setVisibility(View.INVISIBLE);
        }
    }

    private void errorOccurred(String message) {
        TextView textViewRoleError = (TextView) findViewById(R.id.textViewRoleError);
        TextView textViewCodeError = (TextView) findViewById(R.id.textViewCodeError);

        textViewCodeError.setText("");
        textViewCodeError.setVisibility(View.INVISIBLE);

        XP_Library_CM XPLIBCM = new XP_Library_CM();

        if (!XPLIBCM.isBlank(message)) {
            textViewRoleError.setText(message);
            textViewRoleError.setVisibility(View.VISIBLE);
        } else {
            textViewRoleError.setVisibility(View.INVISIBLE);
        }
    }

    public void cancelVerificationCode(View view) {
        LinearLayout phoneNumberLayout = (LinearLayout) findViewById(R.id.phoneNumberLayout);
        LinearLayout confirmLayout = (LinearLayout) findViewById(R.id.confirmLayout);
        LinearLayout verificationLayout = (LinearLayout) findViewById(R.id.verificationLayout);

        EditText verificationCodeEdit = (EditText) findViewById(R.id.verification_code_edit);
        verificationCodeEdit.setText("");

        EditText phoneNumberCodeEdit = (EditText) findViewById(R.id.phone_number_edit);
        phoneNumberCodeEdit.setText("");

        phoneNumberLayout.setVisibility(View.VISIBLE);
        confirmLayout.setVisibility(View.VISIBLE);
        verificationLayout.setVisibility(View.GONE);

        errorOccurred("");
    }

    public void confirmVerificationCode(View view) {
        XP_Library_CM XPLIBCM = new XP_Library_CM();

        final EditText verificationCodeEdit = (EditText) findViewById(R.id.verification_code_edit);
        final String verificationCode = verificationCodeEdit.getText().toString();

        if (!XPLIBCM.isBlank(verificationCode)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    XP_Library_WS XPLIBWS = new XP_Library_WS();

                    if (XPLIBWS.verifyRole(verificationCode)) {
                        if (XPLIBWS.getRoles()) {
                            if (role == _Role.enmRoles.Administrator) {
                                Global.getInstance().getDb().writeAdminRequest(true);
                            } else if (role == _Role.enmRoles.Premium) {
                                Global.getInstance().getDb().writePremiumRequest(true);
                            }

                            returnToParent();
                        } else {
                            runOnUiThread(new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    errorCodeOccurred("Something caused an error");
                                }
                            }));
                        }
                    } else {
                        runOnUiThread(new Thread(new Runnable() {
                            @Override
                            public void run() {
                                errorCodeOccurred("Unable to verify code");
                            }
                        }));
                    }
                }
            });

            thread.start();
        }
    }

    private void returnToParent() {
        Intent intent = new Intent();
        intent.putExtra("args", args);
        intent.putExtra("role", role);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void cancelToParent() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            cancelToParent();
//            onBackPressed(); return true;
        }

        return super.onOptionsItemSelected(item);
    }
}