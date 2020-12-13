package com.iot.falldetection;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;

public class BLEService extends Service {

    private ArrayList<BluetoothDevice> btDevices = null;
    private ArrayList<Integer> receivedInformation = new ArrayList<Integer>();
    private ArrayList<Integer> sentInformationtoFirebase = new ArrayList<Integer>();

    BluetoothManager btManager = null;
    BluetoothAdapter btAdapter = null;
    BluetoothLeScanner btScanner = null;

    BluetoothGatt btGatt = null;
    BluetoothGattCallback gattCallback = null;

    BluetoothGattService gattService = null;
    BluetoothGattCharacteristic gattCharacteristic = null;
    BluetoothGattDescriptor gattDescriptor = null;

    BluetoothDevice selectedBT = new BluetoothDevice("TEST", "TEST");

    private boolean mScanning;
    private Handler handler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private final String tag = getClass().getSimpleName(); // Nom de la classe

    public static final String SEND_DEVICES = "senddevices"; // Acció
    public static final String CHANGE_BTN_STATUS = "changebtnstatus"; // Acció
    public static final String SEND_ADDRESS = "sendaddress"; // Acció
    public static final String SEND_LOG = "sendlog"; // Acció
    public static final String SEND_LOG_NOTIFICATION = "sendlognotification"; // Acció

    public static final String RECEIVEDINFORMATIONCSEND = "receivedinformationsend"; // Acció
    public static final String UPDATEUI = "updateUI"; // Acció
    public static final String SEND_TOAST = "sendToast"; // Acció


    private BLEService.Receptor receptor;

    private boolean isConnected = false;

    private DatabaseReference myDatabase;
    private FirebaseAuth myAuth;

    private ArrayList<Integer> rec;
    private Integer index = 42;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        final IntentFilter filter = new IntentFilter();

        filter.addAction(MainActivity.START_SEARCH);
        filter.addAction(MainActivity.SEND_SELECTED_DEVICE);

        filter.addAction(LogActivity.GETADDRESS);
        filter.addAction(LogActivity.CONNECT);
        filter.addAction(LogActivity.DISCONNECT);

        receptor = new BLEService.Receptor();
        registerReceiver(receptor, filter);

        InitializationCallback();

        myDatabase = FirebaseDatabase.getInstance().getReference();

        myAuth = FirebaseAuth.getInstance();

        myAuth.signInWithEmailAndPassword("app@fall.com", "appfalldetectionIOT");

        rec = new ArrayList<>();

        Log.e("s", "iniciado");
    }

    private class Receptor extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("s", "comanda");

            final String action = intent.getAction();

            switch (action) {
                case MainActivity.START_SEARCH:
                    startSearch();
                    break;
                case MainActivity.SEND_SELECTED_DEVICE:
                    selectedDevice(intent.getIntExtra("int", -1));
                    break;
                case LogActivity.GETADDRESS:
                    sendAddress();
                    break;
                case LogActivity.CONNECT:
                    connectDevice();
                    break;
                case LogActivity.DISCONNECT:
                    disconnectDevice();
                    break;
                default:
                    break;

            }

        }
    }

    private void startSearch() {

        btDevices = new ArrayList<BluetoothDevice>();

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        if (btAdapter != null) {
            btScanner = btAdapter.getBluetoothLeScanner();
        }

        if (!mScanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.e("s", "stopped");
                    btScanner.stopScan(leScanCallback);
                    sendBroadcast(new Intent(CHANGE_BTN_STATUS));
                }
            }, SCAN_PERIOD);

            mScanning = true;
            btScanner.startScan(leScanCallback);
            sendBroadcast(new Intent(CHANGE_BTN_STATUS));
        }


    }

    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    boolean duplicate = false;
                    for (BluetoothDevice d : btDevices) {
                        if (d.getDeviceAddress().equals(result.getDevice().getAddress()))
                            duplicate = true;
                    }
                    if (!duplicate) {
                        String name = "";
                        if (result.getDevice().getName() == null) {
                            name = "--";
                        } else {
                            name = result.getDevice().getName();
                        }

                        BluetoothDevice device = new BluetoothDevice(name, result.getDevice().getAddress());
                        device.setDevice(result.getDevice());

                        final Intent answer = new Intent(SEND_DEVICES);

                        btDevices.add(device);

                        answer.putExtra("array", device);

                        sendBroadcast(answer);
                        Log.e("g", "working");
                    }
                }
            };


    private void selectedDevice(int i) {
        if (i != -1) {
            selectedBT = btDevices.get(i);
            btGatt = selectedBT.device.connectGatt(this, false, gattCallback);
        }
    }

    private void sendAddress() {
        if (selectedBT != null) {
            Intent intent = new Intent(SEND_ADDRESS);
            intent.putExtra("address", selectedBT.deviceAddress);

            sendBroadcast(intent);
        }
    }

    private void InitializationCallback() {
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        isConnected = true;
                        sendLogInformation("Connected successfully to: " + gatt.getDevice().getAddress(), 3, isConnected);
                        gatt.discoverServices();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        isConnected = false;
                        sendLogInformation("Disconnected successfully from: " + gatt.getDevice().getAddress(), 3, isConnected);
                        gatt.close();
                    }
                } else {
                    isConnected = false;
                    sendLogInformation("Error trying to connect to: " + gatt.getDevice().getAddress(), 2, isConnected);
                    gatt.close();
                }
            };

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {

                if (gatt.getServices().isEmpty()) {
                    sendLogInformation("No services available", 1, isConnected);
                } else {
                    UUID ServiceUUID = UUID.fromString("157ece0d-a1d0-4b56-9ee0-3304b24d59a8"); //SERVICE UUID CREATED FOR THIS USAGE
                    UUID CharacteristicUUID = UUID.fromString("ef3d230b-b091-415a-8694-1b7eb7b1739f"); //CHARACTERISTIC UUID CREATED FOR THIS USAGE
                    UUID DescriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); //DESCRIPTOR NEEDED FOR READING NOTIFICATIONS

                    gattService = btGatt.getService(ServiceUUID);
                    gattCharacteristic = gattService.getCharacteristic(CharacteristicUUID);
                    gattDescriptor = gattCharacteristic.getDescriptor(DescriptorUUID);

                    //NEEDED FOR ADDING CENTRAL AS A SUBSCRIPTOR TO THE CHARACTERISTIC NOTIFICATIONS
                    btGatt.setCharacteristicNotification(gattCharacteristic, true);
                    gattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(gattDescriptor);

                }
                super.onServicesDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                sendNotification("Notification: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));

                manageInformation(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0));
            }
        };


    }

    private void manageInformation(int a){
        rec.add(a);

        if(rec.size()==3){
            myDatabase.child(index.toString()).child("max").setValue(rec.get(0));
            myDatabase.child(index.toString()).child("med").setValue(rec.get(1));
            myDatabase.child(index.toString()).child("min").setValue(rec.get(2));

            rec.clear();
            index++;
            Log.e("s", "Info sent");
            sendBroadcast(new Intent(SEND_TOAST));
        }
    }

    private void sendNotification(String info){
        Log.e("s", info);

        Intent answer = new Intent(SEND_LOG_NOTIFICATION);
        answer.putExtra("logInfo", info);
        sendBroadcast(answer);
    }

    private void sendLogInformation(String info, int type, boolean connected) {
        Log.e("s", info);

        Intent answer = new Intent(SEND_LOG);
        answer.putExtra("logInfo", info);
        answer.putExtra("logColor", type);
        answer.putExtra("connection", connected);
        sendBroadcast(answer);
    }

    private void connectDevice() {
        Log.e("s", "trying");
        btGatt.close();
        btGatt = selectedBT.device.connectGatt(this, false, gattCallback);
    }

    private void disconnectDevice() {
        btGatt.disconnect();
    }

}
