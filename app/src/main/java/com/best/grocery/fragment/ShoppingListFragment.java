package com.best.grocery.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.AdsManager;
import com.best.grocery.AppConfig;
import com.best.grocery.helper.SwipeAndDragShopping;
import com.best.grocery.R;
import com.best.grocery.activity.MainActivity;
import com.best.grocery.adapter.AutocompleteAdapter;
import com.best.grocery.adapter.ShoppingListAdapter;
import com.best.grocery.dialog.CustomDialog;
import com.best.grocery.dialog.DialogCustomLayout;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.ProductService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.utils.PrefManager;

import java.util.ArrayList;


/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
public class ShoppingListFragment extends Fragment implements DefinitionSchema, View.OnClickListener, TextView.OnEditorActionListener,
        PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, TextWatcher {
    private static final String TAG = ShoppingListFragment.class.getSimpleName();
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();
    private AutoCompleteTextView mAutoCompleteTextView;
    private FrameLayout mOpenDrawer;
    private ImageView mMenuShoppingList;
    private ImageView mMicrophone;
    private ImageView mActionShare;
    private ImageView mActionCopy;
    private ImageView mInputAdd;
    private ImageView mInputDelete;
    private ConstraintLayout mLayoutInputTypeTypping;
    private ConstraintLayout mLayoutInputTextUpdate;
    public static TextView mCartPriceInfo;
    public static TextView mListPriceInfo;
    public static TextView mCartCountInfo;
    public static TextView mListCountInfo;
    public static ConstraintLayout mLayoutInfo;
    public static TextView mGuide;
    private Spinner mSpinner;
    ShoppingListAdapter mAdapter;
    private ShoppingListService mShoppingListService;
    private ProductService mProductService;
    private ArrayList<String> mAutocompleteList;
    public static ArrayList<Product> mData;
    public static RecyclerView mRecyclerView;
    private ShoppingList mShoppingList;
    private int iCurrentSelection;

    PrefManager mPrefManager;


    Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // Crashlytics.log(Log.DEBUG, TAG, "onCreate fragment");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        // Crashlytics.log(Log.DEBUG, TAG, "onCreateView fragment");
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Crashlytics.log(Log.DEBUG, TAG, "onActivityCreated fragment");
        mShoppingListService = new ShoppingListService(getContext());
        mProductService = new ProductService(getContext());
        mShoppingList = mShoppingListService.getShoppingListActive();
        mPrefManager = new PrefManager(getContext());
        MainActivity.mNavigationView.getMenu().getItem(0).setChecked(true);
        mPrefManager.putString(SHARE_PREFERENCES_FRAGMENT_ACTIVE, SHOPPING_LIST_ACTIVE);
        initViews();
        initAutoCompleteTextView();
        setOnListener();
        onBackPressed();
        hideSoftKeyBoard();
        initRecyclerView();
        adsManager();
        buidInfoShoppingList();

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void adsManager() {
        AdsManager adsManager = new AdsManager(getContext(), getActivity());
        adsManager.adsShoppingList();
    }


    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        if (MainActivity.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                            MainActivity.mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            getActivity().finish();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initViews() {
        mOpenDrawer = getView().findViewById(R.id.open_drawer);
        mMenuShoppingList = getView().findViewById(R.id.menu_image);
        mAutoCompleteTextView = getView().findViewById(R.id.text_auto_complete);
        mGuide = getView().findViewById(R.id.guide_shopping_list);
        initSpinner();
        mMicrophone = getView().findViewById(R.id.microphone);
        mCartPriceInfo = getView().findViewById(R.id.shopping_list_cart_info);
        mListPriceInfo = getView().findViewById(R.id.shopping_list_info);
        mLayoutInfo = getView().findViewById(R.id.layout_info);
        mListCountInfo = getView().findViewById(R.id.shopping_list_info_total);
        mCartCountInfo = getView().findViewById(R.id.shopping_list_cart_info_total);
        mInputAdd = getView().findViewById(R.id.input_add);
        mInputDelete = getView().findViewById(R.id.input_delete);
        mLayoutInputTextUpdate = getView().findViewById(R.id.layout_input_text_update);
        mLayoutInputTypeTypping = getView().findViewById(R.id.layout_input_type_typing);
        mActionShare = getView().findViewById(R.id.action_share);
        mActionCopy = getView().findViewById(R.id.action_copy);

    }


    private void initRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.recycler_view_shopping_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mData = mShoppingListService.productShoppingSrceen(mShoppingList);
        if (mData.size() == 0) {
            mGuide.setVisibility(View.VISIBLE);
        } else {
            mGuide.setVisibility(View.GONE);
        }
        mAdapter = new ShoppingListAdapter(getActivity(), getContext(), mShoppingList);
        //drag and swipe
        SwipeAndDragShopping swipeAndDragHelper = new SwipeAndDragShopping(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, mAdapter, getContext());
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(mItemTouchHelper);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 1);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    public void buildAgainList() {
        this.mData = mShoppingListService.productShoppingSrceen(mShoppingList);
        if (this.mData.size() == 0) {
            mGuide.setVisibility(View.VISIBLE);
        } else {
            mGuide.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
        buidInfoShoppingList();
    }

    private void setOnListener() {
        mOpenDrawer.setOnClickListener(this);
        mMenuShoppingList.setOnClickListener(this);
        mAutoCompleteTextView.setOnEditorActionListener(this);
        mSpinner.setOnItemSelectedListener(this);
        mMicrophone.setOnClickListener(this);
        mActionShare.setOnClickListener(this);
        mActionCopy.setOnClickListener(this);
        mAutoCompleteTextView.addTextChangedListener(this);
        mInputAdd.setOnClickListener(this);
        mInputDelete.setOnClickListener(this);
    }

    private void initAutoCompleteTextView() {
        mAutocompleteList = mProductService.getAutoComplete();
        final AutocompleteAdapter adapter = new AutocompleteAdapter(getContext(), R.layout.spinner_dropdown_item_layout, mAutocompleteList);
        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.setThreshold(1);
        adapter.setOnClickListener(new AutocompleteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Log.d(TAG, "Input user: " + text);
                if (!text.equals("")) {
                    addProductToList(text);
                    buildAgainList();
                }

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
        mSpinner = getView().findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_tool_bar_shopping_list, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        mSpinner.setAdapter(spinnerArrayAdapter);
        iCurrentSelection = mSpinner.getSelectedItemPosition();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
        switch (id) {
            case R.id.open_drawer:
                Log.d(TAG, "open drawer");
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);

                break;
            case R.id.menu_image:
                PopupMenu popup = new PopupMenu(v.getContext(), mMenuShoppingList);
                popup.inflate(R.menu.shopping_list);
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            case R.id.microphone:
                startVoiceInput();
                break;
            case R.id.action_copy:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ClipboardFragment());
                    }
                }, AppConfig.DELAY_EFFECT);

                break;
            case R.id.action_share:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mData.size() == 0) {
                            Toast.makeText(getContext(), getResources().getString(R.string.toast_list_empty), Toast.LENGTH_LONG).show();
                        } else {
                            ShareProductFragment fragment = new ShareProductFragment();
                            ArrayList<Product> listShare = new ArrayList<>();
                            for (Product product : mData) {
                                if (!product.isChecked()) {
                                    listShare.add(product);
                                }
                            }
                            fragment.setData(listShare);
                            activeFragment(fragment);
                        }
                    }
                }, AppConfig.DELAY_EFFECT);
                Log.d(TAG, "Share text");
                break;
            case R.id.input_add:
                String text = mAutoCompleteTextView.getText().toString();
                addProductToList(text);
                buildAgainList();
                hideSoftKeyBoard();
                Log.d(TAG, "Add product: " + text);
                break;
            case R.id.input_delete:
                mAutoCompleteTextView.setText("");
                Log.d(TAG, "Delete product");
                break;
            default:
                break;

        }

    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String codeLanguage = mPrefManager.getString(SHARE_PREFERENCES_LANGUAGE_CODE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, codeLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.try_saying_something));
        try {
            startActivityForResult(intent, AppConfig.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConfig.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == getActivity().RESULT_OK && null != data) {
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.size() == 1) {
                        mAutoCompleteTextView.setText(result.get(0));
                        break;
                    } else {
                        final CharSequence[] input = result.toArray(new String[result.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.dialog_title_voice);
                        builder.setItems(input, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedText = result.get(which);
                                mAutoCompleteTextView.setText(selectedText);
                            }
                        });
                        builder.create();
                        builder.show();
                    }
                }
                break;
            }

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
    public boolean onMenuItemClick(MenuItem menuItem) {
        CustomDialog customDialog = new CustomDialog(getContext());
        switch (menuItem.getItemId()) {
            case R.id.action_create_list:
                DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(getContext());
                String message = getString(R.string.dialog_message_create_list);
                dialogCustomLayout.onCreate(message, "");
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if (mShoppingListService.checkBeforeUpdateList(text)) {
                            mShoppingListService.createNewListShopping(text);
                            activeFragment(new ShoppingListFragment());
                        } else {
                            hideSoftKeyBoard();
                            Toast.makeText(getContext(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return true;
            case R.id.action_move_copy_items:
                MoveOrCopyProductFragment moveOrCopyProductFragment = new MoveOrCopyProductFragment();
                moveOrCopyProductFragment.setData(mData);
                activeFragment(moveOrCopyProductFragment);
                return true;
            case R.id.action_delete_all:
                customDialog.onCreate(getString(R.string.dialog_message_confirm_delete_all_item)
                        , getString(R.string.abc_delete), getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mShoppingListService.clearAllProduct(mShoppingList);
                        buildAgainList();
                    }

                });
                return true;
            case R.id.action_check_all:
                Log.d(TAG, "Checked all: " + mShoppingList.getName());
                mShoppingListService.checkAll(mShoppingList);
                buildAgainList();
                return true;
            case R.id.action_uncheck_all:
                customDialog.onCreate(getString(R.string.dialog_message_confirm_uncheck_all_item),
                        getString(R.string.abc_confirm), getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mShoppingListService.unCheckAll(mShoppingList);
                        buildAgainList();
                    }

                });

                return true;
            case R.id.action_show_snooze:
                ShoppingSnoozeFragment fragment = new ShoppingSnoozeFragment();
                fragment.setShoppingList(mShoppingList);
                activeFragment(fragment);
                return true;
            case R.id.action_category_manage:
                activeFragment(new CategoryManageFragment());
                return true;
            case R.id.action_manage_list:
                activeFragment(new ManageShoppingFragment());
                return true;
            case R.id.action_multi_list:
                showChoiceList();
                return true;
            case R.id.action_quick_set_qty:
                ArrayList<Product> data = new ArrayList<>();
                for (Product product : mData) {
                    if (!product.isChecked()) {
                        data.add(product);
                    }
                }
                QuickSetQtyFragment qtyFragment = new QuickSetQtyFragment();
                qtyFragment.setData(data);
                activeFragment(qtyFragment);
                return true;
            case R.id.action_shopping_to_pantry:
                ShoppingToPantryFragment toPantryFragment = new ShoppingToPantryFragment();
                toPantryFragment.setData(mData);
                activeFragment(toPantryFragment);
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString().trim();
        Log.d(TAG, "Input user: " + text);
        if (!text.equals("")) {
            addProductToList(text);
            buildAgainList();
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            String text = v.getText().toString().trim();
            Log.d(TAG, "Input user" + text);
            if (!text.equals("")) {
                addProductToList(text);
                buildAgainList();
                Log.d(TAG, "show keyboard");
            } else {
                Log.d(TAG, "Input null");
            }
            return true;
        }
        return false;
    }

    private Product addProductToList(String text) {
        mAutoCompleteTextView.setText("");
        Product product = mShoppingListService.addProductToShopping(text, mShoppingList);
        return product;
    }

    private void buidInfoShoppingList() {
        Double priceCart = 0.0, priceList = 0.0;
        int itemCart = 0, itemList = 0;
        for (Product product : mData) {
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
        if (itemList == 0.0) {
            Log.d(TAG, "hide info list");
            mLayoutInfo.setVisibility(View.GONE);

        } else {
            mLayoutInfo.setVisibility(View.VISIBLE);
            mCartPriceInfo.setText(priceCartText);
            mCartCountInfo.setText(countCartText);
            mListCountInfo.setText(countListText);
            mListPriceInfo.setText(priceListText);

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = (String) parent.getItemAtPosition(position);
        mShoppingList = mShoppingListService.activeShopping(text);
        if (iCurrentSelection != position) {
            activeFragment(new ShoppingListFragment());
        }
        iCurrentSelection = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Log.d(TAG, "on text change: " + charSequence);
        if (charSequence.toString().equals("")) {
            //Log.d(TAG, "Show input type typing");
            mLayoutInputTypeTypping.setVisibility(View.VISIBLE);
            mLayoutInputTextUpdate.setVisibility(View.GONE);
        } else {
            mLayoutInputTypeTypping.setVisibility(View.GONE);
            mLayoutInputTextUpdate.setVisibility(View.VISIBLE);
            //Log.d(TAG, "Show input text update");
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d(TAG, "after text change: ");

    }

    private void showChoiceList() {
        final ArrayList<Integer> mSelectedItems = new ArrayList();
        ArrayList<ShoppingList> allShoppingList = mShoppingListService.getAllShoppingList();
        ShoppingList listActive = mShoppingListService.getShoppingListActive();
        boolean[] checkedItems = new boolean[allShoppingList.size()];
        ArrayList<String> listItems = new ArrayList<>();
        for (int i = 0; i < allShoppingList.size(); i++) {
            String name = allShoppingList.get(i).getName();
            listItems.add(name);
            if (listActive.getName().equals(name)) {
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
                        ArrayList<ShoppingList> listSelected = new ArrayList<>();
                        for (int index : mSelectedItems) {
                            ShoppingList shoppingList = mShoppingListService.getListByName(items[index]);
                            listSelected.add(shoppingList);
                        }
                        if (listSelected.size() == 0) {
                            Toast.makeText(getContext(), getString(R.string.toast_no_selected_list), Toast.LENGTH_LONG).show();
                        } else {
                            MultiShoppingFragment fragment = new MultiShoppingFragment();
                            fragment.setmShoppingListChecked(listSelected);
                            activeFragment(fragment);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.abc_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button btnNegative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnNegative.setTextColor(getResources().getColor(R.color.btn_negative));
        Button btnPositive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        btnPositive.setTextColor(getResources().getColor(R.color.colorAccent));
    }
}
