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
import android.support.v7.app.AppCompatActivity;

import com.aevi.android.rxmessenger.activity.NoSuchInstanceException;
import com.aevi.android.rxmessenger.activity.ObservableActivityHelper;
import com.aevi.sdk.pos.flow.flowservicesample.R;
import com.aevi.sdk.flow.model.NoOpModel;
import com.aevi.sdk.flow.service.BaseApiService;
import com.aevi.sdk.pos.flow.model.PaymentResponse;
import com.aevi.sdk.pos.flow.sample.ui.ModelDisplay;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostFlowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_flow);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ModelDisplay modelDisplay = (ModelDisplay) getSupportFragmentManager().findFragmentById(R.id.fragment_request_details);
        modelDisplay.showPaymentResponse(PaymentResponse.fromJson(getIntent().getStringExtra(BaseApiService.ACTIVITY_REQUEST_KEY)));
    }

    @OnClick(R.id.send_response)
    public void onFinish() {
        sendResponseAndFinish();
    }

    private void sendResponseAndFinish() {
        try {
            ObservableActivityHelper<NoOpModel> activityHelper = ObservableActivityHelper.getInstance(getIntent());
            activityHelper.publishResponse(new NoOpModel());
        } catch (NoSuchInstanceException e) {
            // Ignore
        }
        finish();
    }
}
