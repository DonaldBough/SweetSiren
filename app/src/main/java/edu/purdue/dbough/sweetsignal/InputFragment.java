package edu.purdue.dbough.sweetsignal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class InputFragment extends Fragment {
    View view;
    EditText sugarLevelField;

    public static InputFragment newInstance() {
        InputFragment fragment = new InputFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_input,null);
        return inflater.inflate(R.layout.fragment_input, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //Called from fragment_input.xml
    public void saveData(View view){
        //Save all data to .csv
        sugarLevelField = (EditText) view.findViewById(R.id.sugarLevelField);
        if (sugarLevelField == null) {
            return;
        }
        Integer sugarLevel = Integer.parseInt(sugarLevelField.getText().toString());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
        String date = df.format(c.getTime());
        String time = df2.format(c.getTime());

        Context context = view.getContext();
        File fileDir = new File(context.getFilesDir() + File.separator);
        File file = new File(fileDir + "BloodSugarLevels.csv");

        OutputStream outputStream;
        try{
            outputStream = new FileOutputStream(file, true);
            String output = (sugarLevel + "," + time + "," + date + ",");
            outputStream.write(output.getBytes());
            outputStream.close();
        }
        catch (Exception e){
            Toast.makeText(view.getContext(), "Whoops, couldn't save", Toast.LENGTH_SHORT).show();
        }
    }

    //Closes keyboard. Called from onClick in fragment_input.xml
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) view
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
