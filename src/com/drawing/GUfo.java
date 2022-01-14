package com.drawing;
import com.jogamp.opengl.GL2;

public class GUfo implements GShape {
	float x, y;
	private GEllipse myEllipse;
	float color[];
	private GCircle myCircle;

	
	// Train Constructor
	
	GUfo(float x, float y){
		this.x = x;
		this.y = y;

		myEllipse = new GEllipse(0f, 0f, 10f, 2.5f);
		color = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
		myEllipse.setColor(color);

		myCircle = new GCircle(0f, 3f, 3f);
		float color[] = { 0f, 1f, 0f, 0f, 0f, 0f };
		myCircle.setColor(color);


		
	}
	
	public void render(final GL2 gl) {

		gl.glPushMatrix();
		gl.glLoadIdentity(); // reset model-view matrix

		gl.glTranslatef(x, y, 0.0f);

		myEllipse.render(gl);
		myCircle.render(gl);

		
		gl.glPopMatrix();

		// gl.glTranslatef(-15.0f, -15.0f, 0.0f);
	}
	
	void updateUfo(float disp, float min, float max) {
		if (this.x > min)
			this.x -= disp;
		else
			this.x = max;
	}
}