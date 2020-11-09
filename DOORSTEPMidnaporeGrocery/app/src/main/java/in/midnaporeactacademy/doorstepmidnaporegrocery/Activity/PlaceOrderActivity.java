package in.midnaporeactacademy.doorstepmidnaporegrocery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import in.midnaporeactacademy.doorstepmidnaporegrocery.Adapter.ProductsAdapter;
import in.midnaporeactacademy.doorstepmidnaporegrocery.ModelClass.CartUpload;
import in.midnaporeactacademy.doorstepmidnaporegrocery.ModelClass.Order;
import in.midnaporeactacademy.doorstepmidnaporegrocery.ModelClass.Upload;
import in.midnaporeactacademy.doorstepmidnaporegrocery.R;

public class PlaceOrderActivity extends AppCompatActivity {

    String fullName,shippingAddress,mobileNo;
    String orderId;
    TextView totalAmount_view, fullName_view, address_view, mobileNo_view;
    private RecyclerView mRecyclerView;
    private ProductsAdapter mAdapter;
    private ProgressBar progressBar;
    private List<Upload> mUploads;
    Context mcontext;
    int totalPrice;
    DatabaseReference orderReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        orderReference = FirebaseDatabase.getInstance().getReference();

        mcontext=this.getApplicationContext();
        mRecyclerView= findViewById(R.id.delivery_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads=new ArrayList<>();

        final DatabaseReference mDatabaseCartRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("cart");

        mDatabaseCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Upload upload = postSnapshot.getValue(Upload.class);
                        mUploads.add(upload);

                    }

                    totalAmount_view.setText("Rs. " + totalPrice + " /-");
                    mAdapter = new ProductsAdapter(PlaceOrderActivity.this, mUploads);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });



        totalAmount_view = findViewById(R.id.total_cart_amount_);
        fullName_view = findViewById(R.id.full_name);
        address_view = findViewById(R.id.address);
        mobileNo_view = findViewById(R.id.shipping_mobile_no);

        /////////////////// GETTING & SETTING  VALUES FROM ADDRESS ACTIVITY ///////////////////////

        Intent intent = getIntent();
        fullName = intent.getStringExtra("FullName");
        shippingAddress = intent.getStringExtra("ShippingAddress");
        mobileNo = intent.getStringExtra("MobileNo");
        totalPrice = Integer.parseInt(intent.getStringExtra("value"));

        fullName_view.setText(fullName);
        address_view.setText(shippingAddress);
        mobileNo_view.setText(mobileNo);

        //////////////////////////////////////////////////////////////////////////////////////////
        Button confirmOrderButton = (Button) findViewById(R.id.place_order_btn);

        confirmOrderButton.setOnClickListener(v -> {
            ConfirmOrder();
            finish();
        });

    }

    private void ConfirmOrder() {

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("cart");

        orderId = FirebaseDatabase.getInstance().getReference().child("Orders").push().getKey();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        final String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        final String status = "Pending";

        //////////////////////////////////// For Overall Orders List ///////////////////////////////

        Order order = new Order(
                fullName,
                mobileNo,
                shippingAddress,
                String.valueOf(totalPrice),
                orderId,
                saveCurrentDate,
                saveCurrentTime,
                status,
                Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        orderReference.child("Orders").child(orderId).setValue(order);

        //////////////////////////////////// For Item List ///////////////////////////////

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for(DataSnapshot items: snapshot.getChildren()){
                        final CartUpload cartUpload = items.getValue(CartUpload.class);

                        //////////////////////////////// For Customer's Orders List /////////////////////////////////

                        assert cartUpload != null;
                        final String productUniqueID =FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(FirebaseAuth.getInstance().getUid()).child("orders")
                                .push().getKey();

                        HashMap myOrders = new HashMap();
                        myOrders.put("productImage",cartUpload.getProductImageUrl());
                        myOrders.put("productTitle",cartUpload.getProductName());
                        myOrders.put("orderDate","Ordered on "+saveCurrentDate);
                        myOrders.put("productID",cartUpload.getProductID());
                        myOrders.put("productUniqueID",productUniqueID);
                        myOrders.put("orderStatus","Pending");
                        myOrders.put("orderId",orderId);
                        assert productUniqueID != null;
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .child("orders")
                                .child(productUniqueID)
                                .setValue(myOrders).addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        orderReference.child("Orders").child(orderId).child("Items")
                                                .child(cartUpload.getProductID())
                                                .setValue(cartUpload)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()){
                                                        totalPrice=0;
                                                        Toast.makeText(mcontext,"Thank You For Shopping With Us.",Toast.LENGTH_LONG).show();
                                                        Intent main = new Intent(PlaceOrderActivity.this,MainActivity.class);
                                                        startActivity(main);

                                                    }
                                                    else {
                                                        Toast.makeText(mcontext, "Something Went Wrong. Please Try later.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    else
                                        Toast.makeText(mcontext, "Something Went Wrong. Please Try later.", Toast.LENGTH_SHORT).show();
                                });


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

/////////////////////////////////////////////////////////////////////////////////////////////////

        cartRef.removeValue();

    }
}