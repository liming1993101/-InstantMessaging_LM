package com.lm.im_huanxin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lm.im_huanxin.R;
import com.lm.im_huanxin.entity.ChatEntity;
import com.lm.im_huanxin.ui.ChatActivity;
import com.lm.im_huanxin.ui.VideoPlayActivity;
import com.lm.im_huanxin.utils.Contstants;
import com.lm.im_huanxin.utils.SharePreferenceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */
public class ChatAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {


    private static final int TYPE_MSG_T0=0;
    private static final int   TYPE_MSG_FROM=1;

    public List<ChatEntity> getListItems() {
        return listItems;
    }

    private int VIEW_TYPE=1;
    private List<ChatEntity> listItems=new ArrayList<ChatEntity>();

    public void setListItems(List<ChatEntity> listItems) {
        this.listItems = listItems;
    }

    private Context context;
    private DisplayImageOptions options;

    public ChatAdapter(Context context, List<ChatEntity> listItems) {
        this.context=context;
        this.listItems=listItems;
        this.options=new DisplayImageOptions.Builder().
                cacheInMemory().cacheOnDisc().
                showImageForEmptyUri(R.mipmap.default_error).
                showImageOnFail(R.mipmap.default_error).
                imageScaleType(ImageScaleType.IN_SAMPLE_INT)//图片显示方式
                .bitmapConfig(Bitmap.Config.ARGB_4444).build();//設置圖片配置信息  對圖片進行處理防止內存溢

    }

    @Override
    public int getItemViewType(int position) {
        if (listItems.get(position).getMsgSource()==1)
        {
            return TYPE_MSG_T0;
        }
        return TYPE_MSG_FROM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_MSG_FROM)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.item_msg_from,parent,false);
            return new ViewHolder1(view);
        }
        View view= LayoutInflater.from(context).inflate(R.layout.item_msg_to,parent,false);

        return new ViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position)==TYPE_MSG_FROM)
        {

            if (listItems.get(position).getMsgType()== Contstants.MSG_TEXT) {
                ((ViewHolder1) holder).videoMark.setVisibility(View.GONE);
                ((ViewHolder1) holder).mUserName.setText(listItems.get(position).getUser());
                ((ViewHolder1) holder).mMsg.setVisibility(View.VISIBLE);
                ((ViewHolder1) holder).mMsg.setText(listItems.get(position).getMsg());
                ((ViewHolder1) holder).mMsgImg.setVisibility(View.GONE);
            }
            else if (listItems.get(position).getMsgType()== Contstants.MSG_IMG)
            {
                ((ViewHolder1) holder).videoMark.setVisibility(View.GONE);
                ((ViewHolder1) holder).mUserName.setText(listItems.get(position).getUser());
                ((ViewHolder1) holder).mMsgImg.setVisibility(View.VISIBLE);
                ((ViewHolder1) holder).mMsg.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(listItems.get(position).getImgurl(),((ViewHolder1) holder).mMsgImg,options);


            }
            else if (listItems.get(position).getMsgType()== Contstants.MSG_VIDEO)
            {
                ((ViewHolder1) holder).videoMark.setVisibility(View.VISIBLE);
                ((ViewHolder1) holder).mUserName.setText(listItems.get(position).getUser());
                ((ViewHolder1) holder).mMsgImg.setVisibility(View.VISIBLE);
                ((ViewHolder1) holder).mMsg.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(listItems.get(position).getVideoUpEntity().getVideoThumImage(),((ViewHolder1) holder).mMsgImg,options);

            }

        }
        else {

            if (listItems.get(position).getMsgType()==Contstants.MSG_TEXT) {
                ((ViewHolder2) holder).videoMark.setVisibility(View.GONE);
                ((ViewHolder2) holder).mUserName.setText(listItems.get(position).getUser());
                ((ViewHolder2) holder).mMsg.setVisibility(View.VISIBLE);
                ((ViewHolder2) holder).mMsg.setText(listItems.get(position).getMsg());
                ((ViewHolder2) holder).mMsgImg.setVisibility(View.GONE);
            }
            else if (listItems.get(position).getMsgType()==Contstants.MSG_IMG)
            {
                ((ViewHolder2) holder).videoMark.setVisibility(View.GONE);
                ((ViewHolder2) holder).mMsg.setVisibility(View.GONE);
                    ((ViewHolder2) holder).mUserName.setText(listItems.get(position).getUser());
                    ((ViewHolder2) holder).mMsgImg.setVisibility(View.VISIBLE);

                    if (listItems.get(position).isFistSend()) {
                        String imageUrl = ImageDownloader.Scheme.FILE.wrap(listItems.get(position).getImgurl()+"");
                        ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view,
                                                          Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
//                        Bitmap mBitmap = Bitmap.createScaledBitmap(loadedImage,((ViewHolder2) holder).mMsgImg.getWidth(),
//                                ((ViewHolder2) holder).mMsgImg.getHeight(), true);
                                ((ViewHolder2) holder).mMsgImg.setImageBitmap(loadedImage);
                            }

                        });
                }
                else
                {

                    ImageLoader.getInstance().displayImage(listItems.get(position).getImgurl()+"",((ViewHolder2) holder).mMsgImg,options);

                }
            }
            else if (listItems.get(position).getMsgType()==Contstants.MSG_VIDEO)
            {
                ((ViewHolder2) holder).videoMark.setVisibility(View.VISIBLE);
                ((ViewHolder2) holder).mMsg.setVisibility(View.GONE);
                ((ViewHolder2) holder).mUserName.setText(listItems.get(position).getUser());
                ((ViewHolder2) holder).mMsgImg.setVisibility(View.VISIBLE);
                if (listItems.get(position).isImgSendState())
                {
                    ((ViewHolder2) holder).pb.setVisibility(View.GONE);
                }
                else
                {
                    ((ViewHolder2) holder).pb.setVisibility(View.VISIBLE);
                }

                if (listItems.get(position).isFistSend()) {
                    String imageUrl = ImageDownloader.Scheme.FILE.wrap(listItems.get(position).getVideoUpEntity().getVideoThumImage()+"");
                    ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
//                        Bitmap mBitmap = Bitmap.createScaledBitmap(loadedImage,((ViewHolder2) holder).mMsgImg.getWidth(),
//                                ((ViewHolder2) holder).mMsgImg.getHeight(), true);
                            ((ViewHolder2) holder).mMsgImg.setImageBitmap(loadedImage);
                        }

                    });
                }
                else
                {

                    String user=new SharePreferenceUtil(context,Contstants.SAVE_USER).getUser();
                    if (listItems.get(position).getVideoUpEntity().isLocal())
                    {
                       String  imageUrl= ImageDownloader.Scheme.FILE.wrap(listItems.get(position).getVideoUpEntity().getVideoThumImage()+"");
                        ImageLoader.getInstance().displayImage(imageUrl,((ViewHolder2) holder).mMsgImg, options);

                    }
                    else
                        ImageLoader.getInstance().displayImage(listItems.get(position).getVideoUpEntity().getVideoThumImage(),((ViewHolder2) holder).mMsgImg, options);


                }

            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listItems.get(position).getMsgType()== Contstants.MSG_VIDEO) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(listItems.get(position).getVideoUpEntity().getVideoPath())),"video/mp4");
                    context.startActivity(intent);
//                    context.startActivity(new Intent(context, VideoPlayActivity.class).putExtra("video_info", listItems.get(position)));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView mUserName;
        private TextView mMsg;
        private ImageView mMsgImg;
        private ImageView mHeadImg;
        private ImageView videoMark;
        public ViewHolder1(View itemView) {
            super(itemView);
            mUserName= (TextView) itemView.findViewById(R.id.chat_from_name);
            mMsg= (TextView) itemView.findViewById(R.id.from_chat_text);
            mMsgImg= (ImageView) itemView.findViewById(R.id.from_chat_img);
            mHeadImg= (ImageView) itemView.findViewById(R.id.chat_from_img);
            videoMark= (ImageView) itemView.findViewById(R.id.chat_from_video_mark);
        }
    }
    class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView mUserName;
        private TextView mMsg;
        private ImageView mMsgImg;
        private ImageView mHeadImg;
        private ProgressBar pb;
        private ImageView videoMark;

        public ViewHolder2(View itemView) {
            super(itemView);

            mUserName= (TextView) itemView.findViewById(R.id.chat_my_name);
            mMsg= (TextView) itemView.findViewById(R.id.to_chat_text);
            mMsgImg= (ImageView) itemView.findViewById(R.id.to_chat_img);
            mHeadImg= (ImageView) itemView.findViewById(R.id.chat_my_img);
            pb= (ProgressBar) itemView.findViewById(R.id.state_pb);
            videoMark= (ImageView) itemView.findViewById(R.id.chat_to_video_mark);
        }
        }

}
