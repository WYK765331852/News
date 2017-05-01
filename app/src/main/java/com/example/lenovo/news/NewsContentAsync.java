package com.example.lenovo.news;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Created by lenovo on 2017/4/27.
 */

public class NewsContentAsync extends AppCompatActivity{
    private WebView webView;
    private TextView textView;
    private TextView newsCome,gongGao,shenGao,sheYing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        webView= (WebView) findViewById(R.id.webView);
        textView= (TextView) findViewById(R.id.subTitle);
        newsCome= (TextView) findViewById(R.id.newsCome);
        gongGao= (TextView) findViewById(R.id.gonggao);
        shenGao= (TextView) findViewById(R.id.shengao);
        sheYing= (TextView) findViewById(R.id.sheying);

        Bundle bundle=getIntent().getExtras();
        String index=bundle.getString("index");
        String contentUrl="http://open.twtstudio.com/api/v1/news/"+index;
        new ContentAsyncTask().execute(contentUrl);
    }

    private ContentBean getJsonData(String url){
        ContentBean contentBean= new ContentBean();
        try {
            String jsonString=readStream(new URL(url).openStream());
            JSONObject jsonObject=new JSONObject();
            JSONObject jsonObject_news;
            try {
                jsonObject=new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray=null;
            try {
                jsonObject_news=jsonObject.getJSONObject("data");
                Log.d("jjjj", String.valueOf(jsonObject_news));
                contentBean.subContent=jsonObject_news.getString("content");
                contentBean.subTitle=jsonObject_news.getString("subject");
                contentBean.newsCome=jsonObject_news.getString("newscome");
                contentBean.gongGao=jsonObject_news.getString("gonggao");
                contentBean.sheYing=jsonObject_news.getString("sheying");
                contentBean.shenGao=jsonObject_news.getString("shengao");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBean;
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

    private class ContentAsyncTask extends AsyncTask<String,Void,ContentBean> {
        @Override
        protected ContentBean doInBackground(String... params) {
            return getJsonData(params[0]);
        }
        @Override
        protected void onPostExecute(ContentBean contentBean) {
            super.onPostExecute(contentBean);
            textView.setText(contentBean.subTitle);
            contentBean.subContent=contentBean.subContent.replace("100%","80%");
            webView.loadDataWithBaseURL(null,contentBean.subContent,"text/html","utf-8",null);
            newsCome.setText("来源："+contentBean.newsCome);
            gongGao.setText("供稿："+contentBean.gongGao);
            if(contentBean.shenGao!=null){
                shenGao.setText("审稿："+contentBean.shenGao);
            }
            if (contentBean.sheYing!=null){
                sheYing.setText("摄影："+contentBean.sheYing);
            }
        }
    }
}
