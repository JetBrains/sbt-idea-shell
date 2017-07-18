//resolvers += Resolver.url(
//  "bintray-sbt-plugin-releases",
//  url("https://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
//  Resolver.ivyStylePatterns)
//resolvers += Opts.resolver.sonatypeSnapshots

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "2.0.0")

//addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.1")
