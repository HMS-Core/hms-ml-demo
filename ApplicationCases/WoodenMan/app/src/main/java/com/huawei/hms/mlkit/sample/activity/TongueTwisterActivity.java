/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hms.mlkit.sample.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlkit.sample.cn.R;
import com.huawei.hms.mlkit.sample.util.ClickRepeat;
import com.huawei.hms.mlkit.sample.views.CircleProgressBar;
import com.huawei.hms.mlkit.sample.views.SpeechRecognizerManager;
import com.huawei.hms.mlkit.sample.views.TextConstant;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.common.MLApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TongueTwisterActivity extends BaseActivity implements View.OnClickListener, Runnable {

    private static final String TAG = "TongueTwisterActivity";

    private static final String LANGUAGE_EN = "en-US";

    private static final String LANGUAGE_ZH = "zh";

    private static final int AUDIO_CODE = 1;

    private View mHeadProgress;

    private RelativeLayout.LayoutParams mProgressView;

    private RelativeLayout mEntrance, mTongueTwister, mOver, mHome, mHeadProgressLayout;

    private LinearLayout mTongueTwisterView, mHeadTrophyView;

    private TextView mBegan, mLanguage, mRules, mHeadProportion, mCheckpoint, mTongueTwisterTitle,
            mTongueTime, mTongueStopwatchText, mTongueTwisterText, mTongueTwisterHtml, mHeadTrophy,
            mOverCount, mAgain, mOverText;

    private ImageView mTongueImage, mSpeakVoice;

    private CircleProgressBar mTongueProgress;

    private SpeechRecognizerManager mSpeechManager;

    private OnResultListener mListener;

    private List<int[]> symbolInitList;

    private List<int[][]> symbolErrorReplaceList;

    private List<int[][]> englishErrorReplaceList;

    private boolean languageSign;

    private boolean timerSign;

    boolean speakSign = false;

    boolean progressSign = true;

    private int lookTime = 5;

    private int stopwatchTime = 35;

    private int textErrorIndex = 0;

    private int indexCheck = 0;

    private int gateProgress = 0;

    private String language = LANGUAGE_ZH;

    private String text = "";

    private String errorText = "";

    private String textContrast = "";

    private String textCheck = "";

    private String[] commaSplit;

    private String[] endSplit;

    private String[][] symbol;

    private List<String> fontList;

    private List<String> wordList = new ArrayList<>();;

    private List<String> speakList = new ArrayList<>();

    private Thread mThread;

    /**
     * The Handler of the main thread receives the message from the child thread and modifies the display of TextView
     */
    @SuppressLint("NewApi")
    private Handler mHandler =  new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == AUDIO_CODE && timerSign) {
                int arg1 = msg.arg1;
                if (arg1 - 30 > 0) {
                    mTongueTime.setText(String.valueOf(arg1 - 30));
                    mTongueStopwatchText.setText(arg1 - 30
                            + getResources().getString(R.string.tongue_began_time));
                } else {
                    mTongueTime.setText(String.valueOf(arg1));
                }
                if (arg1 == 0) {
                    mTongueProgress.setProgress(0);
                }
                if (arg1 == 30) {
                    mTongueStopwatchText
                            .setText(getResources().getString(R.string.tongue_began_hint));
                    mTongueTwisterText
                            .setTextColor(getResources().getColor(R.color.tongue_twister_hint));
                }
                if (progressSign) {
                    mProgressView.width = mHeadProgressLayout.getWidth() / 5;
                    mProgressView.height = mHeadProgressLayout.getHeight();
                    mHeadProgress.setLayoutParams(mProgressView);
                    progressSign = false;
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tongue_twister);
        // Set ApiKey.
         MLApplication.getInstance().setApiKey("your ApiKey");
        // Set access token.
        // MLApplication.getInstance().setAccessToken("your access token");
        initView();
        initData();
        initEvent();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        findViewById(R.id.back).setOnClickListener(new ClickRepeat(this));
        // entrance
        mEntrance = findViewById(R.id.tongue_entrance);
        mBegan = findViewById(R.id.tongue_began);
        mLanguage = findViewById(R.id.tongue_language);
        mRules = findViewById(R.id.tongue_rules);
        mRules.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        // Tongue twister
        mTongueTwister = findViewById(R.id.tongue_twister);
        // Gate progress
        mHeadProportion = findViewById(R.id.tongue_progress_text);
        mHeadProgressLayout = findViewById(R.id.tongue_head_progress_view);
        mHeadProgress = findViewById(R.id.tongue_head_progress);
        mProgressView = (RelativeLayout.LayoutParams) mHeadProgress.getLayoutParams();
        mHeadProportion.setText(gateProgress + 1 + "/5");
        mProgressView.width = mHeadProgressLayout.getWidth() / 5;
        mProgressView.height = mHeadProgressLayout.getHeight();
        mHeadProgress.setLayoutParams(mProgressView);
        mCheckpoint = findViewById(R.id.tongue_checkpoint);
        mHeadTrophyView = findViewById(R.id.tongue_trophy_view);
        mHeadTrophy = findViewById(R.id.tongue_trophy_text);
        // Breakthrough game
        mTongueTwisterView = findViewById(R.id.tongue_twister_view);
        mTongueTwisterTitle = findViewById(R.id.tongue_twister_title);
        mTongueProgress = findViewById(R.id.tongue_stopwatch_progress);
        // stopwatch
        mTongueImage = findViewById(R.id.tongue_stopwatch_image);
        mTongueTime = findViewById(R.id.tongue_stopwatch_time);
        mTongueStopwatchText = findViewById(R.id.tongue_stopwatch_text);
        // text
        mTongueTwisterText = findViewById(R.id.tongue_twister_text);
        mTongueTwisterHtml = findViewById(R.id.tongue_twister_html);
        // over
        mOver = findViewById(R.id.tongue_over);
        mOverCount = findViewById(R.id.over_count);
        mHome = findViewById(R.id.over_home);
        mAgain = findViewById(R.id.over_again);
        mOverText = findViewById(R.id.over_text);
        mSpeakVoice = findViewById(R.id.speak_voice);
    }

    private void initData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestCameraPermission();
        }
        // Begin to identify
        symbolInitList = new ArrayList<int[]>();
        symbolInitList.add(TextConstant.chineseInit1);
        symbolInitList.add(TextConstant.chineseInit2);
        symbolInitList.add(TextConstant.chineseInit3);
        symbolInitList.add(TextConstant.chineseInit4);
        symbolInitList.add(TextConstant.chineseInit5);
        // redundant
        symbolErrorReplaceList = new ArrayList<int[][]>();
        symbolErrorReplaceList.add(TextConstant.chineseErrorReplace1);
        symbolErrorReplaceList.add(TextConstant.chineseErrorReplace2);
        symbolErrorReplaceList.add(TextConstant.chineseErrorReplace3);
        symbolErrorReplaceList.add(TextConstant.chineseErrorReplace4);
        symbolErrorReplaceList.add(TextConstant.chineseErrorReplace5);
        englishErrorReplaceList = new ArrayList<int[][]>();
        englishErrorReplaceList.add(TextConstant.englishErrorReplace1);
        englishErrorReplaceList.add(TextConstant.englishErrorReplace2);
        englishErrorReplaceList.add(TextConstant.englishErrorReplace3);
        englishErrorReplaceList.add(TextConstant.englishErrorReplace4);
        englishErrorReplaceList.add(TextConstant.englishErrorReplace5);
        errorTextCombination();
        languageSetting();
        // Initialization of child threads
        mThread = new Thread(this);
        mListener = new OnResultListener();
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    TongueTwisterActivity.AUDIO_CODE);
            return;
        }
    }

    private void initEvent() {
        mBegan.setOnClickListener(new ClickRepeat(this));
        mLanguage.setOnClickListener(new ClickRepeat(this));
        mRules.setOnClickListener(new ClickRepeat(this));
        mHome.setOnClickListener(new ClickRepeat(this));
        mAgain.setOnClickListener(new ClickRepeat(this));
    }

    private void errorTextCombination() {
        errorText = "";
        for (int i = 0; i < symbolErrorReplaceList.get(gateProgress).length; i++) {
            errorText += getResources().getString(symbolErrorReplaceList.get(gateProgress)[i][0]);
        }
    }

    private void languageSetting() {
        if (languageSign) {
            mLanguage.setText(getResources().getText(R.string.tongue_chinese));
            language = LANGUAGE_EN;
            languageSign = false;
        } else {
            mLanguage.setText(getResources().getText(R.string.tongue_english));
            language = LANGUAGE_ZH;
            languageSign = true;
        }
    }

    private void startRecodingOnCustom() {
        if (mSpeechManager == null) {
            startListening();
        } else{
            mSpeechManager.destroy();
            startListening();
        }
    }

    private void startListening() {
        mSpeechManager = new SpeechRecognizerManager(TongueTwisterActivity.this,
                language,
                mListener);
        mSpeechManager.startListening();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
            case R.id.over_home:
                finish();
                break;
            case R.id.tongue_began:
                mEntrance.setVisibility(View.GONE);
                mTongueTwister.setVisibility(View.VISIBLE);
                readyGo();
                break;
            case R.id.tongue_language:
                languageSetting();
                break;
            case R.id.tongue_rules:
                startActivity(new Intent(TongueTwisterActivity.this,
                        RecruitRulesActivity.class));
                break;
            case R.id.over_again:
                mOver.setVisibility(View.GONE);
                mEntrance.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * Determine whether the child thread is active,
     * If it is not active, it is to start the timer or restart the timer;
     * If it's active, it's a pause timer.
     */
    private void timerControl() {
        if(!mThread.isAlive()){
            // Start timer or restart timer, set flag to true
            timerSign = true;
            // Determine whether it is the first time to start.
            // If it is the first time to start, then the status is Thread.State.TERMINATED
            // If not, you need to reinitialize because the previous one is over.
            // And to determine whether the time value of this timer is - 1,
            // if it is, the last time the timer has been completed, then reset it.
            if(mThread.getState()==Thread.State.TERMINATED){
                mThread = new Thread(this);
                if(stopwatchTime != 35) stopwatchTime = 35;
                mThread.start();
            } else {
                mThread.start();
            }
        } else {
            // Pause timer, set flag to false
            // Do not use the stop method.
            // An error will be reported, java.lang.UnsupportedOperationException
            timerSign = false;
            mTongueProgress.setProgress(0);
            mTongueTime.setVisibility(View.GONE);
            mTongueTime.setText(String.valueOf(lookTime));
            mTongueImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Paragraph division
     */
    private void symbolSort() {
        mTongueTwisterHtml.setMaxEms(mTongueTwisterText.getText().length());
        commaSplit = mTongueTwisterText
                .getText()
                .toString()
                .split(getResources().getString(R.string.comma));
        endSplit = mTongueTwisterText
                .getText()
                .toString()
                .split(getResources().getString(R.string.end));
        symbol = new String[commaSplit.length + endSplit.length - 1][3];
        fontList = new ArrayList<>();
        List<String> splitList = new ArrayList<>();
        StringBuilder fontText = new StringBuilder();
        int commaLength = 0;
        int endLength = 0;
        int splitLength = 0;
        String[] splitCopy;
        // comma
        for (int i = 0; i < commaSplit.length; i++) {
            if (i == 0) {
                commaLength = commaSplit[i].length();
                symbol[i][0] = commaLength + "";
                symbol[i][1] = getResources().getString(R.string.comma);
            } else if (i < commaSplit.length - 1) {
                commaLength += 1 + commaSplit[i].length();
                symbol[i][0] = commaLength + "";
                symbol[i][1] = getResources().getString(R.string.comma);
            }
            // comma in end
            splitCopy = commaSplit[i].split(getResources().getString(R.string.end));
            if (commaSplit[i].contains(getResources().getString(R.string.end))) {
                for (String s : splitCopy) {
                    splitLength += s.length();
                    splitList.add(splitLength + "");
                    fontText.append(s);
                }
            } else {
                splitLength += commaSplit[i].length();
                splitList.add(splitLength + "");
                fontText.append(commaSplit[i]);
            }
        }
        // end
        for (int i = 0; i < endSplit.length; i++) {
            if (i == 0) {
                endLength = endSplit[i].length();
            } else {
                endLength += 1 + endSplit[i].length();
            }
            symbol[commaSplit.length - 1 + i][0] = endLength + "";
            symbol[commaSplit.length - 1 + i][1] = getResources().getString(R.string.end);
        }
        // Bubble sort
        for (int i = 0; i < symbol.length - 1; i++) {
            for (int j = 0; j < symbol.length - 1 - i; j++) {
                if (Integer.parseInt(symbol[j][0]) > Integer.parseInt(symbol[j + 1][0])) {
                    String tempInt = symbol[j][0];
                    String tempString = symbol[j][1];
                    symbol[j][0] = symbol[j + 1][0];
                    symbol[j][1] = symbol[j + 1][1];
                    symbol[j + 1][0] = tempInt;
                    symbol[j + 1][1] = tempString;
                }
            }
        }
        // Paragraph length
        for (int i = 0; i < splitList.size(); i++) {
            symbol[i][2] = splitList.get(i);
        }
        // Correct copy
        for (int i = 0; i < fontText.length(); i++) {
            fontList.add(String.valueOf(fontText.charAt(i)));
        }
    }

    private void englishSort() {
        wordList.clear();
        wordList.addAll(Arrays.asList(getResources()
                .getString(TextConstant.englishText[gateProgress])
                .split(" ")));
    }

    /**
     * Stop the thread in the onDestroy() method.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerSign = false;
    }

    @Override
    public void run() {
        // The child thread must set this flag timerSign and countdown.
        while (timerSign  && stopwatchTime >= 0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // InterruptedException
                Log.e(TAG, "Error: The countdown time of InterruptedException");
            } catch (Exception e) {
                // Exception
                Log.e(TAG, "Error: The countdown time of " + e.getMessage());
            }
            // Send a Message to the Handler of the main thread every second,
            // and let the Handler of the main thread modify the UI.
            // Note that the Message here can be obtained through obtain(), so as to save memory.
            // It will automatically check whether there is something that can be reused and will not be created repeatedly.
            Message mMessage = Message.obtain();
            mMessage.what = AUDIO_CODE;
            mMessage.arg1 = stopwatchTime;
            mHandler.sendMessage(mMessage);
            Log.d(TAG, "The countdown time of " + stopwatchTime--);
        }
    }

    private class OnResultListener implements SpeechRecognizerManager.onResultsReady {
        @SuppressLint("SetTextI18n")
        @Override
        public void onResults(ArrayList<String> results) {
            if (results != null && results.size() > 0) {
                if (results.size() == 1) {
                    text = results.get(0);
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (results.size() > 5) {
                        results = (ArrayList<String>) results.subList(0, 5);
                    }
                    for (String result : results) {
                        stringBuilder.append(result).append("\n");
                    }
                    text = stringBuilder.toString();
                }

                if (languageSign) {
                    chineseIdentifying(text);
                } else {
                    englishIdentifying(text);
                }
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onFinish() {
            // finish
            timerControl();
            if (languageSign) {
                chineseResult();
            } else {
                englishResult();
            }
        }

        @Override
        public void onError(int error) {
            Log.e(TAG,"error = " + error);
            timerControl();
            showFailedDialog(getPrompt(error));
        }

    }

    @SuppressLint("SetTextI18n")
    private void chineseIdentifying(String text) {
        // redundancy
        for (int i = 0; i < symbolErrorReplaceList.get(gateProgress).length; i++) {
            if (text.contains(getResources()
                    .getString(symbolErrorReplaceList.get(gateProgress)[i][0]))) {
                text = text.replace(
                        getResources()
                                .getString(symbolErrorReplaceList.get(gateProgress)[i][0]),
                        getResources()
                                .getString(symbolErrorReplaceList.get(gateProgress)[i][1]));
            }
        }

        // Output copy
        speakList.clear();
        for (int i = 0; i < text.length(); i++) {
            speakList.add(String.valueOf(text.charAt(i)));
        }

        // error correction
        int size = speakList.size() >= fontList.size() ? fontList.size() : speakList.size();
        for (int i = 0; i < size; i++) {
            if (!speakList.get(i).equals(fontList.get(i)) && !speakSign
                    && text.length() <= fontList.size()) {
                if (i < 3) {
                    speakList.set(i, fontList.get(i));
                } else {
                    textErrorIndex = i;
                    if (!textCheck.equals("")
                            && indexCheck != 0
                            && indexCheck == textErrorIndex
                            && !errorText.contains(textCheck)
                            && speakList.get(textErrorIndex).equals(textCheck)) {
                        speakSign = true;
                    }
                    textCheck = speakList.get(textErrorIndex);
                    indexCheck = i;
                }
            }
        }

        // Wrong field
        StringBuilder speakErrorText = new StringBuilder();
        if (textErrorIndex >= 2 && speakSign) {
            speakList.set(textErrorIndex, fontList.get(textErrorIndex));
            for (int i = 0; i < textErrorIndex + 1; i++) {
                text = speakErrorText.append(speakList.get(i)).toString();
            }
        }

        StringBuilder resultBuilder = new StringBuilder(text);
        if (text.length()
                >= mTongueTwisterText.length() - commaSplit.length - endSplit.length + 1) {
            for (String[] strings : symbol) {
                resultBuilder.replace(0,
                        3,
                        getResources().getString(
                                TextConstant.chineseReplace[gateProgress]));
                resultBuilder.insert(Integer.parseInt(strings[0]), strings[1]);
                textContrast = resultBuilder.toString();
                mTongueTwisterHtml.setText(textContrast);
            }
        } else {
            for (String[] strings : symbol) {
                if (text.length() >= Integer.parseInt(strings[2])) {
                    resultBuilder.replace(0,
                            3,
                            getResources().getString(
                                    TextConstant.chineseReplace[gateProgress]));
                    resultBuilder.insert(Integer.parseInt(strings[0]), strings[1]);
                    textContrast = resultBuilder.toString();
                    mTongueTwisterHtml.setText(textContrast
                            + getResources().getString(R.string.line));
                } else {
                    if (text.length() < 4) {
                        for (int i = 0; i < text.length(); i++) {
                            textContrast = getResources().getString(
                                    symbolInitList.get(gateProgress)[i]);
                        }
                    } else {
                        resultBuilder.replace(0,
                                3,
                                getResources().getString(
                                        TextConstant.chineseReplace[gateProgress]));
                        textContrast = resultBuilder.toString();
                    }
                    mTongueTwisterHtml.setText(textContrast
                            + getResources().getString(R.string.line));
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void englishIdentifying(String text) {
        // redundancy
        for (int i = 0; i < englishErrorReplaceList.get(gateProgress).length; i++) {
            if (text.contains(getResources()
                    .getString(englishErrorReplaceList.get(gateProgress)[i][0]))) {
                text = text.replace(
                        getResources()
                                .getString(englishErrorReplaceList.get(gateProgress)[i][0]),
                        getResources()
                                .getString(englishErrorReplaceList.get(gateProgress)[i][1]));
            }
        }

        String[] identifyingSplit = text.split(" ");
        if (gateProgress == 0) {
            if (identifyingSplit.length == 0) {
                if (identifyingSplit[0].equals(getResources().getString(R.string.english_init_one_1))) {
                    mTongueTwisterHtml.setText(text + "_");
                    mTongueTwisterHtml.setTextColor(getResources().getColor(R.color.tongue_twister_text));
                } else if (identifyingSplit[0].equals(getResources().getString(R.string.english_replace_1_1))) {
                    mTongueTwisterHtml.setText(getResources().getString(R.string.english_init_one_1) + " " + text + "_");
                    mTongueTwisterHtml.setTextColor(getResources().getColor(R.color.tongue_twister_text));
                } else {
                    mTongueTwisterHtml.setText(text);
                    mTongueTwisterHtml.setTextColor(Color.RED);
                }
            } else {
                if (identifyingSplit[0].equals(getResources().getString(R.string.english_replace_1_1))) {
                    mTongueTwisterHtml.setText(getResources().getString(R.string.english_init_one_1) + " " + text + "_");
                    mTongueTwisterHtml.setTextColor(getResources().getColor(R.color.tongue_twister_text));
                } else {
                    mTongueTwisterHtml.setText(text + "_");
                    mTongueTwisterHtml.setTextColor(getResources().getColor(R.color.tongue_twister_text));
                }
            }
        } else {
            mTongueTwisterHtml.setText(text + "_");
            mTongueTwisterHtml.setTextColor(getResources().getColor(R.color.tongue_twister_text));
        }
    }

    @SuppressLint("SetTextI18n")
    private void chineseResult() {
        String textResult = mTongueTwisterHtml.getText().toString();
        SpannableStringBuilder fontErrorText = new SpannableStringBuilder(textResult);
        if (text.length()
                >= (mTongueTwisterText.length() - commaSplit.length - endSplit.length + 1)) {
            if (speakSign) {
                if (getResources().getString(R.string.comma)
                        .equals(String.valueOf(textResult.charAt(textResult.length() - 1)))
                        || getResources().getString(R.string.end)
                        .equals(String.valueOf(textResult.charAt(textResult.length() - 1)))) {
                    fontErrorText.setSpan(new ForegroundColorSpan(Color.RED),
                            textResult.length() - 2,
                            textResult.length() - 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    fontErrorText.setSpan(new ForegroundColorSpan(Color.RED),
                            textResult.length() - 1,
                            textResult.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mTongueTwisterHtml.setText(fontErrorText);
                gameOver();
            } else {
                // adopt
                nextLevel();
            }
        } else {
            if (speakSign) {
                if (getResources().getString(R.string.comma)
                        .equals(String.valueOf(textResult.charAt(textResult.length() - 3)))
                        || getResources().getString(R.string.end)
                        .equals(String.valueOf(textResult.charAt(textResult.length() - 3)))) {
                    fontErrorText.setSpan(new ForegroundColorSpan(Color.RED),
                            textResult.length() - 4,
                            textResult.length() - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    fontErrorText.setSpan(new ForegroundColorSpan(Color.RED),
                            textResult.length() - 3,
                            textResult.length() - 2,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mTongueTwisterHtml.setText(fontErrorText);
            } else {
                // Failed
                Log.d(TAG, "The pause time is too long");
            }
            gameOver();
        }
    }

    @SuppressLint("SetTextI18n")
    private void englishResult() {
        String textResult = mTongueTwisterHtml.getText().toString();
        String[] wordSplit = textResult.split(" ");
        // Output copy
        speakList.clear();
        speakList.addAll(Arrays.asList(wordSplit).subList(0, wordSplit.length - 1));
        if (textResult.contains(mTongueTwisterText.getText().toString())) {
            mTongueTwisterHtml.setText(mTongueTwisterText.getText().toString());
            // adopt
            nextLevel();
        } else {
            // error correction
            int size = speakList.size() >= wordList.size() ? wordList.size() : speakList.size();
            for (int i = 0; i < size; i++) {
                if (!speakList.get(i).equals(wordList.get(i)) && !speakSign) {
                    textErrorIndex = i;
                    speakSign = true;
                }
            }

            // Wrong field
            StringBuilder speakErrorText = new StringBuilder();
            if (textErrorIndex > 0 && speakSign) {
                speakList.set(textErrorIndex, wordList.get(textErrorIndex));
                for (int i = 0; i < textErrorIndex + 1; i++) {
                    textContrast = speakErrorText.append(speakList.get(i)).append(" ").toString();
                }
                textCheck = textContrast + "_";
                SpannableStringBuilder fontErrorText = new SpannableStringBuilder(textCheck);
                fontErrorText.setSpan(new ForegroundColorSpan(Color.RED),
                        textCheck.length() - wordList.get(textErrorIndex).length() - 2,
                        textCheck.length() - 2,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTongueTwisterHtml.setText(fontErrorText);
            } else {
                mTongueTwisterHtml.setText(textResult);
                // Failed
                Log.d(TAG, "The pause time is too long");
            }
            gameOver();
        }
    }

    @SuppressLint("SetTextI18n")
    private void readyGo() {
        mHeadProportion.setText(gateProgress + 1 + "/5");
        mProgressView.width = mHeadProgressLayout.getWidth() / 5 * (gateProgress + 1);
        mProgressView.height = mHeadProgressLayout.getHeight();
        mHeadProgress.setLayoutParams(mProgressView);
        if (languageSign) {
            mTongueTwisterTitle
                    .setText(getResources().getString(TextConstant.chineseTitle[gateProgress]));
            mTongueTwisterText
                    .setText(getResources().getString(TextConstant.chineseText[gateProgress]));
        } else {
            mTongueTwisterTitle
                    .setText(getResources().getString(TextConstant.englishTitle[gateProgress]));
            mTongueTwisterText
                    .setText(getResources().getString(TextConstant.englishText[gateProgress]));
        }
        mTongueTwisterHtml.setText("");
        mCheckpoint.setText(TextConstant.levelTitle[gateProgress]);
        if (mTongueTwister.getVisibility() == View.VISIBLE
                && mTongueImage.getVisibility() == View.VISIBLE) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTongueImage.setVisibility(View.GONE);
                    mCheckpoint.setVisibility(View.GONE);
                    mTongueTwisterView.setVisibility(View.GONE);
                    mTongueTime.setVisibility(View.VISIBLE);
                    mTongueStopwatchText.setText(lookTime
                            + getResources().getString(R.string.tongue_began_time));
                    mTongueStopwatchText.setVisibility(View.VISIBLE);
                    mTongueTwisterText.setVisibility(View.VISIBLE);
                    mTongueTwisterHtml.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerControl();
                        }
                    });
                }
            }, 1000);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSpeakVoice.setVisibility(View.VISIBLE);
                    mTongueProgress.setProgress(30);
                    startRecodingOnCustom();
                }
            }, 6000);
            if (languageSign) {
                symbolSort();
            } else {
                englishSort();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void nextLevel() {
        mHeadTrophyView.setVisibility(View.VISIBLE);
        mHeadTrophy.setText(getResources().getString(R.string.tongue_great)
                + getResources().getString(TextConstant.levelTitle[gateProgress])
                + getResources().getString(R.string.tongue_trophy));
        gateProgress++;
        if (gateProgress == 5) {
            gameOver();
        } else {
            errorTextCombination();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHeadTrophyView.setVisibility(View.GONE);
                    mTongueTime.setVisibility(View.GONE);
                    mTongueStopwatchText.setVisibility(View.GONE);
                    mTongueTwisterText.setVisibility(View.GONE);
                    mTongueTwisterHtml.setVisibility(View.GONE);
                    mTongueImage.setVisibility(View.VISIBLE);
                    mCheckpoint.setVisibility(View.VISIBLE);
                    mTongueTwisterView.setVisibility(View.VISIBLE);
                    readyGo();
                }
            }, 1000);
        }
    }

    private void gameOver() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHeadTrophyView.setVisibility(View.GONE);
                mTongueTwister.setVisibility(View.GONE);
                mTongueTime.setVisibility(View.GONE);
                mSpeakVoice.setVisibility(View.GONE);
                mTongueImage.setVisibility(View.VISIBLE);
                mOver.setVisibility(View.VISIBLE);
                mOverCount.setText(String.valueOf(gateProgress));
                if (gateProgress == 5) {
                    mOverText.setText(R.string.over_success);
                } else {
                    mOverText.setText(R.string.over_tongue);
                }
                gameAgain();
            }
        }, 1000);
    }

    @SuppressLint("SetTextI18n")
    private void gameAgain() {
        speakSign = false;
        textErrorIndex = 0;
        indexCheck = 0;
        gateProgress = 0;
        text = "";
        textContrast = "";
        textCheck = "";
        fontList.clear();
        speakList.clear();
        mTongueTime.setText(String.valueOf(lookTime));
        mTongueStopwatchText.setText(lookTime
                + getResources().getString(R.string.tongue_began_time));
        errorTextCombination();
    }

    @SuppressLint("NewApi")
    private void showFailedDialog(int res) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(res)
                .setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                setTextColor(getResources().getColor(R.color.button_background));
    }

    private int getPrompt(int errorCode) {
        switch (errorCode) {
            case MLAsrConstants.ERR_NO_NETWORK:
                return R.string.error_no_network;
            case MLAsrConstants.ERR_NO_UNDERSTAND:
                return R.string.error_no_understand;
            case MLAsrConstants.ERR_SERVICE_UNAVAILABLE:
                return R.string.error_service_unavailable;
            default:
                return errorCode;
        }
    }

}
