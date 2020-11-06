import pandas as pd
import random as rd
import numpy as np
import os
from sklearn.model_selection import train_test_split
from sklearn import linear_model
from sklearn.preprocessing import PolynomialFeatures
from sklearn.pipeline import Pipeline

Input = [('polynomial', PolynomialFeatures(degree=3)),('modal', linear_model.LinearRegression())]
pipe = Pipeline(Input)

def init():
    data = pd.read_csv('/Users/goyulim/Desktop/main/DAP-Server/dap_api/df.csv')

    X = pd.DataFrame(data[['emotion_point', 'depression_point', 'feedback_point']])
    y = pd.DataFrame(data['test_code'])

    X_train, X_test, y_train, y_test = train_test_split(X,y,test_size=0.2)
    pipe.fit(X_train,y_train)
    print(pipe)


def extractPred(emotion_point, depression_point, feedback_point ):
    pdate = pd.DataFrame({
        "emotion_point": [emotion_point],
        "depression_point": [depression_point],
        "feedback_point": [feedback_point]
    })

    pred = pipe.predict(pdate)
    pred = pred.flatten()
    print(pred)
    return int(np.round(pred,2))
