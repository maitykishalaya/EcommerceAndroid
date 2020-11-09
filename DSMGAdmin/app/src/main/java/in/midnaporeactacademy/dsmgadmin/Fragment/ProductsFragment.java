package in.midnaporeactacademy.dsmgadmin.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.midnaporeactacademy.dsmgadmin.Activity.AddProductActivity;
import in.midnaporeactacademy.dsmgadmin.Adapter.ProductsAdapter;
import in.midnaporeactacademy.dsmgadmin.R;
import in.midnaporeactacademy.dsmgadmin.Class.Upload;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private ProductsAdapter mAdapter;

    private ProgressBar progressBar;

    private List<Upload> mUploads;

    public ProductsFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        final FragmentActivity fragmentActivity = getActivity();

        FloatingActionButton addProductButton = (FloatingActionButton) view.findViewById(R.id.add_product_button);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(fragmentActivity, AddProductActivity.class));
            }
        });

        mRecyclerView= view.findViewById(R.id.recycler_view_products);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentActivity));

        progressBar= view.findViewById(R.id.progress_circular_products);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF7B5100, android.graphics.PorterDuff.Mode.MULTIPLY);

        mUploads=new ArrayList<>();
        final DatabaseReference mDatabaseProductsRef = FirebaseDatabase.getInstance().getReference("allProducts");
        mDatabaseProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Upload upload=postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                mAdapter=new ProductsAdapter(fragmentActivity, mUploads);
                mRecyclerView.setAdapter(mAdapter);

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdapter=new ProductsAdapter(fragmentActivity, mUploads);
                assert fragmentActivity != null;
                fragmentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();

        return view;
    }
}