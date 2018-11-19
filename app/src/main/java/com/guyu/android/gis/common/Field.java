package com.guyu.android.gis.common;

public class Field {
	private int fieldId;
	private String fieldName;
	private String fieldCnname;
	private int fieldType;
	private int dataType;
	private int dictType;//判断有没有字典表
	private int dispOrder;
	private int isShow; //是否显示 默认1 显示
	private int isUpload;// 是否上传 默认1 上传字段
	private String defaultValue;// 默认值
	private String hintValue;//hint值
	private String formatStyle;// 格式化字符串  主要是日期 与小数 

	public Field() {
	}

	public Field(String p_fieldName, String p_fieldCnname,
			String p_fieldDefaultValue) {
		this.fieldName = p_fieldName;
		this.fieldCnname = p_fieldCnname;
		this.defaultValue = p_fieldDefaultValue;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldCnname() {
		return fieldCnname;
	}

	public void setFieldCnname(String fieldCnname) {
		this.fieldCnname = fieldCnname;
	}

	public int getFieldType() {
		return fieldType;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getDictType() {
		return dictType;
	}

	public void setDictType(int dictType) {
		this.dictType = dictType;
	}

	public int getDispOrder() {
		return dispOrder;
	}

	public void setDispOrder(int dispOrder) {
		this.dispOrder = dispOrder;
	}

	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public void setFormatStyle(String formatStyle) {
		this.formatStyle = formatStyle;
	}
	public void setHintValue(String hintValue) {
		this.hintValue = hintValue;
	}
	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}
	public void setIsUpload(int isUpload) {
		this.isUpload = isUpload;
	}
	public int getIsShow() {
		return isShow;
	}
	public int getIsUpload() {
		return isUpload;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public String getFormatStyle() {
		return formatStyle;
	}
	public String getHintValue() {
		return hintValue;
	}
	

}
