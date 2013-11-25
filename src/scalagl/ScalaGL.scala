package scalagl

import org.lwjgl._
import org.lwjgl.opengl.{Display, DisplayMode}
import org.lwjgl.opengl.GL11._
import org.lwjgl.util.glu.GLU._
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.input._
import math._
import org.lwjgl.util.glu.Sphere
import org.lwjgl.input.Keyboard.KEY_A
import org.lwjgl.input.Keyboard.KEY_D
import org.lwjgl.input.Keyboard.KEY_E
import org.lwjgl.input.Keyboard.KEY_ESCAPE
import org.lwjgl.input.Keyboard.KEY_F
import org.lwjgl.input.Keyboard.KEY_Q
import org.lwjgl.input.Keyboard.KEY_S
import org.lwjgl.input.Keyboard.KEY_W
import org.lwjgl.input.Keyboard.KEY_X
import org.lwjgl.input.Keyboard.KEY_Z
import org.lwjgl.input.Keyboard.getEventKey
import org.lwjgl.input.Keyboard.getEventKeyState
import org.lwjgl.input.Keyboard.isKeyDown
import org.lwjgl.input.Keyboard.isRepeatEvent
import scala.collection.mutable.HashMap

class ScalaGL {
  
  abstract sealed class glLine
  case class Set(name:Symbol)
  
  class Assignments[X, Y, Z] {
    val pointMap = HashMap[Symbol, X]()
    val colorMap = HashMap[Symbol, Y]()
    val floatMap = HashMap[Symbol, Z]()
    
    def set[W >: X with Y with Z](k:Symbol, v:W) = v match {
      case x:X => pointMap(k) = x
      case y:Y => colorMap(k) = y
      case z:Z => floatMap(k) = z
    }
    def point(k:Symbol):X = pointMap(k);
    def color(k:Symbol):Y = colorMap(k);
    def float(k:Symbol):Z = floatMap(k);
    
    def any(k:Symbol):Any = {
      (pointMap.get(k), colorMap.get(k), floatMap.get(k)) match {
        case (Some(x), None, None) => x
        case (None, Some(y), None) => y
        case (None, None, Some(z)) => z
        case _ => None
      }
    }
  }
  
  case class Assignment(sym:Symbol) {
      def :=(v:Point):Function0[Unit] = (() => assignments.set(sym, v))
      def :=(v:Color):Function0[Unit] = (() => assignments.set(sym, v))
      def :=(v:Float):Function0[Unit] = (() => assignments.set(sym, v))
    }

  
  class Point {
    
  }
  
  class Color {
    
  }
  
  var assignments = new Assignments[Point, Color, Float];

  var drawAxes = true
  var finished = false
  
  var rotation = 0.0f
  var look_x = 0.0f
  var look_y = 0.0f
  var look_z = 0.0f
  
  var eye_x = 0.0f
  var eye_y = 0.0f
  var eye_z = 1.5f
  
  
  def main(args: Array[String]) = {
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
    if (isKeyDown(KEY_ESCAPE) || Display.isCloseRequested) {
      finished = true
    }
    while (Keyboard.next()) {
    	if (getEventKeyState()) {
			if (isKeyDown(KEY_W)) {
			  eye_z += 0.05f
			}
			if (isKeyDown(KEY_S)) {
			  eye_z -= 0.05f
			}
			if (isKeyDown(KEY_A)) {
			  eye_x -= 0.05f
			}
			if (isKeyDown(KEY_D)) {
			  eye_x += 0.05f
			}
			if (isKeyDown(KEY_Z)) {
			  eye_y -= 0.05f
			}
			if (isKeyDown(KEY_X)) {
			  eye_y += 0.05f
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
	glFrustum(-v,v,-1,1,1,100)
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
    eye.rotate(rotation, new Vector3f(0.0f, 1.0f, 0.0f))
    gluLookAt(eye.m03, eye.m13, eye.m23, look_x, look_y, look_z, 0.0f, 1.0f, 0.0f)

    if (drawAxes)
      draw_axes()
    
    glTranslatef(0.0f, 0.0f, 0.0f)
    //glRotatef(rotation.toDegrees, 0.0f, 1.0f, 0.0f)
    drawScene()
  }

  def drawScene() {
    //By default, draws nothing
  }
  
  def drawSphere {
    glPushMatrix()
	val sphere = new Sphere;
	sphere.setDrawStyle(GLU_FILL);
	sphere.setNormals(GLU_SMOOTH);
	val quality = 25;
	val radius = .2f
	sphere.draw(radius, quality, quality);
    glPopMatrix()
  }
	
  def drawCube {
	  glPushMatrix
	  glBegin( GL_QUADS )

	  	glNormal3f(1.0f, 0.0f, 0.0f)
	  	glVertex3f(0.5f, -0.5f, 0.5f)
	  	glVertex3f(0.5f, -0.5f, -0.5f)
	  	glVertex3f(0.5f, 0.5f, -0.5f)
	  	glVertex3f(0.5f, 0.5f, 0.5f)

	  	glNormal3f(0.0f, 0.0f, -1.0f)
	  	glVertex3f(0.5f, -0.5f, -0.5f)
	  	glVertex3f(-0.5f, -0.5f, -0.5f)
	  	glVertex3f(-0.5f, 0.5f, -0.5f)
	  	glVertex3f(0.5f, 0.5f, -0.5f)

	  	glNormal3f(-1.0f, 0.0f, 0.0f)
	  	glVertex3f(-0.5f, -0.5f, -0.5f)
	  	glVertex3f(-0.5f, -0.5f, 0.5f)
	  	glVertex3f(-0.5f, 0.5f, 0.5f)
	  	glVertex3f(-0.5f, 0.5f, -0.5f)

	  	glNormal3f(0.0f, 0.0f, 1.0f)
	  	glVertex3f(-0.5f, -0.5f, 0.5f)
	  	glVertex3f(0.5f, -0.5f, 0.5f)
	  	glVertex3f(0.5f, 0.5f, 0.5f)
	  	glVertex3f(-0.5f, 0.5f, 0.5f)

	  	glNormal3f(0.0f, 1.0f, 0.0f)
	  	glVertex3f(0.5f, 0.5f, 0.5f)
	  	glVertex3f(0.5f, 0.5f, -0.5f)
	  	glVertex3f(-0.5f, 0.5f, -0.5f)
	  	glVertex3f(-0.5f, 0.5f, 0.5f)

	  	glNormal3f(0.0f, -1.0f, 0.0f)
	  	glVertex3f(0.5f, -0.5f, 0.5f)
	  	glVertex3f(-0.5f, -0.5f, 0.5f)
	  	glVertex3f(-0.5f, -0.5f, -0.5f)
	  	glVertex3f(0.5f, -0.5f, -0.5f)
	  glEnd
	  glPopMatrix
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
  implicit def symbol2Assignment(sym:Symbol) = Assignment(sym)
}