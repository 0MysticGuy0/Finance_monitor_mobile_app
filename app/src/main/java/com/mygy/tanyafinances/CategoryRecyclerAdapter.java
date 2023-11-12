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

import java.util.List;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Category> categories;

    public CategoryRecyclerAdapter(Context context, List<Category> categories) {
        this.categories = categories;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryRecyclerAdapter.ViewHolder holder, int position) {
        System.out.println("ddd"+categories.get(position));
        Category category = categories.get(position);

        holder.ico.setImageResource(category.getImageRes());
        holder.name.setText(category.getName());
        holder.description.setText(category.getDescription());
        holder.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(inflater.getContext());
            a_builder.setMessage("Удалить категрию \""+category.getName()+"\"?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.user.removeCategory(category);
                            notifyDataSetChanged();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = a_builder.create();
            alert.setTitle("Удление категории");
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ico;
        final TextView name, description;
        final ImageButton deleteBtn;
        ViewHolder(View view){
            super(view);
            ico = view.findViewById(R.id.category_ico);
            name = view.findViewById(R.id.categoryName);
            description = view.findViewById(R.id.categoryDescription);
            deleteBtn = view.findViewById(R.id.deleteCategoryBtn);
        }
    }
}
