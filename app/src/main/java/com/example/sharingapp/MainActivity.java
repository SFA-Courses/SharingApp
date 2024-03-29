package com.example.sharingapp;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Home Activity of the App
 */
public class MainActivity extends AppCompatActivity {

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get databse ready -- calls constructor to load items
        DatabaseManager.getInstance();

        setContentView(R.layout.activity_main);

        Intent intent = getIntent(); // from LoginActivity
        user_id = intent.getStringExtra("user_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), user_id);

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent search_intent = new Intent(this, SearchActivity.class);
                search_intent.putExtra("user_id", user_id);
                startActivity(search_intent);
                return true;
            case R.id.borrowed_items:
                Intent borrowed_intent = new Intent(this, BorrowedItemsActivity.class);
                borrowed_intent.putExtra("user_id", user_id);
                startActivity(borrowed_intent);
                return true;
            case R.id.edit_profile:
                Intent profile_intent = new Intent(this, EditUserActivity.class);
                profile_intent.putExtra("user_id", user_id);
                startActivity(profile_intent);
                return true;
            case R.id.logout:
                Intent logout_intent = new Intent(this, LoginActivity.class);
                Toast.makeText(getApplicationContext(), "Goodbye", Toast.LENGTH_SHORT).show();
                startActivity(logout_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent logout_intent = new Intent(this, LoginActivity.class);
        startActivity(logout_intent);
    }

    public void addItemActivity(View view) {
        Intent intent = new Intent(this, AddItemActivity.class);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }
}
