package in.midnaporeactacademy.doorstepmidnaporegrocery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import in.midnaporeactacademy.doorstepmidnaporegrocery.R;

import static java.security.AccessController.getContext;

public class AddAddress extends AppCompatActivity {

    public String value;
    private String category;
    public static int comingFromMyAccount = 0;
    private EditText fullName, mobile, locality,flat,landMark;
    DatabaseReference user;
    String shippingAddress,fullName_str,mobile_str,locality_str,pin_str,flat_str,landMark_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        ///////////////////////  HIDE STATUS BAR  ///////////////////////////////////

        final Spinner dropdown = findViewById(R.id.uploadProductCategory);
        String[] items = new String[]{"721101","721102"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (String) parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ///////////////////////////////////////////////////////////////////////////////

        Intent intent = getIntent();
        value = intent.getStringExtra("value");

        user = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        Button save = findViewById(R.id.save_btn);
        fullName = (EditText) findViewById(R.id.shipping_full_name);
        mobile = (EditText) findViewById(R.id.shipping_mobile);
        locality = (EditText) findViewById(R.id.shipping_locality);
        flat = (EditText) findViewById(R.id.shipping_building);
        landMark = (EditText) findViewById(R.id.shipping_landmark);

////////////////////////////////DISPLAYING PREVIOUSLY STORED ADDRESS///////////////////////////////

           user.child("ShippingAddress").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        fullName.setText(Objects.requireNonNull(snapshot.child("Name").getValue()).toString());
                        mobile.setText(Objects.requireNonNull(snapshot.child("Mobile").getValue()).toString());
                        locality.setText(Objects.requireNonNull(snapshot.child("Locality").getValue()).toString());
                        dropdown.setSelection(adapter.getPosition(category));
                        flat.setText(Objects.requireNonNull(snapshot.child("Flat_Building_No").getValue()).toString());
                        landMark.setText(Objects.requireNonNull(snapshot.child("Landmark").getValue()).toString());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

           save.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   fullName_str = fullName.getText().toString();
                   mobile_str = mobile.getText().toString();
                   locality_str = locality.getText().toString();
                   pin_str = category;
                   flat_str = flat.getText().toString();
                   landMark_str = landMark.getText().toString();

                   if (fullName_str.isEmpty()){
                       fullName.setError("Name required!");
                       fullName.requestFocus();
                       return;
                   }

                   if (mobile_str.isEmpty()){
                       mobile.setError("Mobile required!");
                       mobile.requestFocus();
                       return;
                   }

                   if (mobile_str.length()!=10){
                       mobile.setError("Enter valid mobile number!");
                       mobile.requestFocus();
                       return;
                   }

                   if (locality_str.isEmpty()){
                       locality.setError("Locality required!");
                       locality.requestFocus();
                       return;
                   }

                   if (flat_str.isEmpty()){
                       flat.setError("Flat required!");
                       flat.requestFocus();
                       return;
                   }

                   else {
                       putDataAndContinue();
                   }
               }
           });

    }

    private void putDataAndContinue() {

        shippingAddress = flat_str + "\n" + locality_str+ "\n" + landMark_str + "\n" + pin_str ;

        HashMap<String, String> address = new HashMap<>();

        address.put("Name", fullName_str);
        address.put("Mobile", mobile_str);
        address.put("Locality", locality_str);
        address.put("PinCode", pin_str);
        address.put("Flat_Building_No", flat_str);
        address.put("Landmark", landMark_str);

        user.child("ShippingAddress").setValue(address).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(comingFromMyAccount == 0) {
                    placeOrder();
                }
                else if (comingFromMyAccount == 1){
                    comingFromMyAccount = 0;
                    Intent mainActivity = new Intent(AddAddress.this,MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(comingFromMyAccount == 0) {
            super.onBackPressed();
        }
        else if (comingFromMyAccount == 1){

            Intent mainActivity = new Intent(AddAddress.this,MainActivity.class);
            startActivity(mainActivity);
            finish();
            comingFromMyAccount = 0;
        }
    }

    public void placeOrder(){
        Intent placeOrder = new Intent(AddAddress.this,PlaceOrderActivity.class);
        placeOrder.putExtra("value",value);
        placeOrder.putExtra("ShippingAddress",shippingAddress);
        placeOrder.putExtra("FullName",fullName_str);
        placeOrder.putExtra("MobileNo",mobile_str);
        startActivity(placeOrder);
        finish();

    }
}