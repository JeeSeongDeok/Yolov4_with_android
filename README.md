# AI 음식 탐지 다이어리

## 배경
대학교 졸업작품을 위해서 다양한 분야를 공부하는 친구들끼리 모여 프로젝트를 준비했다.
세부분야가 다양해서 많은 아이디어가 나왔는데 그 중 AI를 이용해서 음식을 탐지하고, 음식 칼로리를 알려주는 어플리케이션을 만들기로 했다.

## Development

### Android

통신결과로 음식 정보를 들고오고, 음식 정보에 맞는 UI를 추가한다.

runOnUiThread를 이용해 결과를 메인 스레드로 전달해, 화면에 반영한다.

<details>
  <summary> <h3> 눌러서 코드 보기 </h3> </summary>
<div markdown="1">

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
                    public void run() { // Do Something}
                });
            }
        });
```
  </div>
  </details>
  

Autocompletetextview는 자동완성을 도와주는 뷰이다. 3글자 이상부터 통신을 하며, runOnUiThread를 이용해 Ui를 갱신했다.

<details>
<summary> <h3> 눌러서 코드 보기 </h3> </summary>
<div markdown="1">
  
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
                                                                 
</div>
</details>

### Back-end

마이크로 프레임워크인 Flask를 사용해 가벼운 API 서버를 만들 수 있었다. 

자원을 요구하면 YOLO 모델을 로드해 결과를 리턴한다
  
<details>
<summary> <h3> 눌러서 코드 보기 </h3> </summary>
<div markdown="1">

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
</div>
</details>

### Yolov4(Tensorflow 2.x) with colab
 Yolov4는 학습은 다크넷 프레임워크를 이용, weight파일을 pb파일로 변환.
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
## Tech Stack
- Front-end
    - Java
    - Okhttp3
    - GSON
    - SQLLite
- Back-end
    - Python
    - Flask
    - YOLOv4
    - MySQL
    
## Features & Screens

### 첫 화면
![첫화면](https://user-images.githubusercontent.com/23256819/226227600-4af9def1-e00c-4c78-835b-2a003a2bd77d.png)
![2](https://user-images.githubusercontent.com/23256819/226227598-58255b50-136d-41ce-86ac-3a02b303735d.png)
 
- 사용자의 BMI를 계산하기 위해 나이 몸무게 키 성별을 요구
- 음식 사진을 직접 찍거나 앨범에서 들고가기 위해 권한을 요청

### 메인 화면
![image01](https://user-images.githubusercontent.com/23256819/226227663-1c28c9d4-673f-4b6d-bebf-856189ef84eb.png)
![image02](https://user-images.githubusercontent.com/23256819/226227664-a7b68469-b747-44c7-b1cb-af41fabfa23c.png)

1. 이전에 저장했던 정보를 찾을 수 있습니다.
2. 해당 날에 정보가 있을 경우 화면에 표시해줍니다.
3. 메인화면 이동합니다.
4. 모든 기록을 볼 수 있는 음식 일기로 이동합니다.
5. 기록을 추가할 수 있는 헬스 리포트로 이동합니다.
6. 기록을 도식화한 건강 일기로 이동합니다.
7. 개인 설정으로 이동합니다.

### 음식 일기
![image03](https://user-images.githubusercontent.com/23256819/226227764-3c27b4bb-ebc6-4cfb-9470-f897c1adfd12.png)
- 저장된 데이터를 한 눈에 볼 수 있습니다.

### 헬스 리포트

![image04](https://user-images.githubusercontent.com/23256819/226227913-3d7ff6a8-cd0f-4f55-bb80-268dc6a53869.png)
1. 음식 기록을 저장할 수 있는 화면으로 이동합니다.
2. 운동 기록을 저장할 수 있는 화면으로 이동한다.
3. 신체 기록을 저장할 수 있는 화면으로 이동한다.

### 음식 기록 추가 전 화면

![image05](https://user-images.githubusercontent.com/23256819/226227916-04ab412f-6394-4608-90a1-9cd7fd8ac0b8.png)

1. 카메라 아이콘을 클릭 시, 음식 사진을 앨범에서 불러오거나 직접 찍을 수 있는 화면으로 이동된다.
2. 이 음식 사진을 먹은 시간을 정할 수 있다.
3. 이 화면을 불러온 시간을 저장한다.
4. 이 기록에 대한 간단한 메모를 저장할 수 있다.
- 이 화면에서 내장된 사진을 불러오기 위해서 MediaStore을 사용했다.
- 아래의 안드로이드 가이드를 통해서 구현을 했었다.

[](https://developer.android.com/training/data-storage/shared/media?hl=ko)

### 음식 기록 추가 후 화면

![image06](https://user-images.githubusercontent.com/23256819/226227918-ef7da7dc-9bf8-4121-ad30-8f9b3558bf18.png)
![image07](https://user-images.githubusercontent.com/23256819/226227920-cbcc779d-a392-4029-b587-e1e4f39921a8.png)

1. 음식 탐지를 제대로하지 못한 경우 임의적으로 추가할 수 있는 창을 띄어준다.
2. 음식을 클릭 할 경우 해당되는 칼로리를 4번에서 볼 수 있다.
3. 음식을 섭취량을 정할 수 있다.
4. 선택한 음식의 칼로리 및 전체적으로 먹은 칼로리를 보여준다.
5. 모든 기록을 저장할 수 있다.

### 운동 기록 추가 화면

![image08](https://user-images.githubusercontent.com/23256819/226227923-ce62e61f-ad0b-41cd-afad-6842ad9a0b38.png)

1. 운동 제목을 정할 수 있다.
2. 운동 시작 시간을 선택할 수 있다.
3. 운동 끝난 시간을 선택할 수 있다.
4. 2번 3번이 입력이 되면 계산을 통해서 자동으로 총 운동시간을 구할 수 있다.
5. 기록을 저장할 수 있다.

### 신체 기록 추가 화면

![image09](https://user-images.githubusercontent.com/23256819/226227926-8cca3f1c-628e-4f00-a41a-33e7a3e12577.png)

1. 체중 정보를 입력할 수 있다.
2. 체지방 정보를 입력할 수 있다.
3. 골격근량 정보를 입력할 수 있다.
4. 모든 정보를 저장할 수 있다.


### 건강 일기
![image10](https://user-images.githubusercontent.com/23256819/226227929-b5c5dc7e-e0a1-4aee-9e42-7f43bc33149f.png)

건강일기는 하루, 일주일, 한달동안 먹은 칼로리 및 몸무게 변화를 그래프로 볼 수 있다.

1. 단위(하루, 일주일, 한달)를 바꿔주는 버튼이다.

![image11](https://user-images.githubusercontent.com/23256819/226227933-5dfdfe0b-cb73-499c-a387-9d184a955154.png)


![image12](https://user-images.githubusercontent.com/23256819/226227935-d05f25de-31e7-4d82-a368-011bfdd4a2d0.png)

1. 몸무게 변화를 단위별로 오른쪽 사진같이 그래프로 볼 수 있다.
2. 하루동안 섭취한 칼로리들을 단위별로 오른쪽 사진같이 그래프로 볼 수 있다.

### 설정 화면

![image13](https://user-images.githubusercontent.com/23256819/226227936-8fc12f0d-a3e1-45d6-ac77-60b4680362e5.png)

1. 어플리케이션을 실행하기 전 잠금화면을 먼저 보여줄 수 있도록 설정할 수 있다.
2. 달력의 시작일을 정할 수 있다.
3. 개발자에게 문의 메일을 보낼 수 있다.

## 프로젝트에 참가한 사람
- Android 담당, BackEnd 담당 [@JeeSeongDeok](https://github.com/JeeSeongDeok)
- AI 담당 [@HYB0321](https://github.com/HYB0321)
- AI 담당, Android 보조 [@koasy17](https://github.com/koasy17)
- Android 담당, AI 보조 [@tmddmddnjs](https://github.com/tmddmddnjs)
- AI 담당, BackEnd 보조 [@heohyunjun](https://github.com/heohyunjun)
