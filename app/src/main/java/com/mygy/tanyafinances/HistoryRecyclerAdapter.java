package com.mygy.tanyafinances;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private List<Operation> operations;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");

    public HistoryRecyclerAdapter(Context context, List<Operation> operations) {
        this.operations = operations;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public HistoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.history_item, parent, false);
        return new HistoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryRecyclerAdapter.ViewHolder holder, int position) {
        Operation operation = operations.get(position);

        holder.ico.setImageResource(operation.getCategory().getImageRes());
        holder.sum.setText(Double.toString(Math.abs(operation.getDeltaMoney())));
        holder.sign.setImageResource((operation.getDeltaMoney()>=0) ? R.drawable.baseline_arrow_upward_24:R.drawable.baseline_arrow_downward_24);
        holder.category.setText(operation.getCategory().getName());
        holder.date.setText(dateFormat.format(operation.getDate()));
        holder.description.setText(operation.getDescription());
    }

    @Override
    public int getItemCount() {
        return operations.size();
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ico,sign;
        final TextView sum, category, date, description;
        ViewHolder(View view){
            super(view);
            ico = view.findViewById(R.id.historyItem_ico);
            sign = view.findViewById(R.id.historyItem_operationSign);
            sum = view.findViewById(R.id.historyItem_sum);
            category = view.findViewById(R.id.historyItem_category);
            date = view.findViewById(R.id.historyItem_date);
            description = view.findViewById(R.id.historyItem_description);
        }
    }
}
