package com.eboncorvin.tttp.GameObjects;


import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.O;
import com.eboncorvin.tttp.R;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.BitmapFactory.Options;
import android.util.SparseArray;

public class ImgPreload {
	//private final static boolean checkwidth=false;
	private static BitmapFactory.Options op;
	
	private static int[][] list=new int[][]{	//Image ID, Width per frame, image type
		//For image type: 0=for menu, 1=for game, 2=shared
		new int[]{R.drawable.testtitle,		430,	0},
		new int[]{R.drawable.menuitem,		116,	0},
		new int[]{R.drawable.gametable,		800,	0},
		new int[]{R.drawable.coin,			45,		2},
		
		new int[]{R.drawable.configtable,	500,	0},
		new int[]{R.drawable.aboutpage,		600,	0},	
		new int[]{R.drawable.gamesetting,	500,	0},
		new int[]{R.drawable.xomark,		30,		1},
		
		new int[]{R.drawable.tttboard,		300,	1},
		new int[]{R.drawable.cardmark,		55,		1},
		new int[]{R.drawable.cardbody,		70,		1},
		//new int[]{R.drawable.deck,			60,		1},
		
		new int[]{R.drawable.carpet1,		800,	0},
		new int[]{R.drawable.gamemode,		116,	0},
		new int[]{R.drawable.cardsuit,		30,		1},
		new int[]{R.drawable.empty,			1,		2},
		
		//new int[]{R.drawable.msgblock,		400,	1},
		new int[]{R.drawable.gamebtn,		90,		2},
		new int[]{R.drawable.statustext,	240,	1},
		new int[]{R.drawable.arrow,			33,		1},
		
		new int[]{R.drawable.comparesult,	475,	1},
		new int[]{R.drawable.ingamebg,		800,	1},
		new int[]{R.drawable.help,			100,	1},
		//new int[]{R.drawable.gamehelp,		800,	1},
		
		new int[]{R.drawable.language,		100,	0},
		new int[]{R.drawable.sound,			80,		0},
		new int[]{R.drawable.mw_bg,			5,		2},
		new int[]{R.drawable.sort,			32,		1},
		
		new int[]{R.drawable.testmask,		800,	1},
		
		new int[]{R.drawable.tttp_tutorial,		800,	1},
		
	};
	
	private static SparseArray<Bitmap[]> preload;
	
	public static void PrepareObject(Resources r, int imgtype){
        //800x480 is the baseline resolution of the game
		O.screenW=r.getDisplayMetrics().widthPixels;
        O.scaleW=800f/O.screenW;
		O.screenH=r.getDisplayMetrics().heightPixels;
        O.scaleH=480f/O.screenH;
		op=new Options();
		op.inScaled=false;
		preload=new SparseArray<Bitmap[]>();
		for(int[] item:list){
			if(item[2]!=imgtype && item[2]!=2)
				continue;
			Bitmap bm=BitmapFactory.decodeResource(r,item[0],op);
			if(bm==null)
				throw new IllegalArgumentException("The format of the image file is not vaild!");
			int wpf=item[1];
			int bmw=bm.getWidth();
			/*if(bmw % wpf!=0 && checkwidth)
				throw new IllegalArgumentException("Width of the image ("+count+") is not vaild!");*/
			int bmh=bm.getHeight();
			int tf=bmw/wpf;	//Total Frame
			//preloaded[count]=new Bitmap[tf];
			Bitmap[] tmp=new Bitmap[tf];
			preload.put(item[0], tmp);
			for(int i=0;i<tf;i++){		//Slice and store every piece of frame
				tmp[i]=Bitmap.createBitmap(bm, i*wpf, 0, wpf, bmh);
				//preloaded[count][i]=Bitmap.createBitmap(bm, i*wpf, 0, wpf, bmh);
			}
		}
	}
	
	public static Bitmap[] getObject(int ImageNo){
		/*if(ImageNo>=ImgPreload.preloaded.length || ImageNo<0)
			throw new IllegalArgumentException("There are no such object! "+ImageNo);*/
		if(preload.indexOfKey(ImageNo)<0)
			throw new IllegalArgumentException("There are no such object! "+ImageNo);
		return preload.get(ImageNo);
	}
	
	public static Bitmap getBitmap(int ImageNo, int FrameNo){
		Bitmap[] ret=getObject(ImageNo);
		if(ret.length<=FrameNo || ret.length<0)
			throw new IllegalArgumentException("There are no such frame! "+FrameNo);
		return ret[FrameNo];
	}
	
	public static void unloadImage(){
		if(preload==null)
			return;
		for(int i=0;i<preload.size();i++){
			Bitmap[] tmp=preload.valueAt(i);
			if(tmp==null)
				continue;
			for(int j=0;j<tmp.length;j++){
				tmp[j].recycle();
				tmp[j]=null;
			}
		}
		preload.clear();
		System.gc();
		Misc.output("All preloaded images are unloaded!");
	}
}
