package com.example.a0lambj41.poiassignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity
{

    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    private List<POIs> listPOIs;
    Map<String,Drawable> markersType;
    private boolean savetoweb;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_main);

        mv = (MapView)findViewById(R.id.map1);

        mv.setBuiltInZoomControls(true);
        mv.getController().setZoom(14);
        mv.getController().setCenter(new GeoPoint(50.9,-1.4));

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);
        this.listPOIs = new ArrayList<>();
        this.markersType = new HashMap<>();
    }

    private class POIs {
        private String name, type, description;
        private double latitude, longitude;

        public POIs(String nameArray, String typeArray, String descriptionArray, double latArray, double longArray) {
            this.name = nameArray;
            this.type = typeArray;
            this.description = descriptionArray;
            this.latitude = latArray;
            this.longitude = longArray;
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public String getDescription() {
            return this.description;
        }

        public Double getLatitude() {
            return this.latitude;
        }

        public Double getLongitude() {
            return this.longitude;
        }
    }



    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }



    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.addpoi) {
            // react to the menu item being selected...
            // Launch second activity
            Intent intent = new Intent(this, AddPOIActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        else if(item.getItemId() == R.id.save) {
            savePOIs();
            return true;
        }
        else if (item.getItemId() == R.id.load) {
            loadPOIs();
            return true;
        }
        else if (item.getItemId() == R.id.preferences) {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == 0) {
            Bundle bundle = intent.getExtras();

            String poiname = bundle.getString("com.example.pointofinterestapp.name");
            String poitype = bundle.getString("com.example.pointofinterestapp.type");
            String poidesc = bundle.getString("com.example.pointofinterestapp.desc");

            double latitude = mv.getMapCenter().getLatitude();
            double longitude = mv.getMapCenter().getLongitude();

            OverlayItem addpoi = new OverlayItem(poiname, poitype + poidesc, new GeoPoint(latitude, longitude));

            this.listPOIs.add(new POIs(poiname, poitype, poidesc, latitude, longitude));

            items.addItem(addpoi);
            mv.getOverlays().add(items);
            mv.refreshDrawableState();

            Toast.makeText(MainActivity.this, "Marker Created!", Toast.LENGTH_SHORT).show();

        }else if(requestCode == 1) {
        }
    }

    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        savetoweb = prefs.getBoolean("savetoweb", false);
    }

    private void savePOIs(){

        if(savetoweb != true) {
            //Toast.makeText(MainActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath(), Toast.LENGTH_SHORT).show();
            String savedDetails = "";
            for (POIs p : listPOIs) {
                savedDetails += p.getName() + "," + p.getType() + "," + p.getDescription() + "," + p.getLatitude() + "," + p.getLongitude() + "\n";
            }
            try {

                PrintWriter pw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/markers.csv", true));
                pw.println(savedDetails);
                pw.flush();
                pw.close();
            } catch (IOException e) {
                new AlertDialog.Builder(this).setMessage("ERROR: " + e).setPositiveButton("OK", null).show();
            }
            Toast.makeText(MainActivity.this, "Marker Added!", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(MainActivity.this, "Upload to Web!", Toast.LENGTH_LONG).show();
        }
    }














    private void loadPOIs() {
        try {
            Toast.makeText(MainActivity.this, "Markers Loaded", Toast.LENGTH_LONG).show();
            FileReader fr = new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/markers.csv");
            BufferedReader reader = new BufferedReader(fr);
            String line = "";

            while ((line = reader.readLine()) != null)
            {
                String[] components = line.split(",");

                if (components.length > 4) {

                    //Toast.makeText(MainActivity.this, line, Toast.LENGTH_LONG).show();

                    String name = components[0];
                    String type = components[1];
                    String description = components[2];
                    double latitude = Double.parseDouble(components[3]);
                    double longitude = Double.parseDouble(components[4]);

                    OverlayItem loadingitem = new OverlayItem(name, type + description, new GeoPoint(latitude, longitude));
                    items.addItem(loadingitem);
                    mv.getOverlays().add(items);
                }
            }
            reader.close();
        }
        catch (IOException e) {
            new AlertDialog.Builder(this).setMessage("ERROR: " + e).setPositiveButton("OK", null).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        savePOIs();
    }
}