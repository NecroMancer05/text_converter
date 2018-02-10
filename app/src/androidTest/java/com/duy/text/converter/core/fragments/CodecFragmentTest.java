/*
 * Copyright (c) 2018 by Tran Le Duy
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

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.duy.text.converter.R;
import com.duy.text.converter.core.activities.MainActivity;
import com.duy.text.converter.core.codec.interfaces.Codec;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.ASCII;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.ATBASH;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.BASE_32;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.BASE_64;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.BINARY;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.CAESAR;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.HEX;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.LOWER;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.MORSE_CODE;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.NATO;
import static com.duy.text.converter.core.codec.interfaces.CodecMethod.OCTAL;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Duy on 2/10/2018.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CodecFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> mRule = new ActivityTestRule<MainActivity>(MainActivity.class);


    @Test
    public void AsciiCodec_encode() throws InterruptedException {
        testEncode(ASCII.getCodec(), "Hello ASCII");
    }

    @Test
    public void AsciiCodec_decode() throws InterruptedException {
        testDecode(ASCII.getCodec(), ASCII.getCodec().encode("Hello ascii"));
    }

    @Test
    public void AtbashCodec_encode() throws InterruptedException {
        testEncode(ATBASH.getCodec(), "Decode ATBASH");
    }

    @Test
    public void AtbashCodec_decode() throws InterruptedException {
        testDecode(ATBASH.getCodec(), ATBASH.getCodec().encode("Hello ATBASH"));
    }

    @Test
    public void Base32Codec_encode() throws InterruptedException {
        testEncode(BASE_32.getCodec(), "Decode ATBASH");
    }

    @Test
    public void Base32Codec_decode() throws InterruptedException {
        testDecode(BASE_32.getCodec(), BASE_32.getCodec().encode("Hello BASE_32"));
    }

    @Test
    public void Base64Codec_encode() throws InterruptedException {
        testEncode(BASE_64.getCodec(), "Encode BASE_64");
    }

    @Test
    public void Base64Codec_decode() throws InterruptedException {
        testDecode(BASE_64.getCodec(), BASE_64.getCodec().encode("Decode BASE_64"));
    }

    @Test
    public void BinaryCodec_encode() throws InterruptedException {
        testEncode(BINARY.getCodec(), "Encode BINARY");
    }

    @Test
    public void BinaryCodec_decode() throws InterruptedException {
        testDecode(BINARY.getCodec(), BINARY.getCodec().encode("Decode BINARY"));
    }

    @Test
    public void CaesarCodec_encode() throws InterruptedException {
        testEncode(CAESAR.getCodec(), "Encode CAESAR");
    }

    @Test
    public void CaesarCodec_decode() throws InterruptedException {
        testDecode(CAESAR.getCodec(), CAESAR.getCodec().encode("Decode CAESAR"));
    }

    @Test
    public void HexCodec_encode() throws InterruptedException {
        testEncode(HEX.getCodec(), "Encode HEX");
    }

    @Test
    public void HexCodec_decode() throws InterruptedException {
        testDecode(HEX.getCodec(), HEX.getCodec().encode("Decode CAESAR"));
    }

    @Test
    public void LowerCaseCodec_encode() throws InterruptedException {
        testEncode(LOWER.getCodec(), "Encode LOWER");
    }

    @Test
    public void LowerCaseCodec_decode() throws InterruptedException {
        testDecode(LOWER.getCodec(), LOWER.getCodec().encode("Decode LOWER"));
    }


    @Test
    public void MorseCodec_encode() throws InterruptedException {
        testEncode(MORSE_CODE.getCodec(), "Encode MORSE_CODE");
    }

    @Test
    public void MorseCodec_decode() throws InterruptedException {
        testDecode(MORSE_CODE.getCodec(), MORSE_CODE.getCodec().encode("Decode MORSE_CODE"));
    }

    @Test
    public void NatoCodec_encode() throws InterruptedException {
        testEncode(NATO.getCodec(), "Encode NATO");
    }

    @Test
    public void NatoCodec_decode() throws InterruptedException {
        testDecode(NATO.getCodec(), NATO.getCodec().encode("Decode NATO"));
    }

    @Test
    public void OctalCodec_encode() throws InterruptedException {
        testEncode(OCTAL.getCodec(), "Encode OCTAL");
    }

    @Test
    public void OctalCodec_decode() throws InterruptedException {
        testDecode(OCTAL.getCodec(), OCTAL.getCodec().encode("Decode OCTAL"));
    }

    private void testEncode(Codec codec, String stringToBeType) throws InterruptedException {
//        Thread.sleep(1000);

        String selectionText = codec.getName(mRule.getActivity());
        onView(allOf(withId(R.id.spinner_codec_methods), isDisplayed())).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(selectionText))).perform(click());
        onView(allOf(withId(R.id.spinner_codec_methods), isDisplayed())).check(matches(withSpinnerText(selectionText)));
//        Thread.sleep(200);

        onView(allOf(withId(R.id.edit_input), isDisplayed()))
                .perform(click())
                .perform(clearText())
                .perform(typeText(stringToBeType))
                .perform(closeSoftKeyboard());
//        Thread.sleep(200);

        onView(allOf(withId(R.id.edit_output), isDisplayed()))
                .check(matches(withText(codec.encode(stringToBeType))));
    }

    private void testDecode(Codec codec, String stringToBeType) throws InterruptedException {
//        Thread.sleep(1000);

        String selectionText = codec.getName(mRule.getActivity());
        onView(allOf(withId(R.id.spinner_codec_methods), isDisplayed())).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(selectionText))).perform(click());
        onView(withId(R.id.spinner_codec_methods)).check(matches(withSpinnerText(selectionText)));
//        Thread.sleep(200);

        onView(allOf(withId(R.id.edit_output), isDisplayed()))
                .perform(click())
                .perform(clearText()).perform(typeText(stringToBeType))
                .perform(closeSoftKeyboard());

//        Thread.sleep(200);

        onView(allOf(withId(R.id.edit_input), isDisplayed()))
                .check(matches(withText(codec.decode(stringToBeType))));
    }


}