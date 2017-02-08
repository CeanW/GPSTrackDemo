package com.ceanwu.gpstrackdemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shengyun Wu on 2/7/2017.
 */

public class BackTrackDialogFragment extends DialogFragment {

    private Context context;
    private ArrayList<HashMap<String, String>> data;
    private ItemClickListener listener;

    @Override
    public void onAttach(Context context) {
        listener = (ItemClickListener) getActivity();
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        context = getActivity();
        data = fetchData();
        super.onCreate(savedInstanceState);
    }

    private ArrayList<HashMap<String, String>> fetchData() {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        DatabaseAdapter dbAdapter = new DatabaseAdapter(context);
        ArrayList<Track> tracks = dbAdapter.getTracks();
        HashMap<String, String> map = null;
        Track t = null;
        int size = tracks.size();
        for (int i = 0; i < size; i++) {
            map = new HashMap<String, String>();
            t = tracks.get(i);
            map.put("id", String.valueOf(t.getId()));
            map.put("trackName_createDate",
                    t.getTrack_name() + "--" + t.getCreate_date());
            map.put("startEndLoc",
                    "从[" + t.getStart_loc() + "]到[" + t.getEnd_loc() + "]");
            data.add(map);
        }
        return data;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_track_list, null);
        ListView listView = (ListView) view.findViewById(R.id.lv);
        SimpleAdapter adapter = new SimpleAdapter(context,
                data,
                R.layout.track_list_item,
                new String[] { "id", "trackName_createDate", "startEndLoc" },
                new int[] { R.id.textView1_id, R.id.textView2_trackname_createdate, R.id.textView3_startEndLoc });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onDialogItemClick(Integer.valueOf(data.get(position).get("id")));
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a track")
                .setView(view);
        return builder.create();
    }

    public interface ItemClickListener{
        void onDialogItemClick(int id);
    }
}
