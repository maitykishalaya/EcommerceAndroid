package in.midnaporeactacademy.doorstepmidnaporegrocery.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.BabiesNeedsCategoryFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.HomeFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.HomeNeedsCategoryFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.KidsNeedsCategoryFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.MenNeedsCategoryFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.MyAccountFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.MyCartFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.OfferZoneFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.MyOrdersFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.OtherNeedsCategoryFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.StudentsNeedsCategoryFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.Fragment.WomenNeedsCategoryFragment;
import in.midnaporeactacademy.doorstepmidnaporegrocery.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    public static int openCart = 0;
    public static int openHomeApp = 0;
    public static int openMen = 0;
    public static int openWomen = 0;
    public static int openKids = 0;
    public static int openBabies = 0;
    public static int openStudents = 0;
    public static int openOthers = 0;
    public static int openMessages = 0;
    public static int openMyOrders = 0;

    private int RC_APP_UPDATE = 11;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    private TextView name, email;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser==null){
            startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            finish();
        }

        //////////////////////////////////////////////////////////////////////////////

        mAuth = FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser()!=null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            mDrawer = (DrawerLayout) findViewById(R.id.my_nav_drawer);
            drawerToggle = setupDrawerToggle();
            drawerToggle.syncState();

            NavigationView nvDrawer = (NavigationView) findViewById(R.id.nav_view);
            setupDrawerContent(nvDrawer);

            View header=nvDrawer.getHeaderView(0);

            name = header.findViewById(R.id.nav_header_fullName);
            email = header.findViewById(R.id.nav_header_email);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

            userRef.child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        name.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            userRef.child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        email.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

///////////////////////////////////  CATEGORY TO FRAGMENT  //////////////////////////////////////////////////////

        if (openCart == 1){
            setTitle("Cart");

            fragment = new MyCartFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openCart =0;
        }
        else if (openHomeApp == 1){
            setTitle("Home Appliances");

            fragment = new HomeNeedsCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openHomeApp =0;
        }
        else if (openMen == 1){
            setTitle("Men Essentials");

            fragment = new MenNeedsCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openMen =0;
        }
        else if (openWomen == 1){
            setTitle("Women Essentials");

            fragment = new WomenNeedsCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openWomen =0;
        }
        else if (openKids == 1){
            setTitle("Kids Essentials");

            fragment = new KidsNeedsCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openKids =0;
        }
        else if (openBabies == 1){
            setTitle("Babies Essentials");

            fragment = new BabiesNeedsCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openBabies =0;
        }
        else if (openStudents == 1){
            setTitle("Student Essentials");

            fragment = new StudentsNeedsCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openStudents =0;
        }
        else if (openOthers == 1){
            setTitle("Other Essentials");

            fragment = new OtherNeedsCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openOthers =0;
        }
        else if (openMessages == 1){
            setTitle("Talk to us");

            fragment = new OtherNeedsCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openMessages =0;
        }
        else if (openMyOrders == 1){
            setTitle("My Orders");
            fragment = new MyOrdersFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();
            openMessages =0;
        }
        else {
            showHome();
        }

        /////////////////////////////////   UPDATE   /////////////////////////////////////

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE,MainActivity.this,RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE){
            Toast.makeText(MainActivity.this,"Starting download",Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK){
                Log.d("name","Update flow failed. Result code = "+resultCode);
            }
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_home_needs_category:
                fragmentClass = HomeNeedsCategoryFragment.class;
                break;
            case R.id.nav_men_needs_category:
                fragmentClass = MenNeedsCategoryFragment.class;
                break;
            case R.id.nav_women_needs_category:
                fragmentClass = WomenNeedsCategoryFragment.class;
                break;
            case R.id.nav_kids_needs_category:
                fragmentClass = KidsNeedsCategoryFragment.class;
                break;
            case R.id.nav_babies_needs_category:
                fragmentClass = BabiesNeedsCategoryFragment.class;
                break;
            case R.id.nav_students_needs_category:
                fragmentClass = StudentsNeedsCategoryFragment.class;
                break;
            case R.id.nav_other_needs_category:
                fragmentClass = OtherNeedsCategoryFragment.class;
                break;
            case R.id.nav_my_orders:
                fragmentClass = MyOrdersFragment.class;
                break;
            case R.id.nav_my_cart:
                fragmentClass = MyCartFragment.class;
                break;
            case R.id.nav_my_account:
                fragmentClass = MyAccountFragment.class;
                break;
            case R.id.nav_my_notifications:
                fragmentClass = OfferZoneFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(menuItem.getItemId() == R.id.nav_ask){
            Intent chatActivity = new Intent(this,ChatActivity.class);
            startActivity(chatActivity);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragmentManager.beginTransaction().replace(R.id.my_frame_layout, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.main_logout_option){
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            finish();
        }

        if (item.getItemId()==R.id.main_search_option){
            Intent searchProduct = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(searchProduct);
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
       DrawerLayout mDrawer = findViewById(R.id.my_nav_drawer);
        if (mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (fragment instanceof HomeFragment){
               finish();
            }
            else {
               showHome();
            }
        }
    }

    private void showHome() {
        fragment = new HomeFragment();
        setTitle("Home");
        if(fragment != null){
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.my_frame_layout,fragment).commit();
        }
    }
}
