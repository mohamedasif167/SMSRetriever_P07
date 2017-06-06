package com.example.a15017498.smsretrieverps;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentNum extends Fragment {

    Button btnRetrieve;
    TextView tv;
    EditText edtNum;
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter aa;

    public fragmentNum() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_num, container, false);

        btnRetrieve = (Button) view.findViewById(R.id.btnRetrieve);
        tv = (TextView) view.findViewById(R.id.tv);
        edtNum = (EditText) view.findViewById(R.id.edtNum);
        listView = (ListView) view.findViewById(R.id.listView);

        aa = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(aa);

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

                String num = edtNum.getText().toString();
                Log.d("Number Entered:",num);

                Uri uri = Uri.parse("content://sms");

                String[] reqCols = new String[]{"date","address","body","type"};

                ContentResolver cr = getActivity().getContentResolver();
                String filter = "address LIKE ? ";
                String[] filterArgs = {num};
                Cursor cursor = cr.query(uri,reqCols,filter,filterArgs,null);
                String smsBody = "";
                if(cursor.moveToFirst()){
                    do{
                        long dateInMillis = cursor.getLong(0);


                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);


                        String address = cursor.getString(1);
                        Log.d("address",address);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                        String message = type + " " + address + "\n at " + date
                                + "\n\"" + body ;
                        arrayList.add(message);

                    }while (cursor.moveToNext());
                }
              //  tv.setText(smsBody);
                aa.notifyDataSetChanged();
            }
        });



registerForContextMenu(listView);
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

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){

        super.onCreateContextMenu(menu,v, menuInfo);
        AdapterView.AdapterContextMenuInfo infoItem=(AdapterView.AdapterContextMenuInfo) menuInfo;

        if(v.getId()==R.id.listView){
            menu.add(0,0,0,"Send the content to Mr Jason ");

        }
    }

    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo infoItem = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = infoItem.position;
        String message = arrayList.get(index);

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String [] {"15017498@myrp.edu.sg"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SMS Content");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);


        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent,
                    "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                    "No email clients installed.",
                    Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);
    }

}
