/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/utsumiisao/Documents/workspace/Lost_Property_Prevention_v1.10_For_Git_Hub/src/jp/isao/lost_property_prevention/GetAwayHomeBindService.aidl
 */
package jp.isao.lost_property_prevention;
public interface GetAwayHomeBindService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements jp.isao.lost_property_prevention.GetAwayHomeBindService
{
private static final java.lang.String DESCRIPTOR = "jp.isao.lost_property_prevention.GetAwayHomeBindService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an jp.isao.lost_property_prevention.GetAwayHomeBindService interface,
 * generating a proxy if needed.
 */
public static jp.isao.lost_property_prevention.GetAwayHomeBindService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof jp.isao.lost_property_prevention.GetAwayHomeBindService))) {
return ((jp.isao.lost_property_prevention.GetAwayHomeBindService)iin);
}
return new jp.isao.lost_property_prevention.GetAwayHomeBindService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_stopGpsFunction:
{
data.enforceInterface(DESCRIPTOR);
this.stopGpsFunction();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements jp.isao.lost_property_prevention.GetAwayHomeBindService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void stopGpsFunction() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopGpsFunction, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_stopGpsFunction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void stopGpsFunction() throws android.os.RemoteException;
}
