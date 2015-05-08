import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object MyBuild extends Build { 
  val buildOrganization  = "com.myproject"
  val buildVersion       = "0.13.5"
  val buildScalaVersion  = "2.11.4"

  val rootProjectId = "sidescroller"

  lazy val argonaut = "io.argonaut" %% "argonaut" % "6.0.4"  
  lazy val sprayjson = "io.spray" %%  "spray-json" % "1.3.1"

  val lwjglVersion = "3.0"

  object Settings {

    lazy val lwjglNative = {
      def os = System.getProperty("os.name").toLowerCase match {
        case "linux" => ("linux", ":")
        case "mac os x" => ("macosx", ":")
        case "windows xp" | "windows vista" | "windows 7" => ("windows", ";")
        case "sunos" => ("solaris", ":")
        case _ => ("unknown","")
      }

      ("lib/lwjgl/native/" + os._1 + "/x64", os._2)
    }
    

    val newPath = 
        System.getProperty("java.library.path") + lwjglNative._2 + lwjglNative._1

    lazy val rootProject = Defaults.defaultSettings ++ Seq(
      fork in run := true,
      organization    := buildOrganization,
      version         := buildVersion,
      scalaVersion    := buildScalaVersion,
      libraryDependencies ++= Seq(argonaut, sprayjson),
      javaOptions += "-Djava.library.path=" + newPath,
      javaOptions += "-XX:MaxGCPauseMillis=4"
    ) ++ assemblySettings
  } 

  lazy val root = Project(id=rootProjectId, base=file("."), settings=Settings.rootProject)
}