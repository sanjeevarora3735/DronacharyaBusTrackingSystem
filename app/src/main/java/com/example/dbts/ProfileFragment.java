package com.example.dbts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static View view;
    private ScholarData FetchedScholarData;
    private FirebaseAuth mAuth;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout InviteFriendsArrow;
    private LinearLayout HelpSupportArrow;
    private LinearLayout PricingArrow;
    private LinearLayout SettingsArrow;
    private ConstraintLayout InviteFriendsConstraintLayout;
    private ConstraintLayout HelpSupportConstraintLayout;
    private ConstraintLayout PricingArrowConstraintLayout;
    private ConstraintLayout SettingsConstraintLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        }
        mAuth = FirebaseAuth.getInstance();
        UpdateProfileData();

        //InviteFriends Linear Layout :
        InviteFriendsArrow = view.findViewById(R.id.InviteFriendsArrow);
        HelpSupportArrow = view.findViewById(R.id.HelpSupportArrow);
        SettingsArrow = view.findViewById(R.id.SettingsArrow);
        PricingArrow = view.findViewById(R.id.PricingArrow);

        // Constraint Layouts For Profile-onClick Activities:
        InviteFriendsConstraintLayout = view.findViewById(R.id.InviteFriendsConstraintLayout);
        HelpSupportConstraintLayout = view.findViewById(R.id.HelpSupportConstraintLayout);
        SettingsConstraintLayout = view.findViewById(R.id.SettingsConstraintLayout);
        PricingArrowConstraintLayout = view.findViewById(R.id.PricingArrowConstraintLayout);


        InviteFriendsConstraintLayout.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Maintenance.class));
        });

        InviteFriendsArrow.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Maintenance.class));
        });

        HelpSupportConstraintLayout.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Maintenance.class));
        });

        HelpSupportArrow.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Maintenance.class));
        });

        SettingsConstraintLayout.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Maintenance.class));
        });

        SettingsArrow.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Maintenance.class));
        });
        PricingArrow.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Maintenance.class));
        });

        PricingArrowConstraintLayout.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Maintenance.class));
        });


        // Inflate the layout for this fragment
        return view;
    }


    private void SaveUserInformation(ScholarData xScholarData) {
        FetchedScholarData = xScholarData;
        Toast.makeText(getContext(), FetchedScholarData.toString(), Toast.LENGTH_SHORT).show();
    }

    private void UpdateProfileData() {
        String UserDocumentID = null;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            UserDocumentID = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("identifying_information").document(UserDocumentID);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ScholarData CurrentScholarDataObj = documentSnapshot.toObject(ScholarData.class);
                }
            });
        } else {
            // Information Not Found or Wrong Document ID
            Toast.makeText(getActivity(), "Sorry Unable to find the information... Try again later ", Toast.LENGTH_SHORT).show();
        }
    }
}