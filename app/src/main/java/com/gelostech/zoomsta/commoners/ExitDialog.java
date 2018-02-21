package com.gelostech.zoomsta.commoners;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.activities.LoginActivity;

/**
 * Created by tirgei on 8/24/17.
 */

public class ExitDialog extends Dialog implements android.view.View.OnClickListener {

    private Activity activity;
    private Dialog dialog;
    private Button cancel, yes;
    private ImageView icon;
    private DatabaseHelper helper;

    public ExitDialog(Activity activity){
        super(activity);

        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.logout_dialog);
        cancel = findViewById(R.id.button_cancel);
        yes = findViewById(R.id.button_yes);
        icon = findViewById(R.id.exit_icon);

        helper = new DatabaseHelper(activity);

        cancel.setOnClickListener(this);
        yes.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_cancel:
                dismiss();
                break;
            case R.id.button_yes:
                ZoomstaUtil.clearPref(activity);
                helper.clearDb(true);

                Intent i = new Intent(activity, LoginActivity.class);
                activity.startActivity(i);
                activity.overridePendingTransition(R.anim.enter_signin, R.anim.exit_main);
                activity.finish();
                break;
            default:
                break;
        }

        dismiss();
    }
}
