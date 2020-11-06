from django.urls import *
from django.conf.urls import *
from rest_framework.routers import *
from rest_framework.urls import *
from . import views

#django rest framework -> router -> url

router = DefaultRouter()
router.register('user',views.UserViewSet)
router.register('list',views.TestListViewSet)
router.register('depressiontest',views.DepressionTestViewSet)

urlpatterns = [
    url('', include(router.urls)),
    url('api-v1/',include('rest_framework.urls', namespace='rest_framework_category')),
    path('applogin', views.applogin, name='applogin'),
    path('idcheck',views.idcheck),
    path('usertestlist', views.usertestlist, name='usertestlist'),
    path('get-dt-point',views.senderPoint),
    path('get-wt-result', views.senderWTResult),
    path('get-recommand-test',views.getRecommandParameter),
    path('add-recommand-data',views.addRecommandData),
    path('figure-shape-classification',views.figureClassification),
]