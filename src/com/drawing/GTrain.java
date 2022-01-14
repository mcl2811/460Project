package com.drawing;
import com.jogamp.opengl.GL2;

public class GTrain implements GShape {
	float x, y;
	private GQuad myMainQuad;
	private GQuad myJoint;
	private GCircle mySmallWheel;
	private GCircle mySmallHubcap;
	private GCircle myBigWheel;
	private GCircle myBigHubcap;
	private GQuad myFrontThing;
	private GQuad myExhaust;
	private GQuad myWindow;
	private GQuad myRoof;
	
	// Train Constructor
	
	GTrain(float x, float y){
		this.x = x;
		this.y = y;
		float vertex2f[] = { 20.0f, 17.0f, 8.5f, 32f, -3f, 17f };
		
		vertex2f =  new float[] { 0.0f, 16.0f, // 0: left top point
				27f, 0.0f, // 2: bottom right point
				0f, 0f, 1f, // 4: main color
				0f, 0f, 1f }; // 7: outline color
		myMainQuad = new GQuad(vertex2f);
		
		vertex2f =  new float[] { -9.0f, 6.0f, // 0: left top point
				1f, 3.5f, // 2: bottom right point
				0f, 0f, 1f, // 4: main color
				0f, 0f, 1f }; // 7: outline color
		myJoint = new GQuad(vertex2f);
		
		// Here we use 2 different circles to create a wheel, having the same center.
		
		mySmallWheel = new GCircle(21f, 0f, 4f);
		float color[] = { 0.06f, 0f, 0.2f, 0.06f, 0f, 0.2f };
		mySmallWheel.setColor(color);
		
		mySmallHubcap = new GCircle(21f, 0f, 2.4f);
		color = new float[] { 1f, 0f, 0.2f, 1f, 0f, 0.2f };
		mySmallHubcap.setColor(color);
		
		myBigWheel = new GCircle(7.5f, 2.5f, 6.5f);
		color = new float[] { 0.06f, 0f, 0.2f, 0.06f, 0f, 0.2f };
		myBigWheel.setColor(color);
		
		myBigHubcap = new GCircle(7.5f, 2.5f, 4.5f);
		color = new float[] { 1f, 0f, 0.2f, 1f, 0f, 0.2f };
		myBigHubcap.setColor(color);
		
		vertex2f =  new float[] { 27.0f, 12.0f, // 0: left top point
				29f, 3.5f, // 2: bottom right point
				0f, 0f, 0f, // 4: main color
				0f, 0f, 0f }; // 7: outline color
		myFrontThing = new GQuad(vertex2f);
		
		vertex2f =  new float[] { 21.0f, 22.0f, // 0: left top point
				24f, 16f, // 2: bottom right point
				0f, 0f, 1f, // 4: main color
				0f, 0f, 1f }; // 7: outline color
		myExhaust = new GQuad(vertex2f);
		
		vertex2f =  new float[] { 2.0f, 30.0f, // 0: left top point
				17f, 16f, // 2: bottom right point
				1f, 0f, 0f, // 4: main color
				1f, 0f, 0f }; // 7: outline color
		myWindow = new GQuad(vertex2f);
		
		vertex2f =  new float[] { 2.0f, 32.0f, // 0: left top point
				17f, 30f, // 2: bottom right point
				0f, 0f, 1f, // 4: main color
				0f, 0f, 1f }; // 7: outline color
		myRoof = new GQuad(vertex2f);
		
	}
	
	public void render(final GL2 gl) {

		gl.glPushMatrix();
		gl.glLoadIdentity(); // reset model-view matrix

		gl.glTranslatef(x, y, 0.0f);
		myMainQuad.render(gl); // We call the render functions of each object
		myJoint.render(gl);
		mySmallWheel.render(gl);
		mySmallHubcap.render(gl);
		myBigWheel.render(gl);
		myBigHubcap.render(gl);
		myFrontThing.render(gl);
		myExhaust.render(gl);
		myWindow.render(gl);
		myRoof.render(gl);
		
		gl.glPopMatrix();

		// gl.glTranslatef(-15.0f, -15.0f, 0.0f);
	}
	
	void updateTrain(float disp, float max) {
		if (this.x < max*2)
			this.x += disp;
		else
			this.x = -50;
	}
}
