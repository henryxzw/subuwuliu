package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.bean.CommentBean;
import com.johan.subuwuliu.fragment.CommentFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.umeng.analytics.MobclickAgent;

import android.view.View;

public class CommentActivity extends AppThemeActivity {

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "评论";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_commnet;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		if (getIntent() == null) {
			System.out.println("评论界面没有订单号");
			finish();
		}
		final String yid = getIntent().getStringExtra("yid");
		if (null == yid || "".equals(yid)) {
			System.out.println("评论界面没有订单号");
			finish();
		}
		// 加载数据
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "comment_waybill_info");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("yid", yid);
		String action = ToolUtil.encryptionUrlParams(params);
		WaitingFragment waitingFragment = new WaitingFragment(
				NetUtil.DRIVER_URL + action);
		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				System.out.println("评论获取结果---->" + result);
				try {
					CommentBean commentBean = GsonUtil.jsonToObjct(result,
							CommentBean.class);
					CommentFragment commentFragment = new CommentFragment();
					commentFragment.setData(yid, commentBean.info.start_time,
							commentBean.info.end_time, commentBean.info.money);
					showFragment(commentFragment);
				} catch (Exception e) {
					// TODO: handle exception
					CommentActivity.this.finish();
				}
			}

			@Override
			public void onLoadNoNeedUpdate() {

			}
		});
		addFragment(waitingFragment);
	}

	@Override
	public int getContainerId() {
		// TODO Auto-generated method stub
		return R.id.comment_fragment;
	}

	public View getParentView() {
		return findViewById(R.id.comment_layout);
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
