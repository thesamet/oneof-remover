package myexample


object Main {
  def main(args: Array[String]): Unit = {
    // f1 is a top-level field since the "here" oneof is removed.
    val p = myexample.proto2.Person(f1=Some(7))
  }
}
