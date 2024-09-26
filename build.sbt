import org.scalajs.linker.interface.ModuleSplitStyle

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
      name := "SpinoCup",
      scalaVersion := "3.5.1",
      scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature"),
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= {
        _.withModuleKind(ModuleKind.ESModule)
          .withModuleSplitStyle(
              ModuleSplitStyle.SmallModulesFor(List("example")))
      },
      libraryDependencies ++= Seq(
          "org.scala-js" %%% "scalajs-dom" % "2.4.0",
          "com.raquo" %%% "laminar" % "17.1.0"
      )
  )
