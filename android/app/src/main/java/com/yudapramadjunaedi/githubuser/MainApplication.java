package com.yudapramadjunaedi.githubuser;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import com.facebook.react.ReactPackage;
import com.yudapramadjunaedi.githubuser.progresshub.ProgressHUBPackage;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends NavigationApplication {

  @Override
  public boolean isDebug() {
    return BuildConfig.DEBUG;
  }

  @SuppressLint("MissingPermission")
  protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
        new ProgressHUBPackage()
    );
  }

  @Override
  public List<ReactPackage> createAdditionalReactPackages() {
    return getPackages();
  }

  @Nullable
  @Override
  public String getJSMainModuleName() {
    return "index";
  }
}
