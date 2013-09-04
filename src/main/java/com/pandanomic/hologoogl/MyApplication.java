package com.pandanomic.hologoogl;

import android.app.Application;
import android.util.Log;

import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        mailTo = "pandanomic@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_alert, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        Log.i("MyApplication", "ACRA initialized");
    }
}
