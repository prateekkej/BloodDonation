package com.blooddonation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Prateek on 7/28/2017.
 */

public class Home extends Fragment {
   public View view;
    private TabLayout tabLayout;
    private ViewPager homePager;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
initializeViews(inflater,container);
        return view;


    }
private void initializeViews(LayoutInflater inflater,ViewGroup container)
{
    view=inflater.inflate(R.layout.home,container,false);
    tabLayout=(TabLayout)view.findViewById(R.id.homeTabLayout);
    homePager=(ViewPager)view.findViewById(R.id.homepager);
    MyAdapter  myAdapter= new MyAdapter(getActivity().getSupportFragmentManager());
    homePager.setAdapter(myAdapter);
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if(DonorsListFragment.adapter!=null){DonorsListFragment.adapter.notifyDataSetChanged();}
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    });
    tabLayout.setupWithViewPager(homePager);


}
class MyAdapter extends FragmentPagerAdapter{
    MapsFragment map;
    DonorsListFragment donorsListFragment;
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return "Map View";
        }
        else{
            return "List View";
        }
    }

    public MyAdapter(FragmentManager fragmentManager){
    super(fragmentManager);
    map=new MapsFragment();
    donorsListFragment= new DonorsListFragment();
}


    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return map;
                    }
        else{
            return donorsListFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}

}
