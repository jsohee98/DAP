from django.contrib import admin
from .models import UserInfo

class UserAdmin(admin.ModelAdmin):
    list_display = ('userId', 'password', 'email',' birth', 'gender',)

admin.site.register(UserInfo)