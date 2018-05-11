package io.github.robhinds

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger

trait LoggingConfig {
  val conf = ConfigFactory.load()
  private val c = Logging(conf.getBoolean("logging.log-requests"))
  private val l = Logger(classOf[LoggingConfig])
  def logDebug(m: String) = if(c.logRequests) l.debug(m)
  def logInfo(m: String) = if(c.logRequests) l.info(m)
  def logWarn(m: String) = if(c.logRequests) l.warn(m)
  def logError(m: String) = if(c.logRequests) l.error(m)
}

case class Logging(logRequests: Boolean)
