package kth.ess_androidapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.transform.Result;

public class BluetoothConnector{
    private UUID app_uuid;
    private Context context;
    private BluetoothConnectorCallback bluetoothConnectorCallback;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                bluetoothAdapter.cancelDiscovery();
                BluetoothDevice btDevice = (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothConnectorCallback.onBluetoothDeviceFound(btDevice);
                //connectSocket(app_uuid, btDevice);
            }
        }
    };

    public BluetoothConnector(Context context, UUID app_uuid, BluetoothConnectorCallback bluetoothConnectorCallback) {
        this.context = context;
        this.app_uuid = app_uuid;
        this.bluetoothConnectorCallback = bluetoothConnectorCallback;
    
        if(bluetoothAdapter == null){
            if(bluetoothConnectorCallback != null){
                bluetoothConnectorCallback.onBluetoothNotSupported();
            }
            return;
        }
        if(!bluetoothAdapter.isEnabled()){
            bluetoothConnectorCallback.onNotEnabled();
        }else{
            findDevices();
        }
    }

    public void stop(){
        try {
            context.unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    
    private void connectSocket(UUID uuid, BluetoothDevice btDevice){
        ConnectThread thread;
        thread = new ConnectThread(uuid, btDevice, bluetoothAdapter, new ConnectThreadCallback() {
            @Override
            public void onThreadResults(BluetoothSocket socket) {
                if(socket != null){
                    bluetoothConnectorCallback.onBluetoothDeviceConnected(socket);
                }else{
                    bluetoothConnectorCallback.onBluetoothDeviceConnectionError();
                }
            }
    
            @Override
            public void onThreadError() {
                bluetoothConnectorCallback.onBluetoothDeviceConnectionError();
            }
        });
        thread.start();
    }
    
    private void findDevices(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(broadcastReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    public interface BluetoothConnectorCallback {
        void onBluetoothDeviceFound(BluetoothDevice bluetoothDevice);
        void onBluetoothDeviceConnected(BluetoothSocket bluetoothSocket);
        void onBluetoothDeviceConnectionError();
        void onBluetoothNotSupported();
        void onNotEnabled();
    }
    
    private interface ConnectThreadCallback{
        void onThreadResults(BluetoothSocket socket);
        void onThreadError();
    }
    
    private class ConnectThread extends Thread {
        private BluetoothAdapter adapter;
        private BluetoothSocket socket;
        private ConnectThreadCallback callback;
        
        public ConnectThread(UUID uuid, BluetoothDevice device, BluetoothAdapter adapter, ConnectThreadCallback callback){
            try {
                BluetoothSocket btSocket = device.createRfcommSocketToServiceRecord(uuid);
                this.socket = btSocket;
                this.adapter = adapter;
                this.callback = callback;
            } catch (IOException e) {
                e.printStackTrace();
                cancel();
                callback.onThreadError();
            }
        }
        
        public void run(){
            bluetoothAdapter.cancelDiscovery();
            if(socket == null){
                callback.onThreadError();
                return;
            }
            try{
                socket.connect();
                callback.onThreadResults(socket);
            } catch (IOException e){
                callback.onThreadResults(null);
                cancel();
            }
        }
        
        public void cancel(){
            try{
                socket.close();
            }catch (IOException e){
            }
        }
    }
}
