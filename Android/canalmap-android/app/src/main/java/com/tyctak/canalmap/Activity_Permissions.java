package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.tyctak.map.entities.ILibrary_PE;
import com.tyctak.canalmap.libraries.Library_PE;

public class Activity_Permissions extends AppCompatActivity {

    final private String TAG = "Activity_Permissions";
    final private Activity_Permissions activity = this;
    private final Library_PE LIBPE = new Library_PE();
    boolean exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        final String[] permissions = getIntent().getStringArrayExtra("permissions");
        final boolean force = getIntent().getBooleanExtra("force", false);
        exit = getIntent().getBooleanExtra("exit", false);

        ILibrary_PE.enmPermissionGranted granted = LIBPE.checkPermissions(activity, force, permissions);
        if (granted == ILibrary_PE.enmPermissionGranted.Granted || (granted == ILibrary_PE.enmPermissionGranted.Refused || !force)) {
            returnResult(granted);
        }
    }

    private void returnResult(ILibrary_PE.enmPermissionGranted granted) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("granted", granted.ordinal());
        returnIntent.putExtra("exit", exit);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        returnResult((LIBPE.verifyPermissions(grantResults) ? ILibrary_PE.enmPermissionGranted.Granted : ILibrary_PE.enmPermissionGranted.Refused));
    }
}