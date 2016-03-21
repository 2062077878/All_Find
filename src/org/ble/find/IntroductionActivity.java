package org.ble.find;

import java.util.ArrayList;
import java.util.List;

import org.ble.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector.OnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class IntroductionActivity extends Activity implements OnGestureListener{

	private int[] imgID= {R.drawable.introduction3, R.drawable.introduction1, R.drawable.introduction2, R.drawable.introduction4};
	private ImageView im_1;
	private ImageView im_2;
	private ImageView im_3;
	private ImageView im_4;
	private List<ImageView> ivs = new ArrayList<ImageView>();
	private ViewFlipper flipper;
	private GestureDetector detector; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.introduction);
		
		im_1 = (ImageView) findViewById(R.id.iv_1);
		im_2 = (ImageView) findViewById(R.id.iv_2);
		im_3 = (ImageView) findViewById(R.id.iv_3);
		im_4 = (ImageView) findViewById(R.id.iv_4);
		
		ivs.add(im_1);
		ivs.add(im_2);
		ivs.add(im_3);
		ivs.add(im_4);
		
		detector = new GestureDetector(this);
		flipper = (ViewFlipper) findViewById(R.id.viewflipper);
		for (int i = 0; i < imgID.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(imgID[i]);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			flipper.addView(imageView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
		ivs.get(0).setEnabled(false);   //false时显示红色球点
	}
	
	private void dotChange(int index){
		for (int i = 0; i < ivs.size(); i++) {
			if (i == index) {
				ivs.get(i).setEnabled(false);
			}else {
				ivs.get(i).setEnabled(true);
			}
		}
		
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 120) {
			// 添加动画
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_left_out));
			View view = flipper.getChildAt(imgID.length-1);
			View view1 = flipper.getCurrentView();
//			flipper.getDisplayedChild();
			
			if (view == view1) {
				Toast.makeText(this, "最后一张", Toast.LENGTH_LONG).show();
				// return false;
			} else {
				this.flipper.showNext();  //向左滑动
				dotChange(flipper.getDisplayedChild());
			}
			return true;
		}// 从右向左滑动
		else if (e1.getX() - e2.getX() < -120) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_right_out));
			if (flipper.getChildAt(0) == flipper.getCurrentView()) {
				Toast.makeText(this, "第一张", Toast.LENGTH_LONG).show();
			} else {
				this.flipper.showPrevious();
				dotChange(flipper.getDisplayedChild());
			}
			
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);   //touch事件交给手势处理。
	}
}

