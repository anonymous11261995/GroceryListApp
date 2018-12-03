package com.best.grocery.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.CategoryAdapter;
import com.best.grocery.dialog.DialogCustomLayout;
import com.best.grocery.entity.Category;
import com.best.grocery.service.CategoryService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.helper.SwipeAndDrag;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/23/2018.
 */


public class CategoryManageFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = CategoryManageFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ImageView mBack;
    private ImageView mAdd;
    private CategoryService mCategoryService;
    private CategoryAdapter mAdapter;
    private ArrayList<Category> myDataset;
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_manage, container, false);
        mCategoryService = new CategoryService(getContext());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        setOnListener();
        initRecyclerView();
        hideSoftKeyBoard();

    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void initRecyclerView() {
        Log.d("ABCD", "Load list");
        myDataset = mCategoryService.getAllCategory();
        mRecyclerView = getView().findViewById(R.id.category_manage_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CategoryAdapter(getContext(), myDataset);
        SwipeAndDrag swipeAndDragHelper = new SwipeAndDrag(ItemTouchHelper.UP | ItemTouchHelper.DOWN, mAdapter, getContext());
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(mItemTouchHelper);
        mRecyclerView.setAdapter(mAdapter);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    private void initViews() {
        mBack = getView().findViewById(R.id.category_manage_back);
        mAdd = getView().findViewById(R.id.category_manage_add);
    }

    private void setOnListener() {
        mBack.setOnClickListener(this);
        mAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.category_manage_back:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.category_manage_add:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(getContext());
                String message = getString(R.string.dialog_message_create_category);
                dialogCustomLayout.onCreate(message,"");
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if (mCategoryService.checkBeforeUpdate(text)) {
                            Category category = mCategoryService.createCategory(text);
                            myDataset.add(category);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            hideSoftKeyBoard();
                            Toast.makeText(getActivity(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }
                    }
                });
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


}
