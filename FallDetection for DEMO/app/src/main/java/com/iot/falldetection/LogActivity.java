package com.iot.falldetection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class LogActivity extends AppCompatActivity {

    private String address = null;

    private ArrayList<LogItem> logItems = new ArrayList<LogItem>();
    private LogAdapter adapter = null;

    TextView notif = null;

    private MenuItem connectBTN = null;
    private MenuItem disconnectBTN = null;

    private LogActivity.Receptor receptor;

    public static final String GETADDRESS = "getaddress";
    public static final String DISCONNECT = "disconnect";
    public static final String CONNECT = "connect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BLEService.SEND_ADDRESS);
        filter.addAction(BLEService.SEND_LOG);
        filter.addAction(BLEService.SEND_LOG_NOTIFICATION);
        filter.addAction(BLEService.SEND_TOAST);

        receptor = new LogActivity.Receptor();
        registerReceiver(receptor, filter);

        if (address == null) {
            sendBroadcast(new Intent(GETADDRESS));
        }

        ListView listView = (ListView) findViewById(R.id.listLog);

        // Initializes log list.
        adapter = new LogAdapter(this, logItems);

        listView.setAdapter(adapter);

        notif = findViewById(R.id.notif);


    }

    public class Receptor extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("l", "comanda log");

            final String action = intent.getAction();

            switch (action) {
                case BLEService.SEND_ADDRESS:
                    address = intent.getStringExtra("address");
                    getSupportActionBar().setTitle("Log --> Connection to:");
                    getSupportActionBar().setSubtitle(address);
                    break;
                case BLEService.SEND_LOG:
                    logItems.add(new LogItem(intent.getStringExtra("logInfo"), intent.getIntExtra("logColor", -1), intent.getBooleanExtra("connection", false)));
                    checkConnection();
                    adapter.notifyDataSetChanged();
                    break;
                case BLEService.SEND_LOG_NOTIFICATION:
                    notif.setText(intent.getStringExtra("logInfo"));
                    break;
                case BLEService.SEND_TOAST:
                    Toasty.success(getApplicationContext(), "Info sent to Firebase. Avalaible on website").show();
                    break;
                default:
                    break;
            }
        }
    }


    private void checkConnection() {
        LogItem last = logItems.get(logItems.size() - 1);

        if (last.isConnection()) {
            connectBTN.setEnabled(false);
            connectBTN.setIcon(R.drawable.ic_baseline_bluetooth_connected_24);

            disconnectBTN.setEnabled(true);
            disconnectBTN.setIcon(R.drawable.ic_baseline_bluetooth_disabled_24);

        } else {
            connectBTN.setEnabled(true);
            connectBTN.setIcon(R.drawable.ic_baseline_bluetooth_connected_24_white);

            disconnectBTN.setEnabled(false);
            disconnectBTN.setIcon(R.drawable.ic_baseline_bluetooth_disabled_24_grey);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logtopbar, menu);

        connectBTN = menu.getItem(0);
        disconnectBTN = menu.getItem(1);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connectDevice:
                sendBroadcast(new Intent(CONNECT));
                return true;

            case R.id.disconnectDevice:
                sendBroadcast(new Intent(DISCONNECT));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ;

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(receptor);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (disconnectBTN.isEnabled() || (!disconnectBTN.isEnabled() && !connectBTN.isEnabled())) {
            Toasty.warning(this, "Disconnect before closing!!", Toasty.LENGTH_LONG, true).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}