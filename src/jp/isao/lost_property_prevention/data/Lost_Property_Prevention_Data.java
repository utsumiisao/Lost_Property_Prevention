package jp.isao.lost_property_prevention.data;

import java.io.Serializable;

public class Lost_Property_Prevention_Data implements Serializable {
	//serialVersionUID
	private static final long serialVersionUID = 1L;
	//powId
	private int rowId;
	//緯度
	private double latitude;
	//経度
	private double longitude;
	//起動時刻
	private String starttime;
	//実行時間
	private String runtime;
	//確認内容1
	private String edittext_content1;
	//確認内容2
	private String edittext_content2;
	//確認内容3
	private String edittext_content3;
	//確認内容4
	private String edittext_content4;
	//確認内容5
	private String edittext_content5;
	//確認内容6
	private String edittext_content6;
	//確認内容7
	private String edittext_content7;
	//確認内容8
	private String edittext_content8;
	//確認内容9
	private String edittext_content9;
	//確認内容10
	private String edittext_content10;
	//バイブレータ実行時間
	private String vib_runtime;
	//月曜
	private String monday;
	//火曜
	private String tuesday;
	//水曜
	private String wednesday;
	//木曜
	private String thursday;
	//金曜
	private String friday;
	//土曜
	private String saturday;
	//日曜
	private String sunday;
	//サービス実行
	private String run_service;
	//サービス実行中
	private String now_running_service;
	//GPS起動距離
	private String gps_run_circle;
	
	
	public String getGps_run_circle() {
		return gps_run_circle;
	}
	public void setGps_run_circle(String gpsRunCircle) {
		gps_run_circle = gpsRunCircle;
	}
	public String getNow_running_service() {
		return now_running_service;
	}
	public void setNow_running_service(String nowRunningService) {
		now_running_service = nowRunningService;
	}

	public String getVib_runtime() {
		return vib_runtime;
	}
	public void setVib_runtime(String vibRuntime) {
		vib_runtime = vibRuntime;
	}
	public String getStarttime() {
		return starttime;
	}
	public String getRuntime() {
		return runtime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	public String getRun_service() {
		return run_service;
	}
	public void setRun_service(String runService) {
		run_service = runService;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double laitude) {
		this.latitude = laitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getEdittext_content1() {
		return edittext_content1;
	}
	public void setEdittext_content1(String edittextContent1) {
		edittext_content1 = edittextContent1;
	}
	public String getEdittext_content2() {
		return edittext_content2;
	}
	public void setEdittext_content2(String edittextContent2) {
		edittext_content2 = edittextContent2;
	}
	public String getEdittext_content3() {
		return edittext_content3;
	}
	public void setEdittext_content3(String edittextContent3) {
		edittext_content3 = edittextContent3;
	}
	public String getEdittext_content4() {
		return edittext_content4;
	}
	public void setEdittext_content4(String edittextContent4) {
		edittext_content4 = edittextContent4;
	}
	public String getEdittext_content5() {
		return edittext_content5;
	}
	public void setEdittext_content5(String edittextContent5) {
		edittext_content5 = edittextContent5;
	}
	public String getEdittext_content6() {
		return edittext_content6;
	}
	public void setEdittext_content6(String edittextContent6) {
		edittext_content6 = edittextContent6;
	}
	public String getEdittext_content7() {
		return edittext_content7;
	}
	public void setEdittext_content7(String edittextContent7) {
		edittext_content7 = edittextContent7;
	}
	public String getEdittext_content8() {
		return edittext_content8;
	}
	public void setEdittext_content8(String edittextContent8) {
		edittext_content8 = edittextContent8;
	}
	public String getEdittext_content9() {
		return edittext_content9;
	}
	public void setEdittext_content9(String edittextContent9) {
		edittext_content9 = edittextContent9;
	}
	public String getEdittext_content10() {
		return edittext_content10;
	}
	public void setEdittext_content10(String edittextContent10) {
		edittext_content10 = edittextContent10;
	}
	public String getMonday() {
		return monday;
	}
	public void setMonday(String monday) {
		this.monday = monday;
	}
	public String getTuesday() {
		return tuesday;
	}
	public void setTuesday(String tuesday) {
		this.tuesday = tuesday;
	}
	public String getWednesday() {
		return wednesday;
	}
	public void setWednesday(String wednesday) {
		this.wednesday = wednesday;
	}
	public String getThursday() {
		return thursday;
	}
	public void setThursday(String thursday) {
		this.thursday = thursday;
	}
	public String getFriday() {
		return friday;
	}
	public void setFriday(String friday) {
		this.friday = friday;
	}
	public String getSaturday() {
		return saturday;
	}
	public void setSaturday(String saturday) {
		this.saturday = saturday;
	}
	public String getSunday() {
		return sunday;
	}
	public void setSunday(String sunday) {
		this.sunday = sunday;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	


	
	
}
