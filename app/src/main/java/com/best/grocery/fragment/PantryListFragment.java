package com.best.grocery.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.activity.MainActivity;
import com.best.grocery.adapter.AutocompleteAdapter;
import com.best.grocery.adapter.PantryListAdapter;
import com.best.grocery.dialog.CustomDialog;
import com.best.grocery.dialog.DialogCustomLayout;
import com.best.grocery.entity.PantryList;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.PantryListService;
import com.best.grocery.service.ProductService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;
import com.best.grocery.utils.PrefManager;
import com.best.grocery.helper.SwipeAndDrag;

import java.util.ArrayList;
import java.util.Collections;

public class PantryListFragment extends Fragment implements DefinitionSchema, View.OnClickListener,
        TextView.OnEditorActionListener, PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    private static final String TAG = PantryListFragment.class.getSimpleName();
    private AutoCompleteTextView mAutoCompleteTextView;
    private FrameLayout mOpenDrawer;
    private ImageView mImageMenu;
    private ImageView mMicrophone;
    private ImageView mActionShare;
    private ImageView mActionCopy;
    private ImageView mInputAdd;
    private ImageView mInputDelete;
    private ConstraintLayout mLayoutInputTypeTypping;
    private ConstraintLayout mLayoutInputTextUpdate;
    public static TextView mGuide;
    private Spinner mSpinner;
    private Spinner mSpinnerShopping;
    public static ConstraintLayout mLayoutBottom;
    private Button mButtonAdd;
    private ArrayList<String> mAutocompleteList;
    public static ArrayList<Product> mData;
    public static ArrayList<String> mDataChecked;
    public static RecyclerView mRecyclerView;
    private PantryListService mPantryListService;
    private ProductService mProductService;
    private ShoppingListService mShoppingListService;
    private int iCurrentSelection;
    public static PantryList mPantryList;
    private PantryListAdapter mAdapter;
    private PrefManager mPrefManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantry_list, container, false);
        Log.d(TAG, "onCreateView");
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mPantryListService = new PantryListService(getContext());
        mProductService = new ProductService(getContext());
        mShoppingListService = new ShoppingListService(getContext());
        mPantryList = mPantryListService.getListActive();
        mDataChecked = new ArrayList<>();
        mPrefManager = new PrefManager(getContext());
        MainActivity.mNavigationView.getMenu().getItem(1).setChecked(true);
        mPrefManager.putString(SHARE_PREFERENCES_FRAGMENT_ACTIVE, PANTRY_LIST_ACTIVE);
        initViews();
        initRecyclerView();
        initAutoCompleteTextView();
        setOnListener();
        hideSoftKeyBoard();
        onBackPressed();

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

    private void initRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.recycler_view_pantry_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mData = mPantryListService.productPantry(mPantryList);
        for (Product p : mData) {
            Log.d("ABCD", " name: " + p.getContent() + " category: " + p.getCategory().getId());
        }
        if (mData.size() == 0) {
            mGuide.setVisibility(View.VISIBLE);
        } else {
            mGuide.setVisibility(View.GONE);
        }
        if (mDataChecked.size() == 0) {
            mLayoutBottom.setVisibility(View.GONE);
        } else {
            mLayoutBottom.setVisibility(View.VISIBLE);
        }
        mAdapter = new PantryListAdapter(getActivity(), getContext());
        //drag and swipe
        SwipeAndDrag swipeAndDragHelper = new SwipeAndDrag(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT, mAdapter, getContext());
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(mItemTouchHelper);
        mRecyclerView.setAdapter(mAdapter);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void initViews() {
        mOpenDrawer = getView().findViewById(R.id.open_drawer);
        mImageMenu = getView().findViewById(R.id.menu_image);
        mAutoCompleteTextView = getView().findViewById(R.id.text_auto_complete);
        mGuide = getView().findViewById(R.id.guide_pantry_list);
        mSpinner = getView().findViewById(R.id.spinner);
        mSpinnerShopping = getView().findViewById(R.id.pantry_list_spinner);
        mButtonAdd = getView().findViewById(R.id.pantry_list_button_add);
        initSpinner();
        mLayoutBottom = getView().findViewById(R.id.pantry_list_constraint_bottom);
        mMicrophone = getView().findViewById(R.id.microphone);
        mActionShare = getView().findViewById(R.id.action_share);
        mActionShare.setVisibility(View.GONE);
        mActionCopy = getView().findViewById(R.id.action_copy);
        mActionCopy.setVisibility(View.GONE);
        mInputAdd = getView().findViewById(R.id.input_add);
        mInputDelete = getView().findViewById(R.id.input_delete);
        mLayoutInputTextUpdate = getView().findViewById(R.id.layout_input_text_update);
        mLayoutInputTypeTypping = getView().findViewById(R.id.layout_input_type_typing);
    }

    private void initSpinner() {
        ArrayList<PantryList> list = mPantryListService.getAllList();
        Collections.reverse(list);
        ArrayList<String> arrayList = new ArrayList<>();
        for (PantryList pl : list) {
            arrayList.add(pl.getName());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_tool_bar_pantry_list, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        mSpinner.setAdapter(spinnerArrayAdapter);
        iCurrentSelection = mSpinner.getSelectedItemPosition();
        //
        ArrayList<ShoppingList> listShopping = mShoppingListService.getAllShoppingList();
        ArrayList<String> arrayListShopping = new ArrayList<>();
        for (ShoppingList sl : listShopping) {
            if (sl.isActive()) {
                arrayListShopping.add(0, sl.getName());
            } else {
                arrayListShopping.add(sl.getName());
            }
        }
        ArrayAdapter<String> adapterShopping = new ArrayAdapter<>(getActivity(), R.layout.spinner_pantry_list, arrayListShopping);
        adapterShopping.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        mSpinnerShopping.setAdapter(adapterShopping);
    }

    private void initAutoCompleteTextView() {
        mAutocompleteList = mProductService.getAutoComplete();
        AutocompleteAdapter adapter = new AutocompleteAdapter(getContext(), R.layout.spinner_dropdown_item_layout, mAutocompleteList);
        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.setThreshold(1);
        adapter.setOnClickListener(new AutocompleteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Log.d(TAG, "Input user: " + text);
                if (!text.equals("")) {
                    addProductToPantry(text);
                    buildAgainList();
                }

            }

        });
    }

    private void setOnListener() {
        mOpenDrawer.setOnClickListener(this);
        mImageMenu.setOnClickListener(this);
        mAutoCompleteTextView.setOnEditorActionListener(this);
        mSpinner.setOnItemSelectedListener(this);
        mButtonAdd.setOnClickListener(this);
        mMicrophone.setOnClickListener(this);
        mInputAdd.setOnClickListener(this);
        mInputDelete.setOnClickListener(this);
        mAutoCompleteTextView.addTextChangedListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        CustomDialog customDialog = new CustomDialog(getContext());
        DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(getContext());
        switch (menuItem.getItemId()) {
            case R.id.action_create_list:
                dialogCustomLayout.onCreate(getString(R.string.dialog_message_create_list), "");
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if (mPantryListService.checkBeforeUpdateList(text)) {
                            mPantryListService.createPantryList(text);
                            activeFragment(new PantryListFragment());
                        } else {
                            hideSoftKeyBoard();
                            Toast.makeText(getContext(),getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                return true;
            case R.id.action_delete_all:
                customDialog.onCreate(getString(R.string.dialog_message_confirm_delete_all_item)
                        , getString(R.string.abc_delete), getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mPantryListService.clearAllList(mPantryList);
                        buildAgainList();
                    }

                });

                return true;
            case R.id.action_check_all:
                mDataChecked.clear();
                for (Product product : mData) {
                    if (product.getName() != null) {
                        mDataChecked.add(product.getId());
                    }
                }
                buildAgainList();
                return true;
            case R.id.action_uncheck_all:
                mDataChecked.clear();
                buildAgainList();
                return true;
            case R.id.action_rename_list:
                dialogCustomLayout.onCreate(getString(R.string.dialog_title_rename_list), mPantryList.getName());
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if (mPantryListService.checkBeforeUpdateList(text)) {
                            mPantryList.setName(text);
                            mPantryListService.updateList(mPantryList);
                            activeFragment(new PantryListFragment());
                        } else {
                            hideSoftKeyBoard();
                            Toast.makeText(getContext(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                return true;
            case R.id.action_delete_list:
                customDialog.onCreate(getString(R.string.dialog_message_confirm_delete_list)
                        , getString(R.string.abc_delete), getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mPantryListService.deleteList(mPantryList);
                        activeFragment(new PantryListFragment());
                    }

                });

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.open_drawer:
                MainActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_image:
                PopupMenu popup = new PopupMenu(view.getContext(), mImageMenu);
                popup.inflate(R.menu.pantry_list);
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            case R.id.pantry_list_button_add:
                String nameList = mSpinnerShopping.getSelectedItem().toString();
                final ShoppingList shoppingList = mShoppingListService.getListByName(nameList);
                Log.d(TAG, "Add prduct to shopping list, amount : " + mDataChecked.size());
                if (mDataChecked.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setMessage(getResources().getString(R.string.dialog_message_no_item_chooseed));
                    builder.setNeutralButton("OK", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    final ArrayList<String> listProduct = new ArrayList<>();
                    for (Product product : mData) {
                        if (mDataChecked.contains(product.getId())) {
                            listProduct.add(product.getName());
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getResources().getString(R.string.dialog_message_pantry_list_to_shopping_list));
                    builder.setPositiveButton(getResources().getString(R.string.abc_done), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mPantryListService.addProductToShopping(listProduct, shoppingList);
                            activeFragment(new ShoppingListFragment());
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
            case R.id.microphone:
                startVoiceInput();
                break;
            case R.id.input_add:
                String text = mAutoCompleteTextView.getText().toString();
                if (!text.equals("")) {
                    addProductToPantry(text);
                    buildAgainList();
                    Log.d(TAG, "Add product: " + text);
                }
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
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            String text = textView.getText().toString().trim();
            Log.d(TAG, "Input user" + text);
            if (!text.equals("")) {
                addProductToPantry(text);
                buildAgainList();
            }
        }
        return false;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String text = (String) adapterView.getItemAtPosition(position);
        mPantryList = mPantryListService.activeList(text);
        if (iCurrentSelection != position) {
            activeFragment(new PantryListFragment());
        }
        iCurrentSelection = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //nothing
    }

    public void buildAgainList() {
        mData = mPantryListService.productPantry(mPantryList);
        if (this.mData.size() == 0) {
            mGuide.setVisibility(View.VISIBLE);
        } else {
            mGuide.setVisibility(View.GONE);
        }
        if (mDataChecked.size() == 0) {
            mLayoutBottom.setVisibility(View.GONE);
        } else {
            mLayoutBottom.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    private Product addProductToPantry(String text) {
        mAutoCompleteTextView.setText("");
        return mPantryListService.addProductToPantry(text, mPantryList);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "on text change: " + charSequence);
        if (charSequence.toString().equals("")) {
            Log.d(TAG, "Show input type typing");
            mLayoutInputTypeTypping.setVisibility(View.VISIBLE);
            mLayoutInputTextUpdate.setVisibility(View.GONE);
        } else {
            mLayoutInputTypeTypping.setVisibility(View.GONE);
            mLayoutInputTextUpdate.setVisibility(View.VISIBLE);
            Log.d(TAG, "Show input text update");
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
