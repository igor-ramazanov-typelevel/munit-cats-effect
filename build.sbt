import org.typelevel.sbt.gha.WorkflowStep.Run
import org.typelevel.sbt.gha.WorkflowStep.Sbt

ThisBuild / tlBaseVersion := "2.1"

ThisBuild / developers += tlGitHubDev("milanvdm", "Milan van der Meer")
ThisBuild / startYear := Some(2021)

ThisBuild / crossScalaVersions := List("3.3.6", "2.12.20", "2.13.16")

ThisBuild / githubOwner := "igor-ramazanov-typelevel"
ThisBuild / githubRepository := "munit-cats-effect"

ThisBuild / githubWorkflowPublishPreamble := List.empty
ThisBuild / githubWorkflowUseSbtThinClient := true
ThisBuild / githubWorkflowPublish := List(
  Run(
    commands = List("echo \"$PGP_SECRET\" | gpg --import"),
    id = None,
    name = Some("Import PGP key"),
    env = Map("PGP_SECRET" -> "${{ secrets.PGP_SECRET }}"),
    params = Map(),
    timeoutMinutes = None,
    workingDirectory = None
  ),
  Sbt(
    commands = List("+ publish"),
    id = None,
    name = Some("Publish"),
    cond = None,
    env = Map("GITHUB_TOKEN" -> "${{ secrets.GB_TOKEN }}"),
    params = Map.empty,
    timeoutMinutes = None,
    preamble = true
  )
)
ThisBuild / gpgWarnOnFailure := false

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "munit-cats-effect",
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "1.1.1",
      "org.typelevel" %%% "cats-effect" % "3.7-4972921"
    ),
    publishTo := githubPublishTo.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
  )
