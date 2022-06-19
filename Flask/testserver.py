from flask import Flask, jsonify, request
import werkzeug
from Sql import schema
from gevent.pywsgi import WSGIServer

app = Flask(__name__)


@app.route('/model', methods = ['GET', 'POST'])
def handle_request():
    imagefile = request.files['image']
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    print("\nReceived image File name : " + imagefile.filename)
    print("실행")
    imagefile.save(filename)
    result = food_search('불고기@')
    return result

@app.route('/autofood', methods = ['GET', 'POST'])
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
            tmp += j['name'] + '-' + str(processWord.count(i)) + '-' + str(j['Kcal']) + '@'
    print(tmp)
    return tmp



app.run(host="0.0.0.0", port=5000, debug=False)