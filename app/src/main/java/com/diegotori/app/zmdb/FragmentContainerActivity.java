package com.diegotori.app.zmdb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.diegotori.app.zmdb.fragment.TopTenMoviesFragment;

/**
 * Created by Diego on 10/23/2016.
 */

public class FragmentContainerActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA_FRAG_TYPE = "intent_extra_frag_type";

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        final FragmentManager fragMan = getSupportFragmentManager();
                        final ActionBar actionBar = getSupportActionBar();
                        if (fragMan.getBackStackEntryCount() > 0) {
                            actionBar.setDisplayHomeAsUpEnabled(true);
                        } else {
                            actionBar.setDisplayHomeAsUpEnabled(false);
                        }
                    }
                });
        if (savedInstanceState == null) {
            Fragment f = getFragment();
            if (f == null) {
                Toast.makeText(this, "Error: No fragment specified", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.main_container, f).commit();
            }
        }
    }

    private Fragment getFragment() {
        final String fragmentType = getIntent().getStringExtra(INTENT_EXTRA_FRAG_TYPE);
        if(fragmentType == null || fragmentType.isEmpty()){
            return null;
        }
        Fragment result = null;
        if(fragmentType.equals(MainActivity.FRAG_TOP_TEN_MOVIES)){
            result = new TopTenMoviesFragment();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
        }
    }
}
