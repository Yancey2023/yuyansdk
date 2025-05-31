/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.android.completion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyan.imemodule.R;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import yancey.chelper.android.completion.adater.SuggestionListAdapter;
import yancey.chelper.core.CHelperCore;
import yancey.chelper.core.CHelperGuiCore;
import yancey.chelper.core.CommandGuiCoreInterface;
import yancey.chelper.core.ErrorReason;
import yancey.chelper.core.SelectedString;
import yancey.chelper.core.Theme;

public class CompletionView extends FrameLayout {

    private static final String TAG = "WritingCommandView";
    public boolean isGuiLoaded;
    private final CHelperGuiCore core;
    protected final @NonNull View view;
    private final TextView mtv_structure, mtv_description, mtv_errorReasons;
    private final SuggestionListAdapter adapter;

    public CompletionView(@NonNull Context context) {
        this(context, null, 0, 0);
    }

    public CompletionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public CompletionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CompletionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        view = LayoutInflater.from(context).inflate(R.layout.layout_writing_command, this, false);
        addView(view);
        isGuiLoaded = false;
        boolean isDarkMode = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        core = new CHelperGuiCore(isDarkMode ? Theme.THEME_NIGHT : Theme.THEME_DAY);
        mtv_structure = view.findViewById(R.id.tv_structure);
        mtv_description = view.findViewById(R.id.tv_description);
        mtv_errorReasons = view.findViewById(R.id.tv_error_reasons);
        adapter = new SuggestionListAdapter(context, core);
        RecyclerView recyclerView = view.findViewById(R.id.rv_command_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isGuiLoaded = true;
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                core.onSelectionChanged();
            }
        });
        String cpackPath = "cpack/release-experiment-1.21.81.2.cpack";
        if (core.getCore() == null || !Objects.equals(core.getCore().getPath(), cpackPath)) {
            CHelperCore core1 = null;
            try {
                core1 = CHelperCore.fromAssets(getContext().getAssets(), cpackPath);
            } catch (Throwable throwable) {
                mtv_errorReasons.setText("资源包加载失败");
                Log.w(TAG, "fail to load resource pack", throwable);
            }
            core.setCore(core1);
        }
    }

    public void init(@NonNull Supplier<SelectedString> getSelectedString, @NonNull Consumer<SelectedString> setSelectedString) {
        core.setCommandGuiCoreInterface(new CommandGuiCoreInterface() {
            @Override
            public boolean isUpdateStructure() {
                return true;
            }

            @Override
            public boolean isUpdateDescription() {
                return true;
            }

            @Override
            public boolean isUpdateErrorReason() {
                return true;
            }

            @Override
            public boolean isCheckingBySelection() {
                return true;
            }

            @Override
            public boolean isSyntaxHighlight() {
                return false;
            }

            @Override
            public void updateStructure(@Nullable String structure) {
                mtv_structure.setText(structure);
            }

            @Override
            public void updateDescription(@Nullable String description) {
                mtv_description.setText(description);
            }

            @Override
            public void updateErrorReason(@Nullable ErrorReason[] errorReasons) {
                if (errorReasons == null || errorReasons.length == 0) {
                    if (mtv_errorReasons != null) {
                        mtv_errorReasons.setVisibility(View.GONE);
                    }
                } else {
                    if (errorReasons.length == 1) {
                        if (mtv_errorReasons != null) {
                            mtv_errorReasons.setText(errorReasons[0].errorReason);
                        }
                    } else {
                        StringBuilder errorReasonStr = new StringBuilder("可能的错误原因：");
                        for (int i = 0; i < errorReasons.length; i++) {
                            errorReasonStr.append("\n").append(i + 1).append(". ").append(errorReasons[i].errorReason);
                        }
                        if (mtv_errorReasons != null) {
                            mtv_errorReasons.setText(errorReasonStr);
                        }
                    }
                    if (mtv_errorReasons != null) {
                        mtv_errorReasons.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void updateSuggestions() {
                adapter.notifyDataSetChanged();
            }

            @Override
            public SelectedString getSelectedString() {
                return getSelectedString.get();
            }

            @Override
            public void setSelectedString(SelectedString selectedString) {
                setSelectedString.accept(selectedString);
            }

            @Override
            public void updateSyntaxHighlight(int[] colors) {

            }
        });
    }

    public void onSelectionChanged() {
        core.onSelectionChanged();
    }

}
