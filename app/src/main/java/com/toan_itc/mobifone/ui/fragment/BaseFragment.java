package com.toan_itc.mobifone.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.toan_itc.mobifone.R;
import com.toan_itc.mobifone.interfaces.OnBackListener;
import com.toan_itc.mobifone.interfaces.ToolbarTitleListener;
import com.toan_itc.mobifone.libs.view.FadeViewAnimProvider;
import com.toan_itc.mobifone.libs.view.StateLayout;
import com.toan_itc.mobifone.mvp.view.base.BaseView;
import com.toan_itc.mobifone.ui.fragment.congno.CongnoFragment;
import com.toan_itc.mobifone.ui.fragment.congno.ListImageFragment;
import com.toan_itc.mobifone.ui.fragment.khoso.KhosoFragment;
import com.toan_itc.mobifone.ui.fragment.km.KhuyenmaiFragment;
import com.toan_itc.mobifone.ui.fragment.thutuc.ThutucFragment;
import com.toan_itc.mobifone.ui.fragment.upanh.UpanhFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by Toan.IT
 * Date:22/5/2016
 */
public abstract class BaseFragment extends Fragment implements OnBackListener,BaseView {
  private View mContentView;
  private Context mContext;
  protected Subscription subscription = Subscriptions.empty();
  private CompositeSubscription mCompositeSubscription;
  private StateLayout mStateLayout = null;
  private Unbinder unbinder;
  private String TAG = getTAG();
  protected abstract String getTAG();
  protected ToolbarTitleListener toolbarTitleListener;
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      toolbarTitleListener = (ToolbarTitleListener) getActivity();
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
              + " must implement OnHeadlineSelectedListener");
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (null == mContentView) {
      mContentView = inflater.inflate(setLayoutResourceID(),container, false);
    }
    return mContentView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, mContentView);
    mContext = getContext();
    initViews();
    initProgress();
    initData();
    setTitle();
  }

  private void setTitle(){
    String currenttag=getFragmentManager().findFragmentById(R.id.fragment).getTag();
    if(currenttag.equalsIgnoreCase(KhosoFragment.class.getName())) {
      toolbarTitleListener.changeTitle(getString(R.string.kho_so));
    }else if(currenttag.equalsIgnoreCase(ThutucFragment.class.getName())) {
      toolbarTitleListener.changeTitle(getString(R.string.thu_tuc));
    }else if(currenttag.equalsIgnoreCase(CongnoFragment.class.getName())) {
      toolbarTitleListener.changeTitle(getString(R.string.cong_no));
    }else if(currenttag.equalsIgnoreCase(KhuyenmaiFragment.class.getName())) {
      toolbarTitleListener.changeTitle(getString(R.string.ct_km));
    }else if(currenttag.equalsIgnoreCase(UpanhFragment.class.getName())) {
      toolbarTitleListener.changeTitle(getString(R.string.up_anh));
    }else{
      toolbarTitleListener.changeTitle(getResources().getString(R.string.app_name));
    }
  }
  protected abstract void initViews();

  protected abstract int setLayoutResourceID();

  protected abstract void initData();

  protected abstract StateLayout getLoadingTargetView();

  protected View getContentView() {
    return mContentView;
  }

  private Context getMContext() {
    return mContext;
  }

  private void initProgress(){
    try {
      if (null != getLoadingTargetView()) {
        mStateLayout = getLoadingTargetView();
        mStateLayout.setViewSwitchAnimProvider(new FadeViewAnimProvider());
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public CompositeSubscription getCompositeSubscription() {
    if (this.mCompositeSubscription == null) {
      this.mCompositeSubscription = new CompositeSubscription();
    }

    return this.mCompositeSubscription;
  }

  public void addSubscription(Subscription s) {
    if (s == null) {
      return;
    }
    if (this.mCompositeSubscription == null) {
      this.mCompositeSubscription = new CompositeSubscription();
    }
    this.mCompositeSubscription.add(s);
  }
  public void replaceFagment(@NonNull FragmentManager fragmentManager, int frameId, @NonNull Fragment fragment){
    checkNotNull(fragmentManager);
    checkNotNull(fragment);
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(frameId, fragment,fragment.getClass().getName());
    transaction.addToBackStack(null);
    transaction.commit();
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unsubscribe();
    Glide.get(mContext).clearMemory();
  }
  protected void toggleShowLoading(boolean toggle) {
    try {
      if (mStateLayout != null) {
        if (toggle)
          mStateLayout.showProgressView();
        else
          mStateLayout.showContentView();
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  protected void toggleShowEmpty(String msg, View.OnClickListener onClickListener) {
    try {
      if (mStateLayout != null) {
        mStateLayout.showEmptyView(msg);
        if (onClickListener != null) {
          mStateLayout.setEmptyAction(onClickListener);
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  protected void toggleShowError(String msg, View.OnClickListener onClickListener) {
    try {
      if (mStateLayout != null) {
        mStateLayout.showErrorView(msg);
        if (onClickListener != null) {
          mStateLayout.setErrorAction(onClickListener);
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  @Override
  public void showEmptyView(String message) {
    toggleShowEmpty(message, null);
  }

  @Override
  public void showEmptyViewAction(String message, View.OnClickListener onClickListener) {
    toggleShowEmpty(message, onClickListener);
  }

  @Override
  public void showError(String message) {
    toggleShowError(message, null);
  }

  @Override
  public void showLoading() {
    toggleShowLoading(true);
  }

  @Override
  public void hideLoading() {
    toggleShowLoading(false);
  }

  protected void checkException(Throwable e) {
    //NetUtils.checkHttpException(getContext(), e, mRootView);
  }

  private void unsubscribe() {
    if (subscription != null) {
      subscription.unsubscribe();
    }
    if (this.mCompositeSubscription != null) {
      this.mCompositeSubscription.unsubscribe();
    }
      /*  RefWatcher refWatcher = BaseApplication.getRefWatcher();
        refWatcher.watch(this);*/
  }
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  @Override
  public void onBackPress() {
    String currenttag=getFragmentManager().findFragmentById(R.id.fragment).getTag();
    _removeWorkerFragments(currenttag);
    if(currenttag.equalsIgnoreCase(KhosoFragment.class.getName())) {
      replaceFagment(getFragmentManager(),R.id.fragment, MainFragment.newInstance());
    }else if(currenttag.equalsIgnoreCase(KhuyenmaiFragment.class.getName())) {
      replaceFagment(getFragmentManager(),R.id.fragment, MainFragment.newInstance());
    }else if(currenttag.equalsIgnoreCase(UpanhFragment.class.getName())) {
      replaceFagment(getFragmentManager(),R.id.fragment, MainFragment.newInstance());
    }else if(currenttag.equalsIgnoreCase(ListImageFragment.class.getName())) {
      replaceFagment(getFragmentManager(),R.id.fragment, CongnoFragment.newInstance());
    }else{
      replaceFagment(getFragmentManager(),R.id.fragment, MainFragment.newInstance());
    }
  }
  private void _removeWorkerFragments(String fragmentTAG) {
    if(!fragmentTAG.equalsIgnoreCase("")) {
      Fragment fragment=getFragmentManager().findFragmentByTag(fragmentTAG);
      if(fragment!=null)
        getFragmentManager().beginTransaction().remove(fragment).commit();
    }
  }
}
