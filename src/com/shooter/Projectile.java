package com.shooter;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.sjsu.physics.collisiondetection.FineCollision;
import com.sjsu.physics.core.Contact;
import com.sjsu.physics.shapes.Circle;
import com.sjsu.physics.shapes.PolyBody;
import com.sjsu.physics.shapes.Polygon;
import com.sjsu.physics.shapes.RigidBody;
import com.sjsu.physics.utils.Vector2;

import android.util.Log;


/**
 * 
 * @author John Linford
 * 
 * Projectile (laser beams) class that damages enemies
 * or the player on contact
 *
 */
public class Projectile extends Sprite implements GameObject
{
	private int HEALTH = 10;
	private static final int DAMAGE = 10;

	private ShooterActivity activity;
	private Circle body;
	private boolean isPlayerProjectile;

	public Projectile(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,
			final ShooterActivity a, final int vx, final int vy, final boolean playerProjectile)
	{
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		activity = a;
		isPlayerProjectile = playerProjectile;
		
		this.setRotation(90);

		body = new Circle(pX, pY, 15);
		body.setVelocity(vx * 5, vy * 5);
		body.setMass(5);
		
		setUserData(body);
		
//		body.rotateBy((float)Math.PI / 2);
//		this.setRotationCenter(body.center().x(), body.center().y());
//		this.setRotation(100);
		
		// add to world
		a.getPhysicsWorld().addBodyToWorld(body);
	}

	@Override
	public void onManagedUpdate(final float pSecondsElapsed) 
	{
		float x = body.center().x();
		float y = body.center().y();
		
		this.setPosition(x, y);
		
		// if we go out of bounds set health to -1 so it is removed next game loop without explosion
		if (this.getX() > activity.CAMERA_WIDTH || this.getX() < 0 || body == null ||
				this.getY() > activity.CAMERA_HEIGHT || this.getY() < 0)
		{
			this.dealDamage(HEALTH + 1);
			return;
		}

		// if its the player's projectile, check for collisions with enemies
		if (isPlayerProjectile)
		{
			// if we have boss only need to check collisions w/ boss
			if (activity.hasBoss)
			{
				Boss boss = activity.boss;
				if (boss.collidesWith(this))
				{
					Log.i("Shooter", "Proj-Boss collision");
					boss.dealDamage(DAMAGE);
					this.dealDamage(HEALTH + 1);
				}
			}
			else
			{
				// otherwise, check if it collides with all active enemies
				for (int i = 0; i < activity.getScene().getChildCount(); i++)
				{
					try
					{
						Enemy obj = (Enemy) activity.getScene().getChildByIndex(i);

						// if we collide, deal it's damage
						if (obj.collidesWith(this))
						{
							Log.i("Shooter", "Proj-Enemy collision");
							obj.dealDamage(DAMAGE / 10);
							
							// create a collision
							Contact c = FineCollision.getContactPoints(this.getBody(), obj.getBody());
							if (c != null)
							{
								// set laser beam's health to -1 so it is removed on next game loop without explsn
								this.dealDamage(HEALTH + 1);
								activity.getPhysicsWorld().addContact(c);
							}
						}
						continue;
					}
					catch (Exception e){}
					try
					{
						Meteor obj = (Meteor) activity.getScene().getChildByIndex(i);

						// if we collide, deal it's damage
						if (obj.collidesWith(this))
						{
							Log.i("Shooter", "Proj-Enemy collision");
							obj.dealDamage(DAMAGE / 10);
							
							// create a collision
							Contact c = FineCollision.getContactPoints(this.getBody(), obj.getBody());
							if (c != null)
							{
								// set laser beam's health to -1 so it is removed on next game loop without explsn
								this.dealDamage(HEALTH + 1);
								activity.getPhysicsWorld().addContact(c);
							}
						}
						continue;
					}
					catch (Exception e){}
				}
			}
		}

		// Otherwise it is the enemy's projectile, so check for collisions with player
		else if (this.collidesWith(activity.getPlayer()) && !activity.getPlayer().getImmune())
		{
			Log.i("Shooter", "Player-Proj collision");

			// deal half dmg to player
			activity.getPlayer().dealDamage(DAMAGE / 2);

			// set health to -1 so it is removed on next game loop
			HEALTH = -1;
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
