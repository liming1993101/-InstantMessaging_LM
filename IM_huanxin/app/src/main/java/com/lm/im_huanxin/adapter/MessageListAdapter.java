package com.lm.im_huanxin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lm.im_huanxin.R;
import com.lm.im_huanxin.entity.ChatEntity;
import com.lm.im_huanxin.entity.MessageListEntity;
import com.lm.im_huanxin.ui.ChatActivity;
import com.lm.im_huanxin.ui.ContactRequestctivity;
import com.lm.im_huanxin.ui.widget.CirCleImgView;
import com.lm.im_huanxin.utils.Contstants;
import com.lm.im_huanxin.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/17.
 */
public class MessageListAdapter extends RecyclerView.Adapter {

    private List<MessageListEntity>listItems=new ArrayList<MessageListEntity>();
    private Context context;

    public void setListItems(List<MessageListEntity> listItems) {
        this.listItems = listItems;
    }

    public MessageListAdapter(Context context, List<MessageListEntity> listItems) {
        this.context=context;
        this.listItems=listItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.item_message_list,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MessageListEntity entity=listItems.get(position);
        if (listItems.get(position).getMsgCount()==0)
        {
            ((ViewHolder)holder).msgCount.setVisibility(View.INVISIBLE);
        }
        else {
            ((ViewHolder)holder).msgCount.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).msgCount.setText(listItems.get(position).getMsgCount()+"");
        }

        if (listItems.get(position).getMsgType()==1)
        {
            ((ViewHolder)holder).msg.setText(listItems.get(position).getMsgContent());
        }
        else if(entity.getFriendName().equals("好友请求")) {

            ((ViewHolder)holder).msg.setText(listItems.get(position).getMsgContent());
        }
        else if(entity.getFriendName().equals("群组请求")) {

            ((ViewHolder)holder).msg.setText(listItems.get(position).getMsgContent());
        }
        else
        {
            ((ViewHolder)holder).msg.setText("图片");
        }
        ((ViewHolder)holder).name.setText(listItems.get(position).getFriendName());

        ((ViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(entity.getFriendName().equals("好友请求")||entity.getFriendName().equals("群组请求")) {

                    context.startActivity(new Intent(context, ContactRequestctivity.class));
                }
                else if (listItems.get(position).getChatType()==Contstants.GROUP_CHAT)
                {
                    context.startActivity(new Intent(context, ChatActivity.class).
                            putExtra("groupID",listItems.get(position).getFriendName()).putExtra("chat_model",
                            Contstants.GROUP_CHAT).putExtra("contacts",listItems.get(position).getFriendName()));
                }
                else
                context.startActivity(new Intent(context, ChatActivity.class).putExtra("contacts",listItems.get(position).getFriendName()));
            }
        });

        ((ViewHolder)holder).msgTime.setText(getStringDate(listItems.get(position).getMsgTime()));

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CirCleImgView headImg;
        private TextView name;
        private TextView msg;
        private TextView msgTime;
        private TextView msgCount;
        public ViewHolder(View itemView) {
            super(itemView);
            headImg= (CirCleImgView) itemView.findViewById(R.id.message_list_img);
            name= (TextView) itemView.findViewById(R.id.message_list_name);
            msg= (TextView) itemView.findViewById(R.id.message_list_content);
            msgCount= (TextView) itemView.findViewById(R.id.msg_list_count);
            msgTime= (TextView) itemView.findViewById(R.id.msg_list_time);


        }
    }
    private String getStringDate(long time)
    {
        SimpleDateFormat formatter    =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currenTime=null;
        Date date=null;
        try {
            currenTime=formatter.parse(new TimeUtil().getNowDate());
            date = formatter.parse(new TimeUtil().timeConvert(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(currenTime);
        calendar2.setTime(date);
        int MONTH2=calendar2.get(Calendar.MONTH)+1;
        int DAY2=calendar2.get(Calendar.DAY_OF_MONTH);
        int hour = calendar2.get(Calendar.HOUR_OF_DAY);
        int minute=calendar2.get(Calendar.MINUTE);
        int dayCount = (int) ((calendar1.getTimeInMillis()-calendar2.getTimeInMillis())/(1000*3600*24));
        if (dayCount==0)
        {
            return hour+":"+minute;
        }
        else if (dayCount==1)
        {
            return "昨天";
        }
        else
        {
            return MONTH2+"月"+DAY2+"日";
        }
    }
}
