/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.bluetooth.BluetoothGatt;
/*     */ import android.bluetooth.BluetoothGattCharacteristic;
/*     */ import android.bluetooth.BluetoothGattDescriptor;
/*     */ import android.bluetooth.BluetoothGattService;
/*     */ import android.util.Log;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxServiceVirtualDevice extends BluxService
/*     */ {
/*     */   VDServiceDelegate vdDelegate;
/*     */   private static final int BLUX_TU_ADDR_MASK = 64;
/*     */   private static final int BLUX_TU_READ_MASK = 32;
/*     */   private static final int BLUX_TU_SEQ_MASK = 31;
/*     */   private static final int BLUX_TU_TYPE_PACKET = 0;
/*     */   private static final int BLUX_TU_SIZE_MAX = 20;
/*     */   private static final int BLUX_TU_PAYLOAD_SIZE_MAX = 19;
/*     */   private static final int BLUX_TRANSFER_PACKET_SIZE_MAX = 608;
/*     */   private static final int BLUX_TRANSFER_PACKET_PAYLOAD_SIZE_MAX = 606;
/*     */   private static final String BLUX_DEVICE_UUID_STRING = "78667579-4E28-477f-9EF3-44C041A1AC5F";
/*  45 */   private static final UUID VIRTUAL_DEVICE_CHAR_WRITE_UUID = UUID.fromString("78667579-66B6-4755-AF51-8937D87D4251")
/*  45 */     ;
/*     */ 
/*  47 */   private static final UUID VIRTUAL_DEVICE_CHAR_READ_UUID = UUID.fromString("78667579-CC60-4E25-B1CE-6FB511B90785")
/*  47 */     ;
/*     */ 
/*  49 */   private static final UUID VIRTUAL_DEVICE_CHAR_IRQ_UUID = UUID.fromString("78667579-1DF0-447D-95E0-5E5E2A9C01E2")
/*  49 */     ;
/*     */ 
/*  52 */   private static final UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")
/*  52 */     ;
/*     */   private BluetoothGattCharacteristic mBtCharRead;
/*     */   private BluetoothGattCharacteristic mBtCharWrite;
/*     */   private BluetoothGattCharacteristic mBtCharIrq;
/*     */   private List<ReadWriteTransfer> mRegisterReadWrites;
/*     */ 
/*     */   BluxServiceVirtualDevice()
/*     */   {
/* 227 */     this.mRegisterReadWrites = new ArrayList();
/* 228 */     this.mBtServiceUUID = UUID.fromString("78667579-4E28-477f-9EF3-44C041A1AC5F");
/*     */   }
/*     */ 
/*     */   protected void terminate()
/*     */   {
/* 233 */     if (this.mRegisterReadWrites != null) {
/* 234 */       cancelAllReadWrites();
/* 235 */       this.mRegisterReadWrites = null;
/*     */     }
/* 237 */     this.mBtCharRead = (this.mBtCharWrite = this.mBtCharIrq = null);
/* 238 */     this.delegate = null;
/* 239 */     super.terminate();
/*     */   }
/*     */ 
/*     */   boolean writeDevice(BluxVirtualDevice device, short address, byte[] data, Object id)
/*     */   {
/* 244 */     if (this.mBtCharWrite == null) {
/* 245 */       return false;
/*     */     }
/* 247 */     ReadWriteTransfer transfer = new ReadWriteTransfer(device, address, false, data, id);
/* 248 */     addRegisterReadWrite(transfer);
/* 249 */     return true;
/*     */   }
/*     */ 
/*     */   boolean readDevice(BluxVirtualDevice device, short address, byte[] data, Object id)
/*     */   {
/* 254 */     if (this.mBtCharWrite == null) {
/* 255 */       return false;
/*     */     }
/* 257 */     ReadWriteTransfer transfer = new ReadWriteTransfer(device, address, true, data, id);
/* 258 */     addRegisterReadWrite(transfer);
/* 259 */     return true;
/*     */   }
/*     */ 
/*     */   protected void onCharacteristicChanged(BluetoothGattCharacteristic characteristic)
/*     */   {
/* 266 */     if (this.mBtCharIrq == null) {
/* 267 */       return;
/*     */     }
/* 269 */     if (this.mBtCharIrq.getUuid().compareTo(characteristic.getUuid()) == 0) {
/* 270 */       byte[] data = characteristic.getValue();
/*     */ 
/* 272 */       if ((this.vdDelegate != null) && (data != null) && (data.length > 0)) {
/* 273 */         byte[] d = Arrays.copyOfRange(data, 1, data.length);
/* 274 */         int device = data[0] & 0xFF;
/* 275 */         this.vdDelegate.vdIrqReceived(device, d);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onDetached()
/*     */   {
/* 282 */     super.onDetached();
/* 283 */     cancelAllReadWrites();
/* 284 */     this.mBtCharRead = null;
/* 285 */     this.mBtCharWrite = null;
/* 286 */     this.mBtCharIrq = null;
/*     */   }
/*     */ 
/*     */   protected void onDisconnected()
/*     */   {
/* 291 */     super.onDisconnected();
/* 292 */     cancelAllReadWrites();
/* 293 */     this.mBtCharRead = null;
/* 294 */     this.mBtCharWrite = null;
/* 295 */     this.mBtCharIrq = null;
/*     */   }
/*     */ 
/*     */   protected void onConnected(BluetoothGatt btGatt, BluetoothGattService btService)
/*     */   {
/* 300 */     super.onConnected(btGatt, btService);
/*     */ 
/* 302 */     this.mBtCharRead = btService.getCharacteristic(VIRTUAL_DEVICE_CHAR_READ_UUID);
/* 303 */     this.mBtCharWrite = btService.getCharacteristic(VIRTUAL_DEVICE_CHAR_WRITE_UUID);
/* 304 */     this.mBtCharIrq = btService.getCharacteristic(VIRTUAL_DEVICE_CHAR_IRQ_UUID);
/*     */ 
/* 306 */     enableNotification(true);
/*     */ 
/* 308 */     if (this.delegate != null)
/* 309 */       this.delegate.serviceStarted(this, true);
/*     */   }
/*     */ 
/*     */   private void addRegisterReadWrite(ReadWriteTransfer transfer)
/*     */   {
/* 314 */     if (this.mRegisterReadWrites != null) {
/* 315 */       if (this.mRegisterReadWrites.size() == 0)
/* 316 */         transfer.readWriteStart();
/* 317 */       this.mRegisterReadWrites.add(transfer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void completeReadWrite() {
/* 322 */     if (this.mRegisterReadWrites != null) {
/* 323 */       this.mRegisterReadWrites.remove(0);
/* 324 */       if (this.mRegisterReadWrites.size() != 0)
/* 325 */         ((ReadWriteTransfer)this.mRegisterReadWrites.get(0)).readWriteStart();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void cancelAllReadWrites() {
/* 330 */     if (this.mRegisterReadWrites != null) {
/* 331 */       for (ReadWriteTransfer transfer : this.mRegisterReadWrites) {
/* 332 */         transfer.readWriteFinished(false);
/*     */       }
/* 334 */       this.mRegisterReadWrites.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void enableNotification(boolean enable) {
/* 339 */     if ((this.mPeripheral != null) && (this.mBtCharIrq != null)) {
/* 340 */       BluetoothGattDescriptor desc = this.mBtCharIrq.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
/* 341 */       this.mBtGatt.setCharacteristicNotification(this.mBtCharIrq, enable);
/* 342 */       BluxPeripheral.WriteDescTransfer transfer = new BluxPeripheral.WriteDescTransfer(desc, enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
/*     */ 
/* 344 */       this.mPeripheral.putTransfer(transfer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ReadWriteTransfer
/*     */   {
/*     */     private BluxVirtualDevice mDevice;
/*     */     private short mRegister;
/*     */     private boolean mRead;
/*     */     private Object mId;
/*     */     private byte[] mBuffer;
/*     */     private int mBufferOffset;
/*     */     private int mLastSeq;
/*     */ 
/*     */     private int send()
/*     */     {
/* 125 */       short hdr = 0;
/*     */ 
/* 127 */       if (this.mBufferOffset >= this.mBuffer.length) {
/* 128 */         return 0;
/*     */       }
/* 130 */       int size = this.mBuffer.length - this.mBufferOffset;
/* 131 */       size = size > 19 ? 19 : size;
/*     */ 
/* 133 */       if (this.mBufferOffset == 0)
/* 134 */         hdr = (short)(hdr | 0x40);
/* 135 */       if (this.mRead)
/* 136 */         hdr = (short)(hdr | 0x20);
/* 137 */       hdr = (short)(hdr | (this.mBuffer.length - this.mBufferOffset - size + 19 - 1) / 19);
/*     */ 
/* 139 */       byte[] tu = new byte[size + 1];
/* 140 */       tu[0] = (byte)hdr;
/* 141 */       BluxVirtualDevice.arrayCopyToArray(tu, 1, this.mBuffer, this.mBufferOffset, size);
/* 142 */       this.mBufferOffset += size;
/*     */ 
/* 144 */       Log.i("BLUX", new StringBuilder().append("[").append(this.mDevice.address()).append(":").append(this.mRegister).append(this.mRead ? "R" : "W").append("]").append(": ").append(Arrays.toString(tu)).toString());
/*     */ 
/* 147 */       WriteTransfer transfer = new WriteTransfer(BluxServiceVirtualDevice.this.mBtCharWrite, tu);
/* 148 */       BluxServiceVirtualDevice.this.mPeripheral.putTransfer(transfer);
/*     */ 
/* 150 */       return tu.length;
/*     */     }
/*     */ 
/*     */     private int receive(byte[] data) {
/* 154 */       if ((data == null) || (data.length == 0)) {
/* 155 */         return -1;
/*     */       }
/* 157 */       int seq = data[0] & 0x1F;
/* 158 */       if (this.mBufferOffset == 0) {
/* 159 */         this.mLastSeq = seq;
/*     */       }
/* 161 */       else if (seq >= this.mLastSeq) {
/* 162 */         return -1;
/*     */       }
/*     */ 
/* 165 */       BluxVirtualDevice.arrayCopyToArray(this.mBuffer, this.mBufferOffset, data, 1, data.length - 1);
/* 166 */       this.mBufferOffset += data.length - 1;
/*     */ 
/* 169 */       if (seq != 0) {
/* 170 */         ReadTransfer transfer = new ReadTransfer(BluxServiceVirtualDevice.this.mBtCharRead);
/* 171 */         BluxServiceVirtualDevice.this.mPeripheral.putTransfer(transfer);
/*     */       }
/* 173 */       return seq;
/*     */     }
/*     */ 
/*     */     ReadWriteTransfer(BluxVirtualDevice device, short register, boolean read, byte[] data, Object id)
/*     */     {
/* 178 */       this.mDevice = device;
/* 179 */       this.mRegister = register;
/* 180 */       this.mRead = read;
/* 181 */       this.mId = id;
/* 182 */       this.mBufferOffset = 0;
/*     */ 
/* 184 */       int size = data == null ? 0 : data.length;
/* 185 */       size = size > 606 ? 606 : size;
/*     */ 
/* 187 */       this.mBuffer = new byte[size + 2];
/* 188 */       this.mBuffer[0] = (byte)(device.address() & 0xFF);
/* 189 */       this.mBuffer[1] = (byte)(register & 0xFF);
/*     */ 
/* 191 */       BluxVirtualDevice.arrayCopyToArray(this.mBuffer, 2, data, 0, size);
/*     */     }
/*     */     void readWriteStart() {
/* 194 */       send();
/*     */     }
/*     */ 
/*     */     void readWriteFinished(boolean success) {
/* 198 */       if (this.mRead) {
/* 199 */         byte[] data = null;
/* 200 */         if (success) {
/* 201 */           if (this.mBufferOffset != 0) {
/* 202 */             data = Arrays.copyOfRange(this.mBuffer, 0, this.mBufferOffset);
/*     */           }
/*     */           else {
/* 205 */             data = new byte[0];
/*     */           }
/*     */         }
/* 208 */         Log.i("BLUX", new StringBuilder().append("[").append(this.mDevice.address()).append(":").append(this.mRegister).append("RD]: ").append(data == null ? "" : Arrays.toString(data)).toString());
/*     */ 
/* 210 */         this.mDevice.didReadRegister(this.mId, this.mRegister, success, data);
/*     */       }
/*     */       else {
/* 213 */         this.mDevice.didWriteRegister(this.mId, this.mRegister, success);
/*     */       }
/*     */     }
/*     */ 
/*     */     private class ReadTransfer extends BluxPeripheral.ReadCharTransfer
/*     */     {
/*     */       ReadTransfer(BluetoothGattCharacteristic ch)
/*     */       {
/*  90 */         super();
/*     */       }
/*     */ 
/*     */       protected void finished(boolean success) {
/*  94 */         if (success) {
/*  95 */           int more = BluxServiceVirtualDevice.ReadWriteTransfer.this.receive(this.mChar.getValue());
/*  96 */           if (more == 0) {
/*  97 */             BluxServiceVirtualDevice.ReadWriteTransfer.this.readWriteFinished(true);
/*  98 */             BluxServiceVirtualDevice.this.completeReadWrite();
/*     */           }
/* 100 */           else if (more < 0) {
/* 101 */             BluxServiceVirtualDevice.ReadWriteTransfer.this.readWriteFinished(false);
/* 102 */             BluxServiceVirtualDevice.this.completeReadWrite();
/*     */           }
/*     */         }
/*     */         else {
/* 106 */           BluxServiceVirtualDevice.ReadWriteTransfer.this.readWriteFinished(false);
/* 107 */           BluxServiceVirtualDevice.this.completeReadWrite();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private class WriteTransfer extends BluxPeripheral.WriteCharTransfer
/*     */     {
/*     */       WriteTransfer(BluetoothGattCharacteristic ch, byte[] data)
/*     */       {
/*  60 */         super(data);
/*     */       }
/*     */ 
/*     */       protected void finished(boolean success) {
/*  64 */         if ((success) && (BluxServiceVirtualDevice.ReadWriteTransfer.this.send() != 0)) {
/*  65 */           return;
/*     */         }
/*  67 */         if (!BluxServiceVirtualDevice.ReadWriteTransfer.this.mRead) {
/*  68 */           BluxServiceVirtualDevice.ReadWriteTransfer.this.readWriteFinished(success);
/*  69 */           BluxServiceVirtualDevice.this.completeReadWrite();
/*     */         }
/*  72 */         else if (success) {
/*  73 */           BluxServiceVirtualDevice.ReadWriteTransfer.access$302(BluxServiceVirtualDevice.ReadWriteTransfer.this, new byte[606]);
/*  74 */           BluxServiceVirtualDevice.ReadWriteTransfer.access$402(BluxServiceVirtualDevice.ReadWriteTransfer.this, 0);
/*  75 */           BluxServiceVirtualDevice.ReadWriteTransfer.access$502(BluxServiceVirtualDevice.ReadWriteTransfer.this, 0);
/*     */ 
/*  77 */           BluxServiceVirtualDevice.ReadWriteTransfer.ReadTransfer transfer = new BluxServiceVirtualDevice.ReadWriteTransfer.ReadTransfer(BluxServiceVirtualDevice.ReadWriteTransfer.this, BluxServiceVirtualDevice.this.mBtCharRead);
/*  78 */           BluxServiceVirtualDevice.this.mPeripheral.putTransfer(transfer);
/*     */         }
/*     */         else {
/*  81 */           BluxServiceVirtualDevice.ReadWriteTransfer.this.readWriteFinished(false);
/*  82 */           BluxServiceVirtualDevice.this.completeReadWrite();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class VDServiceDelegate
/*     */   {
/*     */     void vdIrqReceived(int address, byte[] data)
/*     */     {
/*     */     }
/*     */ 
/*     */     void vdServiceStateChanged(BluxServiceVirtualDevice.State state)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static enum State
/*     */   {
/*  20 */     DISCONNECTED, CONNECTED, BUSY, READY;
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxServiceVirtualDevice
 * JD-Core Version:    0.6.0
 */