package com.example.bmicalculation;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bmicalculation.databinding.ActivityBmiPictureChoiceBinding;

public class BMIPictureChoiceActivity extends AppCompatActivity {

    private ActivityBmiPictureChoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBmiPictureChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.childButton.setOnClickListener(v -> startActivity(new Intent(this, ChildActivity.class)));
        binding.adultButton.setOnClickListener(v -> startActivity(new Intent(this, AdultActivity.class)));
    }
}
