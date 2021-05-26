package com.example.dictaphone.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dictaphone.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordingFragment extends Fragment {

    private int PERMISSION_CODE = 21;
    private ImageView imageButtonStart;
    private TextView textViewStart;
    private boolean isRecording = false;
    private Chronometer chronometer;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    TextView textView;

    public RecordingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chronometer = view.findViewById(R.id.chronometer);
        imageButtonStart = view.findViewById(R.id.imageButton);
        textViewStart = view.findViewById(R.id.textView2);
        textView = view.findViewById(R.id.textView);


        imageButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    //stop
                    stopRecording();
                    imageButtonStart.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    textViewStart.setText("СТАРТ");
                    isRecording = false;
                } else {
                    //start
                    if (checkPermissions()) {
                        startRecording();
                        imageButtonStart.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                        textViewStart.setText("СТОП");
                        isRecording = true;
                    }
                }
            }
        });
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    private void startRecording() {
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ROOT);
        Date date = new Date();
        recordFile = "Recording " + dateFormat.format(date) + ".mp3";
        textView.setText(recordFile);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
        chronometer.start();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRecording();
    }
}