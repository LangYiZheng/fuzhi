package com.guyu.android.gis.common;

public class FavEntity {
	private int _id;
	private String layerName = "";
	private String peoId = "";
	private String remark = "";
	private long time = 0L;
	private String title = "";
	private String favgeometry;
	private String favattributes;
	private String favsymbol;

	public FavEntity(int _id, String peoId, String title, String layerName,
			String remark, String favgeometry, String favattributes, long time,
			String favsymbol) {
		this._id = _id;
		this.peoId = peoId;
		this.title = title;
		this.layerName = layerName;
		this.remark = remark;
		this.favgeometry = favgeometry;
		this.favattributes = favattributes;
		this.time = time;
		this.favsymbol = favsymbol;
	}

	public FavEntity(int _id, String title, String layerName, String remark,
			String favgeometry, String favattributes, long time,
			String favsymbol) {
		this._id = _id;
		this.title = title;
		this.layerName = layerName;
		this.remark = remark;
		this.favgeometry = favgeometry;
		this.favattributes = favattributes;
		this.time = time;
		this.favsymbol = favsymbol;
	}

	public FavEntity(int _id, String title, String layerName, String remark,
			String favgeometry, String favattributes, String favsymbol) {
		this._id = _id;
		this.title = title;
		this.layerName = layerName;
		this.remark = remark;
		this.favgeometry = favgeometry;
		this.favattributes = favattributes;
		this.favsymbol = favsymbol;
	}

	public FavEntity(String layerName, String favgeometry,
			String favattributes, long time, String favsymbol) {
		this.layerName = layerName;
		this.favgeometry = favgeometry;
		this.favattributes = favattributes;
		this.time = time;
		this.favsymbol = favsymbol;
	}

	public FavEntity(String layerName, String favgeometry,
			String favattributes, String remark, String favsymbol) {
		this.layerName = layerName;
		this.favgeometry = favgeometry;
		this.favattributes = favattributes;
		this.remark = remark;
		this.favsymbol = favsymbol;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getPeoId() {
		return peoId;
	}

	public void setPeoId(String peoId) {
		this.peoId = peoId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFavgeometry() {
		return favgeometry;
	}

	public void setFavgeometry(String favgeometry) {
		this.favgeometry = favgeometry;
	}

	public String getFavattributes() {
		return favattributes;
	}

	public void setFavattributes(String favattributes) {
		this.favattributes = favattributes;
	}

	public String getFavsymbol() {
		return favsymbol;
	}

	public void setFavsymbol(String favsymbol) {
		this.favsymbol = favsymbol;
	}

}
