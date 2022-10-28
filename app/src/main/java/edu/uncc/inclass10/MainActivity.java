package edu.uncc.inclass10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener,
        SignUpFragment.SignUpListener, PostsFragment.PostsListener, CreatePostFragment.CreatePostListener {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the FirebaseAuth instance to see if a user is logged in
        mAuth = FirebaseAuth.getInstance();

        // If the user is not logged in
        if(mAuth.getCurrentUser() == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerView, new LoginFragment())
                    .commit();
        // If the user is logged in
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerView, new PostsFragment())
                    .commit();
        }
    }

    @Override
    public void createNewAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new SignUpFragment())
                .commit();
    }

    /**
     * This method changes from Login to Posts if the user successfully logs in
     */
    @Override
    public void loginSuccessful() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new PostsFragment())
                .commit();
    }

    /**
     * If the user successfully creates an account, go to Posts
     */
    @Override
    public void registerSuccessful() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new PostsFragment())
                .commit();
    }

    @Override
    public void login() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .commit();
    }

    /**
     * Logs the user out, then go to Login Page
     */
    @Override
    public void logout() {
        // Sign out of Firebase
        FirebaseAuth.getInstance().signOut();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .commit();
    }

    @Override
    public void createPost() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new CreatePostFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goBackToPosts() {
        getSupportFragmentManager().popBackStack();
    }
}