# cordova-plugin-mobpay

MobPaySDK 的 Cordova 版本插件。
目前聚合了 微信支付 |支付宝(alipay) |银联支付。
为了集成 MobPaySDK ，您首先需要到 Mob官网注册并且创建应用，申请的appkey与appsecret


# 安装

~~~
cordova plugin add https://github.com/ChandlerVer5/cordova-plugin-mobpay.git  --variable MOB_APPKEY=xxx --variable MOB_APPSECRET=xxx
~~~

测试的key 和secret：
~~~
  --variable MOB_APPKEY=moba6b6c6d6 --variable MOB_APPSECRET=b89d2427a3bc7ad1aea1e1e8c1d36bf3
~~~


# 注意

通过gradle构建，需要先在 platform > android 下的build.gradle 中设置：
~~~
buildscript {
    repositories {
        jcenter()
    }
 
    dependencies {
        ...
        classpath 'com.mob.sdk:MobSDK:+'  //找到该位置，添加该行即可
 
    }
}
~~~

然后再进行 `cordova build android` 。


# 使用
(如果需要测试付款 设置 amount:10.0，即可0.10元)
~~~
payBtn.addEventListener('click', function (e) {
    cordova.plugins.MobPay.pay({
        payChannel:2,
        orderNo:1002399234992394,
        amount:10.0,  //0.10yuan
        title:'测试支付',
        text:'这让他们很成功~'
    },function(res){
        alert("成功:"+JSON.stringify(res));
    },function(err){
        //取消和其他错误会在这里抛出~
        alert("出错："+JSON.stringify(err));
    });
    return true;
}, false);
~~~


# TODO
1. 沙箱环境配置设置选项
