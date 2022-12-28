package com.example.dbts;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RouteSelection extends AppCompatActivity {

    private final int ImageButtonStarting_ID = 2897;
    BusesData TravellingData, FinalTravellingData;
    private Boolean isRouteSetted = true;
    // Initializing some important variable for further use :
    private Button ContinueButton;
    private Toolbar toolbar;
    private TextView SelectedRouteDescription, ErrorText;
    private String StoppagePointList;
    private boolean TravellingDataAccessed = false, isRouteSelected = false;
    private int SelectedRouteNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selection);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Assigning values to some objects & widgets
        ContinueButton = findViewById(R.id.ContinueButton);
        SelectedRouteDescription = findViewById(R.id.SelectedRouteDescription);
        ErrorText = findViewById(R.id.ErrorText);


        // Formatting The Options of ToolBar For Customization
        toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        // Setting up the on-click listener for the ContinueButton
        ContinueButton.setOnClickListener(v ->
        {
            if (isRouteSelected) {
                startActivity(new Intent(RouteSelection.this, locationRequest.class));
                FirebaseFirestore.getInstance().collection("identifying_information")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase(Locale.ROOT))
                        .update("route",SelectedRouteNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RouteSelection.this, "Route Updated " + String.valueOf(SelectedRouteNumber), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                ErrorText.setVisibility(View.VISIBLE);
            }
        });

        // Setting up the on-click listener for the back button
        View logoView = getToolbarLogoView(toolbar);
        logoView.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        // Do Nothing
        Toast.makeText(this, "...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        // Getting the no. of available routes for which we have to create options
        super.onStart();
        if (isRouteSetted) {

            DocumentReference docRef = FirebaseFirestore.getInstance().collection("BUSES").document("Routes");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String available_routes = String.valueOf((document.getData().get("Available_Routes")));
                            SetupAllRoutesButtons(Integer.parseInt(available_routes));
                        }
                    }
                }
            });

            isRouteSetted = false;
        } else {
            Toast.makeText(this, "Do Nothing", Toast.LENGTH_SHORT).show();
        }

    }

    // What we have to when someone select the route via onclick
    private void onRouteSelection(ImageButton routeImageButton, int Routes) {
        int Range = Routes + ImageButtonStarting_ID;
        for (int i = ImageButtonStarting_ID + 1; i <= Range; i++) {
            ImageButton xImageButton = findViewById(i);
            xImageButton.setClickable(true);
            xImageButton.setBackgroundResource(R.drawable.route_selector);
        }
        routeImageButton.setBackgroundResource(R.drawable.route_selected);
        routeImageButton.setClickable(false);
        SettingUpStoppagePoints(routeImageButton.getId() - ImageButtonStarting_ID);
        SelectedRouteNumber = routeImageButton.getId() - ImageButtonStarting_ID;
        isRouteSelected = true;
        if (TravellingDataAccessed) {
//            Toast.makeText(this, "Fucked Up", Toast.LENGTH_SHORT).show();
            SelectedRouteDescription.setText("No routes found with selected route no. please try again later !");
        }
    }

    // Here we create the available routes button for the users
    private void SetupAllRoutesButtons(int i) {
        int routes = 0;
        LinearLayout LinearLayout_RouteSet = findViewById(R.id.LinearLayout_RouteSet);
//        LinearLayout_RouteSet.removeAllViews();
        while (routes < i) {

            routes++;
            // LinearLayout @ Details
            LinearLayout RouteLinearLayout = new LinearLayout(this);
            RouteLinearLayout.setId(routes + 7982);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 20, 0);
//            if (routes == 1) {
//                layoutParams.setMargins(20, 0, 20, 0);
//            }
            RouteLinearLayout.setLayoutParams(findViewById(R.id.Route).getLayoutParams());
            RouteLinearLayout.setOrientation(LinearLayout.VERTICAL);

            // ImageButton @ Details
            ImageButton imageButton = new ImageButton(this);
            imageButton.setId(routes + 2897);
            imageButton.setLayoutParams(findViewById(R.id.RouteNo3ImageButton).getLayoutParams());
            imageButton.setBackground(getDrawable(R.drawable.route_selector));
            imageButton.setCropToPadding(true);
            imageButton.setElevation(2);
            imageButton.setPadding(5, 5, 5, 5);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageButton.setImageResource(R.drawable.routeselectionbus);

            imageButton.setOnClickListener(v -> {
                onRouteSelection(imageButton, i);
//                Toast.makeText(this, "Your Route Number IS : "+ (imageButton.getId()-ImageButtonStarting_ID), Toast.LENGTH_SHORT).show();
            });

            // Route Number TextView
            TextView RouteNumber = new TextView(this);
            RouteNumber.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            RouteNumber.setText("Route " + (routes));
            RouteNumber.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            RouteNumber.setTextSize(17);
            RouteNumber.setTextColor(getResources().getColor(R.color.black));
            RouteNumber.setTypeface(Typeface.DEFAULT_BOLD);


            RouteLinearLayout.addView(imageButton); // Image Button Added Successfully
            RouteLinearLayout.addView(RouteNumber); // Route Number Added Successfully

            LinearLayout_RouteSet.addView(RouteLinearLayout); // Option Developed Successfully

        }

//        LinearLayout_RouteSet.removeView(findViewById(R.id.Route));
    }

    private String SetupRouteDescription(List<String> StoppagePoints) {
        String UpdatedRouteDescription = "";
        for (int i = 0; i < StoppagePoints.size(); i++) {
            UpdatedRouteDescription += StoppagePoints.get(i);
            if (i != StoppagePoints.size() - 1) {
                UpdatedRouteDescription += " - ";
            }
        }
        return UpdatedRouteDescription;
    }


    private void ApplyingDynamicChanges(BusesData FetchedBusData) {
        FinalTravellingData = FetchedBusData;
        TravellingDataAccessed = true;

        String UpdatedRoute = "No routes found with selected route no. please try again later !";

        SetupTextViewDescription(UpdatedRoute, FinalTravellingData);


    }

    private void SetupTextViewDescription(String updatedRoute, BusesData finalTravellingData) {
        String UpdatedRoute = SetupRouteDescription(finalTravellingData.getStations());
        SelectedRouteDescription.setText(UpdatedRoute.toUpperCase(Locale.ROOT));
    }

    public void SettingUpStoppagePoints(int route_number) {

        FirebaseFirestore.getInstance().collection("BUSES")
                .whereEqualTo("Route", route_number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TravellingData = document.toObject(BusesData.class);
                                ApplyingDynamicChanges(TravellingData);
                            }
                        }
                    }
                });

    }

    // Getting ToolBar Logo Button For the functionality of back button
    private View getToolbarLogoView(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programmatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }
}