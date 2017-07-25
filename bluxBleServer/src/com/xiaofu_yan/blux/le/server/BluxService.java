/*    */ package com.xiaofu_yan.blux.le.server;
/*    */ 
/*    */ import android.bluetooth.BluetoothGatt;
/*    */ import android.bluetooth.BluetoothGattCharacteristic;
/*    */ import android.bluetooth.BluetoothGattService;
/*    */ import java.util.UUID;
/*    */ 
/*    */ class BluxService extends BluxObject
/*    */ {
/*    */   Delegate delegate;
/*    */   UUID mBtServiceUUID;
/*    */   BluetoothGatt mBtGatt;
/*    */   BluetoothGattService mBtService;
/*    */   BluxPeripheral mPeripheral;
/*    */ 
/*    */   protected void terminate()
/*    */   {
/* 31 */     this.mBtServiceUUID = null;
/* 32 */     this.mBtGatt = null;
/* 33 */     this.mBtService = null;
/* 34 */     this.mPeripheral = null;
/* 35 */     super.terminate();
/*    */   }
/*    */ 
/*    */   UUID btServiceUUID() {
/* 39 */     return this.mBtServiceUUID;
/*    */   }
/*    */ 
/*    */   protected void onAttached(BluxPeripheral peripheral)
/*    */   {
/* 45 */     this.mPeripheral = peripheral;
/*    */   }
/*    */ 
/*    */   protected void onDetached() {
/* 49 */     this.mPeripheral = null;
/* 50 */     this.mBtGatt = null;
/* 51 */     this.mBtService = null;
/*    */   }
/*    */ 
/*    */   protected void onConnected(BluetoothGatt btGatt, BluetoothGattService btService) {
/* 55 */     this.mBtGatt = btGatt;
/* 56 */     this.mBtService = btService;
/*    */   }
/*    */ 
/*    */   protected void onDisconnected() {
/* 60 */     this.mBtGatt = null;
/* 61 */     this.mBtService = null;
/*    */   }
/*    */ 
/*    */   protected void onCharacteristicChanged(BluetoothGattCharacteristic ch)
/*    */   {
/*    */   }
/*    */ 
/*    */   static class Delegate
/*    */   {
/*    */     void serviceStarted(BluxService service, boolean success)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxService
 * JD-Core Version:    0.6.0
 */