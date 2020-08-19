package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tyctak.map.entities._Entity;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.canalmap.libraries.Library_GR;
import com.tyctak.canalmap.libraries.Library_ME;

//import org.jsoup.helper.StringUtil;

import static com.tyctak.canalmap.MyApp.getContext;

public class Dialog_Entity extends AppCompatActivity {

    private final String TAG = "Dialog_Entity";

    private final Activity activity = this;
    private static String entityGuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_entity);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        boolean isMap = getIntent().getBooleanExtra("isMap", false);

        if (!isMap) {
            getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.8));
        } else {
            getWindow().setLayout((int) dm.widthPixels, (int) dm.heightPixels);
        }

        entityGuid = getIntent().getStringExtra("entityguid");
        _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);

        TextView entityName = (TextView) findViewById(R.id.entityName);
        TextView entityDescription = (TextView) findViewById(R.id.entityDescription);

        XP_Library_CM LIBCM = new XP_Library_CM();
        entityName.setText(LIBCM.isBlank(entity.EntityName) ? activity.getString(R.string.noName)  : entity.EntityName);
        entityDescription.setText(LIBCM.isBlank(entity.Description) ? activity.getString(R.string.noDescription)  : entity.Description);

        ImageButton websiteButton = (ImageButton) findViewById(R.id.buttonWebsite);
        ImageButton youtubeButton = (ImageButton) findViewById(R.id.buttonYoutube);
        ImageButton facebookButton = (ImageButton) findViewById(R.id.buttonFacebook);
        ImageButton twitterButton = (ImageButton) findViewById(R.id.buttonTwitter);
        ImageButton instagramButton = (ImageButton) findViewById(R.id.buttonInstagram);
        ImageButton gotoButton = (ImageButton) findViewById(R.id.buttonGoTo);
        ImageButton favouriteButton = (ImageButton) findViewById(R.id.buttonFavourite);
        ImageButton donateButton = (ImageButton) findViewById(R.id.donateButton);
        LinearLayout topPanel = (LinearLayout) findViewById(R.id.topPanelEntity);

        ImageView boat_image = (ImageView) findViewById(R.id.boat_image);

        if (entity.Avatar != null) {
            Library lib = new Library();
            boat_image.setImageBitmap(lib.decodeBinary(entity.Avatar));
        } else {
            boat_image.setVisibility(View.GONE);
        }

        boolean isGoto = ((entity.Longitude != 0 && entity.LastMoved > XP_Library_CM.getOldestEntityAllowedToMove()) || (entity.Longitude != 0 && entity.EntityGuid.equals(Global.getInstance().getDb().getMyEntityGuid())));

        dV(websiteButton, View.VISIBLE);
        dV(youtubeButton, View.VISIBLE);
        dV(facebookButton, View.VISIBLE);
        dV(twitterButton, View.VISIBLE);
        dV(instagramButton, View.VISIBLE);
        dV(gotoButton, View.VISIBLE);
        dV(favouriteButton, View.VISIBLE);

        dV(websiteButton, !LIBCM.isBlank(entity.Website));
        dV(youtubeButton, !LIBCM.isBlank(entity.YouTube));
        dV(facebookButton, !LIBCM.isBlank(entity.Facebook));
        dV(twitterButton, !LIBCM.isBlank(entity.Twitter));
        dV(instagramButton, !LIBCM.isBlank(entity.Instagram));
        dV(donateButton, !LIBCM.isBlank(entity.Patreon));
        dV(favouriteButton, !entity.EntityGuid.equals(Global.getInstance().getDb().getMyEntityGuid()));
        dV(gotoButton, !isMap);
        dV(topPanel, isMap);

        Library_GR LIBGR = new Library_GR();
        Drawable icon = LIBGR.getMarker(entity.IconName, 17);

        ImageView entityIcon = (ImageView) findViewById(R.id.entityIcon);
        entityIcon.setImageDrawable(icon);

        favouriteButton.setImageResource(entity.Favourite ? R.drawable.ic_favourite_on : R.drawable.ic_favourite_off);

        ImageView entityType1 = (ImageView) findViewById(R.id.entityType);
        ImageView entityType2 = (ImageView) findViewById(R.id.entityType2);
        String[] entityTypeArray = getContext().getResources().getStringArray(R.array.entitytype_array);
        String entityTypeValue = "ic_" + (isMap ? "item_" : "") + entityTypeArray[entity.EntityType].replace(" ", "").toLowerCase();

        entityType1.setImageResource(LIBGR.getDrawableId(getContext(), entityTypeValue));
        entityType2.setImageResource(LIBGR.getDrawableId(getContext(), entityTypeValue));

        dV(entityType1, !isMap);
        dV(entityType2, isMap);

        gotoButton.setTag(entity.EntityGuid + ":" + (isGoto ? "ic_goto" : "ic_goto_disabled"));
        gotoButton.setImageResource(isGoto ? R.drawable.ic_goto : R.drawable.ic_goto_disabled);

        View socialMediaDivision = (View) findViewById(R.id.socialMediaDivision);
        boolean isNotSocialMedia = (facebookButton.getVisibility() + twitterButton.getVisibility() + instagramButton.getVisibility() + youtubeButton.getVisibility() + websiteButton.getVisibility()) == (View.GONE * 5);

        dV(socialMediaDivision, !isNotSocialMedia);

        boolean isBusiness = (entityTypeValue.contains("entitytrader") && (!LIBCM.isBlank(entity.TradingName) || !LIBCM.isBlank(entity.Phone)));
        LinearLayout businessLayout = (LinearLayout) findViewById(R.id.businessLayout);

        dV(businessLayout, isBusiness);

        LinearLayout tradingLayout = (LinearLayout) findViewById(R.id.entityTradingName);
        dV(tradingLayout, !LIBCM.isBlank(entity.TradingName));
        TextView entityTradingNameText = (TextView) findViewById(R.id.entityTradingNameText);
        entityTradingNameText.setText(entity.TradingName);

        LinearLayout entityPhone = (LinearLayout) findViewById(R.id.entityPhone);
        dV(entityPhone, !LIBCM.isBlank(entity.Phone));
        TextView entityPhoneText = (TextView) findViewById(R.id.entityPhoneText);
        entityPhoneText.setText(entity.Phone);
    }

    private void dV(View target, boolean isVisible) {
        target.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void dV(View target, int value) {
        if (target.getVisibility() != value) target.setVisibility(value);
    }

    public void clickTwitter(View view) {
        _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);

        Library_ME LIBME = new Library_ME();
        if (!LIBME.LoadTwitter(activity, entity.Twitter)) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activity, R.string.twitterError);
        }
    }

    public void clickFacebook(View view) {
        _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);

        Library_ME LIBME = new Library_ME();
        if (!LIBME.LoadFacebook(activity, entity.Facebook)) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activity, R.string.facebookError);
        }
    }

    public void clickInstagram(View view) {
        _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);

        Library_ME LIBME = new Library_ME();
        if (!LIBME.LoadInstagram(activity, entity.Instagram)) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activity, R.string.instagramError);
        }
    }

    public void clickPatreon(View view) {
        _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);

        Library_ME LIBME = new Library_ME();
        if (!LIBME.LoadPatreon(activity, entity.Patreon)) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activity, R.string.patreonError);
        }
    }

    public void clickWebsite(View view) {
        _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);

        Library_ME LIBME = new Library_ME();
        if (!LIBME.LoadWebsite(activity, entity.Website)) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activity, R.string.websiteError);
        }
    }

    public void clickYoutube(View view) {
        _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);

        Library_ME LIBME = new Library_ME();
        if (!LIBME.LoadYoutube(activity, entity.YouTube)) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activity, R.string.youtubeError);
        }
    }

    public void clickGoTo(View view) {
        ImageButton button = (ImageButton) view.findViewById(R.id.buttonGoTo);
        String[] temp = button.getTag().toString().split(":");
        final String entityGuid = temp[0];

        if (temp[1].equals(getResources().getResourceEntryName(R.drawable.ic_goto))) {
            Intent intent = new Intent();
            intent.putExtra("entityguid", entityGuid);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    public void clickFavourite(View view) {
        ImageButton buttonFavourite = (ImageButton) view.findViewById(R.id.buttonFavourite);

        if (Global.getInstance().getDb().getEntityFavourite(entityGuid)) {
            buttonFavourite.setImageResource(R.drawable.ic_favourite_off);
            Global.getInstance().getDb().writeEntityFavourite(entityGuid, false);
        } else {
            buttonFavourite.setImageResource(R.drawable.ic_favourite_on);
            Global.getInstance().getDb().writeEntityFavourite(entityGuid, true);
        }

        clickFavourite();
    }

    private void clickFavourite() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
    }

    public void closeEntity(View view) {
        finish();
    }
}