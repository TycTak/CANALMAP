package com.tyctak.canalmap.libraries;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.flexbox.FlexboxLayout;
import com.tyctak.canalmap.Global;
import com.tyctak.canalmap.Library;
import com.tyctak.canalmap.MyApp;
import com.tyctak.canalmap.R;
import com.tyctak.map.entities._Association;
import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._Poi;
import com.tyctak.map.libraries.XP_Library_CM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.graphics.Bitmap.createBitmap;

//import org.jsoup.helper.StringUtil;

public class Library_GR {

    private final String TAG = "Library_GR";
    private final int POI_WIDTH = 300;

    public final static String DEFAULT_ICON = "ic_icon__marker";

    private static HashMap<String, _Association>[] assocSmall = null;
//    private static ArrayList<_Association>[] assocMedium = null;
//    private static ArrayList<_Association>[] assocLarge = null;

//    public boolean getIsLevelChanged(int lastZoomLevel, int currentZoomLevel) {
//        int lastZoomLevelSize = getZoomDimension(lastZoomLevel);
//        int currentZoomLevelSize = getZoomDimension(currentZoomLevel);
//        return (lastZoomLevelSize != currentZoomLevelSize);
//    }

    //Global.getInstance().getDensityMultiplier()
    private int getZoomDimension(Double densityMultiplier, int zoomLevel) {
        int retval;

        _Association.enmSize size;

        if (zoomLevel <= 11) {
            size = _Association.enmSize.small;
        } else if (zoomLevel <= 14) {
            size = _Association.enmSize.medium;
        } else {
            size = _Association.enmSize.large;
        }

        if (size == _Association.enmSize.small) {
            retval = (int) (55 * densityMultiplier);
        } else if (size == _Association.enmSize.medium) {
            retval = (int) (70 * densityMultiplier);
        } else {
            retval = (int) (85 * densityMultiplier);
        }

        return retval;
    }

    public void initialiseGraphics(Context context) {
        if (assocSmall == null) {
            assocSmall = new HashMap[4];
            assocSmall[_Association.enmType.IsNone.ordinal()] = Global.getInstance().getDb().getPois("area IN ('symbols', 'markers')");
            assocSmall[_Association.enmType.IsNone.ordinal()].putAll(Global.getInstance().getDb().getPois("area = 'system'"));
        }
    }

    public Drawable getMarker(String name, int zoomLevel) {
        Drawable retval = null;

        _Association.enmType type = _Association.enmType.IsNone;
//        _Association.enmType type = (!isImage && !isText ? _Association.enmType.IsNone : _Association.enmType.IsBoth);
//        _Association.enmType type = (!isImage && !isText ? _Association.enmType.IsNone : (isImage && isText ? _Association.enmType.IsBoth : (isImage && !isText ? _Association.enmType.IsImage : _Association.enmType.IsText)));

        HashMap<String, _Association> associations;
//        _Association.enmSize size = getSize(zoomLevel);

//        if (size == _Association.enmSize.small) {
            associations = assocSmall[type.ordinal()];
//        } else if (size == _Association.enmSize.medium) {
//            associations = assocMedium;
//        } else {
//            associations = assocLarge;
//        }

//        for (HashMap.SimpleEntry<String, _Association> association : associations[type.ordinal()]) {
        if (associations.containsKey(name)) {
            _Association association = associations.get(name);

            if (association.Marker == null) {
                VectorDrawableCompat bitmapDrawable = null;

                int referenceId = getDrawableId(MyApp.getContext(), association.Name);

                try {
//                    bitmapDrawable = null;
                    bitmapDrawable = (VectorDrawableCompat) VectorDrawableCompat.create(MyApp.getContext().getResources(), referenceId, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                association.Marker = bitmapDrawable; //association.Marker;
            }

            retval = (Drawable) association.Marker;
        }

        return retval;
    }

//    private HashMap<String, _Association> getAssociations(String where) {
////        int dimension = getZoomDimension(zoomLevel);
//
//        HashMap<String, _Association> associations = Global.getInstance().getDb().getPois(where);
//
////        for (_Association association : associations) {
////            try {
////
////
//////            association.ReferenceId = getDrawableId(context, association.Name);
////
//////            Bitmap bitmap = getDrawableImage(context, association.ReferenceId, dimension, dimension);
//////            Drawable marker = new BitmapDrawable(bitmap);
////
//////            VectorDrawableCompat bitmapDrawable = null;
//////
//////            try {
//////                bitmapDrawable = (VectorDrawableCompat) VectorDrawableCompat.create(context.getResources(), association.ReferenceId, null);
//////            } catch (Exception ex) {
//////                ex.printStackTrace();
//////            }
////
//////            bitmapDrawable.setBounds(0, 0, dimension * 2, dimension * 2);
////
//////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//////                VectorDrawable vectorDrawable =  (VectorDrawable) VectorDrawable;
//////            } else {
//////
//////            }
////
//////            ScaleDrawable marker = bitmapDrawable;
//////            new ScaleDrawable(bitmapDrawable, 50, dimension, dimension);
////
//////            Drawable isImageMarker = new BitmapDrawable(getDrawableImage(context, R.drawable.ic_system_image, dimension, dimension));
//////            Drawable isTextMarker = new BitmapDrawable(getDrawableImage(context, R.drawable.ic_system_text, dimension, dimension));
//////            Drawable isBoth = new BitmapDrawable(getDrawableImage(context, R.drawable.ic_system_both, dimension, dimension));
////
//////            if (!isImage && !isText) {
//////            association.Marker = bitmapDrawable;
//////            } else if (isImage && !isText) {
//////                association.Marker = new LayerDrawable(new Drawable[] {marker, isImageMarker});
//////            } else if (!isImage && isText) {
//////                association.Marker = new LayerDrawable(new Drawable[] {marker, isTextMarker});
//////            } else {
//////                association.Marker = new LayerDrawable(new Drawable[] {marker, isBoth});
//////            }
////            } catch (Exception ex1) {
////                ex1.printStackTrace();
////            }
////        }
//
//        return associations;
//    }

    public byte[] getByteArrayFromImageView(ImageView imageView)
    {
        Bitmap bitmap = getBitmapFromImageView(imageView);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    public Bitmap getIconFiltered(Context context, _Poi poi, boolean toggle) {
        int id = (int) context.getResources().getIdentifier(poi.Name, "drawable", context.getPackageName());
        Bitmap bitmap = null;

        try {
            bitmap = getDrawableImage(context, id, 100, 100);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (toggle) {
            poi.Filter = !poi.Filter;
            Global.getInstance().getDb().writeMarkerSymbol(poi.Name, poi.Filter);
        }

        if (!poi.Filter) {
//            if (toggle) LIBUI.snackBar((Activity) context, "That POI will NOT be shown on the map");
            bitmap = adjustOpacity(bitmap, 110);
        } else {
//            if (toggle) LIBUI.snackBar((Activity) context, "That POI will be shown on the map");
            try {
                bitmap = adjustOpacity(bitmap, 255);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return bitmap;
    }

    public Bitmap getBitmapFromImageView(ImageView imageView)
    {
        BitmapDrawable bitmapDrawable = ((BitmapDrawable)imageView.getDrawable());
        Bitmap bitmap;

        if(bitmapDrawable == null){
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            imageView.buildDrawingCache(false);
        }else
        {
            bitmap = bitmapDrawable.getBitmap();
        }

        return bitmap;
    }

    public ArrayList<ImageView> getImages(Activity parent, ArrayList<String> imageNames, Integer width, Integer height, Integer leftMargin, Integer rightMargin, Integer topMargin, Integer bottomMargin, View.OnClickListener onClick) {
        ArrayList<ImageView> retval = new ArrayList<ImageView>();

        for (String item : imageNames) {
            retval.add(getImage(parent, item, width, height, leftMargin, rightMargin, topMargin, bottomMargin, onClick));
        }

        return retval;
    }

    public ImageView getImage(Activity parent, String imageName, Integer width, Integer height, Integer leftMargin, Integer rightMargin, Integer topMargin, Integer bottomMargin, View.OnClickListener onClick) {
        ImageView retval = new ImageView(parent);
        retval.setId(9999);

        Integer iconId = (Integer) parent.getResources().getIdentifier(imageName, "drawable", parent.getPackageName());
//        retval.setImageDrawable(getDrawable(parent, iconId).);/
        retval.setImageResource(iconId);
        retval.setTag(imageName);

        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(width, height);
        lp.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        retval.setLayoutParams(lp);

        retval.setOnClickListener(onClick);

        return retval;
    }

    public Bitmap getAvatarMarker(Context context, Integer id, String name, Bitmap avatar, Boolean isHirer) {
        int tempId = 0;
        XP_Library_CM LIBCM = new XP_Library_CM();

        if (LIBCM.isBlank(name)) {
            tempId = id;
        } else {
            tempId = getDrawableId(context, name);
        }

        @SuppressLint("RestrictedApi")
        Drawable icon = AppCompatDrawableManager.get().getDrawable(context, tempId);
        Boolean isAvatar = (name.contains(DEFAULT_ICON));

        final Integer maxWidth = 100;
        final Integer maxWidthAvatar = 100;

        Double multiplier = ((double)maxWidth / icon.getIntrinsicWidth());
        Integer height = (int)(icon.getMinimumHeight() * multiplier);

        Bitmap bitmap = createBitmap(maxWidth, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int padding = (maxWidth - maxWidthAvatar) / 2;

        if (isAvatar && avatar != null) {
            Bitmap bitmapAvatar = Bitmap.createScaledBitmap(avatar, maxWidthAvatar, (int)(maxWidthAvatar), true);
            bitmapAvatar = getRoundedShape(bitmapAvatar, bitmapAvatar.getWidth(), bitmapAvatar.getHeight());
            canvas.drawBitmap(bitmapAvatar, padding, padding, null);
        }

        icon.setBounds(padding, padding, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);

//        //TODO: Hirer does not work for generic entity
//        if (isHirer) {
//            // TODO: Check this is the correct size depending on each zoom
//            Integer diameter = (int)(200 * multiplier);
//            Drawable hirerIcon = getDrawable(context, R.drawable.ic_ishirer);
//            hirerIcon.setBounds(padding, canvas.getHeight() - diameter, padding + diameter, canvas.getHeight());
//            hirerIcon.draw(canvas);
//        }

        return Bitmap.createScaledBitmap(bitmap, maxWidth, height, true);
    }

    public int getDrawableId(Context context, String name) {
        return (Integer) context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public Bitmap getDrawableImage(Context context, String name, int width, int height) {
        Integer id = (Integer) context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        Drawable icon = getDrawable(context, id);

        Bitmap bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);

        return bitmap;
    }

    public Bitmap getDrawableImage(Context context, int id, int width, int height) {
        Drawable icon = getDrawable(context, id);

        Bitmap bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
//        byte[] byteArray = stream.toByteArray();
//        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        return bitmap;
    }

    @SuppressLint("RestrictedApi")
    private Drawable getDrawable(Context context, int id) {
        return AppCompatDrawableManager.get().getDrawable(context, id);
    }

    public Bitmap getDrawableImage(Context context, Integer id, int width, int height) {
        Drawable icon = getDrawable(context, id);

        Bitmap bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);

        return bitmap;
    }

    private Bitmap getRoundedShape(Bitmap bitmap, Integer width, Integer height) {
        Bitmap targetBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle((float) width / 2, (float) height / 2, (Math.min(((float) width), ((float) height)) / 2), Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = bitmap;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect(0, 0, width, height), null);
        return targetBitmap;
    }

    private Bitmap getContains(String name, boolean isPhoto, boolean isText, Bitmap bitmap) {
        if ((!isText && !isPhoto) || bitmap == null) return bitmap;

        boolean isSymbol = name.startsWith("ic_symbol");
        boolean isService = name.startsWith("ic_symbol_service");

        int diameter = 100;
        int radius = 50;
        int margin = 20;

        if (isSymbol) {
            if (isService) {
                margin = -90;
            } else {
                margin = -20;
            }
        }

        int originalHeight = bitmap.getHeight();
        int newHeight = bitmap.getHeight() + diameter + margin;
        int width = bitmap.getWidth();

        Bitmap targetBitmap = Bitmap.createBitmap(width, newHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        if (isPhoto) {
            p.setColor(Color.RED);
            canvas.drawCircle(0 + radius, (float) originalHeight + radius + margin, radius, p);
        }

        if (isText) {
            p.setColor(Color.BLUE);
            canvas.drawCircle(width - radius, (float) originalHeight + radius + margin, radius, p);
        }

        Bitmap sourceBitmap = bitmap;

        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect(0, 0, width, originalHeight), null);
        return targetBitmap;
    }

    //marker2 = LIBGR.getResizedDrawable("ic_marker_cart", poi.IsImage, poi.IsMessage, context, zoomLevel);

//    public Drawable getTextImageMarker(boolean isImage, boolean isText, Context context, Integer zoomLevel) {
//        String name;
//
//        if (isImage && !isText) {
//            name = "ic_system_image";
//        } else if (isImage && isText) {
//            name = "ic_system_imagetext";
//        } else if (!isImage && isText) {
//            name = "ic_system_text";
//        } else {
//            name = "";
//        }
//
//        Drawable iconResized = null;
//
//        if (!name.isEmpty()) {
//            iconResized = getMarker(name, isImage, isText, zoomLevel);
//        }
//
//        return iconResized;
//    }

    public Drawable getResizedDrawable(String name, boolean isContent, Context context, Integer zoomLevel) {
        if (isContent) {
            if (name.startsWith("ic_marker_transparent")) {
                name = "ic_marker_transparent_contents";
            } else {
                name += "_contents";
            }
        }

        Drawable iconResized = getMarker(name, zoomLevel);
        if (name.startsWith("ic_marker_transparent")) { // && !isContent
            iconResized.setAlpha(0);
        } else if (iconResized != null) {
            iconResized.setAlpha(255);
        }

        return iconResized;
    }

    public Drawable getResizedPoi2(String name, boolean isPhoto, boolean isText, Context context, byte[] marker, Integer zoomLevel) {
        Integer[] zoomLevelWidths =  new Integer[19];
//        zoomLevelWidths[7] = 25;
//        zoomLevelWidths[8] = 30;
//        zoomLevelWidths[9] = 45;
//        zoomLevelWidths[10] = 50;
//        zoomLevelWidths[11] = 55;
//        zoomLevelWidths[12] = 60;
//        zoomLevelWidths[13] = 65;
//        zoomLevelWidths[14] = 70;
//        zoomLevelWidths[15] = 75;
//        zoomLevelWidths[16] = 80;
//        zoomLevelWidths[17] = 85;

        zoomLevelWidths[7] = 30;
        zoomLevelWidths[8] = 30;
        zoomLevelWidths[9] = 30;
        zoomLevelWidths[10] = 55;
        zoomLevelWidths[11] = 55;
        zoomLevelWidths[12] = 55; // Pois usually from here down
        zoomLevelWidths[13] = 55;
        zoomLevelWidths[14] = 55;
        zoomLevelWidths[15] = 75;
        zoomLevelWidths[16] = 75;
        zoomLevelWidths[17] = 75;

        Library LIB = new Library();
        Bitmap markerBitmap = LIB.decodeBinary(marker);

        Integer width = zoomLevelWidths[zoomLevel];

        boolean isTransparent = name.startsWith("ic_marker_transparent");

        if (isTransparent) {
            markerBitmap = adjustOpacity(markerBitmap, 0);
        }

        markerBitmap = getContains(name, isPhoto, isText, markerBitmap);

        Double multiplier = ((double)width / markerBitmap.getWidth());
        Integer height = (int)(markerBitmap.getHeight() * multiplier);

        Bitmap resized = Bitmap.createScaledBitmap(markerBitmap, width, height, false);

        Drawable iconResized = new BitmapDrawable(context.getResources(), resized);

        return iconResized;
    }

    public Bitmap adjustOpacity(Bitmap bitmap, int opacity)
    {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }

    public Drawable getResizedIcon(Context context, Integer id, String name, Integer zoomLevel, byte[] avatarMarker, _Entity.enmStatus status, Integer direction, Boolean isAvatar, Boolean zeroAngleFixed) {
        Integer[] zoomLevelWidths =  new Integer[19];
        zoomLevelWidths[7] = 25;
        zoomLevelWidths[8] = 30;
        zoomLevelWidths[9] = 35;
        zoomLevelWidths[10] = 35;
        zoomLevelWidths[11] = 35;
        zoomLevelWidths[12] = 35;
        zoomLevelWidths[13] = 35;
        zoomLevelWidths[14] = 35;
        zoomLevelWidths[15] = 35;
        zoomLevelWidths[16] = 35;
        zoomLevelWidths[17] = 35;

//        zoomLevelWidths[7] = 25;
//        zoomLevelWidths[8] = 25;
//        zoomLevelWidths[9] = 25;
//        zoomLevelWidths[10] = 27;
//        zoomLevelWidths[11] = 31;
//        zoomLevelWidths[12] = 35;
//        zoomLevelWidths[13] = 39;
//        zoomLevelWidths[14] = 43;
//        zoomLevelWidths[15] = 47;
//        zoomLevelWidths[16] = 51;
//        zoomLevelWidths[17] = 55;

        Library lib = new Library();
        Bitmap avatarMarkerBitmap = (avatarMarker != null ? lib.decodeBinary(avatarMarker) : getAvatarMarker(context, id, name, null, false));

        if (avatarMarker == null) {
            Library LIB = new Library();
            byte[] avatarBinary = (avatarMarkerBitmap != null ? LIB.encodeBinary(avatarMarkerBitmap) : null);
            String entityGuid = Global.getInstance().getDb().getMyEntityGuid();

            Global.getInstance().getDb().writeAvatarMarker(entityGuid, avatarBinary);
        }

        Bitmap directionBitmap = getDrawableImage(context, R.drawable.ic_direction, 120,120);
        Bitmap bitmap = createBitmap(directionBitmap.getWidth(), directionBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (status == _Entity.enmStatus.NotMoving) {
            Bitmap stoppedBitmap = getDrawableImage(context, R.drawable.ic_stopped, 120, 120);
            canvas.drawBitmap(stoppedBitmap, 0, 0, null);
        }

        Integer padding = (int)((directionBitmap.getWidth() - avatarMarkerBitmap.getWidth()) / 2);
        canvas.drawBitmap(avatarMarkerBitmap, padding, padding, null);

        if (!zeroAngleFixed) {
            canvas.rotate(direction, directionBitmap.getWidth() / 2, directionBitmap.getHeight() / 2);
            canvas.drawBitmap(directionBitmap, 0, 0, null);
        }

        Integer width = (isAvatar ? (int)(zoomLevelWidths[zoomLevel]) : zoomLevelWidths[zoomLevel]);
        Double multiplier = ((double)width / directionBitmap.getWidth());
        Integer height = (int)(directionBitmap.getHeight() * multiplier);

        width = Math.round(width * Global.getInstance().getDensityMultiplier());
        height = Math.round(height * Global.getInstance().getDensityMultiplier());

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Drawable iconResized = new BitmapDrawable(context.getResources(), resized);

        return iconResized;
    }

    public Drawable getDrawable(byte[] image) {
        Library lib = new Library();
        Bitmap bitmap = (image != null ? lib.decodeBinary(image) : null);
        return new BitmapDrawable(MyApp.getContext().getResources(), bitmap);
    }

    public Bitmap grabImage(Context context, Uri imageLocation)
    {
        context.getContentResolver().notifyChange(imageLocation, null);
        ContentResolver cr = context.getContentResolver();
        Bitmap bitmap = null;

        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageLocation);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "Failed to load", e);
        }

        return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap bitmap, int maxSize) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        double scale;
        int newHeight;
        int newWidth;

        if (height > width) {
            newHeight = maxSize;
            scale = (double)bitmap.getHeight() / (double)maxSize;
            newWidth = (int)(bitmap.getWidth() / scale);
        } else {
            newWidth = maxSize;
            scale = (double)bitmap.getWidth() / (double)maxSize;
            newHeight = (int)(bitmap.getHeight() / scale);
        }

        Log.d(TAG, "getResizedBitmap -" + scale + "-" + newWidth + "-" + width + "-" + newHeight + "-" + height);
        return getResizedBitmap2(bitmap, newWidth, newHeight);
    }

//    private static Bitmap createScaledBitmap(Bitmap bitmap,int newWidth,int newHeight) {
//        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());
//
//        float scaleX = newWidth / (float) bitmap.getWidth();
//        float scaleY = newHeight / (float) bitmap.getHeight();
//
//        Matrix scaleMatrix = new Matrix();
//        scaleMatrix.setScale(scaleX, scaleY, 0, 0);
//
//        Canvas canvas = new Canvas(scaledBitmap);
//        canvas.setMatrix(scaleMatrix);
//        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
//        paint.setAntiAlias(true);
//        paint.setDither(true);
//        paint.setFilterBitmap(true);
//        canvas.drawBitmap(bitmap, 0, 0, paint);
//
//        return scaledBitmap;
//
//    }

    public Bitmap getResizedBitmap2(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
//        return createScaledBitmap(bitmap, newWidth, newHeight);
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                ExifInterface exif = new ExifInterface(bs);

                String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

                int rotationAngle = 0;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                // Ignore do not check and correct orientation as exception occurred
            }
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Log.d(TAG, "getResizedBitmap " + width + "-" + height + "-" + scaleWidth + "-" + scaleHeight + "-" + newWidth + "-" + newHeight);

        //Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//        bitmap.recycle();

        return resizedBitmap;
    }

    public Bitmap rotateImage(Bitmap bitmap, float rotationAngle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

//    public int getOrientationFromMediaStore(Context context, Uri imageUri) {
//        ContentResolver cr = context.getContentResolver();
//
//        String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
//        Cursor cursor = android.provider.MediaStore.Images.Media.query(cr, imageUri, projection);
//
//        int orientation = -1;
//        if (cursor != null && cursor.moveToFirst()) {
//            orientation = cursor.getInt(0);
//            cursor.close();
//        }
//
//        Log.d(TAG, "Orientation " + orientation);
//
//        return orientation;
//    }
}
