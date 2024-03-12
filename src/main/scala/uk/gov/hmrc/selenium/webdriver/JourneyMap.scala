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

import org.openqa.selenium.{JavascriptExecutor, WebElement}
import org.scalactic.source.Position
import org.scalatest.{Informer, Informing, Outcome, TestSuite, TestSuiteMixin}

trait JourneyMap extends TestSuiteMixin with Informing with Screenshot { this: TestSuite =>

  private val testSuiteName = this.suiteName.replaceAll(" ", "-").replaceAll(":", "")

  protected var testName: String = ""
  private var stepName: String   = ""

  implicit def screenshotter: Screenshotter = new Screenshotter {
    def maybeTakeScreenshot(maybeElement: Option[WebElement] = None): Unit = {
      val screenshotName = s"${System.currentTimeMillis}-$stepName.png"

      maybeElement.filter(isNavigation).foreach { element =>
        highlightElement(element)
      }

      maybeElement match {
        case Some(element) if !isNavigation(element) => ()
        case _                                       =>
          captureScreenshot(
            screenshotName,
            s"./target/journey-map/html-report/images/screenshots/$testSuiteName/$testName/"
          )
      }
    }

    private def isNavigation(element: WebElement) = {
      val tagName       = element.getTagName.toLowerCase
      val typeAttribute = Option(element.getAttribute("type")).map(_.toLowerCase).getOrElse("")

      // Check if it's a link with an href attribute
      val isLink = tagName == "a" && Option(element.getAttribute("href")).isDefined

      // Check if it's a button or an input element that might submit a form
      val isFormButton = tagName == "button" || (tagName == "input" && typeAttribute == "submit")

      isLink || isFormButton
    }

    private def highlightElement(element: WebElement): Unit = {
      val js: JavascriptExecutor = Driver.instance.asInstanceOf[JavascriptExecutor]
      js.executeScript(
        "arguments[0].setAttribute('style', arguments[1]);",
        element,
        "border: 4px solid red;"
      )
    }

    def version = "JourneyMap"
  }

  abstract override def withFixture(test: NoArgTest): Outcome = {
    testName = sanitise(test.name)
    super.withFixture(test)
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
