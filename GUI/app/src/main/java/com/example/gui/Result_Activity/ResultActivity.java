package com.example.gui.Result_Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.gui.DBManger;
import com.example.gui.ui.main.MainActivity;
import com.example.gui.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {
    ImageView Food_imgview;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    String mCurrentPhotoPath, Time;
    File photofile;
    ChipGroup Cal_chipGroup, inbun_chipGroup, time_chipGroup;
    Chip breakfast_chip, lunch_chip, dinner_chip, inbun01, inbun02, inbun03, inbun04;
    Button result_btn;
    SQLiteDatabase db;
    DBManger dbManger;
    TextView day_textview, inbun_textview, total_textview, result_foodTextview;
    EditText contentEditText;
    ArrayList foodList;
    int foodindex;
    double total_cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        this.init();
        this.setDialog();
        this.setListener();
    }
    // init
    public void init(){
        // chipGroup View
        inbun_chipGroup = findViewById(R.id.inbun_chipgroup);
        Cal_chipGroup = findViewById(R.id.cal_chipgroup);
        time_chipGroup = findViewById(R.id.Time_ChipGroup);
        // IamgeView
        Food_imgview = findViewById(R.id.result_img);
        Food_imgview.setColorFilter(Color.parseColor("#FFFFFF"));
        // button View
        result_btn = findViewById(R.id.exerciseSavebtn);
        // textView
        day_textview = findViewById(R.id.result_daytextview);
        inbun_textview = findViewById(R.id.inbun_textview);
        total_textview = findViewById(R.id.total_textview);
        result_foodTextview = findViewById(R.id.result_foodTextview);
        // chip View
        breakfast_chip = findViewById(R.id.breakfast_chip);
        breakfast_chip.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
        lunch_chip = findViewById(R.id.lunch_chip);
        lunch_chip.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
        dinner_chip = findViewById(R.id.dinner_chip);
        dinner_chip.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
        inbun01 = findViewById(R.id.inbun025chip);
        inbun01.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
        inbun02 = findViewById(R.id.inbun05chip);
        inbun02.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
        inbun03 = findViewById(R.id.inbun01chip);
        inbun03.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
        inbun04 = findViewById(R.id.inbun20chip);
        inbun04.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
        // EditText
        contentEditText = findViewById(R.id.result_EditText);
        // UI Setting
        inbun_textview.setVisibility(View.GONE);
        inbun_chipGroup.setVisibility(View.GONE);
        total_textview.setVisibility(View.GONE);
        // Day
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("a h:mm");
        Time = simpleDate.format(mDate);
        day_textview.setText(Time);
        // ArrayList
        foodList = new ArrayList();
        // DB set
        Context context = this;
        dbManger = DBManger.getInstance(context, "Status", null, 1);
        db = dbManger.getWritableDatabase();
    }
    // Dialog Section
    public void setDialog(){
        CharSequence info[] = new CharSequence[] {"사진 찍기", "앨범에서 선택"};
        builder = new AlertDialog.Builder(this);
        builder.setItems(info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:
                        //사진찍기
                        takePicture();
                        break;
                    case 1:
                        //앨범에서 선택
                        takeAlbum();
                        break;
                }
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("이미지 인식 중");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
    }
    // Listener Function
    public void setListener(){
        Food_imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { builder.show(); }
        });
        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save Result Info
                // get Date
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                String getTime = simpleDate.format(mDate);
                System.out.println("날짜: " + getTime);
                // 이미지 경로 저장
                // 전체 칼로리 계산 후 저장
                if(breakfast_chip.isChecked()){
                    dbUpdate("Stats", getTime, (int)total_cal,0);
                    // exit activity
                    Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else if(lunch_chip.isChecked()){
                    dbUpdate("Stats", getTime, (int)total_cal,1);
                    Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // exit activity
                    finish();
                }else if(dinner_chip.isChecked()){
                    dbUpdate("Stats", getTime, (int)total_cal,2);
                    Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    // exit activity
                    finish();
                }else{
                    new AlertDialog.Builder(ResultActivity.this)
                            .setMessage("시간을 선택해주세요")
                            .setPositiveButton("확인", null)
                            .show();
                }
            }
        });
        Cal_chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == -1){
                    inbun_textview.setVisibility(View.GONE);
                    inbun_chipGroup.setVisibility(View.GONE);
                }else{
                    foodindex = checkedId-1;
                    // inbun UI setting
                    inbun_textview.setVisibility(View.VISIBLE);
                    inbun_chipGroup.setVisibility(View.VISIBLE);
                    // get food
                    String tmp = (String) foodList.get(checkedId-1);
                    String data[] = tmp.split("-");
                    // cal add
                    double foodcal = Double.parseDouble(data[1]) * Double.parseDouble(data[2]);
                    total_textview.setText(data[0] +"의 칼로리: " + foodcal + " cal\n총 섭취 칼로리: " + (int)total_cal + " cal");
                    // inbun_chipgroup check
                    if(Double.parseDouble(data[1]) == 1.0)
                        inbun_chipGroup.check(R.id.inbun01chip);
                    else if(Double.parseDouble(data[1]) >= 2.0)
                        inbun_chipGroup.check(R.id.inbun20chip);
                    else if(Double.parseDouble(data[1]) == 0.25)
                        inbun_chipGroup.check(R.id.inbun025chip);
                    else if(Double.parseDouble(data[1]) == 0.5)
                        inbun_chipGroup.check(R.id.inbun05chip);
                }
            }
        });
        inbun_chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                System.out.println("checkedid: " + checkedId);
                Toast.makeText(getApplicationContext(), "checkedId: " + checkedId, Toast.LENGTH_LONG).show();
                if (checkedId == -1){
                    String tmp = (String) foodList.get(foodindex);
                    String data[] = tmp.split("-");
                    if(Double.parseDouble(data[1]) == 1.0)
                        inbun_chipGroup.check(R.id.inbun01chip);
                    else if(Double.parseDouble(data[1]) >= 2.0)
                        inbun_chipGroup.check(R.id.inbun20chip);
                    else if(Double.parseDouble(data[1]) == 0.25)
                        inbun_chipGroup.check(R.id.inbun025chip);
                    else if(Double.parseDouble(data[1]) == 0.5)
                        inbun_chipGroup.check(R.id.inbun05chip);
                }
                String tmp;
                String data[];
                String copy;
                // total cal fix
                switch (checkedId){
                    // 1/4 inbun
                    case 2131362040:
                        // get Food data
                        tmp = (String) foodList.get(foodindex);
                        data = tmp.split("-");
                        // total cal replace
                        total_cal -= Double.parseDouble(data[1]) * Double.parseDouble(data[2]);
                        total_cal += Double.parseDouble(data[2]) * 0.25;
                        // Replace inbun
                        data[1] = "0.25";
                        copy = data[0] + "-" + data[1] + "-" + data[2];
                        foodList.set(foodindex, copy);
                        break;
                    // 1/2 inbun
                    case 2131362041:
                        tmp = (String) foodList.get(foodindex);
                        data = tmp.split("-");
                        total_cal -= Double.parseDouble(data[1]) * Double.parseDouble(data[2]);
                        total_cal += Double.parseDouble(data[2]) * 0.5;
                        data[1] = "0.5";
                        copy = data[0] + "-" + data[1] + "-" + data[2];
                        foodList.set(foodindex, copy);
                        break;
                    // 1 inbun
                    case 2131362039:
                        tmp = (String) foodList.get(foodindex);
                        data = tmp.split("-");
                        total_cal -= Double.parseDouble(data[1]) * Double.parseDouble(data[2]);
                        total_cal += Double.parseDouble(data[2]) * 1.0;
                        data[1] = "1";
                        copy = data[0] + "-" + data[1] + "-" + data[2];
                        foodList.set(foodindex, copy);
                        break;
                    // 2 inbun
                    case 2131362042:
                        tmp = (String) foodList.get(foodindex);
                        data = tmp.split("-");
                        total_cal -= Double.parseDouble(data[1]) * Double.parseDouble(data[2]);
                        total_cal += Double.parseDouble(data[2]) * 2.0;
                        data[1] = "2";
                        copy = data[0] + "-" + data[1] + "-" + data[2];
                        foodList.set(foodindex, copy);
                        break;
                    default:
                        break;
                }
                // food_textview set
                tmp = (String) foodList.get(foodindex);
                data = tmp.split("-");
                double foodcal = Double.parseDouble(data[1]) * Double.parseDouble(data[2]);
                total_textview.setText(data[0] +"의 칼로리: " + (int)foodcal + " cal\n총 섭취 칼로리: " + (int)total_cal + " cal");
            }
        });
        breakfast_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breakfast_chip.setChecked(true);
                dbSearch("Stats", 0);
            }
        });
        lunch_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lunch_chip.setChecked(true);
                dbSearch("Stats", 1);
            }
        });
        dinner_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dinner_chip.setChecked(true);
                dbSearch("Stats", 2);
            }
        });
        result_foodTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog dia = SearchDialog.getInstance();
                dia.show(getSupportFragmentManager(), null);
                dia.setDialogResult(new SearchDialog.OnMyDialogResult() {
                    @Override
                    public void finish(String result) {
                        // result = "불고기-1-335@삼겹살-1-330"
                        // data[0] = "불고기-1-335" data[1] = 삼겹살-1-330
                        String data[] = result.split("@");
                        for (int i = 0; i < data.length; i++) {
                            // str split
                            String split[] = data[i].split("-");
                            // total cal add
                            total_cal += Double.parseDouble(split[1]) * Double.parseDouble(split[2]);
                            // foodList add data
                            foodList.add(data[i]);
                            // Cal_ChipGroup Setting
                            Chip chip = new Chip(ResultActivity.this);
                            chip.setCheckable(true);
                            // close Icon Setting
                            chip.setCloseIconVisible(true);
                            // Text UI Setting
                            chip.setText(split[0]);
                            chip.setTextColor(Color.parseColor("#FFFFFF"));
                            // BG UI Setting
                            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#14151E")));
                            chip.setTextColor(Color.WHITE);
                            // Stroke UI Setting
                            chip.setChipStrokeWidth(3);
                            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#8F8FA1")));
                            // Cancel Ui Setting
                            chip.getCloseIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                chip.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
                            }
                            // Group add
                            Cal_chipGroup.addView(chip);
                            // cal_chip 인스턴스 close 버튼 클릭 리스너
                            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    inbun_textview.setVisibility(View.GONE);
                                    inbun_chipGroup.clearDisappearingChildren();
                                    System.out.println(v.getId() + "이넘아이디");
                                    Cal_chipGroup.removeView(v);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
    // Load Camera Function(Request code = 101)
    public void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            photofile = null;
            try{photofile = createImageFile();}
            catch (IOException e){}
            if(photofile != null){
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.gui.fileprovider", photofile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 101);
            }
        }
    }
    // Load Album Function(Request code = 102)
    public void takeAlbum() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 102);
    }
    private File createImageFile() throws IOException {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String imageFileName = simpleDate.format(mDate);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    // Absolute Path
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    // Picture Save Function
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 101 = camera
        if(requestCode==101 && resultCode==RESULT_OK){
            Cal_chipGroup.removeAllViews();
            time_chipGroup.clearCheck();
            inbun_textview.setVisibility(View.GONE);
            inbun_chipGroup.setVisibility(View.GONE);
            total_textview.setVisibility(View.GONE);
            File file = new File(mCurrentPhotoPath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                if (bitmap != null) {
                    connectServer();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 102 = Album
        else if(requestCode == 102 && resultCode == RESULT_OK){
            Cal_chipGroup.removeAllViews();
            time_chipGroup.clearCheck();
            inbun_textview.setVisibility(View.GONE);
            inbun_chipGroup.setVisibility(View.GONE);
            total_textview.setVisibility(View.GONE);
            Uri uri = data.getData();
            mCurrentPhotoPath = getPath(this, uri);
            File file = new File(mCurrentPhotoPath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                if (bitmap != null) {
                    connectServer();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Server Function
    void connectServer(){
        String portNumber = "5000";
        String postUrl= "http://"+"58.224.125.166"+":"+portNumber+"/model";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        // UI setting
        Food_imgview.setImageBitmap(bitmap);
        Food_imgview.setColorFilter(null);

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();
        progressDialog.show();
        postRequest(postUrl, postBodyImage);
    }
    void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                runOnUiThread(new Runnable() {
                    // Server Connect Fail
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    // Server Connect Success
                    @Override
                    public void run() {
                        try{
                            String result;
                            total_cal = 0;
                            // result = received Data
                            result = response.body().string();
                            System.out.println("받아온 결과: " + result);
                            if(result.equals("")) {
                                new AlertDialog.Builder(ResultActivity.this)
                                        .setMessage("음식을 탐지하지 못했습니다")
                                        .setPositiveButton("확인", null)
                                        .show();
                                Food_imgview.setImageResource(R.drawable.camera);
                                Food_imgview.setColorFilter(Color.parseColor("#FFFFFF"));
                            }
                            else{
                                result_foodTextview.setText("음식\n클릭해서 음식을 추가해보세요");
                                // result = "불고기-1-335@삼겹살-1-330"
                                // data[0] = "불고기-1-335" data[1] = 삼겹살-1-330
                                String data[] = result.split("@");
                                for (int i = 0; i < data.length; i++) {
                                    // str split
                                    String split[] = data[i].split("-");
                                    // total cal add
                                    total_cal += Double.parseDouble(split[1]) * Double.parseDouble(split[2]);
                                    // foodList add data
                                    foodList.add(data[i]);
                                    // Cal_ChipGroup Setting
                                    Chip chip = new Chip(ResultActivity.this);
                                    chip.setCheckable(true);
                                    // close Icon Setting
                                    chip.setCloseIconVisible(true);
                                    // Text UI Setting
                                    chip.setText(split[0]);
                                    chip.setTextColor(Color.parseColor("#FFFFFF"));
                                    // BG UI Setting
                                    chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#14151E")));
                                    chip.setTextColor(Color.WHITE);
                                    // Stroke UI Setting
                                    chip.setChipStrokeWidth(3);
                                    chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#8F8FA1")));
                                    // Cancel Ui Setting
                                    chip.getCloseIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        chip.getCheckedIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
                                    }
                                    // Group add
                                    Cal_chipGroup.addView(chip);
                                    // cal_chip 인스턴스 close 버튼 클릭 리스너
                                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            inbun_textview.setVisibility(View.GONE);
                                            inbun_chipGroup.clearDisappearingChildren();
                                            System.out.println(v.getId() + "이넘아이디");
                                            Cal_chipGroup.removeView(v);
                                        }
                                    });
                                    // Cal_chipgroup Setting End
                                }
                                // Total Cal textview
                                total_textview.setVisibility(View.VISIBLE);
                                total_textview.setText("총 섭취 칼로리: " + (int) total_cal + "Kcal");
                            }
                            // Loading Dialog close
                            progressDialog.dismiss();
                        }catch (IOException e){
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        });
    }
    // DB Update Function
    void dbUpdate(String tableName, String Day, Integer cal, int check) {
        ContentValues contentValues = new ContentValues();
        String str = null;
        // EditText Checked
        if (contentEditText.getText().toString().length() != 0)
            str = contentEditText.getText().toString();
        // Food Text
        String foodName = "";
        for(int i = 0; i < foodList.size(); i++){
            String tmp = (String) foodList.get(i);
            String data[] = tmp.split("-");
            if(i == foodList.size() - 1 )
                foodName += data[0];
            else
                foodName += data[0] + ", ";
        }
        // Switch BreakFast Lunch Dinner
        switch(check){
            // Breakfast
            case 0:
                contentValues.put("BREAKFAST", cal);
                contentValues.put("BREAKFASTIMG", mCurrentPhotoPath);
                contentValues.put("BREAKFASTCONTENT", str);
                contentValues.put("BREAKFASTNAME", foodName);
                break;
            // Lunch
            case 1:
                contentValues.put("LUNCH", cal);
                contentValues.put("LUNCHIMG", mCurrentPhotoPath);
                contentValues.put("LUNCHCONTENT", str);
                contentValues.put("LUNCHNAME", foodName);
                break;
            // Dinner
            case 2:
                contentValues.put("DINNER", cal);
                contentValues.put("DINNERIMG", mCurrentPhotoPath);
                contentValues.put("DINNERCONTENT", str);
                contentValues.put("DINNERNAME", foodName);
                break;
        }

        String dayArr[] = {Day};
        db.update(tableName, contentValues, "DAY = ?", dayArr);

    }
    // DB Search Function
    void dbSearch(String tableName, int checked){
        Cursor cursor = null;
        try{
            cursor = db.query(tableName, null, null, null, null, null, null);
            cursor.moveToLast();
            switch(checked){
                // breakfast
                case 0:
                    if(cursor.getString(cursor.getColumnIndex("BREAKFASTIMG")) != null){
                        new AlertDialog.Builder(ResultActivity.this)
                                .setMessage("아침에 찍은 음식 사진이 존재합니다. 새로 만드시겠습니까? 취소하시겠습니까?")
                                .setPositiveButton("취소하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        breakfast_chip.setChecked(false);
                                    }
                                })
                                .setNegativeButton("새로 만들기", null)
                                .show();
                    }
                    break;
                // lunch
                case 1:
                    if(cursor.getString(cursor.getColumnIndex("LUNCHIMG")) != null){
                        new AlertDialog.Builder(ResultActivity.this)
                                .setMessage("점심에 찍은 음식 사진이 존재합니다. 새로 만드시겠습니까? 취소하시겠습니까?")
                                .setNegativeButton("새로 만들기", null)
                                .setPositiveButton("취소하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        lunch_chip.setChecked(false);
                                    }
                                })
                                .show();
                    }
                    break;
                // dinner
                case 2:
                    if(cursor.getString(cursor.getColumnIndex("DINNERIMG")) != null){
                        new AlertDialog.Builder(ResultActivity.this)
                                .setMessage("저녁에 찍은 음식 사진이 존재합니다. 새로 만드시겠습니까? 취소하시겠습니까?")
                                .setNegativeButton("새로 만들기", null)
                                .setPositiveButton("취소하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dinner_chip.setChecked(false);
                                    }
                                })
                                .show();
                    }
                    break;
            }
        }catch (Exception e){

        }
    }
}
