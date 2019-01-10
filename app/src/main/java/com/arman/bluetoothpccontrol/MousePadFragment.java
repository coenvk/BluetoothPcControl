package com.arman.bluetoothpccontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class MousePadFragment extends Fragment {

    private BluetoothCommandService commandService;

    public void setBluetoothCommandService(BluetoothCommandService btHandler) {
        this.commandService = btHandler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mousepad_fragment, container, false);
        View touchView = view.findViewById(R.id.mousepad);
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    commandService.handleMouseEvent(event);
                }
                return true;
            }
        };
        touchView.setOnTouchListener(touchListener);
        touchView = view.findViewById(R.id.scrollpad);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    commandService.handleScrollEvent(event);
                }
                return true;
            }
        };
        touchView.setOnTouchListener(touchListener);
        touchView = view.findViewById(R.id.left_mouse);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.LEFT_PRESS);
                            break;
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.LEFT_RELEASE);
                            break;
                    }
                }
                return true;
            }
        };
        touchView.setOnTouchListener(touchListener);
        touchView = view.findViewById(R.id.right_mouse);
        touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (commandService != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            commandService.write(BluetoothCommandService.RIGHT_PRESS);
                            break;
                        case MotionEvent.ACTION_UP:
                            commandService.write(BluetoothCommandService.RIGHT_RELEASE);
                            break;
                    }
                }
                return true;
            }
        };
        touchView.setOnTouchListener(touchListener);
        return view;
    }

}
