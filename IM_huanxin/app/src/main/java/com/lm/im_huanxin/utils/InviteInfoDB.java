package com.lm.im_huanxin.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lm.im_huanxin.entity.InviteInfoEntity;
import com.lm.im_huanxin.entity.MessageListEntity;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by Administrator on 2016/8/31.
 */
public class InviteInfoDB {

    private SQLiteDatabase db;
    private String _id;
    public InviteInfoDB(Context context)
    {
        db = context.openOrCreateDatabase(Contstants.DBNAME,
                Context.MODE_PRIVATE, null);
        _id=new SharePreferenceUtil(context,Contstants.SAVE_USER).getUser();
    }
    public void savaInvitInfo(InviteInfoEntity entity)
    {
        db.execSQL("CREATE table IF NOT EXISTS INVITEINFO"+_id+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,inviter TEXT,inviteType TEXT," +
                "inviteInfo TEXT,inviteTime TEXT,inviteReson TEXT,friendImg TEXT,groupId TEXT,dispose TEXT)");
        db.execSQL(
                "insert into INVITEINFO"+_id
                        +" (inviter,inviteType,inviteInfo,inviteTime,inviteReson,groupId,dispose) values(?,?,?,?,?,?,?)",
                new Object[] { entity.getInviter(), entity.getInviterType(),
                        entity.getInviterInfo(), entity.getInviteDate(), entity.getInviterReson(),entity.getGroupID(),1});
    }


    public LinkedList<InviteInfoEntity> getInviteInfo()
    {
        LinkedList<InviteInfoEntity>list=new LinkedList<InviteInfoEntity>();
        db.execSQL("CREATE table IF NOT EXISTS INVITEINFO"+_id+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,inviter TEXT,inviteType TEXT," +
                "inviteInfo TEXT,inviteTime TEXT,inviteReson TEXT,friendImg TEXT,groupId TEXT,dispose TEXT)");

        Cursor c = db.rawQuery("select * from INVITEINFO"+_id, null);
        while (c.moveToNext())
        {
            int id=c.getInt(c.getColumnIndex("_id"));
            String inviter = c.getString(c.getColumnIndex("inviter"));
            int inviteType = c.getInt(c.getColumnIndex("inviteType"));
            String inviteInfo = c.getString(c.getColumnIndex("inviteInfo"));
            int dispose=c.getInt(c.getColumnIndex("dispose"));
            long inviteTime = c.getLong(c.getColumnIndex("inviteTime"));
            String inviteReson= c.getString(c.getColumnIndex("inviteReson"));
            String groupId=c.getString(c.getColumnIndex("groupId"));
            InviteInfoEntity entity=new InviteInfoEntity();
            entity.setId(id);
            entity.setInviterType(inviteType);
            entity.setInviterInfo(inviteInfo);
            entity.setInviterReson(inviteReson);
            entity.setDispose(dispose);
            entity.setInviter(inviter);
            entity.setInviteDate(inviteTime);
            entity.setGroupID(groupId);
            list.add(entity);
        }
        c.close();
        return list;
    }

    public void disposeState(int id)
    {
        db.execSQL("UPDATE INVITEINFO"+_id+" SET dispose=? WHERE _id=?",new String[]{2+"",id+""});

    }


    public void colse()
    {
        db.close();
    }
}
