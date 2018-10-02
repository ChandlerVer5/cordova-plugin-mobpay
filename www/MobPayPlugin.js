var pluginName = "MobPayPlugin";


var MobPay = {
};


MobPay.pay = function (params, successCallback, errorCallback) {
    var result = undefined;
    if(Array.isArray(params)){
        result = params;
    }else{
        result = [params]
    }
    cordova.exec(successCallback, errorCallback, pluginName, "pay", result);
};



module.exports = MobPay;

