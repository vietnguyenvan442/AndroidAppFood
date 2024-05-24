package com.example.app_food.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_food.Domain.User;
import com.example.app_food.R;

import java.util.List;

public class ListUserAdapterAdmin extends RecyclerView.Adapter<ListUserAdapterAdmin.ViewHolder> {
    List<User> list;
    Context context;

    public ListUserAdapterAdmin(List<User> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ListUserAdapterAdmin.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListUserAdapterAdmin.ViewHolder holder, int position) {
        User user = list.get(position);
        holder.nameTxt.setText(user.getName());
        holder.emailTxt.setText(user.getEmail());
        holder.passwordTxt.setText(user.getPassword());
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Thông báo xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa không?");
                builder.setIcon(R.drawable.icon_remove);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog =builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTxt, emailTxt, passwordTxt, btDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            emailTxt = itemView.findViewById(R.id.emailTxt);
            passwordTxt = itemView.findViewById(R.id.passwordTxt);
            btDelete = itemView.findViewById(R.id.btDelete);
        }
    }
}
