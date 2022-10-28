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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.uncc.inclass10.databinding.FragmentCreatePostBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePostFragment extends Fragment {

    private FirebaseAuth mAuth;
    final String TAG = "test";

    private static final String ARG_PARAM_NAME = "name";
    private String name;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name The name of the user.
     * @return A new instance of fragment CreatePostFragment.
     */
    public static CreatePostFragment newInstance(String name) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            this.name = getArguments().getString(ARG_PARAM_NAME);
        }
    }

    FragmentCreatePostBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goBackToPosts();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = binding.editTextPostText.getText().toString();
                if(postText.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid post !!", Toast.LENGTH_SHORT).show();
                } else {
                    // Create post
                    createPost(postText);
                    // Go back to Posts
                    mListener.goBackToPosts();
                }
            }
        });

        getActivity().setTitle(R.string.create_post_label);
    }

    /**
     * This method creates a post with the current user's information
     * @param postText
     */
    private void createPost(String postText) {

        // Get the current Date and Time
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());

        // Get the current user id
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();

        // Get the user's name
        String name = this.name;

        // Get Firebase Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Collection Reference Object
        CollectionReference postsRef = db.collection("posts");

        // Creates a new document in that collection (posts) and store it's id as a string
        // to be used for creating the post's id
        String post_id = postsRef.document().getId();

        // Create the Post using HashMap
        HashMap<String, Object> post = new HashMap<>();
        post.put("created_at", date);
        post.put("created_by_name", name);
        post.put("created_by_uid", user_id);
        post.put("post_id", post_id);
        post.put("post_text", postText);

        // Set the data for the document
        db.collection("posts").document(post_id)
                .set(post);
    }

    CreatePostListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreatePostListener) context;
    }

    interface CreatePostListener {
        void goBackToPosts();
    }
}