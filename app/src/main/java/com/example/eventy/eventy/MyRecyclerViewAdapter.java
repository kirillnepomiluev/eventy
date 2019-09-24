package com.example.eventy.eventy;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder> {
        private List<FeedItem> feedItemList;
        private Context mContext;
        private OnItemClickListener onItemClickListener;

        public MyRecyclerViewAdapter(Context context, List<FeedItem> feedItemList) {
            this.feedItemList = feedItemList;
            this.mContext = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
            FeedItem feedItem = feedItemList.get(i);

            //Render image using Picasso library
            //if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
            String sr = feedItem.getThumbnail();


                Picasso.get().load(sr)
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(customViewHolder.imageView);

                final int n = i;


            //Setting text view title
            customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(feedItemList.get(n));
                }
            };
            customViewHolder.imageView.setOnClickListener(listener);
            customViewHolder.textView.setOnClickListener(listener);
        }

        @Override
        public int getItemCount() {
            return (null != feedItemList ? feedItemList.size() : 0);
        }

        public OnItemClickListener getOnItemClickListener() {
            return onItemClickListener;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            protected ImageView imageView;
            protected TextView textView;

            public CustomViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
                this.textView = (TextView) view.findViewById(R.id.title);
            }
        }
    }




