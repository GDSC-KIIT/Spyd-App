package com.example.signal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    private int signal = 0;
    String type = "";
    final String[] st = {""};
    private LocationManager locationManager;
    private LocationListener locationListener;
    double latitude = 0.0;
    double longitude = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        type = getNetworkClass();
        System.out.println("---------------------------------");
        System.out.println("The type is " + type);
        System.out.println("---------------------------------");

        final TextView textView = (TextView) findViewById(R.id.wifitext);
        System.out.println("Signal strength is : " + signal);

        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        System.out.println(linkSpeed + " dBm");
        textView.setText(linkSpeed + " dBm");



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
                    System.out.println("---------------------------------");
                    System.out.println("Signal strength is : " + st[0]);
                    System.out.println("-------------->"+ strengthType);
                    System.out.println("---------------------------------");
                    System.out.println("---------------------------------");

                    System.out.println("Signal strength is : " + signalStrength);
                    System.out.println("---------------------------------");
                    System.out.println("---------------------------------");

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
                            }
                            break;
                        case "3G":
                            if (i == -70)
                                textView1.setText("Excellent");
                            else if(i >= 71 && i <= 85)
                                textView1.setText("Good");
                            else if(i>= 86 && i<= 100)
                                textView1.setText("Fair");
                            else {
                                textView1.setText("Poor");
                            }
                            break;
                        case "4G":
                            if (i == -90)
                                textView1.setText("Excellent");
                            else if(i >= 91 && i <= 105)
                                textView1.setText("Good");
                            else if(i>= 106 && i<= 110)
                                textView1.setText("Fair");
                            else {
                                textView1.setText("Poor");
                                System.out.println("------------");
                                System.out.println(latitude);
                                System.out.println(longitude);
                                System.out.println("------------");
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

    public void getStrengthClass(String checking1, String type1) {

        TextView textView = (TextView) findViewById(R.id.stext);


        int i = Integer.parseInt(checking1);
        System.out.println("Integer value "+i);

        switch (type1) {
            case "2G":
                if (i >= -70)
                    textView.setText("Excellent");
                else if(i >= -71 && i <= -85)
                    textView.setText("Good");
                else if(i>= -86 && i<= -100)
                    textView.setText("Fair");
                else {
                    textView.setText("Poor");
                }
                break;
            case "3G":
                if (i == -70)
                    textView.setText("Excellent");
                else if(i >= -71 && i <= -85)
                    textView.setText("Good");
                else if(i>= -86 && i<= -100)
                    textView.setText("Fair");
                else {
                    textView.setText("Poor");
                }
                break;
            case "4G":
                if (i == -90)
                    textView.setText("Excellent");
                else if(i >= -91 && i <= -105)
                    textView.setText("Good");
                else if(i>= -106 && i<= -110)
                    textView.setText("Fair");
                else {
                    textView.setText("Poor");
                }
                break;
        }
    }

}