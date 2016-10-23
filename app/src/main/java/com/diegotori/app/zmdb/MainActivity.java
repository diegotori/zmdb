package com.diegotori.app.zmdb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener  {
    public static final String FRAG_TOP_TEN_MOVIES = "TopTenMoviesFragment";
    public static final String FRAG_DISCOVER = "MovieDiscoveryFragment";
    private DemoItem[] demoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        demoItems = createDemoItems();
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                demoItems));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(demoItems[position].intent);
    }

    private DemoItem[] createDemoItems() {
        return new DemoItem[] {
            new DemoItem(getString(R.string.top_ten_movies_title),
                    new Intent(this, FragmentContainerActivity.class)
                        .putExtra(FragmentContainerActivity.INTENT_EXTRA_FRAG_TYPE,
                                FRAG_TOP_TEN_MOVIES))
        };
    }

    static class DemoItem {
        String name;
        Intent intent;

        DemoItem(final String name, final Intent intent){
            this.name = name;
            this.intent = intent;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
