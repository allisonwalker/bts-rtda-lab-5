package cli

import models.CityPopulationEntry
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import services.CityPopulationProcessService

object Main {
  def main(args: Array[String]): Unit = {
    //Init parallel context
    val sc: SparkContext = SparkContext.getOrCreate()

    //calling main app logic
    doRun(sc, args)

    //Stop parallel context
    sc.stop()
  }

  def doRun(sc: SparkContext, args: Array[String]): Unit ={
    val inputCityFemaleFilePath = args(0)
    val inputCityMaleFilePath = args(1)
    val outputFilePath = args(2)
    val operation = args(3);

    val cityFemaleLines: RDD[String] = sc.textFile(inputCityFemaleFilePath)
    val cityMaleLines: RDD[String] = sc.textFile(inputCityMaleFilePath)

    val cityFemale: RDD[CityPopulationEntry] = CityPopulationProcessService
      .buildCityPopulationEntryRDD(cityFemaleLines)

    val cityMale: RDD[CityPopulationEntry] = CityPopulationProcessService
      .buildCityPopulationEntryRDD(cityMaleLines)

    if("total_count_grouped_by_common_year_component" == operation) {
      val result = CityPopulationProcessService.totalCountByYear(cityFemale, cityMale)
      result.saveAsTextFile(outputFilePath)
    }
  }
}
