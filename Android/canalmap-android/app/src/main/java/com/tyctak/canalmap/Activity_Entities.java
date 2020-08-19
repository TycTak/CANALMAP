package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tyctak.map.entities._Entity;
import com.tyctak.canalmap.libraries.Library_UI;
//import com.tyctak.canalmap.libraries.Library_DB;

import java.util.ArrayList;

import static com.tyctak.canalmap.R.id.displayFavourite;
import static com.tyctak.canalmap.R.id.displayShopping;

public class Activity_Entities extends AppCompatActivity {

    final private String TAG = "Activity_Entities";
    final private Activity_Entities activity = this;

    final Library_UI LIBUI = new Library_UI();

    private static boolean isFavourite;
    private static boolean isShopping;
    private static boolean isSocialMedia;
    private static String filter;

    ListView entityList;
    ArrayList<_Entity> entities;
    ArrayAdapter_Entities arrayAdapter;

    private int REQUEST_GOTO = 1;
    boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entities);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_pause);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.entitiesTitle);

        entityList = (ListView) findViewById(R.id.list_entities);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                _Entity entity = Global.getInstance().getDb().getMyEntitySettings();
                isActive = entity.IsActive;

                entities = Global.getInstance().getDb().getEntities(isFavourite, isFavourite, isSocialMedia, filter);

                if (entities.size() > 0) {
                    arrayAdapter = new ArrayAdapter_Entities(activity, entities);
                    entityList.setAdapter(arrayAdapter);

                    entityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            _Entity entity = (_Entity) parent.getItemAtPosition(position);

                            Intent intent = new Intent(Activity_Entities.this, Dialog_Entity.class);
                            intent.putExtra("entityguid", entity.EntityGuid);
                            startActivityForResult(intent, REQUEST_GOTO);
                        }
                    });

                    LinearLayout progress = (LinearLayout) findViewById(R.id.progressEntities);
                    progress.setVisibility(View.GONE);

                    LinearLayout markers = (LinearLayout) findViewById(R.id.toolbarEntities);
                    markers.setVisibility(View.VISIBLE);

                    changeFilter(isFavourite, isShopping, isSocialMedia, filter);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.postDelayed(runnable, 100);

        isFavourite = false;
        isShopping = false;
        isSocialMedia = false;
        filter = null;

        SearchView search = (SearchView) findViewById(R.id.search);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter = newText;
                changeFilter(isFavourite, isShopping, isSocialMedia, filter);
                return false;
            }
        });

        ImageButton favourite = (ImageButton) findViewById(R.id.displayFavourite);
        ImageButton shopping = (ImageButton) findViewById(R.id.displayShopping);
        ImageButton social = (ImageButton) findViewById(R.id.displaySocialMedia);

        favourite.setImageResource(isFavourite ? R.drawable.ic_favourite_enable : R.drawable.ic_favourite_disable);
        shopping.setImageResource(isShopping ? R.drawable.ic_shopping_enable : R.drawable.ic_shopping_disable);
        social.setImageResource(isSocialMedia ? R.drawable.ic_socialmedia_enable : R.drawable.ic_socialmedia_disable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Bundle extras = data.getExtras();

        if (extras != null) {
            if (requestCode == REQUEST_GOTO) {
                String entityGuid = extras.getString("entityguid");
                clickGoto(entityGuid);
            }
        }

        changeFilter(isFavourite, isShopping, isSocialMedia, filter);
    }

    private void changeFilter(boolean isFavourite, boolean isShopping, boolean isSocialMedia, String filter) {
        entities = Global.getInstance().getDb().getEntities(isFavourite, isShopping, isSocialMedia, filter);
        arrayAdapter.clear();
        arrayAdapter.addAll(entities);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void filterShopping(View view) {
        ImageButton display = (ImageButton) findViewById(displayShopping);

        isShopping = !isShopping;
        display.setImageResource(isShopping ? R.drawable.ic_shopping_enable : R.drawable.ic_shopping_disable);

        changeFilter(isFavourite, isShopping, isSocialMedia, filter);
    }

    public void filterSocialMedia(View view) {
        ImageButton display = (ImageButton) findViewById(R.id.displaySocialMedia);

        isSocialMedia = !isSocialMedia;
        display.setImageResource(isSocialMedia ? R.drawable.ic_socialmedia_enable : R.drawable.ic_socialmedia_disable);

        changeFilter(isFavourite, isShopping, isSocialMedia, filter);
    }

    public void filterFavourites(View view) {
        ImageButton display = (ImageButton) findViewById(displayFavourite);

        isFavourite = !isFavourite;
        display.setImageResource(isFavourite ? R.drawable.ic_favourite_enable : R.drawable.ic_favourite_disable);

        changeFilter(isFavourite, isShopping, isSocialMedia, filter);
    }

    public void clickFavourite(View view) {
        final String entityGuid = view.getTag().toString();
        ImageButton buttonFavourite = (ImageButton) view.findViewById(R.id.buttonFavourite);

        if (Global.getInstance().getDb().getEntityFavourite(entityGuid)) {
            buttonFavourite.setImageResource(R.drawable.ic_favourite_off);
            Global.getInstance().getDb().writeEntityFavourite(entityGuid, false);
        } else {
            buttonFavourite.setImageResource(R.drawable.ic_favourite_on);
            Global.getInstance().getDb().writeEntityFavourite(entityGuid, true);
        }

        changeFilter(isFavourite, isShopping, isSocialMedia, filter);
    }

    public void clickGoTo(View view) {
        ImageButton button = (ImageButton) view.findViewById(R.id.buttonGoTo);
        String[] temp = button.getTag().toString().split(":");
        final String entityGuid = temp[0];

        _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);

        if (entity.Latitude != 0 && temp[1].equals(getResources().getResourceEntryName(R.drawable.ic_goto))) {
            clickGoto(entityGuid);
        } else {
            Intent intent = new Intent(Activity_Entities.this, Dialog_Entity.class);
            intent.putExtra("entityguid", entityGuid);
            startActivityForResult(intent, REQUEST_GOTO);
        }
    }

    private void clickGoto(String entityGuid) {
        Intent intent = new Intent();
        intent.putExtra("entityguid", entityGuid);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Global.getInstance().getDb().writeMyEntitySearch(isFavourite, isShopping, isSocialMedia);

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}