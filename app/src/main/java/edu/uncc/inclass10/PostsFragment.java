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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.inclass10.databinding.FragmentPostsBinding;
import edu.uncc.inclass10.databinding.PostRowItemBinding;
import edu.uncc.inclass10.models.Post;

public class PostsFragment extends Fragment {

    private FirebaseAuth mAuth;

    final String TAG = "test";

    private String name;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentPostsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This method sets the name as the Title
        getName();

        // This method gets the Posts and tells the adapter to notify changes
        getPosts();

        binding.buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createPost(name);
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });

        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postsAdapter = new PostsAdapter();
        binding.recyclerViewPosts.setAdapter(postsAdapter);

        getActivity().setTitle(R.string.posts_label);
    }

    PostsAdapter postsAdapter;
    ArrayList<Post> mPosts = new ArrayList<>();

    class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {
        @NonNull
        @Override
        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PostRowItemBinding binding = PostRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new PostsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
            Post post = mPosts.get(position);
            holder.setupUI(post);
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }

        class PostsViewHolder extends RecyclerView.ViewHolder {
            PostRowItemBinding mBinding;
            Post mPost;
            public PostsViewHolder(PostRowItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Post post){
                mPost = post;
                mBinding.textViewPost.setText(post.getPost_text());
                mBinding.textViewCreatedBy.setText(post.getCreated_by_name());
                mBinding.textViewCreatedAt.setText(post.getCreated_at());

                // This checks to see if the current user id is the same as the post's creator user
                // id, if so they can be deleted, otherwise make the button invisible.
                if (mAuth.getCurrentUser().getUid().equals(mPost.getCreated_by_uid())) {
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDelete.setVisibility(View.INVISIBLE);
                }
                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePost(post);
                    }
                });
            }
        }

    }

    /**
     * This method updates the name variable by retrieving it from the document.
     * @param document
     */
    private void updateName(QueryDocumentSnapshot document) {
        this.name = document.getString("full_name");
        binding.textViewTitle.setText(name);
    }

    /**
     * This method accesses the database and finds the document that has the same user_id
     * as the user. Then it passes the document to another method to get the name.
     */
    private void getName() {

        // Get Authentication (User) Instance
        mAuth = FirebaseAuth.getInstance();

        // Get Database Instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the collection "user_info"
        db.collection("user_info").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "onSuccess: ");
                        // Once retrieved, loop to find the document with matching user_id
                        for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                            if (document.getString("user_id").equals(mAuth.getCurrentUser().getUid())){
                                updateName(document);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    /**
     * This method gets posts from the database and adds them to the arraylist mPosts
     */
    private void getPosts(){
        // Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Collection "posts"
        db.collection("posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        // Clear the arraylist every time the database is accessed or changes
                        mPosts.clear();
                        // Loop through the documents
                        for (QueryDocumentSnapshot document: value){

                            Log.d(TAG, "onSuccess: " + document.getId());
                            Log.d(TAG, "onSuccess: " + document.getData());

                            // Convert document to Post Object
                            Post post = document.toObject(Post.class);

                            // Add post to arraylist
                            mPosts.add(post);
                        }
                        // Notify the changes when done adding all posts
                        postsAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * This method deletes the selected post from the database
     * by accessing its post_id and deleting the proper matching document id
     * @param post the post object selected
     */
    private void deletePost(Post post) {

        // Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Collection
        // Select the document by the post_id
        // Then delete
        db.collection("posts").document(post.post_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Post deleted successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Error deleting post.");
                    }
                });
    }

    PostsListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (PostsListener) context;
    }

    interface PostsListener{
        void logout();
        void createPost(String name);
    }
}