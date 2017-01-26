package com.marchesi.federico.contagomme;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class AddBrandDialog {

    private final Context context;
    private final View dialogView;
    private final View okBtn, cancelBtn;
    private final EditText editName;
    private final EditText editDescr;
    private final int title;

    private InputListener inputListener = null;

    public AddBrandDialog(Context context, int title, int hint) {
        this.context = context;
        this.dialogView = LayoutInflater.from(context).inflate(R.layout.input_dialog, null);
        this.title = title;
        this.editName = (EditText) dialogView.findViewById(R.id.input);
        this.editDescr = (EditText) dialogView.findViewById(R.id.coffe_type_descr);
        editName.setHint(hint);
        this.okBtn = dialogView.findViewById(R.id.btn_ok);
        this.cancelBtn = dialogView.findViewById(R.id.btn_cancel);
    }

    public AddBrandDialog(Context context, int title, int hint, String defaultName, String defaultDescr) {
        this.context = context;
        this.dialogView = LayoutInflater.from(context).inflate(R.layout.input_dialog, null);
        this.title = title;
        this.editName = (EditText) dialogView.findViewById(R.id.input);
        this.editDescr = (EditText) dialogView.findViewById(R.id.coffe_type_descr);
        editName.setHint(hint);
        this.okBtn = dialogView.findViewById(R.id.btn_ok);
        this.cancelBtn = dialogView.findViewById(R.id.btn_cancel);

        if (!defaultName.isEmpty()) {
            editName.setText(defaultName);
        }
        if (!defaultDescr.isEmpty()) {
            editDescr.setText(defaultDescr);
        }
    }

    public void setInputListener(final InputListener inputListener) {
        this.inputListener = inputListener;
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ValidationResult result = inputListener.isInputValid(editName.getText().toString());
                okBtn.setEnabled(result.isValid);
            }
        });
    }

    public void setInitialInput(String initialInput) {
        this.editName.setText(initialInput);
    }

    public void show() {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(dialogView)
                .show();
        editName.requestFocus();

        final InputMethodManager inputMethodManager = (InputMethodManager) dialogView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputListener != null)
                    inputListener.onConfirm(editName.getText().toString(), editDescr.getText().toString());
                inputMethodManager.hideSoftInputFromWindow(editName.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(editName.getWindowToken(), 0);
                dialog.dismiss();
            }
        });

    }

    public interface InputListener {

        ValidationResult isInputValid(String input);

        void onConfirm(String input, String descr);

    }

    public static class ValidationResult {

        public final boolean isValid;

        public final int errorMsg;

        public ValidationResult(boolean isValid, int errorMsg) {
            this.isValid = isValid;
            this.errorMsg = errorMsg;
        }

    }

}
