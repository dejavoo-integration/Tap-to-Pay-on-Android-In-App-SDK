package com.denovo.app.invokeiposgosample.utility;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.widget.AppCompatEditText;

/*import com.denovo.app.denovopay.uilayer.interfaces.AmountEntryListener;*/

public class AmountEntryTxtWatcher implements TextWatcher {

    private String current = "";
    /*private int subStringCounter = 0;*/
    private final AppCompatEditText appCompatEditText;
    /*private AmountEntryListener amountEntryListener = null;*/

    /*public void setAmountEntryListener(AmountEntryListener amountEntryListener) {
        this.amountEntryListener = amountEntryListener;
    }*/

    public AmountEntryTxtWatcher(AppCompatEditText appCompatEditText) {
        this.appCompatEditText = appCompatEditText;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(current)) {
            appCompatEditText.removeTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            String edtValue = appCompatEditText.getText().toString();
            edtValue = edtValue.replace(".", "");
            int length = edtValue.length();

            String data;

            if (length == 3) {
                data = insertDecimalPoint(/*"0" +*/ edtValue);
                /*checkForAmountClear(data);*/
            } else if (length == 2) {
                data = insertDecimalPoint("0" + edtValue);
                /*checkForAmountClear(data);*/
            } else if (length == 1) {
                data = insertDecimalPoint("00" + edtValue);
            } else if (edtValue.startsWith("0")) {
                edtValue = edtValue.substring(1);
                data = insertDecimalPoint(edtValue);
                /*if (data.startsWith("0.0")) {
                    onAmountTypingStarted();
                }*/
            } else {
                data = insertDecimalPoint(edtValue);
            }

            current = data;
            appCompatEditText.setText(data);
            appCompatEditText.setSelection(data.length());
        }

        appCompatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private String insertDecimalPoint(String edtValue) {
        int length = edtValue.length();
        if (length > 2) {
            int position = length - 2;
            return addChar(edtValue, position);
        } else {
            return edtValue;
        }
    }


    public String addChar(String str, int position) {
        return str.substring(0, position) + "." + str.substring(position);
    }

    public void setSubStringCounterZero() {
        /* subStringCounter = 0;*/
    }


    /*private void checkForAmountClear(String data) {
        if (data.equalsIgnoreCase("0.00")) {
            onAmountCleared();
        }
    }*/


    /*private void onAmountTypingStarted() {
        if (amountEntryListener != null) {
            amountEntryListener.onAmountTypingStarted();
        }
    }

    private void onAmountCleared() {
        if (amountEntryListener != null) {
            amountEntryListener.onAmountCleared();
        }
    }*/

}
