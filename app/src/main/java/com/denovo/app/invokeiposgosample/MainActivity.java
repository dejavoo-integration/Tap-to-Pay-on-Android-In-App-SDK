package com.denovo.app.invokeiposgosample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.denovo.app.invokeiposgosample.utility.AmountEntryTxtWatcher;
import com.denovo.app.invokeiposgosample.utility.MainUtils;
import com.denovo.app.top.business.tap_on_phone.host_config.TopTxn;
import com.denovo.app.top.business.tap_on_phone.host_config.TopTxnManager;
import com.denovo.app.top.uilayer.sdk.ToPService;
import com.denovo.app.top.uilayer.sdk.exceptions.TransactionException;
import com.denovo.app.top.uilayer.sdk.utils.TOPParams;
import com.denovo.app.top.uilayer.splash.UISplashScreen;
import com.denovo.app.top.utility.CommonAppPreference;
import com.denovo.app.top.utility.Constants;
import com.denovo.app.top.utility.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Dplog";
    private final String SESSION_KEY = "SESSION_KEY";
    private Context context = null;
    /*TopTxnManager*/

    private TabLayout tabLayout;
    private LinearLayout amountLinear;
    private TextInputEditText tpnEditText, merchantKeyEditText, amountEditText;
    private AppCompatTextView registerResultTxt, transactionResultTxt;
    private AppCompatButton registerButton, transactionButton;
    private CheckBox getFullCardNumberCheck,receiptCheck,approvalScreenCheck;

    private int selectedTabPosition = 0;
    private CommonAppPreference preference = null;
    private ToPService toPService = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        tpnEditText = findViewById(R.id.ma_tpnEditText);
        merchantKeyEditText = findViewById(R.id.ma_merchantKeyEditText);
        registerButton = findViewById(R.id.ma_registerButton);
        registerResultTxt = findViewById(R.id.ma_registerResultTxt);

        tabLayout = findViewById(R.id.tabLayout);
        amountLinear = findViewById(R.id.ma_amountLinear);
        amountEditText = findViewById(R.id.ma_amountEditText);
        getFullCardNumberCheck = findViewById(R.id.ma_getFullCardNumberCheck);
        receiptCheck = findViewById(R.id.ma_receiptCheck);
        approvalScreenCheck = findViewById(R.id.ma_approvalScreenCheck);


        transactionButton = findViewById(R.id.ma_transactionButton);
        transactionResultTxt = findViewById(R.id.ma_transactionResultTxt);


        amountEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});///come
        AmountEntryTxtWatcher amountEntryTxtWatcher = new AmountEntryTxtWatcher(amountEditText);
        amountEditText.addTextChangedListener(amountEntryTxtWatcher);

        preference = new CommonAppPreference(context);

        toPService = new ToPService(MainActivity.this);
        registerButton.setOnClickListener(view -> {
            //if (tpn.length() == 12 && merchantKey.length() == 12) {
            if (new MainUtils().isPermissionGranted(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, Constants.LOCATION_PERMISSION)) {

                registerDevice(toPService);
            }
            /*} else {
                setRegisterResultTxt("Enter valid TPN & Merchant Key");
            }*/
        });


        transactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = getTxnTypeBasedOnSelection(selectedTabPosition);
                switch (type) {
                    case TOPParams.SALE:
                        startTransaction(TOPParams.SALE,toPService);
                        break;
                    case TOPParams.VOID:
                        startTransaction(TOPParams.VOID,toPService);
                        break;
                    case TOPParams.REFUND:
                        startTransaction(TOPParams.REFUND,toPService);
                        break;
                    case TOPParams.TIP_ADJUSTMENT:
                        showTipPage();
                        break;
                    case TOPParams.ADMINISTRATIVE_TXN:
                        startTransaction(TOPParams.ADMINISTRATIVE_TXN,toPService);
                        break;
                    case TOPParams.PRE_AUTH:
                        startTransaction(TOPParams.PRE_AUTH,toPService);
                        break;
                    case TOPParams.TICKET:
                        setTransactionResultTxt("Not yet implemented");
                        break;
                    case TOPParams.BATCH:
                        showBatchPage();
                        break;
                    case TOPParams.RECENT_TXN:
                        show_recent_txn();

                    default:
                        break;
                }
            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    private void registerDevice(ToPService toPService) {
        String tpn = tpnEditText.getText().toString();
        String merchantKey = merchantKeyEditText.getText().toString();
        ProgressDialog progressDialog = ProgressDialog.show(context, " Register", "", true);
        toPService.registerDevice(tpn, merchantKey, new ToPService.OnRegisterListener() {
            @Override
            public void onRegisterSuccess(JSONObject jsonObject) {
                Log.e(TAG, "onRegisterSuccess-" + jsonObject.toString());
                progressDialog.dismiss();

                setRegisterResultTxt("" + jsonObject.toString());
                String sessionKey = jsonObject.optString("session_key");
                if (preference != null) {
                    preference.putString(SESSION_KEY, sessionKey);
                }
                /*startTransaction("",toPService);*/
            }

            @Override
            public void onProcess(String s) {
                progressDialog.setMessage(s);
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }

           /* @Override
            public void onProcess(String s) {
                progressDialog.setMessage(s);
            }*/

         /*   @Override
            public void onProcess(String s) {
                progressDialog.setMessage(s);
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }*/


            @Override
            public void onRegisterError(JSONObject jsonObject) {
                Log.e(TAG, "onRegisterError-" + jsonObject.toString());
                progressDialog.dismiss();

                setRegisterResultTxt("" + jsonObject.toString());
            }
        });
    }

    private void setRegisterResultTxt(String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registerResultTxt.setText(content);
            }
        });
    }



    private void startTransaction(String type,ToPService toPService){
        try {
            String amount = amountEditText.getText().toString();
            JSONObject jsonRequest = null;
            JSONObject jsonObject = new JSONObject();
            String sessionKey = preference.getString(SESSION_KEY, "");
            if ((type.equalsIgnoreCase(TOPParams.SALE)  || type.equalsIgnoreCase(TOPParams.REFUND) || type.equalsIgnoreCase(TOPParams.PRE_AUTH)) &&
            !amount.equals("0.00")) {

            if (type.equalsIgnoreCase(TOPParams.SALE)){
                jsonObject.put("type", TOPParams.SALE);
                /*jsonObject.put("lang", TOPParams.HEBREW);*/
                jsonObject.put("amount", amount);
                /*jsonObject.put(TOPParams.UN_MASKED_PAN_NEEDED, isFullCardNumberNeeded());*/
                jsonObject.put(TOPParams.IS_RECEIPTS_NEEDED, isReceiptNeededInResponse());
                jsonObject.put(TOPParams.SHOW_APPROVAL_SCREEN, showApprovalScreen());
                jsonRequest =  getCustomObject(jsonObject);
            }else if (type.equalsIgnoreCase(TOPParams.REFUND)){
                jsonObject.put("type", TOPParams.REFUND);
                jsonObject.put("amount", amount);
                /*jsonObject.put(TOPParams.UN_MASKED_PAN_NEEDED, isFullCardNumberNeeded());*/
                jsonObject.put(TOPParams.IS_RECEIPTS_NEEDED, isReceiptNeededInResponse());
                jsonObject.put(TOPParams.SHOW_APPROVAL_SCREEN, showApprovalScreen());
                jsonRequest =  getCustomObject(jsonObject);
            }else if (type.equalsIgnoreCase(TOPParams.PRE_AUTH)){
                jsonObject.put("type", TOPParams.PRE_AUTH);
                /*jsonObject.put("lang", TOPParams.HEBREW);*/
                jsonObject.put("amount", amount);
                /*jsonObject.put(TOPParams.UN_MASKED_PAN_NEEDED, isFullCardNumberNeeded());*/
                jsonObject.put(TOPParams.IS_RECEIPTS_NEEDED, isReceiptNeededInResponse());
                jsonObject.put(TOPParams.SHOW_APPROVAL_SCREEN, showApprovalScreen());
                jsonRequest =  getCustomObject(jsonObject);
            }
/*
                ProgressDialog progressDialog = ProgressDialog.show(context, " Processing Transaction", "", true);
*/

            toPService.performTransaction(jsonRequest, sessionKey, new ToPService.OnTransactionListener() {
                /*@Override
                public void onTransactionResponse(JSONObject jsonObject) {

                    setTransactionResultTxt("" + jsonObject.toString());
                    Log.d("mResponse",jsonObject.toString());

                }*/
                @Override
                public void onTransactionSuccess(JSONObject jsonObject) {
                    Utils.logPrint('E',"onTransactionSuccess--"+jsonObject.toString());
                    setTransactionResultTxt("" + jsonObject.toString());
                }

                @Override
                public void onTransactionError(JSONObject jsonObject) {
                    Utils.logPrint('E',"onTransactionError--"+jsonObject.toString());
                    setTransactionResultTxt("" + jsonObject.toString());
                }
                @Override
                public void onRegisterNeeded(JSONObject jsonObject) {
                    Utils.logPrint('E',"onRegisterNeeded--"+jsonObject.toString());
                    setTransactionResultTxt("" + jsonObject.toString());
                    /*registerDevice(toPService);*/

                }
            });

            }
            else if (type.equalsIgnoreCase(TOPParams.VOID)){
                jsonObject.put("type", TOPParams.VOID);
                /*jsonObject.put(TOPParams.UN_MASKED_PAN_NEEDED, isFullCardNumberNeeded());*/
                jsonRequest =  getCustomObject(jsonObject);
                toPService.performTransaction(jsonRequest, sessionKey, new ToPService.OnTransactionListener() {
                    /*@Override
                    public void onTransactionResponse(JSONObject jsonObject) {

                        setTransactionResultTxt("" + jsonObject.toString());
                        Log.d("mResponse",jsonObject.toString());

                    }*/
                    @Override
                    public void onTransactionSuccess(JSONObject jsonObject) {
                        Utils.logPrint('E',"onTransactionSuccess--"+jsonObject.toString());
                        setTransactionResultTxt("" + jsonObject.toString());
                    }

                    @Override
                    public void onTransactionError(JSONObject jsonObject) {
                        Utils.logPrint('E',"onTransactionError--"+jsonObject.toString());
                        setTransactionResultTxt("" + jsonObject.toString());
                    }
                    @Override
                    public void onRegisterNeeded(JSONObject jsonObject) {
                        Utils.logPrint('E',"onRegisterNeeded--"+jsonObject.toString());
                        setTransactionResultTxt("" + jsonObject.toString());
                        /*registerDevice(toPService);*/

                    }
                });

            }
            else if (type.equalsIgnoreCase(TOPParams.ADMINISTRATIVE_TXN)){
                jsonObject.put("type", TOPParams.ADMINISTRATIVE_TXN);
                /*jsonObject.put("lang", TOPParams.HEBREW);*/
                /*jsonObject.put("amount", amount);*/
                /*jsonObject.put(TOPParams.UN_MASKED_PAN_NEEDED, isFullCardNumberNeeded());*/
                jsonObject.put(TOPParams.IS_RECEIPTS_NEEDED, isReceiptNeededInResponse());
                jsonObject.put(TOPParams.SHOW_APPROVAL_SCREEN, showApprovalScreen());
                jsonRequest =  getCustomObject(jsonObject);
                toPService.performTransaction(jsonRequest, sessionKey, new ToPService.OnTransactionListener() {
                    /*@Override
                    public void onTransactionResponse(JSONObject jsonObject) {

                        setTransactionResultTxt("" + jsonObject.toString());
                        Log.d("mResponse",jsonObject.toString());

                    }*/
                    @Override
                    public void onTransactionSuccess(JSONObject jsonObject) {
                        Utils.logPrint('E',"onTransactionSuccess--"+jsonObject.toString());
                        setTransactionResultTxt("" + jsonObject.toString());
                    }

                    @Override
                    public void onTransactionError(JSONObject jsonObject) {
                        Utils.logPrint('E',"onTransactionError--"+jsonObject.toString());
                        setTransactionResultTxt("" + jsonObject.toString());
                    }
                    @Override
                    public void onRegisterNeeded(JSONObject jsonObject) {
                        Utils.logPrint('E',"onRegisterNeeded--"+jsonObject.toString());
                        setTransactionResultTxt("" + jsonObject.toString());
                        /*registerDevice(toPService);*/

                    }
                });

            }
            else {
                setTransactionResultTxt("Enter the Amount");
            }

        } catch (JSONException | TransactionException e) {
            e.printStackTrace();
            setTransactionResultTxt("Exp:" + e.getMessage());
        }
    }

    private JSONObject getCustomObject(JSONObject jsonObject) throws JSONException{
        JSONObject customObject = new JSONObject();
        customObject.put("CustomerEmail","graghu@denovosystem.com");
        customObject.put("PhoneNumber","919840720372");
        customObject.put("CreditType","1");
        customObject.put("NumberOfPayments","1");
        customObject.put("ExtraData","[]");
        customObject.put("HolderID","1");
        customObject.put("TransactionUniqueIdForQuery", UUID.randomUUID().toString());
        jsonObject.put("CustomObject",customObject);
        return jsonObject;
    }




    private void setTransactionResultTxt(String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                transactionResultTxt.setText(content);
            }
        });
    }

    private void showBatchPage(){
        ToPService toPServicess = new ToPService(MainActivity.this);
        String sessionKey = preference.getString(SESSION_KEY, "");
        toPServicess.showBatch(sessionKey, new ToPService.OnBatchViewListener() {
            @Override
            public void onBatchViewSuccess(JSONObject jsonObject) {
                Utils.logPrint('E',"onBatchViewSuccess--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }

            @Override
            public void onBatchViewFailed(JSONObject jsonObject) {
                Utils.logPrint('E',"onBatchViewFailed--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }

            @Override
            public void onRegisterNeeded(JSONObject jsonObject) {
                Utils.logPrint('E',"onRegisterNeeded--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }
        });
    }


    private void showTipPage(){
        ToPService Topservicespp = new ToPService(MainActivity.this);
        String sessionKey = preference.getString(SESSION_KEY, "");
        Topservicespp.tipAdjustments(sessionKey, new ToPService.OnTipAdjustListener() {
            @Override
            public void onTipViewSuccess(JSONObject jsonObject) {
                Utils.logPrint('E',"onTipViewSuccess--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }

            @Override
            public void onTipViewFailed(JSONObject jsonObject) {
                Utils.logPrint('E',"onTipViewFailed--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }

            @Override
            public void onRegisterNeeded(JSONObject jsonObject) {
                Utils.logPrint('E',"onRegisterNeeded--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }
        });
    }

    private void show_recent_txn() {
        ToPService toPServicess = new ToPService(MainActivity.this);
        String sessionKey = preference.getString(SESSION_KEY, "");
        toPServicess.show_recent_txn(sessionKey,tpnEditText.getText().toString(), new ToPService.OnRecentTxnListener() {
            @Override
            public void onRecentTxnSuccess(JSONObject jsonObject) {
                Utils.logPrint('E',"onSuccess--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }

            @Override
            public void onRecentTxnFailed(JSONObject jsonObject) {
                Utils.logPrint('E',"onFailed--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }

            @Override
            public void onRegisterNeeded(JSONObject jsonObject) {
                Utils.logPrint('E',"onRegisterNeeded--"+jsonObject.toString());
                setTransactionResultTxt("" + jsonObject.toString());
            }


        });
    }

    private String getTxnTypeBasedOnSelection(int selectedTabPosition){
        switch (selectedTabPosition) {
            case 0: //SALE
                return TOPParams.SALE;
            case 1: //VOID
                return TOPParams.VOID;
            case 2: //REFUND
                return TOPParams.REFUND;
            case 3: //PRE-AUTH
                return TOPParams.PRE_AUTH;
            case 4: //TICKET
                return TOPParams.TICKET;
            case 5: //BATCH
                return TOPParams.BATCH;
            case 6: //TIP-ADJUST
                return TOPParams.TIP_ADJUSTMENT;
            case 7: //TCN TXN
                return TOPParams.ADMINISTRATIVE_TXN;
            case 8: //TCN TXN
                return TOPParams.RECENT_TXN;
            default:
                return TOPParams.SALE;
        }
    }

    private boolean isFullCardNumberNeeded(){
        return getFullCardNumberCheck.isChecked();
    }

    private boolean isReceiptNeededInResponse(){
        return receiptCheck.isChecked();
    }

    private boolean showApprovalScreen(){
        return approvalScreenCheck.isChecked();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Utils.logPrint('E', "SPLASH-PERMISSION-requestCode-" + requestCode);
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (toPService!=null) {
                        registerDevice(toPService);
                    }
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // now, user has denied permission (but not permanently!)
                        finish();
                    } else {
                        // now, user has denied permission permanently!
                        Utils.showPermissionSnackBar(MainActivity.this, getResources().getString(com.denovo.app.top.R.string.LOCATION_PERMISSION_CONTENT));
                    }
                }
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




}

