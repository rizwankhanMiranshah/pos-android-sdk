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

package com.aevi.sdk.pos.flow.stage;

import android.app.Activity;
import android.content.Context;
import com.aevi.sdk.flow.model.AdditionalData;
import com.aevi.sdk.flow.service.BaseApiService;
import com.aevi.sdk.flow.service.ClientCommunicator;
import com.aevi.sdk.flow.stage.BaseStageModel;
import com.aevi.sdk.pos.flow.model.FlowResponse;
import com.aevi.sdk.pos.flow.model.TransactionSummary;
import com.aevi.sdk.pos.flow.service.ActivityProxyService;
import com.aevi.sdk.pos.flow.service.BasePaymentFlowService;

import static com.aevi.sdk.flow.service.ActivityHelper.ACTIVITY_REQUEST_KEY;
import static com.aevi.sdk.flow.util.Preconditions.checkNotEmpty;
import static com.aevi.sdk.flow.util.Preconditions.checkNotNull;

/**
 * Model for the post-transaction stage that exposes all the data functions and other utilities required for any app to process this stage.
 *
 * See {@link BasePaymentFlowService#onPreFlow(PreFlowModel)} for how to retrieve the model from a service context, and {@link ActivityProxyService} for
 * how to proxy the request onto an activity from where this can be instantiated via {@link #fromActivity(Activity)}.
 *
 * If data has been augmented, {@link #sendResponse()} must be called for these changes to be applied. If called with no changes, it has the same
 * effect as calling {@link #skip()}.
 *
 * If no changes are required, call {@link #skip()}.
 *
 * @see <a href="https://github.com/AEVI-AppFlow/pos-android-sdk/wiki/implementing-flow-services" target="_blank">Implementing Flow Services</a>
 */
public class PostTransactionModel extends BaseStageModel {

    private final TransactionSummary transactionSummary;
    private final FlowResponse flowResponse;

    private PostTransactionModel(Activity activity, TransactionSummary transactionSummary) {
        super(activity);
        this.transactionSummary = transactionSummary;
        this.flowResponse = new FlowResponse();
    }

    private PostTransactionModel(ClientCommunicator clientCommunicator, TransactionSummary transactionSummary) {
        super(clientCommunicator);
        this.transactionSummary = transactionSummary;
        this.flowResponse = new FlowResponse();
    }

    /**
     * Create an instance from an activity context.
     *
     * This assumes that the activity was started via {@link BaseStageModel#processInActivity(Context, Class)},
     * or via the {@link ActivityProxyService}.
     *
     * @param activity The activity that was started via one of the means described above
     * @return An instance of {@link PostTransactionModel}
     */
    public static PostTransactionModel fromActivity(Activity activity) {
        String request = activity.getIntent().getStringExtra(ACTIVITY_REQUEST_KEY);
        return new PostTransactionModel(activity, TransactionSummary.fromJson(request));
    }

    /**
     * Create an instance from a service context.
     *
     * @param clientCommunicator The client communicator for sending/receiving messages at this point in the flow
     * @param request            The deserialised Payment provided as a string via {@link BaseApiService#processRequest(ClientCommunicator, String, String)}
     * @return An instance of {@link PostTransactionModel}
     */
    public static PostTransactionModel fromService(ClientCommunicator clientCommunicator, TransactionSummary request) {
        return new PostTransactionModel(clientCommunicator, request);
    }

    /**
     * Get the transaction summary.
     *
     * @return The transaction summary
     */
    public TransactionSummary getTransactionSummary() {
        return transactionSummary;
    }

    /**
     * Add payment references to the transaction.
     *
     * The value of this can be any arbitrary data or collection of data.
     *
     * See {@link AdditionalData#addData(String, Object[])} for more info.
     *
     * @param key    The key to use for this data
     * @param values A var-args input of values associated with the key
     * @param <T>    The type of object this data is an array of
     * @throws IllegalArgumentException If key or values are not set
     */
    @SafeVarargs
    public final <T> void addReferences(String key, T... values) {
        checkNotNull(key, "Key must be set");
        checkNotEmpty(values, "At least one value must be provided");
        flowResponse.addAdditionalRequestData(key, values);
    }

    /**
     * Add payment references to the transaction.
     *
     * See {@link AdditionalData} for more info.
     *
     * @param references The references
     */
    public final void addReferences(AdditionalData references) {
        flowResponse.setAdditionalRequestData(references);
    }

    /**
     * Get the flow response that is created from this model.
     *
     * For internal use.
     *
     * @return The flow response
     */
    FlowResponse getFlowResponse() {
        return flowResponse;
    }

    /**
     * Send off the response.
     *
     * Note that this does NOT finish any activity or stop any service. That is down to the activity/service to manage internally.
     */
    public void sendResponse() {
        doSendResponse(flowResponse.toJson());
    }

    /**
     * Call to inform FPS that processing is done and no augmentation is required.
     *
     * Note that this does NOT finish any activity or stop any service. That is down to the activity/service to manage internally.
     */
    public void skip() {
        sendEmptyResponse();
    }

    @Override
    public String getRequestJson() {
        return transactionSummary.toJson();
    }
}
