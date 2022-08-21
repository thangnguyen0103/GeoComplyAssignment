package com.thangnguyen.geocomplyassigment.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;


import com.thangnguyen.geocomplyassigment.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding.btnSubmit.setOnClickListener(view -> {
            mainViewModel.submitInput(binding.edComment.getText().toString());
        });

        observeData();
    }

    private void observeData() {
        mainViewModel.getContent().observe(this, s -> binding.tvContent.setText(s));

        mainViewModel.getError().observe(this, e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }
}