package com.lm.im_huanxin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lm.im_huanxin.R;
import com.lm.im_huanxin.entity.VideoUpEntity;
import com.lm.im_huanxin.ui.UpVideoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2016/8/23.
 */
public class VideoUpAdatper extends RecyclerView.Adapter<VideoUpAdatper.ViewHolder> {

    private DisplayImageOptions options;
    private List<VideoUpEntity> listVideo;
     private Context context;

    public OnSelectContactsListener onSelectContactsListener;
    public interface OnSelectContactsListener
    {
        public void selectContacts(int position);
    }

    public VideoUpAdatper(Context context, List<VideoUpEntity> listVideo) {
        this.context=context;
        this.listVideo=listVideo;
        this.options=new DisplayImageOptions.Builder().
                cacheInMemory().cacheOnDisc().
                showImageForEmptyUri(R.mipmap.default_error).
                showImageOnFail(R.mipmap.default_error).
                imageScaleType(ImageScaleType.IN_SAMPLE_INT)//图片显示方式
                .bitmapConfig(Bitmap.Config.ARGB_4444).build();//設置圖片配置信息  對圖片進行處理防止內存溢
    }

    public List<VideoUpEntity> getListVideo() {
        return listVideo;
    }

    public void setListVideo(List<VideoUpEntity> listVideo) {
        this.listVideo = listVideo;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.item_up_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {

        if (listVideo.get(position).isMark())
        {
            holder.mMark.setImageResource(R.mipmap.btn_selected);
        }
        else
        {
            holder.mMark.setImageResource(R.mipmap.btn_unselected);
        }

        String imageUrl = ImageDownloader.Scheme.FILE.wrap(listVideo.get(position).getVideoThumImage()+"");
        ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Bitmap mBitmap = Bitmap.createScaledBitmap(loadedImage,holder.mPreview.getWidth(),holder.mPreview.getHeight(), true);
                holder.mPreview.setImageBitmap(mBitmap);
            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onSelectContactsListener.selectContacts(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listVideo.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPreview;
        private ImageView mMark;
        public ViewHolder(View itemView) {
            super(itemView);
            mPreview= (ImageView) itemView.findViewById(R.id.up_video_preview);
            mMark= (ImageView) itemView.findViewById(R.id.up_video_mark);
        }
    }

    public void onAttach(UpVideoActivity context)
    {
        onSelectContactsListener=(OnSelectContactsListener) context;
    }
    public void setOnItemSelectedListener(UpVideoActivity context) {
        // TODO Auto-generated method stub
        onAttach(context);
    }
}
