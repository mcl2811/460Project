package com.drawing;
import com.jogamp.opengl.GL2;

public class GRocket implements GShape {
	float x, y, scale;
	private GEllipse myMainBody;
	private GTriangle myFrontPart;
	private GCircle myWindow;
	private GTriangle myLeftBack;
	private GTriangle myRightBack;
	float color[];
	
	// Cloud constructor
	
	GRocket(float x, float y, float scale){
		this.x = x;
		this.y = y;
		
		// Clouds are created by using 3 circles
		
		myMainBody = new GEllipse(0f, 0f, 3f, 8f);
		color = new float[] { 1f, 1f, 1f, 1f, 0f, 0.0f };
		myMainBody.setColor(color);
		
		float vertex2f[] = { 2.5f, 5.0f, 0f, 10f, -2.5f, 5f };
		myFrontPart = new GTriangle(vertex2f);
		float color[] = { 1f,  0f,  0f,  1f,  0f,  0f };
		//myFrontPart.setColor(color);
		
		myWindow = new GCircle(0f, 0.5f, 2f);
		color = new float[]{ 0.2f, 0.7f, 1f, 0f, 0f, 0f };
		myWindow.setColor(color);
		
		vertex2f = new float[]{ -2.5f, -5.0f, -5f, -8f, -2.9f, 0f };
		myLeftBack = new GTriangle(vertex2f);
		color = new float[]{ 1f,  0f,  0f,  1f,  0f,  0f };
		//myLeftBack.setColor(color);
		
		vertex2f = new float[]{ 2.5f, -5.0f, 5f, -8f, 2.9f, 0f };
		myRightBack = new GTriangle(vertex2f);
		color = new float[]{ 1f,  0f,  0f,  1f,  0f,  0f };
		
		//myRightBack.setColor(color);
	}
	
	public void render(final GL2 gl) {
		
		gl.glPushMatrix();
		gl.glLoadIdentity(); // reset model-view matrix

		gl.glTranslatef(x, y, 0.0f);
		myMainBody.render(gl); // We call the render function for the different shapes
		myFrontPart.render(gl);
		myWindow.render(gl);
		myLeftBack.render(gl);
		myRightBack.render(gl);


		gl.glPopMatrix();

		// gl.glTranslatef(-15.0f, -15.0f, 0.0f);
	}
	
	void updateRocket(float disp, float max) {
		if (this.y < max*2)
			this.y += disp;
		else
			this.y = -50;
	}
	
}