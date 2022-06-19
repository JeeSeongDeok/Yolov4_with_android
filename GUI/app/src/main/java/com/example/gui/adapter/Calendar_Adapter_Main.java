package com.example.gui.adapter;

import android.content.Context;
import android.icu.text.CaseMap;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gui.R;

import java.util.ArrayList;
import java.util.Dictionary;

public class Calendar_Adapter_Main extends RecyclerView.Adapter<Calendar_Adapter_Main.CustomViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<calendar_Data> mList;

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView Title;
        protected TextView Content;

        public CustomViewHolder(View view) {
            super(view);
            this.Title = (TextView)view.findViewById(R.id.textView1);
            this.Content = (TextView)view.findViewById(R.id.textView2);

            Title.setTextColor(0xffd9f099);
            Content.setTextColor(0xffd9f099);
            view.setOnCreateContextMenuListener(Calendar_Adapter_Main.this::onCreateContextMenu);
        }
    }
    public Calendar_Adapter_Main(ArrayList<calendar_Data> list) {
        this.mList = list;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem Delete = menu.add(Menu.NONE, 1001, 1, "삭제");
        Delete.setOnMenuItemClickListener(onEditMenu);
    }

    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
//        CustomViewHolder holder = (CustomViewHolder)convertView.getTag();
//        int position = holder.getAdapterPosition();
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case 1001:
//                    mList.remove(getAdapterPosition());
//                    notifyItemRemoved(getAdapterPosition());
//                    notifyItemRangeChanged(getAdapterPosition(), mList.size());
                    break;
            }
            return true;
        }
    };
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calendar_adapter_, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.Title.setText(mList.get(position).getTitle());
        viewholder.Content.setText(mList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }
}
