package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hitomi.cmlibrary.CircleMenu;
import com.sust.kinblooddemo.notification.APIService;
import com.sust.kinblooddemo.notification.Client;
import com.sust.kinblooddemo.notification.Data;
import com.sust.kinblooddemo.notification.MyResponse;
import com.sust.kinblooddemo.notification.NotificationSender;
import com.sust.kinblooddemo.notification.Token;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity implements OnMapReadyCallback {


    private String uid;
    private DocumentReference DOCUMENT_REFERENCE;

    private Dialog toastMessageDialog, locationDialog, permanentDenyDialog;
    private int bG = 0;
    private APIService apiService;
    private EditText condition, noOfBags;
    private String locationName_, locationAddress_, condition_, noOfBags_, blood_group;
    private String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private GoogleMap mMap;
    private LatLng latLng;
    private TextView locationAddress;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DOCUMENT_REFERENCE = FirebaseFirestore.getInstance().collection("Users").document(uid);

        toastMessageDialog = new Dialog(NotificationActivity.this);
        locationDialog = new Dialog(this);
        permanentDenyDialog = new Dialog(this);

        CircleMenu circleMenu = findViewById(R.id.circleMenu);
        EditText searchLocation = findViewById(R.id.map_search);
        locationAddress = findViewById(R.id.selected_location);
        condition = findViewById(R.id.et_condition);
        noOfBags = findViewById(R.id.et_noOfBags);
        ImageView home = findViewById(R.id.iv_home);
        TextView selectedBloodGroup = findViewById(R.id.tv_blood);
        Button send = findViewById(R.id.btn_sendNotif);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        home.setOnClickListener(view -> {
            startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
            finish();
        });

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
                    Toast.makeText(NotificationActivity.this, blood_group + " selected", Toast.LENGTH_LONG).show();
                    selectedBloodGroup.setText(blood_group);
                });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_notif);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Places.initialize(getApplicationContext(), "AIzaSyDdC5d5BNuQ8vCFIMC_QjZtgm4DcCKhnqs");

        searchLocation.setFocusable(false);
        searchLocation.setOnClickListener(view -> {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(NotificationActivity.this);
            startActivityForResult(intent, 304);

        });

        send.setOnClickListener(v -> {
            if (isOnline()) {
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
                    Toast.makeText(NotificationActivity.this, "Please select your blood group", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("Tokens").child("5OF7T9cyDHXfvvQor8P1weYZ1qw2").child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userToken = dataSnapshot.getValue(String.class);
                        DOCUMENT_REFERENCE.get().addOnSuccessListener(documentSnapshot -> {
                            SignupHelper signupHelper = documentSnapshot.toObject(SignupHelper.class);
                            String fullName = signupHelper.getFullName();
                            String phoneNumber = signupHelper.getPhoneNumber();
                            //sendNotifications(userToken, blood_group, hospital_, condition_, noOfBags_, uid, fullName, phoneNumber);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(NotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
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

    public void sendNotifications(String userToken, String bloodGroup, String hospital, String condition, String noOfBags, String uid, String fullName, String phoneNumber) {
        Data data = new Data(bloodGroup, hospital, condition, noOfBags, uid, fullName, phoneNumber);
        NotificationSender sender = new NotificationSender(data, userToken);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                Log.d("fire", "Response: " + response.toString());
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().success != 1) {
                            Toast.makeText(NotificationActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(NotificationActivity.this, AfterNotifTemp.class));
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
                    Geocoder geocoder = new Geocoder(NotificationActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    locationName_ = addresses.get(0).getLocality();
                    locationAddress_ = addresses.get(0).getAddressLine(0);
                    locationAddress.setText(locationAddress_);
                    CameraUpdate currentLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
                    mMap.animateCamera(currentLocation);
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Location couldnt be received", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(NotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showMessageInfo();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 501);
        }
    }

    @SuppressLint("MissingPermission")
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
                if (!ActivityCompat.shouldShowRequestPermissionRationale(NotificationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessagePermanentDeny();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null){
                getCurrentLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 304 && resultCode == RESULT_OK) {
            assert data != null;
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationName_ = place.getName();
            latLng = place.getLatLng();
            locationAddress_ = place.getAddress();
            locationAddress.setText(locationAddress_);
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current location: " + locationName_));
            CameraUpdate currentLocation = CameraUpdateFactory.newLatLngZoom(latLng, 9.0f);
            mMap.animateCamera(currentLocation);
        } else {
            assert data != null;
            Status status = Autocomplete.getStatusFromIntent(data);
            assert status.getStatusMessage() != null;
            Log.d("Statx", status.getStatusMessage());
        }
    }

    private void showMessageInfo() {
        locationDialog.setContentView(R.layout.item_location_dialog);
        locationDialog.setCanceledOnTouchOutside(false);

        TextView continueButton = locationDialog.findViewById(R.id.btn_continue_location);

        continueButton.setOnClickListener(view -> {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 501);
        });

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

}
