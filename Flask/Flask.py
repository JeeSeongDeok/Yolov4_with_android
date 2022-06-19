from flask import Flask, jsonify, request
from sqlalchemy import create_engine, text
from yolov4.detect import *
from yolov4.foodApi import *
from datetime import datetime
from Sql import schema
import tensorflow as tf

app = Flask(__name__)
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
    # API Server
    #temp = cnt(result_str)

    print(temp)
    return temp
@app.route('/autofood', methods = ['POST'])
def autofood():
    word = request.form['word']
    db_class = schema.Database()
    sql = "select name, Kcal from food where name like '" + str(word) + "%%' order by name limit 5"
    print("MYSQL: "+sql)
    row = db_class.executeAll(sql)
    print(row)
    tmp = ""
    for i in row:
        tmp += i['name'] + '   ' + i['Kcal'] + 'Kcal@'
    print(tmp)
    return tmp

def food_search(word):
    processWord = word.split('@')
    processWord = list(filter(None, processWord))
    print(processWord)
    db_class = schema.Database()
    tmp = ""
    for i in set(processWord):
        sql = "select name, Kcal from food where name = '" + i + "'"
        row = db_class.executeAll(sql)
        for j in row:
            Kcal = str(j['Kcal'])
            indexKcal = Kcal.find('.')
            tmp += j['name'] + '-' + str(processWord.count(i)) + '-' + str(Kcal[:indexKcal]) + '@'
    print(tmp)
    return tmp

def gpuSet():
    gpus = tf.config.experimental.list_physical_devices('GPU')
    if gpus:
        try:
            # Currently, memory growth needs to be the same across GPUs
            for gpu in gpus:
                tf.config.experimental.set_memory_growth(gpu, True)
            logical_gpus = tf.config.experimental.list_logical_devices('GPU')
            print(len(gpus), "Physical GPUs,", len(logical_gpus), "Logical GPUs")
        except RuntimeError as e:
            # Memory growth must be set before GPUs have been initialized
            print(e)

if __name__ == '__main__':
    gpuSet()
    app.run(host="0.0.0.0", port=5000, debug=True)