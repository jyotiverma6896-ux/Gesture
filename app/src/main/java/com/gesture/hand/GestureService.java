package com.gesture.hand;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;

public class GestureService extends AccessibilityService {
  static GestureService inst;
  @Override protected void onServiceConnected(){ inst = this; }
  @Override public boolean onUnbind(Intent i){ inst = null; return super.onUnbind(i); }
  @Override public void onAccessibilityEvent(AccessibilityEvent e){}
  @Override public void onInterrupt(){}

  static boolean run(String d){
    GestureService s = inst; if (s == null) return false;
    DisplayMetrics m = s.getResources().getDisplayMetrics();
    float W = m.widthPixels, H = m.heightPixels, cx = W/2, cy = H/2;
    Path p = new Path(); long dur = 200;
    switch (d){
      case "up":    p.moveTo(cx, H*.75f); p.lineTo(cx, H*.25f); break;
      case "down":  p.moveTo(cx, H*.25f); p.lineTo(cx, H*.75f); break;
      case "right": p.moveTo(W*.8f, cy);  p.lineTo(W*.2f, cy);  break;
      case "left":  p.moveTo(W*.2f, cy);  p.lineTo(W*.8f, cy);  break;
      case "tap":   p.moveTo(cx, cy); dur = 50; break;
      default: return false;
    }
    return s.dispatchGesture(new GestureDescription.Builder()
        .addStroke(new GestureDescription.StrokeDescription(p, 0, dur)).build(), null, null);
  }
}
