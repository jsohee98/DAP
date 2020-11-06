from django.shortcuts import get_object_or_404
from rest_framework import viewsets, routers
from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import *
from .serializers import UserSerializer, DepressionTestSerializer, TestListSerializer, WordTestSerializer, TemperamentPersonalityDescSerializer, PatternPersonalityDescSerializer, FigureTestSerializer
from rest_framework import status
from .models import UserInfo, DepressionTest, TestList, WordTest, TemperamentPersonalityDesc, PatternPersonalityDesc, FigureTest
from django.utils.datastructures import MultiValueDictKeyError
import datetime
from rest_framework.renderers import JSONRenderer
import dap_api.recommandSystem as rsys
import dap_api.detectColor as dc
import dap_api.shape_classification as cnn
import csv
import cv2
import boto3
import botocore
from PIL import Image
import os

#model learning

# if not os.path.exists('primaryModel.h5'):
#     cnn.primaryModel()
# else:
#     model = load_model('primaryModel.h5')
#     cnn.modelFitting(model)

# if not os.path.exists('patternModel.h5'):
#     cnn.patternModel()
# else:
#     pattern_model = load_model('patternModel.h5')
#     cnn.patternModelFitting(pattern_model)

# if not os.path.exists('secondModel.h5'):
#     cnn.secondModel()
# else:
#     second_model = load_model('secondModel.h5')
#     cnn.secondModelFitting(second_model)

# if not os.path.exists('concentrationSecondTemperamentModel.h5'):
#     cnn.secondModel()
# else:
#     concentration_second_model = load_model('concentrationSecondTemperamentModel.h5')
#     cnn.concentrationModelFitting(concentration_second_model)

class TestListViewSet(viewsets.ModelViewSet):
    queryset = TestList.objects.all()
    serializer_class = TestListSerializer

    @action(detail=False, methods=['post'])
    def add_test_result(self, request):
        data = {
            "testId": request.POST["testId"],
            "userId": request.POST["userId"],
            "testCode": request.POST["testCode"],
            "testDate": datetime.datetime.strptime(request.POST["testDate"], "%Y-%m-%d").date()
        }
        testlistSerializer = TestListSerializer(data=data)
        if testlistSerializer.is_valid():
            test_list = testlistSerializer.save()
            if(request.POST["testCode"] == 'FT'):
                result = testSave(request)
                return Response({"testResult": result}, status=status.HTTP_201_CREATED)
            else:
                testSave(request)
                return Response(testlistSerializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response(testlistSerializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, **kwargs):
        if kwargs.get('testId') is None:
            return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)
        else:
            test_id = kwargs.get('testId')
            test_list = TestList.objects.get(testId=test_id)
            test_list.delete()
            return Response("Successful Delete", status=status.HTTP_200_OK)

class DepressionTestViewSet(viewsets.ModelViewSet):
    queryset = DepressionTest.objects.all()
    serializer_class = DepressionTestSerializer

class WordTestViewSet(viewsets.ModelViewSet):
    queryset = WordTest.objects.all()
    serializer_class = WordTestSerializer

class UserViewSet(viewsets.ModelViewSet):
    queryset = UserInfo.objects.all()
    serializer_class = UserSerializer

    def post(self, request):
        print(request)
        userSerializer = UserSerializer(data=request.data)
        print(request.data)
        if userSerializer.is_valid():
            print("post")
            userSerializer.save()
            return Response(userSerializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response(userSerializer.errors, status=status.HTTP_400_BAD_REQUEST)

    #GET /user
    #GET /user/{userId}
    def get(self, request, **kwargs):
        print("get 실행")
        if kwargs.get('userId') is None: #모든 User정보
            userQueryset = UserInfo.objects.all()
            userQueryset_serializer = UserSerializer(userQueryset, many = True)
            return Response(userQueryset_serializer.data, status=status.HTTP_200_OK)
        else: #특정 id 요청
            user_id = kwargs.get('userId')
            user_serializer = UserSerializer(UserInfo.objects.get(userId=user_id))
            return Response(user_serializer.data, status.HTTP_200_OK)

    #PUT /user/{userId}
    def put(self, request,**kwargs):
        if kwargs.get('userId'):
            if kwargs.get('userId') is None:
                return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)
            else:
                user_id = kwargs.get('userId')
                user_object = UserInfo.objects.get(userId= user_id)

                update_user_serializer = UserSerializer(user_object, data = request.data)
                if update_user_serializer.is_valid():
                    update_user_serializer.save()
                    return Response(update_user_serializer.data, status=status.HTTP_200_OK)
                else:
                    return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)

    #DELETE /user/{userId}
    def delete(self, request, **kwargs):
        if kwargs.get('userId') is None:
            return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)
        else:
            user_id = kwargs.get('userId')
            user_object = UserInfo.objects.get(userId=user_id)
            user_object.delete()
            return Response("Successful Delete", status=status.HTTP_200_OK)

@api_view(['POST'])
def applogin(request):
    print('check')
    received_id = request.POST['userId']
    received_pwd = request.POST['password']

    valid_id=None
    valid_id = UserInfo.objects.get(pk=received_id)
    print('check')
    if not(valid_id==None):
        valid_pwd = valid_id.password
        if(received_pwd == valid_pwd):
            print("로그인 성공")
            return Response({'messsage':True}, status=status.HTTP_200_OK)
        else:
            print("비밀번호 불일치")
            return Response({'message':False}, status=status.HTTP_400_BAD_REQUEST)
    else:
        return Response(status=status.HTTP_100_CONTINUE)

@api_view(['GET'])
def idcheck(request):
    id=request.GET.get('userId')
    check = None
    try:
        check=UserInfo.objects.get(pk=id)
    except:
        print("중복 없음")
    if not check:
        return Response({"message":True}, status=status.HTTP_200_OK)
    else:
        return Response({"message":False}, status=status.HTTP_302_FOUND)


@api_view(['POST'])
def usertestlist(request):
    print("ok")
    user_id  = request.POST['userId']
    testlist=TestList.objects.filter(userId=user_id)
    serializer = TestListSerializer(testlist, many=True)
    print(serializer.data)
    return Response(serializer.data)

@api_view(['POST'])
def senderPoint(request):
    test_Id = request.POST['testId']
    id_obj = DepressionTest.objects.get(testId=test_Id)
    serializer = DepressionTestSerializer(id_obj)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST'])
def senderWTResult(request):
    test_id = request.POST['testId']
    id_obj = WordTest.objects.get(testId=test_id)
    serializer = WordTestSerializer(id_obj)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST'])
def getRecommandParameter(request):
    rsys.init()
    feedback_point = 5
    emotion_point = request.POST["emotion_point"]
    user_id = request.POST["userId"]

    depression_point = getDepressionPoint(user_id)
        
    # 추천시스템 얻기
    prediction = rsys.extractPred(emotion_point, depression_point, feedback_point)
    print(prediction)
    if(prediction == 0):
        prediction = 'FT'
    elif(prediction == 1):
        prediction = 'DT'
    elif(prediction == 2 ):
        prediction = 'WT' 
    return Response({"prediction": [prediction],
                     "depression_point": depression_point}, status=status.HTTP_200_OK)

@api_view(['POST'])
def addRecommandData(request):
    emotion_point = request.POST['emotion_point']
    user_id = request.POST['userId']
    feedback_point = request.POST['feedback_point']
    testCode = request.POST['testCode']
    print(testCode)
    depression_point = getDepressionPoint(user_id)

    if(testCode == 'FT'): testCode = 0
    elif(testCode == 'DT'): testCode = 1
    elif(testCode == 'WT'): testCode = 2
    with open('/Users/goyulim/Desktop/main/DAP-Server/dap_api/df.csv','a+', encoding='utf-8') as f:
        f.seek(0)
        data = f.read(100)
        if len(data) > 0:
            f.write("\n")
        f.writelines([str(emotion_point)+",",str(depression_point)+",",str(feedback_point)+",", str(testCode)])
    return Response({"resutl": "ok"},status=status.HTTP_200_OK)
      
def testSave(request):
    if request.POST["testCode"] == "DT":
        test_list = TestList.objects.get(testId=request.POST["testId"])
        DepressionTest.objects.create(
            testId=test_list,
            testPoint=request.POST["testPoint"]
        )
    elif request.POST["testCode"] == "WT":
        test_list = TestList.objects.get(testId=request.POST["testId"])
        WordTest.objects.create(
            testId=test_list,
            testResult=request.POST["testResult"]
        )
    elif request.POST["testCode"] == "FT":
        path = "/Users/goyulim/Desktop/main/DAP-Server/dap_api/IMGS"
        if not os.path.exists(path):
            os.makedirs(path)
        test_list = TestList.objects.get(testId=request.POST["testId"])
        img_file = request.FILES.get('image')
        if img_file != None:
            im = Image.open(img_file)
            im = im.save(path+"/"+request.POST['userId']+"testImage.jpg")
        # s3 업로드 코드 추가
        for bucket in s3.buckets.all():
            print(bucket.name)

        s3 = boto3.client('s3')

        #s3에 파일 업로드
        filefrom = "C:\\test.txt" #업로드할 파일 위치 
        filename = "test.txt" #업로드할 파일 이름 (s3에 저장될 이름)
        bucket_name = "dap-s3"
        s3.upload_file(filefrom, bucket_name, filename)

        #s3에서 파일 다운로드
        # downfilename = “”  #다운받을 파일 이름 (s3에 저장된 이름)
        # downto = “” #(파일을 다운받을 위치)

        try: 
            s3.Bucket(bucket_name).download_file(downfilename, downto) 
        except botocore.exceptions.ClientError as e: 
            if e.response['Error']['Code'] == "404": 
                print("The object does not exist.") 
            else: 
                raise
        
        #primary_temperament, second_temperament, figure_pattern = figureClassification(path+"/"+request.POST['userId']+"testImage.jpg")
        # test result description 찾기
        temperaments = figureClassification(path+"/"+request.POST['userId']+"testImage.jpg") 
        desc = TemperamentPersonalityDesc.objects.filter(primaryTemperament=temperaments['primary'], secondaryTemperament=temperaments['secondary']) + PatternPersonalityDesc.objects.get(sixPattern=temperments['pattern'])
        # FT db에 검사 코드(testId), 결과 디스크립션 저장 testId = request.POST['testId']
        FigureTest.objects.create(
            testId = test_list,
            testImage = path,
            testDescription = desc,
            primaryTemperament = temperaments['primary'],
            secondaryTemperament = temperaments['secondary'],
            sixPattern = temperaments['pattern'],
        )

# exception 처리 추가
def figureClassification(path):
    # 컬러 분류된 이미지 path 리스트
    try:
        img_path = dc.detectColor(path)
        print(img_path)
    except :
        print("cv2 색상 분류 exception 발생")
        pass

    primary_temperament = cnn.predict('primary', img_path)
    figure_pattern = cnn.predict('pattern', img_path)
    if figure_pattern == 'concentration':
        second_temperament = cnn.predict('concentrationSecondTemperament', path) 
    else:
        second_temperament = cnn.predict('second', path)
    return {'primary':primary_temperament, 'secondary':second_temperament, 'pattern':figure_pattern}

def getDepressionPoint(user_id):
    d_test = TestList.objects.filter(userId=user_id, testCode='DT').order_by('testDate').last()
    if not d_test:
        return 0
    else:
        testlist_serializer = TestListSerializer(d_test)
        # 고객의 id와 일치하는 최신 depression test의 id값
        if testlist_serializer.is_valid:
            data = testlist_serializer.data
            depression_obj = DepressionTest.objects.get(testId=data.get('testId'))
            depression_serializer = DepressionTestSerializer(depression_obj)
            depression_data = depression_serializer.data
            return depression_data.get('testPoint')
            
        else:
            return 0