# oneof-remover

Example of a ScalaPB project that defines a protoc plugin in the project
directory.

This particular protoc plugin generates a modified descriptor set (essentially
transforming the input protos). Another project in the same build generates
Scala source based on the generated descriptor set.

