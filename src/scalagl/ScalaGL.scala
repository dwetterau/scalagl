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
  
  // Actual rendering stuff
  
  var drawAxes = true
  var finished = false
  
  var rotation = 0.0f
  var uprotation = 0.0f
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
    Display.setTitle("Scala LWJGL")
    Display.setVSyncEnabled(true)
    Display.setDisplayMode(new DisplayMode(800,600))
    Display.create()
    glEnable(GL_DOUBLEBUFFER)
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_LIGHTING)
	glEnable(GL_LIGHT0)
    adjust_cam
    event_loop
  }

  def clean_up() {
	  Display.destroy()
  }
  
  def mouse_events() {
    import Mouse._
    while (Mouse.next()) {
      if (isButtonDown(0)){
        rotation = (rotation + .005f * getEventDX) % (Math.PI.toFloat * 2.0f)
        uprotation = (uprotation - .05f * getEventDY)
        if (uprotation > Math.PI.toFloat / 2.0f) {
          uprotation -= Math.PI.toFloat
        } else if (uprotation < -Math.PI.toFloat / 2.0f) {
          uprotation += Math.PI.toFloat
        }
      }
      if (getEventDWheel != 0) {
    	var delta = 0.0f;  
        if(getEventDWheel > 0) {
    	    delta = 0.98f
        } else {
        	delta = 1.02f    	  
    	}
        eye_x *= delta
        eye_y *= delta
        eye_z *= delta
        println(eye_y)
      }
    }
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
			  rotation = (rotation - 0.05f) % (Math.PI.toFloat * 2.0f)
			}
			if (isKeyDown(KEY_E)) {
			  rotation = (rotation + 0.05f) % (Math.PI.toFloat * 2.0f)
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
    var ang = (rotation + Math.PI / 2.0f) % (2 * Math.PI) 
    var vect = new Vector3f(Math.cos(ang).toFloat, 0.0f, -Math.sin(ang).toFloat)
    vect.normalise()
    eye.rotate(uprotation, vect)
    
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

  def event_loop {
    while(!finished) {
      Display.update
      
      key_events
      mouse_events
      render
      
      Display.sync(30)
      finished = true
    }
    clean_up
  }
  
  
  
  // Scala DSL stuff
  abstract sealed class glLine
  case class Set(fn:Function0[Unit]) extends glLine
  case class PointDef(fn:Function0[Unit]) extends glLine
  case class ColorDef(fn:Function0[Unit]) extends glLine
  case class PrintFloat(s: Symbol) extends glLine
  case class PrintTuple(s: Symbol) extends glLine
  
  case class Assignment(sym:Symbol) {
      def :=(v:Float):Function0[Unit] = (() => assignments.set(sym, v))
      def :=(v:(Any, Any, Any)):Function0[Unit] = {
        (() => assignments.set(sym, v))
      }
  }
  // Reads in the lines of the program and puts them in a list
  case class BuildLine(uselessNumber: Int) {
    object printfloat {
      def apply(s: Symbol) = {
        lines = lines :+ PrintFloat(s)
      }
    }
    
    object set { 
      def apply(fn:Function0[Unit]) = lines = lines :+ Set(fn)
    }
    
    object color {
      def apply(fn:Function0[Unit]) = lines = lines :+ ColorDef(fn)
    }
    
    object point {
      def apply(fn:Function0[Unit]) = lines = lines :+ PointDef(fn)
    }
    
    object printcolor {
      def apply(s: Symbol) = {
        lines = lines :+ PrintTuple(s)
      }
    }
    object printpoint {
      def apply(s: Symbol) = {
        lines = lines :+ PrintTuple(s)
      }
    }
  }
  
  def start() {
    executeLine(0)
  }
  
  private def executeLine(index: Int) {
    if (index >= lines.length) {
      return
    }
    lines(index) match {
      case Set(fn:Function0[Unit]) => {
    	fn()
    	executeLine(index + 1)
      }
      case PointDef(fn:Function0[Unit]) => {
        fn()
        executeLine(index + 1)
      }
      case ColorDef(fn:Function0[Unit]) => {
        fn()
        executeLine(index + 1)
      }
      case PrintFloat(s:Symbol) => {
        println(assignments.float(s))
        executeLine(index + 1)
      }
      case PrintTuple(s:Symbol) => {
        println(assignments.tuple(s))
        executeLine(index + 1)
      }
    }
  }
  
  class Assignments[Z] {
    val tupleMap = HashMap[Symbol, (Any, Any, Any)]()
    val floatMap = HashMap[Symbol, Z]()
    
    def set[W >: (Any, Any, Any) with Z](k:Symbol, v:W) = v match {
      case y:(Any, Any, Any) => tupleMap(k) = y
      case z:Z => floatMap(k) = z
    }
    def tuple(k:Symbol):(Float, Float, Float) = {
      var v = tupleMap(k);
      var v1 = 0.0f 
      var v2 = 0.0f
	  var v3 = 0.0f
	  v._1 match {
	    case s:Symbol => {
	      println("we're like looking up the symbol: ", s)
	      v1 = assignments.float(s);
	      println("We have found the symbol and set v1=)", v1)
	    }
	    case f:Float => v1 = f
	    case _ => v1 = 0.0f
	  }
	  v._2 match {
	    case s:Symbol => {
	      v2 = assignments.float(s);
	    }
	    case f:Float => v2 = f
	    case _ => v2 = 0.0f
	  }
	  v._3 match {
	    case s:Symbol => {
	      v3 = assignments.float(s);
	    }
	    case f:Float => v3 = f
	    case _ => v3 = 0.0f
	  }
	  return (v1, v2, v3)
    }
    def float(k:Symbol):Z = floatMap(k);
    
    def any(k:Symbol):Any = {
      (tupleMap.get(k), floatMap.get(k)) match {
        case (Some(y), None) => y
        case (None, Some(z)) => z
        case _ => None
      }
    }
  }
  
  var assignments = new Assignments[Float];
  var lines = new Array[glLine](0)
  var labels = HashMap[String, Int]()

  
  implicit def int2BuildLine(i: Int) = BuildLine(i)
  implicit def symbol2Assignment(sym:Symbol) = Assignment(sym)
}