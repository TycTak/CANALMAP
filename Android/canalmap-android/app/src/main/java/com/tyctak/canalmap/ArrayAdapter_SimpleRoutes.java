package com.tyctak.canalmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tyctak.map.entities._Route;
import com.tyctak.canalmap.libraries.Library_UI;

import java.util.ArrayList;

public class ArrayAdapter_SimpleRoutes extends ArrayAdapter<_Route> {

    final private static String TAG = "ArrayAdapter_SimpleRoutes";
    private String dialogRouteGuid;

    public void setDialogRouteGuid(String pDialogRouteGuid) { dialogRouteGuid = pDialogRouteGuid; }

    public ArrayAdapter_SimpleRoutes(Context context, ArrayList<_Route> routes) {
        super(context, 0, routes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        _Route route = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.arrayadapter_simpleroutes, parent, false);
        }

        Library_UI LIBUI = new Library_UI();

        Button update_button = (Button) convertView.findViewById(R.id.update_button_item);

        TextView route_name = (TextView) convertView.findViewById(R.id.waterway_name);
        TextView price = (TextView) convertView.findViewById(R.id.route_description_simple);

        route_name.setText(route.RouteName);

        if (route.Purchased && route.Empty && route.TotalTiles == 0) {
            price.setText("purchased");

            if (dialogRouteGuid != null && dialogRouteGuid.equals(route.RouteGuid)) {
                LIBUI.dV(update_button, View.GONE);
                LIBUI.dV(price, View.VISIBLE);
                price.setText(route.Description);
            } else {
                LIBUI.dV(update_button, View.VISIBLE);
                LIBUI.dV(price, View.VISIBLE);
                price.setText(route.Description);
                update_button.setText("Download");
            }
        } else if (route.Purchased && route.TilesDownloaded >= route.TotalTiles && route.TilesDownloaded != 0 && route.MyVersion < route.CurrentVersion && route.MyVersion > 0) {
            LIBUI.dV(update_button, View.VISIBLE);
            update_button.setText("Update");
            price.setText(route.Description);
        } else if (!route.Purchased && route.CurrentVersion >= 0) {
            LIBUI.dV(update_button, View.GONE);
            LIBUI.dV(price, View.VISIBLE);
            price.setText(route.Description);
        } else {
            LIBUI.dV(update_button, View.GONE);
            LIBUI.dV(price, View.VISIBLE);

            if (route.CurrentVersion < 0) {
                price.setText("not available");
            } else if (route.TilesDownloaded >= route.TotalTiles && price.getText() != "downloading - please wait") {
                price.setText(route.Description);
            } else if (route.TilesDownloaded == 0) {
                price.setText("downloading - please wait");
            } else {
                price.setText("downloading (" + route.TilesDownloaded + "/" + route.TotalTiles + "/" + route.Percentage + "% tiles)");
            }
        }

        update_button.setTag(route.RouteGuid);

        return convertView;
    }
}