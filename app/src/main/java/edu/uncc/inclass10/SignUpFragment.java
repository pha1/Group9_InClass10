/**
 * In Class 10
 * Group9_InClass10
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.inclass10;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import edu.uncc.inclass10.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    final String TAG = "test";

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentSignUpBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.login();
            }
        });

        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editTextName.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                if(name.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid name!", Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid password!", Toast.LENGTH_SHORT).show();
                } else {
                    // Get the Firebase Authentication instance
                    mAuth = FirebaseAuth.getInstance();

                    // Firebase create an account method
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {

                                        // Log successful
                                        Log.d(TAG, "onComplete: Sign Up Successful.");

                                        // Store the name into the database
                                        setName(name);

                                        // Signs the new user in, go to Posts
                                        // Implemented by Main Activity
                                        mListener.registerSuccessful();
                                    } else {
                                        Log.d(TAG, "onComplete: Sign Up Error.");
                                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                        // Displays response message by Firebase
                                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        getActivity().setTitle(R.string.create_account_label);

    }

    private void setName(String name) {
        // Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // HashMap user
        HashMap<String, Object> user = new HashMap<>();
        user.put("full_name", name);
        user.put("user_id", mAuth.getCurrentUser().getUid());

        // Collection
        // Add the user
        db.collection("user_info")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: User added.");
                    }
                });
    }

    SignUpListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (SignUpListener) context;
    }

    interface SignUpListener {
        void registerSuccessful();
        void login();
    }
}