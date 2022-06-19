package com.example.gui.adapter;
/*
* Second Tap
* CollectView(Fragment) Used Recycler View Adapter
* 2021.02.16
* */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gui.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class collect_adapter extends RecyclerView.Adapter<collect_adapter.ItemViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<collect_data> listData = null;
    public collect_adapter(ArrayList<collect_data> datalist){
        listData = datalist;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collect_recyclerview, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull collect_adapter.ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }
    void addItem(collect_data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }
    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView title_day;
        private ImageView breakfastView, lunchView, dinnerView;

        ItemViewHolder(View itemView) {
            super(itemView);

            title_day = itemView.findViewById(R.id.collect_title);
            breakfastView = itemView.findViewById(R.id.collect_breakfast_view);
            lunchView = itemView.findViewById(R.id.collect_lunch_view);
            dinnerView = itemView.findViewById(R.id.collect_dinner_view);
        }
        void setbitmap(String data, ImageView image){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // bitmap set
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(data, options);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                image.setImageBitmap(bitmap);
            }
            catch (Exception e) {
                // 어케하지? 생각을 해보자
            }
        }
        void onBind(collect_data data) {
            // Title UI Set
            title_day.setText(data.getDay());
            if(data.getBreakfast() != null)
                setbitmap(data.getBreakfast(), breakfastView);
            else if(data.getBreakfast() == null)
                breakfastView.setVisibility(View.INVISIBLE);
            if(data.getLunch() != null)
                setbitmap(data.getLunch(), lunchView);
            else if(data.getLunch() == null)
                lunchView.setVisibility(View.INVISIBLE);
            if(data.getDinner() != null)
                setbitmap(data.getDinner(), dinnerView);
            else if(data.getDinner() == null)
                dinnerView.setVisibility(View.INVISIBLE);
        }
    }

}
