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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SettingsFragment extends Fragment {
    View view;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings,null);
        loadContacts(view);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void loadContacts(View view) {
        TextView contactView = (TextView) view.findViewById(R.id.contactView);
        ArrayList<String> contactList = ((MainActivity)getActivity()).getContacts(view);
        for (String contact : contactList) {
            contactView.setText("\n" + contact);
        }
        view.invalidate();  //for refreshment

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
