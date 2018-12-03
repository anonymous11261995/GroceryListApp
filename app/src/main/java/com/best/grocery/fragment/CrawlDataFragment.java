package com.best.grocery.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;
import com.best.grocery.service.ProductService;
import com.best.grocery.service.ShoppingListService;
import com.best.grocery.utils.DefinitionSchema;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;

public class CrawlDataFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();
    private Spinner mSpinnerList;
    private CardView mCardView;
    private TextView mTitle;
    private TextView mDescription;
    private ImageView mImageViewAddList;
    private Button mButtonAdd;
    private String textTitle;
    private String textDescription;
    private ShoppingListService mShoppingListService;
    private ProductService mProductService;
    private String mTextRecive;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crawl_data, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShoppingListService = new ShoppingListService(getContext());
        mProductService = new ProductService(getContext());
        initViews();
        setOnListener();
        hideSoftKeyBoard();
        onBackPressed();

    }

    private void initViews() {
        mSpinnerList = getView().findViewById(R.id.spinner);
        initSpinner();
        mCardView = getView().findViewById(R.id.cardview_crawlproduct);
        mCardView.setVisibility(View.GONE);
        mTitle = getView().findViewById(R.id.edittext_title);
        mDescription = getView().findViewById(R.id.textview_description);
        setValueText();
        mButtonAdd = getView().findViewById(R.id.button_add_item);
        mImageViewAddList = getView().findViewById(R.id.imageview_crawlproduct_add_list);

    }


    private void setOnListener() {
        mButtonAdd.setOnClickListener(this);
        mImageViewAddList.setOnClickListener(this);

    }

    private void setValueText() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.dialog_message_processing));
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = mTextRecive;
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Connection.Response response = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                            .referrer("http://www.google.com")
                            .method(Connection.Method.GET) //or Method.POST
                            .execute();
                    Document document = response.parse();
                    textTitle = mProductService.readHtml(document);
                    textDescription = url;

                } catch (Exception e) {
                    Log.e("Error", "[ClipboardFragment] : " + e.getMessage());
                }
                progressDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (textTitle == null) {
                            Toast.makeText(getContext(), "Could not add item to BList. The shared content did not contain a valid web URL", Toast.LENGTH_LONG).show();
                            mCardView.setVisibility(View.GONE);
                        } else {
                            mCardView.setVisibility(View.VISIBLE);
                            mTitle.setText(textTitle);
                            mDescription.setText(textDescription);
                        }
                    }
                });
            }
        });
        t.start();
        progressDialog.show();
    }


    private void initSpinner() {
        ArrayList<ShoppingList> shoppingLists = mShoppingListService.getAllShoppingList();
        ArrayList<String> arrayList = new ArrayList<>();
        for (ShoppingList sl : shoppingLists) {
            arrayList.add(sl.getName());
        }
        Collections.reverse(arrayList);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_recipe_detail, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        mSpinnerList.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imageview_crawlproduct_add_list:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View alertView = inflater.inflate(R.layout.view_dialog_create, null);
                TextView titleView = alertView.findViewById(R.id.dialog_create_title);
                titleView.setText(getResources().getString(R.string.dialog_message_create_list));
                builder.setView(alertView);
                builder.setPositiveButton(getResources().getString(R.string.abc_create), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editView = alertView.findViewById(R.id.dialog_create_content);
                        String name = editView.getText().toString().trim();
                        if (mShoppingListService.checkBeforeUpdateList(name)) {
                            mShoppingListService.createNewListShopping(name);
                        }
                        else {
                            hideSoftKeyBoard();
                            Toast.makeText(getContext(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                        initSpinner();

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
            case R.id.button_add_item:
                String nameList = mSpinnerList.getSelectedItem().toString();
                if (textTitle != null) {
                    Product product = new Product();
                    double price = 0.0;
                    String name = textTitle;
                    String words[] = textTitle.split(" ");
                    for (String text : words) {
                        if (text.indexOf(CURRENCY_DEFUALT) != -1) {
                            price = Double.valueOf(text.replace(",", "").replace(CURRENCY_DEFUALT, ""));
                            name = textTitle.substring(0, textTitle.indexOf(text));
                            break;
                        }
                    }
                    product.setName(name);
                    product.setUnitPrice(price);
                    product.setUrl(textDescription);
                    mShoppingListService.addProductCrawled(nameList, product);
                    builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setMessage(getResources().getString(R.string.dialog_message_add_item_website_sucess));
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
                    alert = builder.create();
                    alert.show();
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

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setTextRecive(String text) {
        this.mTextRecive = text;
    }
}
