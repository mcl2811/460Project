package com.drawing;

import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_LINE_LOOP;
import static com.jogamp.opengl.GL.GL_TRIANGLE_STRIP;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

import java.awt.Point;
import java.util.Arrays;
import java.util.Random;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class GCObstacle implements GShape, Comparable<GCObstacle> {

	float vertex2f[];
	float speed;

	final int TEXHEIGHTCORRECTION = 150;
	int texture_height;
	private Texture texID;


	GCObstacle(final GL2 gl, float vertex2f[], float speed) {
		// only color information is used
		this.vertex2f = Arrays.copyOf(vertex2f, vertex2f.length);
		// first 3 elements are color
		// 4th, 5th two elements provides the lower left corner point
		// 6th element is the width
		// 7th element is the height
		this.vertex2f[4] = -125f;
		this.vertex2f[6] = 70f;
		this.speed = speed;
		reloadThings(gl, false);

	}

	public void reloadThings(final GL2 gl, boolean resetPosFlag) {
		Random rand = new Random();

		final int MIN_DELAY_LENGTH = (int) GLUTCanvas.DRAWING_WIDTH/8 , 
				MAX_DELAY_LENGTH = (int) GLUTCanvas.DRAWING_WIDTH/4;
		
		
		// it must be a function of the previous GRect objects y value
		final int MIN_HEIGHT = (int) GLUTCanvas.DRAWING_HEIGHT/16 , 
				MAX_HEIGHT = (int) GLUTCanvas.DRAWING_WIDTH/8;
		
		if (resetPosFlag == true) {
		// position in the world
		vertex2f[3] = (int) GLUTCanvas.DRAWING_WIDTH / 2 + MIN_DELAY_LENGTH + rand.nextInt(MAX_DELAY_LENGTH);
		
		Random r = new Random();
		double low = 1;
		double high = 1.08;
		double multiplier = low + (high - low) * r.nextDouble();
		float fmulti = (float)multiplier;
		this.vertex2f[4] = -125f * fmulti;
		
	}
		// calculating texture height
		texture_height = (int) (this.getWidth() / this.getHeight() * TEXHEIGHTCORRECTION);

		// speed of movement
		final float MIN_SPEED = 0.25f, MAX_SPEED = 1.0f;
		speed = MIN_SPEED + (float) rand.nextDouble() * MAX_SPEED;

		String textureName = "/world/obstacle/obstacletexture.png";

		// storing the texture in the array
		texID = GTextureUtil.loadTextureProjectDir(gl, textureName, "PNG");
		texID.setTexParameterf(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		texID.setTexParameterf(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

	}

	public float getWidth() {
		return vertex2f[5];
	}

	public float getHeight() {
		return vertex2f[6];
	}

	public float getBaseHeight() {
		return vertex2f[4];
	}

	public float getMiddleHorizontalPoint() {
		return vertex2f[3] + this.getWidth() / 2;
	}

	public Point getLowerLeftPoint()
	{
		Point p = new Point();
		p.x = (int) vertex2f[3];
		p.y = (int) vertex2f[4];
		return p;
	}

	public Point getUpperRightPoint() {
		Point p = new Point();
//		p.x = (int) (vertex2f[5]);
//		p.y = (int) (vertex2f[6]);
		p.x = (int) (vertex2f[3] + vertex2f[5]);
		p.y = (int) (vertex2f[4] + vertex2f[6]);
		return p;

	}




	// draws the outline of the rectangle
	public void drawOutline(final GL2 gl) {

		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		gl.glLineWidth(4f);

		gl.glBegin(GL_LINE_LOOP);

		gl.glVertex2f(1f, 0f); // v0 bottom right
		gl.glVertex2f(1f, 1f); // v1 top right
		gl.glVertex2f(0f, 1f); // v3 top left
		gl.glVertex2f(0f, 0f); // v2 bottom left

		gl.glEnd();

		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}

	// draws with texture or color
	public void drawFilled(final GL2 gl) {
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);


		gl.glBegin(GL_TRIANGLE_STRIP);

		gl.glVertex2f(1f, 0f); // v0 bottom right
		gl.glVertex2f(1f, 1f); // v1 top right
		gl.glVertex2f(0f, 0f); // v2 bottom left
		gl.glVertex2f(0f, 1f); // v3 top left

		gl.glEnd();
	}

	private void renderQuad(final GL2 gl, Texture texture) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glEnable(GL2.GL_BLEND);
		// gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		// gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_BLEND);
		texture.enable(gl);
		texture.bind(gl);
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

		// drawOutline(gl);

		if (texture != null) {
			gl.glDisable(GL2.GL_TEXTURE_2D);
			texture.disable(gl);
		}

		gl.glDisable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_TEXTURE_2D);

	}

	public void render(final GL2 gl) {

		if (vertex2f[3] < -(GLUTCanvas.DRAWING_WIDTH / 2 + this.getWidth())) {
			reloadThings(gl, true);
		}

		// move the land piece
		vertex2f[3] = vertex2f[3] - this.speed;

		gl.glPushMatrix();
		gl.glTranslatef(vertex2f[3], vertex2f[4], 0f);

		gl.glScalef(vertex2f[5], vertex2f[6], 1.0f);
		gl.glColor3fv(vertex2f, 0); // drawing color
		drawOutline(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(vertex2f[3], vertex2f[4]+30, 0f);
//		gl.glTranslatef(vertex2f[3], vertex2f[4] - this.texture_height / 2, 0f);
		gl.glColor3f(1.0f, 1.0f, 1.0f); // drawing color
		gl.glScalef(vertex2f[5], this.texture_height, 1.0f);
		renderQuad(gl, this.texID);

		gl.glPopMatrix();
	}

	// returns whether a given point is inside the rectangle
	boolean isInside(Point p) {
		Point pa = getLowerLeftPoint();
		Point pb = getUpperRightPoint();
		return GCollision.isInside((int) pa.getX(), (int) pa.getY(), (int) pb.getX(), (int) pb.getY(), (int) p.getX(),
				(int) p.getY());
	}

	// sort the elements based on the height
	public int compareTo(GCObstacle paramRect) {
		if (this.getBaseHeight() < paramRect.getBaseHeight())
			return 1;
		else if (this.getBaseHeight() > paramRect.getBaseHeight())
			return -1;
		else
			return 0;

	}

}
