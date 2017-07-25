/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.bluetooth.BluetoothDevice;
/*     */ import android.bluetooth.BluetoothGatt;
/*     */ import android.bluetooth.BluetoothGattCallback;
/*     */ import android.bluetooth.BluetoothGattCharacteristic;
/*     */ import android.bluetooth.BluetoothGattDescriptor;
/*     */ import android.bluetooth.BluetoothGattService;
/*     */ import android.content.Context;
/*     */ import android.util.Log;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxPeripheral extends BluxObject
/*     */ {
/*     */   Delegate delegate;
/*     */   private State mConnectState;
/*     */   private Context mContext;
/*     */   private BluetoothDevice mBtDevice;
/*     */   private BluetoothGatt mBtGatt;
/*     */   private List<BluetoothGattService> mBtServices;
/*     */   private HashMap<UUID, BluxService> mServices;
/*     */   private List<Transfer> mTransfers;
/*     */ 
/*     */   BluxPeripheral(Context context, BluetoothDevice device)
/*     */   {
/* 189 */     this.mContext = context;
/* 190 */     this.mBtDevice = device;
/* 191 */     this.mTransfers = new ArrayList();
/* 192 */     this.mServices = new HashMap();
/* 193 */     this.mConnectState = State.DISCONNECTED;
/*     */   }
/*     */ 
/*     */   protected void terminate() {
/* 197 */     if (this.mTransfers != null) {
/* 198 */       cancelTransfers();
/* 199 */       this.mTransfers = null;
/*     */     }
/* 201 */     if (this.mBtGatt != null) {
/* 202 */       this.mBtGatt.close();
/* 203 */       this.mBtGatt = null;
/*     */     }
/* 205 */     this.delegate = null;
/* 206 */     this.mConnectState = State.DISCONNECTED;
/* 207 */     this.mContext = null;
/* 208 */     this.mBtDevice = null;
/* 209 */     this.mBtServices = null;
/* 210 */     this.mServices = null;
/* 211 */     super.terminate();
/*     */   }
/*     */ 
/*     */   static String bluetoothDeviceIdentifier(BluetoothDevice device) {
/* 215 */     return device.getAddress();
/*     */   }
/*     */ 
/*     */   boolean connect()
/*     */   {
/* 220 */     if ((this.mConnectState == State.DISCONNECTED) && 
/* 221 */       (this.mServices != null) && (this.mBtGatt == null)) {
/* 222 */       Log.w("BLUX", "conn>");
/*     */ 
/* 224 */       GattCallback cb = new GattCallback(null);
/* 225 */       this.mBtGatt = this.mBtDevice.connectGatt(this.mContext, false, cb);
/* 226 */       this.mConnectState = State.CONNECTING;
/* 227 */       return true;
/*     */     }
/*     */ 
/* 230 */     return false;
/*     */   }
/*     */ 
/*     */   void cancelConnect() {
/* 234 */     if ((this.mConnectState == State.CONNECTED) || (this.mConnectState == State.CONNECTING)) {
/* 235 */       Log.w("BLUX", "cnclconn>");
/*     */ 
/* 237 */       if (this.mBtGatt != null) {
/* 238 */         this.mBtGatt.disconnect();
/*     */       }
/* 240 */       if (this.mConnectState == State.CONNECTING) {
/* 241 */         delayAction(new DisconnectedDelayed(null), 0);
/*     */       }
/* 243 */       this.mConnectState = State.DISCONNECTING;
/*     */     }
/*     */   }
/*     */ 
/*     */   void attachService(BluxService service)
/*     */   {
/* 249 */     if (this.mServices == null) {
/* 250 */       return;
/*     */     }
/* 252 */     if (this.mServices.get(service.btServiceUUID()) == null) {
/* 253 */       this.mServices.put(service.btServiceUUID(), service);
/* 254 */       service.onAttached(this);
/* 255 */       if (connected())
/* 256 */         notifyServiceConnected(service);
/*     */     }
/*     */   }
/*     */ 
/*     */   void detachService(BluxService service)
/*     */   {
/* 262 */     if (this.mServices == null) {
/* 263 */       return;
/*     */     }
/* 265 */     if (this.mServices.get(service.btServiceUUID()) != null) {
/* 266 */       service.onDetached();
/* 267 */       this.mServices.remove(service.btServiceUUID());
/*     */     }
/*     */   }
/*     */ 
/*     */   void detachAllServices() {
/* 272 */     if (this.mServices == null) {
/* 273 */       return;
/*     */     }
/* 275 */     Collection services = this.mServices.values();
/* 276 */     for (BluxService service : services) {
/* 277 */       service.onDetached();
/*     */     }
/* 279 */     this.mServices.clear();
/*     */   }
/*     */ 
/*     */   boolean connected()
/*     */   {
/* 284 */     return (this.mConnectState == State.CONNECTED) || (this.mConnectState == State.DISCONNECTING);
/*     */   }
/*     */ 
/*     */   String identifier() {
/* 288 */     if (this.mBtDevice != null)
/* 289 */       return bluetoothDeviceIdentifier(this.mBtDevice);
/* 290 */     return null;
/*     */   }
/*     */ 
/*     */   String name() {
/* 294 */     if (this.mBtDevice != null)
/* 295 */       return this.mBtDevice.getName();
/* 296 */     return null;
/*     */   }
/*     */ 
/*     */   void putTransfer(Transfer transfer)
/*     */   {
/* 302 */     if (this.mTransfers != null) {
/* 303 */       if (this.mTransfers.size() == 0)
/* 304 */         transfer.start(this.mBtGatt);
/* 305 */       this.mTransfers.add(transfer);
/*     */     }
/*     */   }
/*     */ 
/*     */   void cancelTransfers() {
/* 310 */     if ((this.mTransfers != null) && (this.mTransfers.size() != 0)) {
/* 311 */       for (Transfer transfer : this.mTransfers) {
/* 312 */         transfer.finished(false);
/*     */       }
/* 314 */       this.mTransfers.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notifyServiceConnected(BluxService service)
/*     */   {
/* 450 */     if (this.mBtServices != null)
/* 451 */       for (BluetoothGattService btService : this.mBtServices)
/* 452 */         if (btService.getUuid().compareTo(service.btServiceUUID()) == 0) {
/* 453 */           service.onConnected(this.mBtGatt, btService);
/* 454 */           break;
/*     */         }
/*     */   }
/*     */ 
/*     */   private class DisconnectedDelayed extends BluxObject.DelayedAction
/*     */   {
/*     */     private DisconnectedDelayed()
/*     */     {
/* 421 */       super();
/*     */     }
/*     */     protected void act() {
/* 424 */       if (BluxPeripheral.this.mServices == null) {
/* 425 */         return;
/*     */       }
/* 427 */       boolean closed = BluxPeripheral.this.mConnectState == BluxPeripheral.State.DISCONNECTING;
/*     */ 
/* 429 */       if (BluxPeripheral.this.mBtGatt != null) {
/* 430 */         BluxPeripheral.this.mBtGatt.close();
/* 431 */         BluxPeripheral.access$202(BluxPeripheral.this, null);
/*     */       }
/* 433 */       BluxPeripheral.access$502(BluxPeripheral.this, null);
/* 434 */       BluxPeripheral.access$602(BluxPeripheral.this, BluxPeripheral.State.DISCONNECTED);
/*     */ 
/* 436 */       BluxPeripheral.this.cancelTransfers();
/*     */ 
/* 438 */       Collection services = BluxPeripheral.this.mServices.values();
/* 439 */       for (BluxService service : services) {
/* 440 */         service.onDisconnected();
/*     */       }
/* 442 */       if (BluxPeripheral.this.delegate != null)
/* 443 */         BluxPeripheral.this.delegate.peripheralDisconnected(BluxPeripheral.this, closed);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ConnectedDelayed extends BluxObject.DelayedAction
/*     */   {
/*     */     private int mStatus;
/*     */ 
/*     */     ConnectedDelayed(int status)
/*     */     {
/* 394 */       super();
/* 395 */       this.mStatus = status;
/*     */     }
/*     */ 
/*     */     protected void act() {
/* 399 */       if (BluxPeripheral.this.mServices == null) {
/* 400 */         return;
/*     */       }
/* 402 */       if (this.mStatus != 0) {
/* 403 */         if (BluxPeripheral.this.mBtGatt != null) {
/* 404 */           BluxPeripheral.this.mBtGatt.close();
/* 405 */           BluxPeripheral.access$202(BluxPeripheral.this, null);
/*     */         }
/* 407 */         BluxPeripheral.this.delayAction(new BluxPeripheral.DisconnectedDelayed(BluxPeripheral.this, null), 0);
/* 408 */         return;
/*     */       }
/*     */ 
/* 411 */       Log.w("BLUX", "conndcvsvr>");
/* 412 */       BluxPeripheral.access$602(BluxPeripheral.this, BluxPeripheral.State.CONNECTED);
/* 413 */       BluxPeripheral.this.mBtGatt.discoverServices();
/*     */ 
/* 415 */       if (BluxPeripheral.this.delegate != null)
/* 416 */         BluxPeripheral.this.delegate.peripheralConnected(BluxPeripheral.this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ServiceDiscoveredDelayed extends BluxObject.DelayedAction
/*     */   {
/*     */     int mStatus;
/*     */ 
/*     */     ServiceDiscoveredDelayed(int status)
/*     */     {
/* 370 */       super();
/* 371 */       this.mStatus = status;
/*     */     }
/*     */ 
/*     */     protected void act() {
/* 375 */       if ((BluxPeripheral.this.mServices == null) || (BluxPeripheral.this.mBtGatt == null)) {
/* 376 */         return;
/*     */       }
/* 378 */       if (this.mStatus != 0) {
/* 379 */         return;
/*     */       }
/* 381 */       BluxPeripheral.access$502(BluxPeripheral.this, BluxPeripheral.this.mBtGatt.getServices());
/* 382 */       for (BluetoothGattService btService : BluxPeripheral.this.mBtServices) {
/* 383 */         BluxService service = (BluxService)BluxPeripheral.this.mServices.get(btService.getUuid());
/* 384 */         if (service != null)
/*     */         {
/* 386 */           service.onConnected(BluxPeripheral.this.mBtGatt, btService);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CharChangeDelayed extends BluxObject.DelayedAction
/*     */   {
/*     */     private BluetoothGattCharacteristic mChar;
/*     */ 
/*     */     CharChangeDelayed(BluetoothGattCharacteristic ch)
/*     */     {
/* 353 */       super();
/* 354 */       this.mChar = ch;
/*     */     }
/*     */ 
/*     */     protected void act() {
/* 358 */       if (BluxPeripheral.this.mServices == null) {
/* 359 */         return;
/*     */       }
/* 361 */       BluxService service = (BluxService)BluxPeripheral.this.mServices.get(this.mChar.getService().getUuid());
/* 362 */       if (service != null)
/* 363 */         service.onCharacteristicChanged(this.mChar);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CharOrDescReadWriteDelayed extends BluxObject.DelayedAction
/*     */   {
/*     */     private BluetoothGattDescriptor mDesc;
/*     */     private BluetoothGattCharacteristic mChar;
/*     */     boolean mSuccess;
/*     */ 
/*     */     CharOrDescReadWriteDelayed(BluetoothGattDescriptor desc, BluetoothGattCharacteristic ch, boolean success)
/*     */     {
/* 324 */       super();
/* 325 */       this.mDesc = desc;
/* 326 */       this.mChar = ch;
/* 327 */       this.mSuccess = success;
/*     */     }
/*     */ 
/*     */     protected void act() {
/* 331 */       if (BluxPeripheral.this.mBtGatt != null) if (((BluxPeripheral.this.mTransfers != null ? 1 : 0) & (BluxPeripheral.this.mTransfers.size() != 0 ? 1 : 0)) != 0) {
/* 332 */           BluxPeripheral.Transfer transfer = (BluxPeripheral.Transfer)BluxPeripheral.this.mTransfers.get(0);
/* 333 */           if ((transfer.mRetry != 0) && (!this.mSuccess)) {
/* 334 */             transfer.mRetry -= 1;
/* 335 */             transfer.start(BluxPeripheral.this.mBtGatt);
/*     */           }
/*     */           else {
/* 338 */             transfer.mDesc = this.mDesc;
/* 339 */             transfer.mChar = this.mChar;
/* 340 */             transfer.finished(this.mSuccess);
/*     */ 
/* 342 */             BluxPeripheral.this.mTransfers.remove(0);
/* 343 */             if (BluxPeripheral.this.mTransfers.size() != 0)
/* 344 */               ((BluxPeripheral.Transfer)BluxPeripheral.this.mTransfers.get(0)).start(BluxPeripheral.this.mBtGatt);
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum State
/*     */   {
/* 170 */     CONNECTING, 
/* 171 */     CONNECTED, 
/* 172 */     DISCONNECTING, 
/* 173 */     DISCONNECTED;
/*     */   }
/*     */ 
/*     */   static class WriteDescTransfer extends BluxPeripheral.Transfer
/*     */   {
/*     */     private byte[] mData;
/*     */ 
/*     */     WriteDescTransfer(BluetoothGattDescriptor desc, byte[] data)
/*     */     {
/* 157 */       this.mDesc = desc;
/* 158 */       this.mData = data;
/*     */     }
/*     */     protected void start(BluetoothGatt gatt) {
/* 161 */       if (gatt != null) {
/* 162 */         this.mDesc.setValue(this.mData);
/* 163 */         gatt.writeDescriptor(this.mDesc);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void finished(boolean success)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ReadDescTransfer extends BluxPeripheral.Transfer
/*     */   {
/*     */     ReadDescTransfer(BluetoothGattDescriptor desc)
/*     */     {
/* 145 */       this.mDesc = desc;
/*     */     }
/*     */     protected void start(BluetoothGatt gatt) {
/* 148 */       if (gatt != null)
/* 149 */         gatt.readDescriptor(this.mDesc);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class WriteCharTransfer extends BluxPeripheral.Transfer
/*     */   {
/*     */     private byte[] mData;
/*     */ 
/*     */     WriteCharTransfer(BluetoothGattCharacteristic ch, byte[] data)
/*     */     {
/* 132 */       this.mChar = ch;
/* 133 */       this.mData = data;
/*     */     }
/*     */     protected void start(BluetoothGatt gatt) {
/* 136 */       if (gatt != null) {
/* 137 */         this.mChar.setValue(this.mData);
/* 138 */         gatt.writeCharacteristic(this.mChar);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ReadCharTransfer extends BluxPeripheral.Transfer
/*     */   {
/*     */     ReadCharTransfer(BluetoothGattCharacteristic ch)
/*     */     {
/* 120 */       this.mChar = ch;
/*     */     }
/*     */     protected void start(BluetoothGatt gatt) {
/* 123 */       if (gatt != null)
/* 124 */         gatt.readCharacteristic(this.mChar);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Transfer
/*     */   {
/*     */     protected int mRetry;
/*     */     protected BluetoothGattCharacteristic mChar;
/*     */     protected BluetoothGattDescriptor mDesc;
/*     */ 
/*     */     void setRetry(int retry)
/*     */     {
/* 112 */       this.mRetry = retry;
/*     */     }
/*     */ 
/*     */     protected void start(BluetoothGatt gatt)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void finished(boolean success)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class GattCallback extends BluetoothGattCallback
/*     */   {
/*     */     private GattCallback()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
/*     */     {
/*  47 */       Log.w("BLUX", "<conn: " + newState + " : " + status);
/*     */ 
/*  49 */       if (newState == 2) {
/*  50 */         BluxPeripheral.this.delayAction(new BluxPeripheral.ConnectedDelayed(BluxPeripheral.this, status), 0);
/*     */       }
/*  52 */       else if (newState == 0)
/*  53 */         BluxPeripheral.this.delayAction(new BluxPeripheral.DisconnectedDelayed(BluxPeripheral.this, null), 0);
/*     */     }
/*     */ 
/*     */     public void onServicesDiscovered(BluetoothGatt gatt, int status)
/*     */     {
/*  58 */       Log.w("BLUX", "<serdis: " + status);
/*     */ 
/*  61 */       BluxPeripheral.this.delayAction(new BluxPeripheral.ServiceDiscoveredDelayed(BluxPeripheral.this, status), 0);
/*     */     }
/*     */ 
/*     */     public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
/*     */     {
/*  66 */       Log.w("BLUX", "<dscrd: " + status);
/*     */ 
/*  68 */       BluxPeripheral.this.delayAction(new BluxPeripheral.CharOrDescReadWriteDelayed(BluxPeripheral.this, descriptor, null, status == 0), 0);
/*     */     }
/*     */ 
/*     */     public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
/*     */     {
/*  73 */       Log.w("BLUX", "<dscwr: " + status);
/*     */ 
/*  75 */       BluxPeripheral.this.delayAction(new BluxPeripheral.CharOrDescReadWriteDelayed(BluxPeripheral.this, descriptor, null, status == 0), 0);
/*     */     }
/*     */ 
/*     */     public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
/*     */     {
/*  80 */       Log.w("BLUX", "<chrrd: " + status);
/*     */ 
/*  82 */       BluxPeripheral.this.delayAction(new BluxPeripheral.CharOrDescReadWriteDelayed(BluxPeripheral.this, null, characteristic, status == 0), 0);
/*     */     }
/*     */ 
/*     */     public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
/*     */     {
/*  87 */       Log.w("BLUX", "<chrwr: " + status);
/*     */ 
/*  89 */       BluxPeripheral.this.delayAction(new BluxPeripheral.CharOrDescReadWriteDelayed(BluxPeripheral.this, null, characteristic, status == 0), 0);
/*     */     }
/*     */ 
/*     */     public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
/*     */     {
/*  94 */       Log.w("BLUX", "<chrchg");
/*     */ 
/*  96 */       BluxPeripheral.this.delayAction(new BluxPeripheral.CharChangeDelayed(BluxPeripheral.this, characteristic), 0);
/*     */     }
/*     */ 
/*     */     public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     void peripheralConnected(BluxPeripheral peripheral)
/*     */     {
/*     */     }
/*     */ 
/*     */     void peripheralDisconnected(BluxPeripheral peripheral, boolean closed)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxPeripheral
 * JD-Core Version:    0.6.0
 */