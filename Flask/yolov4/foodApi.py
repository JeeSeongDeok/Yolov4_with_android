from bs4 import BeautifulSoup
import requests
# reut : 모델 결과값 (ex: "불고기 김치 김치")



def cnt(reut):
    key = '0635f786f2df4f25ad6e'
    url = 'http://openapi.foodsafetykorea.go.kr/api/0635f786f2df4f25ad6e/I2790/xml/1/1/DESC_KOR='
    if(reut == ''):
        return '음식을 탐지하지 못했습니다'
    pre_name = reut.split('@')
    # 빈칸제거
    pre_name = list(filter(None, pre_name))
    name, cnt, kcal = [], [], []

    # 음식 갯수,이름
    for i in set(pre_name):
        food_cnt = pre_name.count(i)
        cnt.append(food_cnt)
        name.append(i)

    # 음식 리스트 항목으로 API 칼로리 검색
    for a in name:
        api_url  =url +a
        req = requests.get(api_url)
        soup = BeautifulSoup(req.text, 'html.parser')
        for a in soup.select("NUTR_CONT1"):
            kcal.append(a.text)
    temp = ""
    # 출력
    for a,b,c in zip(name,cnt,kcal):
        print(a,b,c ,sep="-")
        temp += a + "-" + str(b) + "-" + str(c) + "@"
    return temp
if __name__ == '__main__':
    cnt('불고기@')
