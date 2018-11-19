package com.guyu.android.gis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.database.task.CaseImgDBMExternal;
import com.guyu.android.database.task.CaseSoundDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CaseImg;
import com.guyu.android.utils.BitmapUtils;
import com.guyu.android.utils.UtilsTools;
import com.guyu.android.R;

public class ImgViewActivity extends Activity {
	private View.OnClickListener mClickListener = null;
	private ImageView mImageView = null;
	private Button mNext = null;
	private Button mPage = null;
	private Button mPrv = null;
	private boolean m_bIsDoubleClick = false;
	private int m_nCurrentShowIndex = 0;
	private int m_nImgIndex = 0;
	private String mCaseID = null;
	private CaseImgDBMExternal mCaseImgManager = null;
	private List<CaseImg> mAllCaseImgObjs;
	private Matrix matrix = new Matrix();
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_img_show);


		initActivity();
	}

	private void initActivity() {
		Intent localIntent = getIntent();
		this.mCaseID = getIntent().getStringExtra("caseId");
		this.mCaseImgManager = new CaseImgDBMExternal(this);
		this.mAllCaseImgObjs = this.mCaseImgManager.getTheCaseAllImg(this.mCaseID);
		if ((mAllCaseImgObjs != null) && (mAllCaseImgObjs.size() > 0)) {
			this.mClickListener = new MOnClickListener();
			initWidget();
			refreshBtnState();
			showImg(0);
		} else {
			NoImgRecordDlg();
		}

	}

	private void initWidget() {
		this.mPrv = ((Button) findViewById(R.id.prv_img));
		this.mPrv.setOnClickListener(this.mClickListener);
		this.mNext = ((Button) findViewById(R.id.next_img));
		this.mNext.setOnClickListener(this.mClickListener);
		this.mPage = ((Button) findViewById(R.id.img_page));
		this.mImageView = ((ImageView) findViewById(R.id.img_show));
        Button btn = ((Button) findViewById(R.id.btnBackup));
        btn.setOnClickListener(mClickListener);
		this.mImageView.setOnTouchListener(new MTouchListener());
		if (this.mAllCaseImgObjs.size() > 1) {
			findViewById(R.id.ll_pic_select_bar).setVisibility(View.VISIBLE);
		}
	}

	private void refreshBtnState() {
		int imgCount = this.mAllCaseImgObjs.size();
		if (imgCount <= 1) {
			this.mNext.setEnabled(false);
			this.mPrv.setEnabled(false);
			this.mPage.setText(imgCount + "/" + imgCount);

		} else {

			if (this.m_nCurrentShowIndex == 0) {
				this.mNext.setEnabled(true);
				this.mPrv.setEnabled(false);
			} else if (1 + this.m_nCurrentShowIndex == imgCount) {
				this.mNext.setEnabled(false);
				this.mPrv.setEnabled(true);
			} else {
				this.mNext.setEnabled(true);
				this.mPrv.setEnabled(true);
			}
		}
		this.mPage.setText((1 + this.m_nCurrentShowIndex) + "/" + imgCount);

	}

	private void showImg(int paramInt) {
		if ((paramInt > -1) && (this.mAllCaseImgObjs.size() > paramInt)) {
			String str = this.mAllCaseImgObjs.get(paramInt).getImgPath();
			if (new File(str).exists()) {
				Display localDisplay = getWindowManager().getDefaultDisplay();
				try {
					Bitmap localBitmap = BitmapUtils.getBitmapByPath(str, localDisplay.getWidth(), localDisplay.getHeight());
					if (localBitmap != null) {
						this.mImageView.setImageBitmap(localBitmap);						
						DisplayMetrics metric = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metric);
						int mWidth = metric.widthPixels; // 屏幕宽度（像素）
						int mHeight = metric.heightPixels; // 屏幕高度（像素）
						matrix = new Matrix();
						matrix.postTranslate((mWidth - localBitmap.getWidth()) / 2, (mHeight - localBitmap.getHeight()) / 2);
						this.mImageView.setImageMatrix(matrix);
					}
					this.m_nCurrentShowIndex = paramInt;
					refreshBtnState();
				} catch (FileNotFoundException localFileNotFoundException) {
					localFileNotFoundException.printStackTrace();
				}
			} else {
				ToastUtils.showLong(  "图片已删除或者不存在");
			}
		} else {
			Log.e("changchun", "m_nDownloadIndex:不符合要求");
			ToastUtils.showLong(  "未找到相关图片");
		}

	}

	private void NoImgRecordDlg() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder.setTitle("提示");
		localBuilder.setPositiveButton("返回上一级", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				ImgViewActivity.this.finish();
			}
		});
		localBuilder.setMessage("没有相关图片记录");
		localBuilder.show();
	}

	private class MOnClickListener implements View.OnClickListener {
		public void onClick(View paramView) {
			switch (paramView.getId()) {

			case R.id.prv_img:
				int j = -1 + ImgViewActivity.this.m_nImgIndex;
				ImgViewActivity.this.m_nImgIndex = j;
				ImgViewActivity.this.showImg(j);
				break;
			case R.id.next_img:
				int i = 1 + ImgViewActivity.this.m_nImgIndex;
				ImgViewActivity.this.m_nImgIndex = i;
				ImgViewActivity.this.showImg(i);
				break;
			case R.id.img_page:
			    break;
                case R.id.btnBackup:
                    finish();
                    break;
			default:
				return;
			}
		}

	}

	public class MTouchListener implements View.OnTouchListener {
		static final int DRAG = 1;
		static final int NONE = 0;
		static final int ZOOM = 2;		
		PointF mid = new PointF();
		int mode = 0;
		float oldDist = 1.0F;
		Matrix savedMatrix = new Matrix();
		PointF start = new PointF();

		// 计算移动距离
		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return (float) Math.sqrt(x * x + y * y);
		}

		// 计算中点位置
		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}

		public boolean onTouch(View paramView, MotionEvent event) {
			ImageView view = (ImageView) paramView;

			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 设置拖拉模式
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				mode = DRAG;
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			// 设置多点触摸模式
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;
			// 若为DRAG模式，则点击移动图片
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					// 设置位移
					matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
				}
				// 若为ZOOM模式，则多点触摸缩放
				else if (mode == ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						// 设置缩放比例和图片中点位置
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}

			// Perform the transformation
			view.setImageMatrix(matrix);
			return true;

		}
	}
}