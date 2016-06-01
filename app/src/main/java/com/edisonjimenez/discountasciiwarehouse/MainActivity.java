package com.edisonjimenez.discountasciiwarehouse;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private List<AsciiFace> mAsciiFaceList = new ArrayList<AsciiFace>();
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager mLayoutManager;
    private SearchView mSearchView;
    private String query;
    private SharedPreferences sharedPref;
    public static TextView mInfoTextView;
    private AlertDialog mOptionsDialog;
    private ProcessAsciiFaces processAsciiFaces;
    private Context mContext;

    private RecyclerView.OnScrollListener mOnScrollListener;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar();
        setTitle(R.string.app_name_shortened);
        mContext = this;
        

        query = getSavedPreferenceString(QUERY);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewAdapter = new RecyclerViewAdapter(mContext, new ArrayList<AsciiFace>());
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mInfoTextView = (TextView) findViewById(R.id.infoText);

        mOnScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount % 10 == 0 && totalItemsCount < 100) {
                    // Load more data
                    processAsciiFaces = new ProcessAsciiFaces(mContext, getSavedPreferenceString(QUERY), getSavedPreferenceInt(STOCK), page, true);
                    processAsciiFaces.execute();
                }
            }
        };

        mOptionsDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.search_options)
                .setSingleChoiceItems(R.array.stock_options, getSavedPreferenceInt(STOCK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.edit().putInt(STOCK, which).apply();
                    }
                })
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        processAsciiFaces = new ProcessAsciiFaces(mContext, getSavedPreferenceString(QUERY), getSavedPreferenceInt(STOCK), 0, false);
                        processAsciiFaces.execute();
                    }
                })
                .create();
    }

    // Remove all listeners when Activity is destroyed
    @Override
    protected void onStop() {
        super.onStop();
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        query = replaceSpacesWithComma(getSavedPreferenceString(QUERY));

        processAsciiFaces = new ProcessAsciiFaces(mContext, query, getSavedPreferenceInt(STOCK), 0, false);
        processAsciiFaces.execute();

        mRecyclerView.addOnScrollListener(mOnScrollListener);

        if (recyclerViewAdapter.getItemCount() == 0) {
            mInfoTextView.setVisibility(View.VISIBLE);
        }
        else {
            mInfoTextView.setVisibility(View.INVISIBLE);
        }
    }

    private String getSavedPreferenceString(String key) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getString(key, "");
    }

    private int getSavedPreferenceInt(String key) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getInt(key, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);

        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setIconifiedByDefault(true); // Default to a closed state upon application start
        mSearchView.setIconified(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                sharedPref.edit().putString(QUERY, s).apply();
                query = replaceSpacesWithComma(getSavedPreferenceString(QUERY));
                mSearchView.clearFocus();
                if(query.length() > 0) {
                    processAsciiFaces = new ProcessAsciiFaces(mContext, query, sharedPref.getInt(STOCK, 0), 0, false);
                    processAsciiFaces.execute();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setQuery(getSavedPreferenceString(QUERY), false);
            }
        });

        mSearchView.setOnCloseListener(null);
        return true;
    }

    /**
     * Replaces any spaces with commas. Needed to send comma-separated tags to API
     * @param query
     * @return the query if it doesn't contain spaces, else return the modified query
     */
    private String replaceSpacesWithComma(String query) {
        if (query.contains(" ")) {
            return query.replace(" ", ",");
        }

        return query;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_customize:
                mOptionsDialog.show();
                break;

            // Clear all filters and return to defaults
            case R.id.menu_clear:
                sharedPref.edit().putString(QUERY, "").apply();
                sharedPref.edit().putInt(STOCK, 0).apply();
                recyclerViewAdapter.replaceNewData(new ArrayList<AsciiFace>());
                mInfoTextView.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.cleared_success, Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ProcessAsciiFaces extends GetAsciiFaceNDJSON {
        private boolean appendData;

        public ProcessAsciiFaces(Context context, String searchCriteria, int onlyInStock, int page, boolean appendData) {
            super(context, searchCriteria, onlyInStock, page, appendData);
            this.appendData = appendData;
        }

        public void execute() {
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJSONData {

            protected void onPostExecute(String webData) {
                super.onPostExecute(webData);

                if (!appendData) {
                    recyclerViewAdapter.replaceNewData(getAsciiFaces());
                }
                else {
                    recyclerViewAdapter.addNewData(getAsciiFaces());
                }

            }
        }
    }

}
