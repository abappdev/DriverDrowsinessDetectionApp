package ab.appdev.drivemaster;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingCardsAdapter introViewAdapter;

    private ViewPager slider;

    private TabLayout selected_page_indicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_onboarding);

        slider = (ViewPager) findViewById(R.id.view_pager);
        selected_page_indicator = findViewById(R.id.selected_page_indicator);
        introViewAdapter = new OnboardingCardsAdapter(this, Configurable.getIntroViewList());


        slider.setAdapter(introViewAdapter);


        selected_page_indicator.setupWithViewPager(slider);

    }

    public void onClose(View view) {
        finish();
    }

    public void nextPressed(View view) {

        int pose = slider.getCurrentItem() + 1;
        slider.setCurrentItem(pose < introViewAdapter.getCount() ? pose : 0);

    }

}
