package com.best.grocery.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.database.DatabaseHelper;
import com.best.grocery.entity.Category;
import com.best.grocery.service.CategoryService;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;
import java.util.Date;

public class MasterListDetailItemFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = MasterListDetailItemFragment.class.getSimpleName();
    private ImageView mImageBackScreen;
    private Button mButtonSave;
    private EditText mEditName;
    private Spinner mSpinner;
    private ImageView mImageCreateCategory;
    private String mText;
    private ProductService mProductService;
    private CategoryService mCategoryService;
    private ArrayList<String> mListCategory;

    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_list_detail_item, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductService = new ProductService(getContext());
        mCategoryService = new CategoryService(getContext());
        initViews();
        setOnListener();
        hideSoftKeyBoard();
    }

    private void setOnListener() {
        mImageBackScreen.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);
        mImageCreateCategory.setOnClickListener(this);
        mEditName.setKeyListener(null);

    }

    private void initViews() {
        mImageBackScreen = getView().findViewById(R.id.image_back_srceen);
        mButtonSave = getView().findViewById(R.id.button_save);
        mEditName = getView().findViewById(R.id.text_name);
        mEditName.setText(mText);
        mSpinner = getView().findViewById(R.id.spinner);
        mImageCreateCategory = getView().findViewById(R.id.image_create_category);
        setSpinnerCategory();


    }


    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setSpinnerCategory() {
        mListCategory = new ArrayList<>();
        ArrayList<Category> categoriesObject = mCategoryService.getAllCategory();
        for (Category category : categoriesObject) {
            mListCategory.add(category.getName());
        }
        mListCategory.add(getResources().getString(R.string.default_other_category));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mListCategory);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerArrayAdapter);
        Category category = mCategoryService.getCategoryOfNameProduct(mText);
        int positionCategory = spinnerArrayAdapter.getPosition(category.getName());
        Log.d("AAA", "pos:" + positionCategory + "name_category: " + category.getName());
        mSpinner.setSelection(positionCategory);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back_srceen:
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new MasterListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.button_save:
                saveData();
                break;
            case R.id.image_create_category:
                addNewCategory();
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

    private void saveData() {

        String category = mSpinner.getSelectedItem().toString();
        Category categoryObject = mCategoryService.getCategoryByName(category);
        mProductService.changeItemMasterList(mText, categoryObject);
    }


    private void addNewCategory() {
        final View alertView;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        alertView = inflater.inflate(R.layout.view_dialog_create, null);
        TextView titleView = alertView.findViewById(R.id.dialog_create_title);
        titleView.setText((getResources().getString(R.string.dialog_message_create_category)));
        builder.setView(alertView);
        builder.setPositiveButton(getResources().getString(R.string.abc_create), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EditText editView = alertView.findViewById(R.id.dialog_create_content);
                String name = editView.getText().toString().trim();
                if (!createCategory(name)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                } else {
                    mListCategory.add(name);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mListCategory);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinner.setAdapter(spinnerArrayAdapter);
                    int positionCategory = spinnerArrayAdapter.getPosition(name);
                    mSpinner.setSelection(positionCategory);
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.abc_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean createCategory(String name) {
        Log.d(TAG, "Name category" + name);
        String nameCompare = name.toLowerCase();
        ArrayList<Category> list = mCategoryService.getAllCategory();
        for (Category category : list) {
            if (nameCompare.equals(category.getName().toLowerCase()) || nameCompare.equals(getResources().getString(R.string.default_other_category).toLowerCase())) {
                return false;
            }
        }
        int orderLast = -1;
        if (list.size() != 0) {
            Category categoryLast = list.get(list.size() - 1);
            orderLast = categoryLast.getOrderView();
        }
        Category category = new Category();
        category.setOrderView(orderLast + 1);
        category.setName(name);
        category.setId(mCategoryService.createCodeId(name, new Date()));
        return DatabaseHelper.mCategoryDao.createCategory(category);
    }


    public void setmText(String mText) {
        this.mText = mText;
    }
}
