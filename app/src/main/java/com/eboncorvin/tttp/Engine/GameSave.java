package com.eboncorvin.tttp.Engine;

import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.Abstracts.AbsCardPlacer;
import com.eboncorvin.tttp.Abstracts.AbsCardPool;
import com.eboncorvin.tttp.Abstracts.AbsHandCard;
import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Abstracts.AbstractLayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GameSave {
	private AbsCardPool cardpool;
	private AbsHandCard playerhc;
	private AbsHandCard opphc;
	private AbsCardPlacer placer;
	private AbsHands card2place;
	public boolean fileExist=true;
	private static File datafile;

	public static void setCacheDir(File f){
		datafile=new File(f, "savefile");
	}

	public AbsCardPool getCardpool() {
		return cardpool;
	}

	public AbsHandCard getPlayerhc() {
		return playerhc;
	}

	public AbsHandCard getOpphc() {
		return opphc;
	}

	public AbsCardPlacer getPlacer() {
		return placer;
	}

	public AbsHands getCard2place() {
		return card2place;
	}
	
	/**
	 * Constructor for reading save file <br/>
	 * After created the GameSave object, call the corresponding <br/>
	 * getters to get the saved game object 
	 */
	public GameSave(){
		try {
			FileInputStream fis=new FileInputStream(datafile);
			cardpool=new AbsCardPool(getByteArray(fis, fis.read()));
			//card2place=new AbsHands(getByteArray(fis, fis.read()));	
			playerhc=new AbsHandCard(getByteArray(fis, fis.read()), cardpool);
			opphc=new AbsHandCard(getByteArray(fis, fis.read()), cardpool);
			placer=new AbsCardPlacer(getByteArray(fis, fis.read()));
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Misc.output("File not found! Start a new game");
			fileExist=false;
		}
	}
	
	private byte[] getByteArray(InputStream source, int length) throws IOException{
		try {
			byte[] tmp=new byte[length];
			source.read(tmp);
			return tmp;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 *  A static method that write the current AbstractLayer into a binary file
	 */
	public static void writeGameSave(AbstractLayer al){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			writeArray(baos, al.getCardpool().getByteArray());
			//writeArray(baos, al.getCard2place().getByteArray());
			writeArray(baos, al.getPlayerhc().getByteArray());
			writeArray(baos, al.getOpphc().getByteArray());
			writeArray(baos, al.getPlacer().getByteArray());
			FileOutputStream fos=new FileOutputStream(datafile);
			fos.write(baos.toByteArray());
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void writeArray(ByteArrayOutputStream target, byte[] data) throws IOException{
		target.write(data.length);
		target.write(data);
	}
	
	public static boolean isSaveExist(){
		return datafile.exists();
	}
	
	public static void deleteSave(){
		datafile.delete();
	}
}
