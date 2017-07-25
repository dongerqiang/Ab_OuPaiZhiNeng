/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.os.Bundle;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxBlueGuard extends BluxObject
/*     */ {
/*     */   private static final int HEART_BEAT_PERIOD = 500;
/*     */   private static final int OPEN_TRUNK_PERIOD = 800;
/*     */   private static final int RSSI_BUFFER_SIZE = 5;
/*     */   private static final int FENCE_RSSI_MIN = -75;
/*     */   private static final int FENCE_RSSI_MAX = -30;
/*     */   private static final int FENCE_RIBBON_PERCENT = 4;
/*     */   private BluxDevice mDevice;
/*     */   private BluxVDPrivate mVDPrivate;
/*     */   private BluxVDSmartGuard mVDSmartGuard;
/*     */   private List<Delegate> mDelegates;
/*     */   private String mName;
/*     */   private String mIdentifier;
/*     */   private State mState;
/*     */   private boolean mArmDeviceOnDisconnect;
/*     */   private int mFenceRangePercent;
/*     */   private int mLastRangePercent;
/*     */   private List<Integer> mRssiBuffer;
/*     */ 
/*     */   BluxBlueGuard(BluxPeripheral peripheral)
/*     */   {
/*  77 */     this.mDevice = new BluxDevice(peripheral);
/*  78 */     this.mDevice.delegate = new DeviceDelegate(null);
/*     */ 
/*  80 */     this.mRssiBuffer = new ArrayList();
/*  81 */     this.mDelegates = new ArrayList();
/*     */ 
/*  83 */     this.mName = peripheral.name();
/*  84 */     this.mIdentifier = peripheral.identifier();
/*  85 */     this.mState = State.UNKNOWN;
/*     */ 
/*  87 */     this.mFenceRangePercent = -1;
/*  88 */     this.mArmDeviceOnDisconnect = false;
/*     */   }
/*     */ 
/*     */   protected void terminate() {
/*  92 */     if (this.mDevice != null) {
/*  93 */       this.mDevice.terminate();
/*  94 */       this.mDevice = null;
/*     */     }
/*  96 */     if (this.mRssiBuffer != null) {
/*  97 */       this.mRssiBuffer = null;
/*     */     }
/*  99 */     this.mDelegates = null;
/* 100 */     this.mName = null;
/* 101 */     this.mIdentifier = null;
/* 102 */     super.terminate();
/*     */   }
/*     */ 
/*     */   void registerDelegate(Delegate delegate) {
/* 106 */     if (!this.mDelegates.contains(delegate))
/* 107 */       this.mDelegates.add(delegate);
/*     */   }
/*     */ 
/*     */   void unregisterDelegate(Delegate delegate) {
/* 111 */     this.mDelegates.remove(delegate);
/*     */   }
/*     */ 
/*     */   String identifier()
/*     */   {
/* 116 */     return this.mIdentifier;
/*     */   }
/*     */ 
/*     */   boolean connected() {
/* 120 */     if (this.mDevice != null)
/* 121 */       return this.mDevice.connected();
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   State state() {
/* 126 */     return this.mState;
/*     */   }
/*     */ 
/*     */   void setState(State state) {
/* 130 */     if (this.mVDSmartGuard != null)
/* 131 */       this.mVDSmartGuard.writeState(stateToVdState(state), false);
/*     */   }
/*     */ 
/*     */   String name() {
/* 135 */     return this.mName;
/*     */   }
/*     */ 
/*     */   void setName(String name) {
/* 139 */     if (this.mVDPrivate != null)
/* 140 */       this.mVDPrivate.writeName(name);
/*     */   }
/*     */ 
/*     */   int fenceRangePercent() {
/* 144 */     return this.mFenceRangePercent;
/*     */   }
/*     */ 
/*     */   int currentRangePercent() {
/* 148 */     float percent = 0.0F;
/* 149 */     float rssi = 0.0F;
/*     */ 
/* 151 */     if ((this.mRssiBuffer != null) && (this.mRssiBuffer.size() > 0)) {
/* 152 */       for (Integer n : this.mRssiBuffer)
/* 153 */         rssi += n.intValue();
/* 154 */       rssi /= this.mRssiBuffer.size();
/*     */     }
/*     */     else {
/* 157 */       rssi = -100.0F;
/*     */     }
/* 159 */     percent = (-30.0F - rssi) * 100.0F / 45.0F;
/*     */ 
/* 161 */     return (int)percent;
/*     */   }
/*     */ 
/*     */   BluxAccountManager getAccountManager()
/*     */   {
/* 166 */     if (this.mDevice != null) {
/* 167 */       return this.mDevice.getAccountManager();
/*     */     }
/* 169 */     return null;
/*     */   }
/*     */ 
/*     */   void connect() {
/* 173 */     if (this.mDevice != null)
/* 174 */       this.mDevice.connect();
/*     */   }
/*     */ 
/*     */   void cancelConnect()
/*     */   {
/* 179 */     if (this.mDevice != null)
/* 180 */       this.mDevice.cancelConnect();
/*     */   }
/*     */ 
/*     */   void passPair(int pass)
/*     */   {
/* 185 */     if (this.mDevice != null)
/* 186 */       this.mDevice.passPair(pass);
/*     */   }
/*     */ 
/*     */   void cancelPair()
/*     */   {
/* 191 */     if (this.mDevice != null)
/* 192 */       this.mDevice.cancelPair();
/*     */   }
/*     */ 
/*     */   void setKey(String key)
/*     */   {
/* 197 */     if (this.mDevice != null)
/* 198 */       this.mDevice.setKey(key);
/*     */   }
/*     */ 
/*     */   void playSound(int id)
/*     */   {
/* 203 */     if (this.mVDSmartGuard != null)
/* 204 */       this.mVDSmartGuard.playMusic((byte)id);
/*     */   }
/*     */ 
/*     */   void openTrunk()
/*     */   {
/* 209 */     if (this.mVDSmartGuard != null)
/* 210 */       this.mVDSmartGuard.openTrunk(800);
/*     */   }
/*     */ 
/*     */   void getSerialNumber()
/*     */   {
/* 215 */     if (this.mVDPrivate != null)
/* 216 */       this.mVDPrivate.getSerialNumber();
/*     */   }
/*     */ 
/*     */   void getPairPasskey()
/*     */   {
/* 221 */     if (this.mVDPrivate != null)
/* 222 */       this.mVDPrivate.getPairPasskey();
/*     */   }
/*     */ 
/*     */   void getAlarmConfig()
/*     */   {
/* 227 */     if (this.mVDSmartGuard != null)
/* 228 */       this.mVDSmartGuard.readAlarmConfig();
/*     */   }
/*     */ 
/*     */   void setAlarmConfig(boolean alarmDevice, boolean notifyPhone)
/*     */   {
/* 233 */     if (this.mVDSmartGuard != null)
/* 234 */       this.mVDSmartGuard.writeAlarmConfig(alarmDevice, notifyPhone);
/*     */   }
/*     */ 
/*     */   void getShockSensitivity()
/*     */   {
/* 239 */     if (this.mVDSmartGuard != null)
/* 240 */       this.mVDSmartGuard.readShockSensitivity();
/*     */   }
/*     */ 
/*     */   void setShockSensitivity(int level)
/*     */   {
/* 245 */     if (this.mVDSmartGuard != null)
/* 246 */       this.mVDSmartGuard.writeShockSensitivity((byte)level);
/*     */   }
/*     */ 
/*     */   void setMileage(long pulses)
/*     */   {
/* 251 */     if (this.mVDSmartGuard != null)
/* 252 */       this.mVDSmartGuard.writeMileage(pulses);
/*     */   }
/*     */ 
/*     */   void setFenceRangePercent(int fenceRangePercent)
/*     */   {
/* 257 */     fenceRangePercent = (fenceRangePercent >= 0) && (fenceRangePercent <= 100) ? fenceRangePercent : -1;
/*     */ 
/* 259 */     if (fenceRangePercent != this.mFenceRangePercent) {
/* 260 */       this.mFenceRangePercent = fenceRangePercent;
/* 261 */       setArmDeviceOnDisconnect(this.mFenceRangePercent != -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean writeVirtualDevice(UUID uuidType, int register, byte[] data) {
/* 266 */     if (this.mDevice != null) {
/* 267 */       BluxVirtualDevice vd = this.mDevice.getVirtualDevice(uuidType);
/* 268 */       if (vd != null)
/* 269 */         return vd.writeRegister(register, data);
/*     */     }
/* 271 */     return false;
/*     */   }
/*     */ 
/*     */   private BluxVDSmartGuard.State stateToVdState(State state)
/*     */   {
/* 276 */     if (state == State.ARMED)
/* 277 */       return BluxVDSmartGuard.State.ARMED;
/* 278 */     if (state == State.STOPPED)
/* 279 */       return BluxVDSmartGuard.State.STOPPED;
/* 280 */     if (state == State.STARTED)
/* 281 */       return BluxVDSmartGuard.State.STARTED;
/* 282 */     if (state == State.RUNNING)
/* 283 */       return BluxVDSmartGuard.State.RUNNING;
/* 284 */     return BluxVDSmartGuard.State.UNKNOWN;
/*     */   }
/*     */ 
/*     */   private State vdStateToState(BluxVDSmartGuard.State state) {
/* 288 */     if (state == BluxVDSmartGuard.State.ARMED)
/* 289 */       return State.ARMED;
/* 290 */     if (state == BluxVDSmartGuard.State.STOPPED)
/* 291 */       return State.STOPPED;
/* 292 */     if (state == BluxVDSmartGuard.State.STARTED)
/* 293 */       return State.STARTED;
/* 294 */     if (state == BluxVDSmartGuard.State.RUNNING)
/* 295 */       return State.RUNNING;
/* 296 */     return State.UNKNOWN;
/*     */   }
/*     */ 
/*     */   private void setArmDeviceOnDisconnect(boolean armDeviceOnDisconnect) {
/* 300 */     if (this.mArmDeviceOnDisconnect != armDeviceOnDisconnect) {
/* 301 */       this.mArmDeviceOnDisconnect = armDeviceOnDisconnect;
/* 302 */       if (this.mVDSmartGuard != null)
/* 303 */         this.mVDSmartGuard.setAutoArm(this.mArmDeviceOnDisconnect);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateRSSI(int RSSI)
/*     */   {
/* 310 */     this.mRssiBuffer.add(Integer.valueOf(RSSI));
/* 311 */     if (this.mRssiBuffer.size() > 5) {
/* 312 */       this.mRssiBuffer.remove(0);
/*     */     }
/* 314 */     int range = currentRangePercent();
/*     */     int rangeEnter;
/* 315 */     if ((this.mFenceRangePercent >= 0) && (this.mFenceRangePercent <= 100) && (this.mRssiBuffer.size() == 5))
/*     */     {
/* 317 */       rangeEnter = this.mFenceRangePercent - 4;
/* 318 */       int rangeLeave = this.mFenceRangePercent + 4;
/* 319 */       if ((range >= rangeLeave) && (this.mLastRangePercent < rangeLeave)) {
/* 320 */         for (Delegate delegate : this.mDelegates)
/* 321 */           delegate.blueGuardLeaveFence(this);
/* 322 */         if (this.mState == State.STOPPED)
/* 323 */           setState(State.ARMED);
/*     */       }
/* 325 */       else if ((range <= rangeEnter) && (this.mLastRangePercent > rangeEnter)) {
/* 326 */         for (Delegate delegate : this.mDelegates)
/* 327 */           delegate.blueGuardEnterFence(this);
/* 328 */         if (this.mState == State.ARMED)
/* 329 */           setState(State.STOPPED);
/*     */       }
/*     */     }
/* 332 */     this.mLastRangePercent = range;
/*     */ 
/* 334 */     for (Delegate delegate : this.mDelegates)
/* 335 */       delegate.blueGuardCurrentRange(this, range);
/*     */   }
/*     */ 
/*     */   private void broadcastConnection(boolean connected) {
/* 339 */     Bundle msg = new Bundle();
/* 340 */     msg.putString("sender", identifier());
/* 341 */     msg.putString("action", "connection");
/* 342 */     msg.putBoolean("connected", connected);
/* 343 */     BluxSsManager.broadcast(msg);
/*     */   }
/*     */ 
/*     */   private void broadcastState(State state) {
/* 347 */     Bundle msg = new Bundle();
/* 348 */     msg.putString("sender", identifier());
/* 349 */     msg.putString("action", "state");
/* 350 */     if (state == State.ARMED)
/* 351 */       msg.putString("state", "armed");
/* 352 */     else if (state == State.RUNNING)
/* 353 */       msg.putString("state", "running");
/* 354 */     else if (state == State.STARTED)
/* 355 */       msg.putString("state", "started");
/* 356 */     else if (state == State.STOPPED)
/* 357 */       msg.putString("state", "stopped");
/*     */     else
/* 359 */       msg.putString("state", "unknown");
/* 360 */     BluxSsManager.broadcast(msg);
/*     */   }
/*     */ 
/*     */   private void broadcastAlarm(int level) {
/* 364 */     Bundle msg = new Bundle();
/* 365 */     msg.putString("sender", identifier());
/* 366 */     msg.putString("action", "connection");
/* 367 */     msg.putInt("level", level);
/* 368 */     BluxSsManager.broadcast(msg);
/*     */   }
/*     */ 
/*     */   private class VDSmartGuardDelegate extends BluxVDSmartGuard.Delegate
/*     */   {
/*     */     private VDSmartGuardDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void alarm(byte type)
/*     */     {
/* 490 */       BluxBlueGuard.AlarmType t = BluxBlueGuard.AlarmType.LOW_ALARM;
/* 491 */       if (type == 1)
/* 492 */         t = BluxBlueGuard.AlarmType.HIGH_ALARM;
/* 493 */       else if (type == 2) {
/* 494 */         t = BluxBlueGuard.AlarmType.POWER_LEFT_ON;
/*     */       }
/* 496 */       BluxBlueGuard.this.broadcastAlarm(type);
/*     */ 
/* 498 */       if (BluxBlueGuard.this.mDelegates != null)
/*     */       {
/* 500 */         for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 501 */           delegate.blueGuardAlarm(BluxBlueGuard.this, t);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void updateState(BluxVDSmartGuard.State state)
/*     */     {
/* 508 */       BluxBlueGuard.access$1102(BluxBlueGuard.this, BluxBlueGuard.this.vdStateToState(state));
/* 509 */       BluxBlueGuard.this.broadcastState(BluxBlueGuard.this.mState);
/*     */ 
/* 511 */       if (BluxBlueGuard.this.mDelegates != null)
/* 512 */         for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 513 */           delegate.blueGuardState(BluxBlueGuard.this, BluxBlueGuard.this.mState);
/*     */     }
/*     */ 
/*     */     protected void updateShockSensitivity(int level)
/*     */     {
/* 519 */       if (BluxBlueGuard.this.mDelegates != null)
/* 520 */         for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 521 */           delegate.blueGuardShockLevel(BluxBlueGuard.this, level);
/*     */     }
/*     */ 
/*     */     protected void updateADC(short mv)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateAlarmConfig(boolean alarmDevice, boolean notifyPhone)
/*     */     {
/* 531 */       if (BluxBlueGuard.this.mDelegates != null)
/* 532 */         for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 533 */           delegate.blueGuardAlarmConfig(BluxBlueGuard.this, alarmDevice, notifyPhone);
/*     */     }
/*     */ 
/*     */     protected void updateSpeedConfig(int monitorPeriod, int hallSensorCounter)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateShockLevelTable(byte[] table)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateAlarmMusicConfig(int musicCount, int musicSize, int noteCount, int noteSize)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateAlarmNote(int noteID, byte[] data)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateAlarmMusic(int musicID, byte[] data)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class VDPrivateDelegate extends BluxVDPrivate.Delegate
/*     */   {
/*     */     private VDPrivateDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void heartBeat(byte[] data)
/*     */     {
/* 422 */       if (data.length >= 13)
/*     */       {
/* 425 */         int rssi = data[0];
/* 426 */         BluxBlueGuard.this.updateRSSI(rssi);
/*     */ 
/* 428 */         if (BluxBlueGuard.this.mDelegates != null)
/* 429 */           for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 430 */             delegate.blueGuardUpdateData(BluxBlueGuard.this, data);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void updateSerialNumber(String serialNumber)
/*     */     {
/* 437 */       if (BluxBlueGuard.this.mDelegates != null)
/* 438 */         for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 439 */           delegate.blueGuardSerialNumber(BluxBlueGuard.this, serialNumber);
/*     */     }
/*     */ 
/*     */     protected void updateName(String name)
/*     */     {
/* 445 */       BluxBlueGuard.access$902(BluxBlueGuard.this, name);
/* 446 */       if (BluxBlueGuard.this.mDelegates != null)
/* 447 */         for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 448 */           delegate.blueGuardName(BluxBlueGuard.this, name);
/*     */     }
/*     */ 
/*     */     protected void updateHeartBeat(short period)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updatePeerRole(byte role)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateConnectionParam(short timeOut, short minMs, short maxMs, short latency)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updatePairPasskey(String passKey)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateBroadcastAD(byte[] ad)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateUserStorageConfig(short size)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateUserStorage(short offset, byte[] words)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updatePrivateNvm(short offset, byte[] words)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class DeviceDelegate extends BluxDevice.Delegate
/*     */   {
/*     */     private DeviceDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void deviceConnected(BluxDevice device)
/*     */     {
/* 376 */       BluxBlueGuard.access$102(BluxBlueGuard.this, (BluxVDPrivate)device.getVirtualDevice(BluxVDPrivate.VD_TYPE_UUID));
/* 377 */       if (BluxBlueGuard.this.mVDPrivate != null) {
/* 378 */         BluxBlueGuard.this.mVDPrivate.delegate = new BluxBlueGuard.VDPrivateDelegate(BluxBlueGuard.this, null);
/* 379 */         BluxBlueGuard.this.mVDPrivate.writePeerHeartBeat(500);
/*     */       }
/*     */ 
/* 382 */       BluxBlueGuard.access$302(BluxBlueGuard.this, (BluxVDSmartGuard)device.getVirtualDevice(BluxVDSmartGuard.VD_TYPE_UUID));
/* 383 */       if (BluxBlueGuard.this.mVDSmartGuard != null) {
/* 384 */         BluxBlueGuard.this.mVDSmartGuard.delegate = new BluxBlueGuard.VDSmartGuardDelegate(BluxBlueGuard.this, null);
/* 385 */         BluxBlueGuard.this.mVDSmartGuard.syncState(BluxBlueGuard.this.mArmDeviceOnDisconnect);
/*     */       }
/*     */ 
/* 388 */       if ((BluxBlueGuard.this.mVDPrivate != null) && (BluxBlueGuard.this.mVDSmartGuard != null))
/*     */       {
/* 390 */         BluxBlueGuard.this.broadcastConnection(true);
/*     */ 
/* 392 */         if (BluxBlueGuard.this.mDelegates != null)
/* 393 */           for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 394 */             delegate.blueGuardConnected(BluxBlueGuard.this);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void deviceDisconnected(BluxDevice device, BluxDevice.DisconnectReason reason)
/*     */     {
/* 401 */       if (BluxBlueGuard.this.mDelegates != null) {
/* 402 */         for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 403 */           delegate.blueGuardDisconnected(BluxBlueGuard.this, reason);
/*     */       }
/* 405 */       BluxBlueGuard.access$102(BluxBlueGuard.this, null);
/* 406 */       BluxBlueGuard.access$302(BluxBlueGuard.this, null);
/*     */     }
/*     */ 
/*     */     protected void devicePairResult(BluxDevice device, BluxDevice.PairResult result, String key)
/*     */     {
/* 411 */       if (BluxBlueGuard.this.mDelegates != null)
/* 412 */         for (BluxBlueGuard.Delegate delegate : BluxBlueGuard.this.mDelegates)
/* 413 */           delegate.blueGuardPairResult(BluxBlueGuard.this, result, key);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void blueGuardConnected(BluxBlueGuard bg)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardDisconnected(BluxBlueGuard bg, BluxDevice.DisconnectReason reason)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardCurrentRange(BluxBlueGuard bg, int percentRange)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardLeaveFence(BluxBlueGuard bg)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardEnterFence(BluxBlueGuard bg)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardAlarm(BluxBlueGuard bg, BluxBlueGuard.AlarmType type)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardAlarmConfig(BluxBlueGuard bg, boolean alarmDevice, boolean notifyPhone)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardState(BluxBlueGuard bg, BluxBlueGuard.State state)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardName(BluxBlueGuard bg, String name)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardShockLevel(BluxBlueGuard bg, int level)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardSerialNumber(BluxBlueGuard bg, String sn)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardPairPasskey(String passkey)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardUpdateData(BluxBlueGuard bg, byte[] data)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardAccountManager(BluxBlueGuard bg, BluxAccountManager bam)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void blueGuardPairResult(BluxBlueGuard bg, BluxDevice.PairResult result, String key)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static enum AlarmType
/*     */   {
/*  24 */     LOW_ALARM, 
/*  25 */     HIGH_ALARM, 
/*  26 */     POWER_LEFT_ON;
/*     */   }
/*     */ 
/*     */   static enum State
/*     */   {
/*  16 */     UNKNOWN, 
/*  17 */     ARMED, 
/*  18 */     STOPPED, 
/*  19 */     STARTED, 
/*  20 */     RUNNING;
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxBlueGuard
 * JD-Core Version:    0.6.0
 */