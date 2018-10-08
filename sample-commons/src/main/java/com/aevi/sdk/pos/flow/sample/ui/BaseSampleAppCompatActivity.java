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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aevi.android.rxmessenger.activity.NoSuchInstanceException;
import com.aevi.android.rxmessenger.activity.ObservableActivityHelper;
import com.aevi.sdk.flow.constants.ActivityEvents;
import com.aevi.sdk.pos.flow.sample.R;

public abstract class BaseSampleAppCompatActivity<RESPONSE> extends AppCompatActivity {

    protected void registerForActivityEvents() {
        try {
            ObservableActivityHelper<RESPONSE> helper = ObservableActivityHelper.getInstance(getIntent());
            helper.registerForEvents(getLifecycle()).subscribe(event -> {
                switch (event) {
                    case ActivityEvents.FINISH:
                        finish();
                        break;
                }
            });
        } catch (NoSuchInstanceException e) {
            Log.e(getClass().getSimpleName(), "No ObservableActivityHelper found - finishing activity");
            finish();
        }
    }

    protected void sendResponseAndFinish(RESPONSE response) {
        try {
            ObservableActivityHelper<RESPONSE> activityHelper = ObservableActivityHelper.getInstance(getIntent());
            activityHelper.publishResponse(response);
        } catch (NoSuchInstanceException e) {
            // Ignore
        }
        finish();
    }

    protected void setupToolbar(Toolbar toolbar, int titleRes) {
        toolbar.setTitle(titleRes);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.flow_menu, menu);
        if (!showFlowStagesOption()) {
            menu.findItem(R.id.menu_flow_stage).setVisible(false);
        }
        if (!showViewModelOption()) {
            menu.findItem(R.id.menu_view_model).setVisible(false);
        }
        if (!showViewRequestOption()) {
            menu.findItem(R.id.menu_view_request).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.menu_flow_stage) {
            onShowFlowStages();
            return true;
        } else if (itemId == R.id.menu_help) {
            onShowHelpInfo();
            return true;
        } else if (itemId == R.id.menu_view_request) {
            onShowRequest();
            return true;
        } else if (itemId == R.id.menu_view_model) {
            onShowModel();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    protected boolean showFlowStagesOption() {
        return true;
    }

    protected boolean showViewModelOption() {
        return !getResources().getBoolean(R.bool.dualPane);
    }

    protected boolean showViewRequestOption() {
        return true;
    }

    protected abstract int getPrimaryColor();

    protected abstract String getCurrentStage();

    protected abstract Class<?> getRequestClass();

    protected abstract Class<?> getResponseClass();

    protected abstract String getModelJson();

    protected abstract String getRequestJson();

    protected abstract String getHelpText();

    private void onShowHelpInfo() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.KEY_TEXT, getHelpText());
        intent.putExtra(HelpActivity.KEY_TITLE_BG, getPrimaryColor());
        startActivity(intent);
    }

    private void onShowFlowStages() {
        Intent intent = new Intent(this, FlowStagesActivity.class);
        intent.putExtra(FlowStagesActivity.KEY_TITLE_BG, getPrimaryColor());
        intent.putExtra(FlowStagesActivity.KEY_CURRENT_STAGE, getCurrentStage());
        startActivity(intent);
    }

    private void onShowModel() {
        Intent intent = new Intent(this, ModelDetailsActivity.class);
        intent.putExtra(ModelDetailsActivity.KEY_MODEL_TYPE, getResponseClass().getName());
        intent.putExtra(ModelDetailsActivity.KEY_MODEL_DATA, getModelJson());
        intent.putExtra(ModelDetailsActivity.KEY_TITLE, getResponseClass().getSimpleName());
        intent.putExtra(ModelDetailsActivity.KEY_TITLE_BG, getPrimaryColor());
        startActivity(intent);
    }

    private void onShowRequest() {
        Intent intent = new Intent(this, ModelDetailsActivity.class);
        intent.putExtra(ModelDetailsActivity.KEY_MODEL_TYPE, getRequestClass().getName());
        intent.putExtra(ModelDetailsActivity.KEY_MODEL_DATA, getRequestJson());
        intent.putExtra(ModelDetailsActivity.KEY_TITLE, getRequestClass().getSimpleName());
        intent.putExtra(ModelDetailsActivity.KEY_TITLE_BG, getPrimaryColor());
        startActivity(intent);
    }
}
