/*
 *  Copyright (C) 2017 Bilibili
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tcj.sunshine.boxing;

import android.content.Intent;
import android.os.Bundle;

import com.tcj.sunshine.boxing.model.BoxingManager;
import com.tcj.sunshine.boxing.model.config.BoxingConfig;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.boxing.presenter.PickerPresenter;
import com.tcj.sunshine.boxing.presenter.PickerContract;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * A abstract class to connect {@link PickerContract.View} and {@link PickerContract.Presenter}.
 * one job has to be done. override {@link #onCreateBoxingView(ArrayList)} to create a subclass for {@link AbsBoxingViewFragment}.
 *
 * @author ChenSL
 */
public abstract class AbsBoxingActivity extends AppCompatActivity implements Boxing.OnBoxingFinishListener {

    private AbsBoxingViewFragment view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = onCreateBoxingView(getSelectedMedias(getIntent()));
        BoxingConfig pickerConfig = BoxingManager.getInstance().getBoxingConfig();
        view.setPresenter(new PickerPresenter(view));
        view.setPickerConfig(pickerConfig);
        Boxing.get().setupFragment(view, this);
    }

    private ArrayList<BaseMedia> getSelectedMedias(Intent intent) {
        return intent.getParcelableArrayListExtra(Boxing.EXTRA_SELECTED_MEDIA);
    }

    public BoxingConfig getBoxingConfig() {
        return BoxingManager.getInstance().getBoxingConfig();
    }

    /**
     * create a {@link PickerContract.View} attaching to
     * {@link PickerContract.Presenter},call in {@link #onCreate(Bundle)}
     */
    @NonNull
    public abstract AbsBoxingViewFragment onCreateBoxingView(ArrayList<BaseMedia> medias);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(view != null) {
            view.onActivityResult(requestCode, resultCode, data);
        }
    }
}
