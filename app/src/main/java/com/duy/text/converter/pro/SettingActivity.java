/*
 * Copyright (c) 2017 by Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.text.converter.pro;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.duy.text.converter.R;
import com.duy.text.converter.core.activities.BaseActivity;
import com.duy.text.converter.pro.notification.SettingFragment;
import com.duy.text.converter.pro.notification.StyleNotificationManager;

/**
 * Created by Duy on 26-Jul-17.
 */

public class SettingActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, new SettingFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        super.onSharedPreferenceChanged(sharedPreferences, s);
        if (s.equalsIgnoreCase(getString(R.string.pref_key_enable_encode_notification))) {
            StyleNotificationManager.showNotificationEncodeIfNeed(this);
        } else if (s.equalsIgnoreCase(getString(R.string.pref_key_enable_decode_notification))) {
            StyleNotificationManager.showNotificationDecodeIfNeed(this);
        } else if (s.equalsIgnoreCase(getString(R.string.pref_key_theme))) {
//            overridePendingTransition(0, 0);
//            finish();
//            startActivity(new Intent(this, SettingActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

}
