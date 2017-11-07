package com.asmodeusstudio.parade.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.asmodeusstudio.parade.Main;
import com.asmodeusstudio.parade.R;
import com.asmodeusstudio.parade.database.Database;
import com.asmodeusstudio.parade.database.User;
import com.asmodeusstudio.parade.database.UserListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements LoginInterface {
    private final static String TAG = "KJKP6_LOGIN";
    private FragmentManager fragmentManager;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = SigninFragment.newInstance(this);
        fragmentTransaction.add(R.id.container, fragment, "selection");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0)
            super.onBackPressed();
    }

    private void onSuccessfulLogin() {
        Database.getInstance().addUserListener(userDataCheckListener);
        Database.getInstance().addUserEventListener();
    }

    private UserListener userDataCheckListener = new UserListener() {
        @Override
        public void onUpdate(User user) {
            Database.getInstance().removeUserListener(this);
            if (user == null) {
                Log.w(TAG, "user " + FirebaseAuth.getInstance().getCurrentUser().getUid() + " is new");
                Database.getInstance().addUserListenerWithoutNotifying(userDataCreateListener);
                Database.getInstance().setUser(new User("username", "email"));
            } else {
                Log.w(TAG, "user is old");
                onSuccessfulUserData();
            }
        }
    };

    private UserListener userDataCreateListener = new UserListener() {
        @Override
        public void onUpdate(User user) {
            Database.getInstance().removeUserListener(this);
            if (user == null) {
                Log.w(TAG, "user creation failed");
            } else {
                Log.w(TAG, "user " + FirebaseAuth.getInstance().getCurrentUser().getUid() + " is created");
                onSuccessfulUserData();
            }
        }
    };


    private void onSuccessfulUserData() {
        Intent i = new Intent(Login.this, Main.class);
        startActivity(i);
    }

    // =============================================================================================
    public void setCurrentFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void signinWithAuthCredential(AuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            onSuccessfulLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signinWithEmainAndPassword(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signinUserWithEmail:success");
                            onSuccessfulLogin();
                        } else {
                            Log.w(TAG, "signinUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(Login.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "signinUserWithEmail:failure", e);
                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void registerWithEmailAndPassword(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            onSuccessfulLogin();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(Login.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "createUserWithEmail:failure", e);
                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
