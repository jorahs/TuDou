package com.luwei.fragment;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.luwei.potato.R;
import com.luwei.navignation.util.ToastUtil;
import com.luwei.navignation.util.AMapUtil;

public class NavigationPage extends SherlockFragment implements
AMapLocationListener, Runnable {
	private LocationManagerProxy aMapLocManager = null;
	private TextView myLocation;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.nagvigation_page, container,
				false);
		myLocation = (TextView) rootView.findViewById(R.id.myLocation);
		aMapLocManager = LocationManagerProxy
				.getInstance(getSherlockActivity());
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		return rootView;
	}

	@Override
	public void onPause() {
		super.onPause();
		stopLocation();// 停止定位
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}
	
	/**
	 * 混合定位回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			this.aMapLocation = location;// 判断超时机制
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
					+ "\n精    度    :" + location.getAccuracy() + "米"
					+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
					+ AMapUtil.convertToTime(location.getTime()) + "\n城市编码:"
					+ cityCode + "\n位置描述:" + desc + "\n省:"
					+ location.getProvince() + "\n市:" + location.getCity()
					+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
					.getAdCode());
			myLocation.setText(str);
		}
	}
	
	@Override
	public void run() {
		if (aMapLocation == null) {
			ToastUtil.show(getSherlockActivity(), "12秒内还没有定位成功，停止定位");
			myLocation.setText("12秒内还没有定位成功，停止定位");
			stopLocation();// 销毁掉定位
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
