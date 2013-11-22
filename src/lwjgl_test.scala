import org.lwjgl._
import opengl.{Display,GL11,DisplayMode}
import GL11._
import util.glu.GLU._
import util.vector.Matrix4f
import util.vector.Vector3f
import input._
import math._

object lwjgl_test {

  var drawAxes = true
  var finished = false
  
  var rotation = 0.0f
  var look_x = 0.0f
  var look_y = 0.0f
  var look_z = 0.0f
  
  var eye_x = 0.5f
  var eye_y = 1.0f
  var eye_z = -0.5f

  def main(args: Array[String]): Unit = {
    run
  }
  
  def run() {
    Display.setVSyncEnabled(true)
    Display.setDisplayMode(new DisplayMode(800,600))
    Display.create()
    glEnable(GL_DOUBLEBUFFER)
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_LIGHTING)
	glEnable(GL_LIGHT0)
    adjust_cam
    event_loop()
  }

  def clean_up() {
	  Display.destroy()
  }

  def key_events() = {
    import Keyboard._
    Keyboard.enableRepeatEvents(true)
    if (isKeyDown(KEY_ESCAPE) || Display.isCloseRequested()) {
      finished = true
    }
    while (Keyboard.next()) {
    	if (getEventKeyState()) {
			if (isKeyDown(KEY_W)) {
			  look_z += 0.05f
			}
			if (isKeyDown(KEY_S)) {
			  look_z -= 0.05f
			}
			if (isKeyDown(KEY_A)) {
			  look_x -= 0.05f
			}
			if (isKeyDown(KEY_D)) {
			  look_x += 0.05f
			}
			if (isKeyDown(KEY_Q)) {
			  rotation = (rotation - 0.05f) % 360.0f
			}
			if (isKeyDown(KEY_E)) {
			  rotation = (rotation + 0.05f) % 360.0f
			}
			// Make it toggle (instead of taking repeat events, like isKeyDown does)
			if (getEventKey() == KEY_F && !isRepeatEvent()) {
				drawAxes = !(drawAxes)
			}
    	}
    }
  }

  def adjust_cam(){
	val v = Display.getDisplayMode.getWidth.toFloat/Display.getDisplayMode.getHeight.toFloat
	printf("v:%f",v)
	glMatrixMode(GL_PROJECTION)
	glLoadIdentity
	//glFrustum(-v,v,-10,10,0,10)
	glMatrixMode(GL_MODELVIEW)
  }

  def draw_axes() {
    glDisable(GL_LIGHTING)
    glBegin(GL_LINES)
    	// x-axis
    	glColor3f(1.0f, 0.0f, 0.0f)
    	glVertex3f(0.0f, 0.0f, 0.0f)
    	glVertex3f(1.0f, 0.0f, 0.0f)
    	// y-axis
    	glColor3f(0.0f, 1.0f, 0.0f)
    	glVertex3f(0.0f, 0.0f, 0.0f)
    	glVertex3f(0.0f, 1.0f, 0.0f)
    	// z-axis
    	glColor3f(0.0f, 0.0f, 1.0f)
    	glVertex3f(0.0f, 0.0f, 0.0f)
    	glVertex3f(0.0f, 0.0f, 1.0f)
    glEnd()
    glEnable(GL_LIGHTING)
  }

  def render() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity()
    var eye = new Matrix4f()
    eye.m00 = 1.0f
    eye.m11 = 1.0f
    eye.m22 = 1.0f
    eye.m33 = 1.0f
    eye.m03 = eye_x
    eye.m13 = eye_y
    eye.m23 = eye_z
    eye.rotate(rotation.toRadians, new Vector3f(0.0f, 1.0f, 0.0f))
    gluLookAt(eye.m03, eye.m13, eye.m23, look_x, look_y, look_z, 0.0f, 1.0f, 0.0f)

    if (drawAxes)
      draw_axes()
    
    glTranslatef(0.0f, 0.0f, 0.0f)
    //glRotatef(rotation.toDegrees, 0.0f, 1.0f, 0.0f)
    drawScene()
  }

  def drawScene() {
    // for obj in scene, call draw on it?
    glDisable(GL_LIGHTING)
    glBegin(GL_QUADS)
    	glColor3f(1.0f, 1.0f, 1.0f)
    	glVertex3f(-0.25f, 0.25f, 0.0f)
    	
    	glColor3f(1.0f, 0.0f, 0.0f)
    	glVertex3f(0.25f, 0.25f, 0.0f)
    	
    	glColor3f(0.0f, 1.0f, 0.0f)
    	glVertex3f(0.25f, -0.25f, 0.0f)
    	
    	glColor3f(0.0f, 0.0f, 1.0f)
    	glVertex3f(-0.25f, -0.25f, 0.0f)
    glEnd()
    glEnable(GL_LIGHTING)
  }

  def event_loop() {
    while(!finished) {
      Display.update()
      
      
      key_events()
      render()
      
      Display.sync(30)
    }
    clean_up()
  }

}