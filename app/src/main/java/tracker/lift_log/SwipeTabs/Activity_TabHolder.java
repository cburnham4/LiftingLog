package tracker.lift_log.SwipeTabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import tracker.lift_log.Helpers.EdgeToEdgeHelper;
import tracker.lift_log.R;

public class Activity_TabHolder extends AppCompatActivity {

    private int lid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabholder);
        EdgeToEdgeHelper.apply(this, findViewById(android.R.id.content));

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        Intent receivedIntent = getIntent();
        lid = receivedIntent.getIntExtra("LID", 0);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment fragmentCurrentDay = new Fragment_CurrentDay();
        Fragment fragmentPastDates = new Fragment_PastDates();
        Fragment fragmentGraph = new Fragment_Graph();

        Bundle args = new Bundle();
        args.putInt("lid", lid);
        fragmentCurrentDay.setArguments(args);
        fragmentPastDates.setArguments(args);
        fragmentGraph.setArguments(args);

        adapter.addFragment(fragmentCurrentDay, "Current Day");
        adapter.addFragment(fragmentPastDates, "Past Days");
        adapter.addFragment(fragmentGraph, "Graph");
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
