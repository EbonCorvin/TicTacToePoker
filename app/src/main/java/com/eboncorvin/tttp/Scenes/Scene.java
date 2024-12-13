package com.eboncorvin.tttp.Scenes;

import android.graphics.Bitmap;
import com.eboncorvin.tttp.GameObjects.GameObject;

public abstract class Scene extends GameObject {

	public Scene() {
		super(0, 0, 800, 480, 254);
	}

	@Override
	public Bitmap frameUpdate() {
		// TODO Auto-generated method stub
		return updateScene();
	}
	
	protected abstract Bitmap updateScene();
}
