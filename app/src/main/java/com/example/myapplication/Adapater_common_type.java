package com.example.myapplication;
import android.widget.FrameLayout;
import java.io.Serializable;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public interface Adapater_common_type{

    public int get_widget_type();
}

final class Movie_card implements Adapater_common_type,Serializable {
    public static final int widget_type=1;
    public String img_url;
    public String name;
    public String score;
    public String special_effect;
    public String length;
    public String release_data;
    public String actors;
    public int imgHeight;
    @Override
    public int get_widget_type()
    {
        return widget_type;
    }
}
final class Adapter_Header implements Adapater_common_type,Serializable{
    public static final int widget_type=0;

    @Override
    public int get_widget_type()
    {
        return widget_type;
    }
}

final class Adapter_Scene implements Adapater_common_type,Serializable{
    public static final int widget_type=4;
    public String price;
    public String hall;
    public String type;
    public String start;
    public String finish;
    public String name;
    public String seats;
    public String movie_type;
    public int id;
    @Override
    public int get_widget_type()
    {
        return widget_type;
    }
}

final class Adapter_seat implements Adapater_common_type,Serializable{
    public static final int widget_type=5;
    public int num;
    boolean selected;
    boolean select;
    Adapter_seat(int nums,boolean selecteds,boolean selects){
        num=nums;
        selected=selecteds;
        select=selects;
    }
    @Override
    public int get_widget_type()
    {
        return widget_type;
    }
}

final class Adapter_seat_info implements Adapater_common_type,Serializable{
    public static final int widget_type=6;
    public String seat_info;
    public int seat_num;
    Adapter_seat_info(int num)
    {
        seat_num=num;
    }
    @Override
    public int get_widget_type()
    {
        return widget_type;
    }
}
final class Adapter_order implements Adapater_common_type,Serializable{
    public static final int widget_type=7;
    public String code;
    public String hall;
    public String time;
    public String date;
    public String movie;
    public String seat;
    public String img_url;
    public String id;
    public String order_id;
    public int watched;
    public boolean is_commented;
    @Override
    public int get_widget_type()
    {
        return widget_type;
    }
}

final class Adapter_comments implements Adapater_common_type,Serializable{
    public static final int widget_type=8;
    public String user_img;
    public String user_name;
    public String comment;

    public float mark;
    @Override
    public int get_widget_type()
    {
        return widget_type;
    }
}


final class Adapter_recycler_banner implements Adapater_common_type,Serializable{
    public static final int widget_type=2;
    @Override
    public int get_widget_type()
    {
        return widget_type;
    }
    int i;

    public List<RecyclerBanner.BannerEntity> urls = new ArrayList<>();
    public void init()
    {
       if(urls.size()!=5) {
           urls.add(new Entity("http://nightmaremlp.pythonanywhere.com/img/iron_man.png"));
            urls.add(new Entity("http://pic.szjal.cn/img/a49210ffdb7a6a7b1c4561295dfcb540.jpg"));

            urls.add(new Entity("http://pic.szjal.cn/img/023d29650c1969a6046216744d15ca3b.jpg"));

            urls.add(new Entity("http://pic.szjal.cn/img/18e82508237055e6b990143baf1ae8cf.jpg"));

            urls.add(new Entity("http://pic.szjal.cn/img/3f4103f3fe93ba41f6ac4fc6c5a64486.jpg"));
        }
    }

    public void update(View v) {
        i++;
        urls.clear();
        if (i % 2 == 0) {


            urls.add(new Entity("http://pic.szjal.cn/img/a49210ffdb7a6a7b1c4561295dfcb540.jpg"));

            urls.add(new Entity("http://pic.szjal.cn/img/023d29650c1969a6046216744d15ca3b.jpg"));

            urls.add(new Entity("http://pic.szjal.cn/img/18e82508237055e6b990143baf1ae8cf.jpg"));

            urls.add(new Entity("http://pic.szjal.cn/img/3f4103f3fe93ba41f6ac4fc6c5a64486.jpg"));

        } else {

            urls.add(new Entity("http://pic.szjal.cn/img/53f1886c0494f09d39c9ad146a9f5aaf.jpg"));

            urls.add(new Entity("http://pic.szjal.cn/img/18e82508237055e6b990143baf1ae8cf.jpg"));

            urls.add(new Entity("http://pic.szjal.cn/img/a49210ffdb7a6a7b1c4561295dfcb540.jpg"));

        }
        long t = System.currentTimeMillis();
    }

    private class Entity implements RecyclerBanner.BannerEntity {
        String url;

        public Entity(String url) {

            this.url = url;

        }

        @Override
        public String getUrl() {

            return url;

        }

    }
}

