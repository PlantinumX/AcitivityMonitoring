import tensorflow as tf
import tensorflow
import numpy as np
import matplotlib.pyplot as plt
import warnings
from sklearn import datasets
from scipy.io import arff
import pandas as pd
import random

# Dictionary for classlabeling
# TODO dictionary also in java code
class_labels = {"Walking": 0,
                "Jogging": 1,
                "Sitting": 2,
                "Standing": 3,
                "Upstairs": 4,
                "Downstairs": 5}

#
# # load data
# iris = datasets.load_iris()
# x_vals = np.array([x[0:4] for x in iris.data])
# y_vals = np.array(iris.target)
#
# # one hot encoding
# y_vals = np.eye(len(set(y_vals)))[y_vals]
#
# # normalize
# x_vals = (x_vals - x_vals.min(0)) / x_vals.ptp(0)
#
# # train-test split
# np.random.seed(59)
# train_indices = np.random.choice(len(x_vals), round(len(x_vals) * 0.8), replace=False)
# test_indices =np.array(list(set(range(len(x_vals))) - set(train_indices)))
#
# x_vals_train = x_vals[train_indices]
# x_vals_test = x_vals[test_indices]
# y_vals_train = y_vals[train_indices]
# y_vals_test = y_vals[test_indices]
# print(len(y_vals[0]))

#
# load data,
raw_data = np.zeros((5418, 14, 3))
all_data = []


data = arff.loadarff('WISDM_ar_v1.1_transformed.arff')
df = pd.DataFrame(data[0])
labels = np.zeros((5418,6))
counter = 0
for one_value in df.get_values():

    tmp = np.zeros((14,3))
    for cnt_outter in range(0, 10):
        tmp[cnt_outter][0] = one_value[2 + cnt_outter]
        tmp[cnt_outter][1] = one_value[12 + cnt_outter]
        tmp[cnt_outter][2] = one_value[22 + cnt_outter]

    for cnt_outter in range(0, 4):
        tmp[cnt_outter + 10][0] = one_value[32 + (cnt_outter *3)]
        tmp[cnt_outter + 10][1] = one_value[33 + (cnt_outter *3)]
        tmp[cnt_outter + 10][2] = one_value[34 + (cnt_outter *3)]


#    raw_data.append(np.reshape(one_value[2:-2],(14,3)))
    index = class_labels[one_value[-1].decode('ascii')]
    labels[counter][index] = 1
    counter += 1
# GET OUR DATA IN RIGHT SHAPE
raw_data = np.asarray(raw_data)
print(raw_data.shape)
print(len(labels))
print(labels[0:5])
c = list(zip(raw_data, labels))
#
#random.shuffle(c)
raw_data,labels = zip(*c)
raw_data = np.asarray(raw_data)
labels = np.asarray(labels)
#
# train just with 5000 blocks in the arff file
x_vals_train = raw_data[0:5300, :, :] #5000
y_vals_train = labels[0:5300] #
# test just with  400 blocks in the arff file
x_vals_test = raw_data[5300:-1, :, :]
y_vals_test = labels[5300:-1]
print(x_vals_train.shape)
print(y_vals_train.shape)
print(x_vals_test.shape)
print(y_vals_test.shape)
# # We use K-NN algortihm
# k = 2  # look into ten neigbourse
x_data_train = tf.placeholder(shape=[5300, 14,3], dtype=tf.double)
y_data_train = tf.placeholder(shape=[5300, 6], dtype=tf.double)
x_data_test = tf.placeholder(shape=[14, 3], dtype=tf.double)
print("HELLO")
for k in range(2,60):

    # # manhattan distance
    distance = tf.sqrt(tf.reduce_sum(tf.reduce_sum(tf.square(x_data_train - x_data_test), axis=2), axis=1))
    # #
    # # # nearest k points
    _, top_k_indices = tf.nn.top_k(tf.negative(distance), k=k)
    top_k_label = tf.gather(y_data_train, top_k_indices)
    test = tf.reduce_sum(top_k_label,axis=0)
    pred = tf.argmax(test)
    prediction = tf.argmax(top_k_indices, axis=0)

    # #
    # sum_up_predictions = tf.reduce_sum(top_k_label, axis=1)
    #
    # init = tf.global_variables_initializer()
    # Start training
    accuracy = 0
    with tf.Session() as sess:
        # sess.run(init)
        for i in range(117):
            ret = sess.run(pred,feed_dict={x_data_train:x_vals_train,
                                                 x_data_test:x_vals_test[i,:,:],
                                                 y_data_train: y_vals_train})
            # print(ret.shape)
            # print(np.argmax(y_vals_test[i]))
            if ret == np.argmax(y_vals_test[i]):
                accuracy += 1
            # print(ret/6,"and ",y_vals_test[i])
        print("k is ",k , "accurace ",accuracy / 117)

        #
        # # Run the initializer
        # sess.run(init)
        #
        # # loop over test data
        # for i in range(417):
        #     # Get nearest neighbor
        #
        #     prediction_outcome = sess.run(prediction, feed_dict={x_data_train: x_vals_train,
        #                                    x_data_test: x_vals_test[i,:],
        #                                                    y_data_train:y_vals_train})
        #     # Get nearest neighbor class label and compare it to its true label
        #     print(prediction_outcome)

