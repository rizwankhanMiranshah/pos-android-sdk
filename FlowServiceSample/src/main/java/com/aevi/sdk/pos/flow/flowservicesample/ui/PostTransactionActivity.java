/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.aevi.sdk.pos.flow.flowservicesample.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.aevi.sdk.flow.model.AdditionalData;
import com.aevi.sdk.flow.service.BaseApiService;
import com.aevi.sdk.pos.flow.flowservicesample.R;
import com.aevi.sdk.pos.flow.model.FlowResponse;
import com.aevi.sdk.pos.flow.model.PaymentStage;
import com.aevi.sdk.pos.flow.model.TransactionSummary;
import com.aevi.sdk.pos.flow.sample.ui.BaseSampleAppCompatActivity;
import com.aevi.sdk.pos.flow.sample.ui.ModelDisplay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostTransactionActivity extends BaseSampleAppCompatActivity<FlowResponse> {

    private FlowResponse flowResponse;
    private TransactionSummary transactionSummary;
    private ModelDisplay modelDisplay;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_txn);
        ButterKnife.bind(this);
        flowResponse = new FlowResponse();
        registerForActivityEvents();
        transactionSummary = TransactionSummary.fromJson(getIntent().getStringExtra(BaseApiService.ACTIVITY_REQUEST_KEY));
        modelDisplay = (ModelDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_request_details);
        if (modelDisplay != null) {
            modelDisplay.showTitle(false);
        }
        setupToolbar(toolbar, R.string.fss_post_payment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        modelDisplay.showTransactionSummary(transactionSummary);
    }

    @OnClick(R.id.add_payment_references)
    public void onAddPaymentReferences() {
        AdditionalData paymentRefs = new AdditionalData();
        paymentRefs.addData("someReference", "addedByPostPaymentSample");
        flowResponse.setPaymentReferences(paymentRefs);
        findViewById(R.id.add_payment_references).setEnabled(false);
    }

    @OnClick(R.id.send_response)
    public void onFinish() {
        sendResponseAndFinish(flowResponse);
    }

    @Override
    protected boolean showViewRequestOption() {
        return false;
    }

    @Override
    protected int getPrimaryColor() {
        return getResources().getColor(R.color.colorPrimary);
    }

    @Override
    protected String getCurrentStage() {
        return PaymentStage.POST_TRANSACTION.name();
    }

    @Override
    protected Class<?> getRequestClass() {
        return TransactionSummary.class;
    }

    @Override
    protected Class<?> getResponseClass() {
        return FlowResponse.class;
    }

    @Override
    protected String getModelJson() {
        return flowResponse.toJson();
    }

    @Override
    protected String getRequestJson() {
        return transactionSummary.toJson();
    }

    @Override
    protected String getHelpText() {
        return getString(R.string.post_txn_help);
    }
}
