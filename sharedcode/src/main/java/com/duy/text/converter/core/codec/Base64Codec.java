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

package com.duy.text.converter.core.codec;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.nio.charset.Charset;

/**
 * Created by Duy on 22-Jun-17.
 */

public class Base64Codec implements Codec {
    @NonNull
    @Override
    public String encode(@NonNull String token) {
        try {
            byte[] encodedBytes = Base64.encode(token.getBytes(), Base64.DEFAULT);
            return new String(encodedBytes, Charset.forName("UTF-8"));
        } catch (Exception e) {
            return token;
        }
    }

    @NonNull
    @Override
    public String decode(@NonNull String token) {
        try {
            byte[] decodedBytes = Base64.decode(token.getBytes(), Base64.DEFAULT);
            return new String(decodedBytes, Charset.forName("UTF-8"));
        } catch (Exception e) {
            return token;
        }
    }

    @Override
    public String getName(Context context) {
        return null;
    }

    @Override
    public int getMax() {
        return 0;
    }

    @Override
    public int getConfident() {
        return 0;
    }
}
