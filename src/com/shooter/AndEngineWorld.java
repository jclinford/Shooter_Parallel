package com.shooter;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;

import com.sjsu.physics.collisiondetection.QuadTreeNode;
import com.sjsu.physics.core.PhysicsThread;
import com.sjsu.physics.core.World;
import com.sjsu.physics.shapes.Rectangle;
import com.sjsu.physics.utils.Globals;

/** A physics AndEngineWorld keeps track of all
 * particles within its AndEngineWorld, and provides a means
 * to update them in parallel. Also does necessary
 * parallel procedures like sync'ing the threads,
 * starting or stopping, etc
 */
public class AndEngineWorld extends World implements IUpdateHandler
{
	private static ArrayList<PhysicsThread> threads;
	private static QuadTreeNode worldRootNode;

	public AndEngineWorld(int maxX, int maxY)
	{
		Rectangle gameRect = new Rectangle(0, 0, maxX, maxY);
		worldRootNode = new QuadTreeNode().init(null, gameRect, 0);
		threads = new ArrayList<PhysicsThread>();

		switch (Globals.NUM_PROCESSORS)
		{
		// single core is responsible for entire AndEngineWorld
		case 1:
			PhysicsThread thread = new PhysicsThread(this, 0, worldRootNode);
			threads.add(thread);
			break;

		// quad core, each thread gets its own tree root
		case 4:
			for (int i = 0; i < Globals.NUM_PROCESSORS; i++)
			{
				PhysicsThread t = new PhysicsThread(this, i, worldRootNode.children()[i]);
				t.setPriority(Thread.MIN_PRIORITY);
				threads.add(t);
			}
			break;
		default:
			System.out.println("Unsupported Processor count");
		}
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}