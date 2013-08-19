package com.pandanomic.hologoogl;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pandanomic.hologoogl.URLContent.ShortenedURLContent;

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
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The URLContent content this fragment is presenting.
     */
    private ShortenedURLContent.ShortenedURLItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public URLDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the URLContent content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ShortenedURLContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }

        getURLStats();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_url_detail, container, false);

        // Show the URLContent content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.url_detail)).setText(mItem.getMetrics());
        }

        return rootView;
    }

    private void getURLStats() {
        JSONObject result;
        String resultURL = null;
        String longUrl = null;
        String created = null;
        try {
            result = new GetTask(this.getActivity(), 1).execute(mItem.content).get(5, TimeUnit.SECONDS);

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

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Long URL");
        alert.setCancelable(true);
        alert.setMessage(longUrl);
        alert.show();
    }
}
