package me.siavash.android.wotd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import me.siavash.android.wotd.R;

public class IntroActivity extends AppIntro2 {


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setColorTransitionsEnabled(true);
    setImmersiveMode(true);


    SliderPage sliderPage1 = new SliderPage();
    sliderPage1.setTitle(getString(R.string.slide1_title));
    sliderPage1.setDescription(getString(R.string.slide1_description));
    sliderPage1.setImageDrawable(R.drawable.mw_logo);
    sliderPage1.setBgColor(getResources().getColor(R.color.intro1));

    addSlide(AppIntroFragment.newInstance(sliderPage1));

    SliderPage sliderPage2 = new SliderPage();
    sliderPage2.setTitle(getString(R.string.slide2_title));
    sliderPage2.setDescription(getString(R.string.slide2_description));
    sliderPage2.setImageDrawable(R.drawable.anki);
    sliderPage2.setBgColor(getResources().getColor(R.color.intro2));
    addSlide(AppIntroFragment.newInstance(sliderPage2));

//    SliderPage sliderPage3 = new SliderPage();
//    sliderPage3.setTitle("Simple, yet Customizable");
//    sliderPage3.setDescription("The library offers a lot of customization, while keeping it simple for those that like simple.");
////        sliderPage3.setImageDrawable(R.drawable.ic_slide3);
//    sliderPage3.setBgColor(getResources().getColor(R.color.intro3));
//    addSlide(AppIntroFragment.newInstance(sliderPage3));
//
//    SliderPage sliderPage4 = new SliderPage();
//    sliderPage4.setTitle("Explore");
//    sliderPage4.setDescription("Feel free to explore the rest of the library demo!");
////        sliderPage4.setImageDrawable(R.drawable.ic_slide4);
//    sliderPage4.setBgColor(getResources().getColor(R.color.intro4));
//    addSlide(AppIntroFragment.newInstance(sliderPage4));


    showSkipButton(true);

    setProgressButtonEnabled(true);


  }

  @Override
  public void onSkipPressed(Fragment currentFragment) {
    super.onSkipPressed(currentFragment);
    // Do something when users tap on Skip button.
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  @Override
  public void onDonePressed(Fragment currentFragment) {
    super.onDonePressed(currentFragment);
    // Do something when users tap on Done button.
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  @Override
  public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
    super.onSlideChanged(oldFragment, newFragment);
    // Do something when the slide changes.
  }
}
