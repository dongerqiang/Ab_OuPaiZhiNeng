/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.bluetooth.BluetoothGatt;
/*     */ import android.bluetooth.BluetoothGattCharacteristic;
/*     */ import android.bluetooth.BluetoothGattService;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxServiceDeviceInfo extends BluxService
/*     */ {
/*  12 */   private static final UUID UUID_MANUFACTURE_NAME = UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB")
/*  12 */     ;
/*     */ 
/*  14 */   private static final UUID UUID_MODEL_NUMBER = UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB")
/*  14 */     ;
/*     */ 
/*  16 */   private static final UUID UUID_SERIAL_NUMBER = UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB")
/*  16 */     ;
/*     */ 
/*  18 */   private static final UUID UUID_HARDWARE_REVISION = UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB")
/*  18 */     ;
/*     */ 
/*  20 */   private static final UUID UUID_FIRMWARE_REVISION = UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB")
/*  20 */     ;
/*     */ 
/*  22 */   private static final UUID UUID_SOFTWARE_REVISION = UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB")
/*  22 */     ;
/*     */ 
/*  24 */   private static final UUID UUID_SYSTEM_ID = UUID.fromString("00002A23-0000-1000-8000-00805F9B34FB")
/*  24 */     ;
/*     */ 
/*  26 */   private static final UUID UUID_IEEE_REGULATORY_CERTIFICATION = UUID.fromString("00002A2A-0000-1000-8000-00805F9B34FB")
/*  26 */     ;
/*     */ 
/*  28 */   private static final UUID UUID_PNP_ID = UUID.fromString("00002A50-0000-1000-8000-00805F9B34FB")
/*  28 */     ;
/*     */   String mManufactureName;
/*     */   String mModelNumber;
/*     */   String mSerialNumber;
/*     */   String mHardwareRevision;
/*     */   String mFirmwareRevision;
/*     */   String mSoftwareRevision;
/*     */   String mSystemId;
/*     */   String mIeeeRegulatoryCertification;
/*     */   String mPnpId;
/*     */   private int charsRead;
/*     */   private int charCount;
/*     */ 
/*     */   protected void onConnected(BluetoothGatt btGatt, BluetoothGattService btService)
/*     */   {
/*  96 */     super.onConnected(btGatt, btService);
/*     */ 
/*  98 */     this.charCount = 0;
/*  99 */     this.charsRead = 0;
/*     */ 
/* 102 */     BluetoothGattCharacteristic ch = this.mBtService.getCharacteristic(UUID_MANUFACTURE_NAME);
/* 103 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 104 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 105 */       this.charCount += 1;
/*     */     }
/* 107 */     ch = this.mBtService.getCharacteristic(UUID_MODEL_NUMBER);
/* 108 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 109 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 110 */       this.charCount += 1;
/*     */     }
/* 112 */     ch = this.mBtService.getCharacteristic(UUID_SERIAL_NUMBER);
/* 113 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 114 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 115 */       this.charCount += 1;
/*     */     }
/* 117 */     ch = this.mBtService.getCharacteristic(UUID_HARDWARE_REVISION);
/* 118 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 119 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 120 */       this.charCount += 1;
/*     */     }
/* 122 */     ch = this.mBtService.getCharacteristic(UUID_FIRMWARE_REVISION);
/* 123 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 124 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 125 */       this.charCount += 1;
/*     */     }
/* 127 */     ch = this.mBtService.getCharacteristic(UUID_SOFTWARE_REVISION);
/* 128 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 129 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 130 */       this.charCount += 1;
/*     */     }
/* 132 */     ch = this.mBtService.getCharacteristic(UUID_SYSTEM_ID);
/* 133 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 134 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 135 */       this.charCount += 1;
/*     */     }
/* 137 */     ch = this.mBtService.getCharacteristic(UUID_IEEE_REGULATORY_CERTIFICATION);
/* 138 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 139 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 140 */       this.charCount += 1;
/*     */     }
/* 142 */     ch = this.mBtService.getCharacteristic(UUID_PNP_ID);
/* 143 */     if ((ch != null) && (this.mPeripheral != null)) {
/* 144 */       this.mPeripheral.putTransfer(new ReadTransfer(ch));
/* 145 */       this.charCount += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onDisconnected()
/*     */   {
/* 151 */     super.onDisconnected();
/*     */   }
/*     */ 
/*     */   BluxServiceDeviceInfo()
/*     */   {
/* 156 */     this.mBtServiceUUID = UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB");
/*     */   }
/*     */ 
/*     */   private class ReadTransfer extends BluxPeripheral.ReadCharTransfer
/*     */   {
/*     */     ReadTransfer(BluetoothGattCharacteristic ch)
/*     */     {
/*  32 */       super();
/*     */     }
/*     */ 
/*     */     protected void finished(boolean success)
/*     */     {
/*  37 */       if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_MANUFACTURE_NAME) == 0) {
/*  38 */         BluxServiceDeviceInfo.this.mManufactureName = this.mChar.getStringValue(0);
/*  39 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*  41 */       else if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_MODEL_NUMBER) == 0) {
/*  42 */         BluxServiceDeviceInfo.this.mModelNumber = this.mChar.getStringValue(0);
/*  43 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*  45 */       else if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_SERIAL_NUMBER) == 0) {
/*  46 */         BluxServiceDeviceInfo.this.mSerialNumber = this.mChar.getStringValue(0);
/*  47 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*  49 */       else if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_HARDWARE_REVISION) == 0) {
/*  50 */         BluxServiceDeviceInfo.this.mHardwareRevision = this.mChar.getStringValue(0);
/*  51 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*  53 */       else if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_FIRMWARE_REVISION) == 0) {
/*  54 */         BluxServiceDeviceInfo.this.mFirmwareRevision = this.mChar.getStringValue(0);
/*  55 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*  57 */       else if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_SOFTWARE_REVISION) == 0) {
/*  58 */         BluxServiceDeviceInfo.this.mSoftwareRevision = this.mChar.getStringValue(0);
/*  59 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*  61 */       else if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_SYSTEM_ID) == 0) {
/*  62 */         BluxServiceDeviceInfo.this.mSystemId = this.mChar.getStringValue(0);
/*  63 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*  65 */       else if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_IEEE_REGULATORY_CERTIFICATION) == 0) {
/*  66 */         BluxServiceDeviceInfo.this.mIeeeRegulatoryCertification = this.mChar.getStringValue(0);
/*  67 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*  69 */       else if (this.mChar.getUuid().compareTo(BluxServiceDeviceInfo.UUID_PNP_ID) == 0) {
/*  70 */         BluxServiceDeviceInfo.this.mPnpId = this.mChar.getStringValue(0);
/*  71 */         BluxServiceDeviceInfo.access$108(BluxServiceDeviceInfo.this);
/*     */       }
/*     */ 
/*  74 */       if ((BluxServiceDeviceInfo.this.charsRead == BluxServiceDeviceInfo.this.charCount) && (BluxServiceDeviceInfo.this.delegate != null))
/*  75 */         BluxServiceDeviceInfo.this.delegate.serviceStarted(BluxServiceDeviceInfo.this, true);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxServiceDeviceInfo
 * JD-Core Version:    0.6.0
 */