package com.best.grocery.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.MasterListAdapter;
import com.best.grocery.entity.PantryList;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.PantryListService;
import com.best.grocery.service.ProductService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.utils.PrefManager;

import java.util.ArrayList;


/**
 * Created by TienTruong on 7/25/2018.
 */


public class MasterListFragment extends Fragment implements DefinitionSchema, View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {
    private static final String TAG = MasterListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    public static Button mButton;
    private ImageView mBack;
    private TextView mTextEmptyData;
    private ImageView mImageSearch;
    private ImageView mImageQuitSearch;
    private EditText editTextSearch;
    private ImageView mImageClearText;
    private ConstraintLayout mLayoutToolbar;
    private ConstraintLayout mLayoutToolbarSearch;
    private ShoppingListService mShoppingListService;
    private ProductService mProductService;
    private PantryListService mPantryListService;
    private MasterListAdapter mAdapter;
    private ArrayList<String> mData = new ArrayList<>();
    private ArrayList<String> mDataMasterList = new ArrayList<>();
    private PrefManager mPrefManager;
    private String lastActiveFragment;

    Handler handler = new Handler();
    Runnable workRunnable;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_master_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPrefManager = new PrefManager(getContext());
        mShoppingListService = new ShoppingListService(getContext());
        mProductService = new ProductService(getContext());
        mPantryListService = new PantryListService(getContext());
        mData = mProductService.getMasterList();
        mDataMasterList.addAll(mData);
        lastActiveFragment = mPrefManager.getString(SHARE_PREFERENCES_FRAGMENT_ACTIVE, SHOPPING_LIST_ACTIVE);
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
        if (mData.size() == 0) {
            mTextEmptyData.setVisibility(View.VISIBLE);
        } else {
            mTextEmptyData.setVisibility(View.GONE);
        }
        mRecyclerView = getView().findViewById(R.id.history_list_product);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                mAdapter = new MasterListAdapter(mData, getContext(), getActivity());
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 1);
    }

    private void setOnListener() {
        mButton.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mImageSearch.setOnClickListener(this);
        mImageQuitSearch.setOnClickListener(this);
        editTextSearch.addTextChangedListener(this);
        editTextSearch.setOnEditorActionListener(this);
        mImageClearText.setOnClickListener(this);
    }

    private void initViews() {
        mLayoutToolbar = getView().findViewById(R.id.layout_toolbar);
        mLayoutToolbarSearch = getView().findViewById(R.id.layout_toolbar_search);
        mButton = getView().findViewById(R.id.action_add_from_history);
        mBack = getView().findViewById(R.id.history_back);
        mTextEmptyData = getView().findViewById(R.id.history_empty_data);
        mImageSearch = getView().findViewById(R.id.image_search);
        mImageQuitSearch = getView().findViewById(R.id.image_quit_search);
        editTextSearch = getView().findViewById(R.id.edittext_search);
        editTextSearch.setText("");
        mImageClearText = getView().findViewById(R.id.image_clear_text);
        mImageClearText.setVisibility(View.GONE);
        displayIconClearText();
    }

    private void displayIconClearText() {
        String text = editTextSearch.getText().toString();
        if (text.equals("")) {
            mImageClearText.setVisibility(View.GONE);
        } else {
            mImageClearText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.action_add_from_history:
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.dialog_message_processing));
                progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> data = mAdapter.getProductChoosed();
                            for (String text : data) {
                                if (lastActiveFragment.equals(SHOPPING_LIST_ACTIVE)) {
                                    ShoppingList shoppingList = mShoppingListService.getShoppingListActive();
                                    mShoppingListService.addProductToShopping(text, shoppingList);


                                } else {
                                    PantryList pantryList = mPantryListService.getListActive();
                                    mPantryListService.addProductToPantry(text, pantryList);
                                }
                            }
                        } catch (Exception ex) {

                        }
                        progressDialog.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (lastActiveFragment.equals(SHOPPING_LIST_ACTIVE)) {
                                    activeFragment(new ShoppingListFragment());

                                } else if (lastActiveFragment.equals(PANTRY_LIST_ACTIVE)) {
                                    activeFragment(new PantryListFragment());
                                }
                            }
                        });
                    }
                });
                t.start();
                progressDialog.show();
                break;
            case R.id.history_back:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (lastActiveFragment.equals(PANTRY_LIST_ACTIVE)) {
                            activeFragment(new PantryListFragment());
                        } else {
                            activeFragment(new ShoppingListFragment());
                        }
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.image_search:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLayoutToolbarSearch.setVisibility(View.VISIBLE);
                        mLayoutToolbar.setVisibility(View.GONE);
                    }
                }, 200);
                break;
            case R.id.image_quit_search:
//                mData.clear();
//                mData.addAll(mDataMasterList);
//                mAdapter.notifyDataSetChanged();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLayoutToolbarSearch.setVisibility(View.GONE);
                        mLayoutToolbar.setVisibility(View.VISIBLE);
                        hideSoftKeyBoard();
                    }
                }, 200);
                break;
            case R.id.image_clear_text:
                editTextSearch.setText("");
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


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(final Editable s) {
        displayIconClearText();
        handler.removeCallbacks(workRunnable);
        workRunnable = new Runnable() {
            @Override
            public void run() {
                search(s.toString());
            }
        };
        handler.postDelayed(workRunnable, AppConfig.DELAY_TEXT_CHANED /*delay*/);

    }

    private void search(String s) {
        Log.d(TAG, "query: " + s);
        mData.clear();
        if (s.equals("")) {
            mData.addAll(mDataMasterList);
        } else {
            ;
            ArrayList<String> suggestionsPre = new ArrayList<>();
            ArrayList<String> suggestionsContains = new ArrayList<>();
            for (String text : mDataMasterList) {
                if (text.toLowerCase().startsWith(s.toLowerCase())) {
                    suggestionsPre.add(text);
                }
            }
            for (String text : mDataMasterList) {
                if (text.toLowerCase().contains(s.toLowerCase()) && !suggestionsPre.contains(text)) {
                    suggestionsContains.add(text);
                }
            }
            mData.addAll(suggestionsPre);
            mData.addAll(suggestionsContains);
        }
        mAdapter.notifyDataSetChanged();
        if (mData.size() == 0) {
            mTextEmptyData.setVisibility(View.VISIBLE);
        } else {
            mTextEmptyData.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
            hideSoftKeyBoard();
            return true;
        }
        return false;
    }

}
