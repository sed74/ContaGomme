package com.sed.willy.contagomme.Dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.sed.willy.contagomme.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InputDialogRace {

    final Calendar myCalendar = Calendar.getInstance();
    private final Context context;
    private final View dialogView;
    private final View okBtn, cancelBtn;
    private final EditText editName;
    private final EditText editDescr;
    private final EditText editDate;
    private final int title;

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();

        }
    };

    View.OnClickListener dateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new DatePickerDialog(context, onDateSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };
    private InputListener inputListener = null;

    public InputDialogRace(final Context context, int title, int hint) {
        this.context = context;
        this.dialogView = LayoutInflater.from(context).inflate(R.layout.input_dialog_race, null);
//        dialogView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        this.title = title;
        this.editName = (EditText) dialogView.findViewById(R.id.input);
        this.editDescr = (EditText) dialogView.findViewById(R.id.race_descr);
        this.editDate = (EditText) dialogView.findViewById(R.id.race_date);
        editDate.setOnClickListener(dateListener);
//        editName.setHint(hint);
        this.okBtn = dialogView.findViewById(R.id.btn_ok);
        this.cancelBtn = dialogView.findViewById(R.id.btn_cancel);
        okBtn.setEnabled(false);
    }

    public void setRaceName(String raceName) {

        editName.setText(raceName);
        okBtn.setEnabled(!raceName.isEmpty());
    }

    public void setRaceDate(String raceDate) {
        editDate.setText(raceDate);
    }

    public void setRaceDescr(String raceDescr) {
        editDescr.setText(raceDescr);
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
                if (s.length() > 0) {
                    ValidationResult result = inputListener.isInputValid(s.toString());
                    okBtn.setEnabled(result.isValid);
                } else
                    okBtn.setEnabled(false);

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

        final InputMethodManager inputMethodManager = (InputMethodManager)
                dialogView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputListener != null) {
                    String raceName = editName.getText().toString();
                    String raceDescr = editDescr.getText().toString();
                    String raceDate = editDate.getText().toString();
                    inputListener.onConfirm(raceName, raceDescr, raceDate);
                }
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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);

        editDate.setText(sdf.format(myCalendar.getTime()));
    }

    public interface InputListener {

//        ValidationResult isInputValid(String input);

        ValidationResult isInputValid(String input);

        void onConfirm(String raceName, String raceDescr, String raceDate);

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
