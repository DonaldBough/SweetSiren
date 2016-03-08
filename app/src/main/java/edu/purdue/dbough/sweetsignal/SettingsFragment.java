package edu.purdue.dbough.sweetsignal;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsFragment extends Fragment {
    View view;
    EditText contactField;
    TextView contactView;
    File contactsFile;

    public SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings,null);
        contactField = (EditText) view.findViewById(R.id.contactField);
        contactView = (TextView) view.findViewById(R.id.contactView);

        File fileDir = new File(view.getContext().getFilesDir() + File.separator);
        if(fileDir.exists() == false) { //Make file if it doesn't exists
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

        Button button = (Button) view.findViewById(R.id.addContact);
        button.setOnClickListener(new View.OnClickListener() //When contact is added
        {
            @Override
            public void onClick(View v)
            {
                refreshContacts();
            }
        });

        refreshContacts();
        return view;
    }

    public void refreshContacts () {
        String contact = contactField.getText().toString();
        String content = "";

        if (contact != "" && contact != null) { //Only write actual contacts to file
            try {
                FileWriter fw = new FileWriter(contactsFile, true);
                fw.write(contact + ',');
                fw.close();
            } catch (IOException e) {}
        }
        String[] newContacts = loadContacts();
        for (String loadedContact: newContacts) { //Refresh TextView
            content += '\n' + loadedContact;
        }
        contactView.setText(content);
    }

    public String[] loadContacts() { //Returns array with contacts
        String[] contactArray;
        try {
            FileInputStream fis = new FileInputStream (new File(String.valueOf(contactsFile)));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            if (line != null) {
                contactArray = line.split(",");
                return contactArray;
            }
        }
        catch (IOException e) {}
        return null;
    }

}
