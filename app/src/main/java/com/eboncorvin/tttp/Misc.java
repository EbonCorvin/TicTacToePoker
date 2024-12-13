package com.eboncorvin.tttp;

import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.GameObjects.ImgPreload;
import com.eboncorvin.tttp.GameObjects.ImgPremake;

import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Misc {
	//This class contains some method or field which may needed in multiclass

	
	/**
	 * Sort a Interger Array, From 1~10 if Asc=true, and From 10~1 if false
	 * @param target
	 * @param isAsc
	 * @return
	 */
	public static int[] sortIntArray(int[] target, boolean isAsc){
		if(target==null)
			throw new NullPointerException("The array given points to NULL!");
		if(isAsc){
			Arrays.sort(target);
			return target;
		}
		int i=target.length, temp=0;
		while(i>0){
			for(int j=0;j<i-1;j++){
				if((isAsc && target[j]>target[j+1]) ||
					(!isAsc && target[j]<target[j+1])){
					temp=target[j];
					target[j]=target[j+1];
					target[j+1]=temp;
				}
			}
			i--;
		}
		return target;
	}

	/**
	 * Create a bitmap that would not automatically scaled by Android
	 */
	public static Bitmap getUnscaledBmp(int width, int height, Bitmap.Config config){
		Bitmap bm=Bitmap.createBitmap(width, height, config);
		bm.setDensity(Bitmap.DENSITY_NONE);
		return bm;
	}
	
	public static Bitmap[] getObject(int Number){
		return ImgPreload.getObject(Number);
	}

	public static Bitmap getBitmap(int Number, int FrameNo){
		return ImgPreload.getBitmap(Number, FrameNo);
	}
	
	public static Bitmap makeHandsImg(AbsHands Hand){
		Bitmap newbmp=getUnscaledBmp(190, 96, Bitmap.Config.ARGB_8888);
		Canvas c=new Canvas(newbmp);
		int count=0;
		for(int i:Hand.getHands()){
			c.drawBitmap(ImgPremake.getCard(i), count*30, 0, ImgPremake.ptr);
			count++;
		}
		return newbmp;
	}
	
	public static void output(Object content){
		StackTraceElement ste=Thread.currentThread().getStackTrace()[3];
		System.out.println(ste.getFileName()+"-"
				+ste.getMethodName()+":"+ste.getLineNumber() +" - "+content);
	}
}
