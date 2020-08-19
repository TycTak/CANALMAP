package com.tyctak.canalmap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._Poi;
import com.tyctak.map.entities._PoiLocation;
import com.tyctak.canalmap.libraries.Library_FS;
import com.tyctak.canalmap.libraries.Library_GR;
import com.tyctak.canalmap.libraries.Library_PE;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_SC;

import java.util.ArrayList;

public class Dialog_PoiEdit extends AppCompatActivity {

    private final String TAG = "Dialog_PoiEdit";
    final Activity activity = this;
    private String selected;
    private double[] points;
    private Uri imageUri;

    _PoiLocation poiLocation;
    private int poiId;

    final private int GALLERY_REQUEST = 1;
    int index = 0;
    final private int CAMERA_REQUEST = 2;
    final private int TEXT_REQUEST = 3;
    private static boolean forceCreate = false;

    final public static String MARKERS = "markers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_poiedit);

        poiId = getIntent().getIntExtra("PoiId", -1);

        final FrameLayout poiScroll = (FrameLayout) findViewById(R.id.poiscroll);
        ImageButton move_poi = (ImageButton) findViewById(R.id.move_poi);
        ImageView marker_selected = (ImageView) findViewById(R.id.marker_selected);

        if (poiId == -1) {
            poiLocation = new _PoiLocation();
            poiLocation.Area = getIntent().getStringExtra("Selected");
            double[] points = getIntent().getDoubleArrayExtra("GeoPoint");
            poiLocation.Latitude = points[1];
            poiLocation.Longitude = points[0];
            poiScroll.setVisibility(View.VISIBLE);
            move_poi.setVisibility(View.GONE);
            marker_selected.setOnClickListener(null);
            forceCreate = true;
        } else {
            poiLocation = Global.getInstance().getDb().getPoiLocation(poiId);
            poiScroll.setVisibility(View.GONE);
        }

        TextView poieditText = (TextView) findViewById(R.id.poieditText);
        poieditText.setText("Edit " + poiLocation.Area.substring(0, poiLocation.Area.length() - 1));

        Library lib = new Library();

        int id = this.getResources().getIdentifier(poiLocation.Name, "drawable", this.getPackageName());
        ImageView image = (ImageView) findViewById(R.id.marker_selected);
        Library_GR LIBGR = new Library_GR();
        image.setImageDrawable(new BitmapDrawable(LIBGR.getDrawableImage(MyApp.getContext(), id, 50, 50)));

        setMarkerImage(lib.decodeBinary(poiLocation.Image), poiLocation.Message);

        selected = poiLocation.Area;
        points = new double[] { poiLocation.Longitude, poiLocation.Latitude};

        final Library_UI LIBUI = new Library_UI();

        final ImageView markerImage = (ImageView) findViewById(R.id.marker_image);
        markerImage.setTag(poiLocation.Id);

        final TextView markerText = (TextView) findViewById(R.id.marker_text);

        final LinearLayout markerLayout = (LinearLayout) findViewById(R.id.marker_layout);
        final LinearLayout symbolLayout = (LinearLayout) findViewById(R.id.symbol_layout);

        Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int id = (int) getResources().getIdentifier(poiLocation.Name, "drawable", getPackageName());
                Library_GR LIBGR = new Library_GR();
                Bitmap marker = LIBGR.getDrawableImage(activity, id, 384, 384);
                if (marker != null) {
                    Bitmap image = null;

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    if (imageView != null) {
                        image = LIBGR.getBitmapFromImageView(imageView);
                    }

                    String text = markerText.getText().toString();

                    byte[] imageBinary = (image == null ? null : Library.encodeBinary(image));

                    _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                    poiLocation.Id = Global.getInstance().getDb().writePoiLocation(mySettings.IsAdministrator, poiLocation.Id, poiLocation.Area, poiLocation.Name, points, 0, forceCreate, Global.getInstance().getDb().getMyEntityGuid(), imageBinary, poiLocation.Action, text);

                    Intent intent = new Intent();
                    intent.putExtra("PoiId", poiLocation.Id);
                    setResult(Activity.RESULT_OK, intent);
                }

                finish();
            }
        };

        final _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        XP_Library_SC XPLIBSC = new XP_Library_SC();
        final boolean isPremium = XPLIBSC.isPremium(mySettings);

        if (selected.equals(MARKERS)) {
            markerLayout.setVisibility(View.VISIBLE);
            symbolLayout.setVisibility(View.GONE);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mySettings.IsAdministrator) {
                        ArrayList<_Poi> pois = Global.getInstance().getDb().getPois("markers", "Editors");
                        LinearLayout linearlayoutEditors = (LinearLayout) findViewById(R.id.linearlayout_editors);

                        if (pois.size() > 0) {
                            linearlayoutEditors.setVisibility(View.VISIBLE);
                            LIBUI.addPois(isPremium, "markers", points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("markers", "Editors"), (FlexboxLayout) findViewById(R.id.icon_flow_editors), false, runnable);
                        } else {
                            linearlayoutEditors.setVisibility(View.GONE);
                        }
                    }

                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPoisRecent("markers", Global.getInstance().getDb().getMyEntityGuid()), (FlexboxLayout) findViewById(R.id.symbol_select_markers_recent), false, runnable);
                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("markers", "Moorings"), (FlexboxLayout) findViewById(R.id.symbol_select_moorings), false, runnable);
                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("markers", "Activities"), (FlexboxLayout) findViewById(R.id.icon_flow_activities), false, runnable);
                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("markers", "Routes"), (FlexboxLayout) findViewById(R.id.icon_flow_routes), false, runnable);
                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("markers", "Area of Interest"), (FlexboxLayout) findViewById(R.id.icon_flow_areainterest), false, runnable);
                }
            });
        } else {
            markerLayout.setVisibility(View.GONE);
            symbolLayout.setVisibility(View.VISIBLE);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    LIBUI.addPois(isPremium, "symbols", points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPoisRecent("symbols", Global.getInstance().getDb().getMyEntityGuid()), (FlexboxLayout)findViewById(R.id.symbol_select_symbols_recent), false, runnable);
                    LIBUI.addPois(isPremium, "symbols", points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("symbols", "Services"), (FlexboxLayout)findViewById(R.id.symbol_select_services), false, runnable);
                    LIBUI.addPois(isPremium, "symbols", points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("symbols", "Food & Drink"), (FlexboxLayout)findViewById(R.id.symbol_select_fooddrink), false, runnable);
                    LIBUI.addPois(isPremium, "symbols", points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("symbols", "Travel"), (FlexboxLayout)findViewById(R.id.symbol_select_travel), false, runnable);
                    LIBUI.addPois(isPremium, "symbols", points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("symbols", "Essentials"), (FlexboxLayout)findViewById(R.id.symbol_select_essentials), false, runnable);
                    LIBUI.addPois(isPremium, "symbols", points, markerText, markerImage, index, poiId, activity, Global.getInstance().getDb().getPois("symbols", "Entertainment"), (FlexboxLayout)findViewById(R.id.symbol_select_entertainment), false, runnable);
                }
            });
        }
    }

    private void setMarkerImage(Bitmap image, String text) {
        boolean isImage = (image != null);
        boolean isText = (text != null && !text.isEmpty());

        TextView pleaseusetoolbar = (TextView) findViewById(R.id.pleaseusetoolbar_layout);
        pleaseusetoolbar.setVisibility((isImage || isText ? View.GONE : View.VISIBLE));

        LinearLayout text_or_image = (LinearLayout) findViewById(R.id.text_or_image);
        if (isImage || isText) {
            text_or_image.setVisibility(View.VISIBLE);
        } else {
            text_or_image.setVisibility(View.GONE);
        }

        LinearLayout deleteimagelayout = (LinearLayout) findViewById(R.id.deleteimage_layout);
        LinearLayout deletetextlayout = (LinearLayout) findViewById(R.id.deletetext_layout);

        TextView markerText = (TextView) findViewById(R.id.marker_text);
        ImageView markerImage = (ImageView) findViewById(R.id.marker_image);

        if (isImage) {
            deleteimagelayout.setVisibility(View.VISIBLE);
            markerImage.setVisibility(View.VISIBLE);
            markerImage.setImageBitmap(image);
        } else {
            deleteimagelayout.setVisibility(View.GONE);
            markerImage.setVisibility(View.GONE);
            markerImage.setImageBitmap(null);
        }

        if (isText) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)markerText.getLayoutParams();

            if (image == null) {
                params.setMargins(0, 0, 0, 0);
            } else {
                params.setMargins(0, 8, 0, 0);
            }

            markerText.setLayoutParams(params);

            deletetextlayout.setVisibility(View.VISIBLE);
            markerText.setVisibility(View.VISIBLE);
            markerText.setText(text);
        } else {
            deletetextlayout.setVisibility(View.GONE);
            markerText.setVisibility(View.GONE);
            markerText.setText(null);
        }

        LinearLayout.LayoutParams paramsText = (LinearLayout.LayoutParams) markerText.getLayoutParams();
        LinearLayout.LayoutParams paramsImage = (LinearLayout.LayoutParams) markerImage.getLayoutParams();

        if (isImage && isText) {
            paramsImage.weight = 3;
            paramsText.weight = 2;
        } else if (isImage && !isText) {
            paramsImage.weight = 5;
            paramsText.weight = 0;
        } else {
            paramsImage.weight = 0;
            paramsText.weight = 5;
        }

        markerText.setLayoutParams(paramsText);
        markerImage.setLayoutParams(paramsImage);
    }

    public void deleteMarker(View view) {
        Library_UI LIBUI = new Library_UI();

        if (selected.equals(MARKERS)) {
            LIBUI.confirmationDialogYesNo(getString(R.string.confirm_deletemarker_title), getString(R.string.confirm_deletemarker), new Runnable() {
                @Override
                public void run() {
                    deleteMarker();
                }
            }, this);
        } else {
            LIBUI.confirmationDialogYesNo(getString(R.string.confirm_deletesymbol_title), getString(R.string.confirm_deletesymbol), new Runnable() {
                @Override
                public void run() {
                    deleteMarker();
                }
            }, this);
        }
    }

    private void deleteMarker() {
        Global.getInstance().getDb().deletePoiLocation(poiLocation.Id, poiLocation.Shared);
        poiLocation = Global.getInstance().getDb().getPoiLocation(poiLocation.Id);

        Intent intent = new Intent();
        intent.putExtra("PoiId", poiLocation.Id);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void attachGallery(View view) {
        Intent intent = new Intent(activity, Activity_Permissions.class);
        intent.putExtra("permissions", new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE});
        intent.putExtra("force", true);
        intent.putExtra("action", "gallery");

        startActivityForResult(intent, Library_PE.GET_PERMISSIONS);
    }

    public void attachText(View view) {
        TextView markerText = (TextView) findViewById(R.id.marker_text);

        Intent intent = new Intent(activity, Z_Activity_EditText.class);
        intent.putExtra("description", markerText.getText().toString());
        intent.putExtra("poiid", poiLocation.Id);

        startActivityForResult(intent, TEXT_REQUEST);
    }

    public void attachCamera(View view) {
        Intent intent = new Intent(activity, Activity_Permissions.class);
        intent.putExtra("permissions", new String[] {Manifest.permission.CAMERA});
        intent.putExtra("force", true);
        intent.putExtra("action", "camera");

        startActivityForResult(intent, Library_PE.GET_PERMISSIONS);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == Library_PE.GET_PERMISSIONS) {
            final String type = data.getStringExtra("action");

            Library_PE LIBPE = new Library_PE();
            LIBPE.handleResult(activity, new Runnable() {
                @Override
                public void run() {
                    if (type == "camera") {
                        Library_FS LIBFS = new Library_FS();
                        imageUri = LIBFS.createTemporaryFile("picture", ".jpg");

                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, -1);

                        startActivityForResult(intent, CAMERA_REQUEST);
                    } else {
                        Library_FS LIBFS = new Library_FS();
                        imageUri = LIBFS.createTemporaryFile("picture", ".jpg");

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, -1);

                        startActivityForResult(intent, GALLERY_REQUEST);
                    }
                }
            }, requestCode, resultCode, data);
        } else if (requestCode == TEXT_REQUEST) {
            Library_GR LIBGR = new Library_GR();
            String text = data.getExtras().get("text").toString();

            if (text != null && !text.isEmpty()) {
                ImageView markerImageView = (ImageView) findViewById(R.id.marker_image);
                Bitmap markerImage = LIBGR.getBitmapFromImageView(markerImageView);

                Bitmap marker = LIBGR.getDrawableImage(this, poiLocation.Name, 384, 384);

                byte[] imageBinary = (markerImage == null ? null : Library.encodeBinary(markerImage));

                _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                int tempPoiId = Global.getInstance().getDb().writePoiLocation(mySettings.IsAdministrator, poiLocation.Id, "markers", poiLocation.Name, points, 0, forceCreate, Global.getInstance().getDb().getMyEntityGuid(), imageBinary, null, text);
                if (poiLocation.Id == -1) {
                    poiLocation.Id = tempPoiId;
                }

                poiLocation.IsMessage = true;
                poiLocation.Message = text;

                Intent intent = new Intent();
                intent.putExtra("PoiId", poiLocation.Id);
                activity.setResult(Activity.RESULT_OK, intent);

                setMarkerImage(markerImage, text);
            } else {
                deleteText();
            }
        } else if (requestCode == CAMERA_REQUEST || requestCode == GALLERY_REQUEST) {
            Bitmap bitmap;
            Library_GR LIBGR = new Library_GR();

            if (requestCode == CAMERA_REQUEST) {
                bitmap = LIBGR.grabImage(this, imageUri);
            } else {
                Uri contentUri = data.getData();
                bitmap = LIBGR.grabImage(this, contentUri);
            }

            if (bitmap != null) {
                TextView markerText = (TextView) findViewById(R.id.marker_text);
                Bitmap marker = null;

                Bitmap resizedImage = LIBGR.getResizedBitmap(bitmap, 512);

                resizedImage = LIBGR.rotateImage(resizedImage, 90);

                byte[] imageBinary = (resizedImage == null ? null : Library.encodeBinary(resizedImage));

                _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                int tempPoiId = Global.getInstance().getDb().writePoiLocation(mySettings.IsAdministrator, poiLocation.Id, "markers", poiLocation.Name, points, 0, forceCreate, Global.getInstance().getDb().getMyEntityGuid(), imageBinary, null, markerText.getText().toString());

                if (poiLocation.Id == -1) {
                    poiLocation.Id = tempPoiId;
                }

                Library lib = new Library();
                poiLocation.IsImage = true;
                poiLocation.Image = lib.encodeBinary(resizedImage);

                Intent intent = new Intent();
                intent.putExtra("PoiId", poiLocation.Id);
                activity.setResult(Activity.RESULT_OK, intent);

                setMarkerImage(resizedImage, markerText.getText().toString());
            } else {
                deleteImage();
            }
        }
    }

    public void showFullPhoto(View view) {
        ImageView markerImage = (ImageView) findViewById(R.id.marker_image);

        Library_GR LIBGR = new Library_GR();

        Intent intent = new Intent(activity, Z_Activity_FullScreenImage.class);
        if (poiId == -1) {
            intent.putExtra("Image", LIBGR.getByteArrayFromImageView(markerImage));
        } else {
            intent.putExtra("PoiId", poiId);
        }

        startActivity(intent);
    }

    public void showFullText(View view) {
        Intent intent = new Intent(activity, Z_Activity_FullScreenText.class);
        intent.putExtra("text", getMessage());
        startActivity(intent);
    }

    public void deleteImage(View view) {
        Library_UI LIBUI = new Library_UI();
        LIBUI.confirmationDialogYesNo(getString(R.string.confirm_deleteimage_title), getString(R.string.confirm_deleteimage), new Runnable() {
            @Override
            public void run() {
                deleteImage();
            }
        }, this);
    }

    private void deleteImage() {
        Global.getInstance().getDb().writePoiLocationImage(poiLocation.Id, null);

        poiLocation.IsImage = false;
        setMarkerImage(null, getMessage());

        Intent intent = new Intent();
        intent.putExtra("PoiId", poiLocation.Id);
        setResult(Activity.RESULT_OK, intent);
    }

    public void deleteText(View view) {
        Library_UI LIBUI = new Library_UI();
        LIBUI.confirmationDialogYesNo(getString(R.string.confirm_deletetext_title), getString(R.string.confirm_deletetext), new Runnable() {
            @Override
            public void run() {
                deleteText();
            }
        }, this);
    }

    private void deleteText() {
        Global.getInstance().getDb().writePoiLocationText(poiLocation.Id, null);

        poiLocation.IsMessage = false;
        setMarkerImage(getImage(), null);

        Intent intent = new Intent();
        intent.putExtra("PoiId", poiLocation.Id);
        setResult(Activity.RESULT_OK, intent);
    }

    public void rotateCurrentImage(View view) {
        Library_GR LIBGR = new Library_GR();
        Library lib = new Library();
        Bitmap bitmap = LIBGR.rotateImage(getImage(), 90);

        Library LIB = new Library();
        byte[] imageBinary = (bitmap == null ? null : LIB.encodeBinary(bitmap));

        Global.getInstance().getDb().writePoiLocationImage(poiLocation.Id, imageBinary);

        poiLocation = Global.getInstance().getDb().getPoiLocation(poiLocation.Id);
        setMarkerImage(lib.decodeBinary(poiLocation.Image), poiLocation.Message);
    }

    private Bitmap getImage() {
        ImageView markerImage = (ImageView) findViewById(R.id.marker_image);
        Library_GR LIBGR = new Library_GR();
        return LIBGR.getBitmapFromImageView(markerImage);
    }

    private String getMessage() {
        TextView markerText = (TextView) findViewById(R.id.marker_text);
        return markerText.getText().toString();
    }

    public void changeIcon(View view) {
        Intent intent = new Intent();

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (poiLocation.EntityGuid.equals(Global.getInstance().getDb().getMyEntityGuid()) || mySettings.IsAdministrator) {
            intent.putExtra("PoiId", poiLocation.Id);
            intent.putExtra("Action", "changeicon");
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    public void movePoi(View view) {
        Intent intent = new Intent();
        intent.putExtra("PoiId", poiLocation.Id);
        intent.putExtra("Action", "moveicon");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void closeMarkers(View view) {
        finish();
    }
}
