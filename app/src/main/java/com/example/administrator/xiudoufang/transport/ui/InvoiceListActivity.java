package com.example.administrator.xiudoufang.transport.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.administrator.xiudoufang.R;
import com.example.administrator.xiudoufang.base.IActivityBase;
import com.example.administrator.xiudoufang.bean.InvoiceFilter;
import com.example.administrator.xiudoufang.bean.InvoiceListBean;
import com.example.administrator.xiudoufang.common.callback.JsonCallback;
import com.example.administrator.xiudoufang.common.utils.PreferencesUtils;
import com.example.administrator.xiudoufang.common.utils.StringUtils;
import com.example.administrator.xiudoufang.common.widget.LoadingViewDialog;
import com.example.administrator.xiudoufang.transport.adapter.InvoiceListAdapter;
import com.example.administrator.xiudoufang.transport.logic.InvoiceListLogic;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvoiceListActivity extends AppCompatActivity implements IActivityBase {

    private final int COUNT = 20;
    private static final int RESULT_FOR_INVOICE_QUERY = 116;
    public static final String SELECTED_ITEM = "selected_item";

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private InvoiceListLogic mLogic;
    private List<InvoiceListBean.InvoiceBean> mList;
    private HashMap<String, String> mParams;
    private int mCurrentPage = 1;
    private InvoiceListAdapter mAdapter;
    private InvoiceFilter mFilter;

    public static void start(Context context) {
        Intent intent = new Intent(context, InvoiceListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_invoice_list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FOR_INVOICE_QUERY && data != null) {
            mFilter = data.getParcelableExtra(InvoiceQueryActivity.INVOICE_FILTER);
            if (!TextUtils.isEmpty(mFilter.getNo()))
                mParams.put("id", mFilter.getNo());
            mParams.put("searchitem", mFilter.getCustomer());
            mParams.put("starttime", mFilter.getStartTime());
            mParams.put("endtime", mFilter.getEndTime());
            mParams.put("yunhao", mFilter.getTransportNum());
            LoadingViewDialog.getInstance().show(this);
            loadInvoiceList(true);
        }
    }

    @Override
    public void initView() {
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    public void initData() {
        mLogic = new InvoiceListLogic();
        mAdapter = new InvoiceListAdapter(R.layout.layout_list_item_invoice_list, mList);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new InnerItemClickListener());
        mRefreshLayout.setOnRefreshListener(new InnerRefreshListener());
        mRefreshLayout.setOnLoadMoreListener(new InnerLoadMoreListener());
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        LoadingViewDialog.getInstance().show(this);
        loadInvoiceList(true);
    }

    private void loadInvoiceList(final boolean isRefresh) {
        if (isRefresh) mCurrentPage = 1;
        if (mParams == null) {
            mParams = new HashMap<>();
            mParams.put("dianid", PreferencesUtils.getPreferences().getString(PreferencesUtils.DIAN_ID, ""));
            mParams.put("userid", PreferencesUtils.getPreferences().getString(PreferencesUtils.USER_ID, ""));
            mParams.put("c_id", "0");
            mParams.put("id", "0");
            mParams.put("searchitem", "");
            mParams.put("starttime", StringUtils.getCurrentTime());
            mParams.put("endtime", StringUtils.getCurrentTime());
            mParams.put("yunhao", "");
            mParams.put("count", String.valueOf(COUNT));
        }
        mParams.put("pagenum", String.valueOf(mCurrentPage++));
        mLogic.requestInvoiceList(mParams, new JsonCallback<InvoiceListBean>() {
            @Override
            public void onSuccess(Response<InvoiceListBean> response) {
                LoadingViewDialog.getInstance().dismiss();
                List<InvoiceListBean.InvoiceBean> temp = response.body().getFx_fahuodanlist();
                if (isRefresh) {
                    mList.clear();
                    mList.addAll(temp);
                    mAdapter.setNewData(mList);
                    mRefreshLayout.finishRefresh();
                    mRefreshLayout.setNoMoreData(mList.size() < COUNT);
                } else {
                    mList.addAll(temp);
                    mAdapter.addData(temp);
                    if (temp.size() < COUNT) {
                        mRefreshLayout.finishLoadMoreWithNoMoreData();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transport_num, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_filter:
                Intent intent = new Intent(InvoiceListActivity.this, InvoiceQueryActivity.class);
                intent.putExtra(InvoiceQueryActivity.INVOICE_FILTER, mFilter);
                startActivityForResult(intent, RESULT_FOR_INVOICE_QUERY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class InnerItemClickListener implements BaseQuickAdapter.OnItemClickListener {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            Intent intent = new Intent(InvoiceListActivity.this, InvoiceDetailsActivity.class);
            intent.putExtra(SELECTED_ITEM, mList.get(position));
            startActivity(intent);
        }
    }

    private class InnerRefreshListener implements OnRefreshListener {

        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mRefreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadInvoiceList(true);
                }
            }, 2000);
        }
    }

    private class InnerLoadMoreListener implements OnLoadMoreListener {

        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            mRefreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadInvoiceList(false);
                }
            }, 2000);
        }
    }
}