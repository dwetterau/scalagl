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
		drawSphere
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
    println("Finished!")
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