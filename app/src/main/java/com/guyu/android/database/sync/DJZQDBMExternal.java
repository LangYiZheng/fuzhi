package com.guyu.android.database.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guyu.android.database.DBOptExternal;
import com.guyu.android.gis.common.DJQ;
import com.guyu.android.gis.common.DJZQ;

import java.util.ArrayList;
import java.util.List;

public class DJZQDBMExternal extends DBOptExternal {
    public DJZQDBMExternal(Context paramContext) {
        super(paramContext, "DJZQ");
    }
    public DJZQDBMExternal(Context paramContext, String paramString) {
        super(paramContext, paramString);
    }
    public List<DJZQ> getAll(){
        List<DJZQ> list = new ArrayList<>();
        Cursor localCursor = query(null, null, null, null, null, null);
        while (localCursor.moveToNext()) {
            DJZQ djzq = new DJZQ();
            djzq.set_id(localCursor.getInt(localCursor.getColumnIndex("_id")));
            djzq.setNAME(localCursor.getString(localCursor.getColumnIndex("NAME")));
            djzq.set_pid(localCursor.getInt(localCursor.getColumnIndex("_pid")));
            list.add(djzq);
        }
        localCursor.close();
        return list;
    }
    public boolean createTable(){
        String[] strs={
                "CREATE TABLE DJZQ (_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, NAME VARCHAR (0, 200) NOT NULL UNIQUE, _pid REFERENCES DJQ (_id))",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (1, '龙坪镇乌石村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (2, '龙坪镇青石村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (3, '连州镇三古滩村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (4, '连州镇昆陂村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (5, '瑶安瑶族乡九龙村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (6, '大路边镇东大村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (7, '大路边镇东峉塘村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (8, '丰阳镇朱岗村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (9, '丰阳镇旗美村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (10, '星子镇清江村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (11, '大路边镇新水罗村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (12, '瑶安瑶族乡大营村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (13, '大路边镇大塘村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (14, '丰阳镇大富头村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (15, '瑶安瑶族乡洛阳村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (16, '星子镇东上村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (17, '大路边镇东联村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (18, '丰阳镇夏炉村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (19, '西岸镇清水村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (20, '星子镇四方村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (21, '西岸镇石马村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (22, '大路边镇寒鸭村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (23, '东陂镇塘联村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (24, '西岸镇三水村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (25, '大路边镇浦东村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (26, '星子镇星子村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (27, '星子镇联西村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (28, '星子镇潭岭村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (29, '瑶安瑶族乡四和村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (30, '瑶安瑶族乡盘东村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (31, '东陂镇东塘村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (32, '东陂镇江夏村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (33, '星子镇沈家村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (34, '东陂镇香花村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (35, '东陂镇东陂村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (36, '星子镇昌黎村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (37, '星子镇上庄村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (38, '西岸镇冲口村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (39, '东陂镇西塘村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (40, '星子镇水源村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (41, '东陂镇大江村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (42, '保安镇子沟村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (43, '星子镇东红村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (44, '西岸镇东江村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (45, '星子镇新村村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (46, '东陂镇前江村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (47, '西岸镇小带村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (48, '保安镇本公洞村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (49, '保安镇种田村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (50, '星子镇潭源村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (51, '星子镇潭岭水库', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (52, '保安镇保安林场', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (53, '星子镇赤塘村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (54, '保安镇麻北村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (55, '保安镇岭咀村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (56, '西岸镇石兰村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (57, '保安镇良塘村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (58, '西岸镇河田村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (59, '星子镇马水村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (60, '星子镇星子林场', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (61, '西岸镇西岸村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (62, '西岸镇溪塘村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (63, '保安镇万家村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (64, '保安镇保安村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (65, '龙坪镇麻步村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (66, '西岸镇东村村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (67, '保安镇栋头村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (68, '龙坪镇凤凰村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (69, '保安镇湾村村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (70, '西岸镇马带村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (71, '保安镇卿罡村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (72, '西岸镇七村村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (73, '龙坪镇垦区村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (74, '龙坪镇黄芒村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (75, '龙坪国有林场龙坪林场', 10)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (76, '龙坪镇东村村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (77, '龙坪镇朝天村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (78, '西岸镇奎池村', 7)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (79, '保安镇黄村村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (80, '三水瑶族乡新八村', 1)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (81, '三水瑶族乡沙坪村', 1)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (82, '三水瑶族乡云雾村', 1)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (83, '星子镇周联村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (84, '瑶安瑶族乡瑶安村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (85, '瑶安瑶族乡田心村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (86, '三水瑶族乡左里村', 1)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (87, '瑶安瑶族乡新九村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (88, '大路边镇大坳村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (89, '星子镇溷坪村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (90, '大路边镇河佳汉村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (91, '大路边镇马占村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (92, '丰阳镇湖江村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (93, '大路边镇荒塘村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (94, '大路边镇汛塘村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (95, '星子镇姜联村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (96, '大路边镇观头洞村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (97, '丰阳镇柯木湾村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (98, '丰阳镇乡梁家村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (99, '大路边镇山洲村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (100, '星子镇内洞村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (101, '星子镇唐家村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (102, '瑶安瑶族乡碧梧村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (103, '大路边镇黄太村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (104, '丰阳镇新立村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (105, '大路边镇东坪村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (106, '大路边镇顺泉村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (107, '丰阳镇丰阳村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (108, '大路边镇黎水村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (109, '东陂镇卫民村', 6)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (110, '丰阳镇陂岭村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (111, '星子镇上联村', 2)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (112, '瑶安瑶族乡清源村', 3)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (113, '丰阳镇夏煌村', 5)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (114, '大路边镇大路边村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (115, '大路边镇油田村', 4)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (116, '保安镇梅田村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (117, '保安镇大冲村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (118, '龙坪镇袁屋村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (119, '龙坪镇元壁村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (120, '保安镇新塘村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (121, '连州镇共和村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (122, '龙坪镇龙坪村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (123, '保安镇水口村', 8)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (124, '龙坪镇松柏村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (125, '连州镇石角村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (126, '连州镇大坪村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (127, '连州镇白云村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (128, '连州镇良江村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (129, '连州镇巾峰村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (130, '龙坪镇石桥村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (131, '西江镇井塘村', 12)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (132, '西江镇外塘村', 12)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (133, '西江镇大岭村', 12)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (134, '西江镇大田村', 12)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (135, '西江镇山塘村', 12)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (136, '西江镇斜磅村', 12)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (137, '西江镇耙田村', 12)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (138, '西江镇西江村', 12)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (139, '九陂镇四联村', 13)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (140, '九陂镇白石村', 13)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (141, '九陂镇联一村', 13)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (142, '连州镇元潭村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (143, '连州镇半岭1村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (144, '连州镇半岭2村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (145, '连州镇协民村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (146, '连州镇城东村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (147, '连州镇城北村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (148, '连州镇城南村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (149, '连州镇城西村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (150, '连州镇沙子岗村', 1)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (151, '连州镇满地村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (152, '连州镇连州市政府', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (153, '连州镇高堆村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (154, '连州镇龙口村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (155, '连州镇龙咀村', 11)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (156, '龙坪镇太坪村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (157, '龙坪镇孔围村', 9)",
                "INSERT INTO DJZQ (_id, NAME, _pid) VALUES (158, '龙坪镇沙坳村', 9)",
        };
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
