package in.midnaporeactacademy.doorstepmidnaporegrocery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import in.midnaporeactacademy.doorstepmidnaporegrocery.R;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ///////////////////////  HIDE STATUS BAR  ///////////////////////////////////
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ///////////////////////////////////////////////////////////////////////////////

        final EditText userEmail=(EditText)findViewById(R.id.login_user_email);
        final EditText userPassword=(EditText)findViewById(R.id.login_user_password);
        Button loginUserButton=(Button)findViewById(R.id.login_user_button);
        TextView goToRegisterActivity=(TextView)findViewById(R.id.don_t_have_an_account);
        TextView goToForgotPasswordActivity=(TextView)findViewById(R.id.forgot_password);
        loadingBar = new ProgressDialog(LoginActivity.this,R.style.MyAlertDialogStyle);

        goToRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

        goToForgotPasswordActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPassword = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(forgotPassword);
                finish();
            }
        });

        loginUserButton.setOnClickListener(v -> {
            mAuth=FirebaseAuth.getInstance();

            String loginEmail=userEmail.getText().toString();
            String loginPassword=userPassword.getText().toString();

            if (loginEmail.isEmpty()){
                userEmail.setError("Email required!");
                userEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()){
                userEmail.setError("Enter a valid Email!");
                userEmail.requestFocus();
                return;
            }
            if (loginPassword.isEmpty()){
                userPassword.setError("Password required!");
                userPassword.requestFocus();
                return;
            }

            login_user(loginEmail,loginPassword);
        });
    }

    private void login_user(String loginEmail, String loginPassword) {

        loadingBar.setTitle("Login");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loadingBar.dismiss();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else {
                    loadingBar.dismiss();
                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(LoginActivity.this,"Error: "+error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent registerActivity = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerActivity);
        finish();
    }
}