package com.best.grocery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.ManageListShoppingAdapter;
import com.best.grocery.adapter.ShoppingSnoozeAdapter;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.ProductService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class ShoppingSnoozeFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private LinearLayout mCannel;
    private Button mDone;

    private ShoppingList mShoppingList;
    private ShoppingListService mShoppingListService;
    private ShoppingSnoozeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_snooze, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShoppingListService = new ShoppingListService(getContext());
        initViews();
        setOnListener();
        initRecyclerView();
        hideSoftKeyBoard();

    }

    private void initViews() {
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        mCannel = getView().findViewById(R.id.layout_cancel);
        mDone = getView().findViewById(R.id.button_done);
    }

    private void setOnListener() {
        mCannel.setOnClickListener(this);
        mDone.setOnClickListener(this);

    }

    private void initRecyclerView() {
        ArrayList<Product> data = mShoppingListService.productsSnooze(mShoppingList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ShoppingSnoozeAdapter(getContext(), data);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
            case R.id.button_done:
                ArrayList<Product> data = mAdapter.getDataChecked();
                ProductService productService = new ProductService(getContext());
                for(Product product: data){
                    product.setHide(false);
                    productService.updateProduct(product);
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

    public void setShoppingList(ShoppingList shoppingList) {
        this.mShoppingList = shoppingList;
    }
}
