/*    */ package com.xiaofu_yan.blux.le.server;
/*    */ 
/*    */ import android.app.Notification;
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ 
/*    */ public class BluxSsServer
/*    */ {
/*    */   private Context mContext;
/*    */   private boolean mServiceRunning;
/*    */   private static Notification sNotification;
/*    */   private static BluxSsServer sServer;
/*    */ 
/*    */   public static BluxSsServer sharedInstance()
/*    */   {
/* 15 */     if (sServer == null)
/* 16 */       sServer = new BluxSsServer();
/* 17 */     return sServer;
/*    */   }
/*    */ 
/*    */   public void setForeGroundNotification(Notification notification) {
/* 21 */     sNotification = notification;
/*    */   }
/*    */ 
/*    */   public void start(Context context) {
/* 25 */     if ((!this.mServiceRunning) && (sNotification != null)) {
/* 26 */       this.mContext = context;
/* 27 */       Intent intent = new Intent(context, BluxSsService.class);
/* 28 */       this.mContext.startService(intent);
/* 29 */       this.mServiceRunning = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void stop() {
/* 33 */     if ((this.mServiceRunning) && (this.mContext != null)) {
/* 34 */       Intent intent = new Intent(this.mContext, BluxSsService.class);
/* 35 */       this.mContext.stopService(intent);
/* 36 */       this.mServiceRunning = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean isStarted() {
/* 41 */     return this.mServiceRunning;
/*    */   }
/*    */ 
/*    */   static Notification getNotification() {
/* 45 */     return sNotification;
/*    */   }
/*    */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxSsServer
 * JD-Core Version:    0.6.0
 */