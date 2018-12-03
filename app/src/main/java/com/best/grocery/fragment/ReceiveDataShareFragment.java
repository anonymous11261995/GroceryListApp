package com.best.grocery.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
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
import com.best.grocery.adapter.ListProductSimpleAdapter;
import com.best.grocery.dialog.DialogCustomLayout;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.ProductService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;
import java.util.Collections;

public class ReceiveDataShareFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = ReceiveDataShareFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private Spinner mSpinnerList;
    private ImageView mImageViewAddList;
    private Button mButtonAdd;

    private String mTextRecive;
    private ShoppingListService mShoppingListService;
    private ProductService mProductService;
    private ListProductSimpleAdapter mAdapter;
    Handler handler = new Handler();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive_data_share, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShoppingListService = new ShoppingListService(getContext());
        mProductService = new ProductService(getContext());
        initViews();
        setOnListener();
        initRecyclerView();
        hideSoftKeyBoard();
        onBackPressed();
    }


    private void initViews() {

        mButtonAdd = getView().findViewById(R.id.button_add_item);
        mSpinnerList = getView().findViewById(R.id.spinner);
        mImageViewAddList = getView().findViewById(R.id.image_create_new_list);
        initSpinner();
    }

    private void setOnListener() {
        mButtonAdd.setOnClickListener(this);
        mImageViewAddList.setOnClickListener(this);
    }

    private void initRecyclerView() {
        ArrayList<Product> products = mProductService.readDataImport(mTextRecive);
        boolean isContinue = false;
        for (Product product : products) {
            if (product.getName() != null) {
                isContinue = true;
                break;
            }
        }
        if (isContinue) {
            mRecyclerView = getView().findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layoutManager);
            mAdapter = new ListProductSimpleAdapter(getActivity(), getContext(), products);
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.setAdapter(mAdapter);
                }
            }, 1);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setMessage(getResources().getString(R.string.clipboard_not_format_data));
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

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
        AlertDialog.Builder builder;
        AlertDialog alert;
        switch (id) {
            case R.id.image_create_new_list:
                DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(getContext());
                String message = getString(R.string.dialog_message_create_list);
                dialogCustomLayout.onCreate(message,"");
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
            case R.id.button_add_item:
                final ArrayList<Product> dataAdd = mAdapter.getDataChecked();
                if (dataAdd.size() == 0) {
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setMessage(getResources().getString(R.string.dialog_message_clipboard_no_data));
                    builder.setNeutralButton("OK", null);
                    alert = builder.create();
                    alert.show();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage(getString(R.string.dialog_message_processing));
                    progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String nameList = mSpinnerList.getSelectedItem().toString();
                                mShoppingListService.addProductShared(nameList, dataAdd);
                            } catch (Exception ex) {
                                Log.e("Error", ex.getMessage());
                            }
                            progressDialog.dismiss();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setCancelable(false);
                                    builder.setMessage(getResources().getString(R.string.dialog_message_item_share_to_shopping_list));
                                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getActivity().finish();
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                        }
                    });
                    t.start();
                    progressDialog.show();
                }
                break;
            default:
                break;
        }

    }

    private void onBackPressed() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        getActivity().finish();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initSpinner() {
        ArrayList<ShoppingList> list = mShoppingListService.getAllShoppingList();
        ShoppingList listActive = mShoppingListService.getShoppingListActive();
        ArrayList<String> arrayList = new ArrayList<>();
        for (ShoppingList sl : list) {
            String name = sl.getName();
            if (name.equals(listActive.getName())) {
                arrayList.add(0, name);
            } else {
                arrayList.add(sl.getName());
            }
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_recipe_detail, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        mSpinnerList.setAdapter(spinnerArrayAdapter);
    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setTextRecive(String text) {
        this.mTextRecive = text;
    }
}
