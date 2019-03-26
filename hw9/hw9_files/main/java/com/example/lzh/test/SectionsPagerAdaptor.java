package com.example.lzh.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdaptor extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();

    public SectionsPagerAdaptor(FragmentManager fm) {super(fm);}

    public void addFragment(Fragment frag){
        fragmentList.add(frag);

    }

    public Fragment getItem(int position){

        return fragmentList.get(position);
    }

    public int getCount(){

        return fragmentList.size();
    }


}
