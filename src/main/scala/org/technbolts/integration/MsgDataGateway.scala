package org.technbolts.integration

import org.springframework.integration.annotation.Gateway
import org.technbolts.model.MsgDataSet

trait MsgDataGateway {

  @Gateway
  def publish(msgs:MsgDataSet):Unit
}