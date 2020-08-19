package com.tyctak.canalmap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tyctak.map.entities._Entity;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.canalmap.libraries.Library_GR;

//import org.jsoup.helper.StringUtil;

import static com.tyctak.canalmap.MyApp.getContext;

public class XXX_CursorAdapter_Entities extends CursorAdapter {

    final private String TAG = "CursorAdapter_Entities";

    public XXX_CursorAdapter_Entities(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.cursoradapter_entities, parent, false);
    }

    private _Entity getEntity(Cursor cursor) {
        _Entity entity = new _Entity();

        entity.EntityGuid = cursor.getString(1);
        entity.EntityName = cursor.getString(2);
        entity.Description = cursor.getString(3);
        entity.AvatarMarker = cursor.getBlob(4);
        entity.Status = _Entity.enmStatus.values()[cursor.getInt(5)];
        entity.Longitude = cursor.getDouble(6);
        entity.Latitude = cursor.getDouble(7);
        entity.Tracker1 = cursor.getInt(8);
        entity.Distance = cursor.getDouble(9);
        entity.Facebook = cursor.getString(10);
        entity.Twitter = cursor.getString(11);
        entity.Instagram = cursor.getString(12);
        entity.Favourite = (cursor.getInt(13) != 0);
        entity.EntityType = cursor.getInt(14);
        entity.YouTube = cursor.getString(15);
        entity.Website = cursor.getString(16);
        entity.LastMoved = cursor.getLong(17);

        return entity;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        _Entity entity = getEntity(cursor);

        boolean isMyEntity = entity.EntityGuid.equals(Global.getInstance().getDb().getMyEntityGuid());

        Library_GR LIBGR = new Library_GR();
        Library_UI LIBUI = new Library_UI();

        TextView description = (TextView) view.findViewById(R.id.entityDescription);
        TextView name = (TextView) view.findViewById(R.id.entityName);
        ImageView imageView = (ImageView) view.findViewById(R.id.entityMarker);
        ImageView imageWebsite = (ImageView) view.findViewById(R.id.imageWebsite);
        ImageView imageFacebook = (ImageView) view.findViewById(R.id.imageFacebook);
        ImageView imageTwitter = (ImageView) view.findViewById(R.id.imageTwitter);
        ImageView imageInstagram = (ImageView) view.findViewById(R.id.imageInstagram);
        ImageView imageYoutube = (ImageView) view.findViewById(R.id.imageYouTube);
        ImageButton gotoButton = (ImageButton) view.findViewById(R.id.buttonGoTo);
        ImageButton favouriteButton = (ImageButton) view.findViewById(R.id.buttonFavourite);
        LinearLayout socialMedia = (LinearLayout) view.findViewById(R.id.socialMedia);
        LinearLayout myEntityMarker = (LinearLayout) view.findViewById(R.id.myEntityMarker);
        ImageView entityType = (ImageView) view.findViewById(R.id.entityType);

        XP_Library_CM LIBCM = new XP_Library_CM();
        name.setText(LIBCM.isBlank(entity.EntityName) ? context.getString(R.string.noName) : entity.EntityName);
        description.setText(LIBCM.isBlank(entity.Description) ? context.getString(R.string.noDescription) : entity.Description);

        Drawable icon = LIBGR.getDrawable(entity.AvatarMarker);
        imageView.setImageDrawable(icon);

        String[] entityTypeArray = getContext().getResources().getStringArray(R.array.entitytype_array);
        String entityTypeValue = "ic_" + entityTypeArray[entity.EntityType].replace(" ", "").toLowerCase();
        entityType.setImageResource(LIBGR.getDrawableId(getContext(), entityTypeValue));

        LIBUI.dV(imageWebsite, View.VISIBLE);
        LIBUI.dV(imageFacebook, View.VISIBLE);
        LIBUI.dV(imageTwitter, View.VISIBLE);
        LIBUI.dV(imageInstagram, View.VISIBLE);
        LIBUI.dV(imageYoutube, View.VISIBLE);
        LIBUI.dV(gotoButton, View.VISIBLE);
        LIBUI.dV(favouriteButton, View.VISIBLE);

        LIBUI.dV(imageWebsite, !LIBCM.isBlank(entity.Website));
        LIBUI.dV(imageFacebook, !LIBCM.isBlank(entity.Facebook));
        LIBUI.dV(imageTwitter, !LIBCM.isBlank(entity.Twitter));
        LIBUI.dV(imageInstagram, !LIBCM.isBlank(entity.Instagram));
        LIBUI.dV(imageYoutube, !LIBCM.isBlank(entity.YouTube));

        LIBUI.dVi(favouriteButton, !isMyEntity);
        LIBUI.dVi(myEntityMarker, isMyEntity);

        boolean isGoto = ((entity.Longitude != 0 && entity.LastMoved > XP_Library_CM.getOldestEntityAllowedToMove()) || (entity.Longitude != 0 && isMyEntity));
        boolean isSocialMedia = (imageFacebook.getVisibility() + imageTwitter.getVisibility() + imageInstagram.getVisibility() + imageYoutube.getVisibility() + imageWebsite.getVisibility()) == (View.GONE * 5);

        socialMedia.setVisibility(isSocialMedia ? View.GONE : View.VISIBLE);
        gotoButton.setImageResource(isGoto ? R.drawable.ic_goto : R.drawable.ic_goto_disabled);
        favouriteButton.setImageResource(entity.Favourite ? R.drawable.ic_favourite_on : R.drawable.ic_favourite_off);

        gotoButton.setTag(entity.EntityGuid + ":" + (isGoto ? "ic_goto" : "ic_goto_disabled"));
        favouriteButton.setTag(entity.EntityGuid);
    }
}