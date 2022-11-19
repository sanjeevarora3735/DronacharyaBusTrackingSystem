package com.example.dbts;


import static com.mapbox.core.constants.Constants.PRECISION_6;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
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


@SuppressWarnings("ALL")
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // Navigation Route Data Members :
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "blue-pin-icon-id";
    static Point origin = Point.fromLngLat(77.175740, 28.580859);
    static int TestCounter = 1;
    static View view;
    // Elements That are Used in The Current Activity :
    private static boolean RouteDeveloped = false;
    private static float DistnMeters = 0;
    private static boolean SavedInstanceFound = false, MapLoaded = false;
    private static int ApplicationInstanceExists = 0;
    Point destination = Point.fromLngLat(0.00, 0.00);
    MapboxMap mapboxMap;
    ScholarData FinalScholarData;
    private Location LastLocation = new Location(LocationManager.GPS_PROVIDER);
    private MapboxDirections client;
    private String mParam1, mParam2;
    private AutoCompleteTextView BusRouteAutoCompleteTextView;
    private MapView MyMapViewInstance;
    private boolean HasLocation = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private TextView BusRouteTextViewMap, originToCampusTime, CampusToOriginTime;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth;
    private boolean SuperUser = false;
    private MaterialButton ViewBusSchedules;
    private SharedPreferences sharedPreference_ScholarData;
    private Location UserCurrentLocation;
    private boolean LocationComponentActivated;
    private Marker NearestBusStop;
    private TextView CurrentLocationView, DistanceLeft;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static Icon drawableToIcon(@NonNull Context context, @DrawableRes int id, @ColorInt int colorRes) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return IconFactory.getInstance(context).fromBitmap(bitmap);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        // MapBox -  Getting Instance @ Api-Key
        Mapbox.getInstance(getContext(), getString(R.string.MapBoxAccessTokenPublic));

        // Fetching The View - For further operations
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        // For Routes Selecttion @ Only Root User
        BusRouteAutoCompleteTextView = view.findViewById(R.id.BusRouteAutoCompleteTextView);

        // For Initialization of MapBoxMap
        MyMapViewInstance = view.findViewById(R.id.MapViewInstance);

        // To Get the FusedLocationProviderClient
        mFusedLocationProviderClient = new FusedLocationProviderClient(getActivity());

        // For Re-Allocation Button @ Floating Button
        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        // Timings of Bus - Arrival & Departure
        originToCampusTime = view.findViewById(R.id.originToCampusTime);
        CampusToOriginTime = view.findViewById(R.id.CampustoOriginTime);

        // Live location textbox...
        CurrentLocationView = view.findViewById(R.id.CurrentLocation);
        DistanceLeft = view.findViewById(R.id.DistanceLeft);

        // Button To View Bus (Updated) Schedules
        ViewBusSchedules = view.findViewById(R.id.ViewBusSchedules);

        // Getting SharedPreferences to save data offline
        sharedPreference_ScholarData = getContext().getSharedPreferences("ScholarData", getContext().MODE_PRIVATE);

        // Firebase Instances (Firestore)
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Code : In-Respect to use the mapbox API
        MyMapViewInstance.onCreate(savedInstanceState);
        MyMapViewInstance.getMapAsync(this);

        // Code For floatingActionButton :
        floatingActionButton.setOnClickListener(v -> {
            GettingThingsReady();
            floatingActionButton.animate().rotation(floatingActionButton.getRotation() - 360).setDuration(2000).start();

        });

        // Onclick Event For ViewBusSchedules
        ViewBusSchedules.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), RealtimeTracker.class));
        });

        // OnlySuper User Event
        BusRouteAutoCompleteTextView.setOnClickListener(v -> {
            if (!SuperUser) {
                //Toast.makeText(getActivity(), "Only super user can access this feature.", //Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getActivity(), "Please enable this feature", //Toast.LENGTH_SHORT).show();
            }
        });
        // Modify the role of Routes DropDown List According to the User's Profile
        SetupRoutesDropDown();

        return view;
    }

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.MapBoxAccessTokenSecret))
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Toast.makeText(getContext(), "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (response.body().routes().size() < 1) {
                    Toast.makeText(getContext(), "No routes found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                DirectionsRoute currentRoute = response.body().routes().get(0);

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
                Toast.makeText(getContext(), "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        StartUpdatingLocation(origin, destination);
        RouteDeveloped = true;

    }

    private void setOrigin(Location location) {
        origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
        CurrentLocationView.setText(String.valueOf((float) origin.latitude()) + " | " + String.valueOf((float) origin.longitude()));

    }

    private void StartUpdatingLocation(Point origin, Point destination) {
        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LastLocation = location;
                setOrigin(LastLocation);
                Location TarDest = new Location(LocationManager.GPS_PROVIDER);
                TarDest.setLatitude(destination.latitude());
                TarDest.setLongitude(destination.longitude());
                DistnMeters = location.distanceTo(TarDest);
                if (DistnMeters < 30) {
                    DistanceLeft.setVisibility(View.GONE);
                } else {
                    DistanceLeft.setVisibility(View.VISIBLE);
                    DistanceLeft.setText(String.valueOf((int) DistnMeters) + " mtrs.");
                }
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(origin.latitude(), origin.longitude()))
                        .zoom(14.5)
                        .tilt(14)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);

            }

        };

        LocationManager locationManager = (LocationManager)
                getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                50,
                mLocationListener);


    }


    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
//        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
//                Feature.fromGeometry(AllotedBusStoppagePoint())}));
//        loadedMapStyle.addSource(iconGeoJsonSource);

    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        routeLayer.setProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineColor(Color.parseColor("#5192DA"))
        );
        loadedMapStyle.addLayer(routeLayer);

        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.red_marker)));

        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                PropertyFactory.iconImage(RED_PIN_ICON_ID),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconOffset(new Float[]{0f, -9f})));
//        NearestBusStop = mapboxMap.getMarkers().get(0);
//        NearestBusStop.setTitle("Moti Bagh");
//        NearestBusStop.setSnippet("DCE Bus Stop");
    }

    private void ApplyingDynamicChanges(ScholarData FetchedScholarData) {
        SyncBusSchedule(FetchedScholarData.getRoute());
        SuperUser = FetchedScholarData.isSuper();
        FinalScholarData = FetchedScholarData;
        if (mapboxMap != null) {
            SetupRoutePosition();
        }
    }

    private void SyncBusSchedule(int RouteNumber) {
        // Updating the Route Number of User
        String RouteNumberText = String.valueOf(RouteNumber);
        BusRouteAutoCompleteTextView.setText("Route No. " + RouteNumberText);

        // Updating the originToCampusTime && CampusToOriginTime
        FirebaseFirestore.getInstance().collection("BUSES")
                .whereEqualTo("Route", RouteNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BusesData TravellingData = document.toObject(BusesData.class);
                                originToCampusTime.setText(TravellingData.getOriginToCampusTime());
                                CampusToOriginTime.setText(TravellingData.getCampusToOriginTime());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void SetupRoutesDropDown() {
        BusRouteAutoCompleteTextView.setInputType(InputType.TYPE_NULL);
    }

    public void GettingThingsReady() {
        if (!SavedInstanceFound) {
            String UserDocumentID = null;
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                UserDocumentID = currentUser.getEmail();
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("identifying_information").document(UserDocumentID);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ScholarData CurrentScholarDataObj = documentSnapshot.toObject(ScholarData.class);
                        ApplyingDynamicChanges(CurrentScholarDataObj);
                    }
                });
            }
        } else {
            // When Data is Found in Local Database :
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        MapLoaded = true;
        if (MapLoaded) {
            GettingThingsReady();
            mapboxMap.getUiSettings().setAttributionEnabled(false);
            mapboxMap.getUiSettings().setLogoEnabled(false);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getContext(), "onStart(); Executed", Toast.LENGTH_SHORT).show();

        RequestPermissionsFromUser();
        GettingThingsReady();

    }

//    private void CameraPositionManagerMiniMap() {
//        RequestPermissionsFromUser();
//        if (MapLoaded) {
//            mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
//                @Override
//                public void onStyleLoaded(@NonNull Style style) {
//                    EnableLocationComponent(style);
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.title("Bus Stop");
//                    markerOptions.setSnippet("Bus Arriving Location");
//                    SetupRoutePosition();
//                    Icon icon = drawableToIcon(getContext(), R.drawable.red_marker, R.color.ActivatedRoute);
//                    markerOptions.position(new LatLng(AllotedBusStoppagePoint().longitude(), AllotedBusStoppagePoint().latitude()));
//                    markerOptions.setIcon(icon);
//                    mapboxMap.addMarker(markerOptions);
//                }
//            });
//        }
//    }

    private void EnableLocationComponent(@NonNull Style loadedMapStyle) {
        // Get an instance of the component
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        // Activate with options
        locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());
        locationComponent.setLocationComponentEnabled(true);

        // Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);
        // Set the component's render mode
        locationComponent.setRenderMode(RenderMode.NORMAL);
    }

    private void RequestPermissionsFromUser() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            }
        } else {
            HasLocation = true;
        }
    }

    private void SetupRoutePosition() {
        mapboxMap.setStyle(Style.OUTDOORS, style -> {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(origin.latitude(), origin.longitude()))
                    .zoom(14.5)
                    .tilt(14)
                    .build();
            if (mapboxMap != null) {
                initSource(style);
                initLayers(style);
                getRoute(mapboxMap, origin, AllotedBusStoppagePoint());
            }
            EnableLocationComponent(style);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Bus Stop");
            markerOptions.setSnippet("Moti Bagh");
            IconFactory iconFactory = IconFactory.getInstance(getContext());
            Icon icon = drawableToIcon(getContext(), R.drawable.markersmall, R.color.ActivatedRoute);
            markerOptions.position(new LatLng(AllotedBusStoppagePoint().latitude(), AllotedBusStoppagePoint().longitude()));
            markerOptions.setIcon(icon);
            mapboxMap.addMarker(markerOptions);
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
        });
    }

    // Returning the AllotedBusStoppagePoint from the firebase :
    private Point AllotedBusStoppagePoint() {
        return Point.fromLngLat(FinalScholarData.getPointLongitude(), FinalScholarData.getPointLatitude());
    }
}
