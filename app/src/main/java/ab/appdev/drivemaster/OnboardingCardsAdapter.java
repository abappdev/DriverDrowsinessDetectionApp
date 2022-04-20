package ab.appdev.drivemaster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;


class OnboardingCardsAdapter extends PagerAdapter {

    private Context mContext;
    private List<IntroViewItem> mListScreen;

    public OnboardingCardsAdapter(Context context, List<IntroViewItem> listScreen) {
        mContext = context;
        mListScreen = listScreen;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_onboarding_card, null);

        ImageView imageView = layoutScreen.findViewById(R.id.intro_image);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView subtitle = layoutScreen.findViewById(R.id.intro_subtitle);

        title.setText(mListScreen.get(position).title);
        subtitle.setText(mListScreen.get(position).description);
        imageView.setImageResource(mListScreen.get(position).image);


//        subtitle.setTextColor(Color.parseColor(mListScreen[position].color))
        container.addView(layoutScreen);


        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object==view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);

    }
}
