package in.midnaporeactacademy.dsmgadmin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import in.midnaporeactacademy.dsmgadmin.Adapter.ChatAdapter;
import in.midnaporeactacademy.dsmgadmin.Adapter.OrderdItemAdapter;
import in.midnaporeactacademy.dsmgadmin.Class.CartUpload;
import in.midnaporeactacademy.dsmgadmin.Class.MyOrderItemModel;
import in.midnaporeactacademy.dsmgadmin.Class.Order;
import in.midnaporeactacademy.dsmgadmin.Class.OrderdItem;
import in.midnaporeactacademy.dsmgadmin.R;

public class CheckOrderDetails extends AppCompatActivity {

    RecyclerView itemRecyclerView;
    OrderdItemAdapter mAdapter;
    List<OrderdItem> itemList;
    DatabaseReference orderReference;
    DatabaseReference orderItemReference;
    TextView orderID,date,time,fullName,address,mobileNo,value;
    RadioGroup radioGroup;
    String orderIDString;

    RadioButton pending,approved,delivered, currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_order_details);

        final Intent intent = getIntent();
        orderIDString = intent.getStringExtra("sendOrderID");

        orderReference = FirebaseDatabase.getInstance().getReference("Orders").child(orderIDString);

        orderID = findViewById(R.id.orderID);
        date = findViewById(R.id.orderDate);
        time = findViewById(R.id.orderTime);
        fullName = findViewById(R.id.customerName);
        address = findViewById(R.id.orderAddress);
        mobileNo = findViewById(R.id.orderMobile);
        value = findViewById(R.id.orderValue);

        radioGroup = findViewById(R.id.statusRadioGroup);
        pending = findViewById(R.id.pendingStatus);
        approved = findViewById(R.id.approvedStatus);
        delivered = findViewById(R.id.deliveredStatus);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                currentStatus = radioGroup.findViewById(i);
                orderReference.child("status").setValue(currentStatus.getText().toString());
                statusUser();
            }

        });

        orderID.setText(orderIDString);

        //////////////////////////////// ORDERED ITEMS ///////////////////////////////////////

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemRecyclerView = findViewById(R.id.orderList);

        itemRecyclerView.setLayoutManager(linearLayoutManager);
        orderItemReference = FirebaseDatabase.getInstance().getReference("Orders").child(orderIDString).child("Items");
        itemList = new ArrayList<>();
        orderItemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot Items:snapshot.getChildren()){
                        OrderdItem items = new OrderdItem(Objects.requireNonNull(Items.child("productName").getValue()).toString(),
                                Objects.requireNonNull(Items.child("productQuantity").getValue()).toString());
                        itemList.add(items);

                    }
                    mAdapter = new OrderdItemAdapter(itemList);
                    itemRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    date.setText(Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                    address.setText(Objects.requireNonNull(snapshot.child("address").getValue()).toString());
                    fullName.setText(Objects.requireNonNull(snapshot.child("fullName").getValue()).toString());
                    mobileNo.setText(Objects.requireNonNull(snapshot.child("mobile").getValue()).toString());
                    time.setText(Objects.requireNonNull(snapshot.child("time").getValue()).toString());
                    value.setText(" Rs " + Objects.requireNonNull(snapshot.child("value").getValue()).toString()+"/-");

                    if(Objects.requireNonNull(snapshot.child("status").getValue()).toString().equals("Pending"))
                        pending.setChecked(true);
                    else if(Objects.requireNonNull(snapshot.child("status").getValue()).toString().equals("Approved"))
                        approved.setChecked(true);
                    if(Objects.requireNonNull(snapshot.child("status").getValue()).toString().equals("Delivered"))
                        delivered.setChecked(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void statusUser(){
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    final String uid = Objects.requireNonNull(snapshot.child("uId").getValue()).toString();

                    final DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(uid)
                            .child("orders");



                    userOrderRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot orders : snapshot.getChildren()){

                                MyOrderItemModel userOrder = orders.getValue(MyOrderItemModel.class);
                                assert userOrder != null;
                                if(userOrder.getOrderID().equals(orderIDString)){
                                    userOrderRef.child(userOrder.getProductUniqueID())
                                            .child("orderStatus").setValue(currentStatus.getText().toString());
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}