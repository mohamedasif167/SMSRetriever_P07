package com.example.a15017498.smsretrieverps;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentWord extends Fragment {

    Button btnRetrieve;
    TextView tv;
    EditText edtWord;

    public fragmentWord() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        btnRetrieve = (Button) view.findViewById(R.id.btnRetrieve);
        tv = (TextView) view.findViewById(R.id.tv);
        edtWord = (EditText) view.findViewById(R.id.edtWord);


        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To show the pop up to ask for permission
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }

                String word = edtWord.getText().toString();

                String[] separated = word.split("\\s+");
              String word1 = separated[0].toString();
              String word2 = separated[1].toString();

                String completed_word = "%"+word+"%";
                String word1_ = "%"+word1+"%";
                String word2_ = "%"+word2+"%";
                Log.d("Number Entered:",word);

                Uri uri = Uri.parse("content://sms");

                String[] reqCols = new String[]{"date","address","body","type"};

                ContentResolver cr = getActivity().getContentResolver();
                String filter = "body LIKE ? OR body LIKE ? ";

                //Scenario 4
                //String filter = "body LIKE ? AND  body LIKE ? OR body LIKE ? ";
                //String[] filterArgs = {completed_word,word1_,word2_};

                String[] filterArgs = {word1_,word2_};
                Cursor cursor = cr.query(uri,reqCols,filter,filterArgs,null);
                String smsBody = "";
                if(cursor.moveToFirst()){
                    do{
                        long dateInMillis = cursor.getLong(0);


                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);


                        String address = cursor.getString(1);

                        String body = cursor.getString(2);
                        Log.d("body",body);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + " at " + date
                                + "\n\"" + body + "\n";

                    }while (cursor.moveToNext());
                }
                tv.setText(smsBody);
            }
        });
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnRetrieve.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getActivity(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
