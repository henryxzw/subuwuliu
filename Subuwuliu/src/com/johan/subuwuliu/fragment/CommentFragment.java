package com.johan.subuwuliu.fragment;

import java.util.HashMap;

import com.johan.subuwuliu.CommentActivity;
import com.johan.subuwuliu.DriverRecordActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class CommentFragment extends AppFragment {

	private String yid;
	private String startTime, stopTime;
	private String money;

	private TextView yidView, startTimeView, stopTimeView, moneyView;
	private RatingBar ratingBar;
	private EditText contentView;
	private Button submit;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_comment;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		yidView = (TextView) layout.findViewById(R.id.comment_yid);
		startTimeView = (TextView) layout.findViewById(R.id.comment_start_time);
		stopTimeView = (TextView) layout.findViewById(R.id.comment_stop_time);
		moneyView = (TextView) layout.findViewById(R.id.comment_money);
		ratingBar = (RatingBar) layout.findViewById(R.id.comment_rate_bar);
		contentView = (EditText) layout.findViewById(R.id.comment_content);
		submit = (Button) layout.findViewById(R.id.comment_submit);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// 初始化View
		yidView.setText("运单号：" + yid);
		startTimeView.setText("订单时间：" + startTime);
		stopTimeView.setText("完成时间：" + stopTime);
		moneyView.setText("￥" + money);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitData();
			}
		});
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				if (rating < 3) {
					ToastUtil.show(getActivity(), "评价低于于3分哦，评价不能少于10个字哦");
					///	ratingBar.setRating(3.0f);
					contentView.setHint("评价不能少于10个字哦");
				} else {
					contentView.setHint("我想说...");
				}
			}
		});
	}

	private void submitData() {
		String content = contentView.getText().toString();
		String rate = String.valueOf(ratingBar.getRating());
		if (ratingBar.getRating() <= 3) {
			if (content.equals("")) {
				ToastUtil.show(getActivity(), "评价不能为空，并要超过10个字");
				return;
			}
			if (content.length() < 10) {
				ToastUtil.show(getActivity(), "低于3星，评价要超过10个字");
				return;
			}
		}
		CommentActivity commentActivity = (CommentActivity) getActivity();
		final WaitingDialog waitingDialog = new WaitingDialog(getActivity());
		waitingDialog.show(commentActivity.getParentView());

		HashMap<String, String> param = new HashMap<String, String>();
		param.put("key", "driver_comment");
		param.put("username", commentActivity.getLoginName());
		param.put("pwd", commentActivity.getLoginPassword());
		param.put("yid", yid);
		param.put("credit", rate);
		System.out.println("评价分数：" + rate);
		param.put("content", content);
		String action = ToolUtil.encryptionUrlParams(param);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("提交评论结果：" + responseInfo.result);
						StatusBean statusBean = GsonUtil.jsonToObjct(
								responseInfo.result, StatusBean.class);
						if ("0".equals(statusBean.status)) {
							Intent it = new Intent(getActivity(),
									DriverRecordActivity.class);
							startActivity(it);
							getActivity().finish();
						} else {
							ToastUtil.show(getActivity(), statusBean.msg);
						}
						waitingDialog.dismiss();
					}

					@Override
					public void onFailure(HttpException exception, String info) {
						ToastUtil.showNetError(getActivity());
						waitingDialog.dismiss();
					}
				});
	}

	public void setData(String yid, String startTime, String stopTime,
			String money) {
		this.yid = yid;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.money = money;
	}

}
