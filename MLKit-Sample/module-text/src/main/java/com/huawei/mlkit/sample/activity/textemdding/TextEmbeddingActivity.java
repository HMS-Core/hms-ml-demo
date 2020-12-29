/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.mlkit.sample.activity.textemdding;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.textembedding.MLTextEmbeddingAnalyzer;
import com.huawei.hms.mlsdk.textembedding.MLTextEmbeddingAnalyzerFactory;
import com.huawei.hms.mlsdk.textembedding.MLTextEmbeddingException;
import com.huawei.hms.mlsdk.textembedding.MLTextEmbeddingSetting;
import com.huawei.hms.mlsdk.textembedding.MLVocabularyVersion;
import com.huawei.mlkit.sample.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextEmbeddingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TextEmbeddingActivity";
    public static final String API_KEY = "client/api_key";
    private MLTextEmbeddingAnalyzer analyzer;

    private TextView tv_dic_info;
    private LinearLayout show_result_dic_info;
    private Button btn_dic_info;
    private TextView result_dic_info;

    private TextView tv_word2v;
    private TextView tv_words2v;
    private TextView tv_sentence2v;
    private TextView tv_word2s;
    private TextView tv_sentence2s;
    private TextView tv_word_for_simil;

    private Spinner languages;

    private LinearLayout show_word_vector;
    private EditText input_word;
    private TextView result_word_vector;
    private ImageButton delete_word;
    private ImageButton search_word;

    private LinearLayout show_words1_vector;
    private EditText input_words1;
    private TextView result_words1_vector;
    private ImageButton delete_words1;
    private ImageButton search_words1;

    private LinearLayout show_sentence_vector;
    private EditText input_sentence;
    private TextView result_sentence_vector;
    private ImageButton delete_sentence;
    private ImageButton search_sentence;

    private LinearLayout show_word_simil;
    private EditText input_word1;
    private EditText input_word2;
    private TextView result_word_simil;
    private ImageButton delete_words;
    private ImageButton search_word_simil;

    private LinearLayout show_sentence_simil;
    private EditText input_sentence1;
    private EditText input_sentence2;
    private TextView result_sentence_simil;
    private ImageButton delete_sentences;
    private ImageButton search_sentence_simil;

    private LinearLayout show_simil_words;
    private EditText input_word_for_simil;
    private EditText input_quantity;
    private TextView result_simil_words;
    private ImageButton delete_word_for_simil;
    private ImageButton search_simil_words;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_embedding);
        setApiKey();

        tv_dic_info = findViewById(R.id.tv_dic_info);
        tv_dic_info.setOnClickListener(this);

        tv_word2v = findViewById(R.id.tv_word2v);
        tv_word2v.setOnClickListener(this);

        tv_words2v = findViewById(R.id.tv_words2v);
        tv_words2v.setOnClickListener(this);

        tv_sentence2v = findViewById(R.id.tv_sentence2v);
        tv_sentence2v.setOnClickListener(this);

        tv_word2s = findViewById(R.id.tv_word2s);
        tv_word2s.setOnClickListener(this);

        tv_sentence2s = findViewById(R.id.tv_sentence2s);
        tv_sentence2s.setOnClickListener(this);

        tv_word_for_simil = findViewById(R.id.tv_word_for_simil);
        tv_word_for_simil.setOnClickListener(this);

        show_result_dic_info = findViewById(R.id.show_result_dic_info);
        btn_dic_info = findViewById(R.id.btn_dic_info);
        result_dic_info = findViewById(R.id.result_dic_info);

        languages = findViewById(R.id.languages);

        show_word_vector = findViewById(R.id.show_word_vector);
        input_word = findViewById(R.id.input_word);
        result_word_vector = findViewById(R.id.result_word_vector);
        delete_word = findViewById(R.id.delete_word);
        search_word = findViewById(R.id.search_word);
        delete_word.setOnClickListener(this);

        show_words1_vector = findViewById(R.id.show_words1_vector);
        input_words1 = findViewById(R.id.input_words1);
        result_words1_vector = findViewById(R.id.result_words1_vector);
        delete_words = findViewById(R.id.delete_words1);
        search_words1 = findViewById(R.id.search_words1);
        delete_words.setOnClickListener(this);

        show_sentence_vector = findViewById(R.id.show_sentence_vector);
        input_sentence = findViewById(R.id.input_sentence);
        result_sentence_vector = findViewById(R.id.result_sentence_vector);
        delete_sentence = findViewById(R.id.delete_sentence);
        search_sentence = findViewById(R.id.search_sentence);
        delete_sentence.setOnClickListener(this);

        show_word_simil = findViewById(R.id.show_word_simil);
        input_word1 = findViewById(R.id.input_word1);
        input_word2 = findViewById(R.id.input_word2);
        result_word_simil = findViewById(R.id.result_word_simil);
        delete_words = findViewById(R.id.delete_words);
        search_word_simil = findViewById(R.id.search_word_simil);
        delete_words.setOnClickListener(this);

        show_sentence_simil = findViewById(R.id.show_sentence_simil);
        input_sentence1 = findViewById(R.id.input_sentence1);
        input_sentence2 = findViewById(R.id.input_sentence2);
        result_sentence_simil = findViewById(R.id.result_sentence_simil);
        delete_sentences = findViewById(R.id.delete_sentences);
        search_sentence_simil = findViewById(R.id.search_sentence_simil);
        delete_sentences.setOnClickListener(this);

        show_simil_words = findViewById(R.id.show_simil_words);
        input_word_for_simil = findViewById(R.id.input_word_for_simil);
        input_quantity = findViewById(R.id.input_quantity);
        result_simil_words = findViewById(R.id.result_simil_words);
        delete_word_for_simil = findViewById(R.id.delete_word_for_simil);
        search_simil_words = findViewById(R.id.search_simil_words);
        delete_word_for_simil.setOnClickListener(this);

        this.createTextEmbeddingAnalyzer();

        btn_dic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<MLVocabularyVersion> vocabularyVersionTask = analyzer.getVocabularyVersion();
                vocabularyVersionTask.addOnSuccessListener(new OnSuccessListener<MLVocabularyVersion>() {
                    @Override
                    public void onSuccess(MLVocabularyVersion dictionaryVersionVo) {
                        result_dic_info.setText(getString(R.string.versions) + dictionaryVersionVo.getVersionNo() + "\n"
                                + getString(R.string.dimension) + dictionaryVersionVo.getDictionaryDimension() + "\n"
                                + getString(R.string.size) + dictionaryVersionVo.getDictionarySize());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        MLTextEmbeddingException embeddingException = (MLTextEmbeddingException) e;
                        embeddingException.getErrCode();
                        embeddingException.getMessage();
                        Toast.makeText(TextEmbeddingActivity.this, embeddingException.getMessage() + ":" + embeddingException.getErrCode(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        search_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<Float[]> wordVectorTask = analyzer.analyseWordVector(input_word.getText().toString());
                Log.d(TAG, input_word.getText().toString());
                wordVectorTask.addOnSuccessListener(new OnSuccessListener<Float[]>() {
                    @Override
                    public void onSuccess(Float[] wordVector) {
                        Toast.makeText(TextEmbeddingActivity.this, "analyseWordVector successed", Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonObject = new JSONArray(wordVector);
                            String result = getString(R.string.Word_vector) + "\n" + jsonObject.toString();
                            result_word_vector.setText(result);
                        } catch (JSONException e) {
                            Log.e(TAG,e.getMessage());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        MLTextEmbeddingException embeddingException = (MLTextEmbeddingException) e;
                        embeddingException.getErrCode();
                        embeddingException.getMessage();
                        Toast.makeText(TextEmbeddingActivity.this, embeddingException.getMessage() + ":" + embeddingException.getErrCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        search_words1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, input_words1.getText().toString());

                String words = input_words1.getText().toString();
                Set<String> stringSet = new HashSet<String>();
                if (!words.isEmpty()) {
                    List<String> stringList = Arrays.asList(words.split(","));
                    stringSet.addAll(stringList);
                }

                Task<Map<String, Float[]>> wordVectorBatchTask = analyzer.analyseWordVectorBatch(stringSet);
                wordVectorBatchTask.addOnSuccessListener(new OnSuccessListener<Map<String, Float[]>>() {
                    @Override
                    public void onSuccess(Map<String, Float[]> wordsVector) {
                        JSONObject jsonObject = new JSONObject(wordsVector);
                        String result = getString(R.string.Words_vector) + "\n" + jsonObject.toString();
                        result_words1_vector.setText(result);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        MLTextEmbeddingException embeddingException = (MLTextEmbeddingException) e;
                        embeddingException.getErrCode();
                        embeddingException.getMessage();
                        Toast.makeText(TextEmbeddingActivity.this, embeddingException.getMessage() + ":" + embeddingException.getErrCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        search_sentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<Float[]> sentenceVectorTask = analyzer.analyseSentenceVector(input_sentence.getText().toString());
                Log.d(TAG, input_sentence.getText().toString());
                sentenceVectorTask.addOnSuccessListener(new OnSuccessListener<Float[]>() {
                    @Override
                    public void onSuccess(Float[] sentenceVector) {
                        Toast.makeText(TextEmbeddingActivity.this, "analyseSentenceVector successed", Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonObject = new JSONArray(sentenceVector);
                            String result = getString(R.string.Sentence_vector) + "\n" + jsonObject.toString();
                            result_sentence_vector.setText(result);
                        } catch (JSONException e) {
                            Log.e(TAG,e.getMessage());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        MLTextEmbeddingException embeddingException = (MLTextEmbeddingException) e;
                        embeddingException.getErrCode();
                        embeddingException.getMessage();
                        Toast.makeText(TextEmbeddingActivity.this, embeddingException.getMessage() + ":" + embeddingException.getErrCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        search_word_simil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<Float> wordsSimilarityTask = analyzer.analyseWordsSimilarity(input_word1.getText().toString(), input_word2.getText().toString());
                wordsSimilarityTask.addOnSuccessListener(new OnSuccessListener<Float>() {
                    @Override
                    public void onSuccess(Float wordsSimilarity) {
                        Toast.makeText(TextEmbeddingActivity.this, "analyseWordsSimilarity successed", Toast.LENGTH_SHORT).show();
                        result_word_simil.setText(getString(R.string.Word_similarity) + "\n" + wordsSimilarity);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        MLTextEmbeddingException embeddingException = (MLTextEmbeddingException) e;
                        embeddingException.getErrCode();
                        embeddingException.getMessage();
                        Toast.makeText(TextEmbeddingActivity.this, embeddingException.getMessage() + ":" + embeddingException.getErrCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        search_sentence_simil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Task<Float> sentencesSimilarityTask = analyzer.analyseSentencesSimilarity(input_sentence1.getText().toString(), input_sentence2.getText().toString());
                sentencesSimilarityTask.addOnSuccessListener(new OnSuccessListener<Float>() {
                    @Override
                    public void onSuccess(Float sentencesSimilarity) {
                        Toast.makeText(TextEmbeddingActivity.this, "analyseSentencesSimilarity successed", Toast.LENGTH_SHORT).show();
                        result_sentence_simil.setText(getString(R.string.sentence_similarity) + "\n" + sentencesSimilarity);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        MLTextEmbeddingException embeddingException = (MLTextEmbeddingException) e;
                        embeddingException.getErrCode();
                        embeddingException.getMessage();
                        Toast.makeText(TextEmbeddingActivity.this, embeddingException.getMessage() + ":" + embeddingException.getErrCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        search_simil_words.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<List<String>> multipleSimilarityWordsTask = analyzer.analyseSimilarWords(input_word_for_simil.getText().toString(), Integer.valueOf(input_quantity.getText().toString()));
                multipleSimilarityWordsTask.addOnSuccessListener(new OnSuccessListener<List<String>>() {
                    @Override
                    public void onSuccess(List<String> words) {
                        Toast.makeText(TextEmbeddingActivity.this, "analyseSimilarWords successed", Toast.LENGTH_SHORT).show();
                        JSONArray jsonObject = new JSONArray(words);
                        result_simil_words.setText(getString(R.string.similar_word) + jsonObject.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        MLTextEmbeddingException embeddingException = (MLTextEmbeddingException) e;
                        embeddingException.getErrCode();
                        embeddingException.getMessage();
                        Toast.makeText(TextEmbeddingActivity.this, embeddingException.getMessage() + ":" + embeddingException.getErrCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Read the ApiKey field in the agconnect-services.json to obtain the API key of the application and set it.
     * For details about how to apply for the agconnect-services.json, see section https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-add-agc.
     */
    private void setApiKey() {
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getApplication());
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dic_info:
                show_result_dic_info.setVisibility(View.VISIBLE);
                show_word_vector.setVisibility(View.GONE);
                show_sentence_vector.setVisibility(View.GONE);
                show_word_simil.setVisibility(View.GONE);
                show_sentence_simil.setVisibility(View.GONE);
                show_simil_words.setVisibility(View.GONE);
                show_words1_vector.setVisibility(View.GONE);
                break;
            case R.id.tv_word2v:
                show_result_dic_info.setVisibility(View.GONE);
                show_word_vector.setVisibility(View.VISIBLE);
                show_sentence_vector.setVisibility(View.GONE);
                show_word_simil.setVisibility(View.GONE);
                show_sentence_simil.setVisibility(View.GONE);
                show_simil_words.setVisibility(View.GONE);
                show_words1_vector.setVisibility(View.GONE);
                break;
            case R.id.tv_words2v:
                show_word_vector.setVisibility(View.GONE);
                show_result_dic_info.setVisibility(View.GONE);
                show_words1_vector.setVisibility(View.VISIBLE);
                show_sentence_vector.setVisibility(View.GONE);
                show_word_simil.setVisibility(View.GONE);
                show_sentence_simil.setVisibility(View.GONE);
                show_simil_words.setVisibility(View.GONE);
                break;
            case R.id.tv_sentence2v:
                show_result_dic_info.setVisibility(View.GONE);
                show_word_vector.setVisibility(View.GONE);
                show_sentence_vector.setVisibility(View.VISIBLE);
                show_word_simil.setVisibility(View.GONE);
                show_sentence_simil.setVisibility(View.GONE);
                show_simil_words.setVisibility(View.GONE);
                show_words1_vector.setVisibility(View.GONE);
                break;
            case R.id.tv_word2s:
                show_result_dic_info.setVisibility(View.GONE);
                show_word_vector.setVisibility(View.GONE);
                show_sentence_vector.setVisibility(View.GONE);
                show_word_simil.setVisibility(View.VISIBLE);
                show_sentence_simil.setVisibility(View.GONE);
                show_simil_words.setVisibility(View.GONE);
                show_words1_vector.setVisibility(View.GONE);
                break;
            case R.id.tv_sentence2s:
                show_result_dic_info.setVisibility(View.GONE);
                show_word_vector.setVisibility(View.GONE);
                show_sentence_vector.setVisibility(View.GONE);
                show_word_simil.setVisibility(View.GONE);
                show_sentence_simil.setVisibility(View.VISIBLE);
                show_simil_words.setVisibility(View.GONE);
                show_words1_vector.setVisibility(View.GONE);
                break;
            case R.id.tv_word_for_simil:
                show_result_dic_info.setVisibility(View.GONE);
                show_word_vector.setVisibility(View.GONE);
                show_sentence_vector.setVisibility(View.GONE);
                show_word_simil.setVisibility(View.GONE);
                show_sentence_simil.setVisibility(View.GONE);
                show_simil_words.setVisibility(View.VISIBLE);
                show_words1_vector.setVisibility(View.GONE);
                break;
            case R.id.delete_word:
                input_word.setText("");
                break;
            case R.id.delete_words1:
                input_words1.setText("");
                break;
            case R.id.delete_sentence:
                input_sentence.setText("");
                break;
            case R.id.delete_words:
                input_word1.setText("");
                input_word2.setText("");
                break;
            case R.id.delete_sentences:
                input_sentence1.setText("");
                input_sentence2.setText("");
                break;
            case R.id.delete_word_for_simil:
                input_word_for_simil.setText("");
                input_quantity.setText("");
                break;
            default:
                break;
        }
    }

    private void createTextEmbeddingAnalyzer() {
        MLTextEmbeddingSetting setting = new MLTextEmbeddingSetting.Factory()
                .setLanguage(MLTextEmbeddingSetting.LANGUAGE_ZH)
                .create();
        this.analyzer = MLTextEmbeddingAnalyzerFactory.getInstance().getMLTextEmbeddingAnalyzer(setting);

        languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String language = MLTextEmbeddingSetting.LANGUAGE_ZH;
                if (position == 1) {
                    language = MLTextEmbeddingSetting.LANGUAGE_EN;
                } else {
                    language = MLTextEmbeddingSetting.LANGUAGE_ZH;
                }

                MLTextEmbeddingSetting setting = new MLTextEmbeddingSetting.Factory()
                        .setLanguage(language)
                        .create();
                TextEmbeddingActivity.this.analyzer = MLTextEmbeddingAnalyzerFactory.getInstance().getMLTextEmbeddingAnalyzer(setting);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}