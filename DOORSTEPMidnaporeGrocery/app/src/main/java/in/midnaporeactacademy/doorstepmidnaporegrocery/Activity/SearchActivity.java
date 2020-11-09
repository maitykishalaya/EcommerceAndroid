package in.midnaporeactacademy.doorstepmidnaporegrocery.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.midnaporeactacademy.doorstepmidnaporegrocery.Adapter.ProductsAdapter;
import in.midnaporeactacademy.doorstepmidnaporegrocery.ModelClass.Upload;
import in.midnaporeactacademy.doorstepmidnaporegrocery.R;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductsAdapter productsAdapter;
    private List<Upload> mProducts;
    private ProgressBar progressBar;
    ImageView noResultImage;
    TextView noResultText;

    EditText searchBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        progressBar= findViewById(R.id.progress_circular_search);
        searchBar = findViewById(R.id.search_bar);
        mProducts = new ArrayList<>();
        productsAdapter = new ProductsAdapter(SearchActivity.this, mProducts);
        recyclerView.setAdapter(productsAdapter);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF03508A, android.graphics.PorterDuff.Mode.MULTIPLY);

        noResultImage = findViewById(R.id.empty_result_image);
        noResultText = findViewById(R.id.empty_result_text);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("allProducts");

        recyclerView.setVisibility(View.VISIBLE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    mProducts.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Upload productData = snapshot.getValue(Upload.class);
                        mProducts.add(productData);
                    }
                    productsAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tokens[] = s.toString().toLowerCase().split(" ");
                for (String token : tokens){
                    productsAdapter.getFilter().filter(token);
                    Log.i("CHAR SEQ: ",s.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

    }


    /*private void searchUsers(final String s) {

        if (s.isEmpty()) {
            mProducts.clear();
            recyclerView.setVisibility(View.INVISIBLE);
            noResultImage.setVisibility(View.GONE);
            noResultText.setVisibility(View.GONE);
        }
        else {
            Query query = FirebaseDatabase.getInstance().getReference().child("allProducts");

            recyclerView.setVisibility(View.VISIBLE);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Upload productData = snapshot.getValue(Upload.class);

                                mProducts.add(productData);
                            }
                        productsAdapter.getFilter().filter(s.toLowerCase().replace(" ",""));
                        }
                        productsAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }*/

}