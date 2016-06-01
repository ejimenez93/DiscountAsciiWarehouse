package com.edisonjimenez.discountasciiwarehouse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetAsciiFaceNDJSON extends GetRawJSON {

    private String TAG = GetAsciiFaceNDJSON.class.getSimpleName();
    private List<AsciiFace> mAsciiFaces;
    private Uri mDestinationUri;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private AlertDialog mNoResultsDialog;

    public GetAsciiFaceNDJSON(Context context, String searchCriteria, int onlyInStock, int page, boolean appendData) {
        super(null);
        this.mContext = context;
        createAndUpdateUri(searchCriteria, onlyInStock, page);
        mAsciiFaces = new ArrayList<AsciiFace>();

        // Setup Progress Dialog
        mProgressDialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Retrieving Faces...");

        mNoResultsDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                .setCancelable(true)
                .setPositiveButton(R.string.okay, null)
                .setTitle(R.string.no_results_label)
                .setMessage(R.string.no_results)
                .create();
    }

    public void execute() {
        super.setmRawURL(mDestinationUri.toString());
        DownloadJSONData downloadJSONData = new DownloadJSONData();
        Log.v(TAG, "Built URI = " + mDestinationUri.toString());
        downloadJSONData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(String searchCriteria, int onlyInStock, int page) {
        final String BASE_URL = "http://74.50.59.155:5000/api/search";
        final String TAGS_PARAM = "q";
        final String ONLY_IN_STOCK_PARAM = "onlyInStock";
        final String SKIP_PARAM="skip";

        mDestinationUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_PARAM, searchCriteria)
                .appendQueryParameter(ONLY_IN_STOCK_PARAM, Integer.toString(onlyInStock))
                .appendQueryParameter(SKIP_PARAM, Integer.toString(page * 10))
                .build();

        return mDestinationUri != null;
    }

    public List<AsciiFace> getAsciiFaces() {
        return mAsciiFaces;
    }

    public void processResult() {
        if (getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(TAG, "Error downloading the raw data");
            return;
        }

        // All the fields that come back from the JSON object
        final String DAW_TYPE = "type";
        final String DAW_ID = "id";
        final String DAW_SIZE = "size";
        final String DAW_PRICE = "price";
        final String DAW_FACE = "face";
        final String DAW_STOCK = "stock";
        final String DAW_TAGS = "tags";

        try {
            String ndJSON = getmData();
            String[] itemsArray = ndJSON.split("\n");

            if (!ndJSON.equals("")) {

                MainActivity.mInfoTextView.setVisibility(View.INVISIBLE);
                for (int x = 0; x < itemsArray.length; x++) {
                    JSONObject asciiFaceJSON = new JSONObject(itemsArray[x]);

                    String type = asciiFaceJSON.getString(DAW_TYPE);
                    String id = asciiFaceJSON.getString(DAW_ID);
                    int size = asciiFaceJSON.getInt(DAW_SIZE);
                    int price = asciiFaceJSON.getInt(DAW_PRICE);
                    String face = asciiFaceJSON.getString(DAW_FACE);
                    int stock = asciiFaceJSON.getInt(DAW_STOCK);
                    String tags = asciiFaceJSON.getString(DAW_TAGS);

                    AsciiFace photoObject = new AsciiFace(type, id, size, price, face, stock, tags);

                    this.mAsciiFaces.add(photoObject);
                }

                for (AsciiFace singleFace : mAsciiFaces) {
                    Log.v(TAG, singleFace.toString());
                }

            }
            else {
                throw new Exception("No Results");
            }
        } catch (JSONException jsonError) {
            jsonError.printStackTrace();
            Log.e(TAG, "Error Processing JSON Data");
        }
        catch (Exception e) {
            if (e.getMessage().equals("No Results")) {
                mNoResultsDialog.show();
                MainActivity.mInfoTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public class DownloadJSONData extends DownloadRawData {

        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();

            // Hide Loading Progress
            if (mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }

        protected String doInBackground(String... params) {
            String[] parameters = {
                mDestinationUri.toString()
            };

            return super.doInBackground(parameters);
        }
    }

}

