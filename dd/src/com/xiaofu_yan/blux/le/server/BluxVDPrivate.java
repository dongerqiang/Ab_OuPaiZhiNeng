/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.annotation.SuppressLint;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Arrays;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxVDPrivate extends BluxVirtualDevice
/*     */ {
/*     */   Delegate delegate;
/*  31 */   static final UUID VD_TYPE_UUID = UUID.fromString("78667579-BDAC-48A0-AB46-869A3D2F8493")
/*  31 */     ;
/*     */   private static final int REG_RESET = 17;
/*     */   private static final int REG_BLE_ROLE = 18;
/*     */   private static final int REG_HEART_BEAT = 19;
/*     */   private static final int REG_CONNECTION_PARAMETER = 33;
/*     */   private static final int REG_NAME = 49;
/*     */   private static final int REG_SERIAL_NUMBER = 50;
/*     */   private static final int REG_PAIR_PASSKEY = 51;
/*     */   private static final int REG_BROADCAST_AD = 52;
/*     */   private static final int REG_USER_STORAGE_CONFIG = 53;
/*     */   private static final int REG_USER_STORAGE = 54;
/*     */   private static final int REG_PRIVATE_NVM = 55;
/*     */ 
/*     */   BluxVDPrivate(BluxServiceVirtualDevice service, BluxVirtualDevice.Descriptor desc)
/*     */   {
/*  48 */     super(service, desc);
/*     */   }
/*     */ 
/*     */   protected void terminate()
/*     */   {
/*  53 */     this.delegate = null;
/*  54 */     super.terminate();
/*     */   }
/*     */ 
/*     */   void resetPeer(short delay) {
/*  58 */     byte[] data = new byte[2];
/*     */ 
/*  60 */     s2le(delay, data, 0);
/*  61 */     writeRegister(17, data);
/*     */   }
/*     */ 
/*     */   void getSerialNumber() {
/*  65 */     readRegister(50, null);
/*     */   }
/*     */ 
/*     */   void getPairPasskey() {
/*  69 */     readRegister(51, null);
/*     */   }
/*     */ 
/*     */   void readPeerRole() {
/*  73 */     readRegister(18, null);
/*     */   }
/*     */ 
/*     */   void writePeerRole(byte role) {
/*  77 */     byte[] data = { role };
/*     */ 
/*  79 */     writeRegister(18, data);
/*     */   }
/*     */ 
/*     */   void readPeerHeartBeat() {
/*  83 */     readRegister(19, null);
/*     */   }
/*     */ 
/*     */   void writePeerHeartBeat(short period) {
/*  87 */     byte[] data = new byte[2];
/*     */ 
/*  89 */     s2le(period, data, 0);
/*  90 */     writeRegister(19, data);
/*     */   }
/*     */ 
/*     */   void readName() {
/*  94 */     readRegister(49, null);
/*     */   }
/*     */ 
/*     */   void writeName(String name)
/*     */   {
/*     */     try {
/* 100 */       byte[] data = name.getBytes("UTF-8");
/* 101 */       writeRegister(49, data);
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void readConnectionParam() {
/* 108 */     readRegister(33, null);
/*     */   }
/*     */ 
/*     */   void writeConnectionParam(int minMs, int maxMs, int n, int timeOut) {
/* 112 */     byte[] data = new byte[8];
/*     */ 
/* 114 */     s2le((short)minMs, data, 0);
/* 115 */     s2le((short)maxMs, data, 2);
/* 116 */     s2le((short)n, data, 4);
/* 117 */     s2le((short)timeOut, data, 6);
/*     */ 
/* 119 */     writeRegister(33, data);
/*     */   }
/*     */ 
/*     */   void readBroadcastAD() {
/* 123 */     readRegister(52, null);
/*     */   }
/*     */ 
/*     */   void writeBroadcastAD(byte[] ad) {
/* 127 */     writeRegister(52, ad);
/*     */   }
/*     */ 
/*     */   void readUserStorageConfig() {
/* 131 */     readRegister(53, null);
/*     */   }
/*     */ 
/*     */   void readUserStorage(short offset, byte words) {
/* 135 */     byte[] data = new byte[3];
/*     */ 
/* 137 */     s2le(offset, data, 0);
/* 138 */     data[2] = words;
/*     */ 
/* 140 */     readRegister(54, null);
/*     */   }
/*     */ 
/*     */   void writeUserStorage(short offset, byte[] words) {
/* 144 */     byte[] data = new byte[words.length + 2];
/*     */ 
/* 146 */     s2le(offset, data, 0);
/* 147 */     arrayCopyToArray(data, 2, words, 0, words.length);
/*     */ 
/* 149 */     writeRegister(54, data);
/*     */   }
/*     */ 
/*     */   void readPrivateNvm(short offset, byte words) {
/* 153 */     byte[] data = new byte[3];
/*     */ 
/* 155 */     s2le(offset, data, 0);
/* 156 */     data[2] = words;
/*     */ 
/* 158 */     readRegister(55, data);
/*     */   }
/*     */ 
/*     */   void writePrivateNvm(short offset, byte[] words) {
/* 162 */     byte[] data = new byte[words.length + 2];
/*     */ 
/* 164 */     s2le(offset, data, 0);
/* 165 */     arrayCopyToArray(data, 2, words, 0, words.length);
/*     */ 
/* 167 */     writeRegister(55, data);
/*     */   }
/*     */ 
/*     */   static boolean isKindOf(UUID uuidType)
/*     */   {
/* 173 */     return VD_TYPE_UUID.compareTo(uuidType) == 0;
/*     */   }
/*     */ 
/*     */   @SuppressLint({"DefaultLocale"})
/*     */   protected void didReadRegister(Object id, int register, boolean success, byte[] data) {
/* 179 */     if ((this.delegate == null) || (!success) || (data == null)) {
/* 180 */       return;
/*     */     }
/* 182 */     switch (register) {
/*     */     case 18:
/* 184 */       if (data.length == 0) break;
/* 185 */       byte role = data[0];
/* 186 */       this.delegate.updatePeerRole(role);
/* 187 */       break;
/*     */     case 19:
/* 191 */       if (data.length != 2) break;
/* 192 */       short period = le2s(data, 0);
/* 193 */       this.delegate.updateHeartBeat(period);
/* 194 */       break;
/*     */     case 33:
/* 198 */       if (data.length != 8) break;
/* 199 */       short minInterval = le2s(data, 0);
/* 200 */       short maxInterval = le2s(data, 2);
/* 201 */       short latency = le2s(data, 4);
/* 202 */       short timeOut = le2s(data, 6);
/* 203 */       this.delegate.updateConnectionParam(timeOut, minInterval, maxInterval, latency);
/* 204 */       break;
/*     */     case 49:
/* 208 */       String name = new String(data, Charset.forName("UTF-8"));
/* 209 */       this.delegate.updateName(name);
/* 210 */       break;
/*     */     case 50:
/* 213 */       if (data.length == 0) break;
/* 214 */       String str = new String(data, Charset.forName("UTF-8"));
/* 215 */       this.delegate.updateSerialNumber(str);
/* 216 */       break;
/*     */     case 51:
/* 220 */       if (data.length != 4) break;
/* 221 */       int key = le2l(data, 0);
/* 222 */       String str = String.format("%06d", new Object[] { Integer.valueOf(key) });
/* 223 */       this.delegate.updatePairPasskey(str);
/* 224 */       break;
/*     */     case 52:
/* 228 */       this.delegate.updateBroadcastAD(data);
/* 229 */       break;
/*     */     case 53:
/* 232 */       if (data.length != 2) break;
/* 233 */       short count = le2s(data, 0);
/* 234 */       this.delegate.updateUserStorageConfig(count);
/* 235 */       break;
/*     */     case 54:
/* 239 */       if (data.length <= 2) break;
/* 240 */       short offset = le2s(data, 0);
/* 241 */       byte[] d = Arrays.copyOfRange(data, 2, data.length);
/* 242 */       this.delegate.updateUserStorage(offset, d);
/* 243 */       break;
/*     */     case 55:
/* 247 */       if (data.length <= 2) break;
/* 248 */       short offset = le2s(data, 0);
/* 249 */       byte[] d = Arrays.copyOfRange(data, 2, data.length);
/* 250 */       this.delegate.updatePrivateNvm(offset, d);
/* 251 */       break;
/*     */     case 20:
/*     */     case 21:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 29:
/*     */     case 30:
/*     */     case 31:
/*     */     case 32:
/*     */     case 34:
/*     */     case 35:
/*     */     case 36:
/*     */     case 37:
/*     */     case 38:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/*     */     case 46:
/*     */     case 47:
/*     */     case 48: }  } 
/* 263 */   protected void irq(byte[] data) { if ((data != null) && (data.length > 0) && (this.delegate != null)) {
/* 264 */       short register = (short)((short)data[0] & 0xFF);
/* 265 */       if (register == 19) {
/* 266 */         byte[] d = Arrays.copyOfRange(data, 1, data.length);
/* 267 */         this.delegate.heartBeat(d);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void heartBeat(byte[] data)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateHeartBeat(short period)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateSerialNumber(String serialNumber)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updatePairPasskey(String passKey)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updatePeerRole(byte role)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateName(String name)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateConnectionParam(short timeOut, short minMs, short maxMs, short latency)
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
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxVDPrivate
 * JD-Core Version:    0.6.0
 */