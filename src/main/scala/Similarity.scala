import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.sql.SparkSession
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
//    val userProfiles = Seq(
//      (1, "Backend Developer", Seq("Java", "Spring", "Docker", "Kubernetes", "AWS", "MySQL", "Git")),
//      (2, "Data Scientist", Seq("Python", "R", "TensorFlow", "Keras", "Pandas", "NumPy", "Scikit-learn"))
//    ).toDF("userId", "position", "techStack")
//
//    val jobPostings = Seq(
//      (1, "Backend Developer", Seq("Java", "Spring Boot", "MongoDB", "Docker", "AWS", "Git", "Jenkins"), Seq("Kubernetes", "Ansible", "Terraform")),
//      (2, "Data Scientist", Seq("Python", "R", "SQL", "TensorFlow", "PyTorch"), Seq("Apache Spark", "Hadoop", "Keras"))
//    ).toDF("jobId", "position", "requiredTechStack", "preferredTechStack")
    val userProfiles = Seq(
      (0, Seq("Java, Python")),
      (1, Seq("Python, Scala"))
    ).toDF("userId", "techStack")

    val jobPostings = Seq(
      (0, Seq("Python, Scala")),
      (1, Seq("Java, Scala"))
    ).toDF("jobId", "techStack")

    // CountVectorizer를 사용하여 기술 스택 벡터화
    val cvModel = new CountVectorizer()
      .setInputCol("techStack")
      .setOutputCol("features")
      .fit(userProfiles.select("techStack").union(jobPostings.select("techStack")))

    userProfiles.select("techStack").union(jobPostings.select("techStack")).show()
    println("cvModel.vocabulary = ", cvModel.vocabulary.mkString("Array(", ", ", ")"))

    val userFeatures = cvModel.transform(userProfiles)
    val jobFeatures = cvModel.transform(jobPostings)

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
      val userVec = row.getAs[SparseVector](2)
      val jobVec = row.getAs[SparseVector](5)
      println("userVec = ", userVec)
      println("jobVec = ", jobVec)
      val similarity = cosineSimilarity(userVec, jobVec)
      (row.getInt(0), row.getInt(3), similarity) // (userId, jobId, similarityScore)
    }.toDF("userId", "jobId", "similarityScore")

    similarityScores.show()
  }
}
