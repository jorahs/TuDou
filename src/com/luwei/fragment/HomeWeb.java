package com.luwei.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.luwei.potato.R;
import com.luwei.ui.util.FileDownUtil;

/**
 * Created by luwei on 2015-1-25.
 */
public class HomeWeb extends SherlockFragment {
    private WebView webView;
    private Button back;
    private Button reflush;
    private TextView mTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_web, container, false);
        webView = (WebView) rootView.findViewById(R.id.home_WebView);
        webView.loadUrl("http://www.cqxinxiang.com");
        back = (Button) rootView.findViewById(R.id.webView_back);
        reflush = (Button) rootView.findViewById(R.id.webView_reflush);
        mTitle = (TextView) rootView.findViewById(R.id.webView_subTitle);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.goBack();
            }
        });

        reflush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                new FileDownUtil(url).start();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                mTitle.setText(title);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        return rootView;
    }
}
