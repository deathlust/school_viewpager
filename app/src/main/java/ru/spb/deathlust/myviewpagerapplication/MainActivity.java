package ru.spb.deathlust.myviewpagerapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ViewPager.PageTransformer, ViewPager.OnPageChangeListener, DeletePageDialogFragment.OnResultListener {
    DialogFragment mDialog;

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter());
        pager.addOnPageChangeListener(this);
        pager.setPageTransformer(true, this);
        mDialog = new DeletePageDialogFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {
            mDialog.show(getSupportFragmentManager(), "dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(boolean delete) {
        if (delete) {
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            ((MyPagerAdapter)pager.getAdapter()).removeView(pager.getCurrentItem());
        }
    }

    private static class MyPagerAdapter extends PagerAdapter {
        private static final int PAGE_COUNT = 5;
        final private List<Integer> mList = new ArrayList<>(PAGE_COUNT);

        private final HashMap<Integer, View> mMap = new HashMap<>(PAGE_COUNT);

        public MyPagerAdapter() {
            super();
            for (int i = 1; i <= PAGE_COUNT; ++i)
                mList.add(i);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final int n = mList.get(position);
            final Context context = container.getContext();
            final TextView view = new TextView(context);
            view.setGravity(Gravity.CENTER);
            view.setText(String.format(context.getString(R.string.page_text_format), n));
            view.setBackgroundColor(n % 2 == 0 ? Color.GREEN : Color.RED);
            mMap.put(n, view);
            container.addView(view,
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
            return n;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mMap.containsKey(object)) {
                container.removeView(mMap.remove(object));
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(mMap.get(object));
        }

        @Override
        public int getItemPosition(Object object) {
            if (mList.contains(object)) {
                return mList.indexOf(object);
            }
            return POSITION_NONE;
        }

        public void removeView(int index) {
            mList.remove(index);
            notifyDataSetChanged();
        }
    }
}
