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
  
  var rotation = 3.0f * Math.PI.toFloat / 2.0f
  var uprotation = Math.PI.toFloat / 2.0f
  var look_x = 0.0f
  var look_y = 0.0f
  var look_z = 0.0f
  
  var eye_x = -2.0f
  var eye_y = 0.0f
  var eye_z = 0.0f
  var delta = 2.0f;  
  
  
  
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
        rotation = rotation + .005f * getEventDX
        uprotation = uprotation - .005f * getEventDY
        fix_angles
      }
      if (getEventDWheel != 0) {
        if(getEventDWheel > 0) {
    	    delta -= .02f
        } else {
        	delta += .02f   	  
    	}
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
			if (isKeyDown(KEY_E)) {
			  // zoom in
			  delta -= 0.02f
			}
			if (isKeyDown(KEY_Q)) {
			  // zoom out
			  delta += .02f
			}
			if (isKeyDown(KEY_W)) {
			  // uprotation +
			  uprotation -= .05f
			}
			if (isKeyDown(KEY_S)) {
			  // uprotation -
			  uprotation += .05f
			}
			if (isKeyDown(KEY_D)) {
			  rotation = rotation - 0.05f
			}
			if (isKeyDown(KEY_A)) {
			  rotation = rotation + 0.05f
			}
			// Make it toggle (instead of taking repeat events, like isKeyDown does)
			if (getEventKey() == KEY_F && !isRepeatEvent()) {
				drawAxes = !(drawAxes)
			}
			fix_angles
    	}
    }
  }
  
  def fix_angles() {
    // flips around the camera correctly
	if (uprotation > Math.PI.toFloat) {
      uprotation = Math.PI.toFloat - (uprotation - Math.PI.toFloat)
      rotation += Math.PI.toFloat
    } else if (uprotation < 0) {
      uprotation = -uprotation
      rotation += Math.PI.toFloat
    }
	rotation %= (Math.PI.toFloat * 2.0f)
  }

  def adjust_cam(){
	val v = Display.getDisplayMode.getWidth.toFloat/Display.getDisplayMode.getHeight.toFloat
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
    var length = delta
    var x = length * Math.sin(uprotation).toFloat * Math.cos(rotation - Math.PI).toFloat
    var y = length * Math.cos(uprotation).toFloat
    var z = length * Math.sin(uprotation).toFloat * Math.sin(rotation - Math.PI).toFloat
    //gluLookAt(eye_x, eye_y, eye_z, look_x, look_y, look_z, 0.0f, 1.0f, 0.0f)
    gluLookAt(x, y, z, look_x, look_y, look_z, 0.0f, 1.0f, 0.0f)
    if (drawAxes) {
      draw_axes()
    }
    glTranslatef(0.0f, 0.0f, 0.0f)
    //glRotatef(rotation.toDegrees, 0.0f, 1.0f, 0.0f)
    drawScene()
  }

  def drawScene() {
    //By default, draws nothing
  }
  
  // assumes vertices were passed in correct order (clockwise)
  def drawTriangle(p1:(Float, Float, Float), p2:(Float, Float, Float), p3:(Float, Float, Float), c:(Float, Float, Float)) {
    glColor3f(c._1, c._2, c._3)
    glDisable(GL_LIGHTING)
    glBegin(GL_TRIANGLES)
    glVertex3f(p1._1, p1._2, p1._3)
    glVertex3f(p2._1, p2._2, p2._3)
    glVertex3f(p3._1, p3._2, p3._3)
    glEnd
  }
  
  def drawSphere(p:(Float, Float, Float), r: Float, c:(Float, Float, Float)) {
    glPushMatrix
    glTranslatef(p._1, p._2, p._3);
    glColor3f(c._1, c._2, c._3)
	glDisable(GL_LIGHTING)
	val sphere = new Sphere;
	sphere.setDrawStyle(GLU_FILL)
	sphere.setNormals(GLU_SMOOTH)
	val quality = 25
	sphere.draw(r, quality, quality)
    glPopMatrix()
  }
	
  def drawCube(p:(Float, Float, Float), s: Float, c:(Float, Float, Float)) {
	  glPushMatrix
	  glTranslatef(p._1, p._2, p._3);
	  glScalef(s, s, s)
	  glColor3f(c._1, c._2, c._3)
	  glDisable(GL_LIGHTING)
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
	  glEnable(GL_LIGHTING)
	  glPopMatrix
}

  def event_loop {
    while(!finished) {
      Display.update
      
      key_events
      mouse_events
      render
      
      Display.sync(30)
      //finished = true
    }
    clean_up
  }
  
  
  
  // Scala DSL stuff
  abstract sealed class glLine
  case class SetDef(fn:Function0[Unit]) extends glLine
  case class PointDef(fn:Function0[Unit]) extends glLine
  case class ColorDef(fn:Function0[Unit]) extends glLine
  case class PrintFloatDef(s: Symbol) extends glLine
  case class PrintTuple(s: Symbol) extends glLine
  case class CubeDef(p: Symbol, s: Float, c: Symbol) extends glLine
  case class CubeDefs(p: Symbol, s: Symbol, c: Symbol) extends glLine
  case class CubeDeff(p: Symbol, s: Function0[Float], c: Symbol) extends glLine
  case class SphereDef(p: Symbol, r: Float, c: Symbol) extends glLine
  case class SphereDefs(p: Symbol, r: Symbol, c: Symbol) extends glLine
  case class SphereDeff(p: Symbol, r: Function0[Float], c: Symbol) extends glLine
  case class TriangleDef(p1: Symbol, p2:Symbol, p3:Symbol, c: Symbol) extends glLine
  case class LabelDef(s: String) extends glLine
  case class CheckDef(fn:Function0[Boolean], label:String) extends glLine
  
  case class IfBuilder(fn:Function0[Boolean]) {
    def goto(label:String) = {
      lines = lines += CheckDef(fn, label)
    }
  }
  
  case class Assignment(sym:Symbol) {
      def :=(v:(Any, Any, Any)):Function0[Unit] = (() => assignments.set(sym, v))
      def :=(v:Float):Function0[Unit] = (() => assignments.set(sym, v))
      def :=(v:Function0[Float]):Function0[Unit] = (() => assignments.set(sym, v()))
      def :=(v:Symbol):Function0[Unit] = (() => assignments.set(sym, assignments.float(v)))
  }
  
  case class MathFn(left:Function0[Float]) {
      // Addition
	  def +(right:Symbol):Function0[Float] = (() => left() + assignments.float(right))
      def +(right:Function0[Float]):Function0[Float] = (() => left() + right())
      def +(right:Float):Function0[Float] = (() => left() + right)
      def +(right:Double):Function0[Float] = (() => left() + right.toFloat)
      // Subtraction
      def -(right:Symbol):Function0[Float] = (() => left() - assignments.float(right))
      def -(right:Function0[Float]):Function0[Float] = (() => left() - right())
      def -(right:Float):Function0[Float] = (() => left() - right)
      def -(right:Double):Function0[Float] = (() => left() - right.toFloat)
      // Division
      def /(right:Symbol):Function0[Float] = (() => left() / assignments.float(right))
      def /(right:Function0[Float]):Function0[Float] = (() => left() / right())
      def /(right:Float):Function0[Float] = (() => left() / right)
      def /(right:Double):Function0[Float] = (() => left() / right.toFloat)
      // Multiplication
      def *(right:Symbol):Function0[Float] = (() => left() * assignments.float(right))
      def *(right:Function0[Float]):Function0[Float] = (() => left() * right())
      def *(right:Float):Function0[Float] = (() => left() * right)
      def *(right:Double):Function0[Float] = (() => left() * right.toFloat)
  }
  
  case class BinaryFn(left:Function0[Float]) {
    // Equality
    def ==(right:Symbol):Function0[Boolean] = (() => left() == assignments.float(right))
    def ==(right:Function0[Float]):Function0[Boolean] = (() => left() == right())
    def ==(right:Float):Function0[Boolean] = (() => left() == right)
    // not equality
    def !=(right:Symbol):Function0[Boolean] = (() => left() != assignments.float(right))
    def !=(right:Function0[Float]):Function0[Boolean] = (() => left() != right())
    def !=(right:Float):Function0[Boolean] = (() => left() != right)
    // LE
    def <=(right:Symbol):Function0[Boolean] = (() => left() <= assignments.float(right))
    def <=(right:Function0[Float]):Function0[Boolean] = (() => left() <= right())
    def <=(right:Float):Function0[Boolean] = (() => left() <= right)
    // L
    def <(right:Symbol):Function0[Boolean] = (() => left() < assignments.float(right))
    def <(right:Function0[Float]):Function0[Boolean] = (() => left() < right())
    def <(right:Float):Function0[Boolean] = (() => left() < right)
    // GE
    def >=(right:Symbol):Function0[Boolean] = (() => left() >= assignments.float(right))
    def >=(right:Function0[Float]):Function0[Boolean] = (() => left() >= right())
    def >=(right:Float):Function0[Boolean] = (() => left() >= right)
    // G
    def >(right:Symbol):Function0[Boolean] = (() => left() > assignments.float(right))
    def >(right:Function0[Float]):Function0[Boolean] = (() => left() > right())
    def >(right:Float):Function0[Boolean] = (() => left() > right)
  }
  
  // Reads in the lines of the program and puts them in a list

  object printfloat {
    def apply(s: Symbol) = {
      lines = lines += PrintFloatDef(s)
    }
  }

  def pow(b:Symbol, p:Symbol):Function0[Float] = (() => math.pow(assignments.float(b), assignments.float(p)).toFloat)
  def pow(b:Symbol, p:Float):Function0[Float] = (() => math.pow(assignments.float(b), p).toFloat)
  def pow(b:Symbol, p:Function0[Float]):Function0[Float] = (() => math.pow(assignments.float(b), p()).toFloat)
  def pow(b:Float, p:Symbol):Function0[Float] = (() => math.pow(b, assignments.float(p)).toFloat)
  def pow(b:Float, p:Float):Function0[Float] = (() => math.pow(b, p).toFloat)
  def pow(b:Float, p:Function0[Float]):Function0[Float] = (() => math.pow(b, p()).toFloat)
  def pow(b:Function0[Float], p:Symbol):Function0[Float] = (() => math.pow(b(), assignments.float(p)).toFloat)
  def pow(b:Function0[Float], p:Float):Function0[Float] = (() => math.pow(b(), p).toFloat)
  def pow(b:Function0[Float], p:Function0[Float]):Function0[Float] = (() => math.pow(b(), p()).toFloat)
  def sqrt(s:Symbol):Function0[Float] = (() => math.sqrt(assignments.float(s).toDouble).toFloat)
  def cos(s:Symbol):Function0[Float] = (() => math.cos(assignments.float(s).toDouble).toFloat)
  def sin(s:Symbol):Function0[Float] = (() => math.sin(assignments.float(s).toDouble).toFloat)
  def tan(s:Symbol):Function0[Float] = (() => math.tan(assignments.float(s).toDouble).toFloat)
  def sqrt(f:Float):Function0[Float] = (() => math.sqrt(f).toFloat)
  def cos(f:Float):Function0[Float] = (() => math.cos(f).toFloat)
  def sin(f:Float):Function0[Float] = (() => math.sin(f).toFloat)
  def tan(f:Float):Function0[Float] = (() => math.tan(f).toFloat)
  def sqrt(fn:Function0[Float]):Function0[Float] = (() => math.sqrt(fn()).toFloat)
  def cos(fn:Function0[Float]):Function0[Float] = (() => math.cos(fn()).toFloat)
  def sin(fn:Function0[Float]):Function0[Float] = (() => math.sin(fn()).toFloat)
  def tan(fn:Function0[Float]):Function0[Float] = (() => math.tan(fn()).toFloat)
  def acos(s:Symbol):Function0[Float] = (() => math.acos(assignments.float(s).toDouble).toFloat)
  def asin(s:Symbol):Function0[Float] = (() => math.asin(assignments.float(s).toDouble).toFloat)
  def atan(s:Symbol):Function0[Float] = (() => math.atan(assignments.float(s).toDouble).toFloat)
  def acos(f:Float):Function0[Float] = (() => math.acos(f.toDouble).toFloat)
  def asin(f:Float):Function0[Float] = (() => math.asin(f.toDouble).toFloat)
  def atan(f:Float):Function0[Float] = (() => math.atan(f.toDouble).toFloat)
  def acos(fn:Function0[Float]):Function0[Float] = (() => math.acos(fn()).toFloat)
  def asin(fn:Function0[Float]):Function0[Float] = (() => math.asin(fn()).toFloat)
  def atan(fn:Function0[Float]):Function0[Float] = (() => math.atan(fn()).toFloat)
  def abs(s:Symbol):Function0[Float] = (() => math.abs(assignments.float(s)))
  def abs(f:Float):Function0[Float] = (() => math.abs(f))
  def abs(fn:Function0[Float]):Function0[Float] = (() => math.abs(fn()))
  
  object set { 
    def apply(fn:Function0[Unit]) = lines = lines += SetDef(fn)
  }
    
  object color {
    def apply(fn:Function0[Unit]) = lines = lines += ColorDef(fn)
  }
    
  object point {
    def apply(fn:Function0[Unit]) = lines = lines += PointDef(fn)
  }
    
  object printcolor {
    def apply(s: Symbol) = {
      lines = lines += PrintTuple(s)
    }
  }
  object printpoint {
    def apply(s: Symbol) = {
      lines = lines += PrintTuple(s)
    }
  }
  object cube {
    def apply(s:(Symbol, Any, Symbol)) = {
      s._2 match {
        case c:Symbol => {
      	  lines = lines += CubeDefs(s._1, c, s._3)
        }
        case f:Float => {
      	  lines = lines += CubeDef(s._1, f, s._3)
        }
        case fn:Function0[Float] => {
      	  lines = lines += CubeDeff(s._1, fn, s._3)
        }
      }
    }
  }
  
  object sphere {
    def apply(s:(Symbol, Any, Symbol)) = {
      s._2 match {
        case c:Symbol => {
      	  lines = lines += SphereDefs(s._1, c, s._3)
        }
        case f:Float => {
      	  lines = lines += SphereDef(s._1, f, s._3)
        }
        case fn:Function0[Float] => {
      	  lines = lines += SphereDeff(s._1, fn, s._3)
        }
      }
    }
  }
  
  object triangle {
    def apply(s:(Symbol, Symbol, Symbol, Symbol)) = {
      lines = lines += TriangleDef(s._1, s._2, s._3, s._4)
    }
  }
  
  object label {
    def apply(s:String) = {
      lines = lines += LabelDef(s)
      labels(s) = lines.length
    }
  }
  object check {
    def apply(fn:Function0[Boolean]) = IfBuilder(fn)
  }
  
  def start() {
    executeLine(0)
  }
  
  private def executeLine(index: Int) {
    if (index >= lines.length) {
      return
    }
    lines(index) match {
      case SetDef(fn:Function0[Unit]) => {
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
      case PrintFloatDef(s:Symbol) => {
        println(assignments.float(s))
        executeLine(index + 1)
      }
      case PrintTuple(s:Symbol) => {
        println(assignments.tuple(s))
        executeLine(index + 1)
      }
      case CubeDef(p: Symbol, s: Float, c: Symbol) => {
        var position = assignments.tuple(p)
        var color = assignments.tuple(c)
        drawCube(position, s, color)
        executeLine(index + 1)
      }
      case CubeDefs(p: Symbol, s: Symbol, c: Symbol) => {
        var position = assignments.tuple(p)
        var color = assignments.tuple(c)
        var scale = assignments.float(s)
        drawCube(position, scale, color)
        executeLine(index + 1)
      }
      case CubeDeff(p: Symbol, s: Function0[Float], c: Symbol) => {
        var position = assignments.tuple(p)
        var color = assignments.tuple(c)
        drawCube(position, s(), color)
        executeLine(index + 1)
      }
      case SphereDef(p: Symbol, r: Float, c: Symbol) => {
        var position = assignments.tuple(p)
        var color = assignments.tuple(c)
        drawSphere(position, r, color)
        executeLine(index + 1)
      }
      case SphereDefs(p: Symbol, r: Symbol, c: Symbol) => {
        var position = assignments.tuple(p)
        var color = assignments.tuple(c)
        var scale = assignments.float(r)
        drawSphere(position, scale, color)
        executeLine(index + 1)
      }
      case SphereDeff(p: Symbol, r: Function0[Float], c: Symbol) => {
        var position = assignments.tuple(p)
        var color = assignments.tuple(c)
        drawSphere(position, r(), color)
        executeLine(index + 1)
      }
      case TriangleDef(p1: Symbol, p2: Symbol, p3: Symbol, c: Symbol) => {
        var po1 = assignments.tuple(p1)
        var po2= assignments.tuple(p2)
        var po3 = assignments.tuple(p3)
        var color = assignments.tuple(c)
        drawTriangle(po1, po2, po3, color)
        executeLine(index + 1)
      }
      case LabelDef(s:String) => {
        executeLine(index + 1)
      }
      case CheckDef(fn:Function0[Boolean], gotoLabel:String) => {
        if(fn()) {
          executeLine(labels(gotoLabel))
        } else {
          executeLine(index + 1)
        }
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
	      v1 = assignments.float(s);
	    }
	    case f:Float => v1 = f
	    case fn:Function0[Float] => v1 = fn()
	    case _ => v1 = 0.0f
	  }
	  v._2 match {
	    case s:Symbol => {
	      v2 = assignments.float(s);
	    }
	    case f:Float => v2 = f
	    case fn:Function0[Float] => v2 = fn()
	    case _ => v2 = 0.0f
	  }
	  v._3 match {
	    case s:Symbol => {
	      v3 = assignments.float(s);
	    }
	    case f:Float => v3 = f
	    case fn:Function0[Float] => v3 = fn()
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
  var lines = scala.collection.mutable.ArrayBuffer.empty[glLine]
  var labels = HashMap[String, Int]()

  implicit def symbol2Assignment(sym:Symbol) = Assignment(sym)
  
  implicit def float2BinaryFn(f:Float) = BinaryFn(() => f)
  implicit def symbol2BinaryFn(s:Symbol) = BinaryFn(() => assignments.float(s))
  implicit def fnOfInt2BinaryFn(fn:Function0[Float]) = BinaryFn(fn)
  implicit def float2MathFn(f:Float) = MathFn(() => f)
  implicit def symbol2MathFn(s:Symbol) = MathFn(() => assignments.float(s))
  implicit def fnOfFloat2MathFn(fn:Function0[Float]) = MathFn(fn)
}