package com.best.grocery.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.AdsManager;
import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.adapter.MultiListShoppingAdapter;
import com.best.grocery.dialog.CustomDialog;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.ProductService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;

import java.util.ArrayList;

public class MultiShoppingFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = MultiShoppingFragment.class.getSimpleName();
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();
    private ImageView mButtonBack;
    private TextView mTextNameList;
    private ConstraintLayout mLayoutChangeMultiList;
    private RecyclerView mRecyclerView;
    private ImageView mImageKeepAwake;
    private boolean isKeepAwake;
    private TextView mCartPriceInfo;
    private TextView mListPriceInfo;
    private TextView mCartCountInfo;
    private TextView mListCountInfo;

    private ShoppingListService mShoppingListService;
    private ProductService mProductService;
    private MultiListShoppingAdapter mAdapter;
    private ArrayList<Product> mData = new ArrayList<>();
    private ArrayList<ShoppingList> mShoppingListChecked = new ArrayList<>();
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_multi_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShoppingListService = new ShoppingListService(getContext());
        mProductService = new ProductService(getContext());
        isKeepAwake = false;
        initViews();
        initRecyclerView();
        setOnListener();
        hideSoftKeyBoard();
        adsManager();
    }

    @Override
    public void onDestroyView() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroyView();
    }

    private void initViews() {
        mButtonBack = getView().findViewById(R.id.image_back_screen);
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        mTextNameList = getView().findViewById(R.id.text_name_list);
        mLayoutChangeMultiList = getView().findViewById(R.id.layout_change_multi_list);
        setTextNameList();
        mCartPriceInfo = getView().findViewById(R.id.text_cart_info_price);
        mListPriceInfo = getView().findViewById(R.id.text_list_info_price);
        mCartCountInfo = getView().findViewById(R.id.text_cart_info_total);
        mListCountInfo = getView().findViewById(R.id.text_list_info_total);
        mImageKeepAwake = getView().findViewById(R.id.image_keep_awake);
        setImageKeepAwake();

    }

    private void setImageKeepAwake() {
        if (isKeepAwake) {
            mImageKeepAwake.setImageResource(R.drawable.icon_light_on_circle);
        } else {
            mImageKeepAwake.setImageResource(R.drawable.icon_light_off_circle);
        }
    }

    private void setTextNameList() {
        String text = "";
        if (mShoppingListChecked.size() == 1) {
            text = mShoppingListChecked.get(0).getName();
        } else {
            for (int i = 0; i < mShoppingListChecked.size() - 1; i++) {
                text = text + mShoppingListChecked.get(i).getName() + " + ";
            }
            text = text + mShoppingListChecked.get(mShoppingListChecked.size() - 1).getName();
        }
        mTextNameList.setText(text);
    }

    private void initRecyclerView() {
        mData = mShoppingListService.getAllProductShopping(mShoppingListChecked);
        buidInfoShoppingList(mData);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MultiListShoppingAdapter(getActivity(), getContext(), mData);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 1);
        mAdapter.setOnItemClickListener(new MultiListShoppingAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(Product product) {
                if (product.isChecked()) {
                    product.setChecked(false);
                } else {
                    product.setChecked(true);
                }
                mProductService.updateProduct(product);
                loadAgainShopping(mShoppingListChecked);
                Log.d(TAG, "name: " + product.getName());
            }

            @Override
            public void onHeaderClickListener(int id) {
                final ArrayList<Product> productsChecked = new ArrayList<>();
                for (Product product : mData) {
                    if (product.getName() != null && product.isChecked()) {
                        productsChecked.add(product);
                    }
                }
                switch (id) {
                    case R.id.action_delete_all:
                        Log.d(TAG, "Delete all");
                        deleteAllProductPurchased(productsChecked);
                        break;
                    case R.id.action_uncheck_all:
                        Log.d(TAG, "Uncheck all");
                        uncheckAllProduct(productsChecked);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void uncheckAllProduct(final ArrayList<Product> products) {
        CustomDialog customDialog = new CustomDialog(getContext());
        customDialog.onCreate(getString(R.string.dialog_message_confirm_uncheck_all_item)
                , getString(R.string.abc_confirm), getString(R.string.abc_cancel));
        customDialog.setListener(new CustomDialog.OnClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                for (Product product : products) {
                    product.setChecked(false);
                    mProductService.updateProduct(product);
                }
                loadAgainShopping(mShoppingListChecked);
            }

        });

    }

    private void deleteAllProductPurchased(final ArrayList<Product> products) {
        CustomDialog customDialog = new CustomDialog(getContext());
        customDialog.onCreate(getString(R.string.dialog_message_confirm_delete_all_item)
                , getString(R.string.abc_delete), getString(R.string.abc_cancel));
        customDialog.setListener(new CustomDialog.OnClickListener() {
            @Override
            public void onClickPositiveButton(DialogInterface dialog, int id) {
                for (Product product : products) {
                    product.setShoppingList(new ShoppingList());
                    mProductService.updateProduct(product);
                }
                loadAgainShopping(mShoppingListChecked);
            }

        });

    }

    private void loadAgainShopping(ArrayList<ShoppingList> arrayList) {
        mData = mShoppingListService.getAllProductShopping(arrayList);
        mAdapter.loadAgianList(mData);
        buidInfoShoppingList(mData);
    }

    private void setOnListener() {
        mButtonBack.setOnClickListener(this);
        mLayoutChangeMultiList.setOnClickListener(this);
        mImageKeepAwake.setOnClickListener(this);
    }


    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void adsManager() {
        AdsManager adsManager = new AdsManager(getContext(), getActivity());
        adsManager.adsShoppingList();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.image_back_screen:
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;
            case R.id.layout_change_multi_list:
                showChoiceList();
                break;
            case R.id.image_keep_awake:
                String message = "";
                Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
                View view = toast.getView();
                view.getBackground().setColorFilter(getContext().getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                TextView textView = view.findViewById(android.R.id.message);
                if (!isKeepAwake) {
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    message = getString(R.string.toast_turn_on_keep_awake);
                    isKeepAwake = true;
                    setImageKeepAwake();
                } else {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    message = getString(R.string.toast_turn_off_keep_awake);
                    isKeepAwake = false;
                    setImageKeepAwake();
                }
                textView.setText(message);
                toast.show();
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


    private void showChoiceList() {
        final ArrayList<Integer> mSelectedItems = new ArrayList();
        ArrayList<ShoppingList> allShoppingList = mShoppingListService.getAllShoppingList();
        boolean[] checkedItems = new boolean[allShoppingList.size()];
        ArrayList<String> listNameChoice = new ArrayList<>();
        ArrayList<String> listItems = new ArrayList<>();
        for (ShoppingList shoppingList : mShoppingListChecked) {
            listNameChoice.add(shoppingList.getName());
        }
        for (int i = 0; i < allShoppingList.size(); i++) {
            String name = allShoppingList.get(i).getName();
            listItems.add(name);
            if (listNameChoice.contains(name)) {
                checkedItems[i] = true;
                mSelectedItems.add(i);
            } else {
                checkedItems[i] = false;
            }

        }
        final String[] items = listItems.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_title_choice_list));
        builder.setMultiChoiceItems(items, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            mSelectedItems.add(which);
                        } else if (mSelectedItems.contains(which)) {
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton(getString(R.string.abc_got_it), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mSelectedItems.size() != 0) {
                            mShoppingListChecked.clear();
                            for (int index : mSelectedItems) {
                                ShoppingList shoppingList = mShoppingListService.getListByName(items[index]);
                                mShoppingListChecked.add(shoppingList);
                            }
                            loadAgainShopping(mShoppingListChecked);
                            setTextNameList();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.toast_no_selected_list), Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setNegativeButton(getString(R.string.abc_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void setmShoppingListChecked(ArrayList<ShoppingList> mShoppingListChecked) {
        this.mShoppingListChecked = mShoppingListChecked;
    }

    private void buidInfoShoppingList(ArrayList<Product> data) {
        Double priceCart = 0.0, priceList = 0.0;
        int itemCart = 0, itemList = 0;
        for (Product product : data) {
            if (!TextUtils.isEmpty(product.getName())) {
                Double price = (product.getQuantity() * product.getUnitPrice());
                if (product.isChecked()) {
                    itemList++;
                    priceList += price;
                    itemCart++;
                    priceCart += price;
                } else {
                    itemList++;
                    priceList += price;
                }
            }
        }
        String priceCartText = CURRENCY_DEFUALT + mProductService.parserPrice(priceCart);
        String priceListText = CURRENCY_DEFUALT + mProductService.parserPrice(priceList);
        if (priceCart == 0.0) {
            priceCartText = CURRENCY_DEFUALT + "0.0";
        }
        if (priceList == 0.0) {
            priceListText = CURRENCY_DEFUALT + "0.0";
        }
        String countCartText = String.valueOf(itemCart);
        String countListText = String.valueOf(itemList);

        mCartPriceInfo.setText(priceCartText);
        mCartCountInfo.setText(countCartText);
        mListCountInfo.setText(countListText);
        mListPriceInfo.setText(priceListText);


    }

}
