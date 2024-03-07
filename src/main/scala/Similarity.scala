import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.ml.linalg.{SparseVector, Vector, Vectors}
import org.apache.spark.sql.functions.udf

object Similarity {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    // Spark 세션 초기화
    val spark = SparkSession.builder
      .appName("TechStackSimilarity")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // 예시 데이터 (실제 데이터 로딩 로직 필요)
    val userProfiles = Seq(
      (1, "Backend Developer", Seq("Java", "Spring", "Docker", "Kubernetes", "AWS", "MySQL", "Git")),
      (2, "Data Scientist", Seq("Python", "R", "TensorFlow", "Keras", "Pandas", "NumPy", "Scikit-learn"))
    ).toDF("userId", "position", "techStack")

    val jobPostings0 = Seq(
      (1, "Backend Developer", Seq("Java", "Spring Boot", "MongoDB", "Docker", "AWS", "Git", "Jenkins"), Seq("Kubernetes", "Ansible", "Terraform")),
      (2, "Data Scientist", Seq("Python", "R", "SQL", "TensorFlow", "PyTorch"), Seq("Apache Spark", "Hadoop", "Keras"))
    ).toDF("jobId", "position", "requiredTechStack", "preferredTechStack")
//    val userProfiles = Seq(
//      (0, Array("Java", "Python")),
//      (1, Array("Python", "Scala"))
//    ).toDF("userId", "techStack")
//
//    val jobPostings = Seq(
//      (0, Array("Python", "Scala")),
//      (1, Array("Java", "Scala"))
//    ).toDF("jobId", "techStack")

    val jobPostings = jobPostings0.map(row => (row.getInt(0), row.getString(1), row.getSeq[String](2) ++ row.getSeq[String](2) ++ row.getSeq[String](3)))
      .toDF("jobId", "position", "techStack")

    // CountVectorizer를 사용하여 기술 스택 벡터화
    val cvModel = new CountVectorizer()
      .setInputCol("techStack")
      .setOutputCol("features")
      .fit(userProfiles.select("techStack").union(jobPostings.select("techStack")))

    println(cvModel.vocabulary.mkString("Array(", ", ", ")"))
    println("cvModel.vocabulary = ", cvModel.vocabulary.mkString("Array(", ", ", ")"))
    cvModel.vocabulary.foreach(println)

    val userFeatures = cvModel.transform(userProfiles).toDF("userId", "position", "techStack", "features_user")
    val jobFeatures = cvModel.transform(jobPostings).toDF("jobId", "position", "techStack", "features_job")

    userFeatures.show()
    jobFeatures.show()

    // 코사인 유사도 계산을 위한 사용자 정의 함수
    def cosineSimilarity(vectorA: SparseVector, vectorB: SparseVector): Double = {

      require(vectorA.size == vectorB.size, "Vector dimensions must match")

      val indicesA = vectorA.indices
      val valuesA = vectorA.values
      val indicesB = vectorB.indices
      val valuesB = vectorB.values


      var dotProduct = 0.0
      var normA = 0.0
      var normB = 0.0

      // dot product
      var i = 0
      var j = 0
      while (i < indicesA.length && j < indicesB.length) {
        if (indicesA(i) == indicesB(j)) {
          dotProduct += valuesA(i) * valuesB(j)
          i += 1
          j += 1
        } else if (indicesA(i) < indicesB(j)) {
          i += 1
        } else {
          j += 1
        }
      }

      println(s"dotProduct = $dotProduct")
      // norm
      normA = math.sqrt(valuesA.map(math.pow(_, 2)).sum)
      normB = math.sqrt(valuesB.map(math.pow(_, 2)).sum)

      println(s"normA = $normA, normB = $normB")
      if (normA * normB == 0) 0.0 else dotProduct / (normA * normB)
    }

    // 유사도 계산
    val similarityScores = userFeatures.crossJoin(jobFeatures).map { row =>
      val userVec = row.getAs[SparseVector]("features_user")
      val jobVec = row.getAs[SparseVector]("features_job")
      println("userVec = ", userVec)
      println("jobVec = ", jobVec)
      println(row)
      val similarity = cosineSimilarity(userVec, jobVec)
      (row.getAs[Int]("userId"), row.getAs[Int]("jobId"), similarity) // (userId, jobId, similarityScore)
    }.toDF("userId", "jobId", "similarityScore")
      .sort($"userId", $"similarityScore".desc)

    similarityScores.show()
  }
}
