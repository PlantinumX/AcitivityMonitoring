import random

import tensorflow as tf
import numpy as np

# Dictionary for classlabeling
# TODO dictionary also in java code
# TODO if classification bad insert avg as a extra row and maybe extra feature

class_labels = {"Walking": 0,
                "Jogging": 1,
                "Sitting": 2,
                "Standing": 3,
                "Upstairs": 4,
                "Downstairs": 5}
data  = []
labels = []
with open("WISDM_ar_v1.1_raw.txt","r") as fd:
    lines = fd.readlines()
    for line in lines:
        line = line.replace(";", "")
        line = line.replace("\n", "")
        line = line.split(",")
        if len(line) > 4 and line[1] and (line[-1] is not None and len(line[-1])) and (line[-2] is not None and len(line[-2])) and (
                line[-3] is not None and len(line[-3])):
            data.append([float(line[-3]),float(line[-2]),float(line[-1])])
            labels.append(class_labels[line[1]])

data = np.asarray(data)
print(data.shape)
labels = np.asarray(labels)
data = data.reshape((217294,5,3))
new_data = []
for m in data:
    #calculate average
    avg = np.mean(m,axis=0)

    #insert average as last line
    m_new = np.insert(m, -1, np.array(avg), 0)
    new_data.append(m_new)
labels = labels.reshape((217294,5))
new_labels = np.zeros((217294,6))
new_data = np.asarray(new_data)

index = 0
for label in labels:
    unique, counts = np.unique(label, return_counts=True)
    key= list(dict(zip(unique,counts)).items())[0][0]
    new_labels[index,key] = 1
    index += 1


# train just with 5000 blocks in the arff file
x_vals_train = data[0:200000, :, :] #217294,5,3
y_vals_train = new_labels[0:200000] #

x_vals_test = data[200000:-1, :, :]
y_vals_test = new_labels[200000:-1]
c = list(zip(x_vals_test,y_vals_test))

random.shuffle(c)

x_vals_test,y_vals_test = zip(*c)
x_vals_test = np.asarray(x_vals_test)
y_vals_test = np.asarray(y_vals_test)
print(x_vals_train.shape)
print(y_vals_train.shape)
print(y_vals_test.shape)
print(x_vals_test.shape)
# # We use K-NN algortihm

x_data_train = tf.placeholder(shape=[200000, 5,3], dtype=tf.float32)
y_data_train = tf.placeholder(shape=[200000, 6], dtype=tf.float32)
x_data_test = tf.placeholder(shape=[5, 3], dtype=tf.float32)
print("HELLO")
for k in range(2,60):

    # # manhattan distance
    distance = tf.sqrt(tf.reduce_sum(tf.reduce_sum(tf.square(x_data_train - x_data_test), axis=2), axis=1))
    # # # nearest k points
    _, top_k_indices = tf.nn.top_k(tf.negative(distance), k=k)
    top_k_label = tf.gather(y_data_train, top_k_indices)
    test = tf.reduce_sum(top_k_label,axis=0)
    pred = tf.argmax(test)
    init = tf.global_variables_initializer()
    # Start training
    accuracy = 0
    with tf.Session() as sess:
        sess.run(init)
        for i in range(2200):
            ind = sess.run(pred,feed_dict={x_data_train : x_vals_train,
                                                 x_data_test : x_vals_test[i,:,:],
                                                 y_data_train :  y_vals_train})


            if ind == np.argmax(y_vals_test[i]):
                accuracy += 1
        print("k is ",k , "accurace ",accuracy / 2200)

