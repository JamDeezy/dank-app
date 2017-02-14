/**
  * Created by ezdz on 2017-02-14.
  */

object DankApp extends App {
  final val default_path = "people.csv"

  println("Here comes some dank csv")

  // Reserve some cpu from cluster
  import org.apache.spark.sql.SparkSession
  val spark = SparkSession.builder().getOrCreate()

  // Encode People schema
  import org.apache.spark.sql.Encoders
  final case class People(id: Long, name: String, number: Int)
  val schema = Encoders.product[People].schema

  try {
    val filename = if (args.length < 1) { default_path } else { args(0) }

    import spark.implicits._
    val people = spark.read
      .option("header", "true")
      .schema(schema)
      .csv(filename)
      .as[People]

    people.show()

    // Group By
    people.groupBy('id % 2 as "group").count.show

    // Group By Key
    people.groupByKey(x => x.id % 2).count.show

    // Map

  } finally {
    // Release resources from cluster
    spark.stop()
  }
}