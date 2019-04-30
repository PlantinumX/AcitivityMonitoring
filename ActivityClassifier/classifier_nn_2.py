
########################################################################################################################
###### This document was inspired by: https://aqibsaeed.github.io/2016-11-04-human-activity-recognition-cnn/ and    ####
###### https://aqibsaeed.github.io/2017-05-02-deploying-tensorflow-model-andorid-device-human-activity-recognition/ ####
######               Our trainingsset is from http://www.cis.fordham.edu/wisdm/dataset.php                          ####
########################################################################################################################


import pandas as pd
import numpy as np
from scipy import stats
import tensorflow as tf
from tensorflow.python.tools import freeze_graph

#TODO check what happes if we do not normalize dataset
#is it better or far worse
def read_data(file_path):
    column_names = ['user-id', 'activity', 'timestamp', 'x-axis', 'y-axis', 'z-axis']
    data = pd.read_csv(file_path, header=None, names=column_names)

    data.dropna(axis=0, how='any', inplace=True)

    return data


def windows(data, size):
    start = 0
    while start < data.count():
        yield int(start), int(start + size)
        start += (size/2)


def segment_signal(data, window_size=90):
    segments = np.zeros((0, window_size, 3))
    labels = np.zeros((0))
    for (start, end) in windows(data["timestamp"], window_size):
        x_tmp = data["x-axis"][start:end]
        y_tmp = data["y-axis"][start:end]
        z_tmp = data["z-axis"][start:end]

        if (len(dataset["timestamp"][start:end]) == window_size):
            segments = np.vstack([segments, np.dstack([x_tmp, y_tmp, z_tmp])])
            labels = np.append(labels, stats.mode(data["activity"][start:end])[0][0])

    return segments, labels


def weight_variable(shape):
    initial = tf.truncated_normal(shape, stddev=0.1)
    return tf.Variable(initial)


def bias_variable(shape):
    initial = tf.constant(0.0, shape=shape)
    return tf.Variable(initial)


def depthwise_conv2d(x, W):
    return tf.nn.depthwise_conv2d(x, W, [1, 1, 1, 1], padding='VALID')


def apply_depthwise_conv(x, kernel_size, num_channels, depth):
    weights = weight_variable([1, kernel_size, num_channels, depth])
    biases = bias_variable([depth * num_channels])
    return tf.nn.relu(tf.add(depthwise_conv2d(x, weights), biases))


def apply_max_pool(x, kernel_size, stride_size):
    return tf.nn.max_pool(x, ksize=[1, 1, kernel_size, 1], strides=[1, 1, stride_size, 1], padding='VALID')

input_height = 1
input_width = 90
num_labels = 3
num_channels = 3

batch_size = 1
kernel_size = 60
depth = 60
num_hidden = 1000

learning_rate = 0.0001
training_epochs = 1

dataset = read_data('data_v3.txt')

segments, labels = segment_signal(dataset)
labels = np.asarray(pd.get_dummies(labels), dtype = np.int8)

reshaped_segments = segments.reshape(len(segments), 1, 90, 3)

train_test_split = np.random.rand(len(reshaped_segments)) < 0.70

train_x = reshaped_segments[train_test_split]
train_y = labels[train_test_split]
test_x = reshaped_segments[~train_test_split]
test_y = labels[~train_test_split]

total_batchs = train_x.shape[0] // batch_size


X = tf.placeholder(tf.float32, shape=[batch_size, input_width * num_channels], name="input")
X_reshaped = tf.reshape(X, [-1, 1, 90, 3])
Y = tf.placeholder(tf.float32, shape=[None, num_labels])


c = apply_depthwise_conv(X_reshaped, kernel_size, num_channels, depth)
p = apply_max_pool(c, 20, 2)
c = apply_depthwise_conv(p, 3, depth*num_channels, depth//10)

shape = c.get_shape().as_list()
c_flat = tf.reshape(c, [-1, shape[1] * shape[2] * shape[3]])

f_weights_l1 = weight_variable([shape[1] * shape[2] * depth * num_channels * (depth//10), num_hidden])
f_biases_l1 = bias_variable([num_hidden])
f = tf.nn.tanh(tf.add(tf.matmul(c_flat, f_weights_l1), f_biases_l1))

out_weights = weight_variable([num_hidden, num_labels])
out_biases = bias_variable([num_labels])
y_ = tf.nn.softmax(tf.matmul(f, out_weights) + out_biases, name="y_")

loss = -tf.reduce_sum(Y * tf.log(y_))
optimizer = tf.train.AdamOptimizer(learning_rate = learning_rate).minimize(loss)

correct_prediction = tf.equal(tf.argmax(y_, 1), tf.argmax(Y, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
saver = tf.train.Saver()
with tf.Session() as session:
    tf.global_variables_initializer().run()
    for epoch in range(training_epochs):
        cost_history = np.zeros(shape=[1], dtype=float)
        for b in range(total_batchs):
            offset = (b * batch_size) % (train_y.shape[0] - batch_size)
            batch_x = train_x[offset:(offset + batch_size), :, :, :]
            batch_x = np.reshape(batch_x, (1, 270))
            batch_y = train_y[offset:(offset + batch_size), :]
            _, c = session.run([optimizer, loss], feed_dict={X: batch_x, Y: batch_y})
            cost_history = np.append(cost_history, c)
        print("Epoch: ", epoch, " Training Loss: ", np.mean(cost_history), " Training Accuracy: ",
        session.run(accuracy, feed_dict={X_reshaped: train_x, Y: train_y}))
    print ("Testing Accuracy:", session.run(accuracy, feed_dict={X_reshaped: test_x, Y: test_y}))

    tf.train.write_graph(session.graph_def, '.', './checkpoint/activity.pbtxt')
    saver.save(session, save_path="./checkpoint/activity.ckpt")


    MODEL_NAME = 'activity'

    input_graph_path = 'checkpoint/' + MODEL_NAME+'.pbtxt'
    checkpoint_path = './checkpoint/' +MODEL_NAME+'.ckpt'
    restore_op_name = "save/restore_all"
    filename_tensor_name = "save/Const:0"
    output_frozen_graph_name = 'frozen_'+MODEL_NAME+'.pb'

    freeze_graph.freeze_graph(input_graph=input_graph_path, input_saver="",
                              input_binary=False, input_checkpoint=checkpoint_path,
                              output_node_names="y_", restore_op_name="save/restore_all",
                              filename_tensor_name="save/Const:0",
                              output_graph=output_frozen_graph_name, clear_devices=True, initializer_nodes="")