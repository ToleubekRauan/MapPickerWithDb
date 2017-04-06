package com.example.rauan.mappickerwithdb.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauan.mappickerwithdb.R;
import com.example.rauan.mappickerwithdb.adapters.PlaceAdapter;
import com.example.rauan.mappickerwithdb.datebase.DatabaseConnector;
import com.example.rauan.mappickerwithdb.model.PlacePickModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private List<PlacePickModel> placePickModelList = new ArrayList<>();
    PlaceAdapter placeAdapter;
    ListView listViewPlace;
    PlacePickModel placePickModel;
    RemovePlacePick removePlacePick;
    DatabaseConnector databaseConnector;
    TextView tvNoPlace;
    private static final int ACCESS_CODE = 100;

    private GetPlaceInfo getPlaceInfo;

    private WritePlaceInfoToDbAsync writePlaceInfoToDbAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewPlace = (ListView) findViewById(R.id.listViewPlaceInfo);
        tvNoPlace = (TextView) findViewById(R.id.tvNoPlace);
        placeAdapter = new PlaceAdapter(MainActivity.this, placePickModelList);
        listViewPlace.setAdapter(placeAdapter);

        getPlaceInfo = new GetPlaceInfo();
        getPlaceInfo.execute();
        listViewPlace.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PlacePickModel placePickModelDelete = placePickModelList.get(position);
                showRemovePlaceDialog(placePickModelDelete);
                return false;
            }
        });
        listViewPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlacePickModel placePickModel = placePickModelList.get(position);
                String lat = placePickModel.getLatitude();
                String lng = placePickModel.getLongitude();
                String latlng = lat+","+lng;
                Intent intent = new Intent(MainActivity.this,GoogleMapWithMarkers.class);
                intent.putExtra("latlng", latlng);
                startActivity(intent);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mapPick:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MainActivity.this), ACCESS_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.mapAll:
                Intent intent = new Intent(MainActivity.this,GoogleMapWithMarkers.class);
                startActivity(intent);
                break;
            case R.id.mapDelete:
                MainActivity.this.deleteDatabase(databaseConnector.DATABASE_NAME);
                listViewPlace.setAdapter(null);


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 100){
                Place place = PlacePicker.getPlace(MainActivity.this,data);
                String name = place.getName().toString();
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                PlacePickModel placePickModel = new PlacePickModel(name, latitude, longitude);
                placePickModelList.add(placePickModel);

                Log.d("PlacePickList", placePickModelList.toString());


                writePlaceInfoToDbAsync = new WritePlaceInfoToDbAsync(placePickModel);
                writePlaceInfoToDbAsync.execute();
                placeAdapter.notifyDataSetChanged();

                Log.d("place", place.toString());


            }
        }
    }
    private class GetPlaceInfo extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseConnector connector = new DatabaseConnector(MainActivity.this);
            placePickModelList.addAll(connector.getPlacesInfo());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            placeAdapter = new PlaceAdapter(MainActivity.this,placePickModelList);
//            listViewPlace.setAdapter(placeAdapter);
            listViewPlace.setEmptyView(findViewById(R.id.tvNoPlace));
            placeAdapter.notifyDataSetChanged();
        }
    }

    private class WritePlaceInfoToDbAsync extends AsyncTask<Void,Void,Void>{
        private PlacePickModel placePickModel;


        public WritePlaceInfoToDbAsync(PlacePickModel placePickModel) {
            this.placePickModel = placePickModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //PlacePickModel model = new PlacePickModel(name, latitude, longitude);
            DatabaseConnector connector = new DatabaseConnector(MainActivity.this);
            connector.insertPlaceInfo(placePickModel);
            Log.d("", placePickModel.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    private class RemovePlacePick extends AsyncTask<Void,Void,Void>{
        private PlacePickModel placePickModel;

        public RemovePlacePick(PlacePickModel placePickModel) {
            this.placePickModel = placePickModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseConnector connector = new DatabaseConnector(MainActivity.this);
            connector.removeMapPick(placePickModel);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            placePickModelList.remove(placePickModel);
            placeAdapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, placePickModel.getName()+ " was removed" ,Toast.LENGTH_SHORT).show();
        }
    }
    private void showRemovePlaceDialog(final PlacePickModel placePickModel) {
        AlertDialog alertDialog =
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Do you want remove " + placePickModel.getName() + " ?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removePlacePick = new RemovePlacePick(placePickModel);
                                removePlacePick.execute();
                            }
                        })
                        .create();
        alertDialog.show();
    }
}
