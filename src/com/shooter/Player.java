package com.shooter;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.sjsu.physics.shapes.PolyBody;
import com.sjsu.physics.shapes.Polygon;
import com.sjsu.physics.shapes.RigidBody;
import com.sjsu.physics.utils.Vector2;


/**
 * 
 * @author John Linford
 * 
 *	Player class that the user controls
 *	to move or shoot
 *
 */
public class Player extends Sprite implements GameObject
{
	public static int HEALTH = 1000;
	private static final int DAMAGE = 10;

	private ShooterActivity activity;
	private final PolyBody body;

	private boolean isImmune;


	public Player(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,
			final ShooterActivity a) 
	{
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		activity = a;
		isImmune = false;
		//physicsHandler = new PhysicsHandler(this);
		//registerUpdateHandler(physicsHandler);
		
		this.setRotation(-90);

		int h = (int) this.getHeight() / 2;
		int w = (int) this.getWidth() / 2;
		
		int[] xpoints = { -w, w, w, -w };
		int[] ypoints = { -h, -h, h, h };

		Polygon p = new Polygon(xpoints, ypoints, 4);
		Vector2 loc = new Vector2(pX, pY);
		body = new PolyBody(p, loc);
		body.setInverseMass(0);
		
		setUserData(body);
		
//		this.setRotationCenter(body.center().x(), body.center().y());
//		rotate((float)Math.PI / 2);
//		rotate(270);

		activity.getScene().registerTouchArea(this);
		activity.getScene().attachChild(this);
		
		// add to world
		activity.getPhysicsWorld().addBodyToWorld(body);

	}

	@Override
	public void onManagedUpdate(final float pSecondsElapsed) 
	{
		super.onManagedUpdate(pSecondsElapsed);
		
		float x = body.center().x();
		float y = body.center().y();
		
		if (body.center().x() + body.bounds().halfWidth() + 10 > activity.CAMERA_WIDTH)
		{
			x = activity.CAMERA_WIDTH - body.bounds().halfWidth() - 20;
		}
		else if (body.center().x() - body.bounds().halfWidth() - 10 < 0)
		{
			x = body.bounds().halfWidth() + 20;
		}
		if (body.center().y() + body.bounds().halfHeight() + 10 > activity.CAMERA_HEIGHT)
		{
			y = activity.CAMERA_HEIGHT - body.bounds().halfHeight() - 20;
		}
		else if (body.center().y() - body.bounds().halfHeight() - 10 < 0)
		{
			y = body.bounds().halfHeight() + 20;
		}
		
		this.setPosition(x, y);
		body.setCenter(x, y);
	}


	//---------------------//
	// Getters and Setters //
	//---------------------//
	public void dealDamage(int damage)
	{
		// only deal damage if immunity is not active
		if (!isImmune)
		{
			HEALTH -= damage;
			activity.setHealth(HEALTH);
		}
	}
	public int getDamage()
	{
		return DAMAGE;
	}
	public int getHealth() 
	{
		return HEALTH;
	}

	// if we pick up the invisible buff, visibly change the player
	public void setImmune(boolean b)
	{
		isImmune = b;

		if (isImmune)
			this.setColor(.5f, .5f, .5f, .3f);
		else
			this.setColor(1f, 1f, 1f, 1f);
	}

	// if we are immune, returns true
	public boolean getImmune()
	{
		return isImmune;
	}

	// return the physics body of our player
	public RigidBody getBody() 
	{
		return body;
	}

}
