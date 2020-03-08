package com.example.opreaghizelamaria.licenta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class ChatMap extends AppCompatActivity {



    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_map);

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));


        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.app_name,R.string.app_name);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile) {
                    intent = new Intent(ChatMap.this, Profile.class);
                    startActivity(intent);
                } else if (id == R.id.chat) {

                    intent = new Intent(ChatMap.this, ChatMap.class);
                    startActivity(intent);
                } else if (id == R.id.home) {
                    intent = new Intent(ChatMap.this, Main.class);
                    startActivity(intent);
                } else if (id == R.id.monitorizare) {
                    intent = new Intent(ChatMap.this, Monitoring.class);
                    startActivity(intent);
                }
                return true;

            }
        });


    }@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return MapFragment.newInstance("FirstFragment", "Instance 1");
                case 1:
                    return ChatFragment.newInstance("SecondFragment"," Instance 1");
                default:
                    return MapFragment.newInstance("ThirdFragment"," Default");
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
