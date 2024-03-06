import org.apache.spark.sql.SparkSession

import scala.collection.mutable

object SparkRecommendService {

  def recommendTop5JobPostings(userId : Int): String = {
    val spark = SparkSession.builder
      .appName("JobPostingRecommendationApp")
      .master("local")
      //      .config("spark.driver.extraJavaOptions", "add-opens java.base/sun.nio.ch=ALL-UNNAMED")
      //      .config("spark.executor.extraJavaOptions", "--add-opens java.base/sun.nio.ch=ALL-UNNAMED")
      .config("spark.mongodb.input.uri", "mongodb://oh:ohssafy@localhost:27017")
      .getOrCreate()

    import spark.implicits._

    val jobPostingsDF = spark.read
      .format("mongodb")
      .option("connection.uri", "mongodb://oh:ohssafy@localhost:27017")
      .option("database", "recommend")
      .option("collection", "jobPosting")
      .load()

    val userProfilesDF = spark.read
      .format("mongodb")
      .option("connection.uri", "mongodb://oh:ohssafy@localhost:27017")
      .option("database", "recommend")
      .option("collection", "userProfile")
      .load()

    //    println("Job Postings")
    //    jobPostingsDF.show()
    //    println("User Profiles")
    //    userProfilesDF.show()

    // 1번 사용자 프로필 필터링
    val userSkillDF = userProfilesDF.filter($"userId" === userId).select("skills").as[Seq[String]].collect()(0)

    // 채용 공고별로 사용자의 기술 스택과 일치하는 필수 기술의 수 계산
    //    val matchScoreDF = jobPostingsDF.alias("jobs")
    //      .crossJoin(userSkills.alias("userSkills"))
    //      .filter(array_contains($"jobs.requiredSkills", $"userSkills.userSkill"))
    //      .groupBy($"jobs.position", $"jobs.company")
    //      .agg(count($"userSkills.userSkill").as("matchScore"))
    //      .orderBy($"matchScore".desc)
    //      .limit(5)

    // 자격 요건 및 우대 사항과의 매칭 점수 계산 ( 자격 요건 : 우대 사항 <-> 2:1 )
    val recommendedJobsDF = jobPostingsDF.map { jobPosting =>
        val requiredSkills = jobPosting.getAs[mutable.ArraySeq[String]]("requiredSkills")
        val preferredSkills = jobPosting.getAs[mutable.ArraySeq[String]]("preferredSkills")
        val matchScore = userSkillDF.intersect(requiredSkills).length * 2 +
          userSkillDF.intersect(preferredSkills).length
        (
          jobPosting.getAs[String]("_id"),
          jobPosting.getAs[String]("position"),
          jobPosting.getAs[String]("company"),
          matchScore
        )
      }
      .toDF("jobId", "position", "company", "matchScore")
      .filter($"matchScore" > 0)
      .orderBy($"matchScore".desc)
      .limit(5)

    recommendedJobsDF.show()
    recommendedJobsDF.toString()
  }
}
