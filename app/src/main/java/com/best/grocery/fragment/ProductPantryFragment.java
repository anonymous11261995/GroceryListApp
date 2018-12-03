package com.best.grocery.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.database.DatabaseHelper;
import com.best.grocery.entity.Category;
import com.best.grocery.entity.Product;
import com.best.grocery.service.CategoryService;
import com.best.grocery.service.PantryListService;
import com.best.grocery.service.ProductService;
import com.best.grocery.utils.DefinitionSchema;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProductPantryFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private final String TAG = ProductPantryFragment.class.getSimpleName();
    private EditText mProductName;
    private Spinner mProductCategory;
    private ImageView mAddNewCategory;
    private EditText mProductCreated;
    private EditText mProductExpirationDate;
    private LinearLayout mLayoutExpirationDate;
    private LinearLayout mCannel;
    private Button mSave;
    private Button mDelete;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioLow;
    private RadioButton mRadioFull;
    private Product mProduct;
    private ProductService mProductService;
    private PantryListService mPantryListService;
    private CategoryService mCategoryService;
    private ArrayList<String> mListCategory;

    private SimpleDateFormat formatter = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN);
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_pantry, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPantryListService = new PantryListService(getContext());
        mCategoryService = new CategoryService(getContext());
        mProductService = new ProductService(getContext());
        initViews();
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
        DialogFragment datePickerFragment = new DatePickerFragment();
        ((DatePickerFragment) datePickerFragment).setMinDate(mProduct.getCreated().getTime());
        switch (id) {
            case R.id.layout_cancel:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new PantryListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.button_save:
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new PantryListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                saveProduct();
                break;
            case R.id.button_delete:
                confirmDelete();
                break;
            case R.id.image_add_new_category:
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                addNewCategory();
                break;
            case R.id.layout_expiration_date:
                Log.d(TAG, "Change date");
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                ((DatePickerFragment) datePickerFragment).setListener(new DatePickerFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, Date date) {
                        mProductExpirationDate.setText(formatter.format(date));
                        mProduct.setExpired(date);
                    }
                });
                break;
            case R.id.edit_expiration_date:
                Log.d(TAG, "Change date");
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                ((DatePickerFragment) datePickerFragment).setListener(new DatePickerFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, Date date) {
                        mProductExpirationDate.setText(formatter.format(date));
                        mProduct.setExpired(date);
                    }
                });
                break;
            default:
                break;

        }

    }


    private void initViews() {
        mCannel = getView().findViewById(R.id.layout_cancel);
        mProductName = getView().findViewById(R.id.edit_name);
        mAddNewCategory = getView().findViewById(R.id.image_add_new_category);
        mProductCategory = getView().findViewById(R.id.spinner_category);
        mSave = getView().findViewById(R.id.button_save);
        mDelete = getView().findViewById(R.id.button_delete);
        mRadioGroup = getView().findViewById(R.id.low_full_radio_group);
        mRadioLow = getView().findViewById(R.id.radio_button_low);
        mRadioFull = getView().findViewById(R.id.radio_button_full);
        mProductName.setText(mProduct.getName());
        String state = mProduct.getState();
        if (state.equals(PRODUCT_LOW)) {
            mRadioLow.setChecked(true);
            mRadioFull.setChecked(false);
        } else {
            mRadioFull.setChecked(true);
            mRadioLow.setChecked(false);
        }
        Date created = mProduct.getCreated();
        mProductCreated = getView().findViewById(R.id.edit_created);
        mProductCreated.setText(convertMilliSecondsToFormattedDate(created.getTime()));
        mProductExpirationDate = getView().findViewById(R.id.edit_expiration_date);
        mLayoutExpirationDate = getView().findViewById(R.id.layout_expiration_date);
        if (mProduct.getExpired().after(new Date(0))) {
            //Log.d("ABC",mProduct.getExpired().toString() + "abcd: " + (new Date(0)).toString());
            mProductExpirationDate.setText(formatter.format(mProduct.getExpired()));
        }
        setSpinnerCategory();
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
        mLayoutExpirationDate.setOnClickListener(this);
        mProductExpirationDate.setOnClickListener(this);

    }


    private void saveProduct() {
        int idchecked = mRadioGroup.getCheckedRadioButtonId();
        if (idchecked == R.id.radio_button_low) {
            mProduct.setState(PRODUCT_LOW);
        } else {
            mProduct.setState(PRODUCT_FULL);
        }
        mProduct.setName(String.valueOf(mProductName.getText()));
        String category = mProductCategory.getSelectedItem().toString();
        Category categoryObject = mCategoryService.getCategoryByName(category);
        mProduct.setCategory(categoryObject);
        mProduct.setModified(new Date());
        Log.d(TAG, " category change: " + category + " id: " + categoryObject.getId());
        mProductService.updateProduct(mProduct);
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.abc_delete));
        builder.setMessage((getResources().getString(R.string.dialog_message_confirm_delete_item)));
        builder.setPositiveButton(getResources().getString(R.string.abc_delete), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                mPantryListService.removeProduct(mProduct);
                activeFragment(new PantryListFragment());
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

    private boolean createCategory(String name) {
        Log.d(TAG, "Name category" + name);
        String nameCompare = name.toLowerCase();
        ArrayList<Category> list = mCategoryService.getAllCategory();
        for (Category category : list) {
            if (nameCompare.equals(category.getName().toLowerCase()) || nameCompare.equals(getResources().getString(R.string.default_other_category).toLowerCase())) {
                return false;
            }
        }
        Category categoryLast = list.get(list.size() - 1);
        int orderLast = categoryLast.getOrderView();
        Category category = new Category();
        category.setOrderView(orderLast + 1);
        category.setName(name);
        category.setId(mProductService.createCodeId(name, new Date()));
        return DatabaseHelper.mCategoryDao.createCategory(category);
    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public Product getProduct() {
        return mProduct;
    }

    public void setProduct(Product mProduct) {
        this.mProduct = mProduct;
    }

    public String convertMilliSecondsToFormattedDate(long milliSeconds) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            Log.e("Error", "Read file backup: " + e.getMessage());
            return "";
        }

    }
}
