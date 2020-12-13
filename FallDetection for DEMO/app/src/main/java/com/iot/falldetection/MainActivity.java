package com.iot.falldetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final static String titleAddress = "TITLE_ADDRESS";

    private ArrayList<BluetoothDevice> btDevices = new ArrayList<BluetoothDevice>();
    private DeviceAdapter adapter = null;
    private MainActivity.Receptor receptor;

    private boolean showTextEmpty = true;
    private TextView textViewEmpty = null;
    private ProgressDialog progressDialog = null;

    private boolean checked = true; //true --> Address // false --> Name

    private boolean textBtn = true; //true --> Search // false --> Searching...

    public static final String START_SEARCH = "searchdevices";
    public static final String SEND_SELECTED_DEVICE = "sendselecteddevice";

    Button searchDevices = null;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(MainActivity.this, BLEService.class));

        ListView listView = (ListView) findViewById(R.id.listDevices);

        textViewEmpty = (TextView) findViewById(R.id.empty);

        getSupportActionBar().setTitle("Fall Detection IoT APP");

        // Initializes Bluetooth list.
        adapter = new DeviceAdapter(this, btDevices);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent1 = new Intent(SEND_SELECTED_DEVICE);
                intent1.putExtra("int", position);
                sendBroadcast(intent1);

                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                startActivity(intent);
            }
        });

        searchDevices = (Button) findViewById(R.id.searchButton);

        searchDevices.setText("Search devices");

        searchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textBtn) {
                    btDevices.clear();
                    sendBroadcast(new Intent(START_SEARCH));
                    Log.e("r", "enviado");
                }
            }
        });

        RadioGroup rdb = (RadioGroup) findViewById(R.id.rdbToShow);

        rdb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdbName) {
                    checked = false;
                    for (int i = 0; i < btDevices.size(); i++) {
                        btDevices.get(i).setTextToShowToName();
                    }
                    adapter.notifyDataSetChanged();
                } else if (checkedId == R.id.rdbAddress) {
                    checked = true;
                    for (int i = 0; i < btDevices.size(); i++) {
                        btDevices.get(i).setTextToShowToAddress();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BLEService.SEND_DEVICES);
        filter.addAction(BLEService.CHANGE_BTN_STATUS);

        receptor = new Receptor();
        registerReceiver(receptor, filter);

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.

        if (((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter() == null || !((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }


    private class Receptor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            switch (action) {
                case BLEService.SEND_DEVICES:
                    Log.e("r", "recibido device");

                    final BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getSerializableExtra("array");

                    if (!checked) {
                        bluetoothDevice.setTextToShowToName();
                    }

                    btDevices.add(bluetoothDevice);

                    if (showTextEmpty) {
                        showTextEmpty = false;
                        textViewEmpty.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();
                    break;
                case BLEService.CHANGE_BTN_STATUS:
                    Log.e("r", "recibido text");
                    if (textBtn) {
                        textBtn = false;
                        searchDevices.setText("Searching...");
                        searchDevices.setEnabled(false);
                    } else {
                        textBtn = true;
                        searchDevices.setText("Search devices");
                        searchDevices.setEnabled(true);
                    }
                    break;
                default:
                    break;

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(receptor);
        super.onDestroy();
    }

}