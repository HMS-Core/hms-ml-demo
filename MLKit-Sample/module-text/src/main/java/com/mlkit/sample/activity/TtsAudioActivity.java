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

package com.mlkit.sample.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;
import com.mlkit.sample.R;
import com.mlkit.sample.util.FileUtils;
import com.mlkit.sample.util.PCMToWav;

public class TtsAudioActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "TtsAudioActivity";

    private static final String NO_NETWORK = "0104";
    private static final String SPEAK_ABNORMAL = "7002";

    private static final int MESSAGE_TYPE_INFO = 1;
    private static final int MESSAGE_TYPE_RANGE = 2;

    public static String AUDIO_PATH;
    private static String AUDIO_FILE_NAME_PCM;
    private static String AUDIO_FILE_NAME_WAV;
    private MediaPlayer mediaPlayer;

    private EditText editText;

    private Button addBtn;
    private Button pauseBtn;
    private Button playBtn;
    private Button stopBtn;

    private SeekBar speedSeek;
    private SeekBar volumeSeek;

    private TextView textView_volume;
    private TextView textView_speed;
    private TextView languageText;
    private TextView styleText;
    private TextView modeText;

    private ImageView clear;

    private RelativeLayout rl_language;
    private RelativeLayout rl_style;
    private RelativeLayout rl_mode;

    private MLTtsEngine mlTtsEngine;

    private TextView back;
    private String[] languageSpeak = new String[]{MLTtsConstants.TTS_ZH_HANS,
            MLTtsConstants.TTS_EN_US, MLTtsConstants.TTS_LAN_FR_FR, MLTtsConstants.TTS_LAN_ES_ES};
    private int[] languageResources = new int[]{R.string.chinese, R.string.english_choose, R.string.french, R.string.spanish};
    private String[] languageStyle = new String[]{MLTtsConstants.TTS_SPEAKER_FEMALE_ZH,
            MLTtsConstants.TTS_SPEAKER_FEMALE_EN,
            MLTtsConstants.TTS_SPEAKER_MALE_ZH,
            MLTtsConstants.TTS_SPEAKER_MALE_EN,
            MLTtsConstants.TTS_SPEAKER_FEMALE_FR,
            MLTtsConstants.TTS_SPEAKER_FEMALE_ES};
    private int[] languageStyleResources = new int[]{R.string.female_zh, R.string.female_en,
            R.string.male_zh, R.string.male_en, R.string.female_fr, R.string.female_es};

    private int[] playModeResources = new int[]{R.string.queuing_mode, R.string.clear_mode};
    private boolean isFlush = false;
    private boolean isPause = false;

    private Map<String, String> temp = new HashMap<>();
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case MESSAGE_TYPE_INFO:
                    String extension = (String) message.obj;
                    if (extension == null) {
                        addBtn.setText(R.string.queue_add);
                        break;
                    }
                    if (NO_NETWORK.equals(extension)) {
                        showFailedDialog(R.string.nonetwork);
                    } else if (SPEAK_ABNORMAL.equals(extension)) {
                        showFailedDialog(R.string.speak_abnormal);
                    } else {
                        showFailedDialog(R.string.abnormal);
                    }
                    addBtn.setText(R.string.replay);
                    break;
                case MESSAGE_TYPE_RANGE:
                    if (editText.getText().toString().isEmpty()) {
                        break;
                    }
                    Bundle bundle = message.getData();
                    String taskId = bundle.getString("taskId");
                    int start = bundle.getInt("start");
                    int end = bundle.getInt("end");
                    String text1 = temp.get(taskId);
                    SpannableStringBuilder style = new SpannableStringBuilder(text1);
                    // Set the background color of the specified position of textView
                    style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.button_background)),
                            start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    editText.setText(style);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    MLTtsCallback callback = new MLTtsCallback() {
        @Override
        public void onError(String taskId, MLTtsError err) {
            sendMsg(MESSAGE_TYPE_INFO, (String) err.getExtension());
        }

        @Override
        public void onWarn(String taskId, MLTtsWarn warn) {
        }

        @Override
        public void onRangeStart(String taskId, int start, int end) {
            sendRangeMsg(taskId, start, end);
        }

        @Override
        public void onAudioAvailable(String s, MLTtsAudioFragment mlTtsAudioFragment, int i, Pair<Integer, Integer> pair, Bundle bundle) {
            FileUtils.writeBufferToFile(mlTtsAudioFragment.getAudioData(), AUDIO_FILE_NAME_PCM, true);
        }

        @Override
        public void onEvent(String taskId, int eventID, Bundle bundle) {
            // The synthesis is complete.
            if (eventID == MLTtsConstants.EVENT_SYNTHESIS_COMPLETE) {
                AUDIO_FILE_NAME_WAV = PCMToWav.convertWaveFile(AUDIO_FILE_NAME_PCM, AUDIO_FILE_NAME_WAV, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                restartPlayer(AUDIO_FILE_NAME_WAV);
            }
        }
    };

    private void restartPlayer(String path) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendRangeMsg(String str, int start, int end) {
        Message msg = new Message();
        msg.what = MESSAGE_TYPE_RANGE;
        Bundle bundle = new Bundle();
        bundle.putString("taskId", str);
        bundle.putInt("start", start);
        bundle.putInt("end", end);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void sendMsg(int id, String str) {
        Message msg = new Message();
        msg.what = id;
        msg.obj = str;
        handler.sendMessage(msg);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && v != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // Necessary, otherwise all components will not have touch event
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    /**
     * Hide keyboard or not
     *
     * @param v view
     * @param event event
     * @return true of false
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // Get the current location of the input view
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            // Click the input box area to keep the event of clicking EditText
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_audio);
        initView();
        initAction();
        // Configuration item. If it is not set, the default configuration will be used.
        MLTtsConfig mlConfigs = new MLTtsConfig();
        mlTtsEngine = new MLTtsEngine(mlConfigs);
        // Set playback callback
        mlTtsEngine.setTtsCallback(callback);
        // Create audio file.
        AUDIO_PATH = FileUtils.initFile(this);
        AUDIO_FILE_NAME_WAV = AUDIO_PATH + "/tts.wav";
        AUDIO_FILE_NAME_PCM = AUDIO_PATH + "/tts.pcm";
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource("");
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initView() {
        editText = findViewById(R.id.edit_text);
        addBtn = findViewById(R.id.btn_add);

        playBtn = findViewById(R.id.btn_play);
        pauseBtn = findViewById(R.id.btn_pause);
        stopBtn = findViewById(R.id.btn_stop);
        volumeSeek = findViewById(R.id.volumeSeek);
        speedSeek = findViewById(R.id.speedSeek);

        clear = findViewById(R.id.close);

        textView_volume = findViewById(R.id.textView_volume);
        textView_speed = findViewById(R.id.textView_speed);
        back = findViewById(R.id.back);

        languageText = findViewById(R.id.languagetext);
        styleText = findViewById(R.id.styletext);
        modeText = findViewById(R.id.modetext);
        rl_language = findViewById(R.id.rl_language);
        rl_style = findViewById(R.id.rl_style);
        rl_mode = findViewById(R.id.rl_mode);

        createLanguageDialog();
        createStyleDialog();
        createModeDialog();

        textView_volume.setText(R.string.init_progress);
        textView_speed.setText(R.string.init_progress);

        languageText.setText(languageResources[0]);
        styleText.setText(languageStyleResources[0]);
        modeText.setText(playModeResources[0]);
    }

    private Dialog styleDialog;
    private TextView FemaleZh;
    private TextView FemaleEn;
    private TextView MaleZh;
    private TextView MaleEh;
    private TextView MaleFr;
    private TextView MaleEs;
    private String styleType = MLTtsConstants.TTS_SPEAKER_FEMALE_ZH;

    private void createStyleDialog() {
        this.styleDialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_style, null);
        this.styleDialog.setContentView(view);
        this.FemaleZh = view.findViewById(R.id.female_zh);
        this.FemaleZh.setOnClickListener(this);
        this.FemaleEn = view.findViewById(R.id.female_en);
        this.FemaleEn.setOnClickListener(this);
        this.MaleZh = view.findViewById(R.id.male_zh);
        this.MaleZh.setOnClickListener(this);
        this.MaleEh = view.findViewById(R.id.male_en);
        this.MaleEh.setOnClickListener(this);
        this.MaleFr = view.findViewById(R.id.male_fr);
        this.MaleFr.setOnClickListener(this);
        this.MaleEs = view.findViewById(R.id.male_es);
        this.MaleEs.setOnClickListener(this);
        this.styleDialog.setCanceledOnTouchOutside(true);
        // Set the size of the dialog
        Window dialogWindow = this.styleDialog.getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(layoutParams);
        }
    }

    private Dialog languageDialog;
    private TextView textCN;
    private TextView textEN;
    private TextView textFr;
    private TextView textEs;
    private String textType = MLTtsConstants.TTS_ZH_HANS;

    private void createLanguageDialog() {
        this.languageDialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_tts_language, null);
        // Set up a custom layout
        this.languageDialog.setContentView(view);
        this.textCN = view.findViewById(R.id.simple_cn);
        this.textCN.setOnClickListener(this);
        this.textEN = view.findViewById(R.id.english);
        this.textEN.setOnClickListener(this);
        this.textFr = view.findViewById(R.id.french);
        this.textFr.setOnClickListener(this);
        this.textEs = view.findViewById(R.id.spanish);
        this.textEs.setOnClickListener(this);
        this.languageDialog.setCanceledOnTouchOutside(true);
        // Set the size of the dialog
        Window dialogWindow = this.languageDialog.getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(layoutParams);
        }
    }

    private Dialog playModeDialog;
    private TextView textQueue;
    private TextView textClear;

    private void createModeDialog() {
        this.playModeDialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_mode, null);
        this.playModeDialog.setContentView(view);
        this.textQueue = view.findViewById(R.id.queueing_mode);
        this.textQueue.setOnClickListener(this);
        this.textClear = view.findViewById(R.id.clear_mode);
        this.textClear.setOnClickListener(this);
        this.playModeDialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = this.playModeDialog.getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(layoutParams);
        }
    }

    private void initLanguageDialogViews() {
        this.textCN.setSelected(false);
        this.textEN.setSelected(false);
        switch (textType) {
            case MLTtsConstants.TTS_ZH_HANS:
                this.textCN.setSelected(true);
                break;
            case MLTtsConstants.TTS_EN_US:
                this.textEN.setSelected(true);
                break;
            default:
                break;
        }
    }

    private void initStyleDialogViews() {
        this.FemaleZh.setSelected(false);
        this.FemaleEn.setSelected(false);
        this.MaleZh.setSelected(false);
        this.MaleEh.setSelected(false);
        switch (styleType) {
            case MLTtsConstants.TTS_SPEAKER_FEMALE_ZH:
                this.FemaleZh.setSelected(true);
                break;
            case MLTtsConstants.TTS_SPEAKER_FEMALE_EN:
                this.FemaleEn.setSelected(true);
                break;
            case MLTtsConstants.TTS_SPEAKER_MALE_ZH:
                this.MaleZh.setSelected(true);
                break;
            case MLTtsConstants.TTS_SPEAKER_MALE_EN:
                this.MaleEh.setSelected(true);
                break;
            default:
                break;
        }
    }

    private void initModeDialogViews() {
        this.textQueue.setSelected(false);
        this.textClear.setSelected(false);
        if (isFlush) {
            this.textClear.setSelected(true);
        } else {
            this.textQueue.setSelected(true);
        }
    }

    private void initAction() {
        addBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);

        speedSeek.setOnSeekBarChangeListener(this);
        volumeSeek.setOnSeekBarChangeListener(this);
        clear.setOnClickListener(this);

        rl_language.setOnClickListener(this);
        rl_style.setOnClickListener(this);
        rl_mode.setOnClickListener(this);
        back.setOnClickListener(this);
    }

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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.button_background));
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mlTtsEngine != null) {
            mlTtsEngine.stop();
            mlTtsEngine = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    private void playAudio(String audioFilePath) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                editText.setText("");
                break;
            case R.id.btn_play:
                // Use the system player to play the cached audio.
                playAudio(AUDIO_FILE_NAME_WAV);
                break;
            case R.id.btn_add:
                if (mlTtsEngine == null) {
                    return;
                }
                if (isPause) {
                    isPause = false;
                    pauseBtn.setText(R.string.pause);
                }
                sendMsg(MESSAGE_TYPE_INFO, null);
                String text = editText.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.please_enter_text, Toast.LENGTH_SHORT).show();
                }
                String id = mlTtsEngine.speak(text, isFlush ? MLTtsEngine.QUEUE_FLUSH : MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM);
                temp.put(id, text);
                break;
            case R.id.btn_pause:
                isPause = !isPause;
                pauseBtn.setText(isPause ? R.string.resume : R.string.pause);
                if (isPause) {
                    mlTtsEngine.pause();
                } else {
                    mlTtsEngine.resume();
                }
                break;

            case R.id.btn_stop:
                mlTtsEngine.stop();
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.rl_language:
                showLanguageDialog();
                break;
            case R.id.rl_style:
                showStyleDialog();
                break;
            case R.id.rl_mode:
                showModeDialog();
                break;
            case R.id.simple_cn:
                language = languageSpeak[0];
                updateConfig();
                textType = language;
                languageText.setText(languageResources[0]);
                this.languageDialog.dismiss();
                break;
            case R.id.english:
                language = languageSpeak[1];
                updateConfig();
                textType = language;
                languageText.setText(languageResources[1]);
                this.languageDialog.dismiss();
                break;
            case R.id.french:
                language = languageSpeak[2];
                updateConfig();
                textType = language;
                languageText.setText(languageResources[2]);
                this.languageDialog.dismiss();
                break;
            case R.id.spanish:
                language = languageSpeak[3];
                updateConfig();
                textType = language;
                languageText.setText(languageResources[3]);
                this.languageDialog.dismiss();
                break;
            case R.id.female_zh:
                person = languageStyle[0];
                updateConfig();
                styleType = person;
                styleText.setText(languageStyleResources[0]);
                this.styleDialog.dismiss();
                break;
            case R.id.female_en:
                person = languageStyle[1];
                updateConfig();
                styleType = person;
                styleText.setText(languageStyleResources[1]);
                this.styleDialog.dismiss();
                break;
            case R.id.male_zh:
                person = languageStyle[2];
                updateConfig();
                styleType = person;
                styleText.setText(languageStyleResources[2]);
                this.styleDialog.dismiss();
                break;
            case R.id.male_en:
                person = languageStyle[3];
                updateConfig();
                styleType = person;
                styleText.setText(languageStyleResources[3]);
                this.styleDialog.dismiss();
                break;
            case R.id.male_fr:
                person = languageStyle[4];
                updateConfig();
                styleType = person;
                styleText.setText(languageStyleResources[4]);
                this.styleDialog.dismiss();
                break;
            case R.id.male_es:
                person = languageStyle[5];
                updateConfig();
                styleType = person;
                styleText.setText(languageStyleResources[5]);
                this.styleDialog.dismiss();
                break;
            case R.id.queueing_mode:
                isFlush = false;
                modeText.setText(playModeResources[0]);
                this.playModeDialog.dismiss();
                break;
            case R.id.clear_mode:
                isFlush = true;
                modeText.setText(playModeResources[1]);
                this.playModeDialog.dismiss();
                break;
            default:
                break;
        }
    }

    private void showStyleDialog() {
        initStyleDialogViews();
        styleDialog.show();
    }

    private void showLanguageDialog() {
        initLanguageDialogViews();
        languageDialog.show();
    }

    private void showModeDialog() {
        initModeDialogViews();
        playModeDialog.show();
    }

    private void updateConfig() {
        Log.w(TAG, "language：" + language + "   " + "person: " + person);
        MLTtsConfig mlTtsConfig = new MLTtsConfig().setVolume(volumeVal)
                .setSpeed(speedVal)
                .setLanguage(language)
                .setPerson(person);
        mlTtsEngine.updateConfig(mlTtsConfig);
    }

    float speedVal = 1.0f;
    float volumeVal = 1.0f;

    String person = MLTtsConstants.TTS_SPEAKER_FEMALE_ZH;
    String language = MLTtsConstants.TTS_ZH_HANS;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String text = progress * 100 / seekBar.getMax() + "%";
        switch (seekBar.getId()) {
            case R.id.volumeSeek:
                textView_volume.setText(text);
                break;
            case R.id.speedSeek:
                textView_speed.setText(text);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.volumeSeek:  // volumeSeek
                volumeVal = seekBar.getProgress() / 10f;
                updateConfig();
                break;
            case R.id.speedSeek:  // speedSeek
                speedVal = seekBar.getProgress() / 10f;
                updateConfig();
                break;
            default:
                break;
        }
    }
}
