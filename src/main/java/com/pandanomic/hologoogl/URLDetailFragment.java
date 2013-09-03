package com.pandanomic.hologoogl;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A fragment representing a single URL detail screen.
 * This fragment is either contained in a {@link URLListActivity}
 * in two-pane mode (on tablets) or a {@link URLDetailActivity}
 * on handsets.
 */
public class URLDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_URL_STRING = "url_string";

    /**
     * The URLContent content this fragment is presenting.
     */
    private String shortenedURL;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public URLDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_URL_STRING)) {
            shortenedURL = getArguments().getString(ARG_URL_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_url_detail, container, false);

        if (shortenedURL != null) {
            ((TextView) rootView.findViewById(R.id.url_detail)).setText(shortenedURL);
        }

        getURLStats(rootView);

        return rootView;
    }

    private void getURLStats(View v) {
        JSONObject result;
        String resultURL = null;
        String longUrl = null;
        String created = null;
        try {
            result = new GetTask(this.getActivity(), 1).execute(shortenedURL).get(5, TimeUnit.SECONDS);

            Log.d("getURLStats", result.toString());

            if (result == null) {
                Toast.makeText(this.getActivity(), "Error retrieving data", Toast.LENGTH_LONG).show();
                return;
            }

            // TODO: Check for error

            resultURL = result.getString("id");
            longUrl = result.getString("longUrl");
            created = result.getString("created");


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        ((TextView) v.findViewById(R.id.url_detail)).setText(longUrl);

//        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//        alert.setTitle("Long URL");
//        alert.setCancelable(true);
//        alert.setMessage(longUrl);
//        alert.show();
    }
}
