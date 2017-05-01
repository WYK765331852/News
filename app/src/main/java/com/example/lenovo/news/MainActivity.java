package com.example.lenovo.news;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsBean> newsBeanList;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int lastVisibleItem=0;
    private final int PAGE_COUNT=5;
    private final static String URL="http://open.twtstudio.com/api/v1/news/1/page/1";
    private String [] indexList=new String[100];
    private Handler handler=new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
        new GetData().execute(URL);
        setOnScrollListener();


    }
    private void initView(){
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeReRefreshLayout);
        linearLayoutManager=new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
       // mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL_LIST));
        swipeRefreshLayout.setProgressViewOffset(false,0, (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP,24,getResources().getDisplayMetrics()));

    }

    private void setListener(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetData().execute(URL);
            }
        });
    }

    private void setOnScrollListener(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    if (newsAdapter.isFadeTips()==false&&lastVisibleItem+1==newsAdapter.getItemCount()){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                upDataRecyclerView(newsAdapter.getRealLastPosition(),newsAdapter.getRealLastPosition()+PAGE_COUNT);
                            }
                        },500);
                    }
                }
                if (newsAdapter.isFadeTips()==true&&lastVisibleItem+2==newsAdapter.getItemCount()){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            upDataRecyclerView(newsAdapter.getRealLastPosition(),newsAdapter.getRealLastPosition()+PAGE_COUNT);
                        }
                    },500);
                }
            }
        });
    }
    private List<NewsBean> getDatas(final int firstIndex,final int lastIndex ){
        List<NewsBean> resList=new ArrayList<>();
        for(int i=firstIndex;i<lastIndex;i++){
            if (i<newsBeanList.size()){
                resList.add(newsBeanList.get(i));
            }
        }
        return resList;
    }
    private void upDataRecyclerView(int fromIndex,int toIndex){
        List<NewsBean> resList=getDatas(fromIndex,toIndex);
        if (resList.size()>0){
            newsAdapter.updataList(resList,true);
        }else {
            newsAdapter.updataList(null,false);
        }
    }

    private List<NewsBean> getJsonData(String url){
        newsBeanList=new ArrayList<>();
        NewsBean newsBean;
        try {
            String jsonString=readStream(new URL(url).openStream());
            JSONObject jsonObject=new JSONObject(jsonString);
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                jsonObject=jsonArray.getJSONObject(i);
                newsBean = new NewsBean();
                newsBean.newsContent=jsonObject.getString("summary");
                newsBean.newsTitle=jsonObject.getString("subject");
                newsBean.newsImageUrl=jsonObject.getString("pic");
                newsBean.newsVisitCounts=jsonObject.getInt("visitcount");
                newsBean.newsComments=jsonObject.getInt("comments");
                indexList[i]=String.valueOf(jsonObject.getLong("index"));
                newsBean.index= String.valueOf(jsonObject.getLong("index"));
                newsBeanList.add(newsBean);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }
    private String readStream(InputStream is){
        InputStreamReader inputStreamReader;
        String result="";
        try {
            String line="";
            inputStreamReader=new InputStreamReader(is,"utf-8");
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            while ((line=bufferedReader.readLine())!=null){
                result+=line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private class GetData extends  AsyncTask<String,Void,List<NewsBean>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }
        @Override
        protected List<NewsBean> doInBackground(String... params) {
                return getJsonData(params[0]);
        }
        @Override
        protected void onPostExecute(final List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            if (newsAdapter==null) {
                newsAdapter = new NewsAdapter(MainActivity.this, newsBeen, true);
                mRecyclerView.setAdapter(newsAdapter);
                newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent=new Intent(MainActivity.this,NewsContentAsync.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("index",newsBeen.get(position).index);
                        Log.d("aa",newsBeen.get(position).index);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        /*Intent intent=new Intent(MainActivity.this,NewsContent.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("index",newsBeen.get(position).index);
                        intent.putExtras(bundle);
                        startActivity(intent);*/
                    }
                });
            }
            newsAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

        }
    }
}
