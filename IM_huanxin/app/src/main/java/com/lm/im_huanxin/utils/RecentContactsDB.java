package com.lm.im_huanxin.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lm.im_huanxin.entity.ChatEntity;
import com.lm.im_huanxin.entity.MessageListEntity;

import java.util.Collections;
import java.util.LinkedList;



/**
 * Created by Administrator on 2016/6/23.
 */
public class RecentContactsDB {
    private SQLiteDatabase db;
    private String _id;
    public RecentContactsDB(Context context)
    {
        db = context.openOrCreateDatabase(Contstants.DBNAME,
                Context.MODE_PRIVATE, null);
        _id=new SharePreferenceUtil(context,Contstants.SAVE_USER).getUser();
    }
    public void savaRctContacts(MessageListEntity entity)
    {
            db.execSQL("CREATE table IF NOT EXISTS "+_id+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,msgType TEXT,msgCount TEXT," +
                    "friendName TEXT,msgTime TEXT,msgContent TEXT,friendImg TEXT,chatType TEXT)");

        if (querryRctContacts(entity.getFriendName()))
        {
            deleteRctContacts(entity.getFriendName());
        }
        db.execSQL(
                "insert into " +_id
                        +" (msgType,msgCount,friendName,msgTime,msgContent,friendImg,chatType) values(?,?,?,?,?,?,?)",
                new Object[] { entity.getMsgType(), entity.getMsgCount(),
                        entity.getFriendName(), entity.getMsgTime(), entity.getMsgContent(),entity.getFirendImg(),entity.getChatType()});
    }


    public LinkedList<MessageListEntity> getRctContacts()
    {
        LinkedList<MessageListEntity>list=new LinkedList<MessageListEntity>();
        db.execSQL("CREATE table IF NOT EXISTS "
                +_id
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "msgType TEXT,msgCount TEXT,friendName TEXT,msgTime TEXT," +
                "msgContent TEXT,friendImg TEXT,chatType TEXT)");

        Cursor c = db.rawQuery("select * from "+_id, null);
        while (c.moveToNext())
        {
            int id=c.getInt(c.getColumnIndex("_id"));
            String friendName = c.getString(c.getColumnIndex("friendName"));
            int msgCount = c.getInt(c.getColumnIndex("msgCount"));
            long msgTime = c.getLong(c.getColumnIndex("msgTime"));
            int msgType = c.getInt(c.getColumnIndex("msgType"));
            int chatType=c.getInt(c.getColumnIndex("chatType"));
            String friendImg= c.getString(c.getColumnIndex("friendImg"));
            String msgContent=c.getString(c.getColumnIndex("msgContent"));
            MessageListEntity entity=new MessageListEntity();
            entity.setMsgType(msgType);
            entity.setMsgContent(msgContent);
            entity.setId(id);
            entity.setChatType(chatType);
            entity.setMsgTime(msgTime);
            entity.setMsgCount(msgCount);
            entity.setFirendImg(friendImg);
            entity.setFriendName(friendName);
            list.add(entity);
        }
        c.close();
        Collections.reverse(list);
        return list;
    }
    public boolean querryRctContacts(String friendName)
    {
        Cursor c = db.rawQuery("select * from "+_id+" where friendName=?",new String[]{friendName+""});

        if (c.moveToNext())
        {
            c.close();
            return true;
        }
        else
        {
            c.close();
            return false;
        }

    }

    public void deleteRctContacts(String friendName)
    {
        db.execSQL("DELETE FROM "+_id+" where friendName=?",new String[]{friendName+""});
    }
    public void reastMsg(String user)
    {
        db.execSQL("UPDATE "+_id+" SET msgCount=? WHERE friendName=?",new String[]{0+"",user+""});
    }

    public void colse()
    {
        db.close();
    }
}


