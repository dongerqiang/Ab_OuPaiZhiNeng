/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.content.Context;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxBlueGuardManager extends BluxObject
/*     */ {
/*     */   private BluxCentral mCentral;
/*     */   private List<Delegate> mDelegates;
/*     */   private List<BluxBlueGuard> mBlueGuardPool;
/*     */ 
/*     */   BluxBlueGuardManager(Context context)
/*     */   {
/*  25 */     this.mCentral = new BluxCentral(context);
/*  26 */     this.mCentral.delegate = new BluxCentralDelegate(null);
/*  27 */     this.mBlueGuardPool = new ArrayList();
/*  28 */     this.mDelegates = new ArrayList();
/*     */   }
/*     */ 
/*     */   protected void terminate() {
/*  32 */     if (this.mBlueGuardPool != null) {
/*  33 */       for (BluxBlueGuard blugGuard : this.mBlueGuardPool)
/*  34 */         blugGuard.terminate();
/*  35 */       this.mBlueGuardPool = null;
/*  36 */       this.mCentral.terminate();
/*  37 */       this.mCentral = null;
/*  38 */       this.mDelegates = null;
/*     */     }
/*  40 */     super.terminate();
/*     */   }
/*     */ 
/*     */   void registerDelegate(Delegate delegate) {
/*  44 */     if (!this.mDelegates.contains(delegate))
/*  45 */       this.mDelegates.add(delegate);
/*     */   }
/*     */ 
/*     */   void unregisterDelegate(Delegate delegate)
/*     */   {
/*  50 */     this.mDelegates.remove(delegate);
/*  51 */     if ((this.mDelegates.size() == 0) && (this.mCentral.isScanning()))
/*  52 */       this.mCentral.stopScan();
/*     */   }
/*     */ 
/*     */   boolean isScannning()
/*     */   {
/*  57 */     if (this.mCentral != null) {
/*  58 */       return this.mCentral.isScanning();
/*     */     }
/*  60 */     return false;
/*     */   }
/*     */ 
/*     */   void scan(UUID uuid) {
/*  64 */     if ((this.mCentral != null) && (uuid != null)) {
/*  65 */       UUID[] uuids = new UUID[1];
/*  66 */       uuids[0] = uuid;
/*  67 */       this.mCentral.startScan(uuids);
/*     */     }
/*     */   }
/*     */ 
/*     */   void stopScan() {
/*  72 */     if (this.mCentral != null)
/*  73 */       this.mCentral.stopScan();
/*     */   }
/*     */ 
/*     */   BluxBlueGuard getBlueGuard(String identifier)
/*     */   {
/*  78 */     if ((identifier == null) || (this.mCentral == null) || (this.mBlueGuardPool == null)) {
/*  79 */       return null;
/*     */     }
/*  81 */     BluxBlueGuard blueGuard = findInPool(identifier);
/*  82 */     if (blueGuard != null) {
/*  83 */       return blueGuard;
/*     */     }
/*  85 */     BluxPeripheral peripheral = this.mCentral.getPeripheral(identifier);
/*  86 */     if (peripheral != null) {
/*  87 */       blueGuard = new BluxBlueGuard(peripheral);
/*  88 */       this.mBlueGuardPool.add(blueGuard);
/*     */     }
/*  90 */     return blueGuard;
/*     */   }
/*     */ 
/*     */   private BluxBlueGuard findInPool(String identifier)
/*     */   {
/* 118 */     for (BluxBlueGuard sg : this.mBlueGuardPool) {
/* 119 */       if (sg.identifier().compareTo(identifier) == 0)
/* 120 */         return sg;
/*     */     }
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   private class BluxCentralDelegate extends BluxCentral.Delegate
/*     */   {
/*     */     private BluxCentralDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void reportPeripheral(BluxPeripheral peripheral, int rssi)
/*     */     {
/*  98 */       if ((BluxBlueGuardManager.this.mBlueGuardPool == null) || (BluxBlueGuardManager.this.mDelegates == null)) {
/*  99 */         return;
/*     */       }
/* 101 */       BluxBlueGuard blueGuard = BluxBlueGuardManager.this.findInPool(peripheral.identifier());
/* 102 */       if (blueGuard == null) {
/* 103 */         blueGuard = new BluxBlueGuard(peripheral);
/* 104 */         BluxBlueGuardManager.this.mBlueGuardPool.add(blueGuard);
/*     */       }
/*     */ 
/* 107 */       for (BluxBlueGuardManager.Delegate delegate : BluxBlueGuardManager.this.mDelegates)
/* 108 */         delegate.blueGuardManagerFoundBlueGuard(BluxBlueGuardManager.this, blueGuard);
/*     */     }
/*     */ 
/*     */     protected void stateChangeTo(int state)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void blueGuardManagerFoundBlueGuard(BluxBlueGuardManager blueGuardManager, BluxBlueGuard blueGuard)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxBlueGuardManager
 * JD-Core Version:    0.6.0
 */