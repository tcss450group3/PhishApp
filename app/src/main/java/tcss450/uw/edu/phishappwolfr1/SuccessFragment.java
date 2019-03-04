package tcss450.uw.edu.phishappwolfr1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tcss450.uw.edu.phishappwolfr1.Model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessFragment extends Fragment {

    TextView successMessage;

    public SuccessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_success, container, false);
        successMessage = v.findViewById(R.id.success_view);

        if (getArguments()!= null){
            if (getArguments().containsKey(getString(R.string.keys_intent_credentials))) {
                Credentials c = (Credentials) getArguments().get(getString(R.string.keys_intent_credentials));
                successMessage.setText(c.getEmail());
            }
        }



        return v;
    }

}
