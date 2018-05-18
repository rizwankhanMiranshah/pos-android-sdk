package com.aevi.sdk.pos.flow.paymentinitiationsample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.aevi.android.rxmessenger.MessageException;
import com.aevi.sdk.pos.flow.paymentinitiationsample.R;
import com.aevi.sdk.pos.flow.model.PaymentResponse;
import com.aevi.sdk.pos.flow.sample.ui.ModelDisplay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aevi.sdk.pos.flow.model.PaymentResponse.Outcome.*;

public class PaymentResultActivity extends AppCompatActivity {

    public static final String PAYMENT_RESPONSE_KEY = "paymentResponse";
    public static final String ERROR_KEY = "error";
    private static final String TAG = PaymentResultActivity.class.getSimpleName();

    private ModelDisplay modelDisplay;

    @BindView(R.id.request_status)
    ImageView requestStatus;

    @BindView(R.id.message_error_code)
    @Nullable
    TextView messageErrorCode;

    @BindView(R.id.message_error_description)
    @Nullable
    TextView messageErrorDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getIntent().hasExtra(PAYMENT_RESPONSE_KEY) ? R.layout.activity_payment_approved : R.layout.activity_payment_error);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.hasExtra(PAYMENT_RESPONSE_KEY)) {
            PaymentResponse response = PaymentResponse.fromJson(intent.getStringExtra(PAYMENT_RESPONSE_KEY));
            modelDisplay = (ModelDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_request_details);
            modelDisplay.showPaymentResponse(response);
            modelDisplay.showTitle(false);
            if (response.getOutcome() == PARTIALLY_FULFILLED) {
                requestStatus.setImageResource(R.drawable.ic_check_circle_amber);
            } else if (response.getOutcome() == FAILED) {
                requestStatus.setImageResource(R.drawable.ic_error_circle);
            }
        } else if (intent.hasExtra(ERROR_KEY)) {
            MessageException error = MessageException.fromJson(intent.getStringExtra(ERROR_KEY));
            showErrorResult(error);
        }
    }

    private void showErrorResult(MessageException error) {
        messageErrorCode.setText(error.getCode());
        messageErrorDesc.setText(error.getMessage());
    }

    @OnClick(R.id.close)
    public void onClose() {
        finish();
    }
}