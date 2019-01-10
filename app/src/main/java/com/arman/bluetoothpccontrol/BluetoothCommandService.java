package com.arman.bluetoothpccontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.UUID;

import android.os.Handler;
import android.view.MotionEvent;

public class BluetoothCommandService {

    public static final String TAG = "BluetoothCommandService";
    public static final boolean DEBUG = true;

    private static final UUID uuid = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter adapter;
    private Handler handler;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public static final String EXIT = "exit";
    public static final String LEFT_CLICK = "left_click";
    public static final String LEFT_PRESS = "left_press";
    public static final String LEFT_RELEASE = "left_release";
    public static final String RIGHT_PRESS = "right_press";
    public static final String RIGHT_RELEASE = "right_release";
    public static final String SCROLL = "scroll";
    public static final String MOUSE_MOVE = "move";
    public static final String KEY_PRESS = "key_press";
    public static final String KEY_RELEASE = "key_release";

    private float mouseX, mouseY, dx, dy;
    private boolean mouseMoved;

    public BluetoothCommandService(Context context, Handler handler) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler;
        mouseMoved = false;
    }

    public void handleMouseEvent(MotionEvent event) {
        if (state == STATE_CONNECTED) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mouseX = event.getX();
                    mouseY = event.getY();
                    mouseMoved = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    dx = event.getX() - mouseX;
                    dy = event.getY() - mouseY;
                    mouseX = event.getX();
                    mouseY = event.getY();
                    if (dx != 0 || dy != 0) {
                        String mousePos = dx + "," + dy;
                        write(BluetoothCommandService.MOUSE_MOVE + " " + mousePos);
                    }
                    mouseMoved = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (!mouseMoved) {
                        write(BluetoothCommandService.LEFT_CLICK);
                    }
                    break;
            }
        }
    }

    public void handleScrollEvent(MotionEvent event) {
        if (state == STATE_CONNECTED) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mouseX = event.getX();
                    mouseY = event.getY();
                    mouseMoved = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    dx = event.getX() - mouseX;
                    dy = event.getY() - mouseY;
                    mouseX = event.getX();
                    mouseY = event.getY();
                    if (dx != 0 || dy != 0) {
                        String scroll = String.valueOf(dy);
                        write(BluetoothCommandService.SCROLL + " " + scroll);
                    }
                    mouseMoved = true;
                    break;
            }
        }
    }

    private synchronized void setState(int state) {
        if (DEBUG) {
            Log.d(TAG, "setting state from: " + this.state + " to: " + state);
        }
        this.state = state;
        handler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return this.state;
    }

    public synchronized void start() {
        if (DEBUG) {
            Log.d(TAG, "start");
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        if (DEBUG) {
            Log.d(TAG, "Connect to: " + device);
        }
        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (DEBUG) {
            Log.d(TAG, "connected");
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        Message msg = handler.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (DEBUG) {
            Log.d(TAG, "stop");
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(STATE_NONE);
    }

    public void write(String out) {
        ConnectedThread t;
        synchronized (this) {
            if (state != STATE_CONNECTED) {
                return;
            }
            t = connectedThread;
        }
        t.write(out);
    }

    public void write(int out) {
        ConnectedThread t;
        synchronized (this) {
            if (state != STATE_CONNECTED) {
                return;
            }
            t = connectedThread;
        }
        t.write(out);
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);
        Message msg = handler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Unable to connect device.");
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void connectionLost() {
        setState(STATE_LISTEN);
        Message msg = handler.obtainMessage(MainActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Device connection was lost.");
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private class ConnectThread extends Thread {

        private BluetoothSocket socket;
        private BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            try {
                this.socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "connect failed", e);
            }
        }

        public void run() {
            Log.i(TAG, "ConnectThread started");
            setName("ConnectThread");
            adapter.cancelDiscovery();
            try {
                socket.connect();
            } catch (IOException e) {
                connectionFailed();
                try {
                    socket.close();
                } catch (IOException e1) {
                    Log.e(TAG, "unable to close socket connection", e1);
                }
                BluetoothCommandService.this.start();
                return;
            }
            synchronized (BluetoothCommandService.this) {
                connectThread = null;
            }
            connected(socket, device);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel connect thread failed", e);
            }
        }

    }

    private class ConnectedThread extends Thread {

        private BluetoothSocket socket;
        private InputStream in;
        private OutputStream out;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create connected thread");
            this.socket = socket;
            try {
                this.in = socket.getInputStream();
                this.out = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "sockets for connected thread not created", e);
            }
        }

        public void run() {
            Log.i(TAG, "ConnectedThread started");
            setName("ConnectedThread");
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    int bytes = in.read(buffer);
                    handler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                out.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void write(String str) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.out)), true);
            out.println(str);
        }

        public void write(int x) {
            try {
                out.write(x);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                out.write(EXIT.getBytes(Charset.defaultCharset()));
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel connected thread failed", e);
            }
        }

    }

}
