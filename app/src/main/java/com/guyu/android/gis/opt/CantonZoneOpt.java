package com.guyu.android.gis.opt;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;


import com.guyu.android.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.guyu.android.database.zonenavi.CantonZoneDBMExternal;
import com.guyu.android.gis.adapter.CantonZoneAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.CantonZone;
import com.guyu.android.gis.wkt.WKT;

public class CantonZoneOpt {
	private View mGroupView = null;
	private TextView canton1;
	private TextView canton2;
	private TextView canton3;
	private TextView canton4;
	private ListView listView;
	private CantonZoneAdapter la;
	private WKT wkt;
	private CantonZoneDBMExternal cantonZoneDBMExternal;
	private static GraphicsLayer grasLayer;
	private boolean init = false;
	
	public CantonZoneOpt(View view) {
		mGroupView = view;
		cantonZoneDBMExternal = new CantonZoneDBMExternal(view.getContext());
	}
	public void initCantonZoneList()
	{
		if(init) return;	
		canton1=(TextView)mGroupView.findViewById(R.id.cantonzone_txt_01);
		canton2=(TextView)mGroupView.findViewById(R.id.cantonzone_txt_02);
		canton3=(TextView)mGroupView.findViewById(R.id.cantonzone_txt_03);
		canton4=(TextView)mGroupView.findViewById(R.id.cantonzone_txt_04);
		listView=(ListView)mGroupView.findViewById(R.id.lst_datas);
		CantonZone rootCantonZone = cantonZoneDBMExternal.getCantonZoneByZoneCode(GisQueryApplication.getApp().getProjectconfig().getCantonZoneRootCode());
		if(rootCantonZone!=null){
			canton1.setText(rootCantonZone.getZonename());//第一级
			canton1.setId(rootCantonZone.getZonecode());
		}else{
			canton1.setId(0);
		}
		canton2.setVisibility(View.INVISIBLE);
		canton3.setVisibility(View.INVISIBLE);
		canton4.setVisibility(View.INVISIBLE);
		
		Integer canton1ID=canton1.getId();
		if(canton1ID>0){
			la=new CantonZoneAdapter(mGroupView.getContext(), getZoneDataByParentid(getCantonZoneIdByCode(canton1ID),"1"));
			
			listView.setAdapter(la);
		}
		listView.setOnItemClickListener(new OnItemClickListener() {  
			  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view, int position,  
                    long id) {
        		String curCantonInfo="";
        		int zoneCode=0;
        		String zoneType="";
        		String zoneName="";
        		curCantonInfo=(String)((TextView)view.findViewById(R.id.textView2)).getText();
        		zoneName=(String)((TextView)view.findViewById(R.id.textView1)).getText();
        		if(zoneName==null||curCantonInfo==null){
        			return;
        		}
        		zoneCode=Integer.parseInt(curCantonInfo.split(",")[0]);
        		zoneType=curCantonInfo.split(",")[1];

        		Integer childType=Integer.parseInt(zoneType)+1;//子级的type比父级大1
        		List<CantonZone> curList = getZoneDataByParentid(getCantonZoneIdByCode(zoneCode),childType.toString());
        		if(curList.size()>0){
        			la=new CantonZoneAdapter(mGroupView.getContext(), curList);
        			listView.setAdapter(la);
        		}else{
        			la=new CantonZoneAdapter(mGroupView.getContext(), curList);
        			listView.setAdapter(la);
        		}
        		
        		if(zoneType.equals("1")){//当前type=1 为第一级，点击该级显示第二级
        			canton2.setId(zoneCode);
        			canton2.setVisibility(View.VISIBLE);
        			canton2.setText(zoneName);
        		}else if(zoneType.equals("2")){
        			canton3.setId(zoneCode);
        			canton3.setText(zoneName);
        			canton3.setVisibility(View.VISIBLE);
        		}else if(zoneType.equals("3")){
        			canton4.setId(zoneCode);
        			canton4.setText(zoneName);
        			canton4.setVisibility(View.VISIBLE);
        		}
        		String wkt=cantonZoneDBMExternal.getAreaInfoByZoneCode(zoneCode).getZonedata();
        		if(wkt.length()==0){
        			if(grasLayer!=null){
        				grasLayer.removeAll();
        			}
        			return;
        		}else{
        			zoomToPolygon(wkt);//根据polygon定位
        		}
            }  
        });
		
		canton1.setTextColor(Color.BLACK);
		canton1.setClickable(true);
		canton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				canton2.setVisibility(View.INVISIBLE);
				canton3.setVisibility(View.INVISIBLE);
				canton4.setVisibility(View.INVISIBLE);
				
				Integer canton1ID=canton1.getId();
				List<CantonZone> curList = getZoneDataByParentid(getCantonZoneIdByCode(canton1ID),"1");
				
				if(canton1ID>0){
					String wkt=cantonZoneDBMExternal.getAreaInfoByZoneCode(canton1ID).getZonedata();
	        		if(wkt.length()==0){
	        			if(grasLayer!=null){
	        				grasLayer.removeAll();
	        			}
	        			return;
	        		}else{
	        			zoomToPolygon(wkt);//根据polygon定位
	        		}
	        		if(curList.size()>0){
	        			la=new CantonZoneAdapter(mGroupView.getContext(), curList);
						listView.setAdapter(la);
	        		}
				}
			}
		});
	
		canton2.setTextColor(Color.BLACK);
		canton2.setClickable(true);
		canton2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				canton3.setVisibility(View.INVISIBLE);
				canton4.setVisibility(View.INVISIBLE);
				Integer canton2ID=canton2.getId();
				List<CantonZone> curList = getZoneDataByParentid(getCantonZoneIdByCode(canton2ID),"2");
				
				if(canton2ID>0){
					String wkt=cantonZoneDBMExternal.getAreaInfoByZoneCode(canton2ID).getZonedata();
	        		if(wkt.length()==0){
	        			if(grasLayer!=null){
	        				grasLayer.removeAll();
	        			}
	        			return;
	        		}else{
	        			zoomToPolygon(wkt);//根据polygon定位
	        		}
	        		if(curList.size()>0){
	        			la=new CantonZoneAdapter(mGroupView.getContext(),curList);
						listView.setAdapter(la);
	        		}
				}
			}
		});
		canton3.setTextColor(Color.BLACK);
		canton3.setClickable(true);
		canton3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				canton4.setVisibility(View.INVISIBLE);
				Integer canton3ID=canton3.getId();
				List<CantonZone> curList = getZoneDataByParentid(getCantonZoneIdByCode(canton3ID),"3");
				
				if(canton3ID>0){
					String wkt=cantonZoneDBMExternal.getAreaInfoByZoneCode(canton3ID).getZonedata();
	        		if(wkt.length()==0){
	        			if(grasLayer!=null){
	        				grasLayer.removeAll();
	        			}
	        			return;
	        		}else{
	        			zoomToPolygon(wkt);//根据polygon定位
	        		}
	        		if(curList.size()>0){
	        			la=new CantonZoneAdapter(mGroupView.getContext(),curList);
						listView.setAdapter(la);
	        		}
				}
				listView.setAdapter(la);
			}
		});
		canton4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Integer canton4ID=canton4.getId();
				List<CantonZone> curList = getZoneDataByParentid(getCantonZoneIdByCode(canton4ID),"4");
        		
				if(canton4ID>0){
					String wkt=cantonZoneDBMExternal.getAreaInfoByZoneCode(canton4ID).getZonedata();
	        		if(wkt.length()==0){
	        			if(grasLayer!=null){
	        				grasLayer.removeAll();
	        			}
	        			return;
	        		}else{
	        			zoomToPolygon(wkt);//根据polygon定位
	        		}
	        		if(curList.size()>0){
	        			la=new CantonZoneAdapter(mGroupView.getContext(),curList);
						listView.setAdapter(la);
	        		}
				}
				listView.setAdapter(la);
			}
		});
		init = true;
	}
	private List<CantonZone> getZoneDataByParentid(String parentid,String type){	
		return cantonZoneDBMExternal.getZoneDataByParentid(parentid,type);
	}
	private String getCantonZoneIdByCode(int zoneCode){
		CantonZone cantonZone = cantonZoneDBMExternal.getCantonZoneByZoneCode(zoneCode);
		return cantonZone.getZoneid();
	}
	/**
	 * 添加并定位到Polygon
	 * 
	 * @param wktStr
	 */
	public void zoomToPolygon(String wktStr) {

		String json = "";
		wkt = new WKT();
		if(wktStr.contains("MULTIPOLYGON")){
			json=wkt.getMULTIPOLYGONWktToJson(wktStr, MapOperate.map.getSpatialReference().getID());
		}else{
			json=wkt.getPOLYGONWktToJson(wktStr, MapOperate.map.getSpatialReference().getID());
		}
		
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser jsonParser;
		try {
			jsonParser = jsonFactory.createJsonParser(json);
			MapGeometry mapGeo = GeometryEngine.jsonToGeometry(jsonParser);
			Geometry geo = mapGeo.getGeometry();
			GraphicsLayer mGraphicsLayer = getPublicGraphicsLayer();
			mGraphicsLayer.removeAll();
		
			SimpleFillSymbol mFillSymbol = new SimpleFillSymbol(Color.TRANSPARENT);
			mFillSymbol.setOutline(new SimpleLineSymbol(Color.CYAN,3,SimpleLineSymbol.STYLE.DASH));
			Polygon mPolygon = ((Polygon) geo);
			Graphic polygonGra = new Graphic(mPolygon, mFillSymbol);
			mGraphicsLayer.addGraphic(polygonGra);
			MapOperate.map.setExtent(polygonGra.getGeometry(),500);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static GraphicsLayer getPublicGraphicsLayer() {
		if (grasLayer == null) {
			grasLayer = new GraphicsLayer();
			MapOperate.map.addLayer(grasLayer);
		}
		return grasLayer;
	}
	public static void updateAllCantonZoneList(Context paramContext, CallBack callback) {
		CantonZoneOpt.updateCantonZoneData(paramContext);
		if (callback != null) {
			callback.execute();
		}
	}
	/**
	 * 更新任务列表
	 * 
	 * @param paramContext
	 */
	public static  void updateCantonZoneData(Context paramContext) {
		String updateCantonZoneUrl = GisQueryApplication.getApp().getProjectconfig().getCantonZoneDownloadUrl();
		
		System.out.println("更新左侧区划导航列表，服务地址：" + updateCantonZoneUrl);
		
		try {
			URL url = new URL(updateCantonZoneUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			Log.i("CONN", conn.toString());
			if (conn.getResponseCode() == 200) {
				InputStream ins = conn.getInputStream();
				parseTaskXML(ins,paramContext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析任务XML
	 * 
	 * @param ins
	 * @param paramContext
	 * @throws Exception
	 */
	private static void parseTaskXML(InputStream ins,Context paramContext)
			throws Exception {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(ins);
		Element root = document.getDocumentElement();

		NodeList nL_list = root
				.getElementsByTagName("CANTONZONE");
		
		ArrayList<CantonZone> al_list = new ArrayList<CantonZone>();
		try {
			for (int i = 0; i < nL_list.getLength(); i++) {
				CantonZone curCantonZone = new CantonZone();
				
				NodeList nl_zoneid = root.getElementsByTagName("ZONEID");
				Element el_zoneid = (Element) nl_zoneid.item(i);
				curCantonZone.setZoneid(el_zoneid.getTextContent());
				
				NodeList nl_zonecode = root.getElementsByTagName("ZONECODE");
				Element el_zonecode = (Element) nl_zonecode.item(i);
				curCantonZone.setZonecode(Integer.parseInt(el_zonecode.getTextContent()));
				
				NodeList nl_parentid = root.getElementsByTagName("PARENTID");
				Element el_parentid = (Element) nl_parentid.item(i);
				curCantonZone.setParentid(el_parentid.getTextContent());
				
				NodeList nl_zonedata = root.getElementsByTagName("ZONEDATA");
				Element el_zonedata = (Element) nl_zonedata.item(i);
				curCantonZone.setZonedata(el_zonedata.getTextContent());
				
				NodeList nl_zonename = root.getElementsByTagName("ZONENAME");
				Element el_zonename = (Element) nl_zonename.item(i);
				curCantonZone.setZonename(el_zonename.getTextContent());
				
				NodeList nl_disporder = root.getElementsByTagName("DISPORDER");
				Element el_disporder = (Element) nl_disporder.item(i);
				curCantonZone.setDisporder((Integer.parseInt(el_disporder.getTextContent())));
				
				al_list.add(curCantonZone);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (al_list.size() > 0) {
			System.out.println("从服务器获取到的区划导航条数为："+al_list.size());
			updateCantonListInfo(al_list, paramContext);
		}
	}

	private static void updateCantonListInfo(List<CantonZone> cantonZoneList, Context paramContext) {
		boolean isDeleSucess = new CantonZoneDBMExternal(paramContext).deleteAllCantonZone();
		if(isDeleSucess){
			new CantonZoneDBMExternal(paramContext).insertMultiCantonZoneInfo(cantonZoneList);
		}		
	}
}
