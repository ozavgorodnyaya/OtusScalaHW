package parser

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._

import java.io.{FileOutputStream, PrintStream}
import scala.io.Source.fromURL

object Main extends App {

  case class CountryName(official: String)

  case class AllCountries(
                            name: CountryName,
                            capital: Seq[String],
                            area: Double,
                            region: String
                         )

  case class BigAfricanCountry(
                                 name: String,
                                 capital: String,
                                 area: Double
                               )

  val json = fromURL("https://raw.githubusercontent.com/mledoze/countries/master/countries.json").mkString
  val countryList = decode[Seq[AllCountries]](json)
    .map(countries => countries
    .filter(_.region.equals("Africa"))
    .map(x => BigAfricanCountry(x.name.official, x.capital.iterator.next(), x.area))
    .sortWith(_.area > _.area)
    .take(10)
    .toList
  )

  val result = Encoder[List[BigAfricanCountry]].apply(countryList.getOrElse(Nil))

  val outFile = args(0)
  val fileStream = new FileOutputStream(outFile)
  val filePrinter = new PrintStream(fileStream)
  filePrinter.println(result)

}
