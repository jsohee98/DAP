from django.db import models

# Create your models here.
class UserInfo(models.Model):
    userId = models.CharField(max_length=10,primary_key=True)
    password = models.CharField(max_length=10)
    email = models.EmailField(unique=True)
    gender = models.IntegerField()
    birth = models.CharField(max_length=10)

    class Meta:
        db_table = "UserInfo"

#Create test list 
class TestList(models.Model):
    testId = models.CharField(primary_key=True, max_length=255)
    userId = models.ForeignKey(UserInfo, on_delete=models.CASCADE)
    testCode = models.CharField(max_length=224)
    testDate = models.DateField()

    class Meta:
        db_table = "TestList"

#Create Temperament Personality desc's table
class TemperamentPersonalityDesc(models.Model):
    primaryTemperament=models.CharField(max_length=224, null=True)
    secondaryTemperament=models.CharField(max_length=224, null=True)
    description=models.TextField(null=True)

    class Meta:
        db_table = "TemperamentPersonalityDesc"

#Create Pattern Personality desc's table
class PatternPersonalityDesc(models.Model):
    sixPattern = models.CharField(max_length=224, null=True)
    description = models.TextField(null=True)

    class Meta:
        db_table = "PatternPersonalityDesc"

#Create Test1's table
class FigureTest(models.Model):
    testId=models.ForeignKey(TestList, max_length=224, primary_key=True, on_delete=models.CASCADE)
    testImage=models.CharField(max_length=224)
    primaryTemperament=models.ForeignKey(TemperamentPersonalityDesc, max_length=224, on_delete=models.CASCADE)
    secondaryTemperament=models.ForeignKey(TemperamentPersonalityDesc, max_length=224, on_delete=models.CASCADE)
    sixPattern=models.ForeignKey(PatternPersonalityDesc, max_length=224, on_delete=models.CASCADE)

    class Meta:
        db_table = "FigureTest"

#Create Test3's table
class DepressionTest(models.Model):
    testId = models.ForeignKey(TestList, max_length=224, primary_key=True, on_delete=models.CASCADE)
    testPoint= models.IntegerField()

    class Meta:
        db_table = "DepressionTest"

#Create Test2's table
class WordTest(models.Model):
    textId = models.ForeignKey(TestList, max_length=224, primary_key=True, on_delete=models.CASCADE)
    testResult = models.CharField(max_length = 224)

    class Meta:
        db_table = "WordTest"

#Create RecommendTest score table
class TestRecommend(models.Model):
    emotionPoint = models.IntegerField()
    depressionPoint = models.IntegerField()
    TestCode = models.CharField(max_length=244)
    FeedbackPoint = models.CharField(max_length = 244)

    class Meta:
        db_table = "TestRecommend"