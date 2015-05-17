package learn2crack.chat;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class LoginFragment extends Fragment {
    SharedPreferences prefs;
    EditText name, mobno;
    Button login;
    List<NameValuePair> params;
    ProgressDialog progress = null;

    public void stopProgressDialog(){
        if(progress != null){
            progress.dismiss();
        }
    }


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences prefs = context.getSharedPreferences("Chat", 0);
            Bundle extras = intent.getExtras();
            if (extras == null)
                return;

            String phoneNumber = prefs.getString("REG_FROM", "");
            if(phoneNumber.equals(""))
                return;

            String uniqueKey = prefs.getString("UUID", "");
            if(phoneNumber.isEmpty())
                return;

            Object[] pdus = (Object[]) extras.get("pdus");
            SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
            String origNumber = msg.getOriginatingAddress();
            String msgBody = msg.getMessageBody();
            if(!msgBody.equals("Activation:"+uniqueKey))
                return;
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("MOB_NUM_ACTIVE", "true");
            edit.commit();
            new Login().execute();
        }
    };

    public boolean sendVerificationSms(Context context, String phoneNumber){
        SharedPreferences prefs =  context.getSharedPreferences("Chat", 0);
        SharedPreferences.Editor edit = prefs.edit();
        UUID uniqueKey = UUID.randomUUID();
        edit.putString("UUID", uniqueKey.toString());
        edit.putString("PHONE_NUMBER", phoneNumber);
        edit.commit();
        sendSms(phoneNumber,"Activation:"+uniqueKey);
        return true;
    }

    public boolean sendSms(String phoneNumber, String message){
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(phoneNumber, null, message, null, null);
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        prefs = getActivity().getSharedPreferences("Chat", 0);
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(mMessageReceiver, filter);

        name = (EditText)view.findViewById(R.id.name);
        mobno = (EditText)view.findViewById(R.id.mobno);
        login = (Button)view.findViewById(R.id.log_btn);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Registering ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("REG_FROM", mobno.getText().toString());
                edit.putString("FROM_NAME", name.getText().toString());
                edit.commit();
                sendVerificationSms(v.getContext(), mobno.getText().toString());

            }
        });

        return view;
    }

    @Override
    public void onDestroyView(){
        getActivity().unregisterReceiver(mMessageReceiver);
        super.onDestroyView();
    }





    private class Login extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name.getText().toString()));
            params.add(new BasicNameValuePair("mobno", mobno.getText().toString()));
            params.add((new BasicNameValuePair("reg_id",prefs.getString("REG_ID",""))));

            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/login",params);
            return jObj;



        }
        @Override
        protected void onPostExecute(JSONObject json) {
            progress.dismiss();
            try {
                String res = json.getString("response");
                if(res.equals("Sucessfully Registered")) {
                    Fragment reg = new UserFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                }else{
                    Toast.makeText(getActivity(),res,Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}