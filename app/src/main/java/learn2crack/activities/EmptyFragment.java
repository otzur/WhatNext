package learn2crack.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import learn2crack.chat.R;


public class EmptyFragment extends Fragment {


    public EmptyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("WN", "inside on create Freind fregmant");
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_empty, container, false);
    }
}
