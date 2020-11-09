package in.midnaporeactacademy.doorstepmidnaporegrocery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import in.midnaporeactacademy.doorstepmidnaporegrocery.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;
    private CheckBox privacy_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ///////////////////////  HIDE STATUS BAR  ///////////////////////////////////
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ///////////////////////////////////////////////////////////////////////////////

        RootRef= FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        final EditText userName=(EditText) findViewById(R.id.register_user_name);
        final EditText userEmail=(EditText)findViewById(R.id.register_user_email);
        final EditText userMobile=(EditText)findViewById(R.id.register_user_mobile);
        final EditText userPassword=(EditText)findViewById(R.id.register_user_password);
        TextView terms_text = (TextView) findViewById(R.id.terms_text);
        privacy_checkbox = (CheckBox) findViewById(R.id.privacy_checkbox);
        Button registerUserButton=(Button)findViewById(R.id.register_user_button);
        TextView alreadyHaveAnAccount=(TextView)findViewById(R.id.already_have_an_account);
        loadingBar = new ProgressDialog(RegisterActivity.this,R.style.MyAlertDialogStyle);

        terms_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,PolicyActivity.class));
            }
        });

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        registerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=userName.getText().toString();
                String email=userEmail.getText().toString();
                String mobile=userMobile.getText().toString();
                String password=userPassword.getText().toString();

                if (name.isEmpty()){
                    userName.setError("Name required!");
                    userName.requestFocus();
                    return;
                }
                if (email.isEmpty()){
                    userEmail.setError("Email Required!");
                    userEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    userEmail.setError("Enter a valid Email Address!");
                    userEmail.requestFocus();
                    return;
                }
                if (mobile.isEmpty()){
                    userMobile.setError("Mobile Number Required!");
                    userMobile.requestFocus();
                    return;
                }
                if (mobile.length()!=10){
                    userMobile.setError("Enter valid mobile number!");
                    userMobile.requestFocus();
                    return;
                }
                if (password.isEmpty()){
                    userPassword.setError("Create a Password!");
                    userPassword.requestFocus();
                    return;
                }
                if (password.length()<6){
                    userPassword.setError("Create password with minimum six digits!");
                    userPassword.requestFocus();
                    return;
                }
                if (!privacy_checkbox.isChecked()){
                    privacy_checkbox.setError("Check it first!");
                    privacy_checkbox.requestFocus();
                    return;
                }

                register_user(name,email,mobile,password);
            }
        });
    }

    private void register_user(final String name, final String email, final String mobile, String password) {

        loadingBar.setTitle("Creating New Account");
        loadingBar.setMessage("Please wait, while we are creating new account for you...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    final String currentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    RootRef.child("Users").child(currentUser).child("name").setValue(name)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        RootRef.child("Users").child(currentUser).child("email").setValue(email)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            RootRef.child("Users").child(currentUser).child("mobile").setValue(mobile)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                RootRef.child("Users").child(currentUser).child("Uid").setValue(currentUser)
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()){
                                                                                                    HashMap<String,String> address = new HashMap<>();

                                                                                                    address.put("Name","");
                                                                                                    address.put("Mobile","");
                                                                                                    address.put("Locality","");
                                                                                                    address.put("PinCode","");
                                                                                                    address.put("Flat_Building_No","");
                                                                                                    address.put("Landmark","");
                                                                                                    RootRef.child("Users").child(currentUser).child("ShippingAddress").setValue(address);

                                                                                                    loadingBar.dismiss();
                                                                                                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                                                                                    finish();
                                                                                                }
                                                                                                else {
                                                                                                    loadingBar.dismiss();
                                                                                                    String error = Objects.requireNonNull(task.getException()).getMessage();
                                                                                                    Toast.makeText(RegisterActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                            else {
                                                                                loadingBar.dismiss();
                                                                                String error = Objects.requireNonNull(task.getException()).getMessage();
                                                                                Toast.makeText(RegisterActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else {
                                                            loadingBar.dismiss();
                                                            String error = Objects.requireNonNull(task.getException()).getMessage();
                                                            Toast.makeText(RegisterActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        String error = Objects.requireNonNull(task.getException()).getMessage();
                                        Toast.makeText(RegisterActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    loadingBar.dismiss();
                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(RegisterActivity.this,"Error: "+error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}