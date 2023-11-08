package com.example.baseproject.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.baseproject.R;

public class PermissionDialog extends Dialog  implements View.OnClickListener {
    TextView btnNext;
    private OnClickButton onClickButton;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                this.onClickButton.success();
                break;
            case R.id.viewDialog:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface OnClickButton {
        void success();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_permission);
    }

    public PermissionDialog(@NonNull Context context2, int i,OnClickButton onClick) {
        super(context2, i);
        getWindow().setSoftInputMode(16);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = -1;
        attributes.height = -1;
        attributes.gravity = 17;
        getWindow().setAttributes(attributes);
        getWindow().setSoftInputMode(16);
        this.onClickButton = onClick;

    }
}