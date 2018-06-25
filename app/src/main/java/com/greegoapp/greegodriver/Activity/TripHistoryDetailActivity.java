package com.greegoapp.greegodriver.Activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.greegoapp.greegodriver.Adapter.TripHistoryAdapter;
import com.greegoapp.greegodriver.Model.GetDriverData;
import com.greegoapp.greegodriver.Model.GetTripRate;
import com.greegoapp.greegodriver.R;
import com.greegoapp.greegodriver.Utils.ConnectivityDetector;
import com.greegoapp.greegodriver.Utils.MyProgressDialog;
import com.greegoapp.greegodriver.Utils.SnackBar;
import com.greegoapp.greegodriver.databinding.ActivityTripHistoryDetailBinding;
import com.greegoapp.greegodriver.polilineAnimator.MapHttpConnection;
import com.greegoapp.greegodriver.polilineAnimator.PathJSONParser;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TripHistoryDetailActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    ActivityTripHistoryDetailBinding binding;
    Context context;
    private View snackBarView;
    ImageButton ibback;
    public String trip_id;
    public String pick_up_time, drop_off_time, drive_earning;
    TextView tvStartTime, tvEndTime, tvDriveEarningAmt, tvGreegoFeeAmt, tvTotalAmount, tvTripGreegoTriptime,tvRequestTime;
    public double total;
    TextView tvTitle;
    Double to_lat, to_lng, from_lat, from_lng, distance = null;
    Double lat1, lon1, lat2, lon2;
    Double trip_amount,tip_amount;
    TextView tvTripRecept;
    //sapan
    //GoogleMap 21/4/2018
    GoogleMap googleMap, customGoogleMap;
    MapFragment mapFragment;
    private boolean mLocationPermissionGranted;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    LatLng latLng;
    LocationRequest locationRequest;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;

    //sapan
    //LineDraw
    private List<LatLng> routeList;
    private static final long MOVE_ANIMATION_DURATION = 1000;
    private long TURN_ANIMATION_DURATION = 500;
    private Marker marker;
    Bitmap mMarkerIcon;
    String latlong;
    private int mIndexCurrentPoint = 0;
    private LatLng current_location, dropPoint;
    public static final int PICK_CONTACT_REQUEST = 1;  // The request code
    public static final int ADD_EDIT_VEHICAL_REQUEST = 2000;
    private final int REQ_PERMISSION = 999;
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;
    private String drope_for_user;
    private int id;
    public Double greego_fee_per,greego_fee;
    public String formatted_date;
    private String time,request_time;
    private String account_number;
    public String tripId;

    //priyanka (9-6)
    public TextView tvTipAmount;
    private String transaction_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_history_detail);
        context = TripHistoryDetailActivity.this;
        snackBarView = findViewById(android.R.id.content);
        Bugsnag.init(context);
        bindView();
        setListners();
        setFees();
        getGreegoFee();


       /* tvTripGreegoTriptime.setText();
        tvTitle.setText(formatted_date);*/

        //  tvTotalAmount.setText(String.valueOf(total));
        /*
        in.putExtra("trip_id",tripList.get(tripHistoryPosition).getId());
        in.putExtra("pick_up_time",tripList.get(tripHistoryPosition).getCreated_at());
        in.putExtra("drop_off_time",tripList.get(tripHistoryPosition).getUpdated_at());
        in.putExtra("drive_earning",tripList.get(tripHistoryPosition).getTotal_estimated_trip_cost());
        in.putExtra("distance",historyAdapter.strDistance);
        in.putExtra("greego_fee",ExpressPayActivity.greego_fee);
*/

    }

    private void setFees() {

        Intent i = getIntent();
        trip_id = i.getStringExtra("trip_id");
        transaction_id = i.getStringExtra("transaction_id");
        pick_up_time = i.getStringExtra("start_time");
        drop_off_time = i.getStringExtra("end_time");
        drive_earning = i.getStringExtra("drive_earning");
        request_time = i.getStringExtra("pick_up_time");
        tripId=i.getStringExtra("trip_id");
       /* to_lat = Double.valueOf(i.getStringExtra("to_lat"));
        to_lng = Double.valueOf(i.getStringExtra("to_lng"));        
        from_lat = Double.valueOf(i.getStringExtra("from_lat"));        
        from_lng = Double.valueOf(i.getStringExtra("from_lng"));*/
        time = i.getStringExtra("trip_time");
       /* distance = distance(from_lat, to_lat, from_lng, to_lng);*/
        lat1 = Double.valueOf(i.getStringExtra("from_lat"));
        lat2 =  Double.valueOf(i.getStringExtra("to_lat"));
        lon1 = Double.valueOf(i.getStringExtra("from_lng"));
        lon2 = Double.valueOf(i.getStringExtra("to_lng"));

        trip_amount = Double.valueOf(i.getStringExtra("actual_trip_amount"));
        tip_amount = Double.valueOf(i.getStringExtra("tip_amount"));


        distance = distance(lat1, lon1, lat2, lon2);
        formatted_date = parseDateToddMMyyyy(request_time);

        
        //     total = Double.valueOf(drive_earning) - greego_fee;
        tvTripRecept.setText("(Receipt #:"+trip_id+")");
        tvStartTime.setText(parseDateToHour(pick_up_time));
        tvEndTime.setText(parseDateToHour(drop_off_time));
        tvRequestTime.setText(parseDateToHour(request_time));
//        tvDriveEarningAmt.setText("$"+new DecimalFormat("##.##").format(Double.valueOf(drive_earning)));
       /* tvDriveEarningAmt.setText("$"+new DecimalFormat("##.##").format(trip_amount));
        tvTipAmount.setText("+$"+new DecimalFormat("##.##").format(tip_amount));*/
        tvDriveEarningAmt.setText("$"+String.format("%.2f",trip_amount));
        tvTipAmount.setText("+$"+String.format("%.2f",tip_amount));
        tvTitle.setText(formatted_date);
        //tvTripGreegoTriptime.setText(new DecimalFormat("##.##").format(distance) + "mi " + " " + String.valueOf(time) + "m");
  /*      tvTripGreegoTriptime.setText(new DecimalFormat("##.##").format(Double.valueOf(getIntent().getStringExtra("distance"))) + "mi " + " " + String.valueOf(time));*/
  tvTripGreegoTriptime.setText(String.format("%.2f",Double.valueOf(getIntent().getStringExtra("distance"))) + "mi " + " " + String.valueOf(time));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM dd, h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }/*catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }*/
        return str;
    }

    private void getGreegoFee() {
        GetTripRate tripRateData = new Gson().fromJson(getSharedPreferences("driverData", 0).getString("fee", ""), GetTripRate.class);
        try {
            MyProgressDialog.hideProgressDialog();
            if (tripRateData.getError_code() == 0) {
                greego_fee_per = Double.valueOf(tripRateData.getData().getGreego_fee());
//                double total_greego_fee = (Double.valueOf(drive_earning) * greego_fee_per) / 100;
//                total = Double.valueOf(drive_earning) - total_greego_fee;
                double total_greego_fee = (trip_amount * greego_fee_per) / 100;
                total = trip_amount - total_greego_fee + tip_amount;
             /*   tvGreegoFeeAmt.setText("-$"+new DecimalFormat("##.##").format(total_greego_fee));
                tvTotalAmount.setText("$"+new DecimalFormat("##.##").format(total));*/
                tvGreegoFeeAmt.setText("-$"+String.format("%.2f",total_greego_fee));
                tvTotalAmount.setText("$"+String.format("%.2f",total));
            } else {
                /*         MyProgressDialog.hideProgressDialog();*/
//                SnackBar.showError(context, snackBarView, response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
    }

    public String parseDateToHour(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (Throwable throwable)
        {
            Bugsnag.notify(throwable);
        }
        return str;
    }


    private void setListners() {
        ibback.setOnClickListener(this);
    }

    private void bindView() {
        ibback = binding.ibBack;
        tvStartTime = binding.tvStartTime;
        tvEndTime = binding.tvEndTime;
        tvDriveEarningAmt = binding.tvDriveEarningAmt;
        tvGreegoFeeAmt = binding.tvGreegoFeeAmt;
        tvTotalAmount = binding.tvTotalAmount;
        tvTripGreegoTriptime = binding.tvTripGreegoTriptime;
        tvTitle = binding.tvTitle;
        tvTripRecept = binding.tvTripRecept;
        tvRequestTime = binding.tvRequestTime;
        tvTipAmount = binding.tvTipAmount;
        initmap();


    }

    private void initmap() {
        mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.tripMap, mapFragment);
        fragmentTransaction.commit();


        if (mapFragment != null)
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onMapReady(GoogleMap googleMap1) {
                    googleMap = googleMap1;
                    getLocationPermission();
                    if (mLocationPermissionGranted == true) {
                        if (mGoogleApiClient == null) {
                            setUpGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(false);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        googleMap.getUiSettings().setCompassEnabled(false);
                        MapStyleOptions mapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
                        googleMap.setMapStyle(mapStyle);
                    }

                    LatLng sydney = new LatLng(-33.867, 151.206);
                    LatLng latLng = new LatLng(Double.valueOf(getIntent().getStringExtra("to_lat").toString()), Double.valueOf(getIntent().getStringExtra("to_lng").toString()));
                    LatLng latLng1 = new LatLng(Double.valueOf(getIntent().getStringExtra("from_lat").toString()), Double.valueOf(getIntent().getStringExtra("from_lng").toString()));
                    current_location = latLng;
                    dropPoint = latLng1;
                    createRoute(latLng, latLng1);
                /*googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

                googleMap.addMarker(new MarkerOptions()
                        .title("Sydney")
                        .snippet("The most populous city in Australia.")
                        .position(sydney));*/

                }
            });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
               /* Intent i = new Intent(this,HomeActivity.class);
                startActivity(i);*/
               finish();
                break;
        }
    }


    private synchronized void setUpGoogleApiClient() {
        Log.d("mapFragment", "createGoogleApi()");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private void getLocationPermission() {

        /*     * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.*/

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    if (mLocationPermissionGranted == true) {
                        if (mGoogleApiClient == null) {
                            setUpGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        googleMap.getUiSettings().setCompassEnabled(false);
                    }
                }
            }
        }
//        updateLocationUI();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("mapHomeFragment", "onConnected()");
        getLastKnownLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w("mapHomeFragment", "onConnectionSuspended()");
        mGoogleApiClient.connect();
    }

    // Get last known location
    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        Log.d(".", "getLastKnownLocation()");
        {

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                Log.i(".", "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();


                double lat = lastLocation.getLatitude();
                double lng = lastLocation.getLongitude();

                if (ConnectivityDetector.isConnectingToInternet(context)) {
                    String currentLat = String.valueOf(lat);
                    String currentLong = String.valueOf(lng);
                } else {
                    SnackBar.showInternetError(context,snackBarView);
//                    Toast.makeText(context, "Please Connect Internet", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.w(".", "No location retrieved yet");
                startLocationUpdates();
            }
        }

    }

    // Start location Updates
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Log.i(".", "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }

    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private void writeActualLocation(Location location) {
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(location.getLatitude(), location.getLongitude())).bearing(360).
                zoom(15f).tilt(40).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onLocationChanged(Location location) {
        /*Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;*/
        Log.d(".", "onLocationChanged [" + location + "]");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(".", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //sapan-->location over
    //sapan->line drawing
    private void createRoute(LatLng pickupPoint, LatLng dropPoint) {
        String url = getMapsApiDirectionsUrl(pickupPoint, dropPoint);
        ReadTask downloadTask = new ReadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    //create a url to find a route point
    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    //async to get points
    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }catch (Throwable throwable)
            {
                Bugsnag.notify(throwable);
            }
            return data;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                new ParserTask().execute(result);
            } else {
                Log.e("TAG", "Didn't get response");
            }
        }
    }

    private void linedraw() {
        current_location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        //   dropPoint = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
//                current_location = new LatLng(23.009748,72.56257);
        if (dropPoint == null)
            dropPoint = new LatLng(23.01349, 72.56221);
        createRoute(current_location, dropPoint);
    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
                /*duration = parser.getDuration(jObject);
                String[] separated = duration.split(" ");
                duration1 = separated[0]; // this will contain "Fruit"*/

            } catch (Exception e) {
                e.printStackTrace();
            }catch (Throwable throwable)
            {
                Bugsnag.notify(throwable);
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
//            ArrayList<LatLng> points = null;
            try {
                PolylineOptions polyLineOptions = null;
                Double dist;
                int mapZoomLevel;
                if (routes.size() > 0) {
                    for (int i = 0; i < routes.size(); i++) {
                        routeList = new ArrayList<LatLng>();
                        polyLineOptions = new PolylineOptions();
                        List<HashMap<String, String>> path = routes.get(i);

                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            routeList.add(position);
                        }


                        polyLineOptions.addAll(routeList);
                        polyLineOptions.width(6);
                        polyLineOptions.color(Color.BLACK);
                    }

                    googleMap.clear();
                    googleMap.addPolyline(polyLineOptions);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(current_location);
                    builder.include(dropPoint);

                    LatLngBounds bounds = builder.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int padding = (int) (width * 0.2);

                    /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cameraUpdate);
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);*/

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cameraUpdate);

                    dist = getDistance(lat1,lon1,lat2,lon2);
//                myCalc(current_location.latitude, current_location.longitude, dropPoint.latitude, dropPoint.longitude);
//                price = CalculateMile(String.valueOf(dist), duration1);
//                    tripTotalDistance = dist;
/*
                    if (dist > 2 && dist <= 5) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
                        mapZoomLevel = 12;
                    } else if (dist > 5 && dist <= 10) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
                        mapZoomLevel = 11;
                    } else if (dist > 10 && dist <= 20) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11.0f));
                        mapZoomLevel = 11;
                    } else if (dist > 20 && dist <= 40) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                        mapZoomLevel = 10;
                    } else if (dist > 40 && dist < 100) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(9.0f));
                        mapZoomLevel = 9;
                    } else if (dist > 100 && dist < 200) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
                        mapZoomLevel = 8;
                    } else if (dist > 200 && dist < 400) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(7.0f));
                        mapZoomLevel = 7;
                    } else if (dist > 400 && dist < 700) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f));
                        mapZoomLevel = 7;
                    } else if (dist > 700 && dist < 1000) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));
                        mapZoomLevel = 6;
                    } else if (dist > 1000) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(4.0f));
                        mapZoomLevel = 5;
                    } else {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
                        mapZoomLevel = 14;
                    }
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);*/

                    googleMap.addMarker(new MarkerOptions()
                            .position(current_location)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_start_pin))
                            .title("Current Location")
                            .snippet("Home"));

                    googleMap.addMarker(new MarkerOptions()
                            .position(dropPoint)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_start_pin))
                            .title("Drop Location")
                            .snippet("Driver"));
                } else {
                    Log.e("TAG", "No route found");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }catch (Throwable throwable)
            {
                Bugsnag.notify(throwable);
            }
        }
    }
    public double getDistance(double lat1, double lng1, double lat2, double lng2) {

//        double earthRadius = 3958.75; // in miles,
        double earthRadius = 6371; //for kilometer output
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }



}
