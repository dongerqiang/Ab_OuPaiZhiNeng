/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.bluetooth.BluetoothAdapter;
/*     */ import android.os.Bundle;
/*     */ import android.os.Message;
/*     */ import android.os.Messenger;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxSsBlueGuardManager extends BluxSsProxy
/*     */ {
/*     */   private static final int CMD_START_SCAN = 1;
/*     */   private static final int CMD_STOP_SCAN = 2;
/*     */   private static final int CMD_GET_SMART_GUARD = 3;
/*     */   private static final int RSP_SCAN_SMART_GUARD = 1;
/*     */   private static final int RSP_GET_SMART_GUARD = 3;
/*     */   private BluxBlueGuardManager mBlueGuardManager;
/*     */   private BlueGuardManagerDelegate mBlueGuardManagerDelegate;
/*     */ 
/*     */   BluxSsBlueGuardManager(BluxBlueGuardManager bgm, UUID processId, Messenger reply)
/*     */   {
/*  15 */     super(processId, reply);
/*  16 */     this.mBlueGuardManager = bgm;
/*  17 */     this.mBlueGuardManagerDelegate = new BlueGuardManagerDelegate();
/*  18 */     this.mBlueGuardManager.registerDelegate(this.mBlueGuardManagerDelegate);
/*     */   }
/*     */ 
/*     */   void setStateData(Bundle data) {
/*  22 */     data.putBoolean("scanning", this.mBlueGuardManager.isScannning());
/*     */   }
/*     */ 
/*     */   protected void terminate()
/*     */   {
/*  27 */     if (this.mBlueGuardManager != null) {
/*  28 */       this.mBlueGuardManager.unregisterDelegate(this.mBlueGuardManagerDelegate);
/*  29 */       this.mBlueGuardManagerDelegate = null;
/*  30 */       this.mBlueGuardManager = null;
/*     */     }
/*  32 */     super.terminate();
/*     */   }
/*     */ 
/*     */   private BluxSsBlueGuard getSsBlueGuard(String identifier, UUID processId, Messenger reply)
/*     */   {
/*  49 */     BluxSsBlueGuard ssBlueGuard = null;
/*  50 */     BluxBlueGuard blueGuard = this.mBlueGuardManager.getBlueGuard(identifier);
/*  51 */     if (blueGuard != null) {
/*  52 */       ssBlueGuard = new BluxSsBlueGuard(blueGuard, processId, reply);
/*     */     }
/*  54 */     return ssBlueGuard;
/*     */   }
/*     */ 
/*     */   protected boolean handleMessage(Message cmd)
/*     */   {
/*  61 */     if ((super.handleMessage(cmd)) || (this.mBlueGuardManager == null)) {
/*  62 */       return true;
/*     */     }
/*  64 */     switch (cmd.what) {
/*     */     case 1:
/*  66 */       String sUuid = cmd.getData().getString("device_uuid");
/*     */       try {
/*  68 */         UUID uuid = UUID.fromString(sUuid);
/*  69 */         this.mBlueGuardManager.scan(uuid);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */ 
/*     */     case 2:
/*  76 */       this.mBlueGuardManager.stopScan();
/*  77 */       break;
/*     */     case 3:
/*  80 */       String identifier = cmd.getData().getString("identifier");
/*  81 */       UUID processId = BluxSsProxy.getMessageProcess(cmd);
/*  82 */       if ((processId == null) || (cmd.replyTo == null) || (!BluetoothAdapter.checkBluetoothAddress(identifier))) break;
/*  83 */       BluxSsBlueGuard ssmg = getSsBlueGuard(identifier, processId, cmd.replyTo);
/*  84 */       if (ssmg != null) {
/*  85 */         Bundle data = new Bundle();
/*  86 */         data.putString("server_id", ssmg.uuid().toString());
/*  87 */         data.putString("client_id", ssmg.clientId().toString());
/*  88 */         ssmg.setStateData(data);
/*  89 */         notifyClient(3, data);
/*     */       }
/*  91 */       break;
/*     */     default:
/*  95 */       return false;
/*     */     }
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */   private class BlueGuardManagerDelegate extends BluxBlueGuardManager.Delegate
/*     */   {
/*     */     private BlueGuardManagerDelegate() {
/*     */     }
/*     */ 
/*     */     protected void blueGuardManagerFoundBlueGuard(BluxBlueGuardManager blueGuardManager, BluxBlueGuard blueGuard) {
/* 106 */       Bundle data = new Bundle();
/* 107 */       data.putString("identifier", blueGuard.identifier());
/* 108 */       data.putString("name", blueGuard.name());
/* 109 */       BluxSsBlueGuardManager.this.notifyClient(1, data);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxSsBlueGuardManager
 * JD-Core Version:    0.6.0
 */