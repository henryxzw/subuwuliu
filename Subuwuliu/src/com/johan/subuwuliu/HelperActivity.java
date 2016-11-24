package com.johan.subuwuliu;

import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;

import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HelperActivity extends AppThemeActivity {
	
	private WebView webView;
	private ProgressBar progressbar;
	private TextView tipView;
	
	private boolean isLoadFail;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "帮助";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_help;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		webView = (WebView) findViewById(R.id.help_webview);
		progressbar = (ProgressBar) findViewById(R.id.help_progress);
		tipView = (TextView) findViewById(R.id.help_tip);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);       
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				progressbar.setProgress(progress);
				System.out.println("progress : " + progress);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				progressbar.setVisibility(View.GONE);
				if(!isLoadFail) {
					tipView.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				System.out.println("加载错误-----------------------------");
				progressbar.setVisibility(View.GONE);
				tipView.setText("加载失败，点击重新加载");
				tipView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						load();
					}
				});
				isLoadFail = true;
			}
		});
		load();
	}
	
	private void load() {
		isLoadFail = false;
		webView.loadUrl(NetUtil.Help_IP);
		tipView.setText("网页正在加载...");
		tipView.setOnClickListener(null);
		progressbar.setVisibility(View.VISIBLE);
	}

}
