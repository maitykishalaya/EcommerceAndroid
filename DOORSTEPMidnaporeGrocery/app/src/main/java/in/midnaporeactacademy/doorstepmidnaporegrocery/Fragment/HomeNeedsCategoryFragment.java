package in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.midnaporeactacademy.doorstepmidnaporegrocery.Adapter.ProductsAdapter;
import in.midnaporeactacademy.doorstepmidnaporegrocery.R;
import in.midnaporeactacademy.doorstepmidnaporegrocery.ModelClass.Upload;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNeedsCategoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ProductsAdapter mAdapter;

    private ProgressBar progressBar;

    private List<Upload> mUploads;

    public HomeNeedsCategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_needs_category, container, false);

        final FragmentActivity fragmentActivity = getActivity();

        mRecyclerView= view.findViewById(R.id.recycler_view_home_needs);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        progressBar= view.findViewById(R.id.progress_circular_home_needs);

        mUploads=new ArrayList<>();
        final DatabaseReference mDatabaseHomeNeedsRef = FirebaseDatabase.getInstance().getReference("productsCategory").child("Home Needs");
        mDatabaseHomeNeedsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mUploads.clear();
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                    {
                        Upload upload=postSnapshot.getValue(Upload.class);
                        mUploads.add(upload);
                    }
                    mAdapter=new ProductsAdapter(fragmentActivity, mUploads);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }

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