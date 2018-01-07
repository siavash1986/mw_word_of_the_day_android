package me.siavash.android.wotd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.siavash.android.wotd.BuildConfig;
import me.siavash.android.wotd.R;

public class AboutActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);

    initView();
    setTitle("About");
    TextView textViewVersion = findViewById(R.id.app_version);
    textViewVersion.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));

  }

  public void openSourceLicense(View view) {
    startActivity(new Intent(this, OpenSourceLicensesActivity.class));
  }

}
