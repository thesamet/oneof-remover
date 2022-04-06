package com.thesamet

import protocgen._
import com.google.protobuf.DescriptorProtos._
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse
import com.google.protobuf.ByteString
import scala.jdk.CollectionConverters._

object OneofRemover extends CodeGenApp {
  def process(request: CodeGenRequest): CodeGenResponse = {
    CodeGenResponse.succeed(
      files = Seq(
        CodeGeneratorResponse.File.newBuilder()
          .setName("descriptors.bin")
          .setContentBytes(removeOneofs(request))
          .build,
      ),
      supportedFeatures = Set(CodeGeneratorResponse.Feature.FEATURE_PROTO3_OPTIONAL)
    )
  }

  def removeOneofs(req: CodeGenRequest): ByteString = {
    FileDescriptorSet.newBuilder()
      .addAllFile(req.allProtos.map(p => removeOneofsFromFile(p.toProto)).asJava)
      .build()
      .toByteString
  }

  def removeOneofsFromFile(file: FileDescriptorProto): FileDescriptorProto = {
    val b = file.toBuilder()
    b.getMessageTypeBuilderList().asScala.foreach(
      removeOneofsFromMessage(_)
    )
    b.build()
  }

  def removeOneofsFromMessage(message: DescriptorProto.Builder): Unit = {
    message.getNestedTypeBuilderList().asScala.foreach(
      removeOneofsFromMessage(_)
    )
    message.clearOneofDecl()
    message.getFieldBuilderList().asScala.foreach(_.clearOneofIndex())
  }
}
