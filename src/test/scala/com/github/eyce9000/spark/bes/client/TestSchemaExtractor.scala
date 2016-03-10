package com.github.eyce9000.spark.bes.client

/**
  * Created by grlucche on 2/21/16.
  */

import com.github.eyce9000.spark.bes.DefaultSource
import org.junit.Test
class TestSchemaExtractor {

  @Test def testExtractor: Unit ={
    val query = {"""
(
        id of it | 0 //@Column id
,        name of it | "" //@Column name
,       ( value of result (it, bes property "DNS Name") | "") //@Column dns
,       last report time of it | (now - 90 * day) //@Column(time) lastReportTime
,       ( value of result (it, bes property "ITSAS Sync Date and Time") as time | (now - 90 * day )) //@Column(time) coreSyncTime
,       ( value of result (it, bes property "SCF Update Date and Time") as time | (now - 90 * day )) //@Column(time) scfSyncTime
,       ( value of result (it, bes property "ITSASUUID") | "") //@Column secosinstid
,       ( value of result (it, bes property "Auto patch managed machine") | "No") //@Column autopatchEnabled
,       ( value of result (it, bes property "Auto patch date option 1") | "") //@Column autopatchSchedule
,       ( value of result (it, bes property "Dont automatically reboot") | "Yes") //@Column autopatchDoNotReboot
,       ( value of result (it, bes property "BES Relay Service Installed") | "No") //@Column relayInstalled
,       ( value of result (it, bes property "BES Console Version") | "") //@Column consoleVersion
,       ( concatenation ";" of values of results (it, bes property "IPs") | "") //@Column ips
,       operating system of it | "" //@Column os
,       ( concatenation ";" of values of results (it, bes property "Location by network") | "") //@Column locationByNetwork
,       ( concatenation ";" of values of results (it, bes property "Person Responsible OS") | "") //@Column personResponsible
) whose (item 3 of it > (now - 14 * day)) of bes computers
"""}
    println(new DefaultSource().columnExtractor(query).treeString)
  }
}
