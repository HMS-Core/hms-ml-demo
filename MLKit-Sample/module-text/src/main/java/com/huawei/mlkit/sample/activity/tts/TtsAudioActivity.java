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

package com.huawei.mlkit.sample.activity.tts;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsSpeaker;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.adapter.TTSLanguageAdapter;
import com.huawei.mlkit.sample.activity.adapter.TtsStyleAdapter;
import com.huawei.mlkit.sample.util.FileUtils;
import com.huawei.mlkit.sample.util.PCMToWav;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.huawei.mlkit.sample.R.color;
import static com.huawei.mlkit.sample.R.id;
import static com.huawei.mlkit.sample.R.layout;
import static com.huawei.mlkit.sample.R.string;
import static com.huawei.mlkit.sample.R.style;

public class TtsAudioActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, TTSLanguageAdapter.OnItemClickListener, TtsStyleAdapter.OnItemClickListener {
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
    private TextView speakerText;
    private TextView modeText;

    private ImageView clear;

    private RelativeLayout rl_language;
    private RelativeLayout rl_speaker;
    private RelativeLayout rl_mode;

    private MLTtsEngine mlTtsEngine;
    private TextView back;

    private Map<String, String> languageMap = new HashMap<>();
    private Map<String, String> speakerMap = new HashMap<>();
    private int[] playModeResources = new int[]{string.queuing_mode, string.clear_mode};
    private boolean isFlush = false;
    private boolean isPause = false;

    private float speedVal = 1.0f;
    private float volumeVal = 1.0f;

    private Map<String, String> temp = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_tts_audio);
        MLTtsConfig mlConfigs = new MLTtsConfig();
        mlTtsEngine = new MLTtsEngine(mlConfigs);
        initLanguageAndVoice();
        initView();
        initAction();
        // Set playback callback
        mlTtsEngine.setTtsCallback(callback);
        updateConfig();
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

    /**
     * Set the language and speaker corresponding to the language and speaker code.
     */
    private void initLanguageAndVoice() {
        languageMap.put("zh-Hans", this.getString(string.chinese));
        languageMap.put("en-US", this.getString(string.english_choose));
        languageMap.put("fr-FR", this.getString(string.french));
        languageMap.put("it-IT", this.getString(string.Italian));
        languageMap.put("de-DE", this.getString(string.German));
        languageMap.put("es-ES", this.getString(string.spanish));
        speakerMap.put("zh-Hans-st-1", this.getString(string.female_zh));
        speakerMap.put("zh-Hans-st-2", this.getString(string.male_zh));
        speakerMap.put("en-US-st-1", this.getString(string.female_en));
        speakerMap.put("en-US-st-2", this.getString(string.male_en));
        speakerMap.put("fr-FR-st-1", this.getString(string.female_fr));
        speakerMap.put("it-IT-st-1", this.getString(string.female_it));
        speakerMap.put("de-DE-st-1", this.getString(string.female_de));
        speakerMap.put("es-ES-st-1", this.getString(string.female_es));
    }

    private void initView() {
        editText = findViewById(id.edit_text);
        addBtn = findViewById(id.btn_add);

        playBtn = findViewById(id.btn_play);
        pauseBtn = findViewById(id.btn_pause);
        stopBtn = findViewById(id.btn_stop);
        volumeSeek = findViewById(id.volumeSeek);
        speedSeek = findViewById(id.speedSeek);

        clear = findViewById(id.close);

        textView_volume = findViewById(id.textView_volume);
        textView_speed = findViewById(id.textView_speed);
        back = findViewById(id.back);

        languageText = findViewById(id.languagetext);
        speakerText = findViewById(id.styletext);
        modeText = findViewById(id.modetext);
        rl_language = findViewById(id.rl_language);
        rl_speaker = findViewById(id.rl_style);
        rl_mode = findViewById(id.rl_mode);

        createLanguageDialog();
        createStyleDialog();
        createModeDialog();

        textView_volume.setText(string.init_progress);
        textView_speed.setText(string.init_progress);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case MESSAGE_TYPE_INFO:
                    String extension = (String) message.obj;
                    if (extension == null) {
                        addBtn.setText(string.queue_add);
                        break;
                    }
                    if (NO_NETWORK.equals(extension)) {
                        showFailedDialog(string.nonetwork);
                    } else if (SPEAK_ABNORMAL.equals(extension)) {
                        showFailedDialog(string.speak_abnormal);
                    } else {
                        showFailedDialog(string.abnormal);
                    }
                    addBtn.setText(string.replay);
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
                    style.setSpan(new ForegroundColorSpan(getResources().getColor(color.button_background)),
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

    private TTSLanguageAdapter tTSLanguageAdapter;
    private TtsStyleAdapter ttsStyleAdapter;
    private List<String> languageCodeList = new ArrayList<>();
    private List<String> speakerCodeList = new ArrayList<>();
    private String defaultLanguageCode = "";
    private String defaultSpeakerCode = "";

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
     * @param v     view
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

    private Dialog languageDialog;

    private void createLanguageDialog() {
        this.languageDialog = new Dialog(this, style.MyDialogStyle);
        View view = View.inflate(this, layout.dialog_tts_language, null);
        // Set up a custom layout
        this.languageDialog.setContentView(view);

        RecyclerView languageRv = view.findViewById(id.language_rv);
        languageCodeList = mlTtsEngine.getLanguages();
        List<String> languageList = new ArrayList<>();
        if (languageCodeList != null) {
            if (defaultLanguageCode.equals("")) {
                defaultLanguageCode = languageCodeList.get(0);
            }
            for (String str : languageCodeList) {
                if (!languageList.contains(languageMap.get(str))) {
                    languageList.add(languageMap.get(str));
                }
            }
            languageText.setText(languageMap.get(defaultLanguageCode));
        }

        Log.d("languageCodeList", languageCodeList.toString());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        languageRv.setLayoutManager(linearLayoutManager);
        languageRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Initialize the adapter.
        tTSLanguageAdapter = new TTSLanguageAdapter(this, languageList);

        // Set adapter.
        languageRv.setAdapter(tTSLanguageAdapter);
        //Set Entry Click Event
        tTSLanguageAdapter.setOnItemClickListener(this);

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

    private Dialog speakerDialog;

    private void createStyleDialog() {
        this.speakerDialog = new Dialog(this, style.MyDialogStyle);
        View view = View.inflate(this, layout.dialog_style, null);
        this.speakerDialog.setContentView(view);

        RecyclerView styleRv = view.findViewById(id.tts_style_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        styleRv.setLayoutManager(linearLayoutManager);
        styleRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        List<MLTtsSpeaker> mlTtsSpeakerCodeList = mlTtsEngine.getSpeaker(defaultLanguageCode);
        List<String> speakerList = new ArrayList<>();
        speakerCodeList.clear();
        if (mlTtsSpeakerCodeList != null) {
            if (defaultSpeakerCode.equals("")) {
                defaultSpeakerCode = mlTtsSpeakerCodeList.get(0).getName();
            }
            for (MLTtsSpeaker str : mlTtsSpeakerCodeList) {
                if (!speakerList.contains(speakerMap.get(str.getName()))) {
                    speakerCodeList.add(str.getName());
                    speakerList.add(speakerMap.get(str.getName()));
                }
            }
            speakerText.setText(speakerMap.get(defaultSpeakerCode));
        }
        Log.d(TAG, speakerCodeList.toString());

        // Initialize adapter.
        ttsStyleAdapter = new TtsStyleAdapter(this, speakerList);

        // set adapter
        styleRv.setAdapter(ttsStyleAdapter);
        // Set Entry Click Event
        ttsStyleAdapter.setOnItemClickListener(this);

        this.speakerDialog.setCanceledOnTouchOutside(true);
        // Set the size of the dialog
        Window dialogWindow = this.speakerDialog.getWindow();
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
        this.playModeDialog = new Dialog(this, style.MyDialogStyle);
        View view = View.inflate(this, layout.dialog_mode, null);
        this.playModeDialog.setContentView(view);
        this.textQueue = view.findViewById(id.queueing_mode);
        this.textQueue.setOnClickListener(this);
        this.textClear = view.findViewById(id.clear_mode);
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
        rl_speaker.setOnClickListener(this);
        rl_mode.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void showFailedDialog(int res) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(res)
                .setPositiveButton(getString(string.str_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(color.button_background));
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
            case id.close:
                editText.setText("");
                break;
            case id.btn_play:
                // Use the system player to play the cached audio.
                playAudio(AUDIO_FILE_NAME_WAV);
                break;
            case id.btn_add:
                if (mlTtsEngine == null) {
                    return;
                }
                if (isPause) {
                    isPause = false;
                    pauseBtn.setText(string.pause);
                }
                sendMsg(MESSAGE_TYPE_INFO, null);
                String text = editText.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(getApplicationContext(), string.please_enter_text, Toast.LENGTH_SHORT).show();
                }
                String id = mlTtsEngine.speak(text, isFlush ? MLTtsEngine.QUEUE_FLUSH : MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM);
                temp.put(id, text);
                break;
            case R.id.btn_pause:
                isPause = !isPause;
                pauseBtn.setText(isPause ? string.resume : string.pause);
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
                createLanguageDialog();
                showLanguageDialog();
                break;
            case R.id.rl_style:
                createStyleDialog();
                showStyleDialog();
                break;
            case R.id.rl_mode:
                showModeDialog();
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
        speakerDialog.show();
    }

    private void showLanguageDialog() {
        languageDialog.show();
    }

    private void showModeDialog() {
        initModeDialogViews();
        playModeDialog.show();
    }

    private void updateConfig() {
        Log.w(TAG, "language：" + defaultLanguageCode + "   " + "person: " + defaultSpeakerCode);
        MLTtsConfig mlTtsConfig = new MLTtsConfig().setVolume(volumeVal)
                .setSpeed(speedVal)
                .setLanguage(defaultLanguageCode)
                .setPerson(defaultSpeakerCode);
        mlTtsEngine.updateConfig(mlTtsConfig);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String text = progress * 100 / seekBar.getMax() + "%";
        switch (seekBar.getId()) {
            case id.volumeSeek:
                if (progress == 0) {
                    String text1 = 100 / seekBar.getMax() + "%";
                    seekBar.setProgress(1);
                    textView_volume.setText(text1);
                } else {
                    textView_volume.setText(text);
                }
                break;
            case id.speedSeek:
                if (progress == 0) {
                    String text1 = 100 / seekBar.getMax() + "%";
                    seekBar.setProgress(1);
                    textView_speed.setText(text1);
                } else {
                    textView_speed.setText(text);
                }
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
            case id.volumeSeek:  // volumeSeek
                volumeVal = seekBar.getProgress() * 18 / (100 * 10f);
                updateConfig();
                break;
            case id.speedSeek:  // speedSeek
                speedVal = seekBar.getProgress() * 18 / (100 * 10f);
                updateConfig();
                break;
            default:
                break;
        }
    }

    @Override
    public void setOnLanguageItemClick(View view, int position) {
        defaultLanguageCode = languageCodeList.get(position);
        languageText.setText(languageMap.get(defaultLanguageCode));
        updateConfig();
        tTSLanguageAdapter.notifyDataSetChanged();
        languageDialog.dismiss();
    }

    @Override
    public void setOnStyleItemClick(View view, int position) {
        defaultSpeakerCode = speakerCodeList.get(position);
        speakerText.setText(speakerMap.get(defaultSpeakerCode));
        updateConfig();
        ttsStyleAdapter.notifyDataSetChanged();
        speakerDialog.dismiss();
    }
}
