/*    */ package com.xiaofu_yan.blux.le.server;
/*    */ 
/*    */ import android.app.Notification;
/*    */ import android.app.Notification.Builder;
/*    */ import android.app.Service;
/*    */ import android.content.Intent;
/*    */ import android.os.Handler;
/*    */ import android.os.IBinder;
/*    */ import android.os.Message;
/*    */ import android.os.Messenger;
/*    */ 
/*    */ public class BluxSsService extends Service
/*    */ {
/*    */   private Messenger mMessenger;
/*    */   private BluxSsManager mManager;
/*    */ 
/*    */   public int onStartCommand(Intent intent, int flags, int startId)
/*    */   {
/* 30 */     return super.onStartCommand(intent, flags, startId);
/*    */   }
/*    */ 
/*    */   public IBinder onBind(Intent intent)
/*    */   {
/* 35 */     return this.mMessenger.getBinder();
/*    */   }
/*    */ 
/*    */   public boolean onUnbind(Intent intent)
/*    */   {
/* 40 */     return super.onUnbind(intent);
/*    */   }
/*    */ 
/*    */   public void onCreate()
/*    */   {
/* 45 */     this.mMessenger = new Messenger(new IncomingHandler());
/* 46 */     this.mManager = new BluxSsManager(this);
/* 47 */     this.mManager.startManager();
/*    */ 
/* 49 */     setToForeGround();
/* 50 */     super.onCreate();
/*    */   }
/*    */ 
/*    */   public void onDestroy()
/*    */   {
/* 55 */     this.mManager.stopManager();
/* 56 */     this.mManager = null;
/* 57 */     super.onDestroy();
/*    */   }
/*    */ 
/*    */   private void setToForeGround()
/*    */   {
/* 62 */     Notification notification = BluxSsServer.getNotification();
/* 63 */     if (notification == null) {
/* 64 */       Notification.Builder nb = new Notification.Builder(this);
/*    */ 
/* 66 */       nb.setTicker("SmartGuard");
/* 67 */       nb.setContentTitle("SmartGuard");
/* 68 */       nb.setContentText("SmartGuard server");
/* 69 */       notification = nb.build();
/*    */     }
/* 71 */     startForeground(100, notification);
/*    */   }
/*    */ 
/*    */   private static class IncomingHandler extends Handler
/*    */   {
/*    */     public void handleMessage(Message msg)
/*    */     {
/* 22 */       BluxSsProxy.manager().deliverMessage(msg);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxSsService
 * JD-Core Version:    0.6.0
 */