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

package com.aevi.sdk.pos.flow.sample.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.aevi.sdk.flow.constants.AdditionalDataKeys;
import com.aevi.sdk.flow.model.*;
import com.aevi.sdk.pos.flow.model.*;
import com.aevi.sdk.pos.flow.sample.AmountFormatter;
import com.aevi.sdk.pos.flow.sample.R;
import com.aevi.ui.library.BaseObservableFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static com.aevi.sdk.pos.flow.sample.AmountFormatter.formatAmount;

public class ModelDetailsFragment extends BaseObservableFragment implements ModelDisplay {

    private SectionedRecyclerViewAdapter adapter;

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_model_details;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SectionedRecyclerViewAdapter();
    }

    private void reset() {
        adapter.removeAllSections();
    }

    private void setupRecyclerView(int title) {
        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ((TextView) getActivity().findViewById(R.id.details_title)).setText(title);
    }

    @Override
    public void showTitle(boolean show) {
        getActivity().findViewById(R.id.details_title).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPayment(Payment payment) {
        reset();
        addPaymentOverview(payment);
        adapter.addSection(createAmountsSection(payment.getAmounts(), null));
        if (payment.getCardToken() != null) {
            adapter.addSection(createTokenSection(payment.getCardToken()));
        }

        addDataSections(payment.getAdditionalData(), payment.getAmounts().getCurrency());
        setupRecyclerView(R.string.payment_model_overview);
    }

    private void addPaymentOverview(Payment payment) {
        List<Pair<String, String>> paymentInfo = new ArrayList<>();
        paymentInfo.add(getStringPair(R.string.id, payment.getId()));
        paymentInfo.add(getStringPair(R.string.transaction_type, payment.getTransactionType()));
        paymentInfo.add(getStringPair(R.string.split_enabled, payment.isSplitEnabled()));

        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, paymentInfo));
    }

    @Override
    public void showRequest(Request request) {
        reset();
        List<Pair<String, String>> requestInfo = new ArrayList<>();
        requestInfo.add(getStringPair(R.string.id, request.getId()));
        requestInfo.add(getStringPair(R.string.request_type, request.getRequestType()));
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, requestInfo));

        addDataSections(request.getRequestData(), null);
        setupRecyclerView(R.string.request_model_overview);
    }

    @Override
    public void showTransactionRequest(TransactionRequest transactionRequest) {
        reset();
        List<Pair<String, String>> requestInfo = new ArrayList<>();
        requestInfo.add(getStringPair(R.string.id, transactionRequest.getId()));
        requestInfo.add(getStringPair(R.string.transaction_type, transactionRequest.getTransactionType()));
        requestInfo.add(getStringPair(R.string.payment_stage, transactionRequest.getPaymentStage().name()));
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, requestInfo));

        if (transactionRequest.getAmounts() != null) {
            adapter.addSection(createAmountsSection(transactionRequest.getAmounts(), null));
        }

        if (transactionRequest.getCard() != null && !transactionRequest.getCard().isEmpty()) {
            adapter.addSection(createCardSection(transactionRequest.getCard()));
        }

        addDataSections(transactionRequest.getAdditionalData(), transactionRequest.getAmounts().getCurrency());
        setupRecyclerView(R.string.txn_request_model_overview);
    }

    @Override
    public void showSplitRequest(SplitRequest splitRequest) {
        reset();
        List<Pair<String, String>> summaryInfo = new ArrayList<>();
        summaryInfo.add(getStringPair(R.string.payment_id, splitRequest.getSourcePayment().getId()));
        summaryInfo.add(getStringPair(R.string.transaction_type, splitRequest.getSourcePayment().getTransactionType()));
        addAmountInfo(summaryInfo, splitRequest.getSourcePayment().getAmounts(), null);
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, summaryInfo));

        int count = 1;
        for (Transaction transaction : splitRequest.getTransactions()) {
            addTransactionSection(transaction, count++);
        }

        setupRecyclerView(R.string.split_request_model_overview);
    }

    @Override
    public void showTransactionSummary(TransactionSummary transactionSummary) {
        reset();
        List<Pair<String, String>> summaryInfo = new ArrayList<>();
        summaryInfo.add(getStringPair(R.string.transaction_type, transactionSummary.getTransactionType()));
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, summaryInfo));
        if (transactionSummary.getCard() != null && !transactionSummary.getCard().isEmpty()) {
            adapter.addSection(createCardSection(transactionSummary.getCard()));
        }
        addTransactionSection(transactionSummary, 1);
        setupRecyclerView(R.string.txn_summary_model_overview);
    }

    @Override
    public void showCardResponse(CardResponse cardResponse) {
        reset();
        List<Pair<String, String>> cardResponseInfo = new ArrayList<>();
        cardResponseInfo.add(getStringPair(R.string.outcome, cardResponse.getResult().name()));
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, cardResponseInfo));
        if (cardResponse.getCard() != null && !cardResponse.getCard().isEmpty()) {
            adapter.addSection(createCardSection(cardResponse.getCard()));
        }
        setupRecyclerView(R.string.card_response_model_overview);
    }

    @Override
    public void showTransactionResponse(TransactionResponse transactionResponse) {
        reset();
        List<Pair<String, String>> responseInfo = createCommonTransactionResponseInfo(getActivity(), transactionResponse);
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, responseInfo));

        if (transactionResponse.getAmountsProcessed() != null) {
            adapter.addSection(createAmountsSection(transactionResponse.getAmountsProcessed(), transactionResponse.getPaymentMethod()));
        }

        if (transactionResponse.getCard() != null && !transactionResponse.getCard().isEmpty()) {
            adapter.addSection(createCardSection(transactionResponse.getCard()));
        }

        addDataSections(transactionResponse.getReferences(), null);
        setupRecyclerView(R.string.payment_response_model_overview);
    }

    static List<Pair<String, String>> createCommonTransactionResponseInfo(Context context, TransactionResponse transactionResponse) {
        List<Pair<String, String>> responseInfo = new ArrayList<>();
        responseInfo.add(getStringPair(context, R.string.outcome, transactionResponse.getOutcome().name()));
        String outcomeMessage = transactionResponse.getOutcomeMessage() != null ? transactionResponse.getOutcomeMessage() : "N/A";
        responseInfo.add(getStringPair(context, R.string.outcome_message, outcomeMessage));
        String respCode = transactionResponse.getResponseCode() != null ? transactionResponse.getResponseCode() : "N/A";
        responseInfo.add(getStringPair(context, R.string.response_code, respCode));
        return responseInfo;
    }

    @Override
    public void showPaymentResponse(PaymentResponse paymentResponse) {
        reset();
        List<Pair<String, String>> responseInfo = new ArrayList<>();
        responseInfo.add(getStringPair(R.string.outcome, paymentResponse.getOutcome().name()));
        responseInfo.add(getStringPair(R.string.failure_reason, paymentResponse.getFailureReason().name()));
        if (paymentResponse.getFailureMessage() != null) {
            responseInfo.add(getStringPair(R.string.failure_message, paymentResponse.getFailureMessage()));
        }
        responseInfo.add(getStringPair(R.string.all_txn_approved, paymentResponse.isAllTransactionsApproved()));
        responseInfo.add(getStringPair(R.string.is_split, paymentResponse.isSplit()));
        responseInfo.add(getStringPair(R.string.num_transactions, paymentResponse.getTransactions().size()));
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, responseInfo));

        int count = 1;
        for (Transaction transaction : paymentResponse.getTransactions()) {
            addTransactionSection(transaction, count++);
        }

        setupRecyclerView(R.string.payment_response_model_overview);
    }

    private void addTransactionSection(Transaction transaction, int index) {
        List<Pair<String, String>> transactionInfo = new ArrayList<>();
        transactionInfo.add(getStringPair(R.string.total_amount_requested, AmountFormatter.formatAmount(transaction.getRequestedAmounts().getCurrency(),
                transaction.getRequestedAmounts().getTotalAmountValue())));
        transactionInfo.add(getStringPair(R.string.total_amount_processed, AmountFormatter.formatAmount(transaction.getRequestedAmounts().getCurrency(),
                transaction.getProcessedAmounts().getTotalAmountValue())));
        transactionInfo.add(getStringPair(R.string.num_responses, transaction.getTransactionResponses().size()));

        adapter.addSection(new TransactionSection(getActivity(), transactionInfo, transaction.getTransactionResponses(), index));
    }

    @Override
    public void showResponse(Response response) {
        reset();
        List<Pair<String, String>> responseInfo = new ArrayList<>();
        responseInfo.add(getStringPair(R.string.request_type, response.getOriginatingRequest().getRequestType()));
        int outcomeRes = response.wasSuccessful() ? R.string.success : R.string.failed;
        responseInfo.add(getStringPair(R.string.outcome, getString(outcomeRes)));
        responseInfo.add(getStringPair(R.string.outcome_message, response.getOutcomeMessage()));
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, responseInfo));

        addDataSections(response.getResponseData(), null);
        setupRecyclerView(R.string.response_model_overview);
    }

    @Override
    public void showFlowResponse(FlowResponse flowResponse) {
        reset();
        List<Pair<String, String>> responseInfo = new ArrayList<>();
        responseInfo.add(getStringPair(R.string.split_requested, flowResponse.shouldEnableSplit()));
        responseInfo.add(getStringPair(R.string.cancel_requested, flowResponse.shouldCancelTransaction()));
        adapter.addSection(new RecyclerViewSection(getActivity(), R.string.overview, responseInfo));

        String currency = null;
        if (flowResponse.getUpdatedRequestAmounts() != null) {
            adapter.addSection(createAmountsSection(flowResponse.getUpdatedRequestAmounts(), null, R.string.updated_request_amounts));
            currency = flowResponse.getUpdatedRequestAmounts().getCurrency();
        }

        if (flowResponse.getAmountsPaid() != null) {
            adapter.addSection(createAmountsSection(flowResponse.getAmountsPaid(), flowResponse.getAmountsPaidPaymentMethod(), R.string.paid_amounts));
            currency = flowResponse.getAmountsPaid().getCurrency();
        }

        if (flowResponse.getRequestAdditionalData() != null) {
            addDataSections(flowResponse.getRequestAdditionalData(), currency, R.string.additional_request_data);
        }

        if (flowResponse.getPaymentReferences() != null) {
            addDataSections(flowResponse.getPaymentReferences(), currency, R.string.payment_references);
        }

        setupRecyclerView(R.string.flow_response_model_overview);
    }

    @Override
    public void showCustomData(AdditionalData additionalData) {
        reset();
        addDataSections(additionalData, null);
        setupRecyclerView(R.string.general_data);
    }

    private void addDataSections(AdditionalData additionalData, String currency, int... sectionTitle) {
        if (additionalData.hasData(AdditionalDataKeys.DATA_KEY_BASKET)) {
            adapter.addSection(createBasketSection(additionalData.getValue(AdditionalDataKeys.DATA_KEY_BASKET, Basket.class), currency, true));
        }

        if (additionalData.hasData(AdditionalDataKeys.DATA_KEY_CUSTOMER)) {
            adapter.addSection(createCustomerSection(additionalData.getValue(AdditionalDataKeys.DATA_KEY_CUSTOMER, Customer.class)));
        }

        if (additionalData.hasData(AdditionalDataKeys.DATA_KEY_TOKEN)) {
            adapter.addSection(createTokenSection(additionalData.getValue(AdditionalDataKeys.DATA_KEY_TOKEN, Token.class)));
        }

        Map<String, Amounts> amountData = additionalData.getDataOfType(Amounts.class);
        for (String key : amountData.keySet()) {
            adapter.addSection(createAmountsSection(additionalData.getValue(key, Amounts.class), null));
        }

        List<Pair<String, String>> otherInfo = new ArrayList<>();
        addGenericDataFields(otherInfo, additionalData);
        if (!otherInfo.isEmpty()) {
            int sectionTitleRes = sectionTitle.length > 0 ? sectionTitle[0] : R.string.other_data;
            adapter.addSection(new RecyclerViewSection(getActivity(), sectionTitleRes, otherInfo));
        }
    }

    private RecyclerViewSection createBasketSection(Basket basket, String currency, boolean addItems) {
        List<Pair<String, String>> basketInfo = new ArrayList<>();
        basketInfo.add(getStringPair(R.string.basket_total, formatAmount(currency, basket.getTotalBasketValue())));
        basketInfo.add(getStringPair(R.string.basket_num_items, basket.getNumberOfUniqueItems()));
        if (addItems) {
            for (BasketItem basketItem : basket.getBasketItems()) {
                String detail = basketItem.getLabel() + " (" + basketItem.getCount() + ") @ " + formatAmount(currency, basketItem.getIndividualAmount());
                basketInfo.add(getStringPair(R.string.item, detail));
            }
        }
        return new RecyclerViewSection(getActivity(), R.string.basket_details, basketInfo);
    }

    private RecyclerViewSection createCustomerSection(Customer customer) {
        List<Pair<String, String>> customerInfo = new ArrayList<>();
        customerInfo.add(getStringPair(R.string.id, customer.getId()));
        customerInfo.add(getStringPair(R.string.full_name, customer.getFullName()));
        if (!customer.getTokens().isEmpty()) {
            customerInfo.add(getStringPair(R.string.token_value, customer.getTokens().get(0).getValue()));
        }

        addGenericDataFields(customerInfo, customer.getCustomerDetails());

        return new RecyclerViewSection(getActivity(), R.string.customer_details, customerInfo);
    }

    private RecyclerViewSection createAmountsSection(Amounts amounts, String paymentMethod, int... sectionTitle) {
        List<Pair<String, String>> amountInfo = new ArrayList<>();
        addAmountInfo(amountInfo, amounts, paymentMethod);
        int sectionTitleRes = sectionTitle.length > 0 ? sectionTitle[0] : R.string.amount_details;
        return new RecyclerViewSection(getActivity(), sectionTitleRes, amountInfo);
    }

    private void addAmountInfo(List<Pair<String, String>> infoList, Amounts amounts, String paymentMethod) {
        infoList.add(getStringPair(R.string.currency, amounts.getCurrency()));
        if (paymentMethod != null) {
            infoList.add(getStringPair(R.string.payment_method, paymentMethod));
        }
        infoList.add(getStringPair(R.string.total_amount, formatAmount(amounts.getCurrency(), amounts.getTotalAmountValue())));
        infoList.add(getStringPair(R.string.base_amount, formatAmount(amounts.getCurrency(), amounts.getBaseAmountValue())));

        for (String amountIdentifier : amounts.getAdditionalAmounts().keySet()) {
            infoList.add(getStringPair(amountIdentifier, formatAmount(amounts.getCurrency(), amounts.getAdditionalAmountValue(amountIdentifier))));
        }
    }

    private RecyclerViewSection createCardSection(Card card) {
        List<Pair<String, String>> cardInfo = new ArrayList<>();
        if (card.getMaskedPan() != null) {
            cardInfo.add(getStringPair(R.string.masked_pan, card.getMaskedPan()));
        }
        if (card.getCardholderName() != null) {
            cardInfo.add(getStringPair(R.string.cardholder_name, card.getCardholderName()));
        }
        if (card.getExpiryDate() != null) {
            String year = "20" + card.getExpiryDate().substring(0, 2);
            String month = card.getExpiryDate().substring(2);
            cardInfo.add(getStringPair(R.string.expiry_date, year + " / " + month));
        }
        if (card.getCardToken() != null) {
            cardInfo.add(getStringPair(R.string.token_value, card.getCardToken().getValue()));
        }
        addGenericDataFields(cardInfo, card.getAdditionalData());
        return new RecyclerViewSection(getActivity(), R.string.card_details, cardInfo);
    }

    private RecyclerViewSection createTokenSection(Token token) {
        List<Pair<String, String>> tokenInfo = new ArrayList<>();
        tokenInfo.add(getStringPair(R.string.token_value, token.getValue()));
        tokenInfo.add(getStringPair(R.string.token_source, token.getSource()));
        if (token.getAlgorithm() != null) {
            tokenInfo.add(getStringPair(R.string.token_algo, token.getAlgorithm()));
        }
        return new RecyclerViewSection(getActivity(), R.string.token_details, tokenInfo);
    }

    private void addGenericDataFields(List<Pair<String, String>> itemList, AdditionalData additionalData) {
        for (String key : additionalData.getKeys()) {
            Object value = additionalData.getValue(key);
            if (value != null && (value instanceof Number || value instanceof String)) {
                itemList.add(getStringPair(key, value));
            }
        }
    }

    Pair<String, String> getStringPair(int labelRes, Object value) {
        return getStringPair(getActivity(), labelRes, value);
    }

    static Pair<String, String> getStringPair(Context context, int labelRes, Object value) {
        return new Pair<>(context.getString(labelRes), value.toString());
    }

    static Pair<String, String> getStringPair(String label, Object value) {
        return new Pair<>(label, value.toString());
    }

}
