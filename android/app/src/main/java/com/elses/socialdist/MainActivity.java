package com.elses.socialdist;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BLUETOOTH = 2;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleUserPermission();
        enableBluetooth();
        handleBeaconServices();
    }

    private void handleBeaconServices(){
        BeaconReferenceApplication application= ((BeaconReferenceApplication) this.getApplication());
        application.enableMonitoring();
        beaconManager = BeaconManager.getInstanceForApplication(this);
    }
    private void enableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }
    private void handleUserPermission(){
        if (this.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_FINE_LOCATION);
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_REQUEST_BLUETOOTH);
        } else {
            //TODO
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        enableBluetooth();
        beaconManager.bind(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for(Beacon beacon : beacons){
                        List<String> beaconMeta = new ArrayList<>();
                        beaconMeta.add(beacon.getId1().toString());
                        beaconMeta.add(beacon.getId2().toString());
                        beaconMeta.add(beacon.getId3().toString());
                        beaconMeta.add(String.valueOf(beacon.getRssi()));
                        //if(beacon.getId1().toString().compareTo("11111111-1111-1111-1111-111111111111")==0)
                        Log.i("","beacon details: id1="+beacon.getId1()+" id2="+beacon.getId2()+" id3="+beacon.getId3()+" rssi="+beacon.getRssi()+" distance = "+beacon.getDistance());
                    }
                }
            }
        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {
        }
    }
}
