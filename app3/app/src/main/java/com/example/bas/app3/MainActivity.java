package com.example.bas.app3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if(savedInstanceState !=null){
            Fragment savedFragment = getSupportFragmentManager().getFragment(savedInstanceState, "FRAGMENT");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                    savedFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                    new HomeFragment()).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_layout);
        if(fragment != null){
            getSupportFragmentManager().putFragment(outState,"FRAGMENT", fragment);
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_registry:
                            selectedFragment = new AddFragment();
                            break;
                        case R.id.nav_reserve:
                            selectedFragment = new BookFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                            selectedFragment).commit();

                    return true;
                }
            };
}


