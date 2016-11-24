package com.johan.subuwuliu;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.johan.subuwuliu.bean.CityBean;
import com.johan.subuwuliu.database.CityHistoryDatabase;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class BackLocationActivity extends AppTheme2Activity implements
		OnClickListener {

	private static final int MAX_SEARCH_PAGENUM = 0;

	private static final String KEY_OF_OLDCITY = "subuwuliu_old_city";
	private static final int REQUEST_CITY_CODE = 260;

	private TextView city, selectedSearch;

	private EditText search;

	private Button seachBut;

	private ListView searchListView;
	private List<CityBean> searchList = new ArrayList<CityBean>();
	private SeachAdapter adapter;
	private WaitingDialog dialog;

	private CityBean selectedCityBean;

	private PoiSearch poiSearch;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_backlocation;
	}

	@Override
	public void preSetContentView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.preSetContentView(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		city = (TextView) findViewById(R.id.backlocation_city);
		search = (EditText) findViewById(R.id.backlocation_saerch);
		seachBut = (Button) findViewById(R.id.backlocation_saerch_but);
		searchListView = (ListView) findViewById(R.id.backlocation_search_list);
		selectedSearch = (TextView) findViewById(R.id.backlocation_selected);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		String oldCityName = getSharedPreferences().getString(KEY_OF_OLDCITY,
				"广州市");
		city.setText(oldCityName);
		city.setOnClickListener(this);
		selectedSearch.setText(oldCityName);
		resetData();
		adapter = new SeachAdapter();
		searchListView.setAdapter(adapter);
		seachBut.setOnClickListener(this);
		searchListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				selectedCityBean = searchList.get(position);
				selectedSearch.setText(selectedCityBean.name);
			}
		});
		searchListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				hideInputMethod();
				return false;
			}
		});
		initSearch();
	}

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "回程地点";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backlocation_city:
			goActivityForResult(SelectCityActivity.class, REQUEST_CITY_CODE);
			break;
		case R.id.backlocation_saerch_but:
			beginSearch();
			hideInputMethod();
			break;
		default:
			break;
		}
	}

	private void initSearch() {
		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(poiListener);
	}

	private void beginSearch() {
		seachBut.setEnabled(false);
		search(0);

	}

	private void search(int pageNum) {
		String searchContent = search.getText().toString();
		if ("".equals(searchContent)) {
			seachBut.setEnabled(true);
			ToastUtil.show(this, "请输入目的地");
			return;
		}
		String cityContent = city.getText().toString();
		String cityName = cityContent.substring(0, cityContent.length() - 1);
		System.out
				.println("------->>>" + cityName + "------>>" + searchContent);
		dialog = new WaitingDialog(this);
		dialog.show(findViewById(R.id.backlocation_ccc));
		poiSearch.searchInCity((new PoiCitySearchOption()).city(cityName)
				.keyword(searchContent).pageNum(pageNum));
	}

	OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
		public void onGetPoiResult(PoiResult result) {
			System.out.println("结果" + result.getCurrentPageNum());
			dialog.dismiss();
			// 获取POI检索结果
			if (result == null
					|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
				ToastUtil.show(BackLocationActivity.this, "没有搜到结果");
				searchList.clear();
				resetData();
				adapter.notifyDataSetChanged();
				seachBut.setEnabled(true);
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				System.out.println("第" + result.getCurrentPageNum() + "结果");
				if (result.getCurrentPageNum() == 0) {
					searchList.clear();
				}
				for (PoiInfo info : result.getAllPoi()) {
					CityBean cityBean = new CityBean(info.name, info.address,
							city.getText().toString(),
							String.valueOf(info.location.longitude),
							String.valueOf(info.location.latitude));
					searchList.add(cityBean);
					System.out.println("-搜索结果--->" + info.name);
				}
				if (result.getCurrentPageNum() < MAX_SEARCH_PAGENUM) {
					search(result.getCurrentPageNum() + 1);
				} else {
					seachBut.setEnabled(true);
					adapter.notifyDataSetChanged();
				}
				return;
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
				seachBut.setEnabled(true);
				String cityContent = city.getText().toString();
				String cityName = cityContent.substring(0,
						cityContent.length() - 1);
				String searchContent = search.getText().toString();
				ToastUtil.show(BackLocationActivity.this, "在" + cityName
						+ "没有找" + searchContent + "这个地方");

				return;
			}

		}

		public void onGetPoiDetailResult(PoiDetailResult result) {
			// 获取Place详情页检索结果
		}
	};

	private void resetData() {
		searchList.clear();
		CityHistoryDatabase database = new CityHistoryDatabase(this);
		searchList.addAll(database.pullAll(city.getText().toString()));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_CITY_CODE && resultCode == RESULT_OK) {
			String cityName = data.getStringExtra("selected_city");
			city.setText(cityName + "市");
			getSharedPreferences().edit()
					.putString(KEY_OF_OLDCITY, cityName + "市").commit();
			selectedSearch.setText(cityName + "市");
			resetData();
			adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	class SeachAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return searchList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return searchList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(BackLocationActivity.this)
						.inflate(R.layout.item_backlocation_list, null);
				viewHolder = new ViewHolder();
				viewHolder.name = (TextView) convertView
						.findViewById(R.id.backlocation_item_name);
				viewHolder.address = (TextView) convertView
						.findViewById(R.id.backlocation_item_address);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			CityBean currentCity = searchList.get(position);
			viewHolder.name.setText(currentCity.name);
			viewHolder.address.setText(currentCity.address);
			return convertView;
		}

		public class ViewHolder {
			public TextView name;
			public TextView address;
		}

	}

	private void hideInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView()
					.getWindowToken(), 0);
		}
	}

	@Override
	public String getThemeTip() {
		// TODO Auto-generated method stub
		return "完成";
	}

	@Override
	public void clickThemeTip() {
		// TODO Auto-generated method stub
		Intent dateIntent = new Intent();
		dateIntent.putExtra("backlocation_city", city.getText().toString());
		dateIntent.putExtra("backlocation_name", selectedSearch.getText()
				.toString());
		if (selectedCityBean == null) {
			dateIntent.putExtra("backlocation_address", "");
			dateIntent.putExtra("backlocation_longitude", "");
			dateIntent.putExtra("backlocation_latitude", "");
		} else {
			dateIntent.putExtra("backlocation_address",
					selectedCityBean.address);
			dateIntent.putExtra("backlocation_longitude",
					selectedCityBean.longitude);
			dateIntent.putExtra("backlocation_latitude",
					selectedCityBean.latitude);
		}
		setResult(RESULT_OK, dateIntent);
		finish();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
