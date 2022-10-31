package com.example.dbts;

import static com.example.dbts.HomeFragment.drawableToIcon;
import static com.mapbox.core.constants.Constants.PRECISION_6;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealtimeTracker extends AppCompatActivity implements OnMapReadyCallback, IBaseGpsListener {

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private static int Counter = 0;
    private final Point origin = Point.fromLngLat(77.176199, 28.580965);
    private final Point destination = Point.fromLngLat(76.869028, 28.429000);
    private final Point StoppagePoint = Point.fromLngLat(77.151814, 28.495214);
    private final Point BusLive = Point.fromLngLat(77.176199, 28.580965);
    MapboxMap mapboxMap;
    MarkerOptions markerOption_Root = new MarkerOptions();
    private MapboxDirections client;
    private MapView mapView;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth;
    private boolean MapboxMapInitiated = false, Marker_Root_Initiated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.MapBoxAccessTokenPublic));
        setContentView(R.layout.activity_realtime_tracker);

        mapView = findViewById(R.id.MapViewInstance);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();


        ///

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Toast.makeText(this, "Height && Width == "+String.valueOf(height)+" && "+String.valueOf(width), Toast.LENGTH_SHORT).show();


        ///


        floatingActionButton = findViewById(R.id.floatingActionButton);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FetchLocationUpdates();
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        MapboxMapInitiated = true;
        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
        mapboxMap.setStyle(Style.OUTDOORS, style -> {
//            initSource(style);
//            initLayers(style);
//            getRoute(mapboxMap, origin, destination);
        });
    }


    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(StoppagePoint.longitude(), StoppagePoint.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        routeLayer.setProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.red_marker)));

        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                PropertyFactory.iconImage(RED_PIN_ICON_ID),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconOffset(new Float[]{0f, -9f})));
    }


    @SuppressLint("MissingPermission")
    private void FetchLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            floatingActionButton.animate().rotation(floatingActionButton.getRotation() - 360).setDuration(2000).start();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            Toast.makeText(this, "Enable GPS !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }


    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .addWaypoint(StoppagePoint)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.MapBoxAccessTokenSecret))
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Toast.makeText(getApplicationContext(), "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (response.body().routes().size() < 1) {
                    Toast.makeText(getApplicationContext(), "No routes found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                DirectionsRoute currentRoute = response.body().routes().get(0);
                Toast.makeText(getApplicationContext(), "Approx " + currentRoute.distance() / 1000 + "Km", Toast.LENGTH_SHORT).show();

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Update Location at Database
        UpdateLocationFirebase(location);

    }

    private void UpdateCameraPosition(Location location) {
        if (MapboxMapInitiated) {
            markerOption_Root.title("DCE Bus : 6");
            markerOption_Root.setSnippet("Live Location");
            Icon icon = drawableToIcon(getApplicationContext(), R.drawable.red_marker, getResources().getColor(R.color.ActivatedRoute));
            markerOption_Root.setIcon(icon);
            markerOption_Root.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

            if (!Marker_Root_Initiated) {
                mapboxMap.addMarker(markerOption_Root);
                Marker_Root_Initiated = true;
            } else {
                mapboxMap.getMarkers().get(0).setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(14)
                    .tilt(14)
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
        }
    }

    private void UpdateLocationFirebase(Location location) {
        // Applied Later......
        UpdateCameraPosition(location);
        Toast.makeText(this, String.valueOf(Counter++), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onGpsStatusChanged(int i) {

    }
}

