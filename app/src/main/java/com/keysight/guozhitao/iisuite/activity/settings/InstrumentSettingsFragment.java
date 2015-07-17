package com.keysight.guozhitao.iisuite.activity.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;

import com.keysight.guozhitao.iisuite.R;
import com.keysight.guozhitao.iisuite.helper.GlobalSettings;
import com.keysight.guozhitao.iisuite.helper.InstrumentInfo;

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
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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

        ListView lv = (ListView) v.findViewById(R.id.instrument_listview);
        ArrayList<HashMap<String, String>> instrumentList = new ArrayList<HashMap<String, String>>();
        ArrayList<InstrumentInfo> instrumentInfoList = mGlobalSettings.getInstrumentInfoList();
        int instrumentInfoListSize = instrumentInfoList.size();
        for (int i = 0; i < instrumentInfoListSize; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Connection String", instrumentInfoList.get(i).getConnection());
            map.put("Connection Configuration", instrumentInfoList.get(i).getInstrumentConfiguration());
            instrumentList.add(map);
        }
        SimpleAdapter sa = new SimpleAdapter(getActivity(),
                instrumentList,
                R.layout.instrument_listview_item_layout,
                new String[]{"Connection String", "Connection Configuration"},
                new int[]{R.id.instrument_connection_string, R.id.instrument_connection_configuration});
        lv.setAdapter(sa);
        registerForContextMenu(lv);

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.instrument_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mGlobalSettings.getInstrumentInfoList().get(info.position).getConnection());
            String[] menuItems = new String[] {
                    "Modify",
                    "Delete"
            };
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
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
