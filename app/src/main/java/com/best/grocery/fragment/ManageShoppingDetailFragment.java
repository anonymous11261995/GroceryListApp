package com.best.grocery.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.ColorPicker;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;



public class ManageShoppingDetailFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private LinearLayout mCancel;
    private Button mSave;
    private EditText mEditName;
    private View mViewColor;
    private Button mChangeColor;
    private ShoppingListService mService;
    private ShoppingList mShoppingList;
    private String lastName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_detail_shopping, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService = new ShoppingListService(getContext());
        initViews();
        setOnListener();
        hideSoftKeyBoard();

    }

    private void initViews() {
        mCancel = getView().findViewById(R.id.layout_cancel);
        mSave = getView().findViewById(R.id.button_save);
        mEditName = getView().findViewById(R.id.edit_name);
        mEditName.setText(mShoppingList.getName());
        mViewColor = getView().findViewById(R.id.view_color);
        Log.d("AAAA","color: " + mShoppingList.getColor());
        if(mShoppingList.getColor() != 0){
            mViewColor.setBackgroundColor(mShoppingList.getColor());
        }
        mChangeColor = getView().findViewById(R.id.button_change_color);
    }

    private void setOnListener() {
        mCancel.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mChangeColor.setOnClickListener(this);

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
                        activeFragment(new ManageShoppingFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.button_save:
                mShoppingList.setName(mEditName.getText().toString().trim());
                if(mService.checkBeforeUpdateList(mShoppingList.getName()) || mShoppingList.getName().equals(lastName)){
                    v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mService.updateList(mShoppingList);
                            activeFragment(new ManageShoppingFragment());
                        }
                    }, AppConfig.DELAY_EFFECT);
                }
                else {
                    hideSoftKeyBoard();
                    Toast.makeText(getActivity(), getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.button_change_color:
                showChangeColor();
            default:
                break;
        }

    }

    private void showChangeColor() {
        final ColorPicker colorPicker = new ColorPicker(getActivity());
        if(mShoppingList.getColor() != 0){
            colorPicker.setDefaultColorButton(mShoppingList.getColor());
        }
        colorPicker
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        mViewColor.setBackgroundColor(color);
                        mShoppingList.setColor(color);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
//                .addListenerButton("newButton", new ColorPicker.OnButtonListener() {
//                    @Override
//                    public void onClick(View v, int position, int color) {
//                        Log.d("position", "" + position);
//                    }
//                }).show();
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.mShoppingList = shoppingList;
        this.lastName = shoppingList.getName();
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
