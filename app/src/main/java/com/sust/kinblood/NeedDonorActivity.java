package com.sust.kinblood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hitomi.cmlibrary.CircleMenu;
import com.sust.kinblood.notification.APIService;
import com.sust.kinblood.notification.Client;
import com.sust.kinblood.notification.Data;
import com.sust.kinblood.notification.MyResponse;
import com.sust.kinblood.notification.NotificationSender;
import com.sust.kinblood.notification.Token;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NeedDonorActivity extends AppCompatActivity implements OnMapReadyCallback {


    private String uid;
    private DocumentReference DOCUMENT_REFERENCE;
    private FirebaseFirestore DATABASE_REFERENCE;

    private Dialog toastMessageDialog, locationDialog, permanentDenyDialog;
    private int bG = 0;
    private APIService apiService;
    private EditText condition, noOfBags, searchLocation;
    private String locationName_, locationAddress_, condition_, noOfBags_, blood_group;
    private String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private GoogleMap mMap;
    public static LatLng latLng;
    private TextView locationAddress;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_donor);

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DATABASE_REFERENCE = FirebaseFirestore.getInstance();
        DOCUMENT_REFERENCE = DATABASE_REFERENCE.collection("Users").document(uid);

        toastMessageDialog = new Dialog(NeedDonorActivity.this);
        locationDialog = new Dialog(this);
        permanentDenyDialog = new Dialog(this);

        CircleMenu circleMenu = findViewById(R.id.activity_need_donor_CircleMenu);
        searchLocation = findViewById(R.id.activity_need_donor_searchMap_EditText);
        locationAddress = findViewById(R.id.activity_need_donor_selected_location_TextView);
        condition = findViewById(R.id.activity_need_donor_condition_TextInputEditText);
        noOfBags = findViewById(R.id.activity_need_donor_noOfBags_TextInputEditText);
        ImageView home = findViewById(R.id.activity_need_donor_home_ImageView);
        TextView selectedBloodGroup = findViewById(R.id.activity_need_donor_selected_blood_TextView);
        Button send = findViewById(R.id.activity_need_donor_sendNotification_Button);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        home.setOnClickListener(view -> finish());

        circleMenu.setMainMenu(Color.parseColor("#EEF4DC"), R.drawable.ic_blood, R.drawable.ic_app_logo_40dp)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.a_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.a_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.b_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.b_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.ab_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.ab_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.o_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.o_negative)
                .setOnMenuSelectedListener(index -> {
                    bG = 1;
                    blood_group = bloodGroups[index];
                    Toast.makeText(NeedDonorActivity.this, blood_group + " selected", Toast.LENGTH_LONG).show();
                    selectedBloodGroup.setText(blood_group);
                });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_need_donor_map_Fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Places.initialize(NeedDonorActivity.this, getResources().getString(R.string.google_maps_key));

        searchLocation.setFocusable(false);
        searchLocation.setOnClickListener(view -> {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("BD").build(NeedDonorActivity.this);
            startActivityForResult(intent, 304);

        });

        send.setOnClickListener(v -> {
            if (isOnline()) {
                if (latLng == null){
                    Toast.makeText(this, "Missing location info", Toast.LENGTH_SHORT).show();
                    return;
                }
                condition_ = condition.getText().toString().trim();
                if (condition_.isEmpty()) {
                    condition.setError("Field cannot be empty");
                    condition.requestFocus();
                    return;
                }
                noOfBags_ = noOfBags.getText().toString().trim();
                if (noOfBags_.isEmpty()) {
                    noOfBags.setError("Field cannot be empty");
                    noOfBags.requestFocus();
                    return;
                }
                if (bG == 0) {
                    Toast.makeText(NeedDonorActivity.this, "Please select your blood group", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("Tokens").child("5OF7T9cyDHXfvvQor8P1weYZ1qw2").child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userToken = dataSnapshot.getValue(String.class);
                        DOCUMENT_REFERENCE.get().addOnSuccessListener(documentSnapshot -> {
                            SignupHelper signupHelper = documentSnapshot.toObject(SignupHelper.class);
                            if (signupHelper != null) {
                                String fullName = signupHelper.getFullName();
                                String phoneNumber = signupHelper.getPhoneNumber();
                                DATABASE_REFERENCE
                                        .collection("Requests")
                                        .document(uid)
                                        .set(new Data(blood_group, locationName_, locationAddress_, condition_, noOfBags_, uid, fullName, phoneNumber, latLng))
                                        .addOnSuccessListener(aVoid -> sendNotifications(userToken, blood_group, locationName_, condition_, noOfBags_))
                                        .addOnFailureListener(e -> Toast.makeText(NeedDonorActivity.this, "An error occured", Toast.LENGTH_SHORT).show());
                            }
                        }).addOnFailureListener(e -> Toast.makeText(NeedDonorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            } else {
                showToastMessage("No Internet Connection!");
            }
        });

        updateToken();
    }

    private void updateToken() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(uid).setValue(token);
    }

    public void sendNotifications(String userToken, String bloodGroup, String locationName, String condition, String noOfBags) {
        Data data = new Data(bloodGroup, locationName, condition, noOfBags);
        NotificationSender sender = new NotificationSender(data, userToken);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().success != 1) {
                            Toast.makeText(NeedDonorActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(NeedDonorActivity.this, AfterNotifMap.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showToastMessage(final String message) {
        toastMessageDialog.setContentView(R.layout.item_toast_message);
        toastMessageDialog.setCanceledOnTouchOutside(true);

        TextView toast_TextView = toastMessageDialog.findViewById(R.id.item_toast_message_toast_TextView);
        toast_TextView.setText(message);

        toastMessageDialog.show();
        Objects.requireNonNull(toastMessageDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        new Handler().postDelayed(() -> toastMessageDialog.cancel(), 5000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(() -> {
            getCurrentLocation();
            return true;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        if (mMap != null){
            getCurrentLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        if (!mMap.isMyLocationEnabled()){
            mMap.setMyLocationEnabled(true);
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(NeedDonorActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    locationName_ = addresses.get(0).getLocality();
                    locationAddress_ = addresses.get(0).getAddressLine(0);
                    updateUi();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Location couldnt be received", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(NeedDonorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showMessageInfo();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 501);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 501) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationDialog.dismiss();
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            } else {
                locationDialog.dismiss();
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(NeedDonorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessagePermanentDeny();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 304 && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationName_ = place.getName();
            latLng = place.getLatLng();
            locationAddress_ = place.getAddress();
            updateUi();
        } else {
            if (data != null){
                Status status = Autocomplete.getStatusFromIntent(data);
                if (status.getStatusMessage() != null){
                    Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showMessageInfo() {
        locationDialog.setContentView(R.layout.item_location_dialog);
        locationDialog.setCanceledOnTouchOutside(false);

        TextView continueButton = locationDialog.findViewById(R.id.btn_continue_location);

        continueButton.setOnClickListener(view -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 501));

        locationDialog.show();
        Objects.requireNonNull(locationDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void showMessagePermanentDeny() {
        permanentDenyDialog.setContentView(R.layout.item_permanent_deny_dialog);
        permanentDenyDialog.setCanceledOnTouchOutside(false);

        TextView settings = permanentDenyDialog.findViewById(R.id.btn_settings_location);
        TextView cancel = permanentDenyDialog.findViewById(R.id.btn_cancel_location);

        settings.setOnClickListener(view -> {
            permanentDenyDialog.dismiss();
            gotoApplicationSettings();
        });
        cancel.setOnClickListener(view -> permanentDenyDialog.dismiss());

        permanentDenyDialog.show();
        Objects.requireNonNull(permanentDenyDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void gotoApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null){
                getCurrentLocation();
            }
        }
    }

    public void updateUi(){
        mMap.clear();
        locationAddress.setText(locationAddress_);
        searchLocation.setText(locationName_);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Need donor to come here"));
        CameraUpdate currentLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
        mMap.animateCamera(currentLocation);
    }
}
