package cz.aspone.drivers_score.driversscore.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.aspone.drivers_score.driversscore.BO.User;
import cz.aspone.drivers_score.driversscore.DB.Loader;
import cz.aspone.drivers_score.driversscore.Fragments.CameraFragment;
import cz.aspone.drivers_score.driversscore.Fragments.PlateFragment;
import cz.aspone.drivers_score.driversscore.Helpers.DriversScore;
import cz.aspone.drivers_score.driversscore.Helpers.ExceptionHandler;
import cz.aspone.drivers_score.driversscore.Helpers.RenderHelper;
import cz.aspone.drivers_score.driversscore.Helpers.SavedSharedPreferences;
import cz.aspone.drivers_score.driversscore.R;

/**
 * Created by Ondrej Vondra on 18.01.2018.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    private RenderHelper helper;
    private Map<String, ?> settings;
    private boolean doubleBackToExitPressedOnce = false;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_main);

        user = ((DriversScore) this.getApplication()).getUser();

        if (user == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        } else {

            settings = SavedSharedPreferences.getSettings(MainActivity.this);

            helper = new RenderHelper(this, settings);

            helper.startSync();

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            getSupportActionBar().setHomeButtonEnabled(true);

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    helper.selectImageOption();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            viewPager = (ViewPager) findViewById(R.id.viewPager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            tabLayout.setupWithViewPager(viewPager);//setting tab over viewpager

            //Implementing tab selected listener over tablayout
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager
                    switch (tab.getPosition()) {
                        case 0: // List
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            getSupportActionBar().show();
                            fab.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.VISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                            helper.stopRecording();
                            break;
                        case 1: // Camera
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            tabLayout.postDelayed(new Runnable() {
                                public void run() {
                                    tabLayout.setVisibility(View.GONE);
                                }
                            }, 2000);
                            getSupportActionBar().hide();
                            fab.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                            CameraFragment cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + viewPager.getCurrentItem());
                            helper.startRecording(cameraFragment);
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View header = navigationView.getHeaderView(0);
            TextView tvUserName = (TextView) header.findViewById(R.id.userName);
            TextView tvUserRole = (TextView) header.findViewById(R.id.userRole);

            tvUserName.setText(user.getLogin());
            tvUserRole.setText(user.getType().getKey());

            TextView tvLogOut = (TextView) header.findViewById(R.id.logOut);

            tvLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SavedSharedPreferences.deletePreference("User", MainActivity.this);
                    Loader.deleteAllTables();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                moveTaskToBack(true);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getSelectedItem(item.getItemId());
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    private void getSelectedItem(int itemId) {
        switch (itemId) {
            case R.id.nav_manage:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_sync:
                user = ((DriversScore) this.getApplication()).getUser();

                helper = new RenderHelper(this, settings);
                helper.synchronize();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getSelectedItem(item.getItemId());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.getPlateFromResult(requestCode, resultCode, data, user);
        PlateFragment plateFragment = (PlateFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 0);
        plateFragment.refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlateFragment plateFragment = (PlateFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 0);
        if (plateFragment != null)
            plateFragment.refreshList();
    }


    //Setting View Pager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new PlateFragment(), getString(R.string.plates));
        adapter.addFrag(new CameraFragment(), getString(R.string.CarMode));
        viewPager.setAdapter(adapter);
    }


    //View Pager fragments setting adapter class
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();//fragment arraylist
        private final List<String> mFragmentTitleList = new ArrayList<>();//title arraylist

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        //adding fragments and title method
        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
