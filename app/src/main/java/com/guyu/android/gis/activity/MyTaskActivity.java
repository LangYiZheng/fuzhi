package com.guyu.android.gis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.guyu.android.R;

import java.util.ArrayList;

/**
 * Created by YunHuan on 2017/12/11.
 */

public class MyTaskActivity extends FragmentActivity
        implements View.OnClickListener,RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener
{

    private RadioButton btnNotProcessed;
    private RadioButton btnProcessed;
    private ViewPager mViewPager;

    //动画图片偏移量
    private int offset = 0;
    private int position_one;

    //动画图片宽度
    private int bmpW;

    //当前页卡编号
    private int currIndex = 0;

    //存放Fragment
    private ArrayList<Fragment> fragmentArrayList;

    //管理Fragment
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytask);
        initView();
    }

    private void initView() {
        btnNotProcessed = (RadioButton) findViewById(R.id.btnNotProcessed);
        btnProcessed = (RadioButton)findViewById(R.id.btnProcessed);
        mViewPager = (ViewPager) findViewById(R.id.vPager);
        btnNotProcessed.setOnClickListener(this);
        btnProcessed.setOnClickListener(this);

        RadioGroup rg = (RadioGroup) findViewById(R.id.rgroup);
        rg.setOnCheckedChangeListener(this);

        fragmentArrayList = new ArrayList<Fragment>();
        addFragment();
        fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter( new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));

        //让ViewPager缓存2个页面
        mViewPager.setOffscreenPageLimit(2);

        //设置默认打开第一页
        mViewPager.setCurrentItem(0);


//        pictureTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));

        //设置viewpager页面滑动监听事件
        mViewPager.addOnPageChangeListener(this);


        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 获取分辨率宽度
        int screenW = dm.widthPixels;

        bmpW = (screenW / 2);

        //设置动画图片宽度
//        setBmpW(cursor, bmpW);
        offset = 0;

        //动画图片偏移量赋值
        position_one = (int) (screenW / 2.0);
    }

    protected void addFragment() {
        fragmentArrayList.add(new MyTaskFragmentNotProcessed());
        fragmentArrayList.add(new MyTaskFragmentProcessed());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Animation animation = null;
        switch (position) {

            //当前为页卡1
            case 0:
                //从页卡1跳转转到页卡2
                if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, 0, 0, 0);
                    btnNotProcessed.setChecked(true);
                }
                break;

            //当前为页卡2
            case 1:
                //从页卡1跳转转到页卡2
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_one, 0, 0);
                    btnProcessed.setChecked(true);
                }
                break;


        }
        currIndex = position;
        if(animation!=null){
            animation.setFillAfter(true);// true:图片停在动画结束位置
            animation.setDuration(300);
//            cursor.startAnimation(animation);
        }


    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNotProcessed:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.btnProcessed:
                mViewPager.setCurrentItem(1);
                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.btnNotProcessed:
                if (btnNotProcessed.isChecked()) {
                    btnNotProcessed.setTextColor(getResources().getColor(R.color.selected_blue));
                    btnProcessed.setTextColor(getResources().getColor(R.color.white));
                } else {
                    btnNotProcessed.setTextColor(getResources().getColor(R.color.white));
                    btnProcessed.setTextColor(getResources().getColor(R.color.selected_blue));
                }
                break;
            case R.id.btnProcessed:
                if (btnProcessed.isChecked()) {
                    btnNotProcessed.setTextColor(getResources().getColor(R.color.white));
                    btnProcessed.setTextColor(getResources().getColor(R.color.selected_blue));
                } else {
                    btnProcessed.setTextColor(getResources().getColor(R.color.white));
                    btnNotProcessed.setTextColor(getResources().getColor(R.color.selected_blue));
                }
                break;
        }
    }

    private class MFragmentPagerAdapter extends FragmentStatePagerAdapter {

        //存放Fragment的数组
        private ArrayList<Fragment> fragmentsList;

        public MFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentsList) {
            super(fm);
            this.fragmentsList = fragmentsList;
        }

        @Override
        public Fragment getItem(int position) {

            return fragmentsList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }
}
