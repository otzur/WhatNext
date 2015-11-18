package learn2crack.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import learn2crack.bl.OptionSelectorManager;
import learn2crack.chat.R;
import learn2crack.db.ConversationDataSource;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnMessageResult;
import learn2crack.models.WnMessageRowOption;


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
        Log.i("WN", "inside on create Ret fragment");
        // Inflate the layout for this fragment
        View rv = inflater.inflate(R.layout.fragment_result, container, false);
        TextView mText = (TextView)rv.findViewById(R.id.result_text);
        mText.setTextSize(40);
        Bundle args = getArguments();

//        Bundle bundle=getArguments().getBundle("INFO");
//        //here is your list array
//        int numberOfOptions = bundle.getInt("numberOfOptions");
//
        String msg_id= args.getString("msg_id", "");
        int tab = args.getInt("numberOfOptions");
        Log.i("WN/RESULT FREG:","numberOfOptions = " + tab);
        WnMessageResult wnResult;
        if(!msg_id.equals("")) {
            MessageDataSource datasource = new MessageDataSource(rv.getContext());
            datasource.open();
            wnResult = datasource.getResultsForMessageID(msg_id);
            datasource.close();
        }
        else{
            ConversationDataSource conversationDataSource = new ConversationDataSource(rv.getContext());
            conversationDataSource.open();
            wnResult = conversationDataSource.getResultsForConversation(args.getString("c_id", ""));
            conversationDataSource.close();
        }

        int numberOfOptions = OptionSelectorManager.getNumberOfOptionsByTab(tab);
        OptionSelectorManager optionSelectorManager = new OptionSelectorManager(numberOfOptions);
        List<WnMessageRowOption>  list  = optionSelectorManager.getList();

        int size = 0;
        if(wnResult.getMatched() != null) {
            size = wnResult.getMatched().size();
        }
        if(size > 0) {
            mText.setText("Matched: \n");
            for (int i = 0; i < size; i++) {
                int index = Integer.valueOf(wnResult.getMatched().get(i));
                mText.append(list.get(index).getName() + "\n");
            }
        }
        else{
            if(wnResult.isAllUsersResponded()) {
                mText.setText("No match... not at all :( \n");
                mText.setTextColor(Color.RED);
            }
            else{
                mText.setText("Waiting for other user to respond \n");
                mText.setTextColor(Color.BLUE);
            }

        }
        return rv;
    }
}
