/*    */ package com.xiaofu_yan.blux.le.server;
/*    */ 
/*    */ import java.util.UUID;
/*    */ 
/*    */ class BluxVDGeneric extends BluxVirtualDevice
/*    */ {
/*    */   Delegate delegate;
/*    */ 
/*    */   static boolean isKindOf(UUID uuidType)
/*    */   {
/* 18 */     return true;
/*    */   }
/*    */ 
/*    */   BluxVDGeneric(BluxServiceVirtualDevice service, BluxVirtualDevice.Descriptor desc)
/*    */   {
/* 23 */     super(service, desc);
/*    */   }
/*    */ 
/*    */   protected void serviceStateChange(BluxServiceVirtualDevice.State state)
/*    */   {
/*    */   }
/*    */ 
/*    */   protected void didReadRegister(Object id, int address, boolean success, byte[] data)
/*    */   {
/* 32 */     if (this.delegate != null)
/* 33 */       this.delegate.didRead(id, address, success, data);
/*    */   }
/*    */ 
/*    */   protected void didWriteRegister(Object id, int address, boolean success)
/*    */   {
/* 39 */     if (this.delegate != null)
/* 40 */       this.delegate.didWrite(id, address, success);
/*    */   }
/*    */ 
/*    */   protected void irq(byte[] data)
/*    */   {
/* 46 */     if (this.delegate != null)
/* 47 */       this.delegate.irq(data);
/*    */   }
/*    */ 
/*    */   static class Delegate
/*    */   {
/*    */     void didRead(Object id, int address, boolean success, byte[] data)
/*    */     {
/*    */     }
/*    */ 
/*    */     void didWrite(Object id, int address, boolean success)
/*    */     {
/*    */     }
/*    */ 
/*    */     void irq(byte[] data)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxVDGeneric
 * JD-Core Version:    0.6.0
 */