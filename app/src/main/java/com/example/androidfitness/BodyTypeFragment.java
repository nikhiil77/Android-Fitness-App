package com.example.androidfitness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class BodyTypeFragment extends Fragment {

    private DBhandler dbHandler;
    private String username;

    public BodyTypeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_type, container, false);

        dbHandler = new DBhandler(requireActivity());
        username = ((ProfileSetupActivity) requireActivity()).getUsername();

        RadioGroup bodyTypeGroup = view.findViewById(R.id.bodyTypeGroup);
        Button nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            int selectedId = bodyTypeGroup.getCheckedRadioButtonId();
            String bodyType = "";

            if (selectedId == R.id.radioEctomorph) {
                bodyType = "Ectomorph";
            } else if (selectedId == R.id.radioEndomorph) {
                bodyType = "Endomorph";
            } else if (selectedId == R.id.radioMesomorph) {
                bodyType = "Mesomorph";
            } else {
                Toast.makeText(getActivity(), "Please select a body type", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update body type in the database
            dbHandler.updateBodyType(username, bodyType);

            // Retrieve gender from the database
            String gender = dbHandler.getUserGender(username);

            if (gender == null || gender.isEmpty()) {
                Toast.makeText(getActivity(), "Error: Gender not set!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Redirect to the appropriate home activity
            ((ProfileSetupActivity) requireActivity()).redirectToHomeActivity(gender);
        });

        return view;
    }
}