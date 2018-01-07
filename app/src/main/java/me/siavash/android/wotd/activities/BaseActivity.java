package me.siavash.android.wotd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import me.siavash.android.wotd.R;
import me.siavash.android.wotd.util.DifferedEntityUpdate;

import static me.siavash.android.wotd.GlobalInfo.GOOGLE_PLAY_URL;


public abstract class BaseActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private final String TAG = this.getClass().getSimpleName();
  private NavigationView mNavigationView;
  private DrawerLayout mDrawerLayout;
  private static Map<String, Integer> activityMap = new HashMap<>();
  protected String mActivityName = this.getClass().getSimpleName();

  static {
    activityMap.put(MainActivity.class.getSimpleName(), R.id.nav_home);
    activityMap.put(Main2Activity.class.getSimpleName(), R.id.nav_archive);
    activityMap.put(HistoryActivity.class.getSimpleName(), R.id.nav_history);
    activityMap.put(FavoritesActivity.class.getSimpleName(), R.id.nav_favorite);
    activityMap.put(AboutActivity.class.getSimpleName(), R.id.nav_about);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DifferedEntityUpdate.getInstance(this).updateEntities();

  }

  @Override
  protected void onStop() {
    super.onStop();
    DifferedEntityUpdate.getInstance(this).updateEntities();
  }

  protected void initView() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mDrawerLayout = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    mDrawerLayout.addDrawerListener(toggle);
    toggle.syncState();


    mNavigationView = findViewById(R.id.nav_view);
    mNavigationView.setNavigationItemSelectedListener(this);

    setNavigationSelectedMenu();

  }

  private void setNavigationSelectedMenu() {
    mNavigationView.setCheckedItem(activityMap.get(TAG));
  }


  @Override
  public final boolean onNavigationItemSelected(MenuItem item) {

    int id = item.getItemId();
    Intent intent = null;

    if (id == R.id.nav_home && !mActivityName.equals(MainActivity.class.getSimpleName())) {
      intent = new Intent(this, MainActivity.class);
      mNavigationView.setCheckedItem(id);
    } else if (id == R.id.nav_favorite && !mActivityName.equals(FavoritesActivity.class.getSimpleName())) {
      intent = new Intent(this, FavoritesActivity.class);

    } else if (id == R.id.nav_history && !mActivityName.equals(HistoryActivity.class.getSimpleName())) {
      intent = new Intent(this, HistoryActivity.class);

    } else if (id == R.id.nav_archive && !mActivityName.equals(Main2Activity.class.getSimpleName())) {
      intent = new Intent(this, Main2Activity.class);

    } else if (id == R.id.nav_share) {
      String APP_PACKAGE_NAME = this.getPackageName();
      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      shareIntent.putExtra(Intent.EXTRA_TEXT,
          getString(R.string.tell_your_friends_msg) + " " + GOOGLE_PLAY_URL
              + APP_PACKAGE_NAME);
      startActivity(Intent.createChooser(shareIntent,
          getString(R.string.tell_your_friends)));

    } else if (id == R.id.nav_about && !mActivityName.equals(AboutActivity.class.getSimpleName())) {
      intent = new Intent(this, AboutActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    }

    if (intent != null) {

      startActivity(intent);
    }


    mDrawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }


  @Override
  public void onBackPressed() {
    mDrawerLayout = findViewById(R.id.drawer_layout);
    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
      mDrawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.menu_about) {
      if (!mActivityName.equals(AboutActivity.class.getSimpleName())) {
        Intent i = new Intent(this, AboutActivity.class);
        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
      }
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main2, menu);
    return true;
  }

}
