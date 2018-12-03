package com.best.grocery.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.database.DatabaseHelper;
import com.best.grocery.entity.Category;
import com.best.grocery.entity.Product;
import com.best.grocery.service.CategoryService;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.utils.PrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by TienTruong on 7/19/2018.
 */

@SuppressWarnings({"ConstantConditions"})
public class ProductDetailFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = ProductDetailFragment.class.getSimpleName();
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();
    private EditText mProductName;
    private EditText mProductQuantity;
    private ImageButton mQuantityIncrement;
    private ImageButton mQuantityDecrement;
    private AutoCompleteTextView mAutoCompleteTextView;
    private Spinner mProductCategory;
    private ImageView mAddNewCategory;
    private EditText mProductNotes;
    private EditText mProductUnitPrice;
    private LinearLayout mLayoutLink;
    private EditText mProductLink;
    private ImageView mDeleteLink;
    private ImageView mRedirectLink;
    private TextView mTextCurrency;
    private LinearLayout mCannel;
    private Button mSave;
    private Button mDelete;
    private Product mProduct;
    private ProductService mProductService;
    private CategoryService mCategoryService;
    private ArrayList<String> mListCategory;
    private PrefManager mPrefManager;
    private ArrayList<String> mAtocompleteList = new ArrayList<>();

    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductService = new ProductService(getContext());
        mCategoryService = new CategoryService(getContext());
        mPrefManager = new PrefManager(getContext());
        initViews();
        setAutocompleteView();
        setOnListener();
        hideSoftKeyBoard();
    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.layout_cancel:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.button_save:
                saveProduct();
                //view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.product_detail_delete:
                confirmDelete();
                break;
            case R.id.product_detail_add_category:
                Log.d(TAG, "Add new category");
                addNewCategory();
                break;
            case R.id.product_quantity_decrement:
                hideSoftKeyBoard();
                int quantityDecrement = 1;
                if (!mProductQuantity.getText().toString().equals("")) {
                    quantityDecrement = Integer.valueOf(mProductQuantity.getText().toString());
                }
                if (quantityDecrement != 1) {
                    quantityDecrement--;
                }
                mProductQuantity.setText(String.valueOf(quantityDecrement));
                Log.d(TAG, "Decrement quantity");
                break;
            case R.id.product_quantity_increment:
                hideSoftKeyBoard();
                int quantityIncrement = 1;
                if (!mProductQuantity.getText().toString().equals("")) {
                    quantityIncrement = Integer.valueOf(mProductQuantity.getText().toString());
                }
                quantityIncrement++;
                mProductQuantity.setText(String.valueOf(quantityIncrement));
                Log.d(TAG, "Increment quantity");
                break;
            case R.id.product_detail_delete_link:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getResources().getString(R.string.dialog_message_confirm_delete_srcurl));
                builder.setPositiveButton(getResources().getString(R.string.abc_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mProductLink.setText("");
                        mProduct.setUrl("");
                        saveProduct();
                        initViews();

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
                break;
            case R.id.product_detail_redirect_link:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.d(TAG, "url: " + mProduct.getUrl());
                intent.setData(Uri.parse(mProduct.getUrl().trim()));
                startActivity(intent);
                break;
            case R.id.product_detail_category:
                hideSoftKeyBoard();
                break;
            default:
                break;

        }

    }

    private void initViews() {
        Log.d(TAG, "product_name: " + mProduct.getName());
        mCannel = getView().findViewById(R.id.layout_cancel);
        mProductName = getView().findViewById(R.id.product_detail_name);
        mProductQuantity = getView().findViewById(R.id.product_detail_quantity);
        mAddNewCategory = getView().findViewById(R.id.product_detail_add_category);
        mProductNotes = getView().findViewById(R.id.product_detail_note);
        mProductCategory = getView().findViewById(R.id.product_detail_category);
        mAutoCompleteTextView = getView().findViewById(R.id.product_detail_unit);
        mAutoCompleteTextView.setText(mProduct.getUnit());
        mSave = getView().findViewById(R.id.button_save);
        mDelete = getView().findViewById(R.id.product_detail_delete);
        mProductQuantity.setText(String.valueOf(mProduct.getQuantity()));
        mProductName.setText(mProduct.getName());
        mQuantityIncrement = getView().findViewById(R.id.product_quantity_increment);
        mQuantityDecrement = getView().findViewById(R.id.product_quantity_decrement);
        if (mProduct.getNote() != null) {
            mProductNotes.setText(mProduct.getNote());
        }
        mProductUnitPrice = getView().findViewById(R.id.product_detail_unit_price);
        if (mProduct.getUnitPrice() != 0.0) {
            mProductUnitPrice.setText(String.valueOf(mProduct.getUnitPrice()));
        }
        mLayoutLink = getView().findViewById(R.id.product_detail_layout_src_link);
        mProductLink = getView().findViewById(R.id.product_detail_link_text);
        mProductLink.setText(mProduct.getUrl());
        mDeleteLink = getView().findViewById(R.id.product_detail_delete_link);
        mRedirectLink = getView().findViewById(R.id.product_detail_redirect_link);
        String srcurl = mProduct.getUrl();
        if (srcurl == null) srcurl = "";
        if (srcurl.equals("")) {
            mLayoutLink.setVisibility(View.GONE);
        } else {
            mLayoutLink.setVisibility(View.VISIBLE);
        }
        setSpinnerCategory();
        mTextCurrency = getView().findViewById(R.id.product_detail_text_currency);
        mTextCurrency.setText(CURRENCY_DEFUALT);

    }

    private void setAutocompleteView() {
        String[] systermUnit = getContext().getResources().getStringArray(R.array.ingredient_unit);
        mAtocompleteList = new ArrayList<>(Arrays.asList(systermUnit));
        ArrayList<String> userUnit = new ArrayList<>(getUserUnit());
        mAtocompleteList.addAll(userUnit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item_layout, mAtocompleteList);
        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.setThreshold(1);
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
        mProductCategory.setAdapter(spinnerArrayAdapter);
        Category category = mProductService.getCategoryOfProduct(mProduct);
        int positionCategory = spinnerArrayAdapter.getPosition(category.getName());
        mProductCategory.setSelection(positionCategory);
    }

    private void setOnListener() {
        mCannel.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mAddNewCategory.setOnClickListener(this);
        mQuantityDecrement.setOnClickListener(this);
        mQuantityIncrement.setOnClickListener(this);
        mRedirectLink.setOnClickListener(this);
        mDeleteLink.setOnClickListener(this);

    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void saveProduct() {
        String unit = mAutoCompleteTextView.getText().toString().toLowerCase().trim();
        mProduct.setUnit(unit);
        if (!unit.equals("") && !mAtocompleteList.contains(unit)) {
            Set<String> set = new HashSet<>();
            ArrayList<String> data = new ArrayList<>(getUserUnit());
            set.addAll(data);
            set.add(unit);
            mPrefManager.putStringSet(SHARE_PREFERENCES_UNIT_PRODUCT, set);
        }
        if (!mProductQuantity.getText().toString().equals("")) {
            mProduct.setQuantity(Integer.valueOf(String.valueOf(mProductQuantity.getText())));
        } else {
            mProduct.setQuantity(1);
        }
        mProduct.setName(String.valueOf(mProductName.getText()));
        mProduct.setNote(String.valueOf(mProductNotes.getText()));
        mProduct.setUrl(String.valueOf(mProductLink.getText()));
        if (!String.valueOf(mProductUnitPrice.getText()).equals(""))
            mProduct.setUnitPrice(Double.valueOf(String.valueOf(mProductUnitPrice.getText())));
        else {
            mProduct.setUnitPrice(0.0);
        }
        String category = mProductCategory.getSelectedItem().toString();
        Category categoryObject = mCategoryService.getCategoryByName(category);
        mProduct.setCategory(categoryObject);
        mProduct.setModified(new Date());
        Log.d(TAG, "unit: " + mAutoCompleteTextView.getText().toString() + " unitPrice: " + mProduct.getUnitPrice() + " quantity: " + mProduct.getQuantity());
        DatabaseHelper.mProductDao.update(mProduct);
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.abc_delete));
        builder.setMessage(getResources().getString(R.string.dialog_message_confirm_delete_item));
        builder.setPositiveButton(getResources().getString(R.string.abc_delete), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                mProductService.deleteProductFromList(mProduct);
                activeFragment(new ShoppingListFragment());
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


    public void setProduct(Product product) {
        this.mProduct = product;
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
                    mProductCategory.setAdapter(spinnerArrayAdapter);
                    int positionCategory = spinnerArrayAdapter.getPosition(name);
                    mProductCategory.setSelection(positionCategory);
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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

    ArrayList<String> getUserUnit() {
        ArrayList<String> data = new ArrayList<>();
        Set<String> strings = mPrefManager.getStringSet(SHARE_PREFERENCES_UNIT_PRODUCT, new HashSet<String>());
        data.addAll(strings);
        return data;
    }


}
