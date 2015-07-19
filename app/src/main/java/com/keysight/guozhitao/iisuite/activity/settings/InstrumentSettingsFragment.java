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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.view.MenuItem;
import android.widget.TextView;

import com.keysight.guozhitao.iisuite.R;
import com.keysight.guozhitao.iisuite.helper.DBService;
import com.keysight.guozhitao.iisuite.helper.GlobalSettings;
import com.keysight.guozhitao.iisuite.helper.InstrumentInfo;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

//import android.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InstrumentSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InstrumentSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InstrumentSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private GlobalSettings mGlobalSettings;
    private String mParam2;

    private String[] mContextmenuItems = new String[] {
            "Copy",
            "Modify",
            "Delete",
            "Delete All",
    };

    private DBService mDBService;

    private ListView mLVInstrument;
    private SimpleAdapter mLVSimpleAdapter;
    private ArrayList<HashMap<String,String>> mInstrumentArrayList;
    private String mCopyInstrument = "";

    final String ID_OBJECT = "OBJECT";
    final String ID_TITLE = "TITLE";
    final String ID_SUBTITLE = "SUBTITLE";

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param globalSettings Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InstrumentSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InstrumentSettingsFragment newInstance(GlobalSettings globalSettings, String param2) {
        InstrumentSettingsFragment fragment = new InstrumentSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, globalSettings);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InstrumentSettingsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_instrument_settings, container, false);
        Button btnAdd = (Button) v.findViewById(R.id.btn_add_instrument);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_input_intrument, container, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //builder.setIcon(R.drawable.question);
                builder.setTitle(getString(R.string.input_instrument));
                builder.setView(ll);
                final EditText edittxtConnection = (EditText) ll.findViewById(R.id.edittxt_connection);
                final CheckBox chkboxConncted = (CheckBox) ll.findViewById(R.id.chkbox_connected);
                final CheckBox chkboxLocked = (CheckBox) ll.findViewById(R.id.chkbox_locked);
                final CheckBox chkboxIDN = (CheckBox) ll.findViewById(R.id.chkbox_idn);
                final CheckBox chkboxSCPI = (CheckBox) ll.findViewById(R.id.chkbox_scpi);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String sInstrument = edittxtConnection.getText().toString().trim();
                        final boolean bConnected = chkboxConncted.isChecked();
                        final boolean bLocked = chkboxLocked.isChecked();
                        final boolean bIDN = chkboxIDN.isChecked();
                        final boolean bSCPI = chkboxSCPI.isChecked();
                        if(sInstrument.isEmpty()) {
                            AlertDialog.Builder builderEmpty = new AlertDialog.Builder(getActivity());
                            builderEmpty.setTitle("Add Instrument").setMessage("Cannot add empty instrument!")
                                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create().show();
                        }
                        else {
                            ArrayList<InstrumentInfo> instrumentInfoList = mGlobalSettings.getInstrumentInfoList();
                            boolean bAlreadyExisted = false;
                            for (InstrumentInfo ii : instrumentInfoList) {
                                if (ii.getConnection().compareTo(sInstrument) == 0) {
                                    bAlreadyExisted = true;
                                    break;
                                }
                            }
                            if (bAlreadyExisted == true) {

                            } else {
                                InstrumentInfo ii = new InstrumentInfo();
                                ii.setConnection("TCPIP0::localhost::inst0::INSTR");
                                ii.setConnected(true);
                                instrumentInfoList.add(0, ii);
                                mDBService.querySet("INSERT INTO iis_instr ( connection, idn, scpitree, connected, locked ) VALUES ( '" +
                                        sInstrument +
                                        "', " +
                                        ", " +
                                        ", " +
                                        ", " +
                                        " )", null);
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

        mLVInstrument = (ListView) v.findViewById(R.id.instrument_listview);
        ArrayList<InstrumentInfo> instrumentInfoList = mGlobalSettings.getInstrumentInfoList();
        mInstrumentArrayList = new ArrayList<>();
        int instrumentInfoListSize = instrumentInfoList.size();
        for (int i = 0; i < instrumentInfoListSize; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(ID_TITLE, instrumentInfoList.get(i).getConnection());
            map.put(ID_SUBTITLE, instrumentInfoList.get(i).getInstrumentConfiguration());
            mInstrumentArrayList.add(map);
        }
        mLVSimpleAdapter = new SimpleAdapter(
                getActivity(),
                mInstrumentArrayList,
                R.layout.instrument_listview_item_layout,
                new String[] {ID_TITLE, ID_SUBTITLE},
                new int[] {R.id.instrument_connection_string, R.id.instrument_connection_configuration});
        mLVInstrument.setAdapter(mLVSimpleAdapter);
        registerForContextMenu(mLVInstrument);

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.instrument_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(mGlobalSettings.getInstrumentInfoList().get(info.position).getConnection());
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
                ClipboardManager clipboard = (android.content.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = android.content.ClipData.newPlainText("Clip", mCopyInstrument);
                clipboard.setPrimaryClip(clip);
            }
                break;
            case 1: {

            }
                break;
            case 2: {
                InstrumentInfo ii = mGlobalSettings.getInstrumentInfoList().get(info.position);
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setTitle(ii.getConnection()).setMessage((R.string.dialog_delete_instrument_msg))
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InstrumentInfo ii = mGlobalSettings.getInstrumentInfoList().get(info.position);
                                if(ii.getConnection().trim().compareToIgnoreCase("TCPIP0::localhost::INSTR") == 0 ||
                                        ii.getConnection().trim().compareToIgnoreCase("TCPIP0::localhost::inst0::INSTR") == 0) {
                                    AlertDialog.Builder abDelete = new AlertDialog.Builder(getActivity());
                                    abDelete.setTitle(ii.getConnection()).setMessage(("The default instrument cannot be deleted!"))
                                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .create().show();
                                }
                                else {
                                    mInstrumentArrayList.remove(info.position);
                                    mGlobalSettings.getInstrumentInfoList().remove(info.position);
                                    mLVSimpleAdapter.notifyDataSetChanged();

                                    mDBService.querySet("DELETE FROM iis_instr where connection='" + ii.getConnection().trim() + "'", null);
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
                ab.setTitle(mContextmenuItems[mitemIndex]).setMessage((R.string.dialog_delete_all_instrument_msg))
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<InstrumentInfo> instrumentInfoList = mGlobalSettings.getInstrumentInfoList();
                                instrumentInfoList.clear();

                                mDBService.querySet("DELETE FROM iis_instr", null);
                                InstrumentInfo ii = new InstrumentInfo();
                                ii.setConnection("TCPIP0::localhost::inst0::INSTR");
                                ii.setConnected(true);
                                instrumentInfoList.add(0, ii);
                                mDBService.querySet("INSERT INTO iis_instr ( connection, idn, scpitree, connected, locked ) VALUES ( 'TCPIP0::localhost::INSTR', 0, 0, 1, 0 )", null);
                                ii = new InstrumentInfo();
                                ii.setConnection("TCPIP0::localhost::INSTR");
                                ii.setConnected(true);
                                instrumentInfoList.add(0, ii);
                                mDBService.querySet("INSERT INTO iis_instr ( connection, idn, scpitree, connected, locked ) VALUES ( 'TCPIP0::localhost::inst0::INSTR', 0, 0, 1, 0 )", null);

                                mInstrumentArrayList.clear();
                                int instrumentInfoListSize = instrumentInfoList.size();
                                for (int i = 0; i < instrumentInfoListSize; i++) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put(ID_TITLE, instrumentInfoList.get(i).getConnection());
                                    map.put(ID_SUBTITLE, instrumentInfoList.get(i).getInstrumentConfiguration());
                                    mInstrumentArrayList.add(map);
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
