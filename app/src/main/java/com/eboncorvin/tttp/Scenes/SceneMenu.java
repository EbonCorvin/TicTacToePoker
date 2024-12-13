package com.eboncorvin.tttp.Scenes;

import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.O.C;
import android.graphics.Bitmap;

public class SceneMenu extends Scene {

	private GameObjectAdapter objlist=new GameObjectAdapter();
	
	public SceneMenu() {
		super();
		// TODO Auto-generated constructor stub
		objlist.add(R.drawable.testtitle, 		-1,	0, 185, 81, -1);
		objlist.add(R.drawable.menuitem, 	-1, 3, 400, 150, C.Stat); //Stat
		objlist.add(R.drawable.menuitem, 	-1, 6, 550, 200, C.About); //About
		objlist.add(R.drawable.menuitem, 	-1,	0, 100, 150, C.Start); //Start
		objlist.add(R.drawable.menuitem, 	-1, 2, 250, 200, C.Option); //Option
		objlist.add(R.drawable.language,	-1, 0, 650, 80, 76); 
	}

	@Override
	protected Bitmap updateScene() {
		// TODO Auto-generated method stub
		return null;
	}

}
