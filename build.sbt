name := "oneof-remover"

scalaVersion := "2.13.4"

// This project triggers a protoc run that uses a custom plugin (OneofRemover)
// that produces a single FileDescriptorSet as an output. The
// FileDescriptorSet is identical to the input request, but with all the
// oneofs removed and the fields inside them retained directly within the message.
val protos = project.in(file("protos"))
    .settings(
        Compile / PB.targets := Seq(
          protocbridge.JvmGenerator("oneof-remover", com.thesamet.OneofRemover) -> (Compile / sourceManaged).value / "remover"
        ),
   )

val client = project.in(file("client"))
    .dependsOn(protos)  // to ensure descriptors.bin is generated first
    .settings(
        Compile / PB.targets := Seq(
          scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
        ),

        // By default, sbt-protoc will add the protos/src/main/protobuf to the
        // include path. We want to avoid that so protoc reads from the
        // descriptor set and not from the files on disk.
        Compile / PB.includePaths := (Compile / PB.protoSources).value,

        // Add the descriptor_set_in command line and add all protos files
        // relative locations (that's how they get indexed in the descriptor
        // set)
        Compile / PB.protocOptions ++= {
            val protosDir = (protos / Compile / PB.protoSources).value

            Seq(
            "--descriptor_set_in=" + (protos / Compile / sourceManaged).value / "remover" / "descriptors.bin") ++
            protosDir.flatMap { d =>
                (d ** "*.proto").get.map {
                    p =>
                        protosDir.head.relativize(p).get.toString
                }
            }
        },

        libraryDependencies ++= Seq(
          "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
        )
    )
