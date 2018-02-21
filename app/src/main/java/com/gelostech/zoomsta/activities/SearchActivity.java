package com.gelostech.zoomsta.activities;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.adapters.HistoryAdapter;
import com.gelostech.zoomsta.adapters.SearchUsersAdapter;
import com.gelostech.zoomsta.commoners.DatabaseHelper;
import com.gelostech.zoomsta.commoners.InstaUtils;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.models.HistoryModel;
import com.gelostech.zoomsta.models.UserObject;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener{
    private MenuItem searchItem;
    private MaterialSearchView searchView;
    private Toolbar toolbar;
    private RecyclerView historyRv, searchRv;
    private List<HistoryModel> historyModels;
    private List<UserObject> userObjects;
    private HistoryAdapter historyAdapter;
    private DatabaseHelper helper;
    private String username;
    private SearchUsersAdapter searchAdapter;
    private CatLoadingView catLoadingView;
    private LinearLayout noHistory;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search_view);
        toolbar = findViewById(R.id.search_toolbar);
        historyRv = findViewById(R.id.history_rv);
        searchRv = findViewById(R.id.search_rv);
        noHistory = findViewById(R.id.no_search_history);

        historyRv.setHasFixedSize(true);
        searchRv.setHasFixedSize(true);
        historyRv.setNestedScrollingEnabled(true);
        searchRv.setNestedScrollingEnabled(true);
        historyModels = new ArrayList<>();
        userObjects = new ArrayList<>();
        helper = new DatabaseHelper(this);
        catLoadingView = new CatLoadingView();
        catLoadingView.setCancelable(false);
        databaseHelper = new DatabaseHelper(this);

        historyRv.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false));
        searchRv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        searchView.setOnSearchViewListener(this);
        searchView.setOnQueryTextListener(this);

        fetchHistory();

        setSupportActionBar(toolbar);


    }

    public void fetchHistory(){
        if(!historyModels.isEmpty())
            historyModels.clear();

        historyModels = helper.fetchHistory(this);
        historyAdapter = new HistoryAdapter(this, historyModels);

        if(historyModels.size() > 0){
            historyRv.setAdapter(historyAdapter);
            historyRv.setVisibility(View.VISIBLE);

            if(noHistory.isShown())
                noHistory.setVisibility(View.GONE);
        } else {
            if(historyRv.isShown())
                historyRv.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        searchItem = menu.findItem(R.id.search_icon);
        searchView.setMenuItem(searchItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        switch (i){
            case R.id.search_icon:
                searchView.showSearch();
                break;

            case android.R.id.home:
                if (searchView.isSearchOpen()) {
                    searchView.closeSearch();
                } else {
                    onBackPressed();
                }
                break;

            case R.id.clear:
                databaseHelper.clearDb(false);
                fetchHistory();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        username = query.trim();
        if(ZoomstaUtil.haveNetworkConnection(this))
            new SearchUserList().execute(new String[0]);
        else
            Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSearchViewShown() {
        if(historyRv.isShown())
            historyRv.setVisibility(View.GONE);
    }

    @Override
    public void onSearchViewClosed() {
        if(!historyRv.isShown())
            historyRv.setVisibility(View.VISIBLE);
    }

    private class SearchUserList extends AsyncTask<String, String, String> {
        String resp;

        private SearchUserList() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            searchAdapter = new SearchUsersAdapter(SearchActivity.this, userObjects);
            catLoadingView.show(getSupportFragmentManager(), null);
            if(!userObjects.isEmpty())
                userObjects.clear();

        }

        protected String doInBackground(String... args) {
            try {
                userObjects.clear();
                userObjects.addAll(InstaUtils.searchUser(SearchActivity.this, username));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.resp;
        }

        protected void onPostExecute(String img) {
            super.onPostExecute(img);

            catLoadingView.dismiss();

            if(userObjects.size() > 0){
                if(noHistory.isShown())
                    noHistory.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchRv.setAdapter(searchAdapter);
                        searchRv.setVisibility(View.VISIBLE);
                    }
                }, 200);

            } else
                Toast.makeText(SearchActivity.this, "No users found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(catLoadingView.isVisible())
            catLoadingView.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.enter_signin, R.anim.exit_main);
        }
    }
}
