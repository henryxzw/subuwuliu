package com.johan.subuwuliu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.johan.subuwuliu.database.CityDatabase;
import com.johan.subuwuliu.util.HanziToPinyin;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public class SelectCityActivity extends AppThemeActivity{
	
	private ListView cityListView;
	
	private List<CityItemModel> originalCityList = new ArrayList<CityItemModel>();
	private List<CityItemModel> cityList = new ArrayList<CityItemModel>();
	
	private CityAdapter adapter;
	
	private EditText searchView;
	
	@Override
	public int getContentView() {
		return R.layout.activity_selectcity;
	}
	
	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "熟悉城市";
	}

	@Override
	public void findId() {
		searchView = (EditText) findViewById(R.id.selectcity_search);
		cityListView = (ListView) findViewById(R.id.selectcity_list);
	}

	@Override
	public void init() {
		//初始化列表
		adapter = new CityAdapter();
		cityListView.setAdapter(adapter);
		adapter.getFilter().filter("");
		cityListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				hideSoftInputMethod();
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			
			}
		});
		cityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(cityList.get(position).type == CityItemModel.TYPE_CITY) {
					String cityName = cityList.get(position).getDescribe();
					System.out.println("select city --> " + cityName);
					Intent intent = new Intent();
					intent.putExtra("selected_city", cityName);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		//初始化数据
		new InitCityListTask().execute();
		//初始化搜索框
		searchView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter.getFilter().filter(s);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		setNeedLoginInfo(false);
	}
	
	/**
	 * 初始化城市列表
	 */
	private void initCityListData() {
		//添加具体热门城市
		CityItemModel hotCityDetail = new CityItemModel("热门具体城市", CityItemModel.TYPE_HOT_CITY);
		String[] hotCityDetailPinyinResult;
		try {
			hotCityDetailPinyinResult = HanziToPinyin.ToPinyin("热门具体城市");
			hotCityDetail.setPinyin(hotCityDetailPinyinResult[0]);
			hotCityDetail.setFirstLetter(hotCityDetailPinyinResult[1]);
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		originalCityList.add(hotCityDetail);
		//添加其余
		List<CityItemModel> tempList = new ArrayList<CityItemModel>();
		//添加所有城市
		CityDatabase database = new CityDatabase(this);
		List<String> allCity = database.getAllCity();
		for(String cityName : allCity) {
			try {
				CityItemModel item = new CityItemModel(cityName, CityItemModel.TYPE_CITY);
				String[] pinyinResult = HanziToPinyin.ToPinyin(cityName);
				item.setPinyin(pinyinResult[0]);
				item.setFirstLetter(pinyinResult[1]);
				tempList.add(item);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		}
		//排序
		Collections.sort(tempList, new CityComparator());
		//添加到所有列表里面
		originalCityList.addAll(tempList);
	}
	
	/**
	 * 城市比较器
	 * @author fengyihuan
	 *
	 */
	class CityComparator implements Comparator<CityItemModel> {
		@Override
		public int compare(CityItemModel lo, CityItemModel ro) {
			return lo.getPinyin().compareTo(ro.getPinyin());
		}
	}
	
	class CityAdapter extends BaseAdapter implements Filterable {
		
		private CityFilter cityFilter;

		@Override
		public int getCount() {
			return cityList.size();
		}

		@Override
		public Object getItem(int position) {
			return cityList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return cityList.get(position).getType();
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderCity holderCity = null;
			ViewHolderHotCity holderHotCity = null;
			CityItemModel currentCityItemModel = cityList.get(position);
			int type = getItemViewType(position);
			if(convertView == null) {
				switch (type) {
				case CityItemModel.TYPE_CITY:
					convertView = LayoutInflater.from(SelectCityActivity.this).inflate(R.layout.item_selectcity_city, null);
					holderCity = new ViewHolderCity();
					holderCity.cityContentView = (TextView) convertView.findViewById(R.id.item_selectcity_city_content);
					convertView.setTag(holderCity);
					break;
				case CityItemModel.TYPE_HOT_CITY:
					convertView = LayoutInflater.from(SelectCityActivity.this).inflate(R.layout.item_selectcity_hot_city, null);
					holderHotCity = new ViewHolderHotCity();
					holderHotCity.hotCityView1 = (TextView) convertView.findViewById(R.id.item_selectcity_hot_city_1_content);
					holderHotCity.hotCityView2 = (TextView) convertView.findViewById(R.id.item_selectcity_hot_city_2_content);
					holderHotCity.hotCityView3 = (TextView) convertView.findViewById(R.id.item_selectcity_hot_city_3_content);
					holderHotCity.hotCityView4 = (TextView) convertView.findViewById(R.id.item_selectcity_hot_city_4_content);
					holderHotCity.hotCityView5 = (TextView) convertView.findViewById(R.id.item_selectcity_hot_city_5_content);
					holderHotCity.hotCityView6 = (TextView) convertView.findViewById(R.id.item_selectcity_hot_city_6_content);
					holderHotCity.hotCityView7 = (TextView) convertView.findViewById(R.id.item_selectcity_hot_city_7_content);
					holderHotCity.hotCityView8 = (TextView) convertView.findViewById(R.id.item_selectcity_hot_city_8_content);
					convertView.setTag(holderHotCity);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case CityItemModel.TYPE_CITY:
					holderCity = (ViewHolderCity) convertView.getTag();
					break;
				case CityItemModel.TYPE_HOT_CITY:
					holderHotCity = (ViewHolderHotCity) convertView.getTag();
					break;
				default:
					break;
				}
			}
			switch (type) {
			case CityItemModel.TYPE_CITY:
				holderCity.cityContentView.setText(currentCityItemModel.getDescribe());
				break;
			case CityItemModel.TYPE_HOT_CITY:
				holderHotCity.hotCityView1.setOnClickListener(hotCityClickListener);
				holderHotCity.hotCityView2.setOnClickListener(hotCityClickListener);
				holderHotCity.hotCityView3.setOnClickListener(hotCityClickListener);
				holderHotCity.hotCityView4.setOnClickListener(hotCityClickListener);
				holderHotCity.hotCityView5.setOnClickListener(hotCityClickListener);
				holderHotCity.hotCityView6.setOnClickListener(hotCityClickListener);
				holderHotCity.hotCityView7.setOnClickListener(hotCityClickListener);
				holderHotCity.hotCityView8.setOnClickListener(hotCityClickListener);
				break;
			default:
				break;
			}
			return convertView;
		}
		
		private OnClickListener hotCityClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String cityName = "中山";
				switch (v.getId()) {
				case R.id.item_selectcity_hot_city_1_content :
					cityName = "中山";
					break;
				case R.id.item_selectcity_hot_city_2_content :
					cityName = "东莞";
					break;
				case R.id.item_selectcity_hot_city_3_content :
					cityName = "深圳";
					break;
				case R.id.item_selectcity_hot_city_4_content :
					cityName = "广州";
					break;
				case R.id.item_selectcity_hot_city_5_content :
					cityName = "江门";
					break;
				case R.id.item_selectcity_hot_city_6_content :
					cityName = "惠州";
					break;
				case R.id.item_selectcity_hot_city_7_content :
					cityName = "珠海";
					break;
				case R.id.item_selectcity_hot_city_8_content :
					cityName = "佛山";
					break;
				default:
					break;
				}
				Intent intent = new Intent();
				intent.putExtra("selected_city", cityName);
				setResult(RESULT_OK, intent);
				finish();
			}
		};
		
		public class ViewHolderCity {
			TextView cityContentView;
		}
		
		public class ViewHolderHotCity {
			TextView hotCityView1;
			TextView hotCityView2;
			TextView hotCityView3;
			TextView hotCityView4;
			TextView hotCityView5;
			TextView hotCityView6;
			TextView hotCityView7;
			TextView hotCityView8;
		}
		
		/**
		 * 城市过滤
		 * @author fengyihuan
		 *
		 */
		class CityFilter extends Filter {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();  
	            if (constraint == null || constraint.length() == 0) {  
	                results.values = originalCityList;  
	                results.count = originalCityList.size();  
	            } else { 
	            	List<CityItemModel> searchCityList = new ArrayList<CityItemModel>();
	            	int allCitySize = originalCityList.size();
	            	for(int i = 0; i < allCitySize; i++) {
	            		CityItemModel currentCity = originalCityList.get(i);
	            		if(currentCity.getDescribe().startsWith(constraint.toString()) || currentCity.getFirstLetter().startsWith(constraint.toString().toUpperCase()) || currentCity.getPinyin().startsWith(constraint.toString().toUpperCase())) {
	            			searchCityList.add(currentCity);
	            		}
	            	}
	            	results.values = searchCityList;  
	                results.count = searchCityList.size(); 
	            }
				return results;
			}
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				cityList = (List<CityItemModel>) results.values;
				notifyDataSetChanged();
			}
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			if(cityFilter == null) {
				cityFilter = new CityFilter();
			}
			return cityFilter;
		}
		
	}
	
	class CityItemModel {
		
		public static final int TYPE_CITY = 0;
		public static final int TYPE_HOT_CITY = 1;
		
		private String describe;
		
		private String firstLetter;
		private String pinyin;

		private int type;
		
		public CityItemModel(String describe, int type) {
			this.describe = describe;
			this.type = type;
		}

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

		public String getFirstLetter() {
			return firstLetter;
		}

		public void setFirstLetter(String firstLetter) {
			this.firstLetter = firstLetter;
		}

		public String getPinyin() {
			return pinyin;
		}

		public void setPinyin(String pinyin) {
			this.pinyin = pinyin;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

	}
	
	/**
	 * 初始化cityList数据
	 * @author fengyihuan
	 *
	 */
	class InitCityListTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			initCityListData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
		}
		
	}
	
	private void hideSoftInputMethod() {
		View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
	}

}
