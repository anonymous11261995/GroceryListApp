package com.best.grocery.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.ListProductSimpleAdapter;
import com.best.grocery.entity.Product;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class ShareProductFragment extends Fragment implements DefinitionSchema, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = ShareProductFragment.class.getSimpleName();

    private ImageView mButtonBack;
    private ImageView mMenu;
    private RecyclerView mRecyclerView;
    private Button mButtonSharing;
    private ArrayList<Product> mData;
    private ListProductSimpleAdapter mAdapter;

    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_product, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initRecyclerView();
        setOnListener();
        hideSoftKeyBoard();
    }

    private void initViews() {
        mButtonBack = getView().findViewById(R.id.share_product_button_back);
        mMenu = getView().findViewById(R.id.share_product_menu);
        mRecyclerView = getView().findViewById(R.id.share_product_recycler_view);
        mButtonSharing = getView().findViewById(R.id.share_product_action_share);

    }

    private void initRecyclerView() {
        Log.d(TAG, " " + mData.size());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ListProductSimpleAdapter(getActivity(), getContext(), mData);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 1);
    }

    private void setOnListener() {
        mButtonBack.setOnClickListener(this);
        mMenu.setOnClickListener(this);
        mButtonSharing.setOnClickListener(this);
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
        int id = v.getId();
        switch (id) {
            case R.id.share_product_button_back:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.share_product_menu:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                PopupMenu popup = new PopupMenu(v.getContext(), mMenu);
                popup.inflate(R.menu.share_product);
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            case R.id.share_product_action_share:
                doShare();
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
        this.mData = data;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                mAdapter.selectAll();
                return true;
            case R.id.action_unselect_all:
                mAdapter.unSelectAll();
                return true;
            default:
                return true;
        }
    }

    private void doShare() {
        ProductService service = new ProductService(getContext());
        String text = service.listToTextShare(mAdapter.getDataChecked());
        Log.d(TAG, "Text share: " + text);
        if (text == null) {
            Toast.makeText(getContext(), getString(R.string.toast_no_item_selected), Toast.LENGTH_LONG).show();
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.abc_share_via)));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activeFragment(new ShoppingListFragment());
                }
            }, AppConfig.DELAY_EFFECT);
        }

    }
}
