package com.example.administrator.xiudoufang.setting;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.administrator.xiudoufang.R;
import com.example.administrator.xiudoufang.base.IActivityBase;
import com.example.administrator.xiudoufang.bean.LoginStore;
import com.example.administrator.xiudoufang.common.utils.LogUtils;
import com.example.administrator.xiudoufang.common.utils.PreferencesUtils;
import com.example.administrator.xiudoufang.common.utils.StringUtils;
import com.example.administrator.xiudoufang.common.widget.LoadingViewDialog;
import com.example.administrator.xiudoufang.login.logic.LoginLogic;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;

public class StoreSwitchActivity extends AppCompatActivity implements IActivityBase {

    private RecyclerView mRecyclerView;

    private int mIndex;
    private ArrayList<LoginStore> mStoreList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_store_switch;
    }

    @Override
    public void initView() {
        setTitle("店铺切换");
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    public void initData() {
        mStoreList = getIntent().getParcelableArrayListExtra("store_list");
        mIndex = getIntent().getIntExtra("index", 0);
        StoreSwitchAdapter adapter = new StoreSwitchAdapter(R.layout.layout_list_item_store_switch, mStoreList);
        adapter.bindToRecyclerView(mRecyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mIndex != position) {
                    mStoreList.get(mIndex).setSelected(false);
                    mStoreList.get(position).setSelected(true);
                    adapter.notifyItemChanged(mIndex);
                    adapter.notifyItemChanged(position);
                    mIndex = position;
                }
            }
        });
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getIntExtra("index", 0) != mIndex) {
            LoadingViewDialog.getInstance().show(this);
            LoginLogic logic = new LoginLogic();
            String username = PreferencesUtils.getPreferences().getString(PreferencesUtils.USER_NAME, "");
            String password = PreferencesUtils.getPreferences().getString(PreferencesUtils.PASSWORD, "");
            logic.requestLogin(this, username, password, mStoreList.get(mIndex).getId(), "", "1", new StringCallback() {

                @Override
                public void onSuccess(Response<String> response) {
                    StringUtils.cacheLoginInfo(response.body(), StringUtils.LOGIN_INFO);
                    LoadingViewDialog.getInstance().dismiss();
                }
            });
            setResult(Activity.RESULT_OK, new Intent().putExtra("index", mIndex));
        }
        super.onBackPressed();
    }
}