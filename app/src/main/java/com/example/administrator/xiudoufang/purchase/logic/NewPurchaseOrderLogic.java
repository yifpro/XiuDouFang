package com.example.administrator.xiudoufang.purchase.logic;

import com.alibaba.fastjson.JSONObject;
import com.example.administrator.xiudoufang.bean.ProductListBean;
import com.example.administrator.xiudoufang.bean.SupplierListBean;
import com.example.administrator.xiudoufang.bean.WarehouseListBean;
import com.example.administrator.xiudoufang.common.callback.JsonCallback;
import com.example.administrator.xiudoufang.common.utils.LogUtils;
import com.example.administrator.xiudoufang.common.utils.PreferencesUtils;
import com.example.administrator.xiudoufang.common.utils.StringUtils;
import com.lzy.okgo.OkGo;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/8/17
 */

public class NewPurchaseOrderLogic {

    //******** 提交采购单 ********
    public void requestPostPurchaseOrder(HashMap<String, String> params, JsonCallback<String> callback) {
        String json = JSONObject.toJSONString(params);
        OkGo.<String>post(StringUtils.BASE_URL + "/Api/products/postpoorderall?postpoorder=0")
                .headers("Content-Type", "application/json")
                .upJson(json)
                .execute(callback);
    }

    //******** 获取供应商列表 ********
    public void requestSupplierList(HashMap<String, String> params, JsonCallback<SupplierListBean> callback) {
        String json = JSONObject.toJSONString(params);
        OkGo.<SupplierListBean>post(StringUtils.BASE_URL + "/Api/products/requset_suppdata?requset_suppdata=0")
                .headers("Content-Type", "application/json")
                .upJson(json)
                .execute(callback);
    }

    //******** 获取仓位列表 ********
    public void requestWarehouseList(JsonCallback<WarehouseListBean> callback) {
        HashMap<String, String> map = new HashMap<>();
        map.put("dianid", PreferencesUtils.getPreferences().getString(PreferencesUtils.DIAN_ID, ""));
        map.put("userid", PreferencesUtils.getPreferences().getString(PreferencesUtils.USER_ID, ""));
        map.put("house", "0");
        OkGo.<WarehouseListBean>get(StringUtils.getUrl("/Api/products/Gethouselists?", map))
                .execute(callback);
    }

    //******** 获取产品列表 ********
    public void requestProductList(HashMap<String, String> params, JsonCallback<ProductListBean> callback) {
        String json = JSONObject.toJSONString(params);
        OkGo.<ProductListBean>post(StringUtils.BASE_URL + "/Api/products/requset_poproductdata?requset_poproductdata=0")
                .headers("Content-Type", "application/json")
                .upJson(json)
                .execute(callback);
    }
}
