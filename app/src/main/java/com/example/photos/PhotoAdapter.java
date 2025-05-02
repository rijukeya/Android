package com.example.photos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Photo> photos;

    public PhotoAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos = new ArrayList<>(photos);
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int index) {
        return photos.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load the image from the file path
        String filePath = photos.get(index).getFilePath();
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4; // Scale down the image
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.error); // Fallback for decoding failure
            }
        } else {
            imageView.setImageResource(R.drawable.error); // Fallback for missing files
        }

        return imageView;
    }

    public void updatePhotos(List<Photo> newPhotos) {
        this.photos.clear();
        this.photos.addAll(newPhotos);
        notifyDataSetChanged();
    }
}