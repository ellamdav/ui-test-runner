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

import org.openqa.selenium.io.FileHandler.{copy, createDir}
import org.openqa.selenium.{OutputType, TakesScreenshot}
import org.scalatest.TestSuite

import java.io.File

trait Screenshot { this: TestSuite =>

  protected def captureScreenshot(screenshotName: String, screenshotDirectory: String): Unit = {
    val tmpFile = Driver.instance.asInstanceOf[TakesScreenshot].getScreenshotAs(OutputType.FILE)
    val screenshotFile = new File(screenshotDirectory, screenshotName)

    createDir(new File(screenshotDirectory))
    copy(tmpFile, screenshotFile)
  }

}
