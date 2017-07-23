package com.fast_prog.dynate.utilities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import com.fast_prog.dynate.R;

/**
 * Created by sarathk on 2/22/17.
 */

public class MyCircularProgressDialog extends ProgressDialog {
    private ObjectAnimator anim;
    private ProgressBar mprogressBar;

    public MyCircularProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_circular_progress_dialog);

        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
    }

    @Override
    public void show() {
        super.show();
        anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, 100);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setDuration(2000);
        anim.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        anim.end();
    }

}

