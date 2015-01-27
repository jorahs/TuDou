package com.luwei.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.luwei.potato.R;

public class SearchResultsActivity extends SherlockActivity {

    private TextView txtQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // get the action bar
        ActionBar actionBar = getSupportActionBar();

        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtQuery = (TextView) findViewById(R.id.txtQuery);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            txtQuery.setText("Search Query: " + query);
        }

    }


    @Override
    public boolean  onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
            overridePendingTransition(R.anim.back_left_in,R.anim.back_right_out);
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }

}
