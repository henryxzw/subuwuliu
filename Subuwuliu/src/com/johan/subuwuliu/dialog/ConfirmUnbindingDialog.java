package com.johan.subuwuliu.dialog;

import com.johan.subuwuliu.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmUnbindingDialog implements OnClickListener {
	
	private Dialog dialog;
	
	private OnClickOkListener onClickOkListener;
	
	private String carCid;
	
	public ConfirmUnbindingDialog(Context context, String carNumber) {
		dialog = new Dialog(context, R.style.DiDiDialog);
		dialog.setContentView(R.layout.dialog_unbinding);
		TextView carNumberTip = (TextView) dialog.findViewById(R.id.unbinding_car_number_tip);
		carNumberTip.setText(carNumber + "车辆的绑定");
		Button butOk = (Button) dialog.findViewById(R.id.unbinding_but_ok);
		Button butCancel = (Button) dialog.findViewById(R.id.unbinding_but_cancel);
		butOk.setOnClickListener(this);
		butCancel.setOnClickListener(this);
	}
	
	public void setOnClickOkListener(OnClickOkListener onClickOkListener) {
		this.onClickOkListener = onClickOkListener;
	}

	public void show() {
		dialog.show();
	}
	
	public void dismiss() {
		dialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.unbinding_but_ok :
			if(onClickOkListener != null) {
				onClickOkListener.clickOk();
			}
			break;
		case R.id.unbinding_but_cancel :
			break;
		default:
			break;
		}
		dismiss();
	}
	
}
