package com.wjipet.mobpay;

import android.app.Activity;
import android.widget.Toast;
import jdk.nashorn.internal.parser.JSONParser;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mob.MobSDK;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.UIHandler;

import com.mob.paysdk.MobPayAPI;
import com.mob.paysdk.OnPayListener;
import com.mob.paysdk.Order;
import com.mob.paysdk.PayOrder;
import com.mob.paysdk.PayResult;
import com.mob.paysdk.PaySDK;
import com.mob.paysdk.AliPayAPI;
import com.mob.paysdk.WXPayAPI;
import com.mob.paysdk.UnionPayAPI;

public class MobPayPlugin extends CordovaPlugin {

	private Activity activity;

	// Corodva 初始化插件
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		this.activity = cordova.getActivity();

		// ApplicationInfo appInfo =
		// getPackageManager().getApplicationInfo(getPackageName(),
		// PackageManager.GET_META_DATA);

		// preferences.getString(APP_KEY, "00");
		// preferences.getString(APP_SECRET, "");

		MobSDK.init(this.activity);
	}

	@Override
	public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
		if ("pay".equals(action)) {
			return this.pay(args, callbackContext);
		}
		return super.execute(action, args, callbackContext);
	}

	/**
	 * [发起Mob支付功能]
	 * 
	 * @param args            [支付参数]
	 * @param callbackContext [Cordova 回调上下文]
	 */
	private boolean pay(CordovaArgs args,final CallbackContext callbackContext) {
		final JSONObject data;
		final PayOrder order = new PayOrder();
		final MobPayAPI payApi;
		try {
		data = args.getJSONObject(0);
		int payChannel = Integer.parseInt(data.getString("payChannel"));
		String orderNo = data.has("orderNo") ? data.getString("orderNo") : "";
		int amount = Integer.parseInt(data.getString("amount"));
		String title = data.has("title") ? data.getString("title") : "";
		String text = data.has("text") ? data.getString("text") : "";
			// 设置支付必须
			order.setOrderNo(orderNo); // 唯一订单号
			order.setAmount(amount); // 支付金额，分
			order.setSubject(title); // 支付标题
			order.setBody(text); // 支付主体

			// 0, 支付宝; 1, 微信支付, 2 银联

			if (0 == payChannel) {
				payApi = PaySDK.createMobPayAPI(AliPayAPI.class); // 获取支付宝的支付api
			} else if (1 == payChannel) {
				payApi = PaySDK.createMobPayAPI(WXPayAPI.class); // 获取微信的支付api
			} else if (2 == payChannel) {
				payApi = PaySDK.createMobPayAPI(UnionPayAPI.class);// 获取银联支付api
			} else {
				payApi = null;
				callbackContext.error("null");
			}

			// 显示loading
			// DialogUtils.showLoading(this);

			// 发起支付
			payApi.pay(order, new OnPayListener<PayOrder>() {
				// 在拉起支付渠道前调用
				@Override
				public boolean onWillPay(String ticketId, PayOrder payOrder, MobPayAPI mobPayAPI) {
					// TODO: 保存本次支付操作的 ticketId
					// 返回false表示不阻止本次支付
					return false;
				}

				@Override
				public void onPayEnd(PayResult payResult, PayOrder payOrder, MobPayAPI mobPayAPI) {
					MobPayPlugin.this.onPayEnd(payResult, payOrder, callbackContext);
				}
			});

			return true;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 当支付结束时回调. 处理支付的结果，成功或失败可以在payResult 中获取
	 * @param order           [description]
	 * @param result          [支付结果]
	 * @param callbackContext [回调]
	 */
	private void onPayEnd(PayResult result, Order order, final CallbackContext callbackContext) {


		try {
			JSONObject r = new JSONObject();
			r.put("payAt",  System.currentTimeMillis());
			r.put("ticketId", order.getTicketId());
			r.put("Message", result.getPayMessage());

		


		// loading 取消
		// DialogUtils.hideLoading(this);

		if (PayResult.PAYCODE_OK == result.getPayCode()) {
		// 支付成功
		r.put("ok", true);
 			callbackContext.success(r);
            Toast.makeText(this.activity, "支付成功", Toast.LENGTH_SHORT).show();
		} else if (PayResult.PAYCODE_CANCEL == result.getPayCode()) {
// 支付取消
            r.put("ok", false);
 			callbackContext.error(r);
            Toast.makeText(this.activity, "支付取消", Toast.LENGTH_SHORT).show();
		} else {
// 支付失败
			r.put("ok", false);
 			callbackContext.error(r);
			Toast.makeText(this.activity, "支付失败", Toast.LENGTH_SHORT).show();
		}

		} catch (JSONException e) {
			//TODO: handle exception
			e.printStackTrace();
		}


	}

}