package org.ble.find;

import org.ble.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {

	public static final String EXTRA_WEB = "WEB_VIEW";
	private WebView webView;
	private String webUrl="";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbs_webview_activity);
		Intent intent=getIntent();
		 webUrl = intent.getStringExtra(EXTRA_WEB);
		 
		webView=(WebView)findViewById(R.id.bbs_webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView view,String url){
				view.loadUrl(url);
				return true;
			}
		});
		webView.loadUrl(webUrl);
	}
	
	protected void onResume() {
		super.onResume();
		
	}
	
}
