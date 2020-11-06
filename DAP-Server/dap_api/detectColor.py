import os
import numpy as np

import cv2

import matplotlib.pyplot as plt

import matplotlib.image as mpimg

from PIL import Image
from datetime import datetime
IMG_PATH = "/Users/goyulim/Desktop/main/DAP-Server/dap_api/IMG"
# IMG_PATH = "C:/Users/USER/Desktop/졸프"

# 색상 범위 설정
# lower_red = np.array([94, 80, 2])
# upper_red = np.array([126, 255, 255])
now_date = datetime.now()
str_date = now_date.strftime('%Y%m%d')

if not os.path.exists(IMG_PATH+"/"+str_date): 
    os.makedirs(IMG_PATH+"/"+str_date)

img_path = IMG_PATH+"/"+str_date+"/redshapes.jpg"

# lower_red = np.array([120, 80, 20])
lower_red = np.array([104, 80, 2])
upper_red = np.array([126, 255, 255])


def detectColor(aws_img_path):
    # 이미지 파일을 읽어온다
    img = mpimg.imread(aws_img_path, cv2.IMREAD_COLOR)
    # RGB to HSV 변환
    img_hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    # 색상 범위를 제한하여 mask 생성
    img_mask = cv2.inRange(img_hsv, lower_red, upper_red)
    # 원본 이미지를 가지고 색상추출 이미지로 생성
    img_result = cv2.bitwise_and(img, img, mask=img_mask)
    # 결과 이미지 생성
    imgplot = plt.imshow(img_result)
    # plt.show()
    plt.savefig(img_path)
    print(img_path)
    return img_path