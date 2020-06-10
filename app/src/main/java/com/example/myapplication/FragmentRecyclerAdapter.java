package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;

public class FragmentRecyclerAdapter extends RecyclerView.Adapter{
    private Context mContext;
    public List<Adapater_common_type> mData;
    public FragmentRecyclerAdapter ReinfoAdapter;
    WaterFallAdapter.Recycler_banner_Holder banner_holder;
    RecyclerBanner pager;
    public FragmentRecyclerAdapter(Context context, List<Adapater_common_type> data) {
        mContext = context;
        mData = data;
    }

    public void getAdapter(FragmentRecyclerAdapter infoAdapter)
    {
        ReinfoAdapter=infoAdapter;
    }
    @Override
    public int getItemViewType(int position) {
        return mData.get(position).get_widget_type();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           if (viewType==4) {
               View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_widget, null);
               return new FragmentRecyclerAdapter.Scene_Holder(view);
           }
           else if(viewType==5)
           {
               View view = LayoutInflater.from(mContext).inflate(R.layout.seat, null);
               return new FragmentRecyclerAdapter.Seat_Holder(view);
           }
           else
           {
               View view = LayoutInflater.from(mContext).inflate(R.layout.seat_info, null);
               return new FragmentRecyclerAdapter.Seat_info(view);
           }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FragmentRecyclerAdapter.Scene_Holder) {
            FragmentRecyclerAdapter.Scene_Holder scene_holder = (FragmentRecyclerAdapter.Scene_Holder) holder;
            final Adapter_Scene movie_scene = ( Adapter_Scene) mData.get(position);
            scene_holder.finish.setText(movie_scene.finish);
            scene_holder.start.setText("Start :"+movie_scene.start);
            scene_holder.type.setText(movie_scene.type);
            scene_holder.finish.setText(movie_scene.finish);
            scene_holder.price.setText("Rp"+movie_scene.price);
            scene_holder.hall.setText("Projection hall :"+movie_scene.hall);
            scene_holder.purchase.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    String movie_name=movie_scene.name;
                    Intent intent=new Intent(mContext,Ticketing_activity.class);
                    intent.putExtra("movie_name",movie_scene.name);
                    intent.putExtra("movie_price",movie_scene.price);
                    intent.putExtra("movie_id",""+movie_scene.id);
                    intent.putExtra("serial_number",movie_scene.seats);
                    intent.putExtra("type",movie_scene.type);
                    intent.putExtra("movie_type",movie_scene.movie_type);
                    mContext.startActivity(intent);
                }
            });

        }
        else if(holder instanceof FragmentRecyclerAdapter.Seat_Holder) {
            FragmentRecyclerAdapter.Seat_Holder seat_holder = (FragmentRecyclerAdapter.Seat_Holder) holder;
            final Adapter_seat movie_seat = ( Adapter_seat) mData.get(position);
            seat_holder.seat_button.setEnabled(!movie_seat.selected);
            if(movie_seat.selected)
                seat_holder.seat_button.setBackgroundColor(Color.parseColor("#44899292"));

            seat_holder.seat_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    for(int i=0;i<Ticketing_activity.seat_nums.size();i++)
                    {
                        Adapter_seat_info seat_infos=(Adapter_seat_info)Ticketing_activity.seat_info_list.get(i);
                        if(Ticketing_activity.seat_nums.get(i)==movie_seat.num)
                            Ticketing_activity.seat_nums.remove(i);
                        if (seat_infos.seat_num==movie_seat.num) {
                            Ticketing_activity.seat_info_list.remove(i);
                            ReinfoAdapter.notifyDataSetChanged();

                            i--;
                        }

                    }
                    if(Ticketing_activity.seat_info_list.size()>=8)
                    {
                        Toast error_toast = Toast.makeText( mContext,"Exceeding the upper limit ", Toast.LENGTH_LONG);
                        error_toast.setGravity(Gravity.CENTER, 0, 0);
                        error_toast.show();
                    }
                    else {
                        Button button = (Button) v;
                        movie_seat.select = !movie_seat.select;
                        button.setBackgroundColor(Color.parseColor("#8DCEE7"));
                        if (movie_seat.select) {
                            button.setBackgroundColor(Color.RED);
                            Ticketing_activity.seat_nums.add(movie_seat.num);
                            Adapter_seat_info seat_info = new Adapter_seat_info(movie_seat.num);
                            Ticketing_activity.seat_info_list.add(seat_info);
                            ReinfoAdapter.notifyItemChanged(Ticketing_activity.seat_info_list.size());
                            ReinfoAdapter.notifyItemRangeChanged(Ticketing_activity.seat_info_list.size(), 1);

                        }
                        Ticketing_activity.priceText.setText("Price : "+Integer.valueOf( Ticketing_activity.price)*Ticketing_activity.seat_info_list.size());
                    }
                }
            });
        }
        else
        {
            FragmentRecyclerAdapter.Seat_info seat_holder = (FragmentRecyclerAdapter.Seat_info) holder;
            Adapter_seat_info movie_seat_info = ( Adapter_seat_info) mData.get(position);
            seat_holder.seat_info.setText("A "+((int)( movie_seat_info.seat_num/5)+1)+"-"+( movie_seat_info.seat_num%5+1));
        }

    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }


    public class Scene_Holder extends RecyclerView.ViewHolder {
        Button purchase;
        TextView hall;
        TextView start;
        TextView finish;
        TextView type;
        TextView price;
        public Scene_Holder(View itemView) {
            super(itemView);
            purchase = (Button) itemView.findViewById(R.id.select_seat);
            hall=(TextView )itemView.findViewById(R.id.movie_hall);
            start=(TextView )itemView.findViewById(R.id.start_time);
            finish=(TextView )itemView.findViewById(R.id.finish_time);
            type=(TextView )itemView.findViewById(R.id.movie_type);
            price=(TextView )itemView.findViewById(R.id.price);
        }
    }

    public class Seat_Holder extends RecyclerView.ViewHolder {
        Button seat_button;
        public Seat_Holder(View itemView) {
            super(itemView);
            seat_button = (Button) itemView.findViewById(R.id.movie_seat_button);

        }
    }

    public class Seat_info extends RecyclerView.ViewHolder {
        TextView seat_info;
        public Seat_info(View itemView) {
            super(itemView);
            seat_info = (TextView) itemView.findViewById(R.id.seat_info);

        }
    }

}
