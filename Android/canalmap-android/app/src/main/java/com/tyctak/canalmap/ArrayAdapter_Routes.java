package com.tyctak.canalmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tyctak.map.entities._Route;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;

//import org.jsoup.helper.StringUtil;

import java.util.ArrayList;

public class ArrayAdapter_Routes extends ArrayAdapter<_Route> {

    final private String TAG = "ArrayAdapter_Routes";

    private boolean allPurchased = false;
    private boolean allPaused = false;
    private String dialogRouteGuid;

    public void setAllPaused(boolean value) {
        allPaused = value;
    }
    public void setAllPurchased(boolean value) {
        allPurchased = value;
    }
    public void setDialogRouteGuid(String pDialogRouteGuid) { dialogRouteGuid = pDialogRouteGuid; }

    public ArrayAdapter_Routes(Context context, ArrayList<_Route> routes) {
        super(context, 0, routes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        _Route route = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.arrayadapter_routes, parent, false);
        }

        Library_UI LIBUI = new Library_UI();

        ImageButton paused_button = (ImageButton) convertView.findViewById(R.id.buttonPaused);
        Button update_button = (Button) convertView.findViewById(R.id.update_button_item);
        Button buy_button = (Button) convertView.findViewById(R.id.buy_button_item);
        LinearLayout availability_layout = (LinearLayout) convertView.findViewById(R.id.availability_layout);
        TextView route_name = (TextView) convertView.findViewById(R.id.waterway_name);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView availability_text = (TextView) convertView.findViewById(R.id.availability_text);
        TextView updatedue_text = (TextView) convertView.findViewById(R.id.updatedue_text);
        ImageView typeid = (ImageView) convertView.findViewById(R.id.entityMarker);
        ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.progressBar);
        LinearLayout updatedue_layout = (LinearLayout) convertView.findViewById(R.id.updatedue_layout);

        int percentage = (route.Empty ? 0 : (int) ((double) route.TilesDownloaded / (double) route.TotalTiles * 100.0));
        boolean isAvailable = (route.CurrentVersion < 0);
        boolean isBuyAll = (route.RouteGuid.equals(Global.getInstance().getDb().getAllRouteGuid()));
        boolean isUpdateDue = (route.CurrentVersion >= 0 && route.Availability != null && !route.Availability.isEmpty());

        paused_button.setImageResource(route.Paused ? R.drawable.ic_play_button : R.drawable.ic_pause_button);

        LIBUI.dV(paused_button, View.GONE);
        XP_Library_CM LIBCM = new XP_Library_CM();

        if (!allPurchased && !route.Purchased && route.CurrentVersion >= 0 && !LIBCM.isBlank(route.Price)) {
            LIBUI.dV(buy_button, View.VISIBLE);
            if (isBuyAll) buy_button.setText("Buy All");
            else buy_button.setText("Buy Route");
        } else {
            LIBUI.dV(buy_button, View.GONE);
        }

        if (!isBuyAll && ((allPurchased && route.MyVersion < route.CurrentVersion && route.MyVersion > 0) || (route.Purchased && route.MyVersion < route.CurrentVersion && route.MyVersion > 0) || (route.Purchased && route.Empty))) {
            LIBUI.dV(update_button, View.VISIBLE);
        } else {
            LIBUI.dV(update_button, View.GONE);
        }

        if (isAvailable) {
            LIBUI.dV(availability_layout, View.VISIBLE);
            availability_text.setText(route.Availability);
        } else {
            LIBUI.dV(availability_layout, View.GONE);
        }

        route_name.setText(route.RouteName);

        if (route.Type.equals("River")) typeid.setImageResource(R.drawable.ic_river);
        if (route.Type.equals("Mixed")) typeid.setImageResource(R.drawable.ic_mixed);
        if (route.Type.equals("Canal")) typeid.setImageResource(R.drawable.ic_canal);
        if (isBuyAll) typeid.setImageResource(R.drawable.ic_allroutes);

        LIBUI.dV(progress, View.INVISIBLE);

        if ((allPurchased || route.Purchased) && !isBuyAll && !isAvailable) {
            progress.setProgress(percentage);

            if (route.Empty && route.TotalTiles == 0) {
                if (dialogRouteGuid != null && dialogRouteGuid.equals(route.RouteGuid)) {
                    LIBUI.dV(update_button, View.GONE);
                } else {
                    price.setText("empty");
                    LIBUI.dV(update_button, View.VISIBLE);
                    update_button.setText("Download");
                }
            } else if (route.TilesDownloaded >= route.TotalTiles && route.TilesDownloaded != 0) {
                if (route.MyVersion < route.CurrentVersion && route.MyVersion > 0) {
                    LIBUI.dV(update_button, View.VISIBLE);
                    route.Availability = "NOW";
                    update_button.setText("Update");
                } else {
                    LIBUI.dV(update_button, View.GONE);
                }
                price.setText("downloaded (" + route.TotalTiles + " tiles)");
            } else if (route.TilesDownloaded >= route.TotalTiles && route.TilesDownloaded != 0) {
                price.setText("downloaded (" + route.TotalTiles + " tiles)");
            } else if (route.TilesDownloaded == 0 && !route.Paused) {
                isUpdateDue = false;
                LIBUI.dV(paused_button, View.VISIBLE);
                price.setText("downloading - please wait");
                if (update_button.getVisibility() != View.GONE) update_button.setVisibility(View.GONE);
                update_button.setText("Download");
            } else {
                if (allPaused) {
                    isUpdateDue = false;
                    price.setText("ALL Downloads Paused (" + route.TilesDownloaded + "/" + route.TotalTiles + "/" + percentage + "% tiles)");
                } else if (route.Paused) {
                    isUpdateDue = false;
                    LIBUI.dV(paused_button, View.VISIBLE);
                    price.setText("ROUTE Download Paused (" + route.TilesDownloaded + "/" + route.TotalTiles + "/" + percentage + "% tiles)");
                } else {
                    isUpdateDue = false;
                    LIBUI.dV(paused_button, View.VISIBLE);
                    price.setText("downloading (" + route.TilesDownloaded + "/" + route.TotalTiles + "/" + percentage + "% tiles)");
                }

                LIBUI.dV(progress, View.VISIBLE);
                LIBUI.dV(update_button, View.GONE);
            }
        } else if (isBuyAll && allPurchased) {
            price.setText("purchased");
            if (route.Purchased) {
                LIBUI.dV(buy_button, View.GONE);
            }
            LIBUI.dV(update_button, View.GONE);
            LIBUI.dV(progress, View.GONE);
        } else if (isAvailable) {
            price.setText("not available yet");
            LIBUI.dV(update_button, View.GONE);
            LIBUI.dV(progress, View.GONE);
        } else if (isBuyAll) {
            LIBUI.dV(progress, View.GONE);
            progress.setProgress(0);
            if (LIBCM.isBlank(route.Price)) {
                price.setText(route.Description);
            } else {
                price.setText(route.Price);
            }
        } else {
            progress.setProgress(0);
            if (LIBCM.isBlank(route.Price)) {
                price.setText(route.Description);
            } else {
                price.setText(route.Price);
            }
        }

        if (isUpdateDue) {
            LIBUI.dV(updatedue_layout, View.VISIBLE);
            updatedue_text.setText(route.Availability);
        } else {
            LIBUI.dV(updatedue_layout, View.GONE);
        }

        buy_button.setTag(route.RouteGuid);
        paused_button.setTag(route.RouteGuid);
        update_button.setTag(route.RouteGuid);

        return convertView;
    }
}