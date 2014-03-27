package com.shooter;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;

import com.sjsu.physics.utils.Vector2;

import android.util.Log;

/**
 * 
 * @author John Linford
 * 
 *	Handles all controls including
 *	accelerometer and touch 
 *
 */
public class Controls implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener, IOnMenuItemClickListener
{
	protected static final int MENU_RESET = 0;
	protected static final int MENU_QUIT = MENU_RESET + 1;
	protected static final int MENU_OK = MENU_QUIT + 1;
	protected static final int MENU_NEXT_LEVEL = MENU_OK + 1;

	private ShooterActivity activity;

	public Controls(ShooterActivity a)
	{
		activity = a;
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {}

	// player will fire projectile at player location when screen is touched
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{		
		//Log.i("Shooter", "Player shoot");
		// only shoot when user puts finger down
		if (pSceneTouchEvent.isActionDown())
		{
			float x = activity.getPlayer().getX();
			float y = activity.getPlayer().getY();

			Projectile proj = new Projectile(x, y - 33, activity.playerProjRegion, activity.getVertexBufferObjectManager(), activity, -15, 0, true);
			Projectile proj2 = new Projectile(x, y + 10, activity.playerProjRegion, activity.getVertexBufferObjectManager(), activity, -15, 0, true);
			activity.getScene().attachChild(proj);
			activity.getScene().attachChild(proj2);
			return true;
		}
		return false;
	}

	// if acellerometer is changed, set the new velocity to our player
	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) 
	{
		// get new velocity
		final Vector2 velocity = new Vector2(pAccelerationData.getX() * 15, pAccelerationData.getY() * 15);

		// update activity and physics world and recycle
		activity.getPlayer().getBody().setVelocity(velocity);	
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) 
	{
		return false;
	}

	// menu click listeners for all our menus
	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) 
	{
		switch(pMenuItem.getID()) 
		{
		case MENU_RESET:
			// reset game
			activity.reset();
			return true;
		case MENU_QUIT:
			// restart activity and exit on quit press
			activity.getScene().reset();
			activity.getScene().clearChildScene();
			activity.reset();
			activity.finish();
			return true;
		case MENU_OK:
			// restart activity on game over
			activity.getScene().reset();
			activity.getScene().clearChildScene();
			activity.reset();
			return true;
		case MENU_NEXT_LEVEL:
			// load level 2
			Log.i("Driver", "Loading next level");
			activity.loadNextLevel();
		default:
			return false;
		}
	}
}
