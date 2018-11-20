package kth.ess_androidapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.widget.ListViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_ENABLE_BT = 1;
    
    private UUID controlUUID = new UUID(0x11,0x21);
    
    private BluetoothSocket controlSocket;
    
    private void openControlView(){
        Intent intent = new Intent(this, ControlActivity.class);
        startActivity(intent);
    }

    private BluetoothConnector bluetoothConnector = null;
    private void openBluetoothConnection(UUID uuid, BluetoothConnector.BluetoothConnectorCallback callback){
        if(bluetoothConnector != null)
            bluetoothConnector.stop();
        bluetoothConnector = new BluetoothConnector(this, uuid, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.viewButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openControlView();
            }
        });

        Button controllerBTButton = findViewById(R.id.btControlButton);
        controllerBTButton.setOnClickListener(new View.OnClickListener() {
    
            ArrayList<String> list = new ArrayList<>();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
            
            public void updateListView(){
                ListView listView = (ListView) findViewById(R.id.bluetoothDeviceListView);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(arrayAdapter);
            }
            
            @Override
            public void onClick(View view) {
                list.clear();
                Toast toast = Toast.makeText(getApplicationContext(), "Setting up bluetooth connection...", Toast.LENGTH_SHORT);
                toast.show();
                if (controlSocket == null || !controlSocket.isConnected()) {
                    openBluetoothConnection( controlUUID, new BluetoothConnector.BluetoothConnectorCallback() {
                        @Override
                        public void onBluetoothDeviceFound(BluetoothDevice bluetoothDevice) {
                            final String name = bluetoothDevice.getName();
                            final String address = bluetoothDevice.getAddress();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list.add(name + "::" + address);
                                    updateListView();
                                }
                            });
                            
                            String UUIDs = "";
                            if(bluetoothDevice.getUuids() != null) {
                                UUIDs = "[";
                                for (int i = 0; i < bluetoothDevice.getUuids().length; ++i) {
                                    if (i > 0) {
                                        UUIDs += ", ";
                                    }
                                    UUIDs += bluetoothDevice.getUuids()[i];
                                }
                                UUIDs += "]";
                            }
                            Log.d("BT-DEVICE","Device found: " + bluetoothDevice.getName() +
                                    " " + bluetoothDevice.getAddress() +
                                    " UUIDs: " + UUIDs);
                        }
    
                        @Override
                        public void onBluetoothDeviceConnected(BluetoothSocket bluetoothSocket) {
                            controlSocket = bluetoothSocket;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Successfully connected to bluetooth server", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
    
                        @Override
                        public void onBluetoothDeviceConnectionError() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Could not connect to a bluetooth server", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
    
                        @Override
                        public void onBluetoothNotSupported() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth not supported?", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
    
                        @Override
                        public void onNotEnabled() {
                            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
                        }
                    });
                } else {
                    Toast toast2 = Toast.makeText(getApplicationContext(), "Bluetooth connection already established", Toast.LENGTH_SHORT);
                    toast2.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
