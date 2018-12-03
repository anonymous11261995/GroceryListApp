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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.MoveOrCopyProductAdapter;
import com.best.grocery.dialog.DialogCustomLayout;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.PantryListService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class MoveOrCopyProductFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = MoveOrCopyProductFragment.class.getSimpleName();
    private ImageView mButtonBack;
    private TextView mTextEmptyList;
    private RecyclerView mRecyclerView;
    private Spinner mSpinnerList;
    private ImageView mButtonAddNewList;
    private Button mButtonMove;
    private Button mButtonCopy;
    private MoveOrCopyProductAdapter mAdapter;
    ArrayList<Product> mData;
    private ShoppingListService mShoppingListService;
    private Handler handler = new Handler();
    public static LinearLayout mLayoutBottom;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_move_copy_products, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShoppingListService = new ShoppingListService(getContext());
        initViews();
        initRecyclerView();
        setOnListener();
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
            case R.id.image_create_new_list:
                DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(getContext());
                String message = getString(R.string.dialog_message_create_list);
                dialogCustomLayout.onCreate(message, "");
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if (mShoppingListService.checkBeforeUpdateList(text)) {
                            mShoppingListService.createNewListShopping(text);
                            initSpinner();
                        } else {
                            hideSoftKeyBoard();
                            Toast.makeText(getContext(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.button_move:
                String nameList = mSpinnerList.getSelectedItem().toString();
                ShoppingList listTarget = mShoppingListService.getListByName(nameList);
                ArrayList<Product> data = mAdapter.getDataChecked();
                mShoppingListService.moveItems(listTarget, data);
                activeFragment(new ShoppingListFragment());
                Log.d(TAG, "Move: " + data.size());
                break;
            case R.id.button_copy:
                String nameListCopy = mSpinnerList.getSelectedItem().toString();
                ShoppingList listTargetCopy = mShoppingListService.getListByName(nameListCopy);
                ArrayList<Product> dataCopy = mAdapter.getDataChecked();
                mShoppingListService.copyItems(listTargetCopy, dataCopy);
                activeFragment(new ShoppingListFragment());
                Log.d(TAG, "Copy: " + dataCopy.size());
                break;
            default:
                break;
        }

    }

    private void initViews() {
        mButtonBack = getView().findViewById(R.id.image_back_screen);
        mButtonMove = getView().findViewById(R.id.button_move);
        mButtonCopy = getView().findViewById(R.id.button_copy);
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        mSpinnerList = getView().findViewById(R.id.spinner);
        mButtonAddNewList = getView().findViewById(R.id.image_create_new_list);
        mTextEmptyList = getView().findViewById(R.id.guide_shopping_list);
        if (mData.size() != 0) {
            mTextEmptyList.setVisibility(View.GONE);
        }
        initSpinner();
        mLayoutBottom = getView().findViewById(R.id.layout_bottom);
        mLayoutBottom.setVisibility(View.GONE);
    }


    private void setOnListener() {
        mButtonBack.setOnClickListener(this);
        mButtonCopy.setOnClickListener(this);
        mButtonMove.setOnClickListener(this);
        mButtonAddNewList.setOnClickListener(this);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MoveOrCopyProductAdapter(getContext(), mData);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 1);

    }

    private void initSpinner() {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<ShoppingList> list = mShoppingListService.getAllShoppingList();
        for (ShoppingList sl : list) {
            if (sl.isActive()){
                arrayList.add(0, sl.getName());
                continue;
            }
            arrayList.add(sl.getName());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_recipe_detail, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        mSpinnerList.setAdapter(spinnerArrayAdapter);
    }


    public void setData(ArrayList<Product> data) {
        mData = new ArrayList<>(data);
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
