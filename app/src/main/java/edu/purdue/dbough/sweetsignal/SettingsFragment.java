package edu.purdue.dbough.sweetsignal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsFragment extends Fragment {
    View view;
    EditText contactField;
    EditText targetField;
    TextView contactView;
    File contactsFile;
    File targetFile;
    int targetBloodSugar = 100;

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
        targetField = (EditText) view.findViewById(R.id.targetSugarField);

        File fileDir = new File(view.getContext().getFilesDir() + File.separator);
        if(fileDir.exists() == false) { //Make directory if it doesn't exists
            try {
                fileDir.mkdir();
            }
            catch (Exception e) {}
        }
        else {
            contactsFile = new File(fileDir + "SweetSirenEmergencyContacts.csv");
            targetFile = new File(fileDir + "TargetBloodSugarFile.csv");
            targetFile.delete();
            if (contactsFile.exists() == false) { //Make file if it doesn't exist
                try {
                    contactsFile.createNewFile();
                }
                catch (IOException e) {}
            }
            if (targetFile.exists() == false) { //Make file if it doesn't exist
                try {
                    targetFile.createNewFile();
                }
                catch (IOException e) {}
            }
        }

        Button contactButton = (Button) view.findViewById(R.id.addContactButton);
        Button targetButton = (Button) view.findViewById(R.id.addTargetSugarButton);

        contactButton.setOnClickListener(new View.OnClickListener() { //When contact is added
            @Override
            public void onClick(View v)
            {
                refreshContacts();
            }
        });
        targetButton.setOnClickListener(new View.OnClickListener() {//When target sugar is added
            @Override
            public void onClick(View v)
            {
                refreshTargetLevel();
            }
        });

        refreshContacts();
        refreshTargetLevel();
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

    public void refreshTargetLevel() {
        try {
            targetBloodSugar = Integer.parseInt(targetField.getText().toString());
        }
        catch (Exception e) {
            targetBloodSugar = 999;
        }

        if (targetBloodSugar != 999) { //Save targetLevel
            try {
                FileWriter fw = new FileWriter(targetFile, false);
                fw.write(String.valueOf(targetBloodSugar));
                fw.close();
            }
            catch (IOException e){}
        }

        else { //targetField was null, try to load a level from file
            try {
                FileInputStream fis = new FileInputStream (new File(String.valueOf(targetFile)));
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line = reader.readLine();
                if (line != null)
                    targetBloodSugar = Integer.parseInt(line);
                else
                    targetBloodSugar = 100; //Default to 100
            }
            catch (IOException e) {}
        }

        targetField.setText(String.valueOf(targetBloodSugar));
    }

    public int getTargetBloodSugar() {
        return targetBloodSugar;
    }

}
