package com.sust.kinblood;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sust.kinblood.directionhelpers.FetchURL;
import com.sust.kinblood.directionhelpers.TaskLoadedCallback;

public class AfterNotifMap extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private String requestStatus_;
    private TextView requestStatus;
    private Polyline polyline;
    private MarkerOptions start=null, end=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_notif_map);

        requestStatus = findViewById(R.id.tv_request_status);

        if (NeedDonorActivity.latLng != null){
            start = new MarkerOptions().position(NeedDonorActivity.latLng);
            end = new MarkerOptions().position(new LatLng(23.667491, 90.3208583));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_user);
        mapFragment.getMapAsync(this);


        HomeActivity.DOCUMENT_REFERENCE.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                requestStatus_ = documentSnapshot.getString("requestStatus");
                if (requestStatus_ != null){
                    if (requestStatus_.equals("negative")){
                        requestStatus.setText(R.string.requestStatus_negative);
                    }else if (requestStatus_.equals("processing")){
                        requestStatus.setText(R.string.requestStatus_processing);
                    }else {
                        if (start != null && end != null) {
                            new FetchURL(AfterNotifMap.this).execute(getUrl(start.getPosition(), end.getPosition()), "driving");
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AfterNotifMap.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getUrl(LatLng origin, LatLng dest) {
        StringBuffer url = new StringBuffer("https://maps.googleapis.com/maps/api/directions/json?origin=");
        url.append(origin.latitude).append(",").append(origin.longitude)
                .append("&destination=").append(dest.latitude).append(",").append(dest.longitude).append("&key=")
                .append(getString(R.string.google_maps_key));
        return url.toString();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (start != null && end != null){
            mMap.addMarker(start);
            mMap.addMarker(end).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_donor));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(start.getPosition());
            builder.include(end.getPosition());
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if(polyline != null)
            polyline.remove();
            polyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}