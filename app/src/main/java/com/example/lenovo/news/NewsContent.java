package com.example.lenovo.news;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lenovo on 2017/4/13.
 */

public class NewsContent extends AppCompatActivity {
    private WebView webView;
    private TextView textView;
    private TextView newsCome,gongGao,shenGao,sheYing;
    private String shey,gong,shen,news;
    private String subTitle;
    private String subContent;
    private final static int SHOW_RESPONSE=0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    JSONObject jsonObject = new JSONObject();
                    JSONObject jsonobject_news;
                    try {
                        jsonObject=new JSONObject(response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray=null;
                    try {
                        jsonobject_news=jsonObject.getJSONObject("data");
                        subContent=jsonobject_news.getString("content");
                        subTitle=jsonobject_news.getString("subject");
                        shey=jsonobject_news.getString("sheying");
                        gong=jsonobject_news.getString("gonggao");
                        shen=jsonobject_news.getString("shengao");
                        news=jsonobject_news.getString("newscome");
                        subContent=subContent.replace("100%","80%");
                        webView.loadDataWithBaseURL(null,subContent,"text/html","utf-8",null);
                        textView.setText(subTitle);
                        newsCome.setText("来源："+news);
                        gongGao.setText("供稿："+gong);
                        shenGao.setText("审稿："+shen);
                        sheYing.setText("摄影："+shey);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };
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
        sendRequestWithUrlConnection(contentUrl);
    }
    private void sendRequestWithUrlConnection(final String urlString){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=new URL(urlString);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder stringBuilder=new StringBuilder();
                    String line;
                    if((line=reader.readLine())!=null){
                        stringBuilder.append(line);
                    }
                    Message message=new Message();
                    message.what=SHOW_RESPONSE;
                    message.obj=stringBuilder.toString();
                    handler.sendMessage(message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(connection==null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}
