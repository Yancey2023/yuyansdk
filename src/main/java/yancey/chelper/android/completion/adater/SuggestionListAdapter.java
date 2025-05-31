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

package yancey.chelper.android.completion.adater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyan.imemodule.R;

import yancey.chelper.core.CHelperGuiCore;
import yancey.chelper.core.Suggestion;

/**
 * 补全提示列表适配器
 */
public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListAdapter.CommandListViewHolder> {

    private final Context context;
    private final CHelperGuiCore core;

    public SuggestionListAdapter(Context context, CHelperGuiCore core) {
        this.context = context;
        this.core = core;
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_suggestion, parent, false));
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        Suggestion data = core.getSuggestion(position);
        if (data == null) {
            holder.mTv_commandName.setText(null);
            if (holder.mTv_commandDescription != null) {
                holder.mTv_commandDescription.setText(null);
            }
            holder.itemView.setOnClickListener(null);
        } else {
            if (holder.mTv_commandDescription == null) {
                holder.mTv_commandName.setText(data.name + " - " + data.description);
            } else {
                holder.mTv_commandName.setText(data.name);
                holder.mTv_commandDescription.setText(data.description);
            }
            holder.itemView.setOnClickListener(view -> core.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return core.getSuggestionsSize();
    }

    public static class CommandListViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final TextView mTv_commandName, mTv_commandDescription;

        public CommandListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.mTv_commandName = itemView.findViewById(R.id.command_list_tv_command_name);
            this.mTv_commandDescription = itemView.findViewById(R.id.command_list_tv_command_description);
        }
    }
}
