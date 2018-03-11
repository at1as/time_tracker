#!/usr/bin/env scala

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import scala.tools.nsc.io.File
import sun.misc.Signal
import sun.misc.SignalHandler


object Signal extends App {
  val date  = LocalDate.now.toString
  val start = LocalTime.now
  
  val timeUse = if (args.length > 0) args(0).toLowerCase else "meeting"

  println(f"${timeUse.capitalize} started on $date at $start local time")

  Signal.handle(new Signal("INT"), new SignalHandler() {
    def handle(sig: Signal) {
      val end   = LocalTime.now
      val delta = Duration.between(start, end).toMillis

      Reporter.addToReport(date, delta, timeUse)
      System.exit(0)
    }
  })

  while (true) { /* Loop until sigint is caught */ }
}


object Reporter {
  val filename = "./time_tracker_log.txt"
  lazy val timeTracker: String = new java.io.File(".").getCanonicalPath + filename
  
  def addToReport(date: String, delta: Long, timeUse: String) {
    scala.tools.nsc.io.File(filename).appendAll(f"$date,$delta,$timeUse\n")
    generateMetrics(date, delta, timeUse)
  }

  def generateMetrics(date: String, delta: Long, timeUse: String) {
    val dailyTotal   = cumulativeDailyTime(date, timeUse)
    val monthlyTotal = cumulativeMonthlyTime(date, timeUse)
    val yearlyTotal  = cumulativeYearlyTime(date, timeUse)
    
    println
    println(f"Spent $delta ms on current $timeUse\n")
    println(f"Spent $dailyTotal ms today on $timeUse")
    println(f"Spent $monthlyTotal ms this month on $timeUse")
    println(f"Spent $yearlyTotal ms this year on $timeUse\n")
  }

  def cumulativeDailyTime(date: String, timeUse: String): Int = {
    // TODO: endsWith $timeUse should do an exact match on last column, since "notmeeting" will match against "meeting"
    io.Source.fromFile(filename).getLines.filter(li => li.startsWith(date) && li.trim.endsWith(timeUse)).map(_.split(",")(1).toInt).fold(0) { (x, y) => x + y } 
  }
  
  def cumulativeMonthlyTime(date: String, timeUse: String): Int = {
    val year_month = date.split("-").slice(0, 2).map( x => x.toString ).mkString("-")
    io.Source.fromFile(filename).getLines.filter(li => li.startsWith(year_month) && li.trim.endsWith(timeUse)).map(_.split(",")(1).toInt).fold(0) { (x, y) => x + y } 
  }

  def cumulativeYearlyTime(date: String, timeUse: String): Int = {
    val year = date.split("-")(0)
    io.Source.fromFile(filename).getLines.filter(li => li.startsWith(year) && li.trim.endsWith(timeUse)).map(_.split(",")(1).toInt).fold(0) { (x, y) => x + y } 
  }
}

