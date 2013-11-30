dragon:
	scalac -classpath "/usr/share/lwjgl/jar/*" src/scalagl/dragon.scala src/scalagl/ScalaGL.scala

run:
	scala -Djava.library.path=/usr/share/lwjgl/native/linux -cp ".:/usr/share/lwjgl/jar/*" scalagl.dragon
 	
