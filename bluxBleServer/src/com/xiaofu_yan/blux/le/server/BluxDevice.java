/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.util.Log;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxDevice extends BluxObject
/*     */ {
/*     */   Delegate delegate;
/*     */   private static final int PAIR_TIMEOUT_SECONDS = 30;
/*     */   private State mState;
/*     */   private BluxPeripheral mPeripheral;
/*     */   private BluxServiceVirtualDevice mService;
/*     */   private BluxVDDevice0 mVDDevice0;
/*     */   private BluxBioSecure mBioSecure;
/*     */   private BluxAccountManager mAccountManager;
/*     */   private AuthType mAuthType;
/*     */   private String mKey;
/*     */   private int mPasskey;
/*     */   private BluxBioSecure.Result mAuthResult;
/*     */   private PairTimeout mPairTimeout;
/*     */ 
/*     */   BluxDevice(BluxPeripheral peripheral)
/*     */   {
/*  73 */     this.mPeripheral = peripheral;
/*  74 */     this.mPeripheral.delegate = new PeripheralDelegate(null);
/*     */ 
/*  76 */     this.mService = new BluxServiceVirtualDevice();
/*  77 */     this.mService.delegate = new ServiceDelegate(null);
/*  78 */     this.mPeripheral.attachService(this.mService);
/*     */ 
/*  80 */     this.mVDDevice0 = new BluxVDDevice0(this.mService);
/*  81 */     this.mVDDevice0.delegate = new VDDevice0Delegate(null);
/*     */ 
/*  83 */     this.mBioSecure = new BluxBioSecure();
/*  84 */     this.mBioSecure.delegate = new BiosecDelegate(null);
/*     */ 
/*  86 */     this.mAccountManager = new BluxAccountManager();
/*  87 */     this.mAccountManager.initDelegate = new AccountManagerInitDelegate(null);
/*     */ 
/*  89 */     this.mState = State.DISCONNECTED;
/*     */   }
/*     */ 
/*     */   protected void terminate() {
/*  93 */     this.delegate = null;
/*  94 */     if (this.mPeripheral != null) {
/*  95 */       this.mPeripheral.terminate();
/*  96 */       this.mPeripheral = null;
/*     */     }
/*  98 */     if (this.mService != null) {
/*  99 */       this.mService.terminate();
/* 100 */       this.mService = null;
/*     */     }
/* 102 */     if (this.mVDDevice0 != null) {
/* 103 */       this.mVDDevice0.terminate();
/* 104 */       this.mVDDevice0 = null;
/*     */     }
/* 106 */     if (this.mBioSecure != null) {
/* 107 */       this.mBioSecure.terminate();
/* 108 */       this.mBioSecure = null;
/*     */     }
/* 110 */     if (this.mAccountManager != null) {
/* 111 */       this.mAccountManager.terminate();
/* 112 */       this.mAccountManager = null;
/*     */     }
/*     */ 
/* 115 */     super.terminate();
/*     */   }
/*     */ 
/*     */   boolean connected() {
/* 119 */     return this.mState == State.CONNECTED;
/*     */   }
/*     */ 
/*     */   BluxAccountManager getAccountManager() {
/* 123 */     return this.mAccountManager;
/*     */   }
/*     */ 
/*     */   void connect() {
/* 127 */     Log.w("BLUX", "[BD:conn:" + this.mState + "]");
/*     */ 
/* 129 */     if ((this.mState == State.DISCONNECTED) && (this.mPeripheral != null) && 
/* 130 */       (!this.mPeripheral
/* 130 */       .connected()) && (this.mPeripheral.connect())) {
/* 131 */       this.mAuthResult = BluxBioSecure.Result.SUCCESS;
/* 132 */       this.mAuthType = AuthType.CONNECT;
/* 133 */       this.mState = State.CONNECTING;
/*     */     }
/*     */   }
/*     */ 
/*     */   void cancelConnect() {
/* 138 */     Log.w("BLUX", "[BD:clconn:" + this.mState + "]");
/*     */ 
/* 140 */     if ((this.mState != State.DISCONNECTED) && (this.mPeripheral != null) && 
/* 141 */       (this.mAuthType == AuthType.CONNECT))
/* 142 */       this.mPeripheral.cancelConnect();
/*     */   }
/*     */ 
/*     */   void passPair(int pass)
/*     */   {
/* 148 */     Log.w("BLUX", "[BD:pp:" + this.mState + "]");
/*     */ 
/* 150 */     if ((this.mState == State.DISCONNECTED) && (this.mPeripheral != null) && 
/* 151 */       (!this.mPeripheral
/* 151 */       .connected()) && (this.mPeripheral.connect())) {
/* 152 */       this.mAuthResult = BluxBioSecure.Result.SUCCESS;
/* 153 */       this.mAuthType = AuthType.PASS_PAIR;
/* 154 */       this.mPasskey = pass;
/* 155 */       startPairTimer();
/* 156 */       this.mState = State.CONNECTING;
/*     */     }
/*     */   }
/*     */ 
/*     */   void cancelPair() {
/* 161 */     Log.w("BLUX", "[BD:cp:" + this.mState + "]");
/*     */ 
/* 163 */     if ((this.mState != State.DISCONNECTED) && (this.mState != State.CONNECTED) && (this.mPeripheral != null) && (
/* 164 */       (this.mAuthType == AuthType.PASS_PAIR) || (this.mAuthType == AuthType.KEY_PAIR))) {
/* 165 */       this.mAuthType = AuthType.NONE;
/* 166 */       this.mPeripheral.cancelConnect();
/* 167 */       stopPairTimer();
/*     */ 
/* 169 */       if (this.delegate != null)
/* 170 */         delayAction(new BluxObject.DelayedAction()
/*     */         {
/*     */           protected void act() {
/* 173 */             BluxDevice.this.delegate.devicePairResult(BluxDevice.this, BluxDevice.PairResult.ERROR_UNKNOWN, null);
/*     */           }
/*     */         }
/*     */         , 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setKey(String key)
/*     */   {
/* 182 */     this.mKey = key;
/*     */   }
/*     */ 
/*     */   BluxVirtualDevice getVirtualDevice(UUID uuidType) {
/* 186 */     if ((this.mState == State.CONNECTED) && (this.mPeripheral != null) && (this.mVDDevice0 != null)) {
/* 187 */       return this.mVDDevice0.getDevice(uuidType);
/*     */     }
/* 189 */     return null;
/*     */   }
/*     */ 
/*     */   private void startPairTimer()
/*     */   {
/* 195 */     stopPairTimer();
/* 196 */     this.mPairTimeout = new PairTimeout(null);
/* 197 */     delayAction(this.mPairTimeout, 30000);
/*     */   }
/*     */ 
/*     */   private void stopPairTimer() {
/* 201 */     if (this.mPairTimeout != null)
/* 202 */       this.mPairTimeout.cancel();
/*     */   }
/*     */ 
/*     */   private class PeripheralDelegate extends BluxPeripheral.Delegate
/*     */   {
/*     */     private PeripheralDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     void peripheralConnected(BluxPeripheral peripheral)
/*     */     {
/* 313 */       BluxDevice.this.mBioSecure.reset();
/*     */     }
/*     */ 
/*     */     protected void peripheralDisconnected(BluxPeripheral peripheral, boolean closed) {
/* 317 */       Log.w("BLUX", "[BD:pdis:" + BluxDevice.this.mAuthResult + " " + closed + "]");
/*     */ 
/* 319 */       BluxDevice.DisconnectReason dr = BluxDevice.DisconnectReason.UNKNOWN;
/* 320 */       if (closed) {
/* 321 */         dr = BluxDevice.DisconnectReason.CLOSED;
/*     */       }
/* 324 */       else if ((BluxDevice.this.mState == BluxDevice.State.AUTHORIZING) && (BluxDevice.this.mAuthResult != BluxBioSecure.Result.SUCCESS)) {
/* 325 */         if (BluxDevice.this.mAuthResult == BluxBioSecure.Result.ERROR_USER)
/* 326 */           dr = BluxDevice.DisconnectReason.PERMISSION;
/* 327 */         else if (BluxDevice.this.mAuthResult == BluxBioSecure.Result.ERROR_KEY)
/* 328 */           dr = BluxDevice.DisconnectReason.KEY;
/*     */       }
/*     */       else {
/* 331 */         dr = BluxDevice.DisconnectReason.LINKLOST;
/*     */       }
/*     */ 
/* 335 */       if (dr == BluxDevice.DisconnectReason.LINKLOST) {
/* 336 */         peripheral.connect();
/*     */       }
/*     */ 
/* 339 */       BluxDevice.State lastState = BluxDevice.this.mState;
/* 340 */       BluxDevice.access$602(BluxDevice.this, BluxDevice.State.DISCONNECTED);
/* 341 */       if ((BluxDevice.this.delegate != null) && (
/* 342 */         (dr != BluxDevice.DisconnectReason.LINKLOST) || (lastState == BluxDevice.State.CONNECTED)))
/* 343 */         BluxDevice.this.delegate.deviceDisconnected(BluxDevice.this, dr);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class VDDevice0Delegate extends BluxVDDevice0.Delegate
/*     */   {
/*     */     private VDDevice0Delegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void device0Started(boolean success)
/*     */     {
/* 298 */       Log.w("BLUX", "[BD:dv0:" + success + "]");
/*     */ 
/* 300 */       if ((success) && (BluxDevice.this.mService != null) && (BluxDevice.this.mVDDevice0 != null)) {
/* 301 */         BluxDevice.access$602(BluxDevice.this, BluxDevice.State.CONNECTED);
/*     */ 
/* 303 */         if (BluxDevice.this.delegate != null)
/* 304 */           BluxDevice.this.delegate.deviceConnected(BluxDevice.this);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class AccountManagerInitDelegate extends BluxAccountManager.InitDelegate
/*     */   {
/*     */     private AccountManagerInitDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void started(boolean success)
/*     */     {
/* 287 */       Log.w("BLUX", "[BD:am:" + success + "]");
/*     */ 
/* 289 */       BluxDevice.access$602(BluxDevice.this, BluxDevice.State.VDINITIALIZING);
/* 290 */       BluxDevice.this.mVDDevice0.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class BiosecDelegate extends BluxBioSecure.Delegate
/*     */   {
/*     */     private BiosecDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void pairResult(BluxBioSecure.Result result, BluxBioSecure.Ticket stub, BluxBioSecure.Ticket ticket)
/*     */     {
/* 244 */       Log.w("BLUX", "[BD:pr:" + result + "]");
/*     */ 
/* 246 */       BluxDevice.access$1302(BluxDevice.this, result);
/* 247 */       if (ticket != null) {
/* 248 */         BluxDevice.access$1202(BluxDevice.this, ticket.toString());
/* 249 */         BluxDevice.access$802(BluxDevice.this, BluxDevice.AuthType.CONNECT);
/*     */       }
/* 251 */       BluxDevice.this.stopPairTimer();
/*     */ 
/* 253 */       if (result == BluxBioSecure.Result.SUCCESS) {
/* 254 */         BluxDevice.access$602(BluxDevice.this, BluxDevice.State.AMINITIALIZING);
/* 255 */         BluxDevice.this.mAccountManager.start(BluxDevice.this.mVDDevice0.getAccountManageChannel());
/*     */       }
/*     */ 
/* 258 */       if (BluxDevice.this.delegate != null)
/*     */       {
/*     */         BluxDevice.PairResult pr;
/*     */         BluxDevice.PairResult pr;
/* 260 */         if (result == BluxBioSecure.Result.SUCCESS) {
/* 261 */           pr = BluxDevice.PairResult.SUCCESS;
/*     */         }
/*     */         else
/*     */         {
/*     */           BluxDevice.PairResult pr;
/* 262 */           if (result == BluxBioSecure.Result.ERROR_USER) {
/* 263 */             pr = BluxDevice.PairResult.ERROR_PERMISSION;
/*     */           }
/*     */           else
/*     */           {
/*     */             BluxDevice.PairResult pr;
/* 264 */             if (result == BluxBioSecure.Result.ERROR_KEY)
/* 265 */               pr = BluxDevice.PairResult.ERROR_KEY;
/*     */             else
/* 267 */               pr = BluxDevice.PairResult.ERROR_UNKNOWN; 
/*     */           }
/*     */         }
/* 268 */         BluxDevice.this.delegate.devicePairResult(BluxDevice.this, pr, ticket == null ? null : ticket.toString());
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void connectResult(BluxBioSecure.Result result) {
/* 273 */       Log.w("BLUX", "[BD:connr:" + result + "]");
/*     */ 
/* 275 */       BluxDevice.access$1302(BluxDevice.this, result);
/* 276 */       if (result == BluxBioSecure.Result.SUCCESS) {
/* 277 */         BluxDevice.access$602(BluxDevice.this, BluxDevice.State.AMINITIALIZING);
/* 278 */         BluxDevice.this.mAccountManager.start(BluxDevice.this.mVDDevice0.getAccountManageChannel());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ServiceDelegate extends BluxService.Delegate
/*     */   {
/*     */     private ServiceDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void serviceStarted(BluxService service, boolean success)
/*     */     {
/* 222 */       Log.w("BLUX", "[BD:clconn:" + BluxDevice.this.mState + " " + BluxDevice.this.mAuthType + "]");
/*     */ 
/* 224 */       if (BluxDevice.this.mAuthType == BluxDevice.AuthType.PASS_PAIR) {
/* 225 */         BluxDevice.this.mBioSecure.startPassPair(0, BluxDevice.this.mPasskey, BluxDevice.this.mVDDevice0.getAuthorizeChannel());
/*     */       }
/*     */       else {
/* 228 */         BluxBioSecure.Ticket ticket = BluxBioSecure.Ticket.fromString(BluxDevice.this.mKey);
/* 229 */         if (BluxDevice.this.mAuthType == BluxDevice.AuthType.CONNECT)
/* 230 */           BluxDevice.this.mBioSecure.startConnect(ticket, BluxDevice.this.mVDDevice0.getAuthorizeChannel());
/* 231 */         else if (BluxDevice.this.mAuthType == BluxDevice.AuthType.KEY_PAIR)
/* 232 */           BluxDevice.this.mBioSecure.startKeyPair(ticket, BluxDevice.this.mVDDevice0.getAuthorizeChannel());
/*     */         else
/* 234 */           Log.e("BLUX", "unknown auth type!!");
/*     */       }
/* 236 */       BluxDevice.access$602(BluxDevice.this, BluxDevice.State.AUTHORIZING);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PairTimeout extends BluxObject.DelayedAction
/*     */   {
/*     */     private PairTimeout()
/*     */     {
/* 207 */       super();
/*     */     }
/*     */     protected void act() {
/* 210 */       Log.w("BLUX", "[BD:pto:" + BluxDevice.this.mState + "]");
/*     */ 
/* 212 */       BluxDevice.access$702(BluxDevice.this, null);
/* 213 */       BluxDevice.this.cancelPair();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum State
/*     */   {
/*  45 */     DISCONNECTED, 
/*  46 */     CONNECTING, 
/*  47 */     AUTHORIZING, 
/*  48 */     AMINITIALIZING, 
/*  49 */     VDINITIALIZING, 
/*  50 */     CONNECTED;
/*     */   }
/*     */ 
/*     */   static enum AuthType
/*     */   {
/*  38 */     NONE, 
/*  39 */     KEY_PAIR, 
/*  40 */     PASS_PAIR, 
/*  41 */     CONNECT;
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void deviceConnected(BluxDevice device)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void deviceDisconnected(BluxDevice device, BluxDevice.DisconnectReason reason)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void devicePairResult(BluxDevice device, BluxDevice.PairResult result, String key)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static enum PairResult
/*     */   {
/*  21 */     SUCCESS, 
/*  22 */     ERROR_UNKNOWN, 
/*  23 */     ERROR_PERMISSION, 
/*  24 */     ERROR_KEY;
/*     */   }
/*     */ 
/*     */   static enum DisconnectReason
/*     */   {
/*  13 */     UNKNOWN, 
/*  14 */     LINKLOST, 
/*  15 */     CLOSED, 
/*  16 */     PERMISSION, 
/*  17 */     KEY;
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxDevice
 * JD-Core Version:    0.6.0
 */