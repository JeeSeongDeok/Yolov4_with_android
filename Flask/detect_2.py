import colorsys
from random import random

import tensorflow as tf
from tensorflow.python.saved_model import tag_constants
import cv2
import numpy as np

MODEL_PATH =''
IOU_THRESHOLD =0.45
SCORE_THRESHOLD =0.25
INPUT_SIZE =416

#load model
saved_model_loaded = tf.saved_model.load('./model', tags=[tag_constants.SERVING])
infer = saved_model_loaded.signatures['serving_default']

def draw_bbox(image, bboxes, classes, show_label=True):
    num_classes = len(classes)
    image_h, image_w, _ = image.shape
    hsv_tuples = [(1.0 * x / num_classes, 1., 1.) for x in range(num_classes)]
    colors = list(map(lambda x: colorsys.hsv_to_rgb(*x), hsv_tuples))
    colors = list(map(lambda x: (int(x[0] * 255), int(x[1] * 255), int(x[2] * 255)), colors))

    random.seed(0)
    random.shuffle(colors)
    random.seed(None)

    out_boxes, out_scores, out_classes, num_boxes = bboxes
    for i in range(num_boxes[0]):
        if int(out_classes[0][i]) < 0 or int(out_classes[0][i]) > num_classes: continue
        coor = out_boxes[0][i]
        coor[0] = int(coor[0] * image_h)
        coor[2] = int(coor[2] * image_h)
        coor[1] = int(coor[1] * image_w)
        coor[3] = int(coor[3] * image_w)

        fontScale = 0.5
        score = out_scores[0][i]
        class_ind = int(out_classes[0][i])
        bbox_color = colors[class_ind]
        bbox_thick = int(0.6 * (image_h + image_w) / 600)
        c1, c2 = (coor[1], coor[0]), (coor[3], coor[2])
        cv2.rectangle(image, c1, c2, bbox_color, bbox_thick)

        if show_label:
            bbox_mess = '%s: %.2f' % (classes[class_ind], score)
            t_size = cv2.getTextSize(bbox_mess, 0, fontScale, thickness=bbox_thick // 2)[0]
            c3 = (c1[0] + t_size[0], c1[1] - t_size[1] - 3)
            cv2.rectangle(image, c1, (np.float32(c3[0]), np.float32(c3[1])), bbox_color, -1) #filled

            cv2.putText(image, bbox_mess, (c1[0], np.float32(c1[1] - 2)), cv2.FONT_HERSHEY_SIMPLEX,
                        fontScale, (0, 0, 0), bbox_thick // 2, lineType=cv2.LINE_AA)
    return image
# 이미지 전처리
def main(img_path):
    img =cv2.imread(img_path)  # 이미지로드
    img =cv2.cvtColor(img,cv2.COLOR_BGR2RGB) # cv2.cvtColor()로 BGR을 RGB로 변환

    img_input=cv2.resize(img,(INPUT_SIZE,INPUT_SIZE)) # 이미지 리사이징
    img_input = img_input / 255. # preprocessing
    img_input = img_input[np.newaxis, ...].astype(np.float32)
    img_input = tf.constant(img_input) # np.array를 tensor로 변환

    pred_bbox =infer(img_input)  # infer()로 로드한 모델에 img_input 넣기

    for key, value in pred_bbox.items():
        boxes = value[:,:,0:4]
        pred_conf = value[:,:,4:]

    boxes,scores,classes ,valid_detections =tf.image.combined_non_max_suppression(
        boxes = tf.reshape(boxes,(tf.shape(boxes)[0],-1,1,4)),
        scores = tf.reshape(
            pred_conf,(tf.shape(pred_conf)[0],-1,tf.shape(pred_conf)[-1])
        ),
        max_output_size_per_class = 50,
        max_total_size=50,
        iou_threshold=IOU_THRESHOLD,
        score_threshold =SCORE_THRESHOLD

    )
    pred_bbox =[boxes.numpy(),scores.numpy(),classes.numpy(),valid_detections.numpy()]
    result = draw_bbox(img,pred_bbox)  # 바운딩박스 그리기

    result = cv2.cvtColor(np.array(result),cv2.COLOR_RGB2BGR)
    cv2.imwrite('result.png',result)

if __name__ == '__main__':
    img_path ='test.jpg'
    main(img_path)
