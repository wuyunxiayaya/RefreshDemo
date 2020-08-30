package com.example.refreshdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private RefreshLayout refreshLayout1;
    private ListView listView;
    private List listData;
    private static final int LIST_CODE = 1;
    private int mCount = 0,mOneCicle = 10;
    private ShowAdapter showAdapter;
    private List<MovieUtil> list = new ArrayList();
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case LIST_CODE:
                    for(int i = 0; i < 10; i ++){//展示10条
                        String movieUrl = ((Map)listData.get(i)).get("m_img_url").toString().trim();
                        String movieName = ((Map)listData.get(i)).get("m_name").toString().trim();
                        Map map = new HashMap();
                        map.put("name",movieName);
                        map.put("image",movieUrl);

                        MovieUtil movieUtil = new MovieUtil(movieName,movieUrl);
                        list.add(movieUtil);
                    }

                     showAdapter = new ShowAdapter(MainActivity.this,
                            list);
                    listView.setAdapter(showAdapter);
                    refreshPage();//页面1
            }

        }
    };

    //刷新 加载更多
    //static 代码段可以防止内存泄露
    static {
//    ClassicsHeader.REFRESH_HEADER_PULLDOWN = "下拉可以刷新";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在刷新...";
        ClassicsHeader.REFRESH_HEADER_LOADING = "正在加载...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "释放立即刷新";
        ClassicsHeader.REFRESH_HEADER_FINISH = "刷新完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = "刷新失败";
        ClassicsHeader.REFRESH_HEADER_SECONDARY = "释放进入二楼";
//        ClassicsHeader.REFRESH_HEADER_LASTTIME = "上次更新 M-d HH:mm";
//        ClassicsHeader.REFRESH_HEADER_LASTTIME = "'Last update' M-d HH:mm";
        ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载...";
//        ClassicsFooter.REFRESH_FOOTER_FINISH = "加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多数据了";

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        refreshLayout1 = (RefreshLayout)findViewById(R.id.refreshLayout);
        getData();
    }

    private void getData(){//请求数据接口  {"code":200,"list":[{"id":247,"cid":1,"m_name":"\n\n 。。。
        HttpUtil.sendOkHttpRequst("http://116.62.147.89:3333/getMainPageMovie", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("test_______","fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("test_______",responseData);
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<String, List>>(){
                        }.getType(),new MyTypeAdapter()).create();
                Map<String,List> dataDetail = gson.fromJson(responseData,new TypeToken<Map<String,List>>(){

                }.getType());//
                listData = new ArrayList();
                listData = dataDetail.get("list");
                handler.sendEmptyMessage(LIST_CODE);
            }
        });
    }

    //    刷新页面1
    private void refreshPage(){
//        mOneCicle一次加载展示多少个
//        mCount一共能加载多少次
//        setLoadMoreFinished
        //刷新

//        refreshLayout.
        refreshLayout1.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {//刷新
                refreshLayout.finishRefresh(1000);//传入false表示刷新失败
                getData();//获取数据
            }
        });
//         加载
        refreshLayout1.setOnLoadMoreListener(new OnLoadMoreListener() {//加载更多
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
                if(mCount==Math.ceil((listData.size())/10) || mOneCicle>listData.size()){//向上取整
                    refreshlayout.finishLoadMoreWithNoMoreData();//全部加载已完成
                }else{
                    refreshLayout1.finishLoadMore();//加载完成
                }
                mCount ++;
                if(mOneCicle>listData.size()){
                    return;
                }
                mOneCicle += 10;
                for(int i = mOneCicle-10; i < mOneCicle; i ++){//加载页面  每次加载十条
                    if(i<listData.size()){
                        String movieUrl = ((Map)listData.get(i)).get("m_img_url").toString().trim();
                        String movieName = ((Map)listData.get(i)).get("m_name").toString().trim();
                        Log.d("刷新页面：",movieName);
                        MovieUtil movieUtil = new MovieUtil(movieName,movieUrl);
                        list.add(movieUtil);
                    }
                }
                showAdapter.notifyDataSetChanged();
            }
        });
    }

}