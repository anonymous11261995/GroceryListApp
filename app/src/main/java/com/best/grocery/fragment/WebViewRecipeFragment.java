package com.best.grocery.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.best.grocery.R;
import com.best.grocery.network.WebViewClientRecipe;
import com.best.grocery.service.RecipeService;
import com.best.grocery.utils.DefinitionSchema;


/**
 * Created by TienTruong on 8/2/2018.
 */

@SuppressWarnings("ConstantConditions")
public class WebViewRecipeFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = WebViewRecipeFragment.class.getSimpleName();
    public static Button mButtonAdd;
    public static TextView mHeaderText;
    public static TextView mHeaderUrl;
    private ImageView mBack;
    private String mUrl;
    private WebViewClientRecipe mWebViewClientRecipe;
    public static WebView mWebView;
    public static RecipeService mRecipeService;
    public static ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        Log.d(TAG, "Load fragment webview url: " + mUrl);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebViewClientRecipe = new WebViewClientRecipe();
        mRecipeService = new RecipeService(getContext());
        initViews();
        setOnListener();
    }

    private void initViews() {
        mProgressBar = getView().findViewById(R.id.determinateBar);
        mProgressBar.getProgressDrawable().setColorFilter(
                ContextCompat.getColor(getContext(), R.color.webview_process_bar), android.graphics.PorterDuff.Mode.SRC_IN);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(1);
        mHeaderText = getView().findViewById(R.id.webview_header_text);
        mHeaderUrl = getView().findViewById(R.id.webview_header_url);
        mHeaderUrl.setText(mUrl);
        mBack = getView().findViewById(R.id.webview_back);
        mButtonAdd = getView().findViewById(R.id.webview_add);
        mButtonAdd.setVisibility(View.GONE);
        mWebView = getView().findViewById(R.id.webview_content);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //improve webView performance
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);
        if (mUrl != null) {
            if (mUrl.indexOf("https://") != -1)
                mWebView.loadUrl(mUrl);
        }
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setProgress(progress);
            }
        });
        mWebView.setWebViewClient(mWebViewClientRecipe);
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }

                return false;
            }
        });
    }

    private void setOnListener() {
        mBack.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.webview_back:
                activeFragment(new RecipeBookFragment());
                break;
            case R.id.webview_add:
//                String urlCurrent = mWebViewClientRecipe.getUrlCurrent();
//                Log.d(TAG, "URL recipe: " + urlCurrent);
                boolean status = mRecipeService.addRecipeBook(WebViewClientRecipe.mRecipe, getContext());
                if (status) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage(getResources().getString(R.string.dialog_message_recipe_book_add_success));
                    alert.setPositiveButton(getResources().getString(R.string.abc_done), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activeFragment(new RecipeBookFragment());
                        }
                    });
                    alert.setNegativeButton(getResources().getString(R.string.abc_continue), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage(getResources().getString(R.string.dialog_message_error));
                    alert.setPositiveButton(getResources().getString(R.string.abc_done), null);
                    alert.show();
                }
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

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
