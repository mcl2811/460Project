package com.drawing;

import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_CW;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_TRIANGLE_STRIP;
import static com.jogamp.opengl.GL2GL3.GL_FILL;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class GSpriteKey implements GShape {
	// stores a set of texture coordinate along with a quad information
	// the textures are rendered at fixed interval (time) and repeated
	// translation based animation (horizontal, vertical) can also be
	// illustrated in the class

	boolean spriteDirection;
	boolean keyPressStatus = false;
	boolean keyMap[];

	enum KEYBOARD {
		UP, DOWN, RIGHT, LEFT, RUN, WALK, JUMP, DIE
	}

	int animationType;
	long startFrameTime = 0;
	long interval = 20; // how fast the animation frames are changing

	int nextFrame = 0; // current frame for this animation
	int maxAnimationFrameCount = 10; // we calculate this for each animation

	final int SPRITE_SIZE = 90;
	// so that we can use these variables from outside this class
	final static int IDLE = 0, WALK = 1, RUN = 2, JUMP = 3, DIE = 4;
	final int TOTAL_ANIM_COUNT = 5;

	// name of the animations
	final String fileName[] = { "idle", "walk", "run", "jump", "dead" };
	// how many frames do we have for each animation
	final int frameNo[] = { 15, 15, 15, 15, 15 };
	// what is the moving speed for each animation
	final int moveSpeed[] = { 0, 5, 10, 12, 0 };

	private Texture spriteTex[][]; // holding all the textures for all the animations

	int spritePosX, spritePosY, spriteBaseHeight; // the speed of moving the model
	float width, height;

	float speedLandPiece = 0f;
	// what is the current selected animation
	int currentAnimationType = IDLE;
	int nextActiveAnimationType = IDLE;

	// collision detection rectangles
	ArrayList<GCRect> collisionRects;
	ArrayList<GCObstacle> collisionObstacle;
	boolean isInsideBoundary = true;
	boolean outsideBoundary = false;

	GPixels spritePixel, leftBottomPixel, rightTopPixel;
	// constructor to initialize the sprite
	GSpriteKey(final GL2 gl, int x, int y, long interval, float w, float h) {

		this.spritePosX = x; // initial position on the world
		this.spritePosY = y;
		spriteBaseHeight = y;
		this.width = w; // dimension of the sprite
		this.height = h;
		this.spriteDirection = true; // facing right
		this.currentAnimationType = IDLE;
		this.nextActiveAnimationType = WALK;
		this.keyMap = new boolean[KEYBOARD.values().length];

		// frame change and collision detection interval
		this.interval = interval;
		this.startFrameTime = System.currentTimeMillis();

		// loading the texture objects
		this.spriteTex = new Texture[TOTAL_ANIM_COUNT][];

		// load all the texture in the array
		for (int i = 0; i < TOTAL_ANIM_COUNT; i++) {
			// allocation memory for all the textures for this row
			this.spriteTex[i] = new Texture[frameNo[i]];

			for (int j = 0; j < frameNo[i]; j++) {
				// calculating the name of the texture
				String name = "/world/guy/" + fileName[i] + "/" + fileName[i] + " (" + (j + 1) + ").png";

				// storing the texture in the array
				this.spriteTex[i][j] = GTextureUtil.loadTextureProjectDir(gl, name, "PNG");
				this.spriteTex[i][j].setTexParameterf(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
				this.spriteTex[i][j].setTexParameterf(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
			}
		}

		this.spritePixel = new GPixels(spritePosX, spritePosY, 50f);
		this.leftBottomPixel = new GPixels(spritePosX, spritePosY, 50f);
		this.rightTopPixel = new GPixels(spritePosX, spritePosY, 50f);

		this.outsideBoundary = false;
		this.isInsideBoundary = true;
	}

	// the model should be within these two rectangles
	void setCollisionRectangles(ArrayList<GCRect> cRectangles) {
		this.collisionRects = cRectangles;
	}
	void setCollisionObstacles(ArrayList<GCObstacle> cObstacle) {
		this.collisionObstacle = cObstacle;
	}

	// by using this method, the outside class can choose which animation to render
	void selectAnimation(int animationType) {

		// in order to ensure that we do not use an invalid animation number
		if (animationType < 0 || animationType >= this.TOTAL_ANIM_COUNT)
			return;

		this.nextFrame = 0;
		// otherwise, change the animation number as well as the frame-number
		this.currentAnimationType = animationType;
		this.maxAnimationFrameCount = frameNo[animationType];
		// we have to do it immediately, in order to ensure that
		// the frame number in the array is correct for this animation
		this.nextFrame = (this.nextFrame + 1) % this.maxAnimationFrameCount;

		if (animationType != JUMP)
			this.spritePosY = this.spriteBaseHeight;

	}
	// this function checks collision of the sprite with the
	// quad boundaries specified in the collisionRects ArrayList
	public void calculateSpriteDeath() {

		// if there are no collission rectangle boundaries
		// exit the function
		if (this.collisionRects == null)
			return;

		// assume that the sprite is not within boundary
		isInsideBoundary = false;
		GCObstacle inRect = null;
		
		// get the base point of the model
		int sPx = (int) spritePosX;
		int sPy = (int) spritePosY;

		// because of the texture rotation, the sprite point
		// needs to be adjusted
		final int H_OFFSETCORRECTION = 22, V_OFFSETCORRECTION = 10;
		if (this.spriteDirection == true)
			sPx = sPx + H_OFFSETCORRECTION; // correcting the offset of the quad
		else
			sPx = sPx - H_OFFSETCORRECTION;

		sPy = sPy + V_OFFSETCORRECTION; // correct the offset of the quad

		// this is the point we are going to use for collision calculation
		Point sPoint = new Point(sPx, sPy);
		
		// determine whether it is inside any of the collision rectangles
		for (GCObstacle rect : this.collisionObstacle) {

			// is the point inside the current rectangle
			boolean inFlagObs = rect.isInside(sPoint);
			// if within the current rectangle boundary
			if (inFlagObs) {

				if (DrawWindow.DEBUG_OUTPUT)
					System.out.println("Collision with obstacle found ..");
				selectAnimation(DIE);
				isInsideBoundary = true;

				// record the rectangle
				inRect = rect;
				this.removeSpriteFromWorld();
				// update the displayed points on the screen
				// bottomleft and topright point is displayed as red dot
				leftBottomPixel.setXY(inRect.getLowerLeftPoint());
				rightTopPixel.setXY(inRect.getUpperRightPoint());
				selectAnimation(DIE);
				break;
			}
		}
		// is the point inside the current rectangle

		
	}


	// this function checks collision of the sprite with the
	// quad boundaries specified in the collisionRects ArrayList
	public void calculateSpriteCollision() {

		// if there are no collission rectangle boundaries
		// exit the function
		if (this.collisionRects == null)
			return;

		// assume that the sprite is not within boundary
		isInsideBoundary = false;
		GCRect inRect = null;
		//GCObstacle inObs = null;
		
		// get the base point of the model
		int sPx = (int) spritePosX;
		int sPy = (int) spritePosY;

		// because of the texture rotation, the sprite point
		// needs to be adjusted
		final int H_OFFSETCORRECTION = 25, V_OFFSETCORRECTION = 25;
		if (this.spriteDirection == true)
			sPx = sPx + H_OFFSETCORRECTION; // correcting the offset of the quad
		else
			sPx = sPx - H_OFFSETCORRECTION;

		sPy = sPy + V_OFFSETCORRECTION; // correct the offset of the quad

		// this is the point we are going to use for collision calculation
		Point sPoint = new Point(sPx, sPy);
		

		// determine whether it is inside any of the collision rectangles
		for (GCRect rect : this.collisionRects) {

			// is the point inside the current rectangle
			boolean inFlag = rect.isInside(sPoint);
			// if within the current rectangle boundary
			if (inFlag) {

				if (DrawWindow.DEBUG_OUTPUT)
					System.out.println("Collision found ..");
				isInsideBoundary = true;

				// record the rectangle
				inRect = rect;
				
				// update the displayed points on the screen
				// bottomleft and topright point is displayed as red dot
				leftBottomPixel.setXY(inRect.getLowerLeftPoint());
				rightTopPixel.setXY(inRect.getUpperRightPoint());
				break;
			}
		}
		
		
		// if inside the boundary of a rectangle
		if (isInsideBoundary == true && inRect != null) {
			// adjust the height of the sprite with respect
			// to this rectangle boundary
			this.spriteBaseHeight = (int) inRect.getBaseHeight() +10;
			this.spritePosY = this.spriteBaseHeight;

			speedLandPiece = inRect.speed;

		}
		else {
			// the sprite is outside the boundary
			// calculate this only when we are not jumping
			if (nextFrame == maxAnimationFrameCount - 1)
				outsideBoundary = true;
			// }
			if (DrawWindow.DEBUG_OUTPUT) {
				System.out.println("No Collision found ..");
				System.out.println(sPoint);
		}
		}

		// update the sprite pixel position
		spritePixel.setXY(sPoint);

	}

	// removes the sprite from screen
	void removeSpriteFromWorld() {
		if (outsideBoundary == true) {
			spritePosY -= 5;
			this.spriteBaseHeight -= 5;
			
			// reset the game
			if (this.spritePosY < -(GLUTCanvas.DRAWING_HEIGHT / 2 + SPRITE_SIZE / 2)) {
				spritePosX = -35;
				spritePosY = -100;
				this.spriteBaseHeight = -100;
				outsideBoundary = false;
				this.isInsideBoundary = true;
				this.spriteDirection = true; // facing right
				this.currentAnimationType = IDLE;
				this.nextActiveAnimationType = WALK;

				this.selectAnimation(IDLE);
				this.calculateSpriteCollision();
			}
		}
	}

	// by using time as a parameter, the next animation frame number is calculated
	private void calculateNextFrame() {
		long endFrameTime = System.currentTimeMillis();

		// interval is the duration of one animation frame
		if ((endFrameTime - startFrameTime) > interval) {
			// go to the next frame
			if(this.currentAnimationType != DIE) {
			nextFrame = (nextFrame + 1) % maxAnimationFrameCount;
			} else {
				if(nextFrame != 14) {
					nextFrame = (nextFrame + 1);
				} else {
					nextFrame = 14; // Stay on the ground when the character falls
				}
			}
			// at the last frame, change the animation type

			startFrameTime = endFrameTime;

			this.calculateSpriteCollision();
			// when we reach to the end of the animation sequence
			// then check for collision
			if (nextFrame == maxAnimationFrameCount - 1) {
				// when we are at the idle state, check collision
				this.calculateSpriteCollision();
			}

			// calculating the new position of the character

			// for each step what is the height of the animated sprite
			float angleStep = (float) Math.PI / this.frameNo[this.currentAnimationType];
			final int JUMP_HEIGHT = 40;
			if (currentAnimationType == JUMP) {
				float angle = angleStep * (nextFrame);
				spritePosY = (int) (Math.sin(angle) * JUMP_HEIGHT);
				spritePosY = spriteBaseHeight + spritePosY;

				// during the jump, when half of the animation has been
				// rendered then check for collision (at landing)
				if (nextFrame > maxAnimationFrameCount / 2 + 1)
				this.calculateSpriteCollision();

			}

			// if there is no keypress event then
			// after the complete animation cycle
			// revert back to idle state
			if (currentAnimationType != DIE && 
					keyPressStatus == false && 
					nextFrame == maxAnimationFrameCount - 1) {
				this.selectAnimation(IDLE);	
				
			} else if (currentAnimationType == DIE && 
					keyPressStatus == false && 
					nextFrame == maxAnimationFrameCount - 1) {
				this.selectAnimation(DIE);
			}

			// if the sprite is outside the boundary
			// remove it from the world
			if (outsideBoundary == true) {
				this.removeSpriteFromWorld();
				return;
			}
			

			if (keyMap[KEYBOARD.UP.ordinal()]) {

				// at jump animation the character does not move vertically
				if (currentAnimationType != JUMP) {
					spritePosY += this.moveSpeed[this.currentAnimationType];
					spriteBaseHeight = spritePosY;
				}

				// if it reaches the top, animate from bottom
				if (spritePosY > GLUTCanvas.DRAWING_HEIGHT / 2)
					spritePosY = -(int) GLUTCanvas.DRAWING_HEIGHT / 2 - SPRITE_SIZE / 2;

			}

			if (keyMap[KEYBOARD.DOWN.ordinal()]) {
				// at jump animation the character does not move vertically
				if (currentAnimationType != JUMP) {
					spritePosY -= this.moveSpeed[this.currentAnimationType];
					spriteBaseHeight = spritePosY;
				}

				// if it reaches the bottom, animate from top
				if (spritePosY < -(GLUTCanvas.DRAWING_HEIGHT / 2 + SPRITE_SIZE / 2))
					spritePosY = (int) GLUTCanvas.DRAWING_HEIGHT / 2 + SPRITE_SIZE / 2;

			}

			// transformation correction: when the quad is rotated
			// the texture is placed ahead of the original position
			final int TEX_POSITION_CORRECTION = 50;

			if (keyMap[KEYBOARD.LEFT.ordinal()]) {
				spritePosX -= this.moveSpeed[this.currentAnimationType];
				if (spritePosX < -GLUTCanvas.DRAWING_WIDTH / 2)
					spritePosX = +(int) GLUTCanvas.DRAWING_WIDTH / 2 + SPRITE_SIZE / 2;

				if (this.spriteDirection == true)
					spritePosX += TEX_POSITION_CORRECTION;

				// the character is facing left
				this.spriteDirection = false;

			}

			if (keyMap[KEYBOARD.RIGHT.ordinal()]) {
				spritePosX += this.moveSpeed[this.currentAnimationType];
				if (spritePosX > GLUTCanvas.DRAWING_WIDTH / 2)
					spritePosX = -(int) GLUTCanvas.DRAWING_WIDTH / 2 - SPRITE_SIZE / 2;

				if (this.spriteDirection == false)
					spritePosX -= TEX_POSITION_CORRECTION;

				// the character is facing right
				this.spriteDirection = true;

			}
		}
	}
	// Checks if the sprite has touched one of the objects from the set of
	// GObstacle objects
	public boolean hasTouched() {
		boolean touch = false;
		int sPx = (int) spritePosX;
		int sPy = (int) spritePosY;
		Point sPoint = new Point(sPx, sPy);
		for (GCObstacle rect : this.collisionObstacle) {

			// is the point inside the current rectangle
			boolean inFlagObs = rect.isInside(sPoint);
			// if within the current rectangle boundary
			if (inFlagObs) {

				if (DrawWindow.DEBUG_OUTPUT)
					System.out.println("Collision with obstacle found ..");
				selectAnimation(DIE);
				touch = true;
				isInsideBoundary = true;
	}
			
		}
		return touch;
	}
	// event driven function call
	// this function is called from the GLUTCanvas class
	void resetKeyBoardEvent() {
		// there is no keyboard event
		keyPressStatus = false;
	}

	// event driven function call
	// this function is called from the GLUTCanvas class
	void processKeyBoardEvent(int key) {
		// the key press event has been recorded
		keyPressStatus = true;

		// determining which buttons have been pressed
		keyMap[KEYBOARD.UP.ordinal()] = (key == KeyEvent.VK_UP) ? true : false;
		keyMap[KEYBOARD.DOWN.ordinal()] = (key == KeyEvent.VK_DOWN) ? true : false;
		keyMap[KEYBOARD.RIGHT.ordinal()] = (key == KeyEvent.VK_RIGHT) ? true : false;
		keyMap[KEYBOARD.LEFT.ordinal()] = (key == KeyEvent.VK_LEFT) ? true : false;
		keyMap[KEYBOARD.RUN.ordinal()] = (key == KeyEvent.VK_R) ? true : false;
		keyMap[KEYBOARD.WALK.ordinal()] = (key == KeyEvent.VK_W) ? true : false;
		keyMap[KEYBOARD.JUMP.ordinal()] = (key == KeyEvent.VK_J) ? true : false;
		keyMap[KEYBOARD.JUMP.ordinal()] = (key == KeyEvent.VK_SPACE) ? true : false;
		keyMap[KEYBOARD.DIE.ordinal()] = (key == KeyEvent.VK_K) ? true : false;

		// toggle animation selection
		// e.g., pressing walk button again will disable walk animation
		if (keyMap[KEYBOARD.RUN.ordinal()]) {
			nextActiveAnimationType = RUN;
			// flip the animation type
			if (currentAnimationType != IDLE) {
				selectAnimation(IDLE);
			}

		}

		if (keyMap[KEYBOARD.WALK.ordinal()]) {
			nextActiveAnimationType = WALK;
			// flip the animation type
			if (currentAnimationType != IDLE) {
				selectAnimation(IDLE);
			}
		}

		if (keyMap[KEYBOARD.JUMP.ordinal()]) {
			nextActiveAnimationType = JUMP;
			// flip the animation type
			if (currentAnimationType != IDLE) {
				selectAnimation(IDLE);
			}
		}
		
		if (keyMap[KEYBOARD.DIE.ordinal()] || hasTouched()) {
			nextActiveAnimationType = DIE;
			// flip the animation type
			if (currentAnimationType != IDLE) {
				selectAnimation(IDLE);
			}
		}
		
		
		// whether the navigation buttons have been pressed
		// if yes, then start the recorded animation type
		if (keyMap[KEYBOARD.UP.ordinal()] || keyMap[KEYBOARD.DOWN.ordinal()] || keyMap[KEYBOARD.LEFT.ordinal()]
				|| keyMap[KEYBOARD.RIGHT.ordinal()])
		{
			// if the character is IDLE then start the next active animation type
			if (currentAnimationType == IDLE) {
				selectAnimation(nextActiveAnimationType);
			}

		}
		
	}

	// this function renders a quad with the nextFrame texture
	private void renderQuad(final GL2 gl, Texture texture) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glEnable(GL2.GL_BLEND);
		// gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		// gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_BLEND);
		texture.enable(gl);
		texture.bind(gl);

		gl.glPushMatrix();
		gl.glTranslatef(spritePosX, spritePosY - 4f, 0.0f);
		gl.glScalef(width, height, 1.0f);

		gl.glEnable(GL_CULL_FACE);
		// depending on the direction, the robot's heading will change
		if (spriteDirection == true) {
			gl.glRotatef(0, 0, 1, 0);
			gl.glFrontFace(GL_CCW);
		} else {
			gl.glRotatef(180.0f, 0, 1, 0);
			gl.glFrontFace(GL_CW);
		}

		gl.glColor3f(1.0f, 1.0f, 1.0f); // drawing color

		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		if (texture != null) {

			gl.glEnable(GL2.GL_TEXTURE_2D);
			texture.enable(gl);
			texture.bind(gl);
		}

		gl.glBegin(GL_TRIANGLE_STRIP);

		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(1f, 0f); // v0 bottom right
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(1f, 1f); // v1 top right
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(0f, 0f); // v2 bottom left
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(0f, 1f); // v3 top left

		gl.glEnd();

		if (texture != null) {
			gl.glDisable(GL2.GL_TEXTURE_2D);
			texture.disable(gl);
		}

		gl.glDisable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_TEXTURE_2D);

		gl.glDisable(GL_CULL_FACE);

		gl.glPopMatrix();

		spritePixel.render(gl);
		leftBottomPixel.render(gl);
		rightTopPixel.render(gl);
	}

	// actual drawing method for the sprite, that renders a texture on a quad
	public void render(final GL2 gl) {
		// determine the animation frame for this animation
		calculateNextFrame();

		// render a quad with the nextFrame texture
		renderQuad(gl, spriteTex[currentAnimationType][nextFrame]);
	}

}


