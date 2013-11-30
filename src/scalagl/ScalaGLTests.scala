package scalagl

object cubeTest extends ScalaGL {
	override def drawScene() = {
		drawCube((0.0f, 0.0f, 0.0f), 1.0f, (1.0f, 0.0f, 0.0f))
	}
}

object realCubeTest extends ScalaGL {
	override def drawScene() = {
	  set ('a := 1.0f)
	  point ('center := (0.0f, 0.0f, 0.0f))
	  color ('color := ('a, 0.0f, 'a))
	  cube ('center, .5f, 'color)
	  point ('center2 := (0.0f, 0.6f, 0.0f))
	  set ('b := .3f)
	  cube ('center2, 'b, 'color)
	  start
	}
}

object sphereTest extends ScalaGL {
	override def drawScene() = {
		set ('r := 0.5f)
		point ('center := (0.0f, 0.0f, 0.0f))
		color ('color := (1.0f, 0.0f, 0.0f))
		sphere ('center, 'r, 'color)
		start
	}
}

object triTest extends ScalaGL {
    override def drawScene() = {
        point ('p1 := (0.0f, 0.0f, 0.0f))
        point ('p2 := (0.0f, 1.0f, 0.0f))
        point ('p3 := (0.0f, 0.0f, 1.0f))
		color ('color := (1.0f, 0.0f, 0.0f))
		triangle ('p1, 'p2, 'p3, 'color)
		start
    }
}

object variableTest extends ScalaGL {
  override def drawScene() = {
    set ('notAPottyWord := 80085f)
    printfloat ('notAPottyWord)
    point ('p1 := (0.0f, 1.0f, 2.0f))
    printpoint ('p1)
    color ('c1 := (0.0f, 1.0f, 2.0f))
    printcolor ('c1)
    point ('p2 := ('notAPottyWord, 0.0f, 1.0f))
    printpoint ('p2)
    point ('p3 := (1.0f * 3.0f, 2.0f, 6.0f*'notAPottyWord + 2.0f))
    printpoint ('p3)
    start
  }
}

object mathTest extends ScalaGL {
  override def drawScene() = {
    set ('a := 0.0f)
    set ('a := 'a + 0.1f)
    printfloat ('a)
    set ('a := 'a - 0.1f)
    printfloat ('a)
    set ('a := ('a + .2f) * 0.5f)
    printfloat ('a)
    set ('a := ('a + .2f) / 0.6f)
    printfloat ('a)
    set ('a := sqrt(4.0f))
    printfloat ('a)
    set ('a := sqrt('a))
    printfloat ('a)
    set ('a := sqrt(pow('a, 2)))
    printfloat('a)
    set ('a := pow(2.0f, 3.0f))
    printfloat('a)
    set ('b := 2.0f)
    set ('a := pow('b, 3.0f))
    printfloat('a)
    set ('c := 3.0f)
    set ('a := pow('b, 'c))
    printfloat('a)
    set ('a := pow(('b - 3)*2, 'c*2 - 1))
    printfloat('a)
    start
  }
}

object logicTest extends ScalaGL {
  override def drawScene() = {
    set ('a := 0.0f)
    set ('limit := 1.0f)
    label ("start_loop")
    printfloat ('a)
    set ('a := 'a + .1f)
    check ('a <= 'limit) goto "start_loop"
    println("Finished Loop 1!")
    
    set ('a := 1000.0f)
    label ("loop2")
    printfloat('a)
    set ('a := sqrt('a))
    check (abs('a - sqrt('a)) >= 1.000001f ) goto "loop2"
    println("Finished Loop 2!")
    start
  }
}

object drawBoxen extends ScalaGL {
  override def drawScene() = {
    set ('x := -1.0f)
    set ('s := 0.5f)
    label ("start_loop")
    point ('center := ('x, 0f, 0f))
    color ('color := ('s, 's, 's))
    cube ('center, 's, 'color)
    set ('x := 'x + 's)
    set ('s := 's - .1f)
    check ('s >= 0f) goto "start_loop"
    start
  }
}

object drawTriFractal extends ScalaGL {
  override def drawScene() = {
    triFractal((-0.5f, 0.0f, 0.0f), (0.0f, .707f, 0.0f), (.5f, 0.0f, 0.0f), 3)
  }
  
  def triFractal(p1: (Float, Float, Float), p2: (Float, Float, Float), p3: (Float, Float, Float), depth: Int) {
    point ('p1 := (p1._1, p1._2, p1._3))
	point ('p2 := (p2._1, p2._2, p2._3))
	point ('p3 := (p3._1, p3._2, p3._3))
	color ('color := (1.0f, 0.0f, 0.0f))
	triangle ('p1, 'p2, 'p3, 'color)
	start
	fracSide(p1, p2, p3, depth)
    fracSide(p2, p3, p1, depth)
    fracSide(p3, p1, p2, depth)
  }
  // line segment from a to b with other point on the triangle c
  // did this to avoid work drawing fractal under triangle that is already drawn
  def fracSide(a:(Float, Float, Float), b: (Float, Float, Float), c: (Float, Float, Float), depth:Int) {
    // call subtriangles
    if (depth == 0) return
    var mid = ((a._1  +b._1) * .5f, (a._2 + b._2) * .5f, (a._3 + b._3) * .5f)
    var mid1 = ((a._1 * 2 + b._1 )/ 3f, (a._2 * 2 + b._2)/ 3f, (a._3 * 2 + b._3)/ 3f)
    var mid2 = ((a._1 + b._1 * 2)/ 3f, (a._2 + b._2 * 2)/ 3f, (a._3 + b._3 * 2)/ 3f)
    // new 3rd point is the center  + 2 * the vector from the center to the midpoint, which comes out to be 2 * mid - center
    var center = ((a._1 + b._1 + c._1) / 3.0f, (a._2 + b._2 + c._2) / 3.0f, (a._3 + b._3 + c._3) / 3.0f)
    var newmid = (2 * mid._1 - center._1, 2 * mid._2 - center._2, 2 * mid._3 - center._3)
    point ('p1 := (mid1._1, mid1._2, mid1._3))
	point ('p2 := (newmid._1, newmid._2, newmid._3))
	point ('p3 := (mid2._1, mid2._2, mid2._3))
	color ('color := (1.0f, 0.0f, 0.0f))
	triangle ('p1, 'p2, 'p3, 'color)
	start
    var cfora = ((2 * a._1 + c._1)/3.0f, (2 * a._2 + c._2)/3.0f, (2 * a._3 + c._3)/3.0f)
    var cforb = ((2 * b._1 + c._1)/3.0f, (2 * b._2 + c._2)/3.0f, (2 * b._3 + c._3)/3.0f)
    fracSide(a, mid1, cfora, depth - 1)
    fracSide(mid1, newmid, mid2, depth - 1)
    fracSide(newmid, mid2, mid1, depth - 1)
    fracSide(mid2, b, cforb, depth - 1)
  }
}

object functionDraw extends ScalaGL {
  override def drawScene() = {
    import math.Pi
    set ('x := -2)
    set ('y := -2)
    set ('d := .1f)
    set ('x_lim := 2)
    set ('y_lim := 2)
    label ("x_loop")
    	set ('y := -2)
    	label ("y_loop")
    		set ('new_x := 'x + 'd)
    		set ('new_y := 'y + 'd)
    		
    		set ('p1z := func('x, 'y))
    		set ('p2z := func('x, 'new_y))
    		set ('p3z := func('new_x, 'new_y))
    		set ('p4z := func('new_x, 'y))
    		
    		set ('p1 := ('x, 'y, 'p1z))
    		set ('p2 := ('x, 'new_y, 'p2z))
    		set ('p3 := ('new_x, 'new_y, 'p3z))
    		set ('p4 := ('new_x, 'y, 'p4z))
    		
    		set ('val := func('x, 'y))
    		set ('red := cos('val))
    		set ('blue := sin('val))
    		set ('green := 'val/(2.0f*Pi).toFloat)
    		set ('color := ('red, 'green, 'blue))
    		
    		triangle ('p1, 'p2, 'p3, 'color)
    		triangle ('p3, 'p4, 'p1, 'color)
    		set ('y := 'new_y)
    		check ('y < 'y_lim) goto "y_loop"
    	set('x := 'x + 'd)
    	check ('x < 'x_lim) goto "x_loop"
    start
  }
  def func(x:Symbol, y:Symbol) : Function0[Float] = {
    set ('x2 := pow(x, 2))
    set ('y2 := pow(y, 2))
    return sqrt('x2 + 'y2) + 3.0f * cos(sqrt('x2 + 'y2))
	//return 2.0f * sin(x) * pow(Math.E.toFloat, (-1.0f * ('x2 + 'y2)))
  }
}


object boxSpiral extends ScalaGL {
  override def drawScene() = {
    import math.Pi
    set ('depth := -1.0f)
    label ("drawSpiral")
    set ('t := 0.0f)
    set ('dt := 0.015f)
    set ('r1 := 1.0f)
    set ('r2 := 0.0f)
    label ("start")
    set ('ratio := 't/(2.0f*Pi).toFloat)
    set ('red := cos('t))
    set ('blue := sin('t))
    set ('x := cos('t)*(('r1*'ratio) + ('r2*('ratio - 1.0f))))
    set ('y := sin('t)*(('r1*'ratio) + ('r2*('ratio - 1.0f))))
    set ('z := 'depth*'ratio)
    point ('p := ('x,'y,'z))
    color ('c := ('red,'blue,'ratio))
    cube ('p, 'dt * 2, 'c)
    set ('t := 't + 'dt)
    check ('t <= (2.0f*Pi).toFloat) goto "start"
    set ('depth := 'depth + 'dt)
    check ('depth <= 1.0f) goto "drawSpiral"
    start
    
  }
}