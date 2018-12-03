package com.best.grocery.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.best.grocery.AdsManager;
import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.MultiListShoppingAdapter;
import com.best.grocery.adapter.QuickSetQtyAdapter;
import com.best.grocery.entity.Product;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class QuickSetQtyFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = QuickSetQtyFragment.class.getSimpleName();
    private LinearLayout mCancel;
    private Button mSave;
    private ArrayList<Product> mData = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private QuickSetQtyAdapter mAdapter;
    private ProductService mService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quick_set_qty, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService = new ProductService(getContext());
        initViews();
        initRecyclerView();
        setOnListener();
        adsManager();

    }

    private void initViews() {
        mCancel = getView().findViewById(R.id.layout_cancel);
        mSave = getView().findViewById(R.id.button_save);
        mRecyclerView = getView().findViewById(R.id.recycler_view);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new QuickSetQtyAdapter(getContext(), mData);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 1);

    }

    private void setOnListener() {
        mCancel.setOnClickListener(this);
        mSave.setOnClickListener(this);

    }

    private void adsManager() {
        AdsManager adsManager = new AdsManager(getContext(), getActivity());
        adsManager.adsShoppingList();
    }


    @Override
    public void onClick(View v) {
        Handler handler = new Handler();
        switch (v.getId()) {
            case R.id.layout_cancel:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.button_save:
                ArrayList<Product> data = mAdapter.getData();
                for(Product product: data){
                    if(!TextUtils.isEmpty(product.getName())){
                        mService.updateProduct(product);
                    }
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            default:
                break;
        }

    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setData(ArrayList<Product> data) {
        this.mData.addAll(data);
    }
}
