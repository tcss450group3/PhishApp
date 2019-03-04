package tcss450.uw.edu.phishappwolfr1;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tcss450.uw.edu.phishappwolfr1.Content.SetList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneSetList extends Fragment {

    SetList mySetList;
    public OneSetList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one_set_list, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {

            mySetList = (SetList) getArguments().get("");

            TextView tv = getActivity().findViewById(R.id.onesetlist_view_setlist_date);
            tv.setText(mySetList.getShowDate());

            tv = getActivity().findViewById(R.id.onesetlist_view_setList_location);
            tv.setText(mySetList.getLocation());

            tv = getActivity().findViewById(R.id.onesetlist_view_data);
            tv.setText(mySetList.getSetListData());

            tv = getActivity().findViewById(R.id.onesetlist_view_notes);
            tv.setText( Html.fromHtml(mySetList.getmSetListNotes()).toString());

        }

        Button b = (Button) getActivity().findViewById(R.id.butt_view_full_setlist_link);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFullView(mySetList);
            }
        });


    }

    private void onClickFullView(SetList s) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s.getUrl()));
        startActivity(myIntent);
    }
}
