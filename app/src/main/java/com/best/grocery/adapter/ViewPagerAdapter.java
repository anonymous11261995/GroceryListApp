package com.best.grocery.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.best.grocery.R;


/**
 * Created by TienTruong on 8/7/2018.
 */

@SuppressWarnings("CanBeFinal")
public class ViewPagerAdapter extends PagerAdapter {
    private int[] layouts;
    private Context context;


    public ViewPagerAdapter(int[] layouts, Context context) {
        this.layouts = layouts;
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert layoutInflater != null;
        View view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);
        if (position == 4) {
            TextView text = view.findViewById(R.id.welcome_desc_5);
            text.setMovementMethod(LinkMovementMethod.getInstance());
            text.setText(Html.fromHtml(context.getResources().getString(R.string.slide_5_desc)));
        }

        return view;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
