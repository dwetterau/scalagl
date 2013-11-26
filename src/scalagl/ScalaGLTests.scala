package scalagl

object cubeTest extends ScalaGL {
	override def drawScene() = {
		drawCube
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