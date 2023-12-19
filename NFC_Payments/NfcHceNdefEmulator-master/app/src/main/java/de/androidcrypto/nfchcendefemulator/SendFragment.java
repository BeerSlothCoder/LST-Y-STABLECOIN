package de.androidcrypto.nfchcendefemulator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RadioButton rbUrl;
    TextView tvTimestamp;
    boolean isTimestamp = false; // start/default
    com.google.android.material.textfield.TextInputLayout dataToSendLayout;
    com.google.android.material.textfield.TextInputEditText dataToSend;
    //private final String DEFAULT_URL = "https://www.google.de/maps/@34.7967917,-111.765671,3a,66.6y,15.7h,102.19t/data=!3m6!1e1!3m4!1sFV61wUEyLNwFi6zHHaKMcg!2e0!7i16384!8i8192";
    private final String DEFAULT_URL = "https://github.com/AndroidCrypto?tab=repositories";

    public SendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendFragment newInstance(String param1, String param2) {
        SendFragment fragment = new SendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // AID is setup in apduservice.xml
    // original AID: F0394148148100

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tvTimestamp = getView().findViewById(R.id.tvTimestamp);
        rbUrl = getView().findViewById(R.id.rbUrl);

        dataToSendLayout = getView().findViewById(R.id.etDataToSendsLayout);
        dataToSendLayout.setEnabled(false);
        dataToSend = getView().findViewById(R.id.etDataToSend);
        dataToSendLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataToSendString = dataToSend.getText().toString();
                if (TextUtils.isEmpty(dataToSendString)) {
                    Toast.makeText(view.getContext(), "Enter data to send", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (rbUrl.isChecked()) {
                    // check for https:// at the beginning
                    if (!dataToSendString.substring(0, 8).toLowerCase().equals("https://")) {
                        Toast.makeText(view.getContext(), "The URL needs to start with https://", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(view.getContext(), MyHostApduService.class);
                    intent.putExtra("ndefUrl", dataToSendString);
                    Toast.makeText(view.getContext(), "This URL is send as NDEF message: " + dataToSendString, Toast.LENGTH_SHORT).show();
                    requireActivity().startService(intent);
                }
            }
        });

        rbUrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (rbUrl.isChecked()) {
                    dataToSendLayout.setEnabled(true);
                    dataToSend.setText(DEFAULT_URL);
                    isTimestamp = false;
                }
            }
        });

        // start with timestamp
        ndefWithTimestamp(view.getContext());
    }

    private void ndefWithTimestamp(Context context) {
        PackageManager pm = context.getPackageManager();
        Timer t = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (isTimestamp) {
                    Date dt = Calendar.getInstance().getTime();
                    //Log.d(TAG, "Set time as " + dt.toString());
                    tvTimestamp.setText(dt.toString());
                    if (pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
                        Intent intent = new Intent(context, MyHostApduService.class);
                        intent.putExtra("ndefMessage", dt.toString());
                        context.startService(intent);
                    }
                }
            }

        };
        //t.scheduleAtFixedRate(task, 0, 1000); // every second
        //t.scheduleAtFixedRate(task, 0, 60000); // every minute
        t.scheduleAtFixedRate(task, 0, 2000); // every 2 seconds
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}