package learn2crack.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import learn2crack.adapters.FragmentAdapter;
import learn2crack.chat.R;
import learn2crack.cotacts.SyncUtils;
import learn2crack.utilities.JSONParser;

public class LoginFragment extends Fragment {
    SharedPreferences prefs;
    EditText name, mobno;
    Button login;
    Button btnRegissteer;
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
    static final String TAG = "WN";
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
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                return false;
            }
            return false;
        }
        return true;
    }

    public boolean validateRegistration(){
        if(prefs.getString("REG_ID", "").isEmpty()){
            if(checkPlayServices()){
                try {
                    new RegisterGCM(getActivity().getApplicationContext()).execute();
                }
                catch (Exception ex){
                    Log.i("ERROR", "could not get gcd");
                }
                return true;
            }else{
                Toast.makeText(getActivity().getBaseContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
                return  false;
            }
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.login_fragment, container, false);
            prefs = getActivity().getSharedPreferences("Chat", 0);
            IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            getActivity().registerReceiver(mMessageReceiver, filter);
            SyncUtils.CreateSyncAccount(getActivity());
            name = (EditText) view.findViewById(R.id.name);
            mobno = (EditText) view.findViewById(R.id.mobno);
            login = (Button) view.findViewById(R.id.log_btn);
            btnRegissteer= (Button) view.findViewById((R.id.btnRegister));
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
                    UUID uniqueKey = UUID.randomUUID();
                    edit.putString("UUID", uniqueKey.toString());
                    edit.putString("PHONE_NUMBER", mobno.getText().toString());

                    //sendVerificationSms(v.getContext(), mobno.getText().toString());
                    edit.putString("MOB_NUM_ACTIVE", "true");
                    edit.commit();
                    String regidfordebug=prefs.getString("REG_ID", "");
                    validateRegistration();
                    new Register().execute();
                }
            });

            btnRegissteer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progress.show();
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_FROM", mobno.getText().toString());
                    edit.putString("FROM_NAME", name.getText().toString());
                    UUID uniqueKey = UUID.randomUUID();
                    edit.putString("UUID", uniqueKey.toString());
                    edit.putString("PHONE_NUMBER", mobno.getText().toString());

                    //sendVerificationSms(v.getContext(), mobno.getText().toString());
                    edit.putString("MOB_NUM_ACTIVE", "true");
                    edit.commit();
                    String regidfordebug=prefs.getString("REG_ID", "");
                    validateRegistration();
                    new Register().execute();
                }
            });
            return view;
        }
        catch (Exception e){
            int bls= 4, bla2 = 5;
            bla2 -= bls;
        }
       return null;
    }

    @Override
    public void onDestroyView(){
        getActivity().unregisterReceiver(mMessageReceiver);
        super.onDestroyView();
    }




    private class Register extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name.getText().toString()));
            params.add(new BasicNameValuePair("mobno", mobno.getText().toString()));
            String regidfordebug=prefs.getString("REG_ID", "");
            while(prefs.getString("REG_ID","").equals("")){
                try {
                    Thread.sleep(1000);
                }
                catch (Exception e){
                    Log.e("WN",e.getMessage());
                }
            }
            params.add((new BasicNameValuePair("reg_id",prefs.getString("REG_ID",""))));

            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/register",params);
            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            progress.dismiss();
            try {
                String res = json.getString("response");
                if(res.equals("Sucessfully Registered")) {
                    /*Fragment reg = new ContactsListFragment();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.viewpager, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                    ft.addToBackStack(null);
                    ft.commit();*/
                    Intent chat = new Intent(getActivity(), MainActivity.class);
                    chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(chat);
                    getActivity().finish();
                }else{
                    Toast.makeText(getActivity(),res,Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    GoogleCloudMessaging gcm;
    String SENDER_ID = "681641134962";
    String regid="";
    private class RegisterGCM extends AsyncTask<String, Void , Void> {

        Context mContext= null;

        public RegisterGCM(Context context){
            mContext = context;
        }

        @Override
        protected Void doInBackground(String... args){
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(mContext);
                    while(regid.equals("")) {
                        try {
                            regid = gcm.register(SENDER_ID);
                        } catch (IOException e) {
                            Thread.sleep(1000);
                        }
                    }
                    Log.e("RegId", regid);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_ID", regid);
                    edit.commit();
                    //return new JSONObject("{\"REG_ID\": "+regid +"}");
                }
                //new JSONObject("{\"REG_ID\": "+regid +"}");
                //return new String(regid);
                //return  regid;

            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                return null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused ) {
            /////////////////////
//            Fragment reg = new LoginFragment();
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.replace(R.id.container_body, reg);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            ft.addToBackStack(null);
//            ft.commit();
        }
    }

    private class Login extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("FROM_NAME", prefs.getString("FROM_NAME", "")));
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM", "")));
            params.add(new BasicNameValuePair("reg_id", prefs.getString("reg_id", "")));
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/login", params);
            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            progress.dismiss();
            try {
                JSONArray res = json.getJSONArray("users");
                if(res.length() > 0 ) {
                    Toast.makeText(getActivity(),"Login Success",Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).changeToUserScreen();
                }else{
                    Toast.makeText(getActivity(),"Login Failed - User not registered",Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}