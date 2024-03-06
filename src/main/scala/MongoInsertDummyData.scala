//import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
//
//import scala.concurrent.duration.DurationInt
//import scala.concurrent.{Await, Future}
//
//object MongoInsertDummyData {
//
//  private val mongoClient: MongoClient = MongoClient("mongodb://oh:ohssafy@localhost:27017")
//  private val database: MongoDatabase = mongoClient.getDatabase("recommend")
//  private val jobPostingCollection: MongoCollection[Document] = database.getCollection("jobPosting")
//  private val userProfileCollection: MongoCollection[Document] = database.getCollection("userProfile")
//
//  def main(args: Array[String]): Unit = {
//
//    val jobPostingsData = Seq(
//      Document("position" -> "Backend Developer", "requiredSkills" -> Seq("Java", "Spring", "MySQL"), "preferredSkills" -> Seq("Docker", "Kubernetes"), "company" -> "TechCorp", "description" -> "We are looking for a Backend Developer..."),
//      Document("position" -> "Frontend Developer", "requiredSkills" -> Seq("JavaScript", "React"), "preferredSkills" -> Seq("TypeScript", "Redux"), "company" -> "WebSolutions", "description" -> "Join our team as a Frontend Developer..."),
//      Document("position" -> "Data Scientist", "requiredSkills" -> Seq("Python", "SQL"), "preferredSkills" -> Seq("Machine Learning", "Deep Learning"), "company" -> "DataDriven", "description" -> "Data Scientist position available..."),
//      Document("position" -> "DevOps Engineer", "requiredSkills" -> Seq("AWS", "Docker"), "preferredSkills" -> Seq("Kubernetes", "Terraform"), "company" -> "CloudSolutions", "description" -> "Seeking a skilled DevOps Engineer..."),
//      Document("position" -> "Project Manager", "requiredSkills" -> Seq("Agile", "Scrum"), "preferredSkills" -> Seq("PMP", "Communication"), "company" -> "ManageRight", "description" -> "Project Manager wanted..."),
//      Document("position" -> "UX/UI Designer", "requiredSkills" -> Seq("Figma", "Sketch"), "preferredSkills" -> Seq("User Research", "Prototyping"), "company" -> "DesignInnovate", "description" -> "Looking for a creative UX/UI Designer..."),
//      Document("position" -> "Mobile Developer", "requiredSkills" -> Seq("Swift", "Kotlin"), "preferredSkills" -> Seq("Firebase", "React Native"), "company" -> "AppMakers", "description" -> "Mobile Developer opportunity..."),
//      Document("position" -> "System Administrator", "requiredSkills" -> Seq("Linux", "Networking"), "preferredSkills" -> Seq("Security", "AWS"), "company" -> "SysAdmins", "description" -> "Experienced System Administrator needed..."),
//      Document("position" -> "Blockchain Developer", "requiredSkills" -> Seq("Solidity", "Ethereum"), "preferredSkills" -> Seq("Smart Contracts", "DApps"), "company" -> "BlockChainInc", "description" -> "Blockchain Developer position open..."),
//      Document("position" -> "Quality Assurance Engineer", "requiredSkills" -> Seq("Testing", "Selenium"), "preferredSkills" -> Seq("Automation", "Cypress"), "company" -> "QualityFirst", "description" -> "QA Engineer wanted for our team...")
//    )
//
//    val userProfilesData = Seq(
//      Document("desiredPosition" -> "Backend Developer", "skills" -> Seq("Java", "Spring", "MySQL", "Docker")),
//      Document("desiredPosition" -> "Frontend Developer", "skills" -> Seq("JavaScript", "React", "Redux", "TypeScript")),
//      Document("desiredPosition" -> "Data Scientist", "skills" -> Seq("Python", "Machine Learning", "SQL")),
//      Document("desiredPosition" -> "DevOps Engineer", "skills" -> Seq("AWS", "Docker", "Kubernetes", "Terraform")),
//      Document("desiredPosition" -> "Project Manager", "skills" -> Seq("Agile", "Scrum", "Communication")),
//      Document("desiredPosition" -> "UX/UI Designer", "skills" -> Seq("Figma", "Sketch", "User Research")),
//      Document("desiredPosition" -> "Mobile Developer", "skills" -> Seq("Swift", "Firebase", "React Native")),
//      Document("desiredPosition" -> "System Administrator", "skills" -> Seq("Linux", "Networking", "AWS")),
//      Document("desiredPosition" -> "Blockchain Developer", "skills" -> Seq("Solidity", "Ethereum", "Smart Contracts")),
//      Document("desiredPosition" -> "Quality Assurance Engineer", "skills" -> Seq("Testing", "Automation", "Selenium", "Cypress"))
//    )
//
//    // 비동기 작업 실행 및 완료 대기
//    val insertJobPostingsFuture = jobPostingCollection.insertMany(jobPostingsData).toFuture()
//    val insertUserProfilesFuture = userProfileCollection.insertMany(userProfilesData).toFuture()
//
//    Thread.sleep(5000)
//
//    println("Data insertion completed")
//
//    mongoClient.close()
//  }
//}
