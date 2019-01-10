package com.arman.bluetoothpccontrol;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabSwitcher extends FragmentStatePagerAdapter {

    private static final int NUM_TABS = 2;
    private BluetoothCommandService commandService;

    public TabSwitcher(FragmentManager fm, BluetoothCommandService commandService) {
        super(fm);
        this.commandService = commandService;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MousePadFragment mpFragment = new MousePadFragment();
                mpFragment.setBluetoothCommandService(commandService);
                return mpFragment;
            case 1:
                KeyboardFragment kbFragment = new KeyboardFragment();
                kbFragment.setBluetoothCommandService(commandService);
                return kbFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }

    public void setBluetoothCommandService(BluetoothCommandService commandService) {
        this.commandService = commandService;
    }

}
