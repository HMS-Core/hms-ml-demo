/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.mlkit.sample.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadListener;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy;
import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsLocalModel;
import com.huawei.hms.mlsdk.tts.MLTtsSpeaker;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.adapter.TTSLocalLanguageAdapter;
import com.huawei.mlkit.sample.activity.adapter.TtsLocalStyleAdapter;
import com.huawei.mlkit.sample.util.FileUtils;
import com.huawei.mlkit.sample.util.PCMToWav;

/**
 * Function Description
 *
 * @since 2020-09-07
 */
public class OfflineModeFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, TtsLocalStyleAdapter.OnItemClickListener, TextWatcher {

    private static final String NO_NETWORK = "0104";
    private static final String SPEAK_ABNORMAL = "7002";

    private static final int MESSAGE_TYPE_INFO = 1;
    private static final int MESSAGE_TYPE_RANGE = 2;
    private static final String TAG = "OnlineModeFragment";

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
    private TextView tv_download_progress;

    private RelativeLayout rl_speaker;
    private RelativeLayout rl_mode;

    private MLTtsEngine mlTtsEngine;

    private Map<String, String> languageMap = new HashMap<>();
    private Map<String, String> speakerMap = new HashMap<>();
    private int[] playModeResources = new int[]{R.string.queuing_mode, R.string.clear_mode};
    private boolean isFlush = false;
    private boolean isPause = false;

    private float speedVal = 1.0f;
    private float volumeVal = 1.0f;

    private CharSequence temps;
    private int nowNum = 0;
    private int maxNum = 500;

    private Map<String, String> temp = new HashMap<>();
    private ImageView close;
    private TextView getChars;
    private List<MLTtsSpeaker> mlTtsSpeakerCodeList;
    private String language;

    public static OfflineModeFragment newInstance() {
        OfflineModeFragment fragment = new OfflineModeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tts_offline_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MLTtsConfig mlConfigs = new MLTtsConfig();
        mlConfigs.setSynthesizeMode(MLTtsConstants.TTS_OFFLINE_MODE);
        mlTtsEngine = new MLTtsEngine(mlConfigs);
        localModelManager = MLLocalModelManager.getInstance();
        initLanguageAndVoice();
        initView(view);
        initAction();
        // Set playback callback
        mlTtsEngine.setTtsCallback(callback);
        updateConfig();
        // Create audio file.
        AUDIO_PATH = FileUtils.initFile(getContext());
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
        languageMap.put("zh-Hans", this.getString(R.string.chinese));
        languageMap.put("en-us", this.getString(R.string.english_choose));
        languageMap.put("fr-fr", this.getString(R.string.french));
        languageMap.put("es-es", this.getString(R.string.spanish));
        languageMap.put("de-de", this.getString(R.string.German));
        languageMap.put("it-it", this.getString(R.string.Italian));
        speakerMap.put("zh-hans-st-eagle-1", this.getString(R.string.female_zh));
        speakerMap.put("zh-hans-st-eagle-2", this.getString(R.string.male_zh));
        speakerMap.put("en-us-st-eagle-1", this.getString(R.string.female_en_eagle));
        speakerMap.put("en-us-st-eagle-2", this.getString(R.string.male_en_eagle));
        speakerMap.put("en-us-st-bee-1", this.getString(R.string.female_en));
        speakerMap.put("fr-fr-st-bee-1", this.getString(R.string.female_fr));
        speakerMap.put("it-it-st-bee-1", this.getString(R.string.female_it));
        speakerMap.put("de-de-st-bee-1", this.getString(R.string.female_de));
        speakerMap.put("es-es-st-bee-1", this.getString(R.string.female_es));
    }


    private void initView(View view) {
        editText = view.findViewById(R.id.edit_text);
        addBtn = view.findViewById(R.id.btn_add);
        close = view.findViewById(R.id.close);
        getChars = view.findViewById(R.id.getChars);

        playBtn = view.findViewById(R.id.btn_play);
        pauseBtn = view.findViewById(R.id.btn_pause);
        stopBtn = view.findViewById(R.id.btn_stop);
        volumeSeek = view.findViewById(R.id.volumeSeek);
        speedSeek = view.findViewById(R.id.speedSeek);
        tv_download_progress = view.findViewById(R.id.tv_download_progress);

        textView_volume = view.findViewById(R.id.textView_volume);
        textView_speed = view.findViewById(R.id.textView_speed);

        languageText = view.findViewById(R.id.languagetext);
        speakerText = view.findViewById(R.id.styletext);
        modeText = view.findViewById(R.id.modetext);
        rl_speaker = view.findViewById(R.id.rl_style);
        rl_mode = view.findViewById(R.id.rl_mode);

        createStyleDialog();
        createModeDialog();

        defaultSpeakerCode = mlTtsSpeakerCodeList.get(0).getName();
        List<String> languages = mlTtsEngine.getLanguages();
        defaultLanguageCode = languages.get(0);


        textView_volume.setText(R.string.init_progress);
        textView_speed.setText(R.string.init_progress);
        modeText.setText(playModeResources[0]);
    }

    private Dialog speakerDialog;
    private TtsLocalStyleAdapter ttsLocalStyleAdapter;
    private List<String> speakerCodeList = new ArrayList<>();
    private String defaultLanguageCode = "";
    private String defaultSpeakerCode = "";
    private MLLocalModelManager localModelManager;
    private final static long M = 1024 * 1024;

    private void createStyleDialog() {
        this.speakerDialog = new Dialog(getContext(), R.style.MyDialogStyle);
        View view = View.inflate(getContext(), R.layout.dialog_style, null);
        this.speakerDialog.setContentView(view);

        RecyclerView styleRv = view.findViewById(R.id.tts_style_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        styleRv.setLayoutManager(linearLayoutManager);
        styleRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mlTtsSpeakerCodeList = mlTtsEngine.getSpeakers();
        List<String> speakerList = new ArrayList<>();
        speakerCodeList.clear();
        if (mlTtsSpeakerCodeList != null || mlTtsSpeakerCodeList.size() > 0) {
            if (defaultSpeakerCode.equals("")) {
                defaultSpeakerCode = mlTtsSpeakerCodeList.get(0).getName();
            }
            for (MLTtsSpeaker str : mlTtsSpeakerCodeList) {
                if (!speakerList.contains(speakerMap.get(str.getName())) && speakerMap.get(str.getName()) != null) {
                    speakerCodeList.add(str.getName());
                    speakerList.add(speakerMap.get(str.getName()));
                }
            }
            speakerText.setText(speakerMap.get(defaultSpeakerCode));
        }
        Log.d(TAG, speakerCodeList.toString());

        // Initialize adapter.
        ttsLocalStyleAdapter = new TtsLocalStyleAdapter(getActivity(), speakerCodeList, speakerMap);

        // set adapter
        styleRv.setAdapter(ttsLocalStyleAdapter);
        // Set Entry Click Event
        ttsLocalStyleAdapter.setOnItemClickListener(this);

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

    private void initAction() {
        addBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        close.setOnClickListener(this);


        speedSeek.setOnSeekBarChangeListener(this);
        volumeSeek.setOnSeekBarChangeListener(this);

        rl_speaker.setOnClickListener(this);
        rl_mode.setOnClickListener(this);

        editText.addTextChangedListener(this);
    }

    private void showFailedDialog(int res) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
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
                    Toast.makeText(getActivity(), R.string.please_enter_text, Toast.LENGTH_SHORT).show();
                }
                MLTtsLocalModel model = new MLTtsLocalModel.Factory(defaultSpeakerCode).create();
                localModelManager.isModelExist(model).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            String id = mlTtsEngine.speak(text, isFlush ? MLTtsEngine.QUEUE_FLUSH | MLTtsEngine.OPEN_STREAM : MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM);
                            temp.put(id, text);
                        } else {
                            downloadModel(true, defaultSpeakerCode, tv_download_progress);
                            showToast("The offline model has not been downloaded.！");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "downloadModel failed: " + e.getMessage());
                        showToast(e.getMessage());
                    }
                });
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
                getActivity().onBackPressed();
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


    private void playAudio(String audioFilePath) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private Dialog playModeDialog;
    private TextView textQueue;
    private TextView textClear;

    private void createModeDialog() {
        this.playModeDialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        View view = View.inflate(getActivity(), R.layout.dialog_mode, null);
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

    private void initModeDialogViews() {
        this.textQueue.setSelected(false);
        this.textClear.setSelected(false);
        if (isFlush) {
            this.textClear.setSelected(true);
        } else {
            this.textQueue.setSelected(true);
        }
    }


    private void showToast(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mlTtsEngine != null) {
            this.mlTtsEngine.shutdown();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String text = progress * 100 / seekBar.getMax() + "%";
        switch (seekBar.getId()) {
            case R.id.volumeSeek:
                if (progress == 0) {
                    String text1 = 100 / seekBar.getMax() + "%";
                    seekBar.setProgress(1);
                    textView_volume.setText(text1);
                } else {
                    textView_volume.setText(text);
                }
                break;
            case R.id.speedSeek:
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
            case R.id.volumeSeek:  // volumeSeek
                volumeVal = seekBar.getProgress() * 18 / (100 * 10f);
                updateConfig();
                break;
            case R.id.speedSeek:  // speedSeek
                speedVal = seekBar.getProgress() * 18 / (100 * 10f);
                updateConfig();
                break;
            default:
                break;
        }
    }

    @Override
    public void setOnStyleItemClick(String speaker) {
        defaultSpeakerCode = speaker;
        speakerText.setText(speakerMap.get(defaultSpeakerCode));
        for (int i = 0; i < mlTtsSpeakerCodeList.size(); i++) {
            language = mlTtsSpeakerCodeList.get(i).getLanguage();
            if (speaker.contains(language)) {
                defaultLanguageCode = language;
            }
        }

        updateConfig();
        ttsLocalStyleAdapter.notifyDataSetChanged();
        speakerDialog.dismiss();
    }

    @Override
    public void setOnDeleteClick(String speaker) {
        MLTtsLocalModel model = new MLTtsLocalModel.Factory(speaker).create();
        localModelManager.deleteModel(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (ttsLocalStyleAdapter != null) {
                    ttsLocalStyleAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    @Override
    public void setOnDownloadClick(String speaker, TextView has_download) {
        downloadModel(false, speaker, has_download);
    }

    /**
     * @param needSpeak     Indicates whether to invoke the speech synthesis method.
     * @param person        Pronunciation person
     * @param downloadState Display the download progress text
     */
    private void downloadModel(boolean needSpeak, String person, TextView downloadState) {
        final MLTtsLocalModel model = new MLTtsLocalModel.Factory(person).create();
        MLModelDownloadStrategy request = new MLModelDownloadStrategy.Factory().needWifi().create();

        MLModelDownloadListener modelDownloadListener = new MLModelDownloadListener() {
            @Override
            public void onProcess(long alreadyDownLength, long totalLength) {
                showProcess(downloadState, alreadyDownLength, "Model download is complete", totalLength);
            }
        };
        localModelManager.downloadModel(model, request, modelDownloadListener)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateConfig();
                        if (ttsLocalStyleAdapter != null) {
                            ttsLocalStyleAdapter.notifyDataSetChanged();
                        }
                        Log.i(TAG, "downloadModel: " + model.getModelName() + " success");
                        showToast("downloadModel Success");
                        if (needSpeak) {
                            String text = editText.getText().toString();
                            String id = mlTtsEngine.speak(text, isFlush ? MLTtsEngine.QUEUE_FLUSH : MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM);
                            temp.put(id, text);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "downloadModel failed: " + e.getMessage());
                        showToast(e.getMessage());
                    }
                });
    }

    private void showProcess(TextView downloadState, long alreadyDownLength, String buttonText, long totalLength) {
        double downDone = alreadyDownLength * 1.0 / M;
        double downTotal = totalLength * 1.0 / M;
        String downD = String.format(Locale.ROOT,"%.2f", downDone);
        String downT = String.format(Locale.ROOT,"%.2f", downTotal);

        String text = downD + "M" + "/" + downT + "M";
        updateButton(downloadState, text, false);
        if (downD.equals(downT)) {
            showToast(buttonText);
            updateButton(downloadState, buttonText, true);
        }
    }

    private void updateButton(TextView downloadState, final String text, final boolean downloadSuccess) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadState.setText(text);
                if (!downloadSuccess) {
                    downloadState.setVisibility(View.VISIBLE);
                } else {
                    downloadState.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        temps = s;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        nowNum = editText.length();
        if (nowNum == 500) {
            showToast(getString(R.string.tts_max_tip));
        }
        getChars.setText(getString(R.string.wording) + nowNum + " / " + maxNum);
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        if (temps.length() > maxNum) {
            editable.delete(selectionStart - 1, selectionEnd);
            editText.setText(editable.toString());
            int selection = editable.length();
            editText.setSelection(selection);
        }
    }
}
