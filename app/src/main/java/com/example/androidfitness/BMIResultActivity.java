package com.example.androidfitness;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class BMIResultActivity extends AppCompatActivity {

    private TextView bmiValue, bmiCategory;
    private View progressForeground;
    private LottieAnimationView bmiAnimation;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_result);

        bmiValue = findViewById(R.id.bmiValue);
        bmiCategory = findViewById(R.id.bmiCategory);
        progressForeground = findViewById(R.id.progressForeground);
        bmiAnimation = findViewById(R.id.bmiAnimation);
        btnBack = findViewById(R.id.btnBack);

        // Get the BMI value from the intent
        double bmi = getIntent().getDoubleExtra("BMI_VALUE", 0.0);

        // Display the BMI value and category
        bmiValue.setText(String.format("%.1f", bmi));
        bmiCategory.setText(getBMICategory(bmi));

        // Animate the circular progress bar
        animateProgressBar(bmi);

        // Play the Lottie animation
        bmiAnimation.playAnimation();

        // Back button logic
        btnBack.setOnClickListener(v -> finish()); // Close the activity and go back
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 24.9) return "Normal";
        if (bmi < 29.9) return "Overweight";
        return "Obese";
    }

    private void animateProgressBar(double bmi) {
        // Calculate progress (BMI ranges from 0 to 40 for visualization)
        float progress = (float) (bmi / 40 * 100);

        // Animate the progress bar
        ValueAnimator animator = ValueAnimator.ofFloat(0, progress);
        animator.setDuration(2000); // 2 seconds animation
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            progressForeground.setScaleX(animatedValue / 100);
            progressForeground.setScaleY(animatedValue / 100);
        });
        animator.start();
    }
}