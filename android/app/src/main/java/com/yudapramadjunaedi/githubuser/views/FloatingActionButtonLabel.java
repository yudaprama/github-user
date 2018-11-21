package com.yudapramadjunaedi.githubuser.views;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.widget.TextView;

import com.yudapramadjunaedi.githubuser.R;
import com.yudapramadjunaedi.githubuser.utils.ViewUtils;

/**
 * Created by yuda on 2/9/18.
 * Copyright © 2018 Tebengan. All rights reserved.
 */


public class FloatingActionButtonLabel extends TextView {
  private int mVerticalPadding = (int) ViewUtils.convertDpToPixel(4);
  private int mHorizontalPadding = (int) ViewUtils.convertDpToPixel(8);

  public FloatingActionButtonLabel(Context context) {
    super(context);
    this.init(context);
  }

  private void init(Context context) {
    setDefaultStyles();
  }

  private void setDefaultStyles() {
    setMinHeight((int) ViewUtils.convertDpToPixel(24));
    ViewCompat.setElevation(this, (int) ViewUtils.convertDpToPixel(1));

    setBackgroundResource(R.drawable.label_corners);
    setTextSize(14);
    setTypeface(getTypeface(), Typeface.BOLD);

    setGravity(Gravity.CENTER);
    setPadding(mHorizontalPadding, mVerticalPadding, mHorizontalPadding, mVerticalPadding);
  }

  public void setBackgroundColor(@ColorInt int color) {
    GradientDrawable drawable = (GradientDrawable) getBackground();
    drawable.setColor(color);
  }
}