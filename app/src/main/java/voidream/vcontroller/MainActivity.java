/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package voidream.vcontroller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.PagerSlidingTabStrip.IconTabProvider;

public class MainActivity extends FragmentActivity {

	private final Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        setTitle("Controller");
        assert getActionBar() != null;
        getActionBar().setIcon(R.drawable.ic_launcher);
        getActionBar().setHomeButtonEnabled(true);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

		pager.setAdapter(adapter);
		tabs.setIndicatorColor(0xFF29B6F6);
		tabs.setIndicatorHeight(300);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		tabs.setShouldExpand(true);
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 1:
                        setTitle("Log");
                        break;
                    case 2:
                        setTitle("Settings");
                        break;
                    default:
                        setTitle("Controller");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

            case R.id.home:
                startActivity(new Intent(this, AboutUs.class));
                break;

			case R.id.menu_help:
				Intent intent_help= new Intent(MainActivity.this, HowItWorks.class);
				startActivity(intent_help);
				break;

			case R.id.menu_command:
				Intent intent_io_command= new Intent(MainActivity.this, AboutUs.class);
				startActivity(intent_io_command);
				break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			assert getActionBar() != null;
			getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	public static final class MyPagerAdapter extends FragmentPagerAdapter implements IconTabProvider{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public static int [] TITLES = {R.drawable.ic_controller, R.drawable.ic_log,
				R.drawable.ic_customize};

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			return FragmentAdapter.newInstance(position);
		}

		@Override
		public int getPageIconResId(int position) {
			return TITLES[position];
		}
	}

}