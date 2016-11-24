package com.johan.subuwuliu.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import com.dk.view.drop.CoverManager;
import com.dk.view.drop.WaterDrop;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.activity.MainActivity;
import com.johan.subuwuliu.GoOrderActivity;
import com.johan.subuwuliu.HomeActivity;
import com.johan.subuwuliu.MyAppointmentActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.TiXingActivity;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.DriverStateBean;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.HomeModeWindow;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.handler.UpdateViewHandle;
import com.johan.subuwuliu.method.MethodConfig;
import com.johan.subuwuliu.receiver.MyPushReceiver;
import com.johan.subuwuliu.receiver.NetworkReceiver;
import com.johan.subuwuliu.server.BackgroundServer;
import com.johan.subuwuliu.server.BackgroundServer.ServiceBinder;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class HomeBindingCarFragment extends AppFragment implements
		OnClickListener, EMEventListener {

	private NetworkReceiver network_receiver;
	
	private TextView timeView, driverNameView, driverCreditTip, carNumber,
			carType, carWeight, carAppointment;

	private TextView notificationXiaoxi, notificationTixing;

	private TextView state;

	private RatingBar driverCreditRatingBar;

	public Button uplineBut, operationBut, modeBut;

	private DriverInfoBean driverInfo;

	private String loginName, loginPassword;

	private WaterDrop unread;

	private SharedPreferences sharedPreferences;

	private String currentYid;

	private String sijiID;

	private float credit;
	
	private boolean isRunning;

	private BackgroundServer server;
	private ListView tixingListView;
	private List<String> tixingList = new ArrayList<String>();
	private TiXingAdapter tixingAdapter;

	private boolean isUpdated = false;
	private SharedPreferences sharedPreferences_C  ;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_home_binding_car;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		sharedPreferences_C = getActivity()
				.getSharedPreferences("sijizhangtai", Context.MODE_PRIVATE);
		timeView = (TextView) layout.findViewById(R.id.home_time);
		driverNameView = (TextView) layout.findViewById(R.id.home_driver_name);
		driverCreditTip = (TextView) layout
				.findViewById(R.id.home_driver_credit_tip);
		carNumber = (TextView) layout.findViewById(R.id.home_car_number);
		driverCreditRatingBar = (RatingBar) layout
				.findViewById(R.id.home_driver_credit_rate_bar);
		carType = (TextView) layout.findViewById(R.id.home_car_type);
		carWeight = (TextView) layout.findViewById(R.id.home_car_weight);
		carAppointment = (TextView) layout.findViewById(R.id.home_appointment);
		notificationXiaoxi = (TextView) layout
				.findViewById(R.id.home_notification_xiaoxi);
		notificationTixing = (TextView) layout
				.findViewById(R.id.home_notification_tixing);
		state = (TextView) layout.findViewById(R.id.home_driver_state);
		uplineBut = (Button) layout.findViewById(R.id.home_upline_but);
		operationBut = (Button) layout.findViewById(R.id.home_operationing_but);
		modeBut = (Button) layout.findViewById(R.id.home_mode_but);
		unread = (WaterDrop) layout.findViewById(R.id.home_unread);
		tixingListView = (ListView) layout.findViewById(R.id.home_tixing_list);
		operationBut.setText("正在进行的运单");

	}

	@Override
	public void init() {
		new Thread(new MyThread()).start();
		// TODO Auto-generated method stub
		driverCreditRatingBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		sharedPreferences = getActivity().getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		String driverInfoData = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
				DiDiApplication.VALUE_UNKOWN);
		driverInfo = GsonUtil.jsonToObjct(driverInfoData, DriverInfoBean.class);
		timeView.setText(FormatUtil.formatTime(System.currentTimeMillis(),
				"yyyy年MM月dd日  EEEE"));
		driverNameView.setText(driverInfo.nick_name);
		driverCreditTip.setText(driverInfo.credit);
		credit = Float.parseFloat(driverInfo.credit);
		driverCreditRatingBar.setRating(credit);
		carNumber.setText(driverInfo.plate_number);
		carType.setText(driverInfo.car_type);
		String carWeightInfo = driverInfo.sizes.replace("T", "吨");
		int carWeightIndex = carWeightInfo.indexOf("吨") + 1;
		carWeight.setText(carWeightInfo.substring(0, carWeightIndex));
		carAppointment.setText("预约订单:" + driverInfo.order_booking);
		uplineBut.setOnClickListener(this);
		operationBut.setOnClickListener(this);
		notificationXiaoxi.setOnClickListener(this);
		notificationTixing.setOnClickListener(this);
		modeBut.setOnClickListener(this);
		carAppointment.setOnClickListener(this);
		tixingAdapter = new TiXingAdapter();
		tixingListView.setAdapter(tixingAdapter);
		// 得到用户名和密码
		loginName = sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN);
		loginPassword = sharedPreferences.getString(
				DiDiApplication.KEY_OF_LOGIN_PWD, DiDiApplication.VALUE_UNKOWN);
		// unread
		CoverManager.getInstance().init(getActivity());
		unread.setVisibility(View.GONE);
		// 注册接收提醒广播
		registerReceiver();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 获取预约数量
		MobclickAgent.onPageStart("HomeBindingCarFragment"); // 统计页面，"MainScreen"为页面名称，可自定义
		try {
			new GetMyAppointmentTask().execute();
			EMChatManager
					.getInstance()
					.registerEventListener(
							this,
							new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
			// 绑定，获取定位信息
			Intent intent = new Intent(getActivity(), BackgroundServer.class);
			getActivity().bindService(intent, serviceConnection,
					Context.BIND_AUTO_CREATE);
			// 是否需要更新状态
			boolean isNeedUpdate = sharedPreferences.getBoolean(
					DiDiApplication.KEY_OF_NEED_UPDATE_DRIVER_STATE, false);
			if (!isUpdated || isNeedUpdate) {
				// 出初始化状态
				uplineBut.setEnabled(false);
				operationBut.setEnabled(false);
				state.setText("正在获取状态，请稍候...");
				// 得到司机状态
				new GetDriverState().execute();
				System.out.println("获取司机状态----------------------------------");
				isUpdated = true;
				sharedPreferences
						.edit()
						.putBoolean(
								DiDiApplication.KEY_OF_NEED_UPDATE_DRIVER_STATE,
								false).commit();
			}
		} catch (Exception e) {
			// TODO: handle exception \
			System.out.println("获取司机状态----------这里有异常------------------------");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("HomeBindingCarFragment");
		EMChatManager.getInstance().unregisterEventListener(this);
		getActivity().unbindService(serviceConnection);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// 注销接收提醒广播
		unregisterReceiver();
		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		if (!ToolUtil.isNetworkAvailable(getActivity())) {
			ToolUtil.dilog(getActivity(), "网络有问题，请先连接网络");
			return;
		}
		switch (v.getId()) {
		case R.id.home_upline_but:
			changeDriverState();
			break;
		case R.id.home_operationing_but:
			Intent goOrderIntent = new Intent(getActivity(),
					GoOrderActivity.class);
			goOrderIntent.putExtra("order_no", currentYid);
			startActivity(goOrderIntent);
			break;
		case R.id.home_mode_but:
			showHomeMode();
			break;
		case R.id.home_notification_xiaoxi:
			Intent xiaoxiIntent = new Intent(getActivity(), MainActivity.class);
			getActivity().startActivity(xiaoxiIntent);

			break;
		case R.id.home_appointment:
			Intent appointmnetIntent = new Intent(getActivity(),
					MyAppointmentActivity.class);
			getActivity().startActivity(appointmnetIntent);
			break;
		case R.id.home_notification_tixing:
			Intent tixingIntent = new Intent(getActivity(),
					TiXingActivity.class);
			getActivity().startActivity(tixingIntent);
			break;
		case R.id.home_driver_state:
			dialog = new WaitingDialog(getActivity());
			dialog.show(layout.findViewById(R.id.home_binding_layout));
			new GetDriverState().execute();
			state.setOnClickListener(null);
		default:
			break;
		}
	}

	private int a = 0;
	private WaitingDialog dialog;

	private void changeDriverState() {
		try {
			dialog = new WaitingDialog(getActivity());
			dialog.show(layout.findViewById(R.id.home_binding_layout));
			final String nextState = uplineBut.isSelected() ? "downline"
					: "online";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key", nextState);
			params.put("username", loginName);
			params.put("pwd", loginPassword);
			try {
				if (!uplineBut.isSelected()) {
					double currentLongitude = server.getNowLongitude();
					double currentLatitude = server.getNowLatitude();
					String currentCity = server.getNowCity();
					String currentAddress = server.getNowAddress();
					// ////////////////////////////
					System.out.println("addr-上线地址-->" + currentAddress + "|"
							+ String.valueOf(currentLongitude) + ","
							+ String.valueOf(currentLatitude));
					if (currentLongitude == 0 || currentLatitude == 0
							|| currentCity == null || currentAddress == null) {
						if (a >= 3) {
							dialog.dismiss();
							ToastUtil.show(getActivity(), "上线失败，请重新点击上线");
							a = 0;
							return;
						}
						// ///////////////////////定位失败重新获取位置
						Intent locationIntent = new Intent(getActivity(),
								BackgroundServer.class);
						locationIntent.putExtra(
								BackgroundServer.BACKGROUND_COMMAND_NAME,
								BackgroundServer.COMMAND_GET_LOCATION);
						getActivity().startService(locationIntent);
						uplineBut.setEnabled(true);
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(1000);
									a++;
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message msg = handler.obtainMessage();
								msg.what = 10002;
								handler.sendMessage(msg);
							}
						}).start();

						return;

					}
					params.put(
							"addr",
							currentAddress + "|"
									+ String.valueOf(currentLongitude) + ","
									+ String.valueOf(currentLatitude));
					params.put("city", currentCity);
				}
			} catch (Exception e) {
				ToastUtil.show(getActivity(), "定位数据出错");
			}
			String action = ToolUtil.encryptionUrlParams(params);
			System.out
					.println("提交上线信息-------------------------------" + action);

			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println("改变司机状态结果---->"
									+ responseInfo.result);
							StatusBean statusBean = GsonUtil.jsonToObjct(
									responseInfo.result, StatusBean.class);
							if(dialog!=null)
							dialog.dismiss();
							if ("0".equals(statusBean.status)) {
								// //////////////////////////////////////////////////////
								// ////保存当前是什么状态
								SharedPreferences sharedPreferences_C = getActivity()
										.getSharedPreferences("sijizhangtai",
												Context.MODE_PRIVATE);
								sharedPreferences_C.edit()
										.putString("sijizhangtai_a", nextState)
										.commit();
								if (nextState.equals("online")) {
									dinshi_handler = true;
									new Thread(new MyThread()).start();
								} else if (nextState.equals("downline")) {
									dinshi_handler = false;
								}
								// /////////////////////
								System.out.println("修改司机状态成功");
								Intent intent = new Intent(getActivity(),
										ForegroundServer.class);
								intent.putExtra(
										ForegroundServer.FOREGROUND_COMMAND_NAME,
										ForegroundServer.COMMAND_DO_LOCATION);
								boolean isStart = !uplineBut.isSelected();
								intent.putExtra("is_start", isStart);
								getActivity().startService(intent);
								sharedPreferences
										.edit()
										.putString(
												DiDiApplication.KEY_OF_DRIVER_STATE,
												nextState).commit();
								if (uplineBut.isSelected()) {
									uplineBut.setText("上线");
									uplineBut.setSelected(false);
									return;
								} else {
									// mode();
									uplineBut.setText("听单中");
									uplineBut.setSelected(true);
									return;
								}
							} else {
								// ToastUtil.show(getActivity(), "修改司机状态失败");
								System.out.println("--->" + statusBean.msg);
							}
						}

						@Override
						public void onFailure(HttpException exception,
								String info) {
							ToastUtil.showNetError(getActivity());
							System.out.println("--->" + exception.getMessage());
							dialog.dismiss();
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("--------->>提交上线状态失败");
		}
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			server = ((ServiceBinder) service).getService();
		}
	};

	private void showHomeMode() {
		Intent intent = new Intent(getActivity(), HomeModeWindow.class);
		getActivity().overridePendingTransition(0, R.anim.anim_home_mode_in);
		getActivity().startActivity(intent);
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance()
				.getAllConversations().values()) {
			if (conversation.getType() == EMConversationType.ChatRoom)
				chatroomUnreadMsgCount = chatroomUnreadMsgCount
						+ conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal - chatroomUnreadMsgCount;
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: {
			EMMessage message = (EMMessage) event.getData();
			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			System.out.println("接收到消息：" + message.getBody().toString());
			// 更新界面
			Message msg = uiHandler.obtainMessage();
			msg.arg1 = 1;
			uiHandler.sendMessage(msg);
			break;
		}
		}
	}

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				if (unread.isTouch()) {
					return;
				}
				unread.setText(String.valueOf(getUnreadMsgCountTotal()));
				if (unread.getVisibility() != View.VISIBLE) {
					unread.reset();
					unread.setVisibility(View.VISIBLE);
				}
			} else if (msg.arg1 == 2) {
				String orderBook = msg.getData().getString("order_booking");
				carAppointment.setText("预约订单:" + orderBook);
			} else if (msg.arg1 == 3) {
				// 获取司机状态
				new GetDriverState().execute();
			}
		}
	};

	private void initStateSuccess(DriverStateBean driverStateBean) {
		state.setText("");
		if ("0".equals(driverStateBean.delivery)) {
			MethodConfig.hasTrack = false;
			uplineBut.setEnabled(true);
			operationBut.setEnabled(false);
			operationBut.setText("无运单");
			// 没有运货
			if ("0".equals(driverStateBean.online)) {
				// 没有上线
				uplineBut.setSelected(false);
				uplineBut.setText("上线");
				dinshi_handler = false;
				SharedPreferences sharedPreferences_C = getActivity()
						.getSharedPreferences("sijizhangtai",
								Context.MODE_PRIVATE);
				sharedPreferences_C.edit()
						.putString("sijizhangtai_a", "downline").commit();
			} else {
				// 上线
				dinshi_handler = true;
				new Thread(new MyThread()).start();
				uplineBut.setSelected(true);
				uplineBut.setText("听单中");
				// // 启动gps服务
				Intent gpsIntent = new Intent(getActivity(),
						ForegroundServer.class);
				gpsIntent.putExtra(ForegroundServer.FOREGROUND_COMMAND_NAME,
						ForegroundServer.COMMAND_DO_LOCATION);
				gpsIntent.putExtra("is_start", true);
				getActivity().startService(gpsIntent);
			}
			// 检查订单
			Intent intent = new Intent(getActivity(), BackgroundServer.class);
			intent.putExtra(BackgroundServer.BACKGROUND_COMMAND_NAME,
					BackgroundServer.COMMAND_CHECK_RECENT_ORDER);
			getActivity().startService(intent);
		} else {
			dinshi_handler = false;
			new Thread(new MyThread()).start();
			operationBut.setText("");
			operationBut.setText("正在进行的运单");
			// 运货
			currentYid = driverStateBean.yid;
			// //开启服务
			ToastUtil.show(getActivity(), "状态改变"+System.currentTimeMillis());
			MethodConfig.hasTrack = true;
			HomeActivity.startTrace();
			uplineBut.setEnabled(false);
			uplineBut.setText("上线");
			operationBut.setEnabled(true);
			operationBut.setSelected(true);
		}
		// 启动前程服务
		Intent intent = new Intent(getActivity(), ForegroundServer.class);
		intent.putExtra(ForegroundServer.FOREGROUND_COMMAND_NAME,
				ForegroundServer.COMMAND_OPERATION_FOREGROUND);
		intent.putExtra("server_state", true);
		getActivity().startService(intent);
	}

	private void initStateFail() {
		
		state.setText("获取司机状态失败,点击重新获取");
       
		state.setOnClickListener(this);
	}
	

	private void initStateFail(String msg) {
		state.setText(msg);
		state.setOnClickListener(this);
	}

	/**
	 * 异步获取司机状态
	 * 
	 * @author Administrator
	 */
	class GetDriverState extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			MethodConfig.hasTrack = true;
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("key", "get_driver_state");
			param.put("username", loginName);
			param.put("pwd", loginPassword);
			String action = ToolUtil.encryptionUrlParams(param);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println("获取司机状态结果："
									+ responseInfo.result);
							DriverStateBean driverStateBean = GsonUtil
									.jsonToObjct(responseInfo.result,
											DriverStateBean.class);
							if ("0".equals(driverStateBean.status)) {
								initStateSuccess(driverStateBean);
							} else {
								initStateFail("网络连接正常，结果为："+responseInfo.result);
							}
							if(dialog!=null)
							dialog.dismiss();
						}

						@Override
						public void onFailure(HttpException exception,
								String info) {
							if(dialog!=null)
                           dialog.dismiss();
							initStateFail();
						}
					});
			return null;
		}
	}

	/**
	 * 提醒Adapter
	 * 
	 * @author fengyihuan
	 */
	class TiXingAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tixingList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return tixingList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_homexiting, null);
				viewHolder = new ViewHolder();
				viewHolder.contentView = (TextView) convertView
						.findViewById(R.id.item_hometixing_content);
				viewHolder.deleteIcon = (ImageView) convertView
						.findViewById(R.id.item_hometixing_delete);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.contentView.setText(tixingList.get(position));
			viewHolder.deleteIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					tixingList.remove(position);
					notifyDataSetChanged();
				}
			});
			return convertView;
		}

		public class ViewHolder {
			public TextView contentView;
			public ImageView deleteIcon;
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(
				MyPushReceiver.ACTION_RECEIVER_TIXING);
		getActivity().registerReceiver(tixingReceiver, filter);
		IntentFilter filters = new IntentFilter(
				MyPushReceiver.ACTION_ORDER_COUNT);
		getActivity().registerReceiver(orderCount, filters);
		IntentFilter filterss = new IntentFilter(
				MyPushReceiver.ACTION_CANCEL_ORDER);
		getActivity().registerReceiver(orderCancel, filterss);
		
		IntentFilter net_fitter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		network_receiver = new NetworkReceiver();
		network_receiver.updateNetHandle=new UpdateViewHandle() {
			
			@Override
			public void Update(String value) {
				if(value.equals("0"))
				{
				   new	GetDriverState().execute();
				}
			}
		};
		getActivity().registerReceiver(network_receiver, net_fitter);
	}

	private void unregisterReceiver() {
		getActivity().unregisterReceiver(tixingReceiver);
		getActivity().unregisterReceiver(orderCount);
		getActivity().unregisterReceiver(orderCancel);
		network_receiver.updateNetHandle = null;
		getActivity().unregisterReceiver(network_receiver);
	}

	private BroadcastReceiver tixingReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String content = intent.getStringExtra("content");
			if (tixingList.size() == 4) {
				tixingList.remove(tixingList.size() - 1);
			}
			System.out.println("接收到的提醒：" + content);
			tixingList.add(0, content);
			tixingAdapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver orderCount = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取预约数量
			new GetMyAppointmentTask().execute();
		}
	};

	private BroadcastReceiver orderCancel = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Message message = uiHandler.obtainMessage();
			message.arg1 = 3;
			uiHandler.sendMessage(message);
		}
	};

	class GetMyAppointmentTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("key", "get_booking_count");
			param.put("username", loginName);
			param.put("pwd", loginPassword);
			String action = ToolUtil.encryptionUrlParams(param);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println("获取预约数量结果："
									+ responseInfo.result);
							BookCountBean bookCountBean = GsonUtil.jsonToObjct(
									responseInfo.result, BookCountBean.class);
							if ("0".equals(bookCountBean.status)) {
								Message message = uiHandler.obtainMessage();
								message.arg1 = 2;
								Bundle bundle = new Bundle();
								bundle.putString("order_booking",
										bookCountBean.count);
								message.setData(bundle);
								uiHandler.sendMessage(message);
							} else {
								System.out.println("获取预约数量出错："
										+ bookCountBean.msg);
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							System.out.println("没有网络，重新获取");
							new GetMyAppointmentTask().execute();
						}
					});
			return null;
		}
	}

	public class BookCountBean {
		public String status;
		public String count;
		public String msg;
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	public static boolean dinshi_handler = false;
	public String zaixian_type = "";
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 10001) {
				System.out.println("-------5分钟执行一次---------->>");
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("key", "get_driver_state");
				param.put("username", loginName);
				param.put("pwd", loginPassword);
				String action = ToolUtil.encryptionUrlParams(param);
				NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
						new RequestCallBack<String>() {
							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								System.out.println("获取司机状态结果："
										+ responseInfo.result);
								DriverStateBean driverStateBean = GsonUtil
										.jsonToObjct(responseInfo.result,
												DriverStateBean.class);
								if ("0".equals(driverStateBean.status)) {
									if (!driverStateBean.delivery.equals("1")
											&& driverStateBean.yid.equals("")) {
										if (driverStateBean.online.equals("1")) {
											zaixian_type = "online";
										} else {
											zaixian_type = "downline";
										}
										if (sharedPreferences_C.getString(
												"sijizhangtai_a", "").equals(
												zaixian_type)) {
											System.out
													.println("------服务器跟本地同步------->>");
											return;
										} else {
											tijiao_shangxian(sharedPreferences_C
													.getString(
															"sijizhangtai_a",
															""));
										}

									}

								}
							}

							@Override
							public void onFailure(HttpException exception,
									String info) {
//								MethodConfig.ShowToast(info);
								initStateFail();
							}
						});
			} else if (msg.what == 10002) {
				dialog.dismiss();
				changeDriverState();
			}
			// 要做的事情
			super.handleMessage(msg);
		}
	};

	// /////////////////////////////
	public class MyThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (dinshi_handler) {
				try {
					Thread.sleep(300 * 1000);// 线程暂停10秒，单位毫秒
					Message message = new Message();
					message.what = 10001;
					handler.sendMessage(message);// 发送消息
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private boolean firstRunService = false;
	private void tijiao_shangxian(final String type) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", type);
		params.put("username", loginName);
		params.put("pwd", loginPassword);
		double currentLongitude = server.getNowLongitude();
		double currentLatitude = server.getNowLatitude();
		String currentCity = server.getNowCity();
		String currentAddress = server.getNowAddress();
		// ////////////////////////////
		System.out.println("addr-上线地址-->" + currentAddress + "|"
				+ String.valueOf(currentLongitude) + ","
				+ String.valueOf(currentLatitude));
		if (firstRunService==false  && (currentLongitude == 0 || currentLatitude == 0
				|| currentCity == null || currentAddress == null)) {
			// ///////////////////////定位失败重新获取位置
			firstRunService = true;
			
			Intent locationIntent = new Intent(getActivity(),
					BackgroundServer.class);
			locationIntent.putExtra(BackgroundServer.BACKGROUND_COMMAND_NAME,
					BackgroundServer.COMMAND_GET_LOCATION);
			
			getActivity().startService(locationIntent);
			ToastUtil.show(getActivity(), "正在定位，请稍后再试...");
			return;
			//tijiao_shangxian(type);
		}
		else if ((currentLongitude == 0 || currentLatitude == 0
				|| currentCity == null || currentAddress == null)) {
			ToastUtil.show(getActivity(), "正在定位，请稍后再试...");
			return;
		}
		params.put("addr",
				currentAddress + "|" + String.valueOf(currentLongitude) + ","
						+ String.valueOf(currentLatitude));
		params.put("city", currentCity);

		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						StatusBean statusBean = GsonUtil.jsonToObjct(
								responseInfo.result, StatusBean.class);
						if ("0".equals(statusBean.status)) {
							System.out.println("--->提交上下线成功");
						} else {
							System.out.println("--返回的status不等0-->"
									+ statusBean.msg);
						}
					}

					@Override
					public void onFailure(HttpException exception, String info) {
						ToastUtil.showNetError(getActivity());
						System.out.println("--->" + exception.getMessage());
					}
				});
	}
}
