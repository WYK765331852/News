package com.example.lenovo.news;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/4/14.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<NewsBean> mList;
    private LayoutInflater inflater;
    private Context mContent;
    private int normalType=0;
    private int footType=1;
    private boolean hasMore=true;
    private boolean fadeTipes=false;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Handler handler=new Handler(Looper.getMainLooper());
    public NewsAdapter(Context context, List<NewsBean> mData,boolean hasMore){
        this.mList=mData;
        this.mContent=context;
        this.hasMore=hasMore;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getItemCount() {
        return mList.size()+1;
    }
    public int getRealLastPosition(){
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==getItemCount()-1){
            return footType;
        }else{
            return normalType;
        }
    }
    class NormalHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvContent;
        public ImageView ivIcon;
        public TextView visitCount;
        public TextView comments;

        public NormalHolder(View itemView) {
            super(itemView);
            tvTitle= (TextView) itemView.findViewById(R.id.news_title);
            tvContent= (TextView) itemView.findViewById(R.id.news_content);
            ivIcon= (ImageView) itemView.findViewById(R.id.news_icon);
            visitCount= (TextView) itemView.findViewById(R.id.visitCount);
            comments= (TextView) itemView.findViewById(R.id.comments);
        }
    }
    class FootHolder extends RecyclerView.ViewHolder{
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips= (TextView) itemView.findViewById(R.id.foot);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==normalType){
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card,parent,false);
            NormalHolder normalHolder=new NormalHolder(v);
            return normalHolder;
        }else{
             FootHolder footHolder= new FootHolder(LayoutInflater.from(mContent).inflate(R.layout.layout_footview,null));
            return footHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalHolder){
            ((NormalHolder)holder).tvContent.setText(mList.get(position).newsContent);
            ((NormalHolder)holder).tvTitle.setText(mList.get(position).newsTitle);
            Glide.with(mContent).load(mList.get(position).newsImageUrl).into(((NormalHolder) holder).ivIcon);
            ((NormalHolder)holder).visitCount.setText("浏览量："+mList.get(position).newsVisitCounts);
            ((NormalHolder)holder).comments.setText("评论："+mList.get(position).newsComments);
        }else {
            ((FootHolder)holder).tips.setVisibility(View.VISIBLE);
            if (hasMore==true){
                fadeTipes=false;
                if (mList.size()>0){
                    ((FootHolder)holder).tips.setText("正在加载更多……");
                }
            }else {
                if (mList.size() > 0) {
                    ((FootHolder) holder).tips.setText("没有更多数据了哦");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            fadeTipes = true;
                            //hashMore设置为true，可以在下一次拉到底时显示“正在加载更多”。
                            hasMore = true;

                        }
                    }, 500);
                }
            }
        }
        if (onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }
        if (onItemLongClickListener!=null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position=holder.getLayoutPosition();
                    onItemLongClickListener.onItemLongClick(holder.itemView,position);
                    return true;
                }
            });
        }

    }
    public boolean isFadeTips(){
        return fadeTipes;
    }
    public void resetDatas(){
        mList=new ArrayList<>();
    }
    public void updataList(List<NewsBean> mNewList,boolean hasMore){
        if (mNewList!=null){
            mList.addAll(mNewList);
        }
        this.hasMore=hasMore;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener=onItemLongClickListener;
    }

}
