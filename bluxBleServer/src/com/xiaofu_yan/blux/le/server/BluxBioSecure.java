/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.annotation.SuppressLint;
/*     */ import java.util.Arrays;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ 
/*     */ class BluxBioSecure extends BluxObject
/*     */ {
/*     */   Delegate delegate;
/*     */   private static final int BIOSEC_CLIENT_REQUEST_CONNECT = 1;
/*     */   private static final int BIOSEC_CLIENT_REQUEST_PASS_PAIR = 16;
/*     */   private static final int BIOSEC_CLIENT_REQUEST_KEY_PAIR = 17;
/*     */   private State mState;
/*     */   private Ticket mStub;
/*     */   private Ticket mTicket;
/*     */   private Procedure mProcedure;
/*     */   private BluxVirtualDevice.PacketChannel mPacketChannel;
/*     */ 
/*     */   BluxBioSecure()
/*     */   {
/* 103 */     this.mStub = new Ticket(0, null);
/*     */   }
/*     */ 
/*     */   protected void terminate() {
/* 107 */     this.delegate = null;
/* 108 */     this.mPacketChannel = null;
/* 109 */     this.mProcedure = null;
/* 110 */     this.mTicket = null;
/* 111 */     this.mStub = null;
/*     */   }
/*     */ 
/*     */   void startPassPair(int userId, int pass, BluxVirtualDevice.PacketChannel packetChannel) {
/* 115 */     if (this.mState != State.READY)
/* 116 */       return;
/* 117 */     this.mPacketChannel = packetChannel;
/* 118 */     byte[] data = new byte[8];
/* 119 */     Arrays.fill(data, (byte) 0);
/* 120 */     data[0] = (byte)(pass & 0xFF);
/* 121 */     data[1] = (byte)(pass >> 8 & 0xFF);
/* 122 */     data[2] = (byte)(pass >> 16 & 0xFF);
/* 123 */     data[3] = (byte)(pass >> 24 & 0xFF);
/* 124 */     this.mTicket = new Ticket(userId, data);
/* 125 */     this.mProcedure = new PassPairProcedure();
/* 126 */     this.mProcedure.start();
/*     */   }
/*     */ 
/*     */   void startKeyPair(Ticket ticket, BluxVirtualDevice.PacketChannel packetChannel) {
/* 130 */     if (this.mState != State.READY)
/* 131 */       return;
/* 132 */     this.mPacketChannel = packetChannel;
/* 133 */     this.mTicket = ticket;
/* 134 */     this.mProcedure = new KeyPairProcedure();
/* 135 */     this.mProcedure.start();
/*     */   }
/*     */ 
/*     */   void startConnect(Ticket ticket, BluxVirtualDevice.PacketChannel packetChannel) {
/* 139 */     if (this.mState != State.READY)
/* 140 */       return;
/* 141 */     this.mPacketChannel = packetChannel;
/* 142 */     this.mTicket = ticket;
/* 143 */     this.mProcedure = new ConnectionProcedure();
/* 144 */     this.mProcedure.start();
/*     */   }
/*     */ 
/*     */   void reset() {
/* 148 */     this.mState = State.READY;
/*     */   }
/*     */ 
/*     */   @SuppressLint({"TrulyRandom"})
/*     */   static byte[] aes_encrypt(byte[] key, byte[] clear)
/*     */   {
/* 368 */     byte[] encrypted = null;
/* 369 */     key = reverse_bytes(key);
/* 370 */     clear = reverse_bytes(clear);
/*     */     try {
/* 372 */       SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/* 373 */       Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
/* 374 */       cipher.init(1, skeySpec);
/* 375 */       encrypted = cipher.doFinal(clear);
/* 376 */       encrypted = reverse_bytes(encrypted);
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 380 */     return encrypted;
/*     */   }
/*     */ 
/*     */   static byte[] aes_decrypt(byte[] key, byte[] encrypted) {
/* 384 */     byte[] decrypted = null;
/* 385 */     key = reverse_bytes(key);
/* 386 */     encrypted = reverse_bytes(encrypted);
/*     */     try {
/* 388 */       SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/* 389 */       Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
/* 390 */       cipher.init(2, skeySpec);
/* 391 */       decrypted = cipher.doFinal(encrypted);
/* 392 */       decrypted = reverse_bytes(decrypted);
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 396 */     return decrypted;
/*     */   }
/*     */ 
/*     */   private static byte[] reverse_bytes(byte[] data)
/*     */   {
/* 401 */     byte[] reversed = new byte[data.length];
/* 402 */     for (int i = 0; i < data.length; i++)
/* 403 */       reversed[(data.length - i - 1)] = data[i];
/* 404 */     return reversed;
/*     */   }
/*     */ 
/*     */   private class ConnectionProcedure extends BluxBioSecure.Procedure
/*     */   {
/*     */     private ConnectionProcedure()
/*     */     {
/* 315 */       super();
/*     */     }
/* 317 */     protected void start() { byte[] data = { 1, (byte)BluxBioSecure.access$500(BluxBioSecure.this).userId };
/* 318 */       send(data);
/* 319 */       BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.CHALLENGE);
/*     */     }
/*     */ 
/*     */     protected void packetReceived(byte[] packet)
/*     */     {
/* 324 */       switch (BluxBioSecure.1.$SwitchMap$com$xiaofu_yan$blux$le$server$BluxBioSecure$State[BluxBioSecure.this.mState.ordinal()]) {
/*     */       case 1:
/* 326 */         if (packet.length == 0) {
/* 327 */           if (BluxBioSecure.this.delegate != null) {
/* 328 */             BluxBioSecure.this.delegate.connectResult(BluxBioSecure.Result.SUCCESS);
/*     */           }
/* 330 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.AUTHORIZED);
/*     */         }
/* 332 */         else if (packet.length != 16) {
/* 333 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.ERROR_USER);
/* 334 */           if (BluxBioSecure.this.delegate == null) break;
/* 335 */           BluxBioSecure.this.delegate.connectResult(BluxBioSecure.Result.ERROR_USER);
/*     */         }
/*     */         else
/*     */         {
/* 339 */           byte[] key = BluxBioSecure.Ticket.makeConnectionKey(BluxBioSecure.this.mTicket, BluxBioSecure.this.mStub);
/* 340 */           packet = BluxBioSecure.aes_encrypt(key, packet);
/* 341 */           send(packet);
/* 342 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.CHECK);
/*     */         }
/* 344 */         break;
/*     */       case 2:
/* 347 */         if (packet[0] == 0) {
/* 348 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.ERROR_KEY);
/* 349 */           if (BluxBioSecure.this.delegate == null) break;
/* 350 */           BluxBioSecure.this.delegate.connectResult(BluxBioSecure.Result.ERROR_KEY);
/*     */         }
/*     */         else
/*     */         {
/* 354 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.AUTHORIZED);
/* 355 */           BluxBioSecure.this.delegate.connectResult(BluxBioSecure.Result.SUCCESS);
/*     */         }
/* 357 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class KeyPairProcedure extends BluxBioSecure.Procedure
/*     */   {
/*     */     private KeyPairProcedure()
/*     */     {
/* 258 */       super();
/*     */     }
/*     */     protected void start() {
/* 261 */       byte[] data = new byte[10];
/* 262 */       data[0] = 17;
/* 263 */       data[1] = (byte)BluxBioSecure.access$500(BluxBioSecure.this).userId;
/* 264 */       for (int i = 0; i < 8; i++) {
/* 265 */         data[(2 + i)] = BluxBioSecure.access$700(BluxBioSecure.this).data[i];
/*     */       }
/* 267 */       send(data);
/* 268 */       BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.CHALLENGE);
/*     */     }
/*     */ 
/*     */     protected void packetReceived(byte[] packet) {
/* 272 */       if ((packet == null) || (packet.length == 0)) {
/* 273 */         if ((BluxBioSecure.this.mState == BluxBioSecure.State.CHALLENGE) && (BluxBioSecure.this.delegate != null)) {
/* 274 */           BluxBioSecure.this.delegate.connectResult(BluxBioSecure.Result.SUCCESS);
/*     */         }
/* 276 */         BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.AUTHORIZED);
/* 277 */         return;
/*     */       }
/*     */ 
/* 280 */       switch (BluxBioSecure.1.$SwitchMap$com$xiaofu_yan$blux$le$server$BluxBioSecure$State[BluxBioSecure.this.mState.ordinal()]) {
/*     */       case 1:
/* 282 */         if (packet.length != 16) {
/* 283 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.ERROR_USER);
/* 284 */           if (BluxBioSecure.this.delegate == null) break;
/* 285 */           BluxBioSecure.this.delegate.pairResult(BluxBioSecure.Result.ERROR_USER, null, null);
/*     */         }
/*     */         else
/*     */         {
/* 289 */           byte[] key = BluxBioSecure.Ticket.makeConnectionKey(BluxBioSecure.this.mTicket, BluxBioSecure.this.mStub);
/* 290 */           packet = BluxBioSecure.aes_encrypt(key, packet);
/* 291 */           send(packet);
/* 292 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.CHECK);
/*     */         }
/* 294 */         break;
/*     */       case 2:
/* 297 */         if (packet[0] == 0) {
/* 298 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.ERROR_KEY);
/* 299 */           if (BluxBioSecure.this.delegate == null) break;
/* 300 */           BluxBioSecure.this.delegate.pairResult(BluxBioSecure.Result.ERROR_KEY, null, null);
/*     */         }
/*     */         else
/*     */         {
/* 304 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.AUTHORIZED);
/* 305 */           BluxBioSecure.this.delegate.pairResult(BluxBioSecure.Result.SUCCESS, BluxBioSecure.this.mStub, BluxBioSecure.this.mTicket);
/*     */         }
/* 307 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PassPairProcedure extends BluxBioSecure.Procedure
/*     */   {
/*     */     private PassPairProcedure()
/*     */     {
/* 194 */       super();
/*     */     }
/*     */     protected void start() {
/* 197 */       byte[] data = { 16, (byte)BluxBioSecure.access$500(BluxBioSecure.this).userId };
/* 198 */       send(data);
/* 199 */       BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.CHALLENGE);
/*     */     }
/*     */ 
/*     */     protected void packetReceived(byte[] packet) {
/* 203 */       if ((packet == null) || (packet.length == 0)) {
/* 204 */         if ((BluxBioSecure.this.mState == BluxBioSecure.State.CHALLENGE) && (BluxBioSecure.this.delegate != null)) {
/* 205 */           BluxBioSecure.this.delegate.connectResult(BluxBioSecure.Result.SUCCESS);
/*     */         }
/* 207 */         BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.AUTHORIZED);
/* 208 */         return;
/*     */       }
/*     */ 
/* 211 */       switch (BluxBioSecure.1.$SwitchMap$com$xiaofu_yan$blux$le$server$BluxBioSecure$State[BluxBioSecure.this.mState.ordinal()]) {
/*     */       case 1:
/* 213 */         if (packet.length != 16) {
/* 214 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.ERROR_USER);
/* 215 */           if (BluxBioSecure.this.delegate == null) break;
/* 216 */           BluxBioSecure.this.delegate.pairResult(BluxBioSecure.Result.ERROR_USER, null, null);
/*     */         }
/*     */         else
/*     */         {
/* 220 */           byte[] key = BluxBioSecure.Ticket.makePassPairKey(BluxBioSecure.this.mTicket);
/* 221 */           packet = BluxBioSecure.aes_encrypt(key, packet);
/* 222 */           send(packet);
/* 223 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.CHECK);
/*     */         }
/* 225 */         break;
/*     */       case 2:
/* 228 */         if (packet[0] == 0) {
/* 229 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.ERROR_KEY);
/* 230 */           if (BluxBioSecure.this.delegate == null) break;
/* 231 */           BluxBioSecure.this.delegate.pairResult(BluxBioSecure.Result.ERROR_KEY, null, null);
/*     */         }
/*     */         else
/*     */         {
/* 235 */           send(BluxBioSecure.this.mStub.data);
/* 236 */           BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.PP_KEY_EXCHANGE);
/*     */         }
/* 238 */         break;
/*     */       case 3:
/* 241 */         if (packet.length == 16) {
/* 242 */           byte[] key = BluxBioSecure.Ticket.makePassPairKey(BluxBioSecure.this.mTicket);
/* 243 */           packet = BluxBioSecure.aes_decrypt(key, packet);
/* 244 */           BluxBioSecure.Ticket ticket = new BluxBioSecure.Ticket(BluxBioSecure.this.mTicket.userId, packet);
/* 245 */           if (BluxBioSecure.this.delegate != null) {
/* 246 */             BluxBioSecure.this.delegate.pairResult(BluxBioSecure.Result.SUCCESS, BluxBioSecure.this.mStub, ticket);
/*     */           }
/*     */         }
/* 249 */         BluxBioSecure.access$602(BluxBioSecure.this, BluxBioSecure.State.AUTHORIZED);
/* 250 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Procedure
/*     */   {
/*     */     Procedure()
/*     */     {
/* 176 */       BluxBioSecure.this.mPacketChannel.mReceiver = new PacketReceiver(null);
/*     */     }
/*     */ 
/*     */     void send(byte[] packet) {
/* 180 */       BluxBioSecure.this.mPacketChannel.send(packet, true);
/*     */     }
/*     */     protected void start() {  }
/*     */ 
/*     */     protected void packetReceived(byte[] packet) {  }
/*     */ 
/*     */     private class PacketReceiver extends BluxVirtualDevice.PacketChannelReceiver { private PacketReceiver() {  }
/*     */ 
/* 185 */       protected void received(boolean success, byte[] packet) { if ((success) && (packet != null))
/* 186 */           BluxBioSecure.Procedure.this.packetReceived(packet);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum State
/*     */   {
/* 165 */     READY, 
/* 166 */     AUTHORIZED, 
/* 167 */     CHALLENGE, 
/* 168 */     CHECK, 
/* 169 */     PP_KEY_EXCHANGE, 
/* 170 */     ERROR_USER, 
/* 171 */     ERROR_KEY;
/*     */   }
/*     */ 
/*     */   static class Ticket
/*     */   {
/*  30 */     static final byte[] KEY = { 88, 105, 97, 111, 102, 117, 89, 97, 110, 40, 99, 41, 50, 48, 49, 52 };
/*     */     int userId;
/*     */     byte[] data;
/*     */ 
/*     */     Ticket(int userId, byte[] data)
/*     */     {
/*  34 */       this.userId = userId;
/*  35 */       if (data != null) {
/*  36 */         this.data = Arrays.copyOf(data, 8);
/*     */       }
/*     */       else {
/*  39 */         this.data = new byte[8];
/*  40 */         Arrays.fill(this.data, 0);
/*     */       }
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  45 */       byte[] d = Arrays.copyOf(this.data, 16);
/*  46 */       d[9] = (byte)this.userId;
/*  47 */       d[10] = 120;
/*  48 */       d[11] = 102;
/*  49 */       d[12] = 117;
/*  50 */       d[13] = 121;
/*  51 */       d = BluxBioSecure.aes_encrypt(KEY, d);
/*  52 */       String str = new String();
/*  53 */       for (int i = 0; i < d.length; i++)
/*     */       {
/*  56 */         char c = (char)(65 + (d[i] & 0xF));
/*  57 */         str = str + c;
/*  58 */         c = (char)(75 + (d[i] >> 4 & 0xF));
/*  59 */         str = str + c;
/*     */       }
/*  61 */       return str;
/*     */     }
/*     */ 
/*     */     public static Ticket fromString(String str) {
/*  65 */       if (str.length() != 32)
/*  66 */         return null;
/*  67 */       byte[] d = new byte[16];
/*  68 */       for (int i = 0; i < str.length(); i += 2)
/*     */       {
/*  71 */         char c = str.charAt(i + 1);
/*  72 */         if ((c < 'K') || (c > 'Z'))
/*  73 */           return null;
/*  74 */         byte b = (byte)(c - 'K');
/*  75 */         c = str.charAt(i);
/*  76 */         if ((c < 'A') || (c > 'P'))
/*  77 */           return null;
/*  78 */         b = (byte)(b << 4 | (byte)(c - 'A'));
/*  79 */         d[(i / 2)] = b;
/*     */       }
/*  81 */       d = BluxBioSecure.aes_decrypt(KEY, d);
/*  82 */       if ((d[10] != 120) || (d[11] != 102) || (d[12] != 117) || (d[13] != 121))
/*  83 */         return null;
/*  84 */       return new Ticket(d[9], d);
/*     */     }
/*     */ 
/*     */     static byte[] makePassPairKey(Ticket ticket) {
/*  88 */       byte[] key = Arrays.copyOf(ticket.data, 16);
/*  89 */       return key;
/*     */     }
/*     */ 
/*     */     static byte[] makeConnectionKey(Ticket ticket, Ticket stub) {
/*  93 */       byte[] key = Arrays.copyOf(ticket.data, 16);
/*  94 */       for (int i = 0; i < 8; i++) {
/*  95 */         key[(i + 8)] = stub.data[i];
/*     */       }
/*  97 */       return key;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void pairResult(BluxBioSecure.Result result, BluxBioSecure.Ticket stub, BluxBioSecure.Ticket ticket)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void connectResult(BluxBioSecure.Result result)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static enum Result
/*     */   {
/*  15 */     SUCCESS, 
/*  16 */     ERROR_USER, 
/*  17 */     ERROR_KEY, 
/*  18 */     ERROR_VERSION;
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxBioSecure
 * JD-Core Version:    0.6.0
 */