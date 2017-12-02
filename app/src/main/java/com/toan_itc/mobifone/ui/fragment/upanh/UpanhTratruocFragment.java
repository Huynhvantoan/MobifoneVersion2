package com.toan_itc.mobifone.ui.fragment.upanh;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.clans.fab.FloatingActionButton;
import com.toan_itc.mobifone.R;
import com.toan_itc.mobifone.intdef.BitmapDef;
import com.toan_itc.mobifone.intdef.StringDef;
import com.toan_itc.mobifone.libs.logger.Logger;
import com.toan_itc.mobifone.libs.takephoto.library.app.TakePhoto;
import com.toan_itc.mobifone.libs.takephoto.library.compress.CompressConfig;
import com.toan_itc.mobifone.libs.takephoto.library.model.CropOptions;
import com.toan_itc.mobifone.libs.takephoto.library.model.TResult;
import com.toan_itc.mobifone.libs.takephoto.library.model.TakePhotoOptions;
import com.toan_itc.mobifone.libs.view.StateLayout;
import com.toan_itc.mobifone.mvp.presenter.upanh.UpanhPresenter;
import com.toan_itc.mobifone.mvp.view.upanh.UpanhView;
import com.toan_itc.mobifone.ui.activity.BaseActivity;
import com.toan_itc.mobifone.ui.fragment.login.LoginFragment;
import com.toan_itc.mobifone.utils.FileUtils;
import java.io.File;
import javax.inject.Inject;

import static com.toan_itc.mobifone.utils.ImageUtils.loadImageView;

public class UpanhTratruocFragment extends TakePhotoFragment implements UpanhView{
  @Inject
  UpanhPresenter
  mUpanhPresenter;
  private Context mContext;
  @BindView(R.id.coordinatorlayout)
  CoordinatorLayout mCoordinatorlayout;
  @BindView(R.id.img_cmnd_mattruoc)
  ImageView imgCmndMattruoc;
  @BindView(R.id.img_cmnd_matsau)
  ImageView imgCmndMatsau;
  @BindView(R.id.img_phuluc)
  ImageView imgPhuluc;
  @BindView(R.id.fab)
  FloatingActionButton mFloatingActionButton;
  private File requestBody=null,requestBody1=null,requestBody2=null;
  private TakePhoto mTakePhoto;
  private Uri imageUri;
  private int index=0;
  public static UpanhTratruocFragment newInstance(int type) {
    UpanhTratruocFragment fra = new UpanhTratruocFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(StringDef.BUNDLE_DATA, type);
    fra.setArguments(bundle);
    return fra;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mContext=context;
  }

  @Override
  protected String getTAG() {
    return this.getClass().getSimpleName();
  }

  @Override
  protected void initViews() {
    ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    mUpanhPresenter.attachView(this);
  }

  @Override
  protected int setLayoutResourceID() {
    return R.layout.upanh_tratruoc_fragment;
  }

  @Override
  protected void initData() {
    mTakePhoto=getTakePhoto();
  }

  @Override
  protected StateLayout getLoadingTargetView() {
     return ButterKnife.findById(getActivity(),R.id.stateLayout);
  }

  @OnClick(R.id.fab)
  public void btn_upload(){
    try {
      if (mUpanhPresenter.getmPreferencesHelper().getJsonInfo().getPhone() != null
          && mUpanhPresenter.getmPreferencesHelper().getJsonInfo().getDichvu() != null)
        mUpanhPresenter.uploadTratruoc(mUpanhPresenter.getmPreferencesHelper().getJsonInfo().getPhone(),
            mUpanhPresenter.getmPreferencesHelper().getJsonInfo().getDichvu(), requestBody,
            requestBody1, requestBody2);
      else
        Snackbar.make(mCoordinatorlayout, "Vui lòng chọn số trước khi sử dụng chức năng!", Snackbar.LENGTH_LONG).show();
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  @OnClick(R.id.btn_cmnd_mattruoc)
  public void btn_cmnd_mattruoc(){
    Dialog_Update_avatar(BitmapDef.CMND1);
  }
  @OnClick(R.id.btn_cmnd_matsau)
  public void btn_cmnd_matsau(){
    Dialog_Update_avatar(BitmapDef.CMND2);
  }
  @OnClick(R.id.btn_phuluc)
  public void btn_phuluc(){
    Dialog_Update_avatar(BitmapDef.PL4);
  }

  private void Dialog_Update_avatar(int index) {
    final Dialog dialog = new Dialog(mContext);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.layout_dialog_avatar);
    Window window = dialog.getWindow();
    WindowManager.LayoutParams wlp = window.getAttributes();
    wlp.gravity = Gravity.BOTTOM;
    window.setAttributes(wlp);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    Button camera = (Button) dialog.findViewById(R.id.camera);
    Button thuvien = (Button) dialog.findViewById(R.id.thuvien);
    Button huy = (Button) dialog.findViewById(R.id.huy);
    camera.setOnClickListener(v -> {
      dialog.dismiss();
      this.index=index;
      initTakePhoto();
      mTakePhoto.onPickFromCaptureWithCrop(imageUri,getCropOptions());
    });
    thuvien.setOnClickListener(v -> {
      dialog.dismiss();
      this.index=index;
      initTakePhoto();
      mTakePhoto.onPickFromGalleryWithCrop(imageUri,getCropOptions());
    });
    huy.setOnClickListener(v -> dialog.dismiss());
    dialog.show();
  }

  private void initTakePhoto(){
    File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
    if (!file.getParentFile().exists())file.getParentFile().mkdirs();
    imageUri = Uri.fromFile(file);
    configCompress(mTakePhoto);
    TakePhotoOptions.Builder builder=new TakePhotoOptions.Builder();
    builder.setWithOwnGallery(true);
    mTakePhoto.setTakePhotoOptions(builder.create());
  }
  private void configCompress(TakePhoto takePhoto){
    CompressConfig config=new CompressConfig.Builder()
            .setMaxSize(102400)
            .setMaxPixel(800)
            .enableReserveRaw(false)
            .create();
    takePhoto.onEnableCompress(config,true);
  }
  private CropOptions getCropOptions(){
    CropOptions.Builder builder=new CropOptions.Builder();
    builder.setOutputX(800).setOutputY(600);
    builder.setWithOwnCrop(true);
    return builder.create();
  }
  private void returnData(String image){
    switch (index){
      case BitmapDef.CMND1:
        try {
          loadImageView(mContext,image,imgCmndMattruoc);
          requestBody = FileUtils.getFileByPath(image);
        }catch (Exception e){
          e.printStackTrace();
        }
        break;
      case BitmapDef.CMND2:
        try {
          loadImageView(mContext,image,imgCmndMatsau);
          requestBody1 = FileUtils.getFileByPath(image);
        }catch (Exception e){
          e.printStackTrace();
        }
        break;
      case BitmapDef.PL4:
        try {
          loadImageView(mContext,image,imgPhuluc);
          requestBody2 = FileUtils.getFileByPath(image);
        }catch (Exception e){
          e.printStackTrace();
        }
        break;
    }
    if(showFab())
      mFloatingActionButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void uploadProgress(int progress) {
    mFloatingActionButton.setProgress(progress,true);
  }

  @Override
  public void uploadOK() {
    Logger.e("uploadOK");
    Snackbar.make(mCoordinatorlayout, "Upload thành công!", Snackbar.LENGTH_LONG).show();
    subscription.unsubscribe();
    mFloatingActionButton.setProgress(100,true);
  }

  @Override
  public void uploadFail() {
    Logger.e("uploadFail");
    Snackbar.make(mCoordinatorlayout, "Upload bị lỗi!", Snackbar.LENGTH_LONG).show();
  }
  private boolean showFab(){
    return requestBody!= null && requestBody1 != null && requestBody2 != null;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUpanhPresenter.detachView();
  }

  @Override
  public void takeSuccess(TResult result) {
    super.takeSuccess(result);
    returnData(result.getImage().getCompressPath());
  }

  @Override public void requestLogin() {
    replaceFagment(getFragmentManager(), R.id.fragment, LoginFragment.newInstance());
  }
}

