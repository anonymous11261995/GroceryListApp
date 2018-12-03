package com.best.grocery.fragment;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.ManageListShoppingAdapter;
import com.best.grocery.dialog.DialogCustomLayout;
import com.best.grocery.entity.Category;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class ManageShoppingFragment extends Fragment implements DefinitionSchema, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ImageView mBack;
    private ImageView mAdd;
    private ArrayList<ShoppingList> mData;

    private ManageListShoppingAdapter mAdapter;
    private ShoppingListService mShoppingListService;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_shopping_list, container, false);

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
        mBack = getView().findViewById(R.id.image_back_screen);
        mAdd = getView().findViewById(R.id.image_add_new_list);

    }


    private void setOnListener() {
        mBack.setOnClickListener(this);
        mAdd.setOnClickListener(this);
    }

    private void initRecyclerView() {
        mData = mShoppingListService.getAllShoppingList();
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ManageListShoppingAdapter(getActivity(), getContext(), mData);
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
            case R.id.image_back_screen:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.image_add_new_list:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(getContext());
                String message = getString(R.string.dialog_message_create_list);
                dialogCustomLayout.onCreate(message, "");
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if (mShoppingListService.checkBeforeUpdateList(text)) {
                            mShoppingListService.createNewListShopping(text);
                            ArrayList<ShoppingList> list = mShoppingListService.getAllShoppingList();
                            mAdapter.setData(list);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            hideSoftKeyBoard();
                            Toast.makeText(getContext(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
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
