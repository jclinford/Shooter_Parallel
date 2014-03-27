package com.shooter;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.sjsu.physics.shapes.Circle;
import com.sjsu.physics.shapes.PolyBody;
import com.sjsu.physics.shapes.RigidBody;
import com.sjsu.physics.utils.Vector2;

import android.util.Log;


/**
 * 
 * @author John Linford
 * 
 *	Power up class can create three different power ups
 *	depending on the type parameter 
 *	0 = kill all enemies
 *	1 = heal player
 *	2 = temp immune
 *
 */
public class PowerUp extends Sprite implements GameObject
{
	private int HEALTH = 1;
	private static final int DAMAGE = 10;

	private int type;

	private ShooterActivity activity;
	private Circle body;


	public PowerUp(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,
			final ShooterActivity a, final int vx, final int vy, final int t)
	{
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		activity = a;
		type = t;

		Vector2 loc = new Vector2(this.getX(), this.getY());
		body = new Circle(loc, 10);

		// rotate body to face north          
//		this.setRotation((float)(3.14 / 2));
		body.setVelocity(vx * 5, vy *5);
		body.setMass(10);

		setUserData(body);
	}

	@Override
	public void onManagedUpdate(final float pSecondsElapsed) 
	{
		this.setPosition(body.center().x() + (.1f), 
				body.center().y());
		body.setCenter(this.mX, this.mY);
		
		// if we go out of bounds set health to -1 so it is removed next game loop
		if (this.getX() > activity.CAMERA_WIDTH)
		{
			this.dealDamage(HEALTH + 1);
			return;
		}

		// check for collisions with player, and take appropriate action depending on type of powerup
		if (this.collidesWith(activity.getPlayer()))
		{
			Log.i("Shooter", "Power up collected");

			// 0 = lightning = kill all enemies
			if (type == 0)
			{
				for (int i = 0; i < activity.getScene().getChildCount(); i++)
				{
					try
					{
						GameObject obj = (GameObject) activity.getScene().getChildByIndex(i);

						// deal damage to all obj's to remove all
						if (obj.getClass() != Player.class && obj != this && obj.getClass() != Boss.class && obj.getClass() != Projectile.class)
							obj.dealDamage(obj.getHealth());
					}
					catch (Exception e){}
				}
				
				this.dealDamage(HEALTH + 1);
			}
			// 1 = wrench = heal
			else if (type == 1)
			{
				// deal negative damage to heal player, then destroy the object
				activity.getPlayer().dealDamage(-10);
				this.dealDamage(HEALTH + 1);
			}
			// 2 = shield = invisible/immunity
			else if (type == 2)
			{
				activity.getPlayer().setImmune(true);
				this.dealDamage(HEALTH + 1);
			}
		}
	}


	//---------------------//
	// Getters and Setters //
	//---------------------//
	public void dealDamage(int damage)
	{
		HEALTH -= damage;
	}
	public int getDamage()
	{
		return DAMAGE;
	}
	public int getHealth() 
	{
		return HEALTH;
	}

	public RigidBody getBody() 
	{
		return body;
	}
}
