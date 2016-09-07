package com.lm.im_huanxin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lm.im_huanxin.R;
import com.lm.im_huanxin.emoji.Image;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2016/8/22.
 */
public class PhotoSendAdapter extends RecyclerView.Adapter<PhotoSendAdapter.ViewHolder> {

    private List<Image>listItems;
    private Context context;
    private boolean markSelect=false;

    public List<Image> getListItems() {
        return listItems;
    }

    private  DisplayImageOptions options;
    public PhotoSendAdapter(Context context, List<Image> listItems) {
        this.context=context;
        this.listItems=listItems;
        this.options=new DisplayImageOptions.Builder().
                cacheInMemory().cacheOnDisc().
                showImageForEmptyUri(R.mipmap.default_error).
                showImageOnFail(R.mipmap.default_error).
                imageScaleType(ImageScaleType.IN_SAMPLE_INT)//图片显示方式
                .bitmapConfig(Bitmap.Config.ARGB_4444).build();//設置圖片配置信息  對圖片進行處理防止內存溢
    }

    public void setListItems(List<Image> listItems) {
        this.listItems = listItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_photo_prview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        if (!listItems.get(position).isMark())
        {
            holder.markImg.setImageResource(R.mipmap.btn_unselected);
        }

        String imageUrl = ImageDownloader.Scheme.FILE.wrap(listItems.get(position).getPath()+"");
        ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Bitmap mBitmap = Bitmap.createScaledBitmap(loadedImage,holder.previewImg.getWidth(),holder.previewImg.getHeight(), true);
                holder.previewImg.setImageBitmap(mBitmap);
            }

        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listItems.get(position).isMark())
                {
                    holder.markImg.setImageResource(R.mipmap.btn_unselected);
                    listItems.get(position).setMark(false);
                }
                else
                {
                    holder.markImg.setImageResource(R.mipmap.btn_selected);
                    listItems.get(position).setMark(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView markImg,previewImg;
        public ViewHolder(View itemView) {
            super(itemView);
            markImg= (ImageView) itemView.findViewById(R.id.send_checkmark);
            previewImg= (ImageView) itemView.findViewById(R.id.photo_seng_image);
        }
    }
}
