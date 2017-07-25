/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ class BluxAccountManager extends BluxObject
/*     */ {
/*     */   InitDelegate initDelegate;
/*     */   private List<Delegate> mDelegates;
/*     */   private BluxVirtualDevice.PacketChannel mPacketChannel;
/*     */   private boolean mInitialized;
/*     */   private int mAccountCount;
/*     */   private int mActiveAccountIndex;
/*     */   private static final int USERMAN_GET_INFO = 1;
/*     */   private static final int USERMAN_GET_USER = 2;
/*     */   private static final int USERMAN_SET_USER = 3;
/*     */ 
/*     */   BluxAccountManager()
/*     */   {
/*  72 */     this.mDelegates = new ArrayList();
/*  73 */     this.mAccountCount = 0;
/*     */   }
/*     */ 
/*     */   protected void terminate()
/*     */   {
/*  78 */     this.mDelegates = null;
/*  79 */     this.initDelegate = null;
/*  80 */     this.mPacketChannel = null;
/*  81 */     super.terminate();
/*     */   }
/*     */ 
/*     */   void registerDelegate(Delegate delegate) {
/*  85 */     if (!this.mDelegates.contains(delegate))
/*  86 */       this.mDelegates.add(delegate);
/*     */   }
/*     */ 
/*     */   void unregisterDelegate(Delegate delegate) {
/*  90 */     this.mDelegates.remove(delegate);
/*     */   }
/*     */ 
/*     */   int count() {
/*  94 */     return this.mAccountCount;
/*     */   }
/*     */ 
/*     */   void start(BluxVirtualDevice.PacketChannel channel) {
/*  98 */     this.mPacketChannel = channel;
/*  99 */     this.mPacketChannel.mReceiver = new PacketReceiver();
/* 100 */     this.mInitialized = false;
/* 101 */     getInfo();
/*     */   }
/*     */ 
/*     */   void setAccount(Account account) {
/* 105 */     if (account != null) {
/* 106 */       byte[] data = account.toStream();
/* 107 */       byte[] packet = new byte[data.length + 1];
/* 108 */       packet[0] = 3;
/* 109 */       BluxVirtualDevice.arrayCopyToArray(packet, 1, data, 0, data.length);
/* 110 */       this.mPacketChannel.send(packet, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   void getAccount(int index) {
/* 115 */     byte[] packet = { 2, (byte)index };
/* 116 */     this.mPacketChannel.send(packet, true);
/*     */   }
/*     */ 
/*     */   private void getInfo()
/*     */   {
/* 133 */     byte[] packet = { 1 };
/* 134 */     this.mPacketChannel.send(packet, true);
/*     */   }
/*     */   private class PacketReceiver extends BluxVirtualDevice.PacketChannelReceiver {
/*     */     private PacketReceiver() {
/*     */     }
/*     */     protected void received(boolean success, byte[] packet) {
/* 140 */       if ((!success) || (packet == null)) {
/* 141 */         return;
/*     */       }
/* 143 */       if ((!BluxAccountManager.this.mInitialized) && (packet.length == 0)) {
/* 144 */         if (BluxAccountManager.this.initDelegate != null)
/* 145 */           BluxAccountManager.this.initDelegate.started(false);
/* 146 */         BluxAccountManager.access$202(BluxAccountManager.this, true);
/*     */       }
/*     */       BluxAccountManager.Account account;
/*     */       BluxAccountManager.Account account;
/* 150 */       switch (packet[0]) {
/*     */       case 1:
/* 152 */         BluxAccountManager.access$302(BluxAccountManager.this, packet[2] & 0xFF);
/* 153 */         BluxAccountManager.access$402(BluxAccountManager.this, packet[3] & 0xFF);
/* 154 */         BluxAccountManager.access$202(BluxAccountManager.this, true);
/* 155 */         if (BluxAccountManager.this.initDelegate == null) break;
/* 156 */         BluxAccountManager.this.initDelegate.started(true); break;
/*     */       case 2:
/* 160 */         if ((BluxAccountManager.this.mDelegates == null) || (BluxAccountManager.this.mDelegates.size() == 0) || (packet.length <= 3) || (packet[1] == 0)) break;
/* 161 */         account = BluxAccountManager.Account.fromStream(Arrays.copyOfRange(packet, 2, packet.length));
/* 162 */         if (account != null) {
/* 163 */           for (BluxAccountManager.Delegate delegate : BluxAccountManager.this.mDelegates)
/* 164 */             delegate.updateAccount(account);
/*     */         }
/* 166 */         break;
/*     */       case 3:
/* 170 */         if ((BluxAccountManager.this.mDelegates == null) || (BluxAccountManager.this.mDelegates.size() == 0) || (packet.length <= 3)) break;
/* 171 */         account = BluxAccountManager.Account.fromStream(Arrays.copyOfRange(packet, 2, packet.length));
/* 172 */         if (account == null) break;
/* 173 */         for (BluxAccountManager.Delegate delegate : BluxAccountManager.this.mDelegates)
/* 174 */           delegate.updateAccount(account);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Account
/*     */   {
/*     */     int index;
/*     */     int control;
/*     */     int passkey;
/*     */     String name;
/*     */ 
/*     */     private byte[] toStream()
/*     */     {
/*  25 */       byte[] name = this.name.getBytes();
/*  26 */       byte[] data = new byte[8 + name.length];
/*     */ 
/*  28 */       data[0] = (byte)this.index;
/*  29 */       BluxVirtualDevice.s2le((short)this.control, data, 1);
/*  30 */       BluxVirtualDevice.l2le(this.passkey, data, 3);
/*  31 */       data[7] = (byte)name.length;
/*  32 */       BluxVirtualDevice.arrayCopyToArray(data, 8, name, 0, name.length);
/*     */ 
/*  34 */       return data;
/*     */     }
/*     */ 
/*     */     Account() {
/*     */     }
/*     */ 
/*     */     Account(int index, int control, int passkey, String name) {
/*  41 */       this.index = index;
/*  42 */       this.control = control;
/*  43 */       this.passkey = passkey;
/*  44 */       this.name = name;
/*     */     }
/*     */ 
/*     */     static Account fromStream(byte[] stream) {
/*  48 */       if ((stream.length < 8) || (stream.length != (stream[7] + 8 & 0xFF))) {
/*  49 */         return null;
/*     */       }
/*  51 */       Account account = new Account();
/*  52 */       account.index = (stream[0] & 0xFF);
/*  53 */       account.control = BluxVirtualDevice.le2s(stream, 1);
/*  54 */       account.passkey = BluxVirtualDevice.le2l(stream, 3);
/*  55 */       if (stream[7] != 0) {
/*  56 */         byte[] name = Arrays.copyOfRange(stream, 8, stream.length);
/*     */         try {
/*  58 */           account.name = new String(name, "UTF-8");
/*     */         } catch (Exception exception) {
/*  60 */           account.name = null;
/*     */         }
/*     */       }
/*  63 */       return account;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class InitDelegate
/*     */   {
/*     */     protected void started(boolean success)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void updateAccount(BluxAccountManager.Account account)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxAccountManager
 * JD-Core Version:    0.6.0
 */