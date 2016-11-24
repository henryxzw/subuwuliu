package com.johan.subuwuliu;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.johan.subuwuliu.util.ToastUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectLocationActivity extends AppActivity {
	
	private RelativeLayout waitingLayout;
	private ImageView waitingIcon;
	private Animation waitingAnimation;
	
	private MapView mapView = null;
	private BaiduMap baiduMap;
	
	private LocationClient client = null;
	private LocationClientOption option = null;
	
	private ListView locationListView;
	private List<LocationPoint> locationList = new ArrayList<LocationPoint>();
	private LocationAdapter adapter;
	
	private double firstLocationLatitude, firstLocationLongitude;
	
	private GeoCoder geoCoder;
	
	private int currentLocation = 0;
	
	private ImageView back;
	private Button ok;
	
	@Override
	public void preSetContentView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		SDKInitializer.initialize(getApplicationContext());
	}

	@Override
	public int getContentView() {
		return R.layout.activity_selectlocation;
	}
	
	@Override
	public int getStatuBarColor() {
		// TODO Auto-generated method stub
		return Color.parseColor("#242736");
	}

	@Override
	public void findId() {
		mapView = (MapView) findViewById(R.id.selectlocation_map_view);
		waitingLayout = (RelativeLayout) findViewById(R.id.selectlocation_waiting_layout);
		waitingIcon = (ImageView) findViewById(R.id.selectlocation_waiting_icon);
		locationListView = (ListView) findViewById(R.id.selectlocation_list);
		back = (ImageView) findViewById(R.id.selectlocation_back);
		ok = (Button) findViewById(R.id.selectlocation_ok);
	}

	@Override
	public void init() {
		//设置是否显示比例尺控件 
	    mapView.showScaleControl(true);
	    //设置是否显示缩放控件 
	    mapView.showZoomControls(false);
	    baiduMap = mapView.getMap();
	    //设置监听
	    baiduMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				return false;
			}
			@Override
			public void onMapClick(final LatLng latLng) {
				System.out.println(latLng.latitude + "--" + latLng.longitude);
				geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
				showWaiting(true);
				ok.setEnabled(false);
				markFirstLocation();
				markClickLocation(latLng);
			}
		});
	    //检索
	    geoCoder = GeoCoder.newInstance();
	    geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);
	    //开始定位
	    client = new LocationClient(getApplicationContext());
		client.setLocOption(getDefaultLocationClientOption());
		client.registerLocationListener(dbListener);
		client.start();
		//waiting的layout的初始化
		waitingAnimation = AnimationUtils.loadAnimation(SelectLocationActivity.this, R.anim.anim_waiting);
		waitingIcon.setAnimation(waitingAnimation);
		showWaiting(true);
		//位置列表初始化
		adapter = new LocationAdapter();
		locationListView.setAdapter(adapter);
		locationListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				locationList.get(currentLocation).setSelected(false);
				locationList.get(position).setSelected(true);
				currentLocation = position;
				adapter.notifyDataSetChanged();
				//重置标志
				markFirstLocation();
				markClickLocation(locationList.get(position).getLatLng());
			}
		});
		//按钮
		back.setOnClickListener(clickListener);
		ok.setOnClickListener(clickListener);
		ok.setEnabled(false);
		Log.e("tag", "---------------------create---------------------------");
	}
	
	private void showWaiting(boolean isShow) {
		if(isShow) {
			waitingAnimation.start();
			waitingLayout.setVisibility(ViewGroup.VISIBLE);
		} else {
			waitingAnimation.cancel();
			waitingLayout.setVisibility(View.GONE);
		}
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        mapView.onDestroy();   
        if(client != null) {
        	client.stop();
        }
        Log.e("tag", "---------------------finish---------------------------");
    }  
	
    @Override  
    protected void onResume() {  
        super.onResume();  
        mapView.onResume();  
    }  
    
    @Override  
    protected void onPause() {  
        super.onPause();  
        mapView.onPause();   
    }

	public LocationClientOption getDefaultLocationClientOption(){
		if(option == null){
			option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
			option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
			option.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		    option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		    option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
		    option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
		    option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		    option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死   
		    option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		    option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		    option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		    //修复6.0错误
		    option.setPriority(LocationClientOption.NetWorkFirst);
		}
		return option;
	}
	
	private BDLocationListener dbListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location && (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation)) {
				System.out.println("================>" + location.getLocType() + BDLocation.TypeOffLineLocation);
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append("\nlatitude : ");
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(location.getLongitude());
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				System.out.println(sb.toString());
				//第一次去的的经纬度
				firstLocationLatitude = location.getLatitude();
				firstLocationLongitude = location.getLongitude();
				//设置位置
				MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                .direction(10).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
				baiduMap.setMyLocationData(locData);
				//更新位置、比例系数、添加标志等状态
				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				markFirstLocation();
				markClickLocation(latLng);
                //更新地图状态
				MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(16).build(); 
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                baiduMap.animateMapStatus(mapStatusUpdate);
                //检索
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                //停止定位
				client.stop();
			} else {
				//定位失败
				showToast("定位失败，请检查网络，重新定位");
				SelectLocationActivity.this.setResult(RESULT_CANCELED);
				SelectLocationActivity.this.finish();
			}
		}
	};
	
	private OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			showWaiting(false);
			if(result != null) {
				if(result.getPoiList() == null) {
					showToast("获取位置信息失败，请重新获取");
					SelectLocationActivity.this.setResult(RESULT_CANCELED);
					SelectLocationActivity.this.finish();
					return;
				}
				locationList.clear();
				LocationPoint firstPoint = new LocationPoint("[ 位置 ]", result.getAddress(), result.getLocation());
				firstPoint.setSelected(true);
				locationList.add(firstPoint);
				List<PoiInfo> allAddrList =  result.getPoiList();
				for(PoiInfo info : allAddrList) {
					LocationPoint point = new LocationPoint(info.name, info.address, info.location);
					point.setSelected(false);
					locationList.add(point);
					System.out.println("地址：" + info.name + ":" + info.address);
				}
				currentLocation = 0;
				adapter.notifyDataSetChanged();
				ok.setEnabled(true);
			} else {
				System.out.println("---->null--------------------");
				if(client != null && !client.isStarted()) {
					ToastUtil.show(SelectLocationActivity.this, "定位不成功，正在自动重新定位");
					client.start();
				} else {
					showToast("获取位置信息失败，请重新获取");	
					SelectLocationActivity.this.setResult(RESULT_CANCELED);
					SelectLocationActivity.this.finish();
				}
			}
		}
		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			
		}
	};
	
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.selectlocation_back :
				SelectLocationActivity.this.setResult(RESULT_CANCELED);
				SelectLocationActivity.this.finish();
				break;
			case R.id.selectlocation_ok :
				LocationPoint locationPoint = locationList.get(currentLocation);
				Intent intent = new Intent();
				intent.putExtra("location_address", locationPoint.detail);
				intent.putExtra("location_latitude", locationPoint.getLatLng().latitude);
				intent.putExtra("location_longitude", locationPoint.getLatLng().longitude);
				SelectLocationActivity.this.setResult(RESULT_OK, intent);
				if(client != null && !client.isStarted()) {
					client.stop();
				} 
				SelectLocationActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};
	
	private void markFirstLocation() {
		LatLng latLng = new LatLng(firstLocationLatitude, firstLocationLongitude);
        BitmapDescriptor markBitmap = BitmapDescriptorFactory.fromResource(R.drawable.lbxz);   
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(markBitmap);
        baiduMap.addOverlay(overlayOptions);
	}
	
	private void markClickLocation(LatLng latLng) {
		baiduMap.clear();
        BitmapDescriptor markBitmap = BitmapDescriptorFactory.fromResource(R.drawable.djwz);   
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(markBitmap);
        baiduMap.addOverlay(overlayOptions);
	}
	
	class LocationAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		
		public LocationAdapter() {
			inflater = LayoutInflater.from(SelectLocationActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return locationList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return locationList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.item_selectlocation, null);
				viewHolder = new ViewHolder();
				viewHolder.title = (TextView) convertView.findViewById(R.id.selectlocation_item_title);
				viewHolder.detail = (TextView) convertView.findViewById(R.id.selectlocation_item_detail);
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.selectlocation_item_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			LocationPoint point = locationList.get(position);
			if(position == 0) {
				viewHolder.title.setText("[ 位置 ]");
			} else {
				viewHolder.title.setText(point.getTitle());
			}
			viewHolder.detail.setText(point.getDetail());
			if(point.isSelected) {
				viewHolder.icon.setVisibility(View.VISIBLE);
			} else {
				viewHolder.icon.setVisibility(View.GONE);
			}
			return convertView;
		}
		
		class ViewHolder {
			public TextView title;
			public TextView detail;
			public ImageView icon;
		}
		
	}
	
	public class LocationPoint {
		
		private String title;
		private String detail;
		
		private LatLng latLng;
		
		private boolean isSelected;

		public LocationPoint(String title, String detail, LatLng latLng) {
			this.title = title;
			this.detail = detail;
			this.latLng = latLng;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public LatLng getLatLng() {
			return latLng;
		}

		public void setLatLng(LatLng latLng) {
			this.latLng = latLng;
		}
		
	}
	
}
