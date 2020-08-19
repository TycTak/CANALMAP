package com.tyctak.canalmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._Item;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._PoiLocation;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.canalmap.libraries.Library_GR;
import com.tyctak.map.libraries.XP_Library_SC;

//import org.jsoup.helper.StringUtil;

import java.util.ArrayList;

import static com.tyctak.canalmap.Dialog_Published.position;

public class ArrayAdapter_Items extends ArrayAdapter<_Item> {

    final private String TAG = "ArrayAdapter_Items";
    final Library_GR LIBGR = new Library_GR();

    public ArrayAdapter_Items(Context context, ArrayList<_Item> items) {
        super(context, 0, items);
    }

    private void setMarkerImage(int position, ImageView selectedImage, String titleText, TextView title, View convertView, boolean isEdit, boolean isImage, boolean isMessage, Bitmap image, String text) {
        TextView markerText = (TextView) convertView.findViewById(R.id.marker_text);
        ImageView markerImage = (ImageView) convertView.findViewById(R.id.marker_image);
        LinearLayout.LayoutParams paramsSelectedImage = (LinearLayout.LayoutParams) selectedImage.getLayoutParams();
        LinearLayout.LayoutParams paramsTitle = (LinearLayout.LayoutParams) title.getLayoutParams();
        LinearLayout.LayoutParams paramsText = (LinearLayout.LayoutParams) markerText.getLayoutParams();
        LinearLayout.LayoutParams paramsImage = (LinearLayout.LayoutParams) markerImage.getLayoutParams();

        markerImage.setTag(position);
        markerText.setTag(position);

        if (isImage) {
            markerImage.setVisibility(View.VISIBLE);
            markerImage.setImageBitmap(image);
        } else {
            markerImage.setVisibility(View.GONE);
            markerImage.setImageBitmap(null);
        }

        if (isMessage) {
            markerText.setVisibility(View.VISIBLE);
            markerText.setText(text);
        } else {
            markerText.setVisibility(View.GONE);
            markerText.setText(null);
        }

        if (isImage && isMessage) {
            paramsImage.weight = 3;
            paramsText.weight = 2;
        } else if (isImage && !isMessage) {
            paramsImage.weight = 5;
            paramsText.weight = 0;
        } else {
            paramsImage.weight = 0;
            paramsText.weight = 5;
        }

        if (isImage || isMessage) {
            if (isEdit) {
                XP_Library_CM LIBCM = new XP_Library_CM();
                if (isImage && LIBCM.isBlank(titleText)) {
                    paramsImage.topMargin = 25;
                    paramsText.topMargin = 25;
                    paramsTitle.topMargin = 12;
                } else {
                    paramsImage.topMargin = 13;
                    paramsText.topMargin = 10;
                    paramsTitle.topMargin = 12;
                }
            } else {
                if (isImage) {
                    paramsImage.topMargin = 20;
                    paramsText.topMargin = 20;
                    paramsTitle.topMargin = 11;
                } else {
                    paramsText.topMargin = 12;
                    paramsTitle.topMargin = 11;
                }
            }
        } else {
            if (isEdit) {
                paramsTitle.topMargin = 14;
                paramsTitle.bottomMargin = -3;
            } else {
                paramsTitle.topMargin = 10;
                paramsTitle.bottomMargin = 9;
            }
        }

        selectedImage.setLayoutParams(paramsSelectedImage);
        markerText.setLayoutParams(paramsText);
        markerImage.setLayoutParams(paramsImage);
        title.setLayoutParams(paramsTitle);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        _Item item = getItem(position);

        _Entity entity = item.Entity;
        _PoiLocation poi = item.PoiLocation;

        if (poi != null) {
            Log.d(TAG,"singleTapConfirmedHelper END #4 " + poi.Name);
            convertView = getPoiLocationView(convertView, position, poi, parent);
        } else {
            convertView = getEntityView(convertView, position, entity, parent);
        }

        return convertView;
    }

    private View getEntityView(View view, int position, _Entity entity, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.arrayadapter_item_entities, parent, false);

        Library_GR LIBGR = new Library_GR();
        Library_UI LIBUI = new Library_UI();

        TextView name = (TextView) view.findViewById(R.id.entityName);
        TextView description = (TextView) view.findViewById(R.id.entityDescription);
        ImageView imageWebsite = (ImageView) view.findViewById(R.id.imageWebsite);
        ImageView imageFacebook = (ImageView) view.findViewById(R.id.imageFacebook);
        ImageView imageTwitter = (ImageView) view.findViewById(R.id.imageTwitter);
        ImageView imageInstagram = (ImageView) view.findViewById(R.id.imageInstagram);
        ImageView imageYoutube = (ImageView) view.findViewById(R.id.imageYouTube);
        ImageView entityType = (ImageView) view.findViewById(R.id.entityType);
        ImageView imageView = (ImageView) view.findViewById(R.id.entityMarker);
        ImageButton favouriteButton = (ImageButton) view.findViewById(R.id.buttonFavourite);
        LinearLayout socialMedia = (LinearLayout) view.findViewById(R.id.socialMedia);
        LinearLayout entityItem = (LinearLayout) view.findViewById(R.id.entityItem);

        XP_Library_CM LIBCM = new XP_Library_CM();
        name.setText(LIBCM.isBlank(entity.EntityName) ? MyApp.getContext().getString(R.string.noName) : entity.EntityName);
        description.setText(LIBCM.isBlank(entity.Description) ? MyApp.getContext().getString(R.string.noDescription) : entity.Description);

        Drawable icon = LIBGR.getDrawable(entity.AvatarMarker);
        imageView.setImageDrawable(icon);

        String[] entityTypeArray = getContext().getResources().getStringArray(R.array.entitytype_array);
        String entityTypeValue = "ic_item_" + entityTypeArray[entity.EntityType].replace(" ", "").toLowerCase();
        entityType.setImageResource(LIBGR.getDrawableId(getContext(), entityTypeValue));

        favouriteButton.setTag(entity.EntityGuid);

        LIBUI.dV(imageWebsite, View.VISIBLE);
        LIBUI.dV(imageFacebook, View.VISIBLE);
        LIBUI.dV(imageTwitter, View.VISIBLE);
        LIBUI.dV(imageInstagram, View.VISIBLE);
        LIBUI.dV(imageYoutube, View.VISIBLE);
        LIBUI.dV(favouriteButton, View.VISIBLE);

        LIBUI.dV(imageWebsite, !LIBCM.isBlank(entity.Website));
        LIBUI.dV(imageFacebook, !LIBCM.isBlank(entity.Facebook));
        LIBUI.dV(imageTwitter, !LIBCM.isBlank(entity.Twitter));
        LIBUI.dV(imageInstagram, !LIBCM.isBlank(entity.Instagram));
        LIBUI.dV(imageYoutube, !LIBCM.isBlank(entity.YouTube));

        boolean isSocialMedia = (imageFacebook.getVisibility() + imageTwitter.getVisibility() + imageInstagram.getVisibility() + imageYoutube.getVisibility() + imageWebsite.getVisibility()) == (View.GONE * 5);

        socialMedia.setVisibility(isSocialMedia ? View.GONE : View.VISIBLE);
        favouriteButton.setImageResource(entity.Favourite ? R.drawable.ic_favourite_on : R.drawable.ic_favourite_off);

        entityItem.setTag(position);

        return view;
    }

    private View getPoiLocationView(View view, int position, _PoiLocation poi, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.arrayadapter_item_poilocations, parent, false);

        LinearLayout publisher_layout = (LinearLayout) view.findViewById(R.id.published_layout);
        ImageView selectedImage = (ImageView) view.findViewById(R.id.marker_selected);
        ImageButton edit_poi = (ImageButton) view.findViewById(R.id.edit_poi);
        ImageButton delete_poi = (ImageButton) view.findViewById(R.id.delete_poi);

        XP_Library_SC XPLIBSC = new XP_Library_SC();
        boolean isMyPoi = XPLIBSC.getMyPoi(poi.EntityGuid);
        boolean isEdit = XPLIBSC.isEdit(poi.EntityGuid, poi.Category, poi.Longitude, poi.Latitude, isMyPoi);

        publisher_layout.setVisibility(View.VISIBLE);

        TextView title;

//        if (isEdit) {
//            view.findViewById(R.id.title2).setVisibility(View.GONE);
//            title = (TextView) view.findViewById(R.id.title1);
//
//            if (poi.Title == null || poi.Title.isEmpty()) {
//                title.setVisibility(View.GONE);
//            } else {
//                title.setVisibility(View.VISIBLE);
//            }
//        } else {
//            view.findViewById(R.id.title1).setVisibility(View.GONE);
//            title = (TextView) view.findViewById(R.id.title2);
//            title.setVisibility(View.VISIBLE);
//        }

        publisher_layout.setTag(position);
        selectedImage.setTag(position);

//        if (isEdit) {
//            edit_poi.setTag(position);
//            edit_poi.setVisibility(View.VISIBLE);
//        } else {
//            edit_poi.setVisibility(View.GONE);
//        }

        if (isEdit) {
            view.findViewById(R.id.title2).setVisibility(View.GONE);
            title = (TextView) view.findViewById(R.id.title1);

            if (poi.Title == null || poi.Title.isEmpty()) {
                title.setVisibility(View.GONE);
            } else {
                title.setVisibility(View.VISIBLE);
            }

            edit_poi.setTag(position);
            edit_poi.setVisibility(View.VISIBLE);

            _MySettings mySettings = Global.getInstance().getDb().getMySettings();

            if (!mySettings.IsAdministrator && mySettings.IsReviewer & !isMyPoi) {
                delete_poi.setVisibility(View.GONE);
            } else {
                delete_poi.setTag(position);
                delete_poi.setVisibility(View.VISIBLE);
            }
        } else {
            edit_poi.setVisibility(View.GONE);
            delete_poi.setVisibility(View.GONE);

            view.findViewById(R.id.title1).setVisibility(View.GONE);
            title = (TextView) view.findViewById(R.id.title2);
            title.setVisibility(View.VISIBLE);
        }

        title.setText(poi.Title);

        Library lib = new Library();
        setMarkerImage(position, selectedImage, poi.Title, title, view, isEdit, poi.IsImage, poi.IsMessage, lib.decodeBinary(poi.Image), poi.Message);

        UpdateSharedMarker(view, poi.ReviewedFeedback, poi.Shared, poi.EntityGuid, isEdit, isMyPoi);

        String poiName = poi.Name;

        if (poiName == null || poiName.startsWith("ic_default_")) poiName = "ic_missing";

        selectedImage.setImageDrawable(new BitmapDrawable(LIBGR.getDrawableImage(MyApp.getContext(), poiName, 100, 100)));

        return view;
    }

    public static void UpdateSharedMarker(View view, _PoiLocation.enmReviewedFeedback reviewedFeedback, int shared, String ownerEntityGuid, boolean isEdit, boolean isMyPoi) {
        LinearLayout review_layout = (LinearLayout) view.findViewById(R.id.review);
        TextView publishedText = (TextView) view.findViewById(R.id.sharedText);
        TextView publishedTextAction = (TextView) view.findViewById(R.id.sharedTextAction);

//        _MySettings mySettings = Bootstrap.getInstance().getDatabase().getMySettings();

        if (shared == 1 && Global.getInstance().getMyPoi(ownerEntityGuid)) {
            ImageView exclamation = (ImageView) view.findViewById(R.id.exclamation);
            exclamation.setVisibility(reviewedFeedback.ordinal() > _PoiLocation.enmReviewedFeedback.NoFeedback.ordinal() ? View.VISIBLE : View.GONE);

            TextView reviewedFeedbackTextView = (TextView) view.findViewById(R.id.reviewedFeedback);
            String[] reviewedFeedbackArray = MyApp.getContext().getResources().getStringArray(R.array.reviewed_feedback);
            reviewedFeedbackTextView.setText(reviewedFeedbackArray[reviewedFeedback.ordinal()]);

            if (reviewedFeedback == _PoiLocation.enmReviewedFeedback.NotReviewed) {
                reviewedFeedbackTextView.setTextColor(R.color.colorReviewWaiting);
            } else {
                reviewedFeedbackTextView.setTextColor(R.color.colorReviewWarning);
            }

//            review_layout.setVisibility(View.VISIBLE);
            review_layout.setTag(position);
        } else {
            review_layout.setVisibility(View.GONE);
        }

        if (isEdit && isMyPoi) {
            publishedText.setText(shared == 0 || shared == -1 ? "PRIVATE > " : "PUBLIC > ");
            publishedTextAction.setText(shared == 0 || shared == -1 ? "click to make public..." : "click to make private...");
        } else {
            publishedText.setText("SHARED");
            publishedTextAction.setText("");
        }

//        if (!isEdit && shared == 1 && !mySettings.IsPublisher && mySettings.IsPremium) {
//            publishedText.setText("Edit this...");
//        } else if (!isEdit && shared == 1) {
//            publishedText.setText("Shared");
//        } else if (isEdit && mySettings.IsPremium) {
//            publishedText.setText(shared == 0 || shared == -1 ? "Private - click to share" : "Public");
//        } else {
//            publishedText.setText("Private");
//        }
    }
}