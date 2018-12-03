package com.best.grocery.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.best.grocery.AppConfig;
import com.best.grocery.R;
import com.best.grocery.utils.DefinitionSchema;

public class ClipboardFragment extends Fragment implements DefinitionSchema, View.OnClickListener {
    private static final String TAG = ClipboardFragment.class.getSimpleName();
    private ImageView mButtonBack;
    private EditText mEditTextData;
    private Button mButtonImport;
    private Button mButtonClear;
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clipboard, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        setOnListener();
        hideSoftKeyBoard();
        hideSoftKeyBoard();
    }


    private void initViews() {
        mButtonBack = getView().findViewById(R.id.clipboard_button_back);
        mEditTextData = getView().findViewById(R.id.clipboard_data);
        mButtonImport = getView().findViewById(R.id.clipboard_button_import);
        mButtonClear = getView().findViewById(R.id.clipboard_button_clear);
        setTextCopy();
    }

    private void setTextCopy() {
        try {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
            String text = clipboard.getText().toString();
            if (text.indexOf("ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE)") == -1)
                mEditTextData.setText(text);
        } catch (Exception e) {
            mEditTextData.setText("");
        }
    }


    private void setOnListener() {
        mButtonBack.setOnClickListener(this);
        mButtonImport.setOnClickListener(this);
        mButtonClear.setOnClickListener(this);
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
        switch (id) {
            case R.id.clipboard_button_back:
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment(new ShoppingListFragment());
                    }
                }, AppConfig.DELAY_EFFECT);
                break;

            case R.id.clipboard_button_import:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mEditTextData.getText().toString().equals("")) {
                            Builder builder = new Builder(getActivity());
                            builder.setCancelable(true);
                            builder.setMessage(getResources().getString(R.string.dialog_message_clipboard_no_data));
                            builder.setNeutralButton("OK", null);
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {

                            ClipboardProductFragment fragment = new ClipboardProductFragment();
                            fragment.setData(mEditTextData.getText().toString());
                            activeFragment(fragment);
                        }
                    }
                }, AppConfig.DELAY_EFFECT);
                break;

            case R.id.clipboard_button_clear:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                        clipboard.setText("");
                        mEditTextData.setText("");
                    }
                }, AppConfig.DELAY_EFFECT);

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

}
