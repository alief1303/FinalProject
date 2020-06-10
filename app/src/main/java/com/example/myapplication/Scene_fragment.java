package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class Scene_fragment extends Fragment {
    private ImageButton make_record_btn;
    public List<Adapater_common_type> Scene;
    private String movie;
    private String date;
    Scene_fragment(String Movie,String Date)
    {
     this.movie=Movie;
     this.date=Date;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scene_fragment, container, false);

       /* make_record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), WebViewMakeRecordSearch.class);
                startActivity(i);
            }
        });*/
        Scene=new ArrayList<Adapater_common_type>();

        AndroidDatabase androidDatabase = new AndroidDatabase(getActivity(), "Shield.db", null, 1);
        final SQLiteDatabase db = androidDatabase.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from Movie WHERE instr(upper(movie_name), upper(?)) > 0 and date=? order by scene", new String[]{movie,date});

        if (cursor.moveToFirst()) {
            do {
                Adapter_Scene scene=new Adapter_Scene();
                scene.start=cursor.getString(cursor.getColumnIndex("start_time"));
                scene.finish=cursor.getString(cursor.getColumnIndex("finish_time"));
                scene.hall=cursor.getString(cursor.getColumnIndex("projection_hall"));
                scene.price=cursor.getString(cursor.getColumnIndex("price"));
                scene.type=cursor.getString(cursor.getColumnIndex("special_effect"));
                scene.name=cursor.getString(cursor.getColumnIndex("movie_name"));
                scene.id=cursor.getInt(cursor.getColumnIndex("movie_id"));
                scene.seats=cursor.getString(cursor.getColumnIndex("serial_number"));
                scene.movie_type=cursor.getString(cursor.getColumnIndex("movie_type"));
                Scene.add(scene);
            }
            while (cursor.moveToNext());
        }


        GridLayoutManager mLayoutManager;
        RecyclerView mRecyclerView;
        FragmentRecyclerAdapter mAdapter;
        mRecyclerView = (RecyclerView)view.findViewById(R.id.Scene_recycler_view);

        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mAdapter = new FragmentRecyclerAdapter(getActivity(), Scene);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}