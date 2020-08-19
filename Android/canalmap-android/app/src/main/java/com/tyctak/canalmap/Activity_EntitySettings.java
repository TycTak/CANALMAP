package com.tyctak.canalmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.tyctak.map.entities._Entity;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.canalmap.libraries.Library_GR;
import com.tyctak.canalmap.libraries.Library_ME;

//import org.jsoup.helper.StringUtil;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Activity_EntitySettings extends AppCompatActivity {

    final private String TAG = "Activity_EntitySettings";
    final private Activity_EntitySettings activity = this;

    final private int GALLERY_REQUEST = 1;
    final private int PICK_ICON_REQUEST = 2;
    private static String previousEntityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entitysettings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_save);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.entitySettingsTitle);

        String[] myResArray = getResources().getStringArray(R.array.entitytype_array);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, myResArray);
        Spinner entity_entitytype_edit = (Spinner) findViewById(R.id.entitytype_spinner);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_view_dropdown);
        entity_entitytype_edit.setAdapter(arrayAdapter);

        entity_entitytype_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getSelectedItem().toString();
                boolean isCustomEntity1 = Pattern.matches(getString(R.string.customEntity1Check), item);
                boolean isCustomEntity2 = Pattern.matches(getString(R.string.customEntity2Check), item);

                LinearLayout customEntity1 = (LinearLayout) findViewById(R.id.customEntity1);
                customEntity1.setVisibility(isCustomEntity1 ? View.VISIBLE : View.GONE);

                LinearLayout customEntity2 = (LinearLayout) findViewById(R.id.customEntity2);
                customEntity2.setVisibility(isCustomEntity2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        EditText entity_name_edit = (EditText) findViewById(R.id.entity_name_edit);
        EditText entity_description_edit = (EditText) findViewById(R.id.entity_people_edit);

        EditText entity_phone_edit = (EditText) findViewById(R.id.phone_edit);
        EditText entity_trading_edit = (EditText) findViewById(R.id.trading_edit);
        EditText entity_facebook_edit = (EditText) findViewById(R.id.facebook_edit);
        EditText entity_twitter_edit = (EditText) findViewById(R.id.twitter_edit);
        EditText entity_instagram_edit = (EditText) findViewById(R.id.instagram_edit);
        EditText entity_youtube_edit = (EditText) findViewById(R.id.youtube_edit);
        EditText entity_patreon_edit = (EditText) findViewById(R.id.patreon_edit);
        EditText entity_website_edit = (EditText) findViewById(R.id.website_edit);
        EditText entity_email_edit = (EditText) findViewById(R.id.entityEmailEdit);
        EditText entity_reference_edit = (EditText) findViewById(R.id.entityReferenceEdit);
        EditText entity_metadata1_edit = (EditText) findViewById(R.id.entityMetaData1Edit);
        EditText entity_metadata2_edit = (EditText) findViewById(R.id.entityMetaData2Edit);
        CheckBox entity_isactive_edit = (CheckBox) findViewById(R.id.isActive);

        ImageView entity_avatar = (ImageView) findViewById(R.id.entity_avatar);
        ImageView entity_icon = (ImageView) findViewById(R.id.entity_icon);

        _Entity myEntitySettings = Global.getInstance().getDb().getMyEntitySettings();

        previousEntityName = myEntitySettings.EntityName;
        entity_isactive_edit.setChecked(myEntitySettings.IsActive);

        entity_entitytype_edit.setSelection(myEntitySettings.EntityType);
        entity_name_edit.setText(myEntitySettings.EntityName);
        entity_description_edit.setText(myEntitySettings.Description);
        entity_phone_edit.setText(myEntitySettings.Phone);
        entity_trading_edit.setText(myEntitySettings.TradingName);
        entity_facebook_edit.setText(myEntitySettings.Facebook);
        entity_twitter_edit.setText(myEntitySettings.Twitter);
        entity_instagram_edit.setText(myEntitySettings.Instagram);
        entity_youtube_edit.setText(myEntitySettings.YouTube);
        entity_patreon_edit.setText(myEntitySettings.Patreon);
        entity_website_edit.setText(myEntitySettings.Website);
        entity_email_edit.setText(myEntitySettings.Email);
        entity_reference_edit.setText(myEntitySettings.Reference);
        entity_metadata1_edit.setText(myEntitySettings.MetaData1);
        entity_metadata2_edit.setText(myEntitySettings.MetaData2);

        XP_Library_CM LIBCM = new XP_Library_CM();

        if (!LIBCM.isBlank(myEntitySettings.IconName)) {
            myEntitySettings.Icon = getResources().getIdentifier(myEntitySettings.IconName, "drawable", getPackageName());
            entity_icon.setImageResource(myEntitySettings.Icon);
            entity_icon.setTag(myEntitySettings.Icon + "," + myEntitySettings.IconName);
        } else {
            entity_icon.setImageResource(R.drawable.ic_icon__logo);
            entity_icon.setTag(R.drawable.ic_icon__logo + ",ic_icon__logo");
        }

        if (myEntitySettings.Avatar != null) {
            Library lib = new Library();
            entity_avatar.setImageBitmap(lib.decodeBinary(myEntitySettings.Avatar));
        } else {
            entity_avatar.setImageResource(R.drawable.ic_photo);
        }
    }

    public void pickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, GALLERY_REQUEST);
    }

    public void saveEntitySettings(MenuItem item) {
        EditText entity_name_edit = (EditText) findViewById(R.id.entity_name_edit);
        EditText entity_description_edit = (EditText) findViewById(R.id.entity_people_edit);
        ImageView entity_icon = ((ImageView)findViewById(R.id.entity_icon));
        ImageView entity_avatar = ((ImageView)findViewById(R.id.entity_avatar));

        Spinner entity_entitytype_edit = (Spinner) findViewById(R.id.entitytype_spinner);
        EditText entity_phone_edit = (EditText) findViewById(R.id.phone_edit);
        EditText entity_trading_edit = (EditText) findViewById(R.id.trading_edit);
        EditText entity_facebook_edit = (EditText) findViewById(R.id.facebook_edit);
        EditText entity_twitter_edit = (EditText) findViewById(R.id.twitter_edit);
        EditText entity_instagram_edit = (EditText) findViewById(R.id.instagram_edit);
        EditText entity_youtube_edit = (EditText) findViewById(R.id.youtube_edit);
        EditText entity_patreon_edit = (EditText) findViewById(R.id.patreon_edit);
        EditText entity_website_edit = (EditText) findViewById(R.id.website_edit);
        EditText entity_email_edit = (EditText) findViewById(R.id.entityEmailEdit);
        EditText entity_reference_edit = (EditText) findViewById(R.id.entityReferenceEdit);
        EditText entity_metadata1_edit = (EditText) findViewById(R.id.entityMetaData1Edit);
        EditText entity_metadata2_edit = (EditText) findViewById(R.id.entityMetaData2Edit);
        CheckBox entity_isactive_edit = (CheckBox) findViewById(R.id.isActive);

        Bitmap avatar = (entity_avatar.getDrawable() instanceof BitmapDrawable ? ((BitmapDrawable)entity_avatar.getDrawable()).getBitmap() : null);

        Integer iconId = 0;
        String iconName = null;

        if (entity_icon.getTag() != null) {
            String[] values = entity_icon.getTag().toString().split(",");
            iconId = Integer.parseInt(values[0]);
            iconName = values[1];
        }

        Library_GR LIBGR = new Library_GR();
        Bitmap avatarMarker = LIBGR.getAvatarMarker(this.getBaseContext(), iconId, iconName, avatar, false);

        _Entity myEntitySettings = Global.getInstance().getDb().getMyEntitySettings();

        XP_Library_CM LIBCM = new XP_Library_CM();

        String entityName = LIBCM.getValue(entity_name_edit.getText().toString());
        String description = LIBCM.getValue(entity_description_edit.getText().toString());
        boolean isActive = entity_isactive_edit.isChecked();

        if (isActive && LIBCM.isBlank(entityName)) {
            entityName = LIBCM.getUUID().replace("-", "").substring(0, 10).toUpperCase();
            entity_name_edit.setText(entityName);
        }

        String entityTypeValue = entity_entitytype_edit.getSelectedItem().toString();
        String[] myResArray = getResources().getStringArray(R.array.entitytype_array);
        int entityType = Arrays.asList(myResArray).indexOf(entityTypeValue);

        String phone = LIBCM.getValue(entity_phone_edit.getText().toString());
        String trading = LIBCM.getValue(entity_trading_edit.getText().toString());
        String facebook = LIBCM.getValue(entity_facebook_edit.getText().toString());
        String twitter = LIBCM.getValue(entity_twitter_edit.getText().toString());
        String instagram = LIBCM.getValue(entity_instagram_edit.getText().toString());
        String youtube = LIBCM.getValue(entity_youtube_edit.getText().toString());
        String patreon = LIBCM.getValue(entity_patreon_edit.getText().toString());
        String website = LIBCM.getValue(entity_website_edit.getText().toString());
        String email = LIBCM.getValue(entity_email_edit.getText().toString());
        String reference = LIBCM.getValue(entity_reference_edit.getText().toString());
        String metaData1 = LIBCM.getValue(entity_metadata1_edit.getText().toString());
        String metaData2 = LIBCM.getValue(entity_metadata2_edit.getText().toString());

        if (website != null && !website.toLowerCase().startsWith("http")) {
            website = "http://" + website;
            entity_website_edit.setText(website);
        }

        Library_UI LIBUI = new Library_UI();

        boolean isCustomEntity1 = Pattern.matches(getString(R.string.customEntity1Check), entityTypeValue);
        boolean isCustomEntity2 = Pattern.matches(getString(R.string.customEntity2Check), entityTypeValue);

        if (!isCustomEntity1) {
            reference = null;
            metaData1 = null;
            metaData2 = null;
        }

        if (!isCustomEntity2) {
            phone = null;
            trading = null;
        }

        Library_ME LIBME = new Library_ME();

        if (facebook != null && LIBME.ValidateFacebook(Library_ME.enmURLType.App, facebook).toString().isEmpty()) {
            LIBUI.snackBar(activity, R.string.facebookInvalid);
        } else if (twitter != null && LIBME.ValidateTwitter(Library_ME.enmURLType.App, twitter).toString().isEmpty()) {
            LIBUI.snackBar(activity, R.string.twitterInvalid);
        } else if (instagram != null && LIBME.ValidateInstagram(Library_ME.enmURLType.App, instagram).toString().isEmpty()) {
            LIBUI.snackBar(activity, R.string.instagramInvalid);
        } else if (youtube != null && LIBME.ValidateYouTube(Library_ME.enmURLType.App, youtube).toString().isEmpty()) {
            LIBUI.snackBar(activity, R.string.youtubeInvalid);
        } else if (patreon != null && LIBME.ValidatePatreon(Library_ME.enmURLType.App, patreon).toString().isEmpty()) {
            LIBUI.snackBar(activity, R.string.patreonInvalid);
        } else if (website != null && !Patterns.WEB_URL.matcher(website).matches()) {
            LIBUI.snackBar(activity, R.string.websiteInvalid);
        } else if (phone != null && !Patterns.PHONE.matcher(phone).matches()) {
            LIBUI.snackBar(activity, R.string.phoneInvalid);
        } else if (email != null && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            LIBUI.snackBar(activity, R.string.emailInvalid);
        } else if (reference != null && !Pattern.matches(getString(R.string.referenceRegex), reference)) {
            LIBUI.snackBar(activity, R.string.referenceInvalid);
        } else if (metaData1 != null && !Pattern.matches(getString(R.string.metaData1Regex), metaData1)) {
            LIBUI.snackBar(activity, R.string.metaData1Invalid);
        } else if (metaData2 != null && !Pattern.matches(getString(R.string.metaData2Regex), metaData2)) {
            LIBUI.snackBar(activity, R.string.metaData2Invalid);
        } else if (!LIBCM.isBlank(previousEntityName) && LIBCM.isBlank(entityName)) {
            LIBUI.snackBar(activity, R.string.entityNameBlank);
        } else {
            Log.d(TAG, "isActive=" + isActive);

            Library LIB = new Library();
            byte[] avatarBinary = (avatar != null ? LIB.encodeBinary(avatar) : null);
            byte[] avatarMarkerBinary = (avatarMarker != null ? LIB.encodeBinary(avatarMarker) : null);

            myEntitySettings.EntityName = entityName;
            myEntitySettings.Description = description;
            myEntitySettings.Avatar = avatarBinary;
            myEntitySettings.Icon = iconId;
            myEntitySettings.IconName = iconName;
            myEntitySettings.AvatarMarker = avatarMarkerBinary;
            myEntitySettings.EntityType = entityType;
            myEntitySettings.Phone = phone;
            myEntitySettings.TradingName = trading;
            myEntitySettings.Facebook = facebook;
            myEntitySettings.Twitter = twitter;
            myEntitySettings.Instagram = instagram;
            myEntitySettings.Website = website;
            myEntitySettings.Email = email;
            myEntitySettings.Reference = reference;
            myEntitySettings.MetaData1 = metaData1;
            myEntitySettings.MetaData2 = metaData2;
            myEntitySettings.YouTube = youtube;
            myEntitySettings.Patreon = patreon;
            myEntitySettings.IsActive = isActive;

            if (Global.getInstance().getDb().writeEntity(null, myEntitySettings)) {
                Intent intent = new Intent();
                intent.putExtra("isactive", isActive);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    public void clearImage(View view) {
        ImageView avatar = (ImageView) findViewById(R.id.entity_avatar);
        avatar.setImageResource(R.drawable.ic_photo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Bundle extras;

        if (requestCode == GALLERY_REQUEST) {
            Bitmap newAvatar = null;
            extras = data.getExtras();

            if (extras != null) {
                newAvatar = extras.getParcelable("data");
            } else if (data != null) {
                try {
                    Uri selectedImageUri = data.getData();
                    InputStream is = this.getContentResolver().openInputStream(selectedImageUri);
                    if (is != null) newAvatar = BitmapFactory.decodeStream(is);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (newAvatar != null) {
                Library_GR LIBGR = new Library_GR();
                Bitmap resizedAvatar = LIBGR.getResizedBitmap(newAvatar, 100, 100);

                ImageView avatar = (ImageView) findViewById(R.id.entity_avatar);
                avatar.setImageBitmap(resizedAvatar);
            }
        }

        if (requestCode == PICK_ICON_REQUEST) {
            extras = data.getExtras();

            if (extras != null) {
                Integer iconId = extras.getInt("iconid");
                String iconName = extras.getString("iconname");
                String tag = iconId + "," + iconName;

                ImageView entity_icon = (ImageView) findViewById(R.id.entity_icon);

                if (iconId != 0) {
                    entity_icon.setImageResource(iconId);
                    entity_icon.setTag(tag);
                } else {
                    entity_icon.setImageResource(R.drawable.ic_icon__logo);
                    entity_icon.setTag(R.drawable.ic_icon__logo + ",ic_icon__logo");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.saveactionbar_entitysettings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pickIcon(View view) {
        startActivityForResult(new Intent(this, Dialog_Icons.class), PICK_ICON_REQUEST);
    }
}