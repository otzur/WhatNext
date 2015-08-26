package learn2crack.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import learn2crack.chat.R;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnMessageResult;


public class ResultFragment extends Fragment {


    public ResultFragment() {
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
        View rv = inflater.inflate(R.layout.fragment_result, container, false);
        TextView mText = (TextView)rv.findViewById(R.id.result_text);
        mText.setTextSize(40);
        Bundle args = getArguments();
        String msg_id= args.getString("msg_id");
        MessageDataSource datasource = new MessageDataSource(rv.getContext());
        datasource.open();
        WnMessageResult wnResult= datasource.getResultsForMessageID(msg_id);
        int size = wnResult.getMatched().size();
        if(size > 0) {
            mText.setText("Matched: \n");
            for (int i = 0; i < size; i++) {
                mText.append(wnResult.getMatched().get(i) + "\n");
            }
        }
        else{
            mText.setText("No match... not at all :( \n");
            mText.setTextColor(Color.RED);

        }
        return rv;
    }
}
