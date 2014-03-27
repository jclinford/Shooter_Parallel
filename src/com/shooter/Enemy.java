package com.shooter;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.sjsu.physics.shapes.PolyBody;
import com.sjsu.physics.shapes.Polygon;
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
public class Enemy extends Sprite implements GameObject
{
	private int HEALTH;
	private int DAMAGE;
	private int TYPE;
	private int VELOCITYX;
	private double SHOOT_RATE;

	private static double totalGameTime = 0;
	private double previousShotTime = 0;

	private ShooterActivity activity;
	private PolyBody body;

	public Enemy(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,
			final ShooterActivity a, final int vx, final int h, final int d, final double r, final int t)
	{
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		activity = a;

		HEALTH = h;
		DAMAGE = d;
		TYPE = t;
		VELOCITYX = vx;
		SHOOT_RATE = r;
		
		this.setRotation(90);

		int height = (int) this.getHeight() / 2;
		int width = (int) this.getWidth() / 2;
		
//		int[] xpoints = { -width, 0, width, 0 };
//		int[] ypoints = { 0, -height, 0, height };
		int[] xpoints = { -width, width, width, -width };
		int[] ypoints = { -height, -height, height, height };

		Polygon p = new Polygon(xpoints, ypoints, 4);
		Vector2 loc = new Vector2(pX, pY);
		body = new PolyBody(p, loc);
		body.setVelocity(vx * 5, 0);
		body.setMass(100);
		body.setAcceleration(new Vector2(1, 0));
		body.setAngularDamping(1f);
		
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
		this.setPosition(body.center().x(), body.center().y());
		this.setRotation((float) ((body.orientation() * 190 / Math.PI) + 90));
		
		if (body == null || body.center().x() > activity.CAMERA_WIDTH || body.center().y() > activity.CAMERA_HEIGHT ||
				body.center().x() < 0 || body.center().y() < 0 )
		{
			this.dealDamage(HEALTH + 1);
			return;
		}
		
		// if we go out of bounds set health to -1 so it is removed next game loop without explosion
		if (this.getX() > activity.CAMERA_WIDTH || this.getX() < 0 || this.getY() > activity.CAMERA_HEIGHT ||
				this.getY() < 0)
		{
			this.dealDamage(HEALTH + 1);
			return;
		}

		// check for collisions with player, set health to zero so it is removed with explosion
		if (this.collidesWith(activity.getPlayer()) && !activity.getPlayer().getImmune())
		{
			Log.i("Shooter", "Player-Enemy collision");
			activity.getPlayer().dealDamage(DAMAGE);
			this.dealDamage(HEALTH);
			return;
		}

		// if we are ship2, then follow the player in x-dir @ velocity 1
		if (TYPE == 2)
		{
			// if we are to the left, move right
			if (this.getY() < activity.getPlayer().getY())
				body.setVelocity(VELOCITYX, 1);
			// if we are right, move left..
			else if (this.getY() > activity.getPlayer().getY())
				body.setVelocity(VELOCITYX, -1);
			// else do nothing
			else if (this.getY() == activity.getPlayer().getY())
				body.setVelocity(VELOCITYX, 0);
		}

		// update totalgame time
		totalGameTime += pSecondsElapsed;

		// fire a shot if we are in time bounds
		if (totalGameTime - previousShotTime > SHOOT_RATE)
		{
			previousShotTime = totalGameTime;
			shoot();
		}
	}

	
	// fire a shot
	public void shoot()
	{
		//Log.i("Shooter", "Enemy shot fired!");
		float x = this.getX();
		float y = this.getY();

		Projectile proj = new Projectile(x, y, activity.enemyProjRegion, activity.getVertexBufferObjectManager(), activity, 7, 0, false);
		activity.getScene().attachChild(proj);
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
