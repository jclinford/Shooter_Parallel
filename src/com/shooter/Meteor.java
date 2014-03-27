package com.shooter;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.sjsu.physics.shapes.Circle;
import com.sjsu.physics.shapes.RigidBody;
import com.sjsu.physics.utils.Vector2;

import android.util.Log;
/**
 * 
 * @author John Linford
 * 
 *	Enemy class that will 'attack' the player
 *	and player can destroy them by hitting them (thus taking dmg)
 *	or by shooting them..
 *
 */
public class Meteor extends Sprite implements GameObject
{
	private int HEALTH;
	private int DAMAGE;
	private int VELOCITYX;

	private static double totalGameTime = 0;
	private double previousShotTime = 0;

	private ShooterActivity activity;
	private Circle body;

	public Meteor(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,
			final ShooterActivity a, final int vx, final int h, final int d, final double r)
	{
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		activity = a;

		HEALTH = h;
		DAMAGE = d;
		VELOCITYX = vx;

		int height = (int) this.getHeight() / 2;
		
		Vector2 loc = new Vector2(pX, pY);
		body = new Circle(loc, height);
		body.setVelocity(vx * 5, 0);
		body.setMass(400);
		body.setAcceleration(new Vector2(5, 0));
//		body.setAngularDamping(1f);
		body.setAngularVelocity(.1f);
		
		setUserData(body);
		
//		body.rotateBy((float)Math.PI / 2);
//		this.setRotationCenter(body.center().x(), body.center().y());
//		this.setRotation(100);
		
		// add to world
		a.getPhysicsWorld().addBodyToWorld(body);
	}

	// update function, called every game loop
	@Override
	public void onManagedUpdate(final float pSecondsElapsed) 
	{
		if (body == null || body.center().x() > activity.CAMERA_WIDTH || body.center().y() > activity.CAMERA_HEIGHT ||
				body.center().x() < 0 || body.center().y() < 0 )
		{
//			Sprite explode = new Sprite(this.mX, this.mY, 10, 10, activity.explosionTextureRegion, activity.getVertexBufferObjectManager());
//			activity.getScene().attachChild(explode);
			this.dealDamage(HEALTH + 1);
		}
		
		// if we go out of bounds set health to -1 so it is removed next game loop without explosion
		if (this.getX() > activity.CAMERA_WIDTH || this.getY() > activity.CAMERA_HEIGHT ||
				this.getX() < 0 || this.getY() < 0)
		{
			this.dealDamage(HEALTH + 1);
			return;
		}
		
		this.setPosition(body.center().x(), body.center().y());
		this.setRotation((float) ((body.orientation() * 190 / Math.PI) + 90));

		// check for collisions with player, set health to zero so it is removed with explosion
		if (this.collidesWith(activity.getPlayer()) && !activity.getPlayer().getImmune())
		{
			Log.i("Shooter", "Player-Enemy collision");
			activity.getPlayer().dealDamage(DAMAGE);
			this.dealDamage(HEALTH);
			return;
		}
	

		// if we are ship2, then follow the player in x-dir @ velocity 1

		// if we are to the left, move right
		if (this.getY() < activity.getPlayer().getY())
			body.setVelocity(VELOCITYX, 1);
		// if we are right, move left..
		else if (this.getY() > activity.getPlayer().getY())
			body.setVelocity(VELOCITYX, -1);
		// else do nothing
		else if (this.getY() == activity.getPlayer().getY())
			body.setVelocity(VELOCITYX, 0);

		// update totalgame time
		totalGameTime += pSecondsElapsed;
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