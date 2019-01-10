package com.arman.bluetoothpccontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class KeyboardFragment extends Fragment {

    private BluetoothCommandService commandService;

    public void setBluetoothCommandService(BluetoothCommandService commandService) {
        this.commandService = commandService;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.keyboard_fragment, container, false);
        Button btn = (Button) view.findViewById(R.id.escape_btn);
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.KEY_PRESS + " " + KeyEvent.ESCAPE);
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.KEY_RELEASE + " " + KeyEvent.ESCAPE);
                    }
                }
                return true;
            }
        };
        btn.setOnTouchListener(touchListener);
        btn = (Button) view.findViewById(R.id.delete_btn);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.KEY_PRESS + " " + KeyEvent.DELETE);
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.KEY_RELEASE + " " + KeyEvent.DELETE);
                    }
                }
                return true;
            }
        };
        btn.setOnTouchListener(touchListener);
        btn = (Button) view.findViewById(R.id.enter_btn);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.KEY_PRESS + " " + KeyEvent.ENTER);
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.KEY_RELEASE + " " + KeyEvent.ENTER);
                    }
                }
                return true;
            }
        };
        btn.setOnTouchListener(touchListener);
        btn = (Button) view.findViewById(R.id.f_btn);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.KEY_PRESS + " " + KeyEvent.F);
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.KEY_RELEASE + " " + KeyEvent.F);
                    }
                }
                return true;
            }
        };
        btn.setOnTouchListener(touchListener);
        btn = (Button) view.findViewById(R.id.n_btn);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.KEY_PRESS + " " + KeyEvent.N);
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.KEY_RELEASE + " " + KeyEvent.N);
                    }
                }
                return true;
            }
        };
        btn.setOnTouchListener(touchListener);
        btn = (Button) view.findViewById(R.id.p_btn);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.KEY_PRESS + " " + KeyEvent.P);
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.KEY_RELEASE + " " + KeyEvent.P);
                    }
                }
                return true;
            }
        };
        btn.setOnTouchListener(touchListener);
        btn = (Button) view.findViewById(R.id.space_btn);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.KEY_PRESS + " " + KeyEvent.SPACE);
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.KEY_RELEASE + " " + KeyEvent.SPACE);
                    }
                }
                return true;
            }
        };
        btn.setOnTouchListener(touchListener);
        return view;
    }

}
