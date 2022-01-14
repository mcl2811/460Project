package com.drawing;
import com.jogamp.opengl.GL2;

public class GCloud implements GShape {
	float x, y, scale;
	private GCircle myBigCircle;
	private GCircle mySmallCircle;
	private GCircle mySmallCircle2;
	
	// Cloud constructor
	
	GCloud(float x, float y, float scale){
		this.x = x;
		this.y = y;
		
		// Clouds are created by using 3 circles
		
		myBigCircle = new GCircle(4f, 0.5f, 4f*scale);
		float color[] = { 0.88f, 0.95f, 1f, 0.88f, 0.95f, 1f };
		myBigCircle.setColor(color);
		
		mySmallCircle = new GCircle(0f, 0f, 3f*scale);
		color = new float[] { 0.88f, 0.95f, 1f, 0.88f, 0.95f, 1f };
		mySmallCircle.setColor(color);
		
		mySmallCircle2 = new GCircle(8f, -0.7f, 3f*scale);
		color = new float[] { 0.88f, 0.95f, 1f, 0.88f, 0.95f, 1f };
		mySmallCircle2.setColor(color);
	}
	
	public void render(final GL2 gl) {
		
		gl.glPushMatrix();
		gl.glLoadIdentity(); // reset model-view matrix

		gl.glTranslatef(x, y, 0.0f);
		myBigCircle.render(gl); // We call the render function for the different shapes
		mySmallCircle.render(gl);
		mySmallCircle2.render(gl);

		gl.glPopMatrix();

		// gl.glTranslatef(-15.0f, -15.0f, 0.0f);
	}
	
	void updateCloud(float disp, float max) {
		if (this.x < max*2)
			this.x += disp;
		else
			this.x = -50;
	}
	
}
