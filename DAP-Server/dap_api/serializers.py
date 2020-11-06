from rest_framework import serializers
from .models import UserInfo, DepressionTest, TestList, WordTest, TestRecommend, TemperamentPersonalityDesc, PatternPersonalityDesc, FigureTest

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserInfo
        fields = '__all__'

class TestListSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestList
        fields = '__all__'

class TemperamentPersonalityDescSerializer(serializers.ModelSerializer):
    class Meta:
        model = TemperamentPersonalityDesc
        fields = '__all__'

class PatternPersonalityDescSerializer(serializers.ModelSerializer):
    class Meta:
        model = PatternPersonalityDesc
        fields = '__all__'

class FigureTestSerializer(serializers.ModelSerializer):
    class Meta:
        model = FigureTest
        fields = '__all__'

class DepressionTestSerializer(serializers.ModelSerializer):
    class Meta:
        model = DepressionTest
        fields = '__all__'

class WordTestSerializer(serializers.ModelSerializer):
    class Meta:
        model = WordTest
        fields = '__all__'
class TestRecommendSerializer(serializers.ModelSerializer):
    class Meta:
        model = TestRecommend
        fields = "__all__"