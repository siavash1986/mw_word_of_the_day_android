package me.siavash.android.wotd.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import me.siavash.android.wotd.R;

public class OpenSourceLicensesActivity extends AppCompatActivity {

  private static final String LICENSES_HTML_PATH = "file:///android_asset/open_source_licenses.html";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_open_source_licenses);

    WebView webViewOpenSourceLicenses = findViewById(R.id.web_view_open_source_licenses);
    webViewOpenSourceLicenses.getSettings().setLoadWithOverviewMode(true);
    webViewOpenSourceLicenses.getSettings().setUseWideViewPort(true);
    webViewOpenSourceLicenses.getSettings().setTextZoom(250);
    webViewOpenSourceLicenses.loadUrl(LICENSES_HTML_PATH);

  }
}
