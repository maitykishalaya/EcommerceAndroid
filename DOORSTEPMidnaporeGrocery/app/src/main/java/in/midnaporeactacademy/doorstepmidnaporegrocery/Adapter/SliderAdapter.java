package in.midnaporeactacademy.doorstepmidnaporegrocery.Adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;


import com.squareup.picasso.Picasso;

import java.util.List;

import in.midnaporeactacademy.doorstepmidnaporegrocery.ModelClass.SliderModel;
import in.midnaporeactacademy.doorstepmidnaporegrocery.R;

public class SliderAdapter extends PagerAdapter {
    private List<SliderModel> sliderModelList;

    public SliderAdapter(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.slider_layout, container, false);
        ConstraintLayout bannerContainer = view.findViewById(R.id.banner_container);
        bannerContainer.setBackgroundTintList(ColorStateList
                .valueOf(Color.parseColor(sliderModelList.get(position).getBackgroundColor())));

        ImageView banner = view.findViewById(R.id.banner_slide);

        container.removeView(view);
        Picasso.get()
                .load(sliderModelList.get(position).getBanner())
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .into(banner);
        container.addView(view, 0);
        return view;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return sliderModelList.size();
    }
}