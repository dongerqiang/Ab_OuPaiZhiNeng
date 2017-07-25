/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.bluetooth.BluetoothAdapter;
/*     */ import android.bluetooth.BluetoothAdapter.LeScanCallback;
/*     */ import android.bluetooth.BluetoothDevice;
/*     */ import android.bluetooth.BluetoothManager;
/*     */ import android.content.Context;
/*     */ import java.util.HashMap;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxCentral extends BluxObject
/*     */ {
/*     */   Delegate delegate;
/*     */   static final int CMD_STOP_SCAN = 1;
/*     */   static final int CMD_START_SCAN = 2;
/*     */   static final int CMD_LIST_SCANNED_DEVICE = 3;
/*     */   static final int CMD_GET_DEVICE = 4;
/*     */   static final int RSP_DEVICE = 2;
/*     */   static final int RSP_LIST_SCANNED_DEVICE = 3;
/*     */   static final int RSP_GET_DEVICE = 4;
/*     */   private Context mContext;
/*     */   private BluetoothAdapter mBluetoothAdapter;
/*     */   private BluetoothAdapter.LeScanCallback mScanCallback;
/*     */   private HashMap<String, BluetoothDevice> mScanDevicePool;
/*     */   private UUID[] mScanningServiceUuids;
/*     */ 
/*     */   BluxCentral(Context context)
/*     */   {
/*  23 */     this.mScanDevicePool = new HashMap();
/*  24 */     this.mContext = context;
/*  25 */     BluetoothManager manager = (BluetoothManager)this.mContext.getSystemService("bluetooth");
/*  26 */     if (manager != null)
/*  27 */       this.mBluetoothAdapter = manager.getAdapter();
/*     */   }
/*     */ 
/*     */   protected void terminate() {
/*  31 */     if (this.mScanDevicePool != null) {
/*  32 */       stopScan();
/*  33 */       this.mBluetoothAdapter = null;
/*  34 */       this.mScanCallback = null;
/*  35 */       this.mScanDevicePool = null;
/*  36 */       this.mScanningServiceUuids = null;
/*  37 */       this.mContext = null;
/*  38 */       this.delegate = null;
/*     */     }
/*  40 */     super.terminate();
/*     */   }
/*     */ 
/*     */   boolean isScanning() {
/*  44 */     return this.mScanCallback != null;
/*     */   }
/*     */ 
/*     */   boolean startScan(UUID[] serviceUuids) {
/*  48 */     boolean ret = false;
/*  49 */     if ((this.mBluetoothAdapter != null) && (this.mScanCallback == null)) {
/*  50 */       synchronized (this.mScanDevicePool) {
/*  51 */         this.mScanningServiceUuids = serviceUuids;
/*     */       }
/*  53 */       this.mScanCallback = new ScanCallback(null);
/*  54 */       ret = this.mBluetoothAdapter.startLeScan(this.mScanCallback);
/*  55 */       if (!ret)
/*  56 */         this.mScanCallback = null;
/*     */     }
/*  58 */     return ret;
/*     */   }
/*     */ 
/*     */   void stopScan() {
/*  62 */     if ((this.mBluetoothAdapter != null) && (this.mScanCallback != null)) {
/*  63 */       this.mBluetoothAdapter.stopLeScan(this.mScanCallback);
/*  64 */       synchronized (this.mScanDevicePool) {
/*  65 */         this.mScanDevicePool.clear();
/*  66 */         this.mScanningServiceUuids = null;
/*  67 */         this.mScanCallback = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   BluxPeripheral getPeripheral(String address) {
/*  73 */     BluxPeripheral peripheral = null;
/*  74 */     if (this.mBluetoothAdapter != null) {
/*  75 */       BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
/*  76 */       if (device != null)
/*  77 */         peripheral = new BluxPeripheral(this.mContext, device);
/*     */     }
/*  79 */     return peripheral;
/*     */   }
/*     */ 
/*     */   private class ScanCallback
/*     */     implements BluetoothAdapter.LeScanCallback
/*     */   {
/*     */     private ScanCallback()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
/*     */     {
/* 120 */       if (BluxCentral.this.mScanDevicePool == null) {
/* 121 */         return;
/*     */       }
/* 123 */       synchronized (BluxCentral.this.mScanDevicePool) {
/* 124 */         if ((!BluxCentral.this.mScanDevicePool.containsKey(device.getAddress())) && (compareScanningServices(scanRecord))) {
/* 125 */           BluxCentral.this.mScanDevicePool.put(device.getAddress(), device);
/* 126 */           BluxCentral.this.delayAction(new BluxCentral.ScanReportDelayed(BluxCentral.this, device, rssi), 0);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private boolean compareScanningServices(byte[] ad)
/*     */     {
/*     */       UUID[] servericeUuids;
/* 134 */       synchronized (BluxCentral.this.mScanDevicePool) {
/* 135 */         servericeUuids = BluxCentral.this.mScanningServiceUuids;
/*     */       }
/*     */       UUID[] servericeUuids;
/* 138 */       if ((servericeUuids == null) || (servericeUuids.length == 0)) {
/* 139 */         return true;
/*     */       }
/*     */ 
/* 142 */       for (int offset = 0; (ad[offset] != 0) && (offset < ad.length); ) {
/* 143 */         if ((ad[offset] == 17) && (ad[(offset + 1)] == 6)) {
/* 144 */           String s = String.format("%02X%02X%02X%02X-%02X%02X-%02X%02X-%02X%02X-%02X%02X%02X%02X%02X%02X", new Object[] { 
/* 145 */             Byte.valueOf(ad[(offset + 17)]), 
/* 145 */             Byte.valueOf(ad[(offset + 16)]), Byte.valueOf(ad[(offset + 15)]), Byte.valueOf(ad[(offset + 14)]), Byte.valueOf(ad[(offset + 13)]), Byte.valueOf(ad[(offset + 12)]), Byte.valueOf(ad[(offset + 11)]), Byte.valueOf(ad[(offset + 10)]), Byte.valueOf(ad[(offset + 9)]), 
/* 146 */             Byte.valueOf(ad[(offset + 8)]), 
/* 146 */             Byte.valueOf(ad[(offset + 7)]), Byte.valueOf(ad[(offset + 6)]), Byte.valueOf(ad[(offset + 5)]), Byte.valueOf(ad[(offset + 4)]), Byte.valueOf(ad[(offset + 3)]), Byte.valueOf(ad[(offset + 2)]) });
/* 147 */           UUID uuid = UUID.fromString(s);
/* 148 */           for (int i = 0; i < servericeUuids.length; i++) {
/* 149 */             if (servericeUuids[i].compareTo(uuid) == 0) {
/* 150 */               return true;
/*     */             }
/*     */           }
/*     */         }
/* 154 */         offset += ad[offset] + 1;
/*     */       }
/* 156 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ScanReportDelayed extends BluxObject.DelayedAction
/*     */   {
/*     */     private int mRssi;
/*     */     private BluetoothDevice mDevice;
/*     */ 
/*     */     ScanReportDelayed(BluetoothDevice device, int rssi)
/*     */     {
/* 105 */       super();
/* 106 */       this.mDevice = device;
/* 107 */       this.mRssi = rssi;
/*     */     }
/*     */     protected void act() {
/* 110 */       BluxPeripheral peripheral = new BluxPeripheral(BluxCentral.this.mContext, this.mDevice);
/* 111 */       if (BluxCentral.this.delegate != null)
/* 112 */         BluxCentral.this.delegate.reportPeripheral(peripheral, this.mRssi);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void reportPeripheral(BluxPeripheral peripheral, int rssi)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void stateChangeTo(int state)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxCentral
 * JD-Core Version:    0.6.0
 */