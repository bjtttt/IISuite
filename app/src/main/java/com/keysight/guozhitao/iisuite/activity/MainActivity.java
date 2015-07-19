package com.keysight.guozhitao.iisuite.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.keysight.guozhitao.iisuite.R;
import com.keysight.guozhitao.iisuite.activity.settings.InstrumentSettingsFragment;
import com.keysight.guozhitao.iisuite.activity.settings.ServerSettingsFragment;
import com.keysight.guozhitao.iisuite.activity.settings.LocalSettingsFragment;
import com.keysight.guozhitao.iisuite.activity.settings.SettingsFragment;
import com.keysight.guozhitao.iisuite.helper.DBService;
import com.keysight.guozhitao.iisuite.helper.InstrumentInfo;
import com.keysight.guozhitao.iisuite.helper.GlobalSettings;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends
        ActionBarActivity
        implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        SCPIFragment.OnFragmentInteractionListener,
        SimulatorFragment.OnFragmentInteractionListener,
        InstrumentSettingsFragment.OnFragmentInteractionListener,
        ServerSettingsFragment.OnFragmentInteractionListener,
        LocalSettingsFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        LogFragment.OnFragmentInteractionListener {
    public final int MENU_ITEM_CONNECT_INSTRUMENT = 0;
    public final int MENU_ITEM_DISCONNECT_INSTRUMENT = 1;
    public final int MENU_ITEM_CONNECT_SERVER = 2;
    public final int MENU_ITEM_DISCONNECT_SERVER = 3;
    public final int MENU_ITEM_EXIT = 4;

    public final int FRAGMENT_SCPI = 0;
    public final int FRAGMENT_SIMULATOR = 1;
    public final int FRAGMENT_SETTINGS = 2;
    public final int FRAGMENT_LOG = 3;

    public final int TAB_SCPI = 1;
    public final int TAB_SIMULATOR = 2;
    public final int TAB_SETTINGS = 3;
    public final int TAB_LOG = 4;

    public final String mInstrDBName = "iis_instr";
    public final String[] mInstrDBColNames = new String[] {
            "connection",
            "idn",
            "scpitree",
            "connected",
            "locked"
    };
    public final int DB_INSTR_COL_CONNECTION = 0;
    public final int DB_INSTR_COL_IDN = 1;
    public final int DB_INSTR_COL_SCPI_TREE = 2;
    public final int DB_INSTR_COL_CONNECTED = 3;
    public final int DB_INSTR_COL_LOCKED = 4;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private int mCurrentSectionIndex;
    private boolean mInstrumentConnected = false;
    private boolean mServerConnected = false;

    private DBService mDBService;
    private GlobalSettings mGlobalSettings = new GlobalSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBService = new DBService(this);
        mGlobalSettings.setDBService(mDBService);
        Cursor c = mDBService.query("SELECT * FROM iis_instr ORDER BY connection", null);
        c.moveToFirst();
        boolean bFindLocalShort = false;
        boolean bFindLocalLong = false;
        while (c.isAfterLast() == false) {
            InstrumentInfo ii = new InstrumentInfo();
            ii.setConnection(c.getString(c.getColumnIndex(mInstrDBColNames[DB_INSTR_COL_CONNECTION])));
            ii.setIDN(c.getInt(c.getColumnIndex(mInstrDBColNames[DB_INSTR_COL_IDN])) == 1);
            ii.setSCPI(c.getInt(c.getColumnIndex(mInstrDBColNames[DB_INSTR_COL_SCPI_TREE])) == 1);
            ii.setConnected(c.getInt(c.getColumnIndex(mInstrDBColNames[DB_INSTR_COL_CONNECTED])) == 1);
            ii.setLocked(c.getInt(c.getColumnIndex(mInstrDBColNames[DB_INSTR_COL_LOCKED])) == 1);
            mGlobalSettings.getInstrumentInfoList().add(ii);

            if(ii.getConnection().trim().compareToIgnoreCase("TCPIP0::localhost::INSTR") == 0)
                bFindLocalShort = true;
            if(ii.getConnection().trim().compareToIgnoreCase("TCPIP0::localhost::inst0::INSTR") == 0)
                bFindLocalLong = true;

            c.moveToNext();
        }
        if(bFindLocalLong == false) {
            InstrumentInfo ii = new InstrumentInfo();
            ii.setConnection("TCPIP0::localhost::inst0::INSTR");
            ii.setConnected(true);
            mGlobalSettings.getInstrumentInfoList().add(0, ii);
            mDBService.querySet("INSERT INTO iis_instr ( connection, idn, scpitree, connected, locked ) VALUES ( 'TCPIP0::localhost::inst0::INSTR', 0, 0, 1, 0 )", null);
        }
        if(bFindLocalShort == false) {
            InstrumentInfo ii = new InstrumentInfo();
            ii.setConnection("TCPIP0::localhost::INSTR");
            ii.setConnected(true);
            mGlobalSettings.getInstrumentInfoList().add(0, ii);
            mDBService.querySet("INSERT INTO iis_instr ( connection, idn, scpitree, connected, locked ) VALUES ( 'TCPIP0::localhost::INSTR', 0, 0, 1, 0 )", null);
        }

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        /*
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        */

        mCurrentSectionIndex = position + 1;
        switch (position) {
            default:
            case FRAGMENT_SCPI:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SCPIFragment.newInstance("", ""))
                        .commit();
                break;
            case FRAGMENT_SIMULATOR:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SimulatorFragment.newInstance("", ""))
                        .commit();
                break;
            case FRAGMENT_SETTINGS:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance(mGlobalSettings, ""))
                        .commit();
                break;
            case FRAGMENT_LOG:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, LogFragment.newInstance("", ""))
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        mCurrentSectionIndex = number;
        switch (number) {
            default:
            case TAB_SCPI:
                mTitle = getString(R.string.title_scpi);
                break;
            case TAB_SIMULATOR:
                mTitle = getString(R.string.title_simulator);
                break;
            case TAB_SETTINGS:
                mTitle = getString(R.string.title_settings);
                break;
            case TAB_LOG:
                mTitle = getString(R.string.title_log);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        switch (mCurrentSectionIndex) {
            default:
            case TAB_SCPI:
                actionBar.setTitle(R.string.title_scpi);
                break;
            case TAB_SIMULATOR:
                actionBar.setTitle(R.string.title_simulator);
                break;
            case TAB_SETTINGS:
                actionBar.setTitle(R.string.title_settings);
                break;
            case TAB_LOG:
                actionBar.setTitle(R.string.title_log);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //MenuItem itemConnectInstrument = (MenuItem) findViewById(R.id.action_connect_instrument);
        //MenuItem itemDisconnectInstrument = (MenuItem) findViewById(R.id.action_disconnect_instrument);
        //MenuItem itemConnectServer = (MenuItem) findViewById(R.id.action_connect_server);
        //MenuItem itemDisconnectServer = (MenuItem) findViewById(R.id.action_disconnect_server);
        if(mInstrumentConnected) {
            menu.getItem(MENU_ITEM_CONNECT_INSTRUMENT).setEnabled(false);
            menu.getItem(MENU_ITEM_DISCONNECT_INSTRUMENT).setEnabled(true);
            //itemConnectInstrument.setEnabled(false);
            //itemDisconnectInstrument.setEnabled(true);
        }
        else {
            menu.getItem(MENU_ITEM_CONNECT_INSTRUMENT).setEnabled(true);
            menu.getItem(MENU_ITEM_DISCONNECT_INSTRUMENT).setEnabled(false);
            //itemConnectInstrument.setEnabled(true);
            //itemDisconnectInstrument.setEnabled(false);
        }
        if(mServerConnected) {
            menu.getItem(MENU_ITEM_CONNECT_SERVER).setEnabled(false);
            menu.getItem(MENU_ITEM_DISCONNECT_SERVER).setEnabled(true);
            //itemConnectServer.setEnabled(false);
            //itemDisconnectServer.setEnabled(true);
        }
        else {
            menu.getItem(MENU_ITEM_CONNECT_SERVER).setEnabled(true);
            menu.getItem(MENU_ITEM_DISCONNECT_SERVER).setEnabled(false);
            //itemConnectServer.setEnabled(true);
            //itemDisconnectServer.setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        if (id == R.id.action_connect_instrument) {
            return true;
        } else if(id == R.id.action_disconnect_instrument) {
            return true;
        } else if(id == R.id.action_connect_server) {
            return true;
        } else if(id == R.id.action_disconnect_server) {
            return true;
        } else if(id == R.id.action_exit) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            //ab.setTitle(R.string.dialog_exit).setIcon(R.drawable.question).setMessage((R.string.dialog_exit_msg))
            ab.setTitle(R.string.dialog_exit).setMessage((R.string.dialog_exit_msg))
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing here
                        }
                    })
                    .create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onFragmentInteraction(Uri uri){

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
