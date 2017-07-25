/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxVirtualDevice extends BluxObject
/*     */ {
/*     */   protected BluxServiceVirtualDevice mService;
/*     */   protected Descriptor mDescriptor;
/*     */ 
/*     */   protected static void arrayCopyToArray(byte[] dest, int offsetDest, byte[] src, int offsetSrc, int size)
/*     */   {
/*   8 */     if ((dest == null) || (src == null)) {
/*   9 */       return;
/*     */     }
/*  11 */     for (int i = 0; i < size; i++)
/*  12 */       dest[(offsetDest + i)] = src[(offsetSrc + i)];
/*     */   }
/*     */ 
/*     */   protected static void s2le(short s, byte[] buffer, int offset)
/*     */   {
/*  17 */     buffer[offset] = (byte)(s & 0xFF);
/*  18 */     buffer[(offset + 1)] = (byte)(s >> 8 & 0xFF);
/*     */   }
/*     */ 
/*     */   protected static void l2le(int l, byte[] buffer, int offset) {
/*  22 */     buffer[offset] = (byte)(l & 0xFF);
/*  23 */     buffer[(offset + 1)] = (byte)(l >> 8 & 0xFF);
/*  24 */     buffer[(offset + 2)] = (byte)(l >> 16 & 0xFF);
/*  25 */     buffer[(offset + 3)] = (byte)(l >> 24 & 0xFF);
/*     */   }
/*     */ 
/*     */   protected static short le2s(byte[] buffer, int offset)
/*     */   {
/*  30 */     short s = (short)buffer[(offset + 1)];
/*  31 */     s = (short)(s << 8 | buffer[offset] & 0xFF);
/*  32 */     return s;
/*     */   }
/*     */ 
/*     */   protected static int le2l(byte[] buffer, int offset)
/*     */   {
/*  37 */     int l = buffer[(offset + 3)];
/*  38 */     l = l << 8 | buffer[(offset + 2)] & 0xFF;
/*  39 */     l = l << 8 | buffer[(offset + 1)] & 0xFF;
/*  40 */     l = l << 8 | buffer[offset] & 0xFF;
/*  41 */     return l;
/*     */   }
/*     */ 
/*     */   static int le2uc(byte[] buffer, int offset) {
/*  45 */     int uc = buffer[offset];
/*  46 */     uc &= 255;
/*  47 */     return uc;
/*     */   }
/*     */ 
/*     */   BluxVirtualDevice(BluxServiceVirtualDevice service, Descriptor desc)
/*     */   {
/*  94 */     this.mService = service;
/*  95 */     this.mDescriptor = desc;
/*     */   }
/*     */ 
/*     */   protected void terminate() {
/*  99 */     this.mService = null;
/* 100 */     this.mDescriptor = null;
/* 101 */     super.terminate();
/*     */   }
/*     */ 
/*     */   protected short address() {
/* 105 */     if (this.mDescriptor != null)
/* 106 */       return this.mDescriptor.address;
/* 107 */     return 0;
/*     */   }
/*     */ 
/*     */   protected UUID typeUuid() {
/* 111 */     if (this.mDescriptor != null)
/* 112 */       return this.mDescriptor.uuid;
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   boolean readRegister(int register, byte[] data, Object id)
/*     */   {
/* 119 */     return this.mService.readDevice(this, (short)register, data, id);
/*     */   }
/*     */ 
/*     */   boolean writeRegister(int register, byte[] data, Object id) {
/* 123 */     return this.mService.writeDevice(this, (short)register, data, id);
/*     */   }
/*     */ 
/*     */   boolean readRegister(int register, byte[] data) {
/* 127 */     return this.mService.readDevice(this, (short)register, data, null);
/*     */   }
/*     */ 
/*     */   boolean writeRegister(int register, byte[] data) {
/* 131 */     return this.mService.writeDevice(this, (short)register, data, null);
/*     */   }
/*     */ 
/*     */   protected void serviceStateChange(BluxServiceVirtualDevice.State state)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void didReadRegister(Object id, int register, boolean success, byte[] data) {
/* 139 */     PacketChannelReceiver receiver = (PacketChannelReceiver)id;
/* 140 */     if (receiver != null)
/* 141 */       receiver.received(success, data);
/*     */   }
/*     */ 
/*     */   protected void didWriteRegister(Object id, int register, boolean success)
/*     */   {
/* 146 */     PacketChannelReceiver receiver = (PacketChannelReceiver)id;
/* 147 */     if (receiver != null)
/* 148 */       receiver.sent(success);
/*     */   }
/*     */ 
/*     */   protected void irq(byte[] data)
/*     */   {
/*     */   }
/*     */ 
/*     */   class PacketChannel
/*     */   {
/*     */     private short mRegister;
/*     */     BluxVirtualDevice.PacketChannelReceiver mReceiver;
/*     */ 
/*     */     PacketChannel(short register)
/*     */     {
/*  77 */       this.mRegister = register;
/*     */     }
/*     */ 
/*     */     boolean send(byte[] packet, boolean read) {
/*  81 */       if (read) {
/*  82 */         return BluxVirtualDevice.this.readRegister(this.mRegister, packet, this.mReceiver);
/*     */       }
/*  84 */       return BluxVirtualDevice.this.writeRegister(this.mRegister, packet, this.mReceiver);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class PacketChannelReceiver
/*     */   {
/*     */     protected void received(boolean success, byte[] packet)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void sent(boolean success)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Descriptor
/*     */   {
/*     */     short address;
/*     */     UUID uuid;
/*     */ 
/*     */     Descriptor(byte[] data)
/*     */     {
/*  57 */       this.address = (short)data[0];
/*  58 */       this.address = (short)(this.address & 0xFF);
/*     */ 
/*  60 */       String s = String.format("%02X%02X%02X%02X-%02X%02X-%02X%02X-%02X%02X-%02X%02X%02X%02X%02X%02X", new Object[] { 
/*  61 */         Byte.valueOf(data[16]), 
/*  61 */         Byte.valueOf(data[15]), Byte.valueOf(data[14]), Byte.valueOf(data[13]), Byte.valueOf(data[12]), Byte.valueOf(data[11]), Byte.valueOf(data[10]), Byte.valueOf(data[9]), Byte.valueOf(data[8]), 
/*  62 */         Byte.valueOf(data[7]), 
/*  62 */         Byte.valueOf(data[6]), Byte.valueOf(data[5]), Byte.valueOf(data[4]), Byte.valueOf(data[3]), Byte.valueOf(data[2]), Byte.valueOf(data[1]) });
/*  63 */       this.uuid = UUID.fromString(s);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxVirtualDevice
 * JD-Core Version:    0.6.0
 */