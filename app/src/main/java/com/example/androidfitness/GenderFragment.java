package com.example.androidfitness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class GenderFragment extends Fragment {

    private DBhandler dbHandler;
    private String username;

    public GenderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gender, container, false);

        dbHandler = new DBhandler(requireActivity());
        username = ((ProfileSetupActivity) requireActivity()).getUsername();

        RadioGroup genderGroup = view.findViewById(R.id.genderGroup);
        Button nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            int selectedId = genderGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.radioMale) {
                dbHandler.updateGender(username, "Male");
                ((ProfileSetupActivity) requireActivity()).setUserGender("Male");
            } else if (selectedId == R.id.radioFemale) {
                dbHandler.updateGender(username, "Female");
                ((ProfileSetupActivity) requireActivity()).setUserGender("Female");
            } else {
                Toast.makeText(getActivity(), "Please select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to BodyTypeFragment after selecting gender
            ((ProfileSetupActivity) requireActivity()).loadFragment(new BodyTypeFragment());
        });

        return view;
    }
}