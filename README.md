# Yolov4(Tensorflow 2.x) with colab, Android, Flask
 Yolov4는 학습은 다크넷 프레임워크를 이용, weight파일을 pb파일로 변환.
 Tensorflow로 모델 구축 후 Flask와 연결.
 Android가 HTTP Protocol을 이용해 Flask(RestFul API)에게 요청.
 Flask는 요청을 받고 모델을 실행 후 결과값을 리턴.
## Yolov4
### Colab
[![colab](https://user-images.githubusercontent.com/4096485/86174097-b56b9000-bb29-11ea-9240-c17f6bacfc34.png)](https://colab.research.google.com/drive/1EAPtYsWcZl_akRj2xQNtndiLd3_eCFA6?usp=sharing)
<br>여기서 Train Code를 볼 수 있습니다.
### Code (Flask\yolov4\Model.py)
이 코드는 Weight 파일을 pb로 변환하는 코드입니다.<br>
참고주소 - https://machinelearningmastery.com/how-to-perform-object-detection-with-yolov3-in-keras/ <br>
```python
weight_reader = WeightReader('weightFileName')
weight_reader.load_weights(model)
model.save('model/food_v2_4.h5')
```
## Flask Server
 Flask에 모델을 장착
### Code (Flask\Flask.py)
```python
@app.route('/model', methods = ['GET', 'POST'])
def handle_request():
    # Recive Img
    imagefile = request.files['image']
    filename = datetime.today().strftime("%Y%m%d%H%M%S") + '.jpg'
    imagefile.save(filename)
    # Call Model
    Model_Path = './yolov4/model/food_v1.h5'
    Class_Path = './yolov4/model/foodv1_class.txt'
    Image_Path = filename
    temp = ""
    # Model Result
    result_str = detect(Model_Path, Class_Path, Image_Path)
    temp = food_search(result_str)

    print(temp)
    return temp
```
## Android
### Connect API Server(ResultActivity.java)
```java
  String portNumber = "5000";
  String postUrl= "http://ipaddress:5000/model";
  // 보낼 이미지를 처리
  RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();
  // 통신 제한시간 지정
  OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
  // postBodyImage를 넣은 뒤
  Request request = new Request.Builder()
                .url(postUrl)
                .post(postBodyImage)
                .build();
  // 통신 시작    
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
                    public void run() {}
                });
            }
        });
```
### AutoComplteTextView
Okhttp3는 Thread안에서 통신하기 때문에 runOnThreadUi를 안할 시 Ui 변경될 때 에러가 나서 꼭 해줘야 합니다. <br>
데이터출처 - https://www.foodsafetykorea.go.kr/fcdb/
```java
private void makeApiCall(String text) {
        String portNumber = "5000";
        String postUrl = "http://" + IpAddress + ":" + portNumber + "/autofood";
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
```

## 프로젝트에 참가한 사람
- [@HYB0321](https://github.com/HYB0321)
- [@koasy17](https://github.com/koasy17)
- [@tmddmddnjs](https://github.com/tmddmddnjs)
- [@heohyunjun](https://github.com/heohyunjun)
