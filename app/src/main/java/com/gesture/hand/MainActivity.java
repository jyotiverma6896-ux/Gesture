package com.gesture.hand;
import android.Manifest;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Rational;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.Toast;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends Activity {
  WebView wv;
  @Override protected void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
      requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
    wv = new WebView(this); setContentView(wv);
    WebSettings s = wv.getSettings();
    s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setMediaPlaybackRequiresUserGesture(false);
    final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this)).build();
    wv.setWebViewClient(new WebViewClient(){
      @Override public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r){
        return loader.shouldInterceptRequest(r.getUrl());
      }
    });
    wv.setWebChromeClient(new WebChromeClient(){
      @Override public void onPermissionRequest(final PermissionRequest r){
        runOnUiThread(() -> r.grant(r.getResources()));
      }
    });
    wv.addJavascriptInterface(new Object(){
      @JavascriptInterface public boolean cmd(String d){ return GestureService.run(d); }
      @JavascriptInterface public void stopService(){ GestureService.stop(); }
      @JavascriptInterface public boolean ready(){ return GestureService.inst != null; }
      @JavascriptInterface public void openAccessibility(){
        runOnUiThread(() -> {
          Toast.makeText(MainActivity.this, "Hand Gesture ko ON karo", Toast.LENGTH_LONG).show();
          startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        });
      }
    }, "Android");
    wv.loadUrl("https://appassets.androidplatform.net/assets/index.html");
  }
  @Override protected void onUserLeaveHint(){
    if (GestureService.inst != null)
      enterPictureInPictureMode(new PictureInPictureParams.Builder().setAspectRatio(new Rational(3,4)).build());
  }
  @Override protected void onStop(){
    super.onStop();
    // PiP window cross (X) dabane par app poori band
    if (isInPictureInPictureMode()) finishAndRemoveTask();
  }
  @Override protected void onDestroy(){
    super.onDestroy();
    if (isFinishing()){
      GestureService.stop();          // accessibility bhi band
      if (wv != null){ wv.destroy(); wv = null; }
    }
  }
}
