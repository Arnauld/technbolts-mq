package org.technbolts.integration

import org.springframework.stereotype.Service
import org.springframework.integration.core.MessageChannel
import javax.annotation.PostConstruct
import org.springframework.integration.message.MessageBuilder
import java.util.Date
import org.springframework.beans.factory.annotation.{Qualifier, Autowired}
import org.technbolts.model.{MsgDataSet, TextPart, MsgData}
import org.technbolts.util.{ExecutionService}
import org.technbolts.util.TimeValue._
import org.apache.log4j.Logger

/**
 *
 */
@Service("msgDataRandomPublisher")
class MsgDataRandomPublisher {

  private var logger: Logger = Logger.getLogger(classOf[MsgDataRandomPublisher])

  var gateway:MsgDataGateway = null
  @Autowired
  @Qualifier("msgDataGateway")
  def setGateway(gateway:MsgDataGateway) = this.gateway = gateway;

  var executor : ExecutionService = null
  @Autowired
  @Qualifier("sharedExecutionService")
  def setExecutionService(executor:ExecutionService) = this.executor = executor;

  @PostConstruct
  def start = executor.scheduleAtFixedRate(()=> { publishRandom }, milliseconds(1000))

  def newRandom() :MsgData = {
    val msgData = new MsgData
    msgData.parts += TextPart("Random content: "+new Date)
    msgData
  }

  def publishRandom():Unit = { 
    val randomMsg  = newRandom()
    val msgDataSet = new MsgDataSet(List(randomMsg))

    val msg = MessageBuilder.withPayload(msgDataSet).build

    if(logger.isInfoEnabled)
      logger.info("\n*\n*\n*Publishing a random message\n*\n*\n*")

    gateway.publish(msgDataSet)
  }

}