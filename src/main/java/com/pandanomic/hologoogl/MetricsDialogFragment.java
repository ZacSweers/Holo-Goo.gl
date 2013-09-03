package com.pandanomic.hologoogl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

/**
 * Created by pandanomic on 9/3/13.
 */
public class MetricsDialogFragment extends DialogFragment {

    public MetricsDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogListView chgList= (ChangeLogListView) layoutInflater.inflate(R.layout.changelog_fragment_dialogstandard, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle("URL Metrics")
                .setView(chgList)
                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();

    }
}
