package com.example.gui.Result_Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.DialogFragment;

import com.example.gui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SearchDialog extends DialogFragment {
    OnMyDialogResult mDialogResult;

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Context context;
    private AutoCompleteTextView autoTextView;
    private AutoCompleteAdapter adapter;
    private TextView selectTextView;
    private Handler handler;
    private Button okBtn, cancelBtn;


    public static SearchDialog getInstance() {
        SearchDialog frag = new SearchDialog();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_foodsearch, container);
        //initView
        // initView
        okBtn = rootView.findViewById(R.id.dialogOkBtn);
        cancelBtn = rootView.findViewById(R.id.dialogCancelBtn);
        selectTextView = rootView.findViewById(R.id.selected_item);
        autoTextView = (AutoCompleteTextView) rootView.findViewById(R.id.auto_complete_edit_text);
        autoTextView.setThreshold(2);
        int layout = android.R.layout.simple_dropdown_item_1line;
        adapter = new AutoCompleteAdapter(getContext(), layout);
        autoTextView.setAdapter(adapter);
        this.listener();

        return rootView;
    }

    public void listener() {
        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectTextView.setText(adapter.getItem(position));
            }
        });
        autoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoTextView.getText())) {
                        makeApiCall(autoTextView.getText().toString());
                    }
                }
                return false;
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int strIndex = -1;
                String str = selectTextView.getText().toString();
                // Remove Space
                str = str.replaceAll(" ", "");
                // Find Kcal index
                strIndex = str.indexOf("Kcal") - 1;
                while (Character.isDigit(str.charAt(strIndex)) ) {
                    strIndex -= 1;
                    if(str.charAt(strIndex) == '.')
                        strIndex -= 1;
                }
                String kCal = str.substring(strIndex + 1, str.indexOf("Kcal"));
                String foodName = str.substring(0, strIndex + 1);
                String tmp = foodName + "-1-" + kCal + "@";
                if(mDialogResult != null){
                    mDialogResult.finish(tmp);
                }
                dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    private void makeApiCall(String text) {
        String portNumber = "5000";
        String postUrl = "http://" + "58.224.125.166" + ":" + portNumber + "/autofood";
        List<String> stringList = new ArrayList<>();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();
            RequestBody body = new FormBody.Builder()
                    .add("word", text)
                    .build();
            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {


                        getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try(ResponseBody responseBody = response.body()) {
                                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                                        String tmp = response.body().string();
                                        String tmpsplit[] = tmp.split("@");
                                        for (int i = 0; i < tmpsplit.length; i++)
                                            stringList.add(tmpsplit[i]);
                                        adapter.setData(stringList);
                                        adapter.notifyDataSetChanged();
                                    } catch (Exception e){

                                    }
                                }
                            });

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }


    public interface OnMyDialogResult {
        void finish(String result);
    }


}
