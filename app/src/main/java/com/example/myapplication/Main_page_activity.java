package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.view.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Main_page_activity extends AppCompatActivity  {
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> mPermissionList = new ArrayList<>();
    public static List<Adapater_common_type> movie_list = new ArrayList<Adapater_common_type>();
    private void checkPermission() {
        mPermissionList.clear();

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        if (mPermissionList.isEmpty()) {

        } else {
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
            ActivityCompat.requestPermissions(Main_page_activity.this, permissions, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    androidx.appcompat.widget.Toolbar toolbar;
    SearchView mSearchView;
    WaterFallAdapter mAdapter;
    private AndroidDatabase androidDatabase;
    // private ImageView welcomeImg = null;
    private static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_layout);
        Fresco.initialize(Main_page_activity.this);
        toolbar=(androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        androidDatabase = new AndroidDatabase(this, "Shield.db", null, 1);
        SQLiteDatabase db = androidDatabase.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
        if(cursor.getCount()==0)
            Toast.makeText(this,"No user login!",Toast.LENGTH_SHORT).show();
        if(movie_list.size()>0)
            movie_list.clear();
        RequestForMovieInform(0) ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.clear();
        getMenuInflater().inflate(R.menu.main_page_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        mSearchView = (SearchView) menuItem.getActionView();
        setListener();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.app_bar_user:
                SQLiteDatabase db = androidDatabase.getWritableDatabase();
                Cursor cursor = db.rawQuery("select * from User where Islogin=?",new String[]{"1"});
                if(cursor.getCount()==0)
                {
                    Intent intent = new Intent(Main_page_activity.this,Login_page_activity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(Main_page_activity.this,User_page_activity.class);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListener(){

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                movie_list.clear();
                mAdapter.notifyDataSetChanged();
                search_from_database(query);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("okhttp_error","word input");
                return false;
            }

        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                movie_list.clear();
                mAdapter.notifyDataSetChanged();
                RequestForMovieInform(1);
                return false;
            }
        });
    }

    private int setSpanSize(int position, List<Adapater_common_type> Adapater_common_type) {
        int count;
        if (position%5==0) {
            count = 2;
        } else {
            count = 1;
        }
        return count;
    }
    GridLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    private void init() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(Main_page_activity.this, 1);
        mAdapter = new WaterFallAdapter(this, movie_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<Adapater_common_type> buildData(){
        String[] names = {"kait","bloody","dark moon rising","json","omar"};
        String[] imgUrs = {
                "http://pic.szjal.cn/img/a49210ffdb7a6a7b1c4561295dfcb540.jpg",
                "http://pic.szjal.cn/img/023d29650c1969a6046216744d15ca3b.jpg",
                "http://pic.szjal.cn/img/18e82508237055e6b990143baf1ae8cf.jpg",
                "http://pic.szjal.cn/img/3f4103f3fe93ba41f6ac4fc6c5a64486.jpg",
                "http://pic.szjal.cn/img/53f1886c0494f09d39c9ad146a9f5aaf.jpg"
        };
        List<Adapater_common_type> list = new ArrayList<>();
        Adapter_recycler_banner banner=new Adapter_recycler_banner();
        list.add(banner);
        for(int i=0;i<names.length;i++)
        {
            Movie_card p = new Movie_card();
            p.img_url = imgUrs[i];
            p.name = names[i];
            p.imgHeight = (i % 2)*100 + 400;
            list.add(p);
        }

        return list;
    }

    public List<Adapater_common_type> RequestForMovieInform(final int mode)  {
        OkHttpClient client = new OkHttpClient();
        final SQLiteDatabase db = androidDatabase.getWritableDatabase();
        final List<Adapater_common_type> list=new ArrayList<>();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("key_word", "");
        Request request = new Request.Builder().url("http://nightmaremlp.pythonanywhere.com/appnet/movie_info").post(formBuilder.build()).build();

            final Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    runOnUiThread(new Runnable() {

                        @Override

                        public void run() {
                            Log.d("okhttp_error", e.getMessage());
                            Toast error_toast = Toast.makeText(Main_page_activity.this, "Could not connect to server", Toast.LENGTH_LONG);
                            error_toast.setGravity(Gravity.CENTER, 0, 0);
                            error_toast.show();
                            RequestForMovieInform(0);
                        }

                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String res = response.body().string();

                    runOnUiThread(new Runnable() {

                        @Override

                        public void run() {
                            try {

                                    Log.d("okhttp_error", res);
                                    JSONArray jsonArray = new JSONArray(res);

                                    Cursor cursor = db.rawQuery("select * from Movie", new String[]{});
                                    for (int i = 0; i < jsonArray.length(); i++) {


                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        int[] arr = new int[200];
                                        if(cursor.getCount()!=0) {

                                            if(cursor.moveToFirst()) {
                                                do {
                                                    arr[cursor.getInt(cursor.getColumnIndex("movie_id"))]=1;
                                                }
                                                while(cursor.moveToNext());

                                            }
                                        }
                                        if (arr[jsonObject.getInt("movie_id")]!=1) {
                                            /*Log.d("okhttp_error", String.valueOf(jsonObject.getInt("movie_id")) + " " + cursor.getCount());
                                            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                            Date date=format1.parse(jsonObject.getString("date"));
                                            Log.d("okhttp_error", jsonObject.getString("date"));*/
                                            String whole_time;
                                            if(jsonObject.getString("date").equals("None"))
                                                whole_time="None";
                                            else
                                            {
                                            whole_time=jsonObject.getString("date")+","+jsonObject.getString("start_time");

                                            }
                                            db.execSQL("insert into Movie values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                                                    , new Object[]{jsonObject.getInt("movie_id"), jsonObject.getString("name"),
                                                            jsonObject.getString("movie_type"), jsonObject.getString("introduction"),
                                                            jsonObject.getString("length"), jsonObject.getString("special_effect"),
                                                            jsonObject.getString("comments"), jsonObject.getString("country"),
                                                            jsonObject.getString("actors"), jsonObject.getString("director"),
                                                            jsonObject.getString("release_data"),jsonObject.getString("date"),
                                                            jsonObject.getString("start_time"), jsonObject.getString("finish_time"),
                                                            jsonObject.getString("scene"), jsonObject.getString("projection_hall"),
                                                            jsonObject.getString("price"), jsonObject.getString("cinemas"),
                                                            "http://nightmaremlp.pythonanywhere.com/img/"+jsonObject.getString("img_url"), jsonObject.getString("serial_number"),
                                                            whole_time,jsonObject.getString("score")

                                                    });

                                        }
                                        else
                                        {
                                            ContentValues values = new ContentValues();
                                            values.put("score", jsonObject.getString("score"));
                                            db.update("Movie", values, "movie_id=?",
                                                    new String[]{String.valueOf(jsonObject.getInt("movie_id"))});
                                        }
                                    }
                                Adapter_recycler_banner banner=new Adapter_recycler_banner();
                                movie_list.add(banner);
                                if (mode==1)
                                    mAdapter.notifyDataSetChanged();
                                final SQLiteDatabase db1 = androidDatabase.getWritableDatabase();
                                cursor = db1.rawQuery("select * from Movie where scene='0' ", new String[]{});
                                if(cursor.moveToFirst()) {

                                        do {
                                            {
                                                Log.d("okhttp_error", cursor.getString(cursor.getColumnIndex("whole_time")));
                                                Movie_card movie_card = new Movie_card();
                                                movie_card.img_url = cursor.getString(cursor.getColumnIndex("img_url"));
                                                movie_card.name = cursor.getString(cursor.getColumnIndex("movie_name"));
                                                movie_card.release_data = cursor.getString(cursor.getColumnIndex("premiere_date"));
                                                movie_card.length = cursor.getString(cursor.getColumnIndex("movie_length"));
                                                movie_card.score = cursor.getString(cursor.getColumnIndex("score"));
                                                movie_card.special_effect = cursor.getString(cursor.getColumnIndex("special_effect"));
                                                movie_card.actors = cursor.getString(cursor.getColumnIndex("actors"));
                                                movie_list.add(movie_card);
                                                if (mode==1)
                                                    mAdapter.notifyDataSetChanged();
                                            }
                                    }
                                    while(cursor.moveToNext());
                                }
                                if (mode == 0)
                                init();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }/* catch (ParseException e) {
                                e.printStackTrace();
                            }*/
                        }

                    });
                }
            });
        return list;
    }

    private List<Adapater_common_type> search_from_database(String keyword)
    {
        final List<Adapater_common_type> list = new ArrayList<>();
        SQLiteDatabase db = androidDatabase.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from Movie WHERE instr(upper(movie_name), upper(?)) > 0 and scene=?  --case-insensitive", new String[]{keyword,"0"});
        if(keyword=="")
            cursor = db.rawQuery("select * from Movie group by movie_name", new String[]{});

        if(cursor.moveToFirst()){
            do{
                Movie_card movie_card = new Movie_card();
                movie_card.img_url =cursor.getString(cursor.getColumnIndex("img_url"));
                movie_card.name = cursor.getString(cursor.getColumnIndex("movie_name"));
                movie_card.release_data=cursor.getString(cursor.getColumnIndex("premiere_date"));
                movie_card.length= cursor.getString(cursor.getColumnIndex("movie_length"));
                movie_card.score= cursor.getString(cursor.getColumnIndex("score"));
                movie_card.special_effect= cursor.getString(cursor.getColumnIndex("special_effect"));
                movie_card.actors= cursor.getString(cursor.getColumnIndex("actors"));
                if(keyword!="") {
                    mAdapter.mData.add(movie_card);
                    mAdapter.notifyItemChanged(mAdapter.mData.size());
                    mAdapter.notifyItemRangeChanged(mAdapter.mData.size(), 1);
                }
                else
                    list.add(movie_card);
            }
            while(cursor.moveToNext());
        }
        return list;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        movie_list.clear();
        mAdapter.notifyDataSetChanged();
        RequestForMovieInform(1);
    }
}

