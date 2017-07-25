/*    */ package com.xiaofu_yan.blux.le.server;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.os.Bundle;
/*    */ import android.os.Message;
/*    */ import android.os.Messenger;
/*    */ import java.util.UUID;
/*    */ 
/*    */ class BluxSsManager extends BluxSsProxy
/*    */ {
/* 13 */   private static final UUID BLUX_SS_CLIENT_MANAGER_UUID = UUID.fromString("78667579-347D-43D8-9E68-CA92CF2FE7B6");
/*    */   private static final int CMD_GET_ROOT_SERVER = 1;
/*    */   private static final int CMD_DETACH_PROCESS = 2;
/*    */   private static final int RSP_GET_ROOT_SERVER = 1;
/*    */   private Context mContext;
/*    */   private BluxBlueGuardManager mBlueGuardManager;
/*    */   static BluxSsManager sActiveManager;
/*    */ 
/*    */   BluxSsManager(Context context)
/*    */   {
/* 26 */     super(null, null);
/* 27 */     this.mContext = context;
/*    */ 
/* 29 */     BluxObject.initThread();
/*    */   }
/*    */ 
/*    */   void startManager() {
/* 33 */     this.mBlueGuardManager = new BluxBlueGuardManager(this.mContext);
/* 34 */     BluxSsProxy.manager().registerProxy(this);
/* 35 */     sActiveManager = this;
/*    */   }
/*    */ 
/*    */   void stopManager() {
/* 39 */     BluxSsProxy.manager().clearAllProxies();
/* 40 */     this.mBlueGuardManager.terminate();
/* 41 */     sActiveManager = null;
/*    */   }
/*    */ 
/*    */   protected boolean handleMessage(Message cmd)
/*    */   {
/* 49 */     switch (cmd.what) {
/*    */     case 1:
/* 51 */       UUID processId = BluxSsProxy.getMessageProcess(cmd);
/* 52 */       if ((processId == null) || (cmd.replyTo == null)) break;
/* 53 */       Bundle data = new Bundle();
/* 54 */       BluxSsBlueGuardManager bgm = new BluxSsBlueGuardManager(this.mBlueGuardManager, processId, cmd.replyTo);
/* 55 */       String to = cmd.getData().getString("from");
/* 56 */       if (to != null)
/* 57 */         data.putString("to", to);
/* 58 */       data.putString("from", BLUX_SS_CLIENT_MANAGER_UUID.toString());
/* 59 */       data.putString("server_id", bgm.uuid().toString());
/* 60 */       data.putString("client_id", bgm.clientId().toString());
/* 61 */       bgm.setStateData(data);
/*    */       try {
/* 63 */         Message msg = Message.obtain(null, 1);
/* 64 */         msg.setData(data);
/* 65 */         cmd.replyTo.send(msg); } catch (Exception localException) {
/*    */       }
/* 67 */       break;
/*    */     case 2:
/* 71 */       Bundle data1 = cmd.getData();
/* 72 */       String uuid = data1.getString("process_uuid");
/* 73 */       BluxSsProxy.manager().notifyProcessDetach(UUID.fromString(uuid));
/* 74 */       break;
/*    */     }
/*    */ 
/* 79 */     return true;
/*    */   }
/*    */ 
/*    */   protected UUID uuid()
/*    */   {
/* 84 */     return BLUX_SS_CLIENT_MANAGER_UUID;
/*    */   }
/*    */ 
/*    */   private void broadcastIntent(Bundle extra)
/*    */   {
/* 89 */     Intent intent = new Intent("com.xiaofu_yan.blux.le.server.action.broadcast");
/* 90 */     intent.putExtras(extra);
/* 91 */     this.mContext.sendBroadcast(intent);
/*    */   }
/*    */ 
/*    */   static void broadcast(Bundle msg)
/*    */   {
/* 96 */     if (sActiveManager != null)
/* 97 */       sActiveManager.broadcastIntent(msg);
/*    */   }
/*    */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxSsManager
 * JD-Core Version:    0.6.0
 */