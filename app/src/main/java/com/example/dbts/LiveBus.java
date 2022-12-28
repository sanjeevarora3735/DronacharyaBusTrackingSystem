package com.example.dbts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

public class LiveBus extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUESTCALL = 6699;
    private static MarkerOptions LiveMark = new MarkerOptions();
    private static boolean MarkerAllocated = false;
    private static MapView mapView;
    private static MapboxMap mapboxMap;
    private static ValueEventListener BusLiveLocation;
    private DatabaseReference DCE_Bus_Live;
    private String Bus_Number;
    private ImageView CallButton;
    private String DriversNumber = "7982344017";
    private static Bus_Location NewBus_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.MapBoxAccessTokenPublic));
        setContentView(R.layout.activity_live_bus);
        mapView = findViewById(R.id.MapViewInstance);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        CallButton = findViewById(R.id.CallButton);

        CallButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + DriversNumber));//change the number

            if (ContextCompat.checkSelfPermission(LiveBus.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LiveBus.this, new String[]{Manifest.permission.CALL_PHONE}, REQUESTCALL);
            } else {
                startActivity(callIntent);
            }
        });

        //Fetching The Bus Number From The Intent

        Bus_Number = getIntent().getExtras().getString("Bus_Number");
        FetchBusLivePoints(Bus_Number);

//        IconFactory iconFactory = IconFactory.getInstance(LiveBus.this);
//        Icon icon = iconFactory.fromResource(R.drawable.custom_marker___edited);
//        LiveMark.setIcon(icon);



    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUESTCALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CallButton.performClick();
            } else {
                Toast.makeText(this, "Permission Denied :(", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void FetchBusLivePoints(String Bus_Number) {
        DCE_Bus_Live = FirebaseDatabase.getInstance().getReference("DCE_BUSES").child(Bus_Number).child("Live_Location");

        BusLiveLocation = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Bus_Location getBusLocation = snapshot.getValue(Bus_Location.class);
                NewBus_location = new Bus_Location(getBusLocation);
                if (mapboxMap != null) {
                    UpdatePosition(getBusLocation.getLatitude(), getBusLocation.getLongitude());
                } else {
                    Toast.makeText(LiveBus.this, "MapBoxMap is Null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        DCE_Bus_Live.addValueEventListener(
                BusLiveLocation
        );

    }

    private void UpdatePosition(double Latitude, double Longitude) {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(Latitude, Longitude))
                .zoom(14)
                .tilt(14)
                .build();
        if (!MarkerAllocated) {
            LiveMark.position(new LatLng(Latitude, Longitude));
            mapboxMap.addMarker(LiveMark);
        } else {
            if (mapboxMap.getMarkers().size() > 0) {
                mapboxMap.getMarkers().get(0).setPosition(new LatLng(Latitude, Longitude));
            }else{
                mapboxMap.addMarker(LiveMark);
                mapboxMap.getMarkers().get(0).setPosition(new LatLng(Latitude, Longitude));
            }

        }
        MarkerAllocated = true;
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
        mapboxMap.setStyle(Style.OUTDOORS, style -> {
//            initSource(style);
//            initLayers(style);
//            getRoute(mapboxMap, origin, destination);
        });

        FirebaseDatabase.getInstance().getReference("DCE_BUSES").child(Bus_Number).child("Live_Location").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    Bus_Location temp = task.getResult().getValue(Bus_Location.class);
                    Toast.makeText(LiveBus.this, "Calling Under OnMapReadyCallback", Toast.LENGTH_SHORT).show();
                    UpdatePosition(temp.getLatitude(), temp.getLongitude());
                    Toast.makeText(LiveBus.this, task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}