package com.keysight.guozhitao.iisuite.activity.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.keysight.guozhitao.iisuite.R;
import com.keysight.guozhitao.iisuite.helper.DBService;
import com.keysight.guozhitao.iisuite.helper.GlobalSettings;
import com.keysight.guozhitao.iisuite.helper.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;

//import android.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServerSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServerSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServerSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private GlobalSettings mGlobalSettings;
    private String mParam2;

    private String[] mContextmenuItems = new String[] {
            "Copy Server String",
            "Modify Server Configuration",
            "Delete Server",
            "Delete All Servers",
    };

    private DBService mDBService;

    private ListView mLVServer;
    private SimpleAdapter mLVSimpleAdapter;
    private ArrayList<HashMap<String,String>> mServerArrayList;

    final String ID_TITLE = "TITLE";
    final String ID_SUBTITLE = "SUBTITLE";

    private ViewGroup mViewGroup;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param globalSettings Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServerSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServerSettingsFragment newInstance(GlobalSettings globalSettings, String param2) {
        ServerSettingsFragment fragment = new ServerSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, globalSettings);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ServerSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGlobalSettings = (GlobalSettings)getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mDBService = mGlobalSettings.getmDBService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mViewGroup = container;

        View v = inflater.inflate(R.layout.fragment_server_settings, container, false);

        Button btnAdd = (Button) v.findViewById(R.id.btn_add_server);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_input_server, container, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //builder.setIcon(R.drawable.question);
                builder.setTitle(getString(R.string.input_server));
                builder.setView(ll);
                final EditText edittxtServer = (EditText) ll.findViewById(R.id.edittxt_server);
                final EditText etxtTimeout = (EditText) ll.findViewById(R.id.etxt_server_timeout);
                etxtTimeout.setText(Integer.toString(GlobalSettings.MIN_TIMEOUT));
                final CheckBox chkboxConncted = (CheckBox) ll.findViewById(R.id.chkbox_server_connected);
                Button btnIncrease = (Button) ll.findViewById(R.id.button_increase_server_timeout);
                btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int iTimeout = Integer.parseInt(etxtTimeout.getText().toString());
                        iTimeout = iTimeout + 1;
                        if(iTimeout < GlobalSettings.MIN_TIMEOUT)
                            iTimeout = GlobalSettings.MIN_TIMEOUT;
                        if(iTimeout > GlobalSettings.MAX_TIMEOUT)
                            iTimeout = GlobalSettings.MAX_TIMEOUT;
                        etxtTimeout.setText(Integer.toString(iTimeout));
                    }
                });
                Button btnDecrease = (Button) ll.findViewById(R.id.button_decrease_server_timeout);
                btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int iTimeout = Integer.parseInt(etxtTimeout.getText().toString());
                        iTimeout = iTimeout - 1;
                        if(iTimeout < GlobalSettings.MIN_TIMEOUT)
                            iTimeout = GlobalSettings.MIN_TIMEOUT;
                        if(iTimeout > GlobalSettings.MAX_TIMEOUT)
                            iTimeout = GlobalSettings.MAX_TIMEOUT;
                        etxtTimeout.setText(Integer.toString(iTimeout));
                    }
                });
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String sServer = edittxtServer.getText().toString().trim();
                        final int iTimeout = Integer.parseInt(etxtTimeout.getText().toString());
                        final boolean bConnected = chkboxConncted.isChecked();
                        if(sServer.isEmpty()) {
                            AlertDialog.Builder builderEmpty = new AlertDialog.Builder(getActivity());
                            builderEmpty.setTitle("Add Server").setMessage("Cannot add empty server!")
                                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create().show();
                        }
                        else {
                            ArrayList<ServerInfo> serverInfoList = mGlobalSettings.getServerInfoList();
                            boolean bAlreadyExisted = false;
                            for (ServerInfo ii : serverInfoList) {
                                if (ii.getServer().compareTo(sServer) == 0) {
                                    bAlreadyExisted = true;
                                    break;
                                }
                            }
                            if (bAlreadyExisted == true) {
                                AlertDialog.Builder builderEmpty = new AlertDialog.Builder(getActivity());
                                builderEmpty.setTitle(sServer).setMessage("Cannot add duplicated server!")
                                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .create().show();
                            } else {
                                ServerInfo si = new ServerInfo();
                                si.setServer(sServer);
                                si.setTimeout(iTimeout);
                                si.setConnected(bConnected);
                                serverInfoList.add(serverInfoList.size(), si);
                                String sql = "INSERT INTO iis_server ( server, timeout, connected ) VALUES ( '" +
                                        sServer +
                                        "', " + Integer.toString(iTimeout) +
                                        ", " + (bConnected ? "1" : "0") +
                                        " )";
                                mDBService.execSQL(sql);
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put(ID_TITLE, si.getServer());
                                map.put(ID_SUBTITLE, si.getServerConfiguration());
                                mServerArrayList.add(map);
                                mLVSimpleAdapter.notifyDataSetChanged();
                                AlertDialog.Builder builderEmpty = new AlertDialog.Builder(getActivity());
                                builderEmpty.setTitle(sServer).setMessage("Successfully add server!")
                                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .create().show();
                            }
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });

        mLVServer = (ListView) v.findViewById(R.id.server_listview);
        ArrayList<ServerInfo> serverInfoList = mGlobalSettings.getServerInfoList();
        mServerArrayList = new ArrayList<>();
        int serverInfoListSize = serverInfoList.size();
        for (int i = 0; i < serverInfoListSize; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(ID_TITLE, serverInfoList.get(i).getServer());
            map.put(ID_SUBTITLE, serverInfoList.get(i).getServerConfiguration());
            mServerArrayList.add(map);
        }
        mLVSimpleAdapter = new SimpleAdapter(
                getActivity(),
                mServerArrayList,
                R.layout.server_listview_item_layout,
                new String[] {ID_TITLE, ID_SUBTITLE},
                new int[] {R.id.server_connection_string, R.id.server_connection_configuration});
        mLVServer.setAdapter(mLVSimpleAdapter);
        registerForContextMenu(mLVServer);

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.server_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(mGlobalSettings.getServerInfoList().get(info.position).getServer());
            for (int i = 0; i < mContextmenuItems.length; i++) {
                menu.add(Menu.NONE, i, i, mContextmenuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean bSuper = super.onContextItemSelected(item);

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final int mitemIndex = item.getItemId();

        switch(mitemIndex) {
            default:
            case 0: {
                final ServerInfo si = mGlobalSettings.getServerInfoList().get(info.position);
                ClipboardManager clipboard = (android.content.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("CopyServer", si.getServer());
                clipboard.setPrimaryClip(clip);
            }
            break;
            case 1: {
                final ServerInfo si = mGlobalSettings.getServerInfoList().get(info.position);
                LinearLayout ll = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_input_server, mViewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //builder.setIcon(R.drawable.question);
                builder.setTitle(getString(R.string.modify_server));
                builder.setView(ll);
                final EditText edittxtServer = (EditText) ll.findViewById(R.id.edittxt_server);
                final EditText etxtTimeout = (EditText) ll.findViewById(R.id.etxt_server_timeout);
                final CheckBox chkboxConncted = (CheckBox) ll.findViewById(R.id.chkbox_server_connected);
                edittxtServer.setText(si.getServer());
                etxtTimeout.setText(Integer.toString(si.getTimeout()));
                edittxtServer.setEnabled(false);
                chkboxConncted.setChecked(si.getConnected());
                Button btnIncrease = (Button) ll.findViewById(R.id.button_increase_server_timeout);
                btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int iTimeout = Integer.parseInt(etxtTimeout.getText().toString());
                        iTimeout = iTimeout + 1;
                        if (iTimeout < GlobalSettings.MIN_TIMEOUT)
                            iTimeout = GlobalSettings.MIN_TIMEOUT;
                        if (iTimeout > GlobalSettings.MAX_TIMEOUT)
                            iTimeout = GlobalSettings.MAX_TIMEOUT;
                        etxtTimeout.setText(Integer.toString(iTimeout));
                    }
                });
                Button btnDecrease = (Button) ll.findViewById(R.id.button_decrease_server_timeout);
                btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int iTimeout = Integer.parseInt(etxtTimeout.getText().toString());
                        iTimeout = iTimeout - 1;
                        if (iTimeout < GlobalSettings.MIN_TIMEOUT)
                            iTimeout = GlobalSettings.MIN_TIMEOUT;
                        if (iTimeout > GlobalSettings.MAX_TIMEOUT)
                            iTimeout = GlobalSettings.MAX_TIMEOUT;
                        etxtTimeout.setText(Integer.toString(iTimeout));
                    }
                });
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int iTimeout = Integer.parseInt(etxtTimeout.getText().toString());
                        final boolean bConnected = chkboxConncted.isChecked();
                        if(iTimeout != si.getTimeout() || bConnected != si.getConnected()) {
                            si.setTimeout(iTimeout);
                            si.setConnected(bConnected);

                            String sql = "UPDATE iis_server set " +
                                    "timeout = " + Integer.toString(iTimeout) +
                                    ", connected = " + (bConnected ? "1" : "0") +
                                    " WHERE server = '" + si.getServer() + "'";
                            mDBService.execSQL(sql);

                            HashMap<String, String> hmServer = null;
                            for(HashMap<String, String> hm : mServerArrayList) {
                                if(hm.get(ID_TITLE).compareTo(si.getServer()) == 0) {
                                    hmServer = hm;
                                    break;
                                }
                            }
                            if(hmServer != null) {
                                hmServer.remove(ID_SUBTITLE);
                                hmServer.put(ID_SUBTITLE, si.getServerConfiguration());
                            }

                            mLVSimpleAdapter.notifyDataSetChanged();
                        }
                        else {
                            AlertDialog.Builder builderEmpty = new AlertDialog.Builder(getActivity());
                            builderEmpty.setTitle(si.getServer()).setMessage("Nothing needs modification!")
                                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create().show();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }
            break;
            case 2: {
                ServerInfo si = mGlobalSettings.getServerInfoList().get(info.position);
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setTitle(si.getServer()).setMessage((R.string.dialog_delete_server_msg))
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ServerInfo si = mGlobalSettings.getServerInfoList().get(info.position);
                                if(si.getServer().trim().compareToIgnoreCase("localhost") == 0) {
                                    AlertDialog.Builder abDelete = new AlertDialog.Builder(getActivity());
                                    abDelete.setTitle(si.getServer()).setMessage(("The default server cannot be deleted!"))
                                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .create().show();
                                }
                                else {
                                    mServerArrayList.remove(info.position);
                                    mGlobalSettings.getServerInfoList().remove(info.position);
                                    mLVSimpleAdapter.notifyDataSetChanged();

                                    mDBService.execSQL("DELETE FROM iis_server where server='" + si.getServer().trim() + "'");
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing here
                            }
                        })
                        .create().show();
            }
            break;
            case 3: {
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setTitle(mContextmenuItems[mitemIndex]).setMessage((R.string.dialog_delete_all_server_msg))
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<ServerInfo> serverInfoList = mGlobalSettings.getServerInfoList();
                                serverInfoList.clear();

                                mDBService.execSQL("DELETE FROM iis_server");
                                ServerInfo si = new ServerInfo();
                                si.setServer("localhost");
                                si.setConnected(true);
                                serverInfoList.add(0, si);
                                mDBService.execSQL("INSERT INTO iis_server ( server, timeout, connected ) VALUES ( 'localhost', 5, 1 )");

                                mServerArrayList.clear();
                                int serverInfoListSize = serverInfoList.size();
                                for (int i = 0; i < serverInfoListSize; i++) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put(ID_TITLE, serverInfoList.get(i).getServer());
                                    map.put(ID_SUBTITLE, serverInfoList.get(i).getServerConfiguration());
                                    mServerArrayList.add(map);
                                }

                                mLVSimpleAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing here
                            }
                        })
                        .create().show();
            }
            break;
        }

        return true && bSuper;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
