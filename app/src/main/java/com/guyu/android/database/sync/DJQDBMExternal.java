package com.guyu.android.database.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.DJQ;

import java.util.ArrayList;
import java.util.List;

public class DJQDBMExternal extends DBOptExternal {
    public DJQDBMExternal(Context paramContext) {
        super(paramContext, "DJQ");
    }
    public DJQDBMExternal(Context paramContext, String paramString) {
        super(paramContext, paramString);
    }
    public List<DJQ> getAll(){
        List<DJQ> list = new ArrayList<>();
        Cursor localCursor = query(null, null, null, null, null, null);
        while (localCursor.moveToNext()) {
            DJQ djq = new DJQ();
            djq.set_id(localCursor.getInt(localCursor.getColumnIndex("_id")));
            djq.setNAME(localCursor.getString(localCursor.getColumnIndex("NAME")));
            list.add(djq);
        }
        localCursor.close();
        return list;
    }
    public boolean createTable(){
        String[] strs={
                "CREATE TABLE DJQ (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, NAME VARCHAR (50, 200) NOT NULL)",
                "INSERT INTO DJQ (_id, NAME) VALUES (1, '三水瑶族')",
                "INSERT INTO DJQ (_id, NAME) VALUES (2, '星子镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (3, '瑶安瑶族')",
                "INSERT INTO DJQ (_id, NAME) VALUES (4, '大路边镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (5, '丰阳镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (6, '东陂镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (7, '西岸镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (8, '保安镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (9, '龙坪镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (10, '龙坪国有林场')",
                "INSERT INTO DJQ (_id, NAME) VALUES (11, '连州镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (12, '西江镇')",
                "INSERT INTO DJQ (_id, NAME) VALUES (13, '九陂镇')", };
        try {
            SQLiteDatabase sql =  this.getmDbHelper().getWritableDatabase();

            for (int i = 0; i < strs.length; i++) {
                sql.execSQL(strs[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
