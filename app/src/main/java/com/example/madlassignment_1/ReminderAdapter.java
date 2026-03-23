package com.example.madlassignment_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;

    public ReminderAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.tvTitle.setText(reminder.getTitle());
        holder.tvDescription.setText(reminder.getDescription());
        holder.tvTime.setText("Time: " + reminder.getTime());
        holder.tvPriority.setText("Priority: " + reminder.getPriority());
        holder.tvLocation.setText("Location: " + reminder.getLocation());
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTime, tvPriority, tvLocation;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            tvTime = itemView.findViewById(R.id.tvItemTime);
            tvPriority = itemView.findViewById(R.id.tvItemPriority);
            tvLocation = itemView.findViewById(R.id.tvItemLocation);
        }
    }
}
