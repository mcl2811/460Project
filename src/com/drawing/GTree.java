package com.drawing;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_LINE_LOOP;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

import java.util.Arrays;

import com.jogamp.opengl.GL2;

public class GTree implements GShape {
	float x, y;
	private GQuad myLog;
	private GEllipse myEllipse;
	private GCircle myApple [] = new GCircle[7] ;
	
	GTree(float x, float y){
		this.x = x;
		this.y = y;
		
		float vertex2f[];
		
		// log
		vertex2f = new float[] { 7.0f, 32.0f, // 0: left top point
				10f, 0f, // 2: bottom right point
				0.7f, 0.3f, 0.2f, // 4: main color
				0.7f, 0.3f, 0.2f }; // 7: outline color
		myLog = new GQuad(vertex2f);
		
		myEllipse = new GEllipse(8.5f, 34f, 9f, 12f);
		
		myApple[0] = new GCircle(13f, 30f, 1f);
		myApple[1] = new GCircle(14f, 33f, 1f);
		myApple[2] = new GCircle(12.5f, 37f, 1.7f);
		myApple[3] = new GCircle(8.5f, 41f, 1f);
		myApple[4] = new GCircle(4.2f, 37f, 1.7f);
		myApple[5] = new GCircle(4.4f, 32f, 1.6f);
		myApple[6] = new GCircle(5.3f, 27f, 1f);

		for (int i = 0; i < 7; i++) {
			float color[] = { 0.7f, 0.0f, 0.0f, 0.7f, 0f, 0f };
			myApple[i].setColor(color);
		}
	}
	
	public void render(final GL2 gl) {

		gl.glPushMatrix();
		gl.glLoadIdentity(); // reset model-view matrix

		gl.glTranslatef(x, y, 0.0f);
		myEllipse.render(gl);
		myLog.render(gl);
		
		for (int i = 0; i < 7; i++) {
			myApple[i].render(gl);
		}
		
		gl.glPopMatrix();

		// gl.glTranslatef(-15.0f, -15.0f, 0.0f);
	}
}
