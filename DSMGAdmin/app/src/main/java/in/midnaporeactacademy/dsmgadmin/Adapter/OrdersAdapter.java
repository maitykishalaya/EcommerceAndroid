package in.midnaporeactacademy.dsmgadmin.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.midnaporeactacademy.dsmgadmin.Activity.CheckOrderDetails;
import in.midnaporeactacademy.dsmgadmin.Activity.ManageProductActivity;
import in.midnaporeactacademy.dsmgadmin.Class.Order;
import in.midnaporeactacademy.dsmgadmin.R;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ProductsViewHolder> implements View.OnClickListener{
    private Context mContext;
    private List<Order> mUploads;

    DatabaseReference order = FirebaseDatabase.getInstance().getReference().child("Orders");
    public OrdersAdapter(Context context, List<Order> orders)
    {
        mContext=context;
        mUploads=orders;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.order_item, viewGroup,false);
        return new ProductsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder productsViewHolder, final int i) {
        final Order orderCur=mUploads.get(i);
        productsViewHolder.orderID.setText(orderCur.getOrderID());
        productsViewHolder.date.setText(orderCur.getDate());
        productsViewHolder.time.setText(orderCur.getTime());
        productsViewHolder.status.setText(orderCur.getStatus());

        productsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkDetailsIntent = new Intent(mContext, CheckOrderDetails.class);
                checkDetailsIntent.putExtra("sendOrderID", orderCur.getOrderID());
                mContext.startActivity(checkDetailsIntent);
            }
        });

        productsViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final int which_item = i;

                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext,R.style.Theme_AppCompat_DayNight_Dialog_Alert);

                dialog.setTitle("Is it delivered?")
                        .setMessage("Do you want to delete this order?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int k) {
                                mUploads.remove(which_item);
                                order.child(orderCur.getOrderID()).removeValue();
                            }
                        })
                        .setNegativeButton("NO",null)
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        return mUploads.size();
    }

    @Override
    public long getItemId(int item_pos) {

        return super.getItemId(item_pos);
    }

    @Override
    public void onClick(View v) {

    }

    public interface mClickListener {
        public void mClick(View v, int position);
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder{
        public TextView orderID, date, time, status;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            orderID=itemView.findViewById(R.id.orderID);
            date=itemView.findViewById(R.id.orderDate);
            time=itemView.findViewById(R.id.orderTime);
            status=itemView.findViewById(R.id.orderStatus);
        }

    }
}
