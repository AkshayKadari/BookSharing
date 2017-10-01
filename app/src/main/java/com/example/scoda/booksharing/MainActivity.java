package com.example.scoda.booksharing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.net.Uri;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements
        SignUpFragment.OnFragmentInteractionListener
        , NavigationView.OnNavigationItemSelectedListener {

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    final static int REQ_CODE = 1;

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    static String userName;
    static String password;

    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.syncState();
        mDrawer.setDrawerListener(actionBarDrawerToggle);
        getFragmentManager().beginTransaction().add(R.id.container, new LoginFragment(), "login").commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.navHome:
                this.gotoHomeFragment(userName,password);
                break;
            case R.id.navPostBook:
                this.gotoPostBookFragment();
                break;
            case R.id.navMyBooks:
                this.gotoMyBooksFragment();
                break;
            case R.id.navMessages:
                this.gotoMessagesFragment();
                break;
            case R.id.navLogout:
                this.gotoLoginFragment();
                break;
            default:
                break;
        }

        setTitle(menuItem.getTitle());
        nvDrawer.getMenu().getItem(1).setChecked(true);
        onNavigationItemSelected(nvDrawer.getMenu().getItem(1));
        menuItem.setChecked(true);
        mDrawer.closeDrawers();
    }

    public void gotoMessagesFragment() {

        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        MessagesFragment messagesFragment = new MessagesFragment();
        messagesFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, messagesFragment, "MyBooks").addToBackStack(null).commit();

    }

    private void gotoMyBooksFragment() {

        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        MyBooksFragment myBooksFragment = new MyBooksFragment();
        myBooksFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, myBooksFragment, "MyBooks").addToBackStack(null).commit();

    }

    private void gotoPostBookFragment() {

        Bundle bundle = new Bundle();
        bundle.putString("userName",userName);
        PostBookFragment postBookFragment = new PostBookFragment();
        postBookFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container,postBookFragment, "post").addToBackStack(null).commit();

    }

    public void gotoSignUpFragment() {

        getFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment(), "signUp").addToBackStack(null).commit();
    }


    public void gotoLoginFragment() {

        getFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment(), "login").commit();
    }

    public void gotoHomeFragment(String userName,String password) {

        NavigationView navigationView = (NavigationView)findViewById(R.id.nvView);
        View hView = navigationView.getHeaderView(0);
        ImageView iv = (ImageView) hView.findViewById(R.id.ivHeaderPhoto);
        iv.setImageResource(R.drawable.default_user_icon_profile);
        TextView headerName = (TextView) hView.findViewById(R.id.ivHeaderText);
        headerName.setText(userName);


        MainActivity.userName = userName;
        MainActivity.password = password;
        Bundle bundle = new Bundle();
        bundle.putString("userName",userName);
        bundle.putString("password", password);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, homeFragment, "Home").addToBackStack(null).commit();
    }
/*    public void gotoHomeFragment() {

        getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(), "Home").addToBackStack(null).commit();
    }*/

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}
