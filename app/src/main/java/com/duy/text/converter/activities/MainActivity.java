/*
 * Copyright (C)  2017-2018 Tran Le Duy
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.duy.text.converter.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.duy.common.purchase.InAppPurchaseHelper;
import com.duy.common.utils.DLog;
import com.duy.common.utils.ShareUtil;
import com.duy.common.utils.StoreUtil;
import com.duy.text.converter.BuildConfig;
import com.duy.text.converter.R;
import com.duy.text.converter.PagerSectionAdapter;
import com.duy.text.converter.activities.base.InAppPurchaseActivityImpl;
import com.duy.text.converter.fragments.AdsFragment;
import com.duy.text.converter.help.HelpDialog;
import com.duy.text.converter.pro.PagerSectionAdapterPro;
import com.duy.text.converter.pro.SettingActivity;
import com.duy.text.converter.pro.floating.codec.FloatingCodecOpenShortCutActivity;
import com.duy.text.converter.pro.floating.stylish.FloatingStylishOpenShortCutActivity;
import com.duy.text.converter.pro.license.Key;
import com.duy.text.converter.pro.license.PolicyFactory;
import com.duy.text.converter.pro.license.Premium;
import com.google.android.gms.ads.MobileAds;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kobakei.ratethisapp.RateThisApp;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import flynn.tim.ciphersolver.frequency.FrequencyActivity;
import flynn.tim.ciphersolver.vigenere.VigenereCipherActivity;


public class MainActivity extends InAppPurchaseActivityImpl implements ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int REQ_CODE_SETTING = 1201;
    private static final String TAG = "MainActivity";
    protected Toolbar mToolbar;
    private LicenseChecker mChecker;
    private CheckLicenseCallBack mCallBack;
    private CoordinatorLayout mCoordinatorLayout;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Handler mHandler;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Premium.isPremium(this)) {
            MobileAds.initialize(this);
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main);
        setupToolbar();
        bindView();
        showDialogRate();
        checkLicense();
    }

    private void checkLicense() {
        mHandler = new Handler();
        Policy policy = PolicyFactory.createPolicy(this, getPackageName());
        mChecker = new LicenseChecker(this, policy, Key.BASE_64_PUBLIC_KEY);
        mCallBack = new CheckLicenseCallBack();
        mChecker.checkAccess(mCallBack);
    }

    private void showDialogRate() {
        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        if (!BuildConfig.DEBUG) {
            RateThisApp.showRateDialogIfNeeded(this);
        }
    }

    private void handleCracked() {
        FirebaseAnalytics.getInstance(this).logEvent("crack_version", new Bundle());
        Premium.setCracked(this, true);
        if (getPackageName().equals(Premium.PRO_PACKAGE)) {
            Toast.makeText(this, "Licence check failed", Toast.LENGTH_LONG).show();
        }
    }

    protected void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void bindView() {
        mCoordinatorLayout = findViewById(R.id.container);
        String text = getTextFromAnotherApp();

        ViewPager viewPager = findViewById(R.id.view_pager);
        PagerAdapter adapter = getPageAdapter(text);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        ((SmartTabLayout) findViewById(R.id.tab_layout)).setViewPager(viewPager);

        //attach listener hide/show keyboard
        KeyBoardEventListener keyBoardEventListener = new KeyBoardEventListener(this);
        mCoordinatorLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyBoardEventListener);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.desc_open_drawer, R.string.desc_close_drawer);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    protected PagerAdapter getPageAdapter(String initValue) {
        if (Premium.isPremium(this)) {
            return new PagerSectionAdapterPro(this, getSupportFragmentManager(), initValue);
        } else {
            return new PagerSectionAdapter(this, getSupportFragmentManager(), initValue);
        }
    }

    @Nullable
    private String getTextFromAnotherApp() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.equals("text/plain")) {
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                FirebaseAnalytics.getInstance(this).logEvent("open_from_another_app", new Bundle());
                return text;
            }
        }
        return null;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Premium.isPremium(this)) {
            menu.findItem(R.id.action_upgrade).setVisible(false);
        } else {
            menu.findItem(R.id.action_upgrade).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mFirebaseAnalytics.logEvent(item.getTitle().toString(), new Bundle());

        mDrawerLayout.closeDrawers();
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                ShareUtil.shareThisApp(this);
                break;
            case R.id.action_review:
                StoreUtil.gotoPlayStore(this, getPackageName());
                break;
            case R.id.action_upgrade:
                Premium.upgrade(this);
                break;
            case R.id.action_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, REQ_CODE_SETTING);
                break;
            case R.id.action_open_stylish:
                if (Premium.isPremium(this)) {
                    startActivity(new Intent(this, FloatingStylishOpenShortCutActivity.class));
                } else {
                    Premium.upgrade(this);
                }
                break;
            case R.id.action_open_codec:
                if (Premium.isPremium(this)) {
                    startActivity(new Intent(this, FloatingCodecOpenShortCutActivity.class));
                } else {
                    Premium.upgrade(this);
                }
                break;
            case R.id.action_how_to_use:
                HelpDialog helpDialog = new HelpDialog();
                helpDialog.show(getSupportFragmentManager(), HelpDialog.TAG);
                break;
            case R.id.action_hash:
                startActivity(new Intent(this, HashActivity.class));
                break;
            case R.id.action_base_converter:
                startActivity(new Intent(this, NumberConverterActivity.class));
                break;
            case R.id.action_codec_file:
                if (Premium.isPremium(this)) {
                    startActivity(new Intent(this, CodecFileActivity.class));
                } else {
                    Premium.upgrade(this);
                }
                break;
            case R.id.action_caesar_cipher:
                startActivity(new Intent(this, CaesarCipherActivity.class));
                break;
            case R.id.action_frequency_analysis:
                startActivity(new Intent(this, FrequencyActivity.class));
                break;
            case R.id.action_vigenere_cipher:
                startActivity(new Intent(this, VigenereCipherActivity.class));
                break;
            case R.id.action_ascii_art:
                StoreUtil.openApp(this, "com.duy.asciiart");
                break;
            case R.id.action_text_editor:
                StoreUtil.openApp(this, "com.duy.text.editor");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DLog.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SETTING:
                if (resultCode == RESULT_OK) {
                    recreate();
                }
                break;
            case InAppPurchaseHelper.RC_REQUEST_UPGRADE:
                if (resultCode == RESULT_OK) {
                    recreate();
                }
                break;
        }
    }

    /**
     * hide appbar layout when keyboard visible
     */
    private void hideAppBar() {
        mToolbar.setVisibility(View.GONE);
    }

    /**
     * show appbar layout when keyboard gone
     */
    private void showAppBar() {
        mToolbar.setVisibility(View.VISIBLE);
    }

    protected void onShowKeyboard() {
        hideAppBar();
    }

    protected void onHideKeyboard() {
        showAppBar();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (Premium.isFree(this)) {
            if (position == AdsFragment.INDEX) {
                hideKeyboard();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onOptionsItemSelected(item);
    }

    private static class KeyBoardEventListener implements ViewTreeObserver.OnGlobalLayoutListener {
        MainActivity activity;

        KeyBoardEventListener(MainActivity activityIde) {
            this.activity = activityIde;
        }

        public void onGlobalLayout() {
            int i = 0;
            int navHeight = this.activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            navHeight = navHeight > 0 ? this.activity.getResources().getDimensionPixelSize(navHeight) : 0;
            int statusBarHeight = this.activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (statusBarHeight > 0) {
                i = this.activity.getResources().getDimensionPixelSize(statusBarHeight);
            }
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            if (activity.mCoordinatorLayout.getRootView().getHeight() - ((navHeight + i) + rect.height()) <= 0) {
                activity.onHideKeyboard();
            } else {
                activity.onShowKeyboard();
            }
        }
    }

    private class CheckLicenseCallBack implements LicenseCheckerCallback {

        @Override
        public void allow(int reason) {
        }

        @Override
        public void dontAllow(int reason) {
            if (isFinishing()) {
                return;
            }
            if (reason == Policy.NOT_LICENSED) {
                handleCracked();
            }
        }

        @Override
        public void applicationError(int errorCode) {
        }
    }
}
