package scalagl

object cubeTest extends ScalaGL {
	override def drawScene() = {
		drawCube((0.0f, 0.0f, 0.0f), 1.0f, (1.0f, 0.0f, 0.0f))
	}
}

object realCubeTest extends ScalaGL {
	override def drawScene() = {
	  0 set ('a := 1.0f)
	  0 point ('center := (0.0f, 0.0f, 0.0f))
	  0 color ('color := ('a, 0.0f, 'a))
	  0 cube ('center, .5f, 'color)
	  1 point ('center2 := (0.0f, 0.6f, 0.0f))
	  0 set ('b := .3f)
	  1 cube ('center2, 'b, 'color)
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
    0 set ('notAPottyWord := 80085f)
    0 printfloat 'notAPottyWord
    1 point ('p1 := (0.0f, 1.0f, 2.0f))
    1 printpoint 'p1
    1 color ('c1 := (0.0f, 1.0f, 2.0f))
    1 printcolor 'c1
    2 point ('p2 := ('notAPottyWord, 0.0f, 1.0f))
    2 printpoint 'p2
    start
  }
}

object mathTest extends ScalaGL {
  override def drawScene() = {
    0 set ('a := 0.0f)
    1 set ('a := 'a + 0.1f)
    1 printfloat 'a
    1 set ('a := 'a - 0.1f)
    1 printfloat 'a
    1 set ('a := ('a + .2f) * 0.5f)
    1 printfloat 'a
    1 set ('a := ('a + .2f) / 0.6f)
    1 printfloat 'a
    start
  }
}