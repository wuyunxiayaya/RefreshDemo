package com.example.refreshdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ShowAdapter extends ArrayAdapter {
    private int resourceId;
    private ImageView imageView;
    private TextView textName;
    private Context context;
    public ShowAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, R.layout.item,objects);
        this.context = context;
        this.resourceId = R.layout.item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MovieUtil movieUtil = (MovieUtil) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        imageView = (ImageView)view.findViewById(R.id.image);
        textName = (TextView)view.findViewById(R.id.text);
        textName.setText(movieUtil.getMovieName());

        Glide
                .with(context)
                .load(movieUtil.getMovieUrl())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background).dontAnimate().override(800, 800))//默认加载图片
                .into(imageView);

        return view;
    }
}
