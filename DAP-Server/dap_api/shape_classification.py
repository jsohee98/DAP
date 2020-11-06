
import os
import numpy as np
import tensorflow as tf
import cv2
import math
from tensorflow.python.keras.models import Sequential
from tensorflow.python.keras.layers import Dense
from tensorflow.python.keras.layers import Flatten
from tensorflow.python.keras.layers.convolutional import Conv2D
from tensorflow.python.keras.layers.convolutional import MaxPooling2D
from tensorflow.python.keras.preprocessing.image import ImageDataGenerator
from tensorflow.python.keras.preprocessing import image
from tensorflow.python.keras.models import load_model

# 랜덤시드 고정시키기
np.random.seed(3)

DATA_PATH = '/Users/goyulim/Documents/1st_data'
# DATA_PATH = 'C:/Users/USER/Desktop/졸프'

# 1.도형분류


def primaryModel():
    print('-----primary model training-----')
    # 모델 구성
    model = Sequential()
    model.add(Conv2D(32, kernel_size=(3, 3),
                     activation='relu',
                     input_shape=(24, 24, 3)))  # 32%,
    model.add(Conv2D(64, (3, 3), activation='relu'))  # 32%, 36%
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Flatten())
    model.add(Dense(128, activation='relu'))
    model.add(Dense(4, activation='softmax'))

    # 모델 학습과정 설정
    model.compile(loss='categorical_crossentropy',
                  optimizer='adam', metrics=['accuracy'])

    modelFitting(model)


def modelFitting(model):
    fit_model = model
    train_datagen = ImageDataGenerator(rescale=1./255)
    test_datagen = ImageDataGenerator(rescale=1./255)
    # 1layer shape 데이터
    train_generator1 = train_datagen.flow_from_directory(
        DATA_PATH+'/1layershape/train',
        target_size=(24, 24),
        batch_size=25,
        class_mode='categorical')
    test_generator1 = test_datagen.flow_from_directory(
        DATA_PATH+'/1layershape/test',
        target_size=(24, 24),
        batch_size=7,
        class_mode='categorical')

    #2layer shape 이미지 데이터
    train_generator2 = train_datagen.flow_from_directory(
        DATA_PATH+'/2layershape/train',
        target_size=(24, 24),
        batch_size=35,
        class_mode='categorical')
    test_generator2 = test_datagen.flow_from_directory(
        DATA_PATH+'/2layershape/test',
        target_size=(24, 24),
        batch_size=4,
        class_mode='categorical')

    # 3layer shape 이미지 데이터
    train_generator = train_datagen.flow_from_directory(
        DATA_PATH+'/shapes1/train',
        target_size=(24, 24),
        batch_size=39,
        class_mode='categorical')
    test_generator = test_datagen.flow_from_directory(
        DATA_PATH+'/shapes1/test',
        target_size=(24, 24),
        batch_size=7,
        class_mode='categorical')

    # 모델 학습
    fit_model.fit(
        train_generator1,
        steps_per_epoch=29,
        epochs=20,
        validation_data=test_generator1,
        validation_steps=28)

    # 모델 평가
    print("-- Evaluate --")
    print('1layer shape score')
    scores = fit_model.evaluate(test_generator1, steps=5)
    print("%s: %.2f%%" % (fit_model.metrics_names[1], scores[1]*100))

    # 모델 학습
    fit_model.fit(
        train_generator2,
        steps_per_epoch=28,
        epochs=20,
        validation_data=test_generator2,
        validation_steps=31)

    # 모델 평가
    print("-- Evaluate --")
    print('2layer shape score')
    scores = fit_model.evaluate(test_generator2, steps=5)
    print("%s: %.2f%%" % (fit_model.metrics_names[1], scores[1]*100))

    fit_model.fit(
        train_generator,
        steps_per_epoch=29,
        epochs=20,
        validation_data=test_generator,
        validation_steps=29)

    # 모델 평가
    print("-- Evaluate --")
    print('3layer shape score')
    scores = fit_model.evaluate(test_generator, steps=5)
    print("%s: %.2f%%" % (fit_model.metrics_names[1], scores[1]*100))

    # 모델 저장
    fit_model.save('primaryModel.h5')

# 2. 2차기질분류


def secondModel():
    print('----second temperament model training----')
    # 모델 구성
    model = Sequential()
    model.add(Conv2D(16, kernel_size=(3, 3),
                     activation='relu',
                     input_shape=(24, 24, 3)))
    model.add(Conv2D(32, (3, 3), activation='relu'))
    model.add(Conv2D(64, (3, 3), activation='relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Flatten())
    model.add(Dense(128, activation='relu'))
    model.add(Dense(5, activation='softmax'))

    # 모델 학습과정 설정
    model.compile(loss='categorical_crossentropy',
                  optimizer='adam', metrics=['accuracy'])

    secondModelFitting(model)
    concentrationModelFitting(model)


def concentrationModelFitting(model):
    fit_model = model
    # 데이터 생성
    train_datagen = ImageDataGenerator(rescale=1./255)
    test_datagen = ImageDataGenerator(rescale=1./255)
    train_generator = train_datagen.flow_from_directory(
        DATA_PATH+'/shapes4/train',
        target_size=(24, 24),
        batch_size=20,
        class_mode='categorical')

    test_generator = test_datagen.flow_from_directory(
           DATA_PATH+'/shapes4/test',
           target_size=(24, 24),
           batch_size=5,
           class_mode='categorical')
    if not os.path.exists('concentrationSecondTemperamentModel.h5'):
        fit_model = model
    else:
        fit_model = load_model('concentrationSecondTemperamentModel.h5')

    # 모델 학습
    fit_model.fit(
        train_generator,
        steps_per_epoch=26,
        epochs=20,
        validation_data=test_generator,
        validation_steps=5)

    # 모델 평가
    print("-- Evaluate --")
    print('second temperament')
    scores = fit_model.evaluate(test_generator, steps=5)
    print("%s: %.2f%%" % (fit_model.metrics_names[1], scores[1]*100))

    # 모델 저장
    fit_model.save('concentrationSecondTemperamentModel.h5')



def secondModelFitting(model):
    fit_model = model
    # 데이터 생성
    train_datagen = ImageDataGenerator(rescale=1./255)
    test_datagen = ImageDataGenerator(rescale=1./255)
    # 데이터 생성
    train_generator = train_datagen.flow_from_directory(
            DATA_PATH+'/shapes2/train',
            target_size=(24, 24),
            batch_size=25,
            class_mode='categorical')
    test_generator = test_datagen.flow_from_directory(
            DATA_PATH+'/shapes2/test',
            target_size=(24, 24),
            batch_size=5,
            class_mode='categorical')
    if not os.path.exists('secondModel.h5'):
        fit_model = model
    else:
        fit_model = load_model('secondModel.h5')

    # 모델 학습
    fit_model.fit(
        train_generator,
        steps_per_epoch=30,
        epochs=20,
        validation_data=test_generator,
        validation_steps=26)

    # 모델 평가
    print("-- Evaluate --")
    print('second temperament')
    scores = fit_model.evaluate(test_generator, steps=5)
    print("%s: %.2f%%" % (fit_model.metrics_names[1], scores[1]*100))
    # 모델 저장
    fit_model.save('secondModel.h5')
        
# 3. 6가지패턴분류


def patternModel():
    print('----patterns model training----')
    # 모델 구성
    model = Sequential()
    model.add(Conv2D(16, kernel_size=(3, 3),
                     activation='relu',
                     input_shape=(24, 24, 3)))
    model.add(Conv2D(32, (3, 3), activation='relu'))
    model.add(Conv2D(64, (3, 3), activation='relu'))
    # model.add(Conv2D(128, (3, 3), activation='relu'))  # 36%, 26.00%, 32.00%
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Flatten())
    model.add(Dense(128, activation='relu'))
    model.add(Dense(7, activation='softmax'))

    # 모델 학습과정 설정
    model.compile(loss='categorical_crossentropy',
                  optimizer='adam', metrics=['accuracy'])

    patternModelFitting(model)


def patternModelFitting(model):
    fit_model = model
    # 데이터 생성
    train_datagen = ImageDataGenerator(rescale=1./255)
    train_generator = train_datagen.flow_from_directory(
        DATA_PATH+'/shapes3/train',
        target_size=(24, 24),
        batch_size=57,
        class_mode='categorical')

    test_datagen = ImageDataGenerator(rescale=1./255)
    test_generator = test_datagen.flow_from_directory(
        DATA_PATH+'/shapes3/test',
        target_size=(24, 24),
        batch_size=6,
        class_mode='categorical')

    # 모델 학습
    fit_model.fit(
        train_generator,
        steps_per_epoch=28,
        epochs=20,
        validation_data=test_generator,
        validation_steps=28)

    # 모델 평가
    print("-- Evaluate --")
    print('patterns score')
    scores = fit_model.evaluate(test_generator, steps=5)
    print("%s: %.2f%%" % (fit_model.metrics_names[1], scores[1]*100))

    fit_model.save('patternModel.h5')

# 모델 사용
def predict(modelName, img):
    print("— Predict —")
    model = load_model(modelName+"Model.h5")
    print("load model = "+modelName+"Model.h5")
    img = [prepare(img)]
    output = model.predict(np.array(img), batch_size=1, steps=1, verbose=1)
    print(output)
    # print(np.array(img).filenames)
    np.set_printoptions(formatter={'float': lambda x: "{0:0.1f}".format(x)})
    return getValue(modelName, output.ravel())


IMG_SIZE = 24


def prepare(file):
    img_array = cv2.imread(file, cv2.IMREAD_COLOR)
    return cv2.resize(img_array, (IMG_SIZE, IMG_SIZE))


def getValue(modelName, arr):
    primaryModelList = ['gloom', 'round', 'square', 'triangle']
    secondModelList = ['gloom', 'none', 'round', 'square', 'triangle']
    patternModelList = ['concentration', 'exploring',
                        'genius', 'none', 'overlap', 'seldom', 'undevelop']
    if modelName == 'primary':
        newlist = [arr[0], arr[1], arr[2], arr[3]]
        arrIndex = newlist.index(1)
        print(primaryModelList[arrIndex])
        return primaryModelList[arrIndex]
    if modelName == 'second' or modelName == 'concentrationSecondTemperament':
        newlist = [arr[0], arr[1], arr[2], arr[3], arr[4]]
        arrIndex = newlist.index(1)
        print(secondModelList[arrIndex])
        return secondModelList[arrIndex]
    if modelName == 'pattern':
        newlist = [arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6]]
        arrIndex = newlist.index(1)
        print(patternModelList[arrIndex])
        return patternModelList[arrIndex]
