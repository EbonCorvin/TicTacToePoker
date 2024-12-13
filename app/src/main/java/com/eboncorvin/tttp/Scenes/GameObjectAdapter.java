package com.eboncorvin.tttp.Scenes;

import com.eboncorvin.tttp.GameObjects.GameObject;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class GameObjectAdapter extends ArrayList<GameObject>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8438720654913902500L;
	
	@Override
	public boolean add(GameObject item){
		return super.add(item);
	}
	
	public boolean add(int objectID, int interval, int initFrame, int x, int y, int actionCode){
		return add(new GameObject(objectID, interval, initFrame, x, y, actionCode));
	}
	
	public boolean add(int objectID, int x, int y, int actionCode){
		return add(new GameObject(objectID, x, y, actionCode));
	}	
	
	public boolean add(Bitmap bm, int x, int y, int actionCode){
		return add(new GameObject(bm, x, y, actionCode));
	}	
}
