/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.selenium.webdriver

import org.scalactic.source.Position
import org.scalatest.{Informer, Informing, Outcome, TestSuite, TestSuiteMixin}

trait JourneyMap extends TestSuiteMixin with Informing with Screenshot { this: TestSuite =>

  val testSuiteName = this.suiteName.replaceAll(" ", "-").replaceAll(":", "")

  var testName: String = ""
  var stepName: String = ""

  implicit def screenshotter: Screenshotter = new Screenshotter {
    def maybeTakeScreenshot(): Unit = {
      val screenshotName = s"${System.currentTimeMillis}-$stepName.png"
      captureScreenshot(
        screenshotName,
        s"./target/journey-map/html-report/images/screenshots/$testSuiteName/$testName/"
      )
    }

    def version = "JourneyMap"
  }

  abstract override def withFixture(test: NoArgTest): Outcome = {
    testName = sanitise(test.name)
    val outcome = super.withFixture(test)
    screenshotter.maybeTakeScreenshot()
    outcome
  }

  private val customInformer = new Informer {
    def apply(info: String, payload: Option[Any] = None)(implicit pos: Position): Unit = {
      stepName = sanitise(info)
      JourneyMap.super.info(info)
    }
  }

  abstract override def info: Informer = customInformer

  private def sanitise(s: String) =
    s.replaceAll(" ", "-").replaceAll(":", "")
}
