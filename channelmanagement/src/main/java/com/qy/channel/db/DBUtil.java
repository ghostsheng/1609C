package com.qy.channel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qy.channel.app.ChannelApplication;
import com.qy.channel.bean.ChannelItem;
import com.qy.channel.bean.ChannelManage;

import java.util.ArrayList;

public class DBUtil {
    private static DBUtil mInstance;
    private Context mContext;
    private SQLHelper mSQLHelp;
    private SQLiteDatabase mSQLiteDatabase;

    private DBUtil(Context context) {
        mContext = context;
        mSQLHelp = new SQLHelper(context);
        mSQLiteDatabase = mSQLHelp.getWritableDatabase();
    }

    /**
     * 初始化数据库操作DBUtil类
     */
    public static DBUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBUtil(context);
        }
        return mInstance;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        mSQLHelp.close();
        mSQLHelp = null;
        mSQLiteDatabase.close();
        mSQLiteDatabase = null;
        mInstance = null;
    }

    /**
     * 添加数据
     */
    public void insertData(ContentValues values) {
        mSQLiteDatabase.insert(SQLHelper.TABLE_CHANNEL, null, values);
    }

    /**
     * 更新数据
     *
     * @param values
     * @param whereClause
     * @param whereArgs
     */
    public void updateData(ContentValues values, String whereClause,
                           String[] whereArgs) {
        mSQLiteDatabase.update(SQLHelper.TABLE_CHANNEL, values, whereClause,
                whereArgs);
    }

    /**
     * 删除数据
     *
     * @param whereClause
     * @param whereArgs
     */
    public void deleteData(String whereClause, String[] whereArgs) {
        mSQLiteDatabase
                .delete(SQLHelper.TABLE_CHANNEL, whereClause, whereArgs);
    }

    /**
     * 查询数据
     *
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public Cursor selectData(String[] columns, String selection,
                             String[] selectionArgs, String groupBy, String having,
                             String orderBy) {
        Cursor cursor = mSQLiteDatabase.query(SQLHelper.TABLE_CHANNEL, columns, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }

    /**
     * 查询展示的tab数据
     *
     * @param context
     * @return
     */
    public static ArrayList<ChannelItem> getTabData(Context context) {
        Cursor cursor = DBUtil.getInstance(context).selectData(null, "selected=?", new String[]{"1"}, null, null, "orderId asc");
        ArrayList<ChannelItem> mChannelList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(SQLHelper.ID));
                String name = cursor.getString(cursor.getColumnIndex(SQLHelper.NAME));
                int orderId = cursor.getInt(cursor.getColumnIndex(SQLHelper.ORDERID));
                int selected = cursor.getInt(cursor.getColumnIndex(SQLHelper.SELECTED));
                int newItem = cursor.getInt(cursor.getColumnIndex(SQLHelper.ISNEWITEM));
                ChannelItem channelItem = new ChannelItem(id, name, orderId, selected, newItem);
                mChannelList.add(channelItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (mChannelList.size() < 1) {
            //如果没有数据 就加载默认的数据
            mChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(ChannelApplication.getApp().getSQLHelper()).getUserChannel());
        }
        return mChannelList;
    }
}