/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxVDDevice0 extends BluxVirtualDevice
/*     */ {
/*     */   Delegate delegate;
/*     */   private List<BluxVirtualDevice> mVirtualDevices;
/*     */   private BluxVirtualDevice.PacketChannel mAuthChannel;
/*     */   private BluxVirtualDevice.PacketChannel mAccountManageChannel;
/*     */   private int mTempCounter;
/*     */   private static final short REG_DEVICE0_AUTH = 16;
/*     */   private static final short REG_DEVICE0_QUERY = 17;
/*     */   private static final short REG_DEVICE0_USER_MANAGEMENT = 33;
/*     */   private static final int DEVICE_GET_COUNT = 1;
/*     */   private static final int DEVICE_GET_DESC = 2;
/*     */ 
/*     */   BluxVDDevice0(BluxServiceVirtualDevice service)
/*     */   {
/*  27 */     super(service, null);
/*  28 */     this.mAccountManageChannel = new BluxVirtualDevice.PacketChannel(this, 33);
/*  29 */     this.mAuthChannel = new BluxVirtualDevice.PacketChannel(this, 16);
/*  30 */     service.vdDelegate = new VDServiceDelegate(null);
/*     */ 
/*  32 */     byte[] data = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*  35 */     this.mDescriptor = new BluxVirtualDevice.Descriptor(data);
/*     */   }
/*     */ 
/*     */   protected void terminate()
/*     */   {
/*  40 */     if (this.mVirtualDevices != null) {
/*  41 */       for (BluxVirtualDevice device : this.mVirtualDevices)
/*  42 */         device.terminate();
/*     */     }
/*  44 */     this.delegate = null;
/*  45 */     this.mVirtualDevices = null;
/*  46 */     this.mAuthChannel = null;
/*  47 */     this.mAccountManageChannel = null;
/*  48 */     super.terminate();
/*     */   }
/*     */ 
/*     */   BluxVirtualDevice.PacketChannel getAuthorizeChannel() {
/*  52 */     return this.mAuthChannel;
/*     */   }
/*     */ 
/*     */   BluxVirtualDevice.PacketChannel getAccountManageChannel() {
/*  56 */     return this.mAccountManageChannel;
/*     */   }
/*     */ 
/*     */   BluxVirtualDevice getDevice(int address) {
/*  60 */     for (BluxVirtualDevice dev : this.mVirtualDevices) {
/*  61 */       if (address == dev.address())
/*  62 */         return dev;
/*     */     }
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   BluxVirtualDevice getDevice(UUID uuidType) {
/*  68 */     for (BluxVirtualDevice dev : this.mVirtualDevices) {
/*  69 */       if (dev.typeUuid().compareTo(uuidType) == 0)
/*  70 */         return dev;
/*     */     }
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   BluxVirtualDevice[] getDevices() {
/*  76 */     BluxVirtualDevice[] vds = new BluxVirtualDevice[this.mVirtualDevices.size()];
/*  77 */     vds = (BluxVirtualDevice[])this.mVirtualDevices.toArray(vds);
/*  78 */     return vds;
/*     */   }
/*     */ 
/*     */   void start() {
/*  82 */     updateVirtualDevices();
/*     */   }
/*     */ 
/*     */   protected void didReadRegister(Object id, int register, boolean success, byte[] data)
/*     */   {
/*  89 */     switch (register) {
/*     */     case 17:
/*  91 */       byte cmd = ((byte[])(byte[])id)[0];
/*  92 */       if ((cmd == 1) && (success)) {
/*  93 */         this.mTempCounter = data[0];
/*  94 */         this.mTempCounter &= 255;
/*  95 */         for (int i = 0; i < this.mTempCounter; i++)
/*  96 */           readDeviceDescriptor(i);
/*     */       }
/*     */       else {
/*  99 */         if (cmd != 2) break;
/* 100 */         if ((success) && 
/* 101 */           (data.length == 17)) {
/* 102 */           BluxVirtualDevice.Descriptor desc = new BluxVirtualDevice.Descriptor(data);
/* 103 */           BluxVirtualDevice dev = createVirtualDevice(desc);
/* 104 */           this.mVirtualDevices.add(dev);
/*     */         }
/*     */ 
/* 107 */         if (this.mTempCounter != 0)
/* 108 */           this.mTempCounter -= 1;
/* 109 */         if ((this.mTempCounter != 0) || (this.delegate == null)) break;
/* 110 */         this.delegate.device0Started(true); } break;
/*     */     case 16:
/*     */     default:
/* 117 */       super.didReadRegister(id, register, success, data);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readDeviceCount()
/*     */   {
/* 157 */     byte[] buffer = { 1 };
/* 158 */     readRegister(17, buffer, buffer);
/*     */   }
/*     */ 
/*     */   private void readDeviceDescriptor(int index) {
/* 162 */     byte[] buffer = new byte[2];
/* 163 */     buffer[0] = 2;
/* 164 */     buffer[1] = (byte)(index & 0xFF);
/* 165 */     readRegister(17, buffer, buffer);
/*     */   }
/*     */ 
/*     */   private void updateVirtualDevices() {
/* 169 */     if ((this.mVirtualDevices == null) || (this.mVirtualDevices.size() == 0)) {
/* 170 */       this.mVirtualDevices = new ArrayList();
/* 171 */       readDeviceCount();
/*     */     }
/* 173 */     else if (this.delegate != null)
/*     */     {
/* 175 */       this.delegate.device0Started(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private BluxVirtualDevice createVirtualDevice(BluxVirtualDevice.Descriptor desc) {
/* 180 */     if (BluxVDSmartGuard.isKindOf(desc.uuid))
/* 181 */       return new BluxVDSmartGuard(this.mService, desc);
/* 182 */     if (BluxVDPrivate.isKindOf(desc.uuid)) {
/* 183 */       return new BluxVDPrivate(this.mService, desc);
/*     */     }
/* 185 */     return new BluxVDGeneric(this.mService, desc);
/*     */   }
/*     */ 
/*     */   private class VDServiceDelegate extends BluxServiceVirtualDevice.VDServiceDelegate
/*     */   {
/*     */     private VDServiceDelegate()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void vdServiceStateChanged(BluxServiceVirtualDevice.State state)
/*     */     {
/* 137 */       for (BluxVirtualDevice dev : BluxVDDevice0.this.mVirtualDevices)
/* 138 */         dev.serviceStateChange(state);
/*     */     }
/*     */ 
/*     */     protected void vdIrqReceived(int address, byte[] data)
/*     */     {
/* 143 */       if (BluxVDDevice0.this.mVirtualDevices != null)
/* 144 */         for (BluxVirtualDevice dev : BluxVDDevice0.this.mVirtualDevices)
/* 145 */           if (dev.address() == address) {
/* 146 */             dev.irq(data);
/* 147 */             break;
/*     */           }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void device0Started(boolean success)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxVDDevice0
 * JD-Core Version:    0.6.0
 */