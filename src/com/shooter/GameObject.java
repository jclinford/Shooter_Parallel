package com.shooter;

import com.sjsu.physics.shapes.RigidBody;


/**
 * @author John Linford
 * 
 *	A game object that all moving objects will implement
 *	such as the player, enemies, projectiles and powerups
 *
 */
public interface GameObject 
{
	
	// Update Loop
	public void onManagedUpdate(final float pSecondsElapsed);

	//---------------------//
	// Getters and Setters //
	//---------------------//
	public void dealDamage(int damage);
	public RigidBody getBody();
	public int getHealth();
	public int getDamage();
}
