package edu.purdue.dbough.sweetsignal;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SettingsFragment extends Fragment {
    View view;
    File contactsFile;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings,null);
        File fileDir = new File(view.getContext().getFilesDir() + File.separator);
        //Make file is it doesn't exists
        if(fileDir.exists() == false) {
            try {
                fileDir.mkdir();
            }
            catch (Exception e) {}
        }
        else {
            contactsFile = new File(fileDir + "SweetSirenEmergencyContacts.csv");
            if (contactsFile.exists() == false) {
                try {
                    contactsFile.createNewFile();
                }
                catch (IOException e) {}
            }
        }
        loadContacts();

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void writeContact (String[] contactArray) {
        try {
            FileWriter fw = new FileWriter(contactsFile, true);
            for (String contact: contactArray) {
                fw.write(contact + ',');
            }
        }
        catch (IOException e) {}
    }

    public String[] loadContacts() {
        String[] contactArray;
        try {
            FileInputStream fis = new FileInputStream (new File(String.valueOf(contactsFile)));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            contactArray = reader.readLine().split(",");
            return contactArray;
        }
        catch (IOException e) {}
        return null;
    }



    //Closes keyboard. Called from onClick in fragment_settings.xml
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) view
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
