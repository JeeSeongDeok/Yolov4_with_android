# Load the model
import numpy as np
from tensorflow.keras.models import load_model, Model
from numpy import expand_dims
from tensorflow.keras.preprocessing.image import load_img
from tensorflow.keras.preprocessing.image import img_to_array
from tensorflow.keras.layers import Conv2D, Input, BatchNormalization, LeakyReLU, ZeroPadding2D, UpSampling2D, MaxPool2D, \
    Activation
import tensorflow as tf
from tensorflow.keras.utils import get_custom_objects
import colorsys
import random
from matplotlib import pyplot
from matplotlib.patches import Rectangle


class Mish(Activation):
    def __init__(self, activation, **kwargs):
        super(Mish, self).__init__(activation, **kwargs)
        self.__name__ = 'mish'
def mysoftplus(x):
    mask_min = tf.cast((x < -20.0), tf.float32)
    ymin = mask_min * tf.math.exp(x)

    mask_max = tf.cast((x > 20.0), tf.float32)
    ymax = mask_max * x

    mask = tf.cast((abs(x) <= 20.0), tf.float32)
    y = mask * tf.math.log(tf.math.exp(x) + 1.0)

    return (ymin + ymax + y)
def mish(x):
    return (x * tf.math.tanh(mysoftplus(x)))

def read_labels(labels_path):
    with open(labels_path, 'rt', encoding='UTF8') as f:
        labels = f.readlines()
    labels = [c.strip() for c in labels]
    return labels
# load and prepare an image
def load_image_pixels(filename, shape):
    # load the image to get its shape
    image = load_img(filename)
    width, height = image.size
    # load the image with the required size
    image = load_img(filename, interpolation='bilinear', target_size=shape)
    # convert to numpy array
    image = img_to_array(image)
    # scale pixel values to [0, 1]
    image = image.astype('float32')
    image /= 255.0
    # add a dimension so that we have one sample
    image = expand_dims(image, 0)
    return image, width, height
class BoundBox:
    def __init__(self, xmin, ymin, xmax, ymax, objness=None, classes=None):
        self.xmin = xmin
        self.ymin = ymin
        self.xmax = xmax
        self.ymax = ymax
        self.objness = objness
        self.classes = classes
        self.label = -1
        self.score = -1

    def get_label(self):
        if self.label == -1:
            self.label = np.argmax(self.classes)

        return self.label

    def get_score(self):
        if self.score == -1:
            self.score = self.classes[self.get_label()]

        return self.score


def _sigmoid(x):
    return 1. / (1. + np.exp(-x))


def decode_netout(netout, anchors, obj_thresh, net_h, net_w, nb_box, scales_x_y):
    grid_h, grid_w = netout.shape[:2]
    netout = netout.reshape((grid_h, grid_w, nb_box, -1))
    nb_class = netout.shape[-1] - 5  # 5 = bx,by,bh,bw,pc

    print("grid_h,grid_w: ", grid_h, grid_w)
    print("nb class: ", nb_class)

    boxes = []
    netout[..., :2] = _sigmoid(netout[..., :2])  # x, y
    netout[..., :2] = netout[..., :2] * scales_x_y - 0.5 * (scales_x_y - 1.0)  # scale x, y

    netout[..., 4:] = _sigmoid(netout[..., 4:])  # objectness + classes probabilities

    for i in range(grid_h * grid_w):

        row = i / grid_w
        col = i % grid_w

        for b in range(nb_box):
            # 4th element is objectness
            objectness = netout[int(row)][int(col)][b][4]

            if (objectness > obj_thresh):
                print("objectness: ", objectness)

                # first 4 elements are x, y, w, and h
                x, y, w, h = netout[int(row)][int(col)][b][:4]
                x = (col + x) / grid_w  # center position, unit: image width
                y = (row + y) / grid_h  # center position, unit: image height
                w = anchors[2 * b + 0] * np.exp(w) / net_w  # unit: image width
                h = anchors[2 * b + 1] * np.exp(h) / net_h  # unit: image height

                # last elements are class probabilities
                classes = objectness * netout[int(row)][col][b][5:]
                classes *= classes > obj_thresh
                box = BoundBox(x - w / 2, y - h / 2, x + w / 2, y + h / 2, objectness, classes)
                boxes.append(box)
    return boxes


def correct_yolo_boxes(boxes, image_h, image_w, net_h, net_w):
 new_w, new_h = net_w, net_h
 for i in range(len(boxes)):
        x_offset, x_scale = (net_w - new_w)/2./net_w, float(new_w)/net_w
        y_offset, y_scale = (net_h - new_h)/2./net_h, float(new_h)/net_h
        boxes[i].xmin = int((boxes[i].xmin - x_offset) / x_scale * image_w)
        boxes[i].xmax = int((boxes[i].xmax - x_offset) / x_scale * image_w)
        boxes[i].ymin = int((boxes[i].ymin - y_offset) / y_scale * image_h)
        boxes[i].ymax = int((boxes[i].ymax - y_offset) / y_scale * image_h)




def _interval_overlap(interval_a, interval_b):
    x1, x2 = interval_a
    x3, x4 = interval_b
    if x3 < x1:
        if x4 < x1:
            return 0
        else:
            return min(x2, x4) - x1
    else:
        if x2 < x3:
            return 0
        else:
            return min(x2, x4) - x3


def bbox_iou(box1, box2):
    intersect_w = _interval_overlap([box1.xmin, box1.xmax], [box2.xmin, box2.xmax])
    intersect_h = _interval_overlap([box1.ymin, box1.ymax], [box2.ymin, box2.ymax])
    intersect = intersect_w * intersect_h
    w1, h1 = box1.xmax - box1.xmin, box1.ymax - box1.ymin
    w2, h2 = box2.xmax - box2.xmin, box2.ymax - box2.ymin
    union = w1 * h1 + w2 * h2 - intersect
    return float(intersect) / union


def do_nms(boxes, nms_thresh):
    if len(boxes) > 0:
        nb_class = len(boxes[0].classes)
    else:
        return
    for c in range(nb_class):
        sorted_indices = np.argsort([-box.classes[c] for box in boxes])
        for i in range(len(sorted_indices)):
            index_i = sorted_indices[i]
            if boxes[index_i].classes[c] == 0: continue
            for j in range(i + 1, len(sorted_indices)):
                index_j = sorted_indices[j]
                if bbox_iou(boxes[index_i], boxes[index_j]) >= nms_thresh:
                    boxes[index_j].classes[c] = 0


def generate_colors(class_names):
    hsv_tuples = [(x / len(class_names), 1., 1.) for x in range(len(class_names))]
    colors = list(map(lambda x: colorsys.hsv_to_rgb(*x), hsv_tuples))
    colors = list(map(lambda x: (int(x[0] ), int(x[1] ), int(x[2] )), colors))
    random.seed(10101)  # Fixed seed for consistent colors across runs.
    random.shuffle(colors)  # Shuffle colors to decorrelate adjacent classes.
    random.seed(None)  # Reset seed to default.
    return colors

# get all of the results above a threshold
def get_boxes(boxes, labels, thresh, colors):
    v_boxes, v_labels, v_scores, v_colors = list(), list(), list(), list()
    # enumerate all boxes
    for box in boxes:
        # enumerate all possible labels
        for i in range(len(labels)):

            # check if the threshold for this label is high enough
            if box.classes[i] > thresh:
                v_boxes.append(box)
                v_labels.append(labels[i])
                v_scores.append(box.classes[i]*100)
                v_colors.append(colors[i])
                # don't break, many labels may trigger for one box
    return v_boxes, v_labels, v_scores, v_colors


# draw all results
def draw_boxes(filename, v_boxes, v_labels, v_scores, v_colors):
    # load the image
    data = pyplot.imread(filename)
    # plot the image
    pyplot.imshow(data)
    # get the context for drawing boxes
    ax = pyplot.gca()
    # plot each box
    for i in range(len(v_boxes)):
        box = v_boxes[i]
        # get coordinates
        y1, x1, y2, x2 = box.ymin, box.xmin, box.ymax, box.xmax
        # calculate width and height of the box
        width, height = x2 - x1, y2 - y1
        # create the shape
        rect = Rectangle((x1, y1), width, height, fill=False, color=v_colors[i])
        # draw the box
        ax.add_patch(rect)
        # draw text and score in top left corner
        label = "%s (%.3f)" % (v_labels[i], v_scores[i])
        pyplot.text(x1, y1, label, color='white')
    # show the plot
    pyplot.savefig('./result.png')



def detect(Path, classes, img):
    get_custom_objects().update({'mish': Mish(mish)})
    yolo_model = load_model(Path, custom_objects={'Mish': Mish})
    labels = read_labels(classes)
    print("nb labels: ", len(labels))
    # Pre-process the image
    input_w, input_h = 608, 608
    photo_filename = img
    image, image_w, image_h = load_image_pixels(photo_filename, (input_w, input_h))
    print("image initial size: ", image_w, image_h)
    print("input image", image.shape)
    # Run the model
    yhat = yolo_model.predict(image)
    print("output", [a.shape for a in yhat])
    # Compute the Yolo layers
    obj_thresh = 0.25
    anchors = [[12, 16, 19, 36, 40, 28], [36, 75, 76, 55, 72, 146], [142, 110, 192, 243, 459, 401]]
    scales_x_y = [1.2, 1.1, 1.05]
    boxes = list()

    for i in range(len(anchors)):
        # decode the output of the network
        boxes += decode_netout(yhat[i][0], anchors[i], obj_thresh, input_h, input_w, 3, scales_x_y[i])

    print("nb boxes detected; ", len(boxes))
    # Correct the boxes according the inital size of the image
    correct_yolo_boxes(boxes, image_h, image_w, input_h, input_w)
    # Suppress the non Maximal boxes
    do_nms(boxes, 0.5)
    print("nb boxes remaining; ", len(boxes))
    # Get the details of the detected objects for a threshold > 0.6
    class_threshold = 0.4
    colors = generate_colors(labels)
    v_boxes, v_labels, v_scores, v_colors = get_boxes(boxes, labels, class_threshold, colors)
    print("nb boxes remaining; ", len(v_boxes))
    tmp = ""
    for i in range(len(v_labels)):
       tmp += v_labels[i] + '@'
    print(tmp)
    return tmp
    # Draw the result
    #for i in range(len(v_boxes)):
       # print(v_labels[i], v_scores[i], v_boxes[i].xmin, v_boxes[i].xmax, v_boxes[i].ymin, v_boxes[i].ymax)
    # draw what we found
    #draw_boxes(photo_filename, v_boxes, v_labels, v_scores, v_colors)


if __name__ == '__main__':
    np.warnings.filterwarnings('ignore')
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

    Path = "model/food_v1.h5"
    Classes = "model/foodv1_class.txt"
    Img = 'images/img6.jpg'
    detect(Path, Classes, Img)