package edu.purdue.dbough.sweetsignal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsFragment extends Fragment {
    View view;
    TextView contactView;
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

    public void writeContact (String contact) {
        try {
            FileWriter fw = new FileWriter(contactsFile, true);
            fw.write(contact + ',');
            fw.close();
        }
        catch (IOException e) {}
    }

    public String[] loadContacts() {
        String content = "";
        String[] contactArray;
        contactView = (TextView) view.findViewById(R.id.contactView);

        try {
            FileInputStream fis = new FileInputStream (new File(String.valueOf(contactsFile)));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            if (line != null) {
                contactArray = line.split(",");

                //Refresh contact text view
                for (String contact: contactArray) {
                    content += '\n' + contact;
                }
                contactView.setText(content);
                view.invalidate();
                return contactArray;
            }
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
