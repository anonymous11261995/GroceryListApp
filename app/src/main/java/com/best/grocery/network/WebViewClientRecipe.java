package com.best.grocery.network;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.best.grocery.R;
import com.best.grocery.fragment.WebViewRecipeFragment;
import com.best.grocery.entity.Recipe;


/**
 * Created by TienTruong on 8/2/2018.
 */

@SuppressWarnings("IndexOfReplaceableByContains")
public class WebViewClientRecipe extends WebViewClient {
    private static final String TAG = WebViewClientRecipe.class.getSimpleName();
    public static Recipe mRecipe;
    private static String[] cookpadPathUrlrecipe = {"/recipes", "/recipe", "/recetas", "/cong-thuc", "/resep", "/recettes", "/receitas", "/rezepte", "/食譜", "/kr", "ricette", "/ir", "/receptek", "/opskrifter",
            "/przepisy", "/recept", "/recepty", "/sintages", "/sa/", "/kw/", "/qa/", "/bh/", "/om/", "/ae/", "/ye/", "/dj/", "/so/", "/km/", "/eg/", "/sd/", "/dz/", "/ly/", "/tn/", "/ma/", "/mr/", "/lb/", "/sy/", "/jo/", "/iq/", "/ps/"};

    // Khi bạn click vào link bên trong trình duyệt (Webview)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.indexOf("https://") != -1) {
            view.loadUrl(url);
        }
        Log.i(TAG, "Click on any interlink on webview that time you got url :" + url);
        return true;
    }


    // Khi trang bắt đầu được tải
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        WebViewRecipeFragment.mProgressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "Your current url when webpage loading.." + url);
        if (url.indexOf("https://") != -1) {
            super.onPageStarted(view, url, favicon);
            changeStatusButton(url);
            WebViewRecipeFragment.mHeaderUrl.setText(url);
            String title = view.getTitle();
            if (!title.equals("")) WebViewRecipeFragment.mHeaderText.setText(title);
        }

    }


    // Khi trang tải xong
    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG, "Your current url when webpage loading.. finish: " + url);
        WebViewRecipeFragment.mProgressBar.setVisibility(View.GONE);
        super.onPageFinished(view, url);
        String title = view.getTitle();
        if (!title.equals("")) WebViewRecipeFragment.mHeaderText.setText(title);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
//        Log.i(TAG, "Url load resoure...." + url);
        boolean isXpath = false;
        for (String text : cookpadPathUrlrecipe) {
            if (url.indexOf("https://cookpad.com/") != -1 && url.indexOf(text) != -1) {
                Log.i(TAG, "Load resource > Xpath url : " + url);
                isXpath = true;
                break;
            }
        }
        if (isXpath) {
            String title = view.getTitle();
            if (!title.equals("")) WebViewRecipeFragment.mHeaderText.setText(title);
            WebViewRecipeFragment.mHeaderUrl.setText(url);
            changeStatusButton(url);
            WebViewRecipeFragment.mProgressBar.setVisibility(View.GONE);
        }

    }


    private void changeStatusButton(String url) {
        Recipe recipe = WebViewRecipeFragment.mRecipeService.htmlToRecipe(url);
        if (recipe == null) {
            Log.d(TAG, "Page not format recipe");
            WebViewRecipeFragment.mButtonAdd.setVisibility(View.GONE);

        } else {
            Log.d(TAG, "Page format recipe");
            mRecipe = recipe;
            WebViewRecipeFragment.mButtonAdd.setVisibility(View.VISIBLE);
            WebViewRecipeFragment.mButtonAdd.setBackgroundResource(R.color.colorPrimaryDark);
            WebViewRecipeFragment.mButtonAdd.setText(R.string.abc_save);
            WebViewRecipeFragment.mButtonAdd.setEnabled(true);
        }
    }
}
