package com.example.scoda.booksharing;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;

public class LoginFragment extends Fragment {

    public static final String Mypreferences = "MyPrefs";
    SharedPreferences sharedPreferences;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ((MainActivity)getActivity()).findViewById(R.id.toolbar).setVisibility(View.INVISIBLE);
        final EditText emailField = (EditText) view.findViewById(R.id.email_login_field);
        final EditText passwordField = (EditText) view.findViewById(R.id.password_login_field);
        final Button loginButton = (Button) view.findViewById(R.id.login_buton);
        final Button signUpButton = (Button) view.findViewById(R.id.new_acc_button);
        final TextView careNumber = (TextView) view.findViewById(R.id.CareNumber);

        careNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:123456789"));
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDatabase userDatabase = new UserDatabase(getActivity().getApplicationContext());
                SQLiteDatabase db = userDatabase.getReadableDatabase();
                Cursor dbCursor;
                dbCursor = db.query(
                        UserDatabase.TableClass.EntryClass.TABLE_NAME,  // The table to query
                        null,                               // The columns to return
                        null,                                // The columns for the WHERE clause
                        null,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                 // The sort order
                );

                if(dbCursor!=null && dbCursor.moveToFirst()) {

                    do {
                        if (dbCursor.getString(dbCursor.getColumnIndex(UserDatabase.TableClass.EntryClass.EMAIL))
                                .equals(emailField.getText().toString()) &&
                                dbCursor.getString(dbCursor.getColumnIndex(UserDatabase.TableClass.EntryClass.USER_PASSWORD))
                                        .equals(passwordField.getText().toString())) {
                            dbCursor.close();

                            sharedPreferences = getActivity().getSharedPreferences(Mypreferences,Context.MODE_PRIVATE);
                            int count = sharedPreferences.getInt("logincount",0);
                            long firstdate=sharedPreferences.getLong("firstlaunchdate",System.currentTimeMillis());
                            long launchdate=sharedPreferences.getLong("lastlaunchdate",System.currentTimeMillis());
                            int currdiff=(int)((System.currentTimeMillis()-launchdate)/(24*60*60*1000));
                            int initdiff=(int)((System.currentTimeMillis()-firstdate)/(24*60*60*1000));

                                    if(initdiff>1 && currdiff<=5 && count<3)
                                    {
                                        count++;
                                        launchdate=System.currentTimeMillis();
                                        Log.d("Count",String.valueOf(count));
                                    }
                                    else if(count>=3 || initdiff>5)
                                    {
                                        firstdate=System.currentTimeMillis();
                                        launchdate=System.currentTimeMillis();
                                        count=1;

                                        Log.d("count value",String.valueOf(count));
                                    }



                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("logincount",count);
                            editor.putLong("lastlaunchdate",launchdate);
                            editor.putLong("firstlaunchdate",firstdate);
                            editor.commit();


                            ((MainActivity) getActivity()).gotoHomeFragment(emailField.getText().toString(),passwordField.getText().toString());

                        }
                    }while (dbCursor.moveToNext());


                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"User Does not Exist",Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).gotoSignUpFragment();
            }
        });
        return view;
    }
    private static String encryptPassword(String password)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
