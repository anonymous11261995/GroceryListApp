package com.best.grocery.fragment;

import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.ShoppingToPantryAdapter;
import com.best.grocery.entity.PantryList;
import com.best.grocery.entity.Product;
import com.best.grocery.service.PantryListService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;
import java.util.Collections;

public class ShoppingToPantryFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = ShareProductFragment.class.getSimpleName();
    private ImageView mButtonBack;
    private RecyclerView mRecyclerView;
    private Spinner mSpinnerList;
    private Button mButtonAdd;
    private ShoppingToPantryAdapter mAdapter;
    ArrayList<Product> mData;
    private PantryListService mPantryListService;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_to_pantry, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPantryListService = new PantryListService(getContext());
        initViews();
        initRecyclerView();
        setOnListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopping_to_pantry_button_back:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.shopping_to_pantry_button_add:
                final ArrayList<Product> dataAdd = new ArrayList<>(mAdapter.getDataChecked());
                Log.d(TAG, "Size of list to pantry: " + dataAdd.size());
                if (dataAdd.size() == 0) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setMessage(getResources().getString(R.string.dialog_message_clipboard_no_data));
                    builder.setNeutralButton("OK", null);
                    AlertDialog alert = builder.create();
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
                                mPantryListService.shoppingListToPantryList(nameList, dataAdd);
                            } catch (Exception ex) {
                                Log.e("Error", ex.getMessage());
                            }
                            progressDialog.dismiss();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setCancelable(false);
                                    builder.setMessage(getResources().getString(R.string.dialog_message_shopping_list_to_pantry));
                                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            activeFragment(new PantryListFragment());
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

    private void initViews() {
        mButtonBack = getView().findViewById(R.id.shopping_to_pantry_button_back);
        mButtonAdd = getView().findViewById(R.id.shopping_to_pantry_button_add);
        mSpinnerList = getView().findViewById(R.id.shopping_to_pantry_spinner);
        initSpinner();
    }


    private void setOnListener() {
        mButtonBack.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
    }

    private void initRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.recycler_view_shopping_to_pantry);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ShoppingToPantryAdapter(getContext(), mData);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 1);


    }

    private void initSpinner() {
        PantryList pantryList = mPantryListService.getListActive();
        ArrayList<PantryList> list = mPantryListService.getAllList();
        ArrayList<String> arrayList = new ArrayList<>();
        for (PantryList item : list) {
            arrayList.add(item.getName());
        }
        Collections.reverse(arrayList);
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
