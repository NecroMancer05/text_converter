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

package com.duy.text.converter.core.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.duy.text.converter.R;
import com.duy.text.converter.core.adapters.StyleAdapter;
import com.duy.text.converter.core.stylish.StylistGenerator;

import java.util.ArrayList;


/**
 * Created by DUy on 07-Feb-17.
 */

public class StylistFragment extends Fragment implements TextWatcher {
    public static final String KEY = "StylistFragment";
    private static final String KEY_INPUT = "KEY_INPUT";
    private static final String KEY_PROCESS_TEXT = "KEY_PROCESS_TEXT";
    private EditText mInput;
    private RecyclerView mListResult;
    private StyleAdapter mAdapter;

    public static StylistFragment newInstance() {
        StylistFragment fragment = new StylistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static StylistFragment newInstance(String input, boolean processText) {
        StylistFragment fragment = new StylistFragment();
        Bundle args = new Bundle();
        args.putString(KEY_INPUT, input);
        args.putBoolean(KEY_PROCESS_TEXT, processText);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stylish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String input = getArguments().getString(KEY_INPUT);
        boolean processText = getArguments().getBoolean(KEY_PROCESS_TEXT);

        mInput = view.findViewById(R.id.edit_input);
        if (processText) mInput.setVisibility(View.GONE);

        mListResult = view.findViewById(R.id.recycler_view);
        mListResult.setLayoutManager(new LinearLayoutManager(getContext()));
        mListResult.setHasFixedSize(true);

        mAdapter = new StyleAdapter(getActivity(),
                processText ? R.layout.list_item_encode_all : R.layout.list_item_style);
        mListResult.setAdapter(mAdapter);

        mInput.addTextChangedListener(this);
    }


    public void convert(String inp) {
        if (inp.isEmpty()) inp = "Type something";
        ArrayList<String> translate = new StylistGenerator().generate(inp);
        mAdapter.setData(translate);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        convert(mInput.getText().toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.edit().putString(KEY + 1, mInput.getText().toString()).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().getString(KEY_INPUT) == null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            mInput.setText(sharedPreferences.getString(KEY + 1, ""));
        }
    }
}
