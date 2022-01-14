package com.drawing;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

// import com.jogamp.opengl.util.gl2.GLUT;

/*
* JOGL 2.0 Program Template (GLCanvas) This is a "Component" which can be added
* into a top-level "Container". It also handles the OpenGL events to render
* graphics.
*/
@SuppressWarnings("serial")
class GLUTCanvas extends GLCanvas implements GLEventListener {

	public static int CANVAS_WIDTH = 1100; // width of the drawable
	public static int CANVAS_HEIGHT = 700; // height of the drawable

	public static final float DRAWING_WIDTH = 550f, DRAWING_HEIGHT = 350f;
	public static float GL_Width, GL_Height;
	// Setup OpenGL Graphics Renderer
	GDrawOrigin myOrigin;
	GKeyBoard keyBoard;
	GMouse mouse;

	GDrawingPoints mousePoints;

	GQuad myQuad;

	
	GWater myWater;

	// GTriangle learnTriangle;

	GPatch myPatch;
	GSpriteKey spriteCharacter;

	ArrayList<GShape> drawingArtObjects;
	ArrayList<GCRect> collisionRects;
	ArrayList<GCObstacle> collisionObstacles;

	/** Constructor to setup the GUI for this Component */
	public GLUTCanvas(GLCapabilities capabilities, GKeyBoard kb, GMouse mouse) {

		super(capabilities);


		// creating a canvas for drawing
		// GLCanvas canvas = new GLCanvas(capabilities);

		this.addGLEventListener(this);
		this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		this.keyBoard = kb;
		this.mouse = mouse;
	}

	// ------ Implement methods declared in GLEventListener ------

	/**
	 * 
	 * Called back immediately after the OpenGL context is initialized. Can be used
	 * to perform one-time initialization. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context

		// ----- Your OpenGL initialization code here -----
		GLU glu = new GLU();

		GL_Width = DRAWING_WIDTH / 2.0f;
		GL_Height = DRAWING_HEIGHT / 2.0f;
		gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
		gl.glLoadIdentity(); // reset projection matrix
		// gl.glOrtho(-GL_Width, GL_Width, -GL_Height, GL_Height, -2.0f, 2.0f); // 2D
		glu.gluOrtho2D(-GL_Width, GL_Width, -GL_Height, GL_Height); // canvas

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW); // specify coordinates
		gl.glLoadIdentity(); // reset

		gl.glClearColor(.90f, .90f, 1.0f, 1.0f); // color used to clean the canvas
		gl.glColor3f(1.0f, 1.0f, 1.0f); // drawing color
		// gl.glEnable(GL2.GL_DEPTH_TEST);
		// gl.glDepthFunc(GL2.GL_LESS);

		mousePoints = new GDrawingPoints(GL_TRIANGLES);

		// origin
		myOrigin = new GDrawOrigin(GLUTCanvas.GL_Width, GLUTCanvas.GL_Height);

		// learn quad initialization
		float vertex2f[] = new float[] { 105f, 0f, 100f, 100f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f };
		// learnTriangle = new GTriangle(vertex2f);
		// learnTriangle.loadTexture(gl, "/world/Texture.png")
		
		float myVertex2f[] = new float[] { 0f, 0f, 0f, -400f, -145f, 1000.0f, 20.0f};
		myWater = new GWater(gl, myVertex2f);
		

		// patch with texture
		myPatch = new GPatch(-275, -175, 550, 350, 5, "/world2/BG.jpg");


		GPlane flySprite = new GPlane(gl, -200, 100, 128, 75, 60);
		flySprite.selectAnimation(1);
//
//		GSprite walkSprite = new GSprite(gl, 25, 98, 128, 75, 60);
//		walkSprite.selectAnimation(1);

		// character controlled sprite animation

		spriteCharacter = new GSpriteKey(gl, 0, -35, 100, 90, 85);
		spriteCharacter.selectAnimation(0);

		// collision rectangle object

		//loadObjectDataFromFile(gl);
		// Ground
		vertex2f = new float[] { .3f, .3f, .3f, -295f, -100f, 600f, 135f };
		GCRect ground = new GCRect(gl, vertex2f, 0f);

		vertex2f = new float[] { .3f, .3f, .3f, -166f, -62f, 20f, 135f };
		GCObstacle myRightBoundary = new GCObstacle(gl, vertex2f, 0.7f);
		
		vertex2f = new float[] { .3f, .3f, .3f, 66f, -62f, 20f, 135f };
		GCObstacle leftBoundary = new GCObstacle(gl, vertex2f, 1f);

		vertex2f = new float[] { .3f, .3f, .3f, 166f, -62f, 20f, 135f };
		GCObstacle rightBoundary = new GCObstacle(gl, vertex2f, 0.9f);
		
		

		// loading collision rectangle object from a file

		// collision rectangles
		collisionRects = new ArrayList<>();
		collisionRects.add(ground);
		collisionObstacles = new ArrayList<>();
		collisionObstacles.add(myRightBoundary);
		collisionObstacles.add(leftBoundary);
		collisionObstacles.add(rightBoundary);

		spriteCharacter.setCollisionRectangles(collisionRects);
		spriteCharacter.setCollisionObstacles(collisionObstacles);


		// adding them all in the arrayList
		drawingArtObjects = new ArrayList<GShape>();
		drawingArtObjects.add(myPatch);
		//drawingArtObjects.add(mousePoints);
		// drawingArtObjects.add(learnTriangle);
		// drawingArtObjects.add(myOrigin);
		 //drawingArtObjects.add(myQuad);
		drawingArtObjects.add(myRightBoundary);
		drawingArtObjects.add(leftBoundary);
		drawingArtObjects.add(rightBoundary);
		drawingArtObjects.add(ground);

		
		//drawingArtObjects.add(myUfo);
		// drawingArtObjects.add(walkSprite);
		drawingArtObjects.add(spriteCharacter);
		//drawingArtObjects.add(water);
		//drawingArtObjects.add(myWater);
		//drawingArtObjects.add(flySprite);


	}

	// reads a file and creates GCRect objects based on the data
	public void loadObjectDataFromFile(final GL2 gl) {

		collisionRects = new ArrayList<>();

		String textPath = "/world/Floaters.txt";
		String texPath = System.getProperty("user.dir") + textPath;
		File myFile = new File(texPath);
		int count = 0;
		try (Scanner myStream = new Scanner(myFile)) {

			float [] vertex2f = new float[] { 0f, 1f, 2f, 3f, 4f, 5f, 6f };
			// read all the

			while (myStream.hasNextLine()) {
				String values = myStream.nextLine();
				if (values.equals("[GCRect]")) {
					// read three lines of data
					// first line: 3 color values
					String data = myStream.nextLine();
					int indexSlash = data.indexOf("//");
					String dataVal = data.substring(0, indexSlash);
					String dataElements[] = dataVal.split(",");
					if(dataElements.length >2)
					{
						vertex2f[0] = Float.parseFloat(dataElements[0]);
						vertex2f[1] = Float.parseFloat(dataElements[1]);
						vertex2f[2] = Float.parseFloat(dataElements[2]);

					}

					// second line: 2 position values

					data = myStream.nextLine();
					indexSlash = data.indexOf("//");
					dataVal = data.substring(0, indexSlash);
					dataElements = dataVal.split(",");
					if (dataElements.length > 1) {
						vertex2f[3] = Float.parseFloat(dataElements[0]);
						vertex2f[4] = Float.parseFloat(dataElements[1]);
					}

					// third line: 2 dimension values

					data = myStream.nextLine();
					indexSlash = data.indexOf("//");
					dataVal = data.substring(0, indexSlash);
					dataElements = dataVal.split(",");
					if (dataElements.length > 1) {
						vertex2f[5] = Float.parseFloat(dataElements[0]);
						vertex2f[6] = Float.parseFloat(dataElements[1]);
					}
						
					// reading an empty line
					if (myStream.hasNextLine())
					myStream.nextLine();

				count++;
					// adding the GCRect object in the ArrayList
					GCRect myRectObject = new GCRect(gl, vertex2f, 0);
					collisionRects.add(myRectObject);

				}

			}

		} catch (Exception e) {
			System.out.println("File reading error");
		}

		if (DrawWindow.DEBUG_OUTPUT)
			System.out.println(count + " objects loaded");

	}

	/**
	 * Call-back handler for window re-size event. Also called when the drawable is
	 * first set to visible.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context

		// Have to add this in order for the canvas to properly scale in the window
		// Found at https://forum.jogamp.org/canvas-not-filling-frame-td4040092.html
		double dpiScalingFactorX = ((Graphics2D) getGraphics()).getTransform().getScaleX();
		double dpiScalingFactorY = ((Graphics2D) getGraphics()).getTransform().getScaleY();
		width = (int) (width / dpiScalingFactorX);
		height = (int) (height / dpiScalingFactorY);

		if (DrawWindow.DEBUG_OUTPUT)
		System.out.println(width + ":" + height);

		GLUTCanvas.CANVAS_HEIGHT = height;
		GLUTCanvas.CANVAS_WIDTH = width;

		// we want this aspect ratio in our drawing
		float target_aspect = DRAWING_WIDTH / DRAWING_HEIGHT;

		if (height < 1)
			height = 1;
		// this is the new aspect ratio based on the resize
		float calc_aspect = (float) width / (float) height;

		float aspect = calc_aspect / target_aspect;

		if (calc_aspect >= target_aspect) {
			GL_Width = DRAWING_WIDTH / 2.0f * aspect;
			GL_Height = DRAWING_HEIGHT / 2.0f;
		} else {
			GL_Width = DRAWING_WIDTH / 2.0f;
			GL_Height = DRAWING_HEIGHT / 2.0f / aspect;
		}

		myOrigin.updateOriginVertex(GLUTCanvas.GL_Width, GLUTCanvas.GL_Height);

		GLU glu = new GLU();
		gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
		gl.glLoadIdentity(); // reset projection matrix
		// gl.glOrtho(-GL_Width, GL_Width, -GL_Height, GL_Height, -2.0f, 2.0f); // 2D
		glu.gluOrtho2D(-GL_Width, GL_Width, -GL_Height, GL_Height); // canvas

		// gl.glViewport(0, 0, (int) GL_Width * 2, -(int) GL_Height * 2);
		gl.glViewport(-(int) GL_Width, (int) GL_Width, -(int) GL_Height, (int) GL_Height);

		// gl.glEnable(GL2.GL_DEPTH_TEST);
		// gl.glDepthFunc(GL2.GL_LESS);
		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW); // specify coordinates
		gl.glLoadIdentity(); // reset

	}

	/**
	 * Called back by the animator to perform rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context

		gl.glClearColor(.90f, .90f, 1.0f, 1.0f); // color used to clean the canvas
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the canvas with color

		gl.glLoadIdentity(); // reset the model-view matrix
		

		// myOrigin.render(gl);
		for (GShape artObject : drawingArtObjects) {
			artObject.render(gl);
		}


		gl.glFlush();
	}
	


	/**
	 * Called back before the OpenGL context is destroyed. Release resource such as
	 * buffers.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	/**
	 * This function updates drawing based on keyboard events
	 */
	public void processKeyBoardEvents(int key) {

		if (keyBoard.getCharPressed() == 't' && keyBoard.isPressReleaseStatus() == true) {
			this.mousePoints.setDrawingType(GL_TRIANGLES);
		}

		else if (keyBoard.getCharPressed() == 'l' && keyBoard.isPressReleaseStatus() == true) {
			this.mousePoints.setDrawingType(GL_LINES);

		}

		else if (keyBoard.getCharPressed() == 'c' && keyBoard.isPressReleaseStatus() == true) {
			this.mousePoints.setDrawingType(GDrawingPoints.XGL_CIRCLE);
		}


		this.spriteCharacter.processKeyBoardEvent(key);

	}

	public void processKeyBoardEventsStop() {
		keyBoard.setPressReleaseStatus(false);
		this.spriteCharacter.resetKeyBoardEvent();
	}

	/**
	 * 
	 */
	public void processMouseEvents()
	{
		if (mouse.isPressReleaseStatus() == true) {
			mousePoints.addPoint(mouse.getPointClicked());
		}
	}

}