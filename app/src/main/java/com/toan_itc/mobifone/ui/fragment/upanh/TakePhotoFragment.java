package com.toan_itc.mobifone.ui.fragment.upanh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.toan_itc.mobifone.R;
import com.toan_itc.mobifone.libs.takephoto.library.app.TakePhoto;
import com.toan_itc.mobifone.libs.takephoto.library.app.TakePhotoImpl;
import com.toan_itc.mobifone.libs.takephoto.library.model.InvokeParam;
import com.toan_itc.mobifone.libs.takephoto.library.model.TContextWrap;
import com.toan_itc.mobifone.libs.takephoto.library.model.TResult;
import com.toan_itc.mobifone.libs.takephoto.library.permission.InvokeListener;
import com.toan_itc.mobifone.libs.takephoto.library.permission.PermissionManager;
import com.toan_itc.mobifone.libs.takephoto.library.permission.TakePhotoInvocationHandler;
import com.toan_itc.mobifone.libs.view.StateLayout;
import com.toan_itc.mobifone.ui.fragment.BaseFragment;

/**
 * Toan.IT
 * Created by vantoan on 3/28/17.
 * Email: Huynhvantoan.itc@gmail.com
 */

public class TakePhotoFragment extends BaseFragment implements TakePhoto.TakeResultListener,InvokeListener {
  private static final String TAG = TakePhotoFragment.class.getName();
  private InvokeParam invokeParam;
  private TakePhoto takePhoto;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    getTakePhoto().onCreate(savedInstanceState);
    super.onCreate(savedInstanceState);
  }
  @Override
  public void onSaveInstanceState(Bundle outState) {
    getTakePhoto().onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    getTakePhoto().onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    PermissionManager.handlePermissionsResult(getActivity(),type,invokeParam,this);
  }
  /**
   *  获取TakePhoto实例
   * @return
   */
  public TakePhoto getTakePhoto(){
    if (takePhoto==null){
      takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
    }
    return takePhoto;
  }
  @Override
  public void takeSuccess(TResult result) {
    Log.i(TAG,"takeSuccess：" + result.getImage().getCompressPath());
  }
  @Override
  public void takeFail(TResult result,String msg) {
    Log.i(TAG, "takeFail:" + msg);
  }
  @Override
  public void takeCancel() {
    Log.i(TAG, getResources().getString(R.string.msg_operation_canceled));
  }
  @Override
  public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
    PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
    if(PermissionManager.TPermissionType.WAIT.equals(type)){
      this.invokeParam=invokeParam;
    }
    return type;
  }

  @Override
  protected String getTAG() {
    return null;
  }

  @Override
  protected void initViews() {

  }

  @Override
  protected int setLayoutResourceID() {
    return 0;
  }

  @Override
  protected void initData() {

  }

  @Override
  protected StateLayout getLoadingTargetView() {
    return null;
  }
}
