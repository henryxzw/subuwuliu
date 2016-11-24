package com.johan.subuwuliu.fragment;

import com.johan.subuwuliu.AppActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class WaitingFragment extends AppFragment {
	
	private TextView waitingTip;
	private ImageView waitingImg;
	
	public String loadUri;
	private OnLoadListener onLoadListener;
	
	public WaitingFragment(String loadUri) {
		this.loadUri = loadUri;
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_waiting;
	}
	
	public void load(final OnLoadListener onLoadListener) {
		this.onLoadListener = onLoadListener;
		NetUtil.getInstance().doGet(loadUri, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String status = GsonUtil.getJsonValue(responseInfo.result, "status");
				if("0".equals(status)) {
					onLoadListener.onLoadSuccess(responseInfo.result);
				} else if("200".equals(status)) {
					onLoadListener.onLoadNoNeedUpdate();
				} else {
					if(null == getActivity() || getActivity().isFinishing()) {
			    		System.out.println("activity 已经关闭， 不能切换Fragment");
			    		return;
			    	} else {
			    		System.out.println("activity 没有关闭， 可以切换Fragment");
			    	}
					String msg = GsonUtil.getJsonValue(responseInfo.result, "msg"); 
					ToastUtil.show(getActivity(), msg);
					waitingTip.setVisibility(View.VISIBLE);
					waitingTip.setEnabled(true);
				}
			}
			@Override
			public void onFailure(HttpException exception, String info) {
				if(null == getActivity() || getActivity().isFinishing()) {
		    		System.out.println("activity 已经关闭， 不能切换Fragment");
		    		return;
		    	} else {
		    		System.out.println("activity 没有关闭， 可以切换Fragment");
		    	}
				ToastUtil.showNetError(getActivity());
				waitingTip.setVisibility(View.VISIBLE);
				waitingTip.setEnabled(true);
			}
		});
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		waitingTip = (TextView) layout.findViewById(R.id.waiting_tip);
		waitingTip.setVisibility(View.GONE);
		waitingTip.setEnabled(false);
		waitingTip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				waitingTip.setVisibility(View.GONE);
				waitingTip.setEnabled(false);
				load(onLoadListener);
			}
		});
		waitingImg = (ImageView) layout.findViewById(R.id.waiting_img);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_waiting);
		waitingImg.setAnimation(animation);
		animation.start();
	}
	
	public interface OnLoadListener {
		public void onLoadSuccess(String result);
		public void onLoadNoNeedUpdate();
	}

}
