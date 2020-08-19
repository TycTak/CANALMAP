package com.tyctak.canalmap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tyctak.map.entities._Entity;
import com.tyctak.canalmap.libraries.Library_GR;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;

import java.util.ArrayList;

public class ArrayAdapter_Entities extends ArrayAdapter<_Entity> {

    final private static String TAG = "ArrayAdapter_Entities";

    public ArrayAdapter_Entities(Context context, ArrayList<_Entity> entities) {
        super(context, 0, entities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        _Entity entity = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.arrayadapter_entities, parent, false);
        }

        boolean isMyEntity = entity.EntityGuid.equals(Global.getInstance().getDb().getMyEntityGuid());

        Library_GR LIBGR = new Library_GR();
        Library_UI LIBUI = new Library_UI();

        TextView description = (TextView) convertView.findViewById(R.id.entityDescription);
        TextView name = (TextView) convertView.findViewById(R.id.entityName);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.entityMarker);
        ImageView imageWebsite = (ImageView) convertView.findViewById(R.id.imageWebsite);
        ImageView imageFacebook = (ImageView) convertView.findViewById(R.id.imageFacebook);
        ImageView imageTwitter = (ImageView) convertView.findViewById(R.id.imageTwitter);
        ImageView imageInstagram = (ImageView) convertView.findViewById(R.id.imageInstagram);
        ImageView imageYoutube = (ImageView) convertView.findViewById(R.id.imageYouTube);
        ImageButton gotoButton = (ImageButton) convertView.findViewById(R.id.buttonGoTo);
        ImageButton favouriteButton = (ImageButton) convertView.findViewById(R.id.buttonFavourite);
        LinearLayout socialMedia = (LinearLayout) convertView.findViewById(R.id.socialMedia);
        LinearLayout myEntityMarker = (LinearLayout) convertView.findViewById(R.id.myEntityMarker);
        ImageView entityType = (ImageView) convertView.findViewById(R.id.entityType);

        XP_Library_CM LIBCM = new XP_Library_CM();
        name.setText(LIBCM.isBlank(entity.EntityName) ? MyApp.getContext().getString(R.string.noName) : entity.EntityName);
        description.setText(LIBCM.isBlank(entity.Description) ? MyApp.getContext().getString(R.string.noDescription) : entity.Description);

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

        return convertView;
    }
}