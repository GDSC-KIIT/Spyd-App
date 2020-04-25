package com.example.signal;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    TelephonyManager tele;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    TextView networkProvider;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    private int signal = 0;
    String type = "";
    final String[] st = {""};
    private LocationManager locationManager;
    private LocationListener locationListener;
    String latitude_db="0";
    String longitude_db="0";
    DatabaseReference reff;
    Location loc;
    private String networkProviderUser = "";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loc = new Location();

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        type = getNetworkClass();
        System.out.println("The type is " + type);

        final TextView textView = (TextView) findViewById(R.id.wifitext);
        System.out.println("Signal strength is : " + signal);

        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        System.out.println(linkSpeed + " dBm");
        textView.setText(linkSpeed + " dBm");

        Button fetch = (Button) findViewById(R.id.button2);
        networkProvider = findViewById(R.id.np);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationTrack = new LocationTrack(MainActivity.this);


                if (locationTrack.canGetLocation()) {


                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();

                    latitude_db = Double.toString(latitude);
                    longitude_db = Double.toString(longitude);

                    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                } else {

                    locationTrack.showSettingsAlert();
                }

            }
        });

        tele = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        } else {
            networkProviderUser = tele.getNetworkOperatorName();
            networkProvider.setText(""+tele.getNetworkOperatorName());
        }
        reff = FirebaseDatabase.getInstance().getReference().child(networkProviderUser);
    }


    public void showStrength(View v) {
        final String strengthType = type;
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength strength) {
                super.onSignalStrengthsChanged(strength);
                TextView textView = (TextView) findViewById(R.id.text);
                TextView textView1 = (TextView) findViewById(R.id.stext);
                if (strength.isGsm()) {
                    String[] parts = strength.toString().split(" ");
                    String signalStrength = "";
                    int currentStrength = strength.getGsmSignalStrength();
                    if (currentStrength <= 0) {
                        if (currentStrength == 0) {
                            signalStrength = String.valueOf(Integer.parseInt(parts[3]));
                        } else {
                            signalStrength = String.valueOf(Integer.parseInt(parts[1]));
                        }
                        st[0] = signalStrength;
                        signalStrength += " dBm";
                    } else {
                        if (currentStrength != 99) {
                            signalStrength = String.valueOf(((2 * currentStrength) - 113));
                            st[0] = signalStrength;
                            signalStrength += " dBm";
                        }
                    }
                    //signal = (2 * signal) - 113;
                    System.out.println("Signal strength is : " + st[0]);
                    System.out.println("-------------->"+ strengthType);
                    System.out.println("Signal strength is : " + signalStrength);

                    String s = st[0];
                    int i = Integer.parseInt(s);
                    i = Math.abs(i);
                    System.out.println("Integer value: "+i);

                    switch (strengthType) {
                        case "2G":
                            if (i >= 70)
                                textView1.setText("Excellent");
                            else if(i >= 71 && i <= 85)
                                textView1.setText("Good");
                            else if(i>= 86 && i<= 100)
                                textView1.setText("Fair");
                            else {
                                textView1.setText("Poor");
                                loc.setLongitude_db(longitude_db);
                                loc.setLatitude_db(latitude_db);
                                reff.push().setValue(loc);
                            }
                            break;
                        case "3G":
                            if (i == 70) {
                                textView1.setText("Excellent");
                            } else if(i >= 71 && i <= 85)
                                textView1.setText("Good");
                            else if(i>= 86 && i<= 100)
                                textView1.setText("Fair");
                            else {
                                textView1.setText("Poor");
                                loc.setLongitude_db(longitude_db);
                                loc.setLatitude_db(latitude_db);
                                reff.push().setValue(loc);
                            }
                            break;
                        case "4G":
                            if (i >= 90)
                                textView1.setText("Excellent");
                            else if(i >= 91 && i <= 105)
                                textView1.setText("Good");
                            else if(i>= 106 && i<= 110)
                                textView1.setText("Fair");
                            else {
                                textView1.setText("Poor");
                                loc.setLongitude_db(longitude_db);
                                loc.setLatitude_db(latitude_db);
                                reff.push().setValue(loc);
                            }
                            break;
                    }
                    textView.setText(signalStrength + "");
                } else {
                    textView.setText("Not a gsm signal.");
                }
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

    }

    public String getNetworkClass() {
        String t="3G";
        final TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        TextView textView = (TextView) findViewById(R.id.typetext);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                textView.setText("2G");
                t = "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                textView.setText("3G");
                t = "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                textView.setText("4G");
                t = "4G";
        }

        return t;
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    networkProviderUser = tele.getNetworkOperatorName();
                    networkProvider.setText(""+tele.getNetworkOperatorName());
                }
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }

}